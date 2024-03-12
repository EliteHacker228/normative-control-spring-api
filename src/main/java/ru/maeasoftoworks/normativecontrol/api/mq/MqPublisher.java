package ru.maeasoftoworks.normativecontrol.api.mq;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class MqPublisher {
    @Value("${amqp.senderQueueName}")
    private String senderQueueName;

    private final AmqpTemplate template;

    @SneakyThrows
    public void publishToVerify(String body, String correlationId) {
        val props = new MessageProperties();
        props.setCorrelationId(correlationId);
        template.send(senderQueueName, new Message(body.getBytes(StandardCharsets.UTF_8), props));
    }
}
