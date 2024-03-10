package ru.maeasoftoworks.normativecontrol.api.mq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;

@Component
@AllArgsConstructor
public class MqPublisher {

    private Channel channel;

    @SneakyThrows
    public void publishToVerify(String body, String correlationId){
        String queueName = "to_be_verified";
//        String correlationId = "435345345";

//        String mockBody = """
//                {"document": "435345345/source.docx","replyTo": "responces_1","resultDocx": "435345345/result.docx","resultHtml": "435345345/result.docx"}""";

        channel.basicPublish("",
                queueName,
                new AMQP.BasicProperties.Builder().correlationId(correlationId).build(),
                body.getBytes(Charset.forName("UTF-8")));
    }
}
