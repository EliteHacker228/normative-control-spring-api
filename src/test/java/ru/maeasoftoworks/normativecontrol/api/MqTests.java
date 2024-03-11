package ru.maeasoftoworks.normativecontrol.api;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import com.rabbitmq.client.*;
import ru.maeasoftoworks.normativecontrol.api.mq.MqConsumer;
import ru.maeasoftoworks.normativecontrol.api.mq.MqPublisher;

import java.io.IOException;
import java.nio.charset.Charset;

@SpringBootTest
@EnableAutoConfiguration
public class MqTests {

    private static final Logger log = LoggerFactory.getLogger(MqConsumer.class);

    @Autowired
    private MqPublisher mqPublisher;

    @Test
    public void MqTest() {

        String corrId = "123456";
        String mockBody = """
                {"document": "123456/source.docx","replyTo": "responces_1","resultDocx": "123456/result.docx","resultHtml": "123456/result.docx"}""";
//        mockBody = "Hello world 2";

        mqPublisher.publishToVerify(mockBody, corrId);
    }

    @Test
    @SneakyThrows
    public void allInOneTest(){
        class LocalMqConsumer extends DefaultConsumer {
            public LocalMqConsumer(Channel channel) {
                super(channel);
            }

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                super.handleDelivery(consumerTag, envelope, properties, body);
                log.info("Message received:");
                log.info(consumerTag, new String(body));
            }
        }

        final String URI = "amqp://maeasoftworks:normativecontrol@localhost:5672";
        final String USERNAME = "maeasoftworks";
        final String PASSWORD = "normativecontrol";

        final String SENDER_QUEUE_NAME = "to_be_verified";
        final String RECEIVER_QUEUE_NAME = "responces_1";


        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(URI);
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);

        Connection connection = factory.newConnection();
        log.info("AMQP connected to " + factory.getHost() + ":" + factory.getPort());

        Channel channel = connection.createChannel();
        channel.queueDeclare(SENDER_QUEUE_NAME, true, false, false, null);
        channel.queueDeclare(RECEIVER_QUEUE_NAME, true, false, false, null);
        channel.basicConsume(RECEIVER_QUEUE_NAME, true, new LocalMqConsumer(channel));

        String queueName = "to_be_verified";
        String correlationId = "123456";
        String body = """
                {"document": "123456/source.docx","replyTo": "responces_1","resultDocx": "123456/result.docx","resultHtml": "123456/result.docx"}""";

        channel.basicPublish("",
                queueName,
                new AMQP.BasicProperties.Builder().correlationId(correlationId).build(),
                body.getBytes(Charset.forName("UTF-8")));
    }
}

