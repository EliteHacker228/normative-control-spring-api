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

        channel.basicPublish("",
                queueName,
                new AMQP.BasicProperties.Builder().correlationId(correlationId).build(),
                body.getBytes(Charset.forName("UTF-8")));
    }
}
