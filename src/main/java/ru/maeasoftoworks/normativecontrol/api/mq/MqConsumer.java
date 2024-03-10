package ru.maeasoftoworks.normativecontrol.api.mq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MqConsumer extends DefaultConsumer {
    private static final Logger log = LoggerFactory.getLogger(MqConsumer.class);

    public MqConsumer(Channel channel) {
        super(channel);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        log.info("Message received:");
        log.info(consumerTag, new String(body));
    }
}