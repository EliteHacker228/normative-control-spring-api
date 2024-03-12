package ru.maeasoftoworks.normativecontrol.api.mq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class MqPublisher {
    @Value("${amqp.senderQueueName}")
    private String senderQueueName;

    private final Channel channel;

    @SneakyThrows
    public void publishToVerify(String body, String correlationId) {
        channel.basicPublish("",
                senderQueueName,
                new AMQP.BasicProperties.Builder().correlationId(correlationId).build(),
                body.getBytes(StandardCharsets.UTF_8));
    }
}
