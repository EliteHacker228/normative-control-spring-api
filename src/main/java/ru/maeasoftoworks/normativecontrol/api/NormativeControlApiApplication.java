package ru.maeasoftoworks.normativecontrol.api;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
@AllArgsConstructor
public class NormativeControlApiApplication {

    private static final Logger log = LoggerFactory.getLogger(NormativeControlApiApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(NormativeControlApiApplication.class, args);
    }


    @PostConstruct
    @Transactional
    protected void initDatabase() {

    }
}
