package ru.maeasoftoworks.normativecontrol.api.mq;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MqConsumer {

    @Value("#{mqConfiguration.getReceiverQueueName()}")
    private String queueName;

    @PostConstruct
    public void pc(){
        log.info("NEW CONSUMER CREATED");
        log.info(queueName);
    }

    @RabbitListener(queues = "#{mqConfiguration.getReceiverQueueName()}")
    public void handleMessage(Message message) {
        log.info("Message received:");
        log.info(message.getMessageProperties().getCorrelationId());
        log.info(message.getMessageProperties().getConsumerTag());
        log.info(new String(message.getBody()));
    }
}