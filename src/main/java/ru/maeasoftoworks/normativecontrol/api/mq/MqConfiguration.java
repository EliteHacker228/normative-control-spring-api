package ru.maeasoftoworks.normativecontrol.api.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

@Configuration
@Slf4j
public class MqConfiguration {
    @Value("${amqp.uri}")
    private String uri;

    @Value("${amqp.senderQueueName}")
    private String senderQueueName;

    @Value("${amqp.receiverQueueName}")
    private String receiverQueueName;

    private final Connection connection;
    private final Channel channel;

    public MqConfiguration() throws URISyntaxException, NoSuchAlgorithmException, KeyManagementException, IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(uri);

        this.connection = factory.newConnection();
        log.info("AMQP connected to " + factory.getHost() + ":" + factory.getPort());

        this.channel = this.connection.createChannel();
        this.channel.queueDeclare(senderQueueName, true, false, false, null);
        this.channel.queueDeclare(receiverQueueName, true, false, false, null);

        channel.basicConsume(receiverQueueName, true, new MqConsumer(this.channel));
    }

    @Bean
    public Channel channel(){
        return this.channel;
    }

    @PreDestroy
    public void closeMqConnections() throws IOException, TimeoutException {
        this.channel.close();
        this.connection.close();
    }
}