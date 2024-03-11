package ru.maeasoftoworks.normativecontrol.api.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

@Configuration
public class MqConfiguration {
    private static final Logger log = LoggerFactory.getLogger(MqConfiguration.class);

    private static final String URI = "amqp://maeasoftworks:normativecontrol@localhost:5672";
    private static final String USERNAME = "maeasoftworks";
    private static final String PASSWORD = "normativecontrol";

    private static final String SENDER_QUEUE_NAME = "to_be_verified";
    private static final String RECEIVER_QUEUE_NAME = "responces_1";

    private Connection connection;
    private Channel channel;

    public MqConfiguration() throws URISyntaxException, NoSuchAlgorithmException, KeyManagementException, IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(URI);
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);

        this.connection = factory.newConnection();
        log.info("AMQP connected to " + factory.getHost() + ":" + factory.getPort());

        this.channel = this.connection.createChannel();
        this.channel.queueDeclare(SENDER_QUEUE_NAME, true, false, false, null);
        if(false)
            this.channel.queueDeclare(RECEIVER_QUEUE_NAME, true, false, false, null);

        channel.basicConsume(RECEIVER_QUEUE_NAME, true, new MqConsumer(this.channel));
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