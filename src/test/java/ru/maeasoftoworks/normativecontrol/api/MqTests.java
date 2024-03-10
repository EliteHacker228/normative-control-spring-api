package ru.maeasoftoworks.normativecontrol.api;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import com.rabbitmq.client.*;
import ru.maeasoftoworks.normativecontrol.api.mq.MqPublisher;

import java.io.IOException;
import java.nio.charset.Charset;

@SpringBootTest
@EnableAutoConfiguration
public class MqTests {
    @Autowired
    private MqPublisher mqPublisher;

    @Test
    public void MqTest() {

        String corrId = "35456746757878";
        String mockBody = """
                {"document": "35456746757878/source.docx","replyTo": "responces_1","resultDocx": "35456746757878/result.docx","resultHtml": "35456746757878/result.docx"}""";
        mockBody = "Hello world 2";

        mqPublisher.publishToVerify(mockBody, corrId);
    }
}