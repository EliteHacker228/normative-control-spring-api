package ru.maeasoftoworks.normativecontrol.api.mq;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
@Slf4j
public class MqConfiguration {
    @Value("${amqp.senderQueueName}")
    private String senderQueueName;

    @Getter
    private final String receiverQueueName = createQueueName();

    @Bean
    public Queue senderQueue() {
        return new Queue(senderQueueName);
    }

    @Bean
    public Queue receiverQueue() {
        return new Queue(receiverQueueName, false, true, true);
    }

    public String createQueueName() {
        return "instance-" + UUID.randomUUID();
    }
}