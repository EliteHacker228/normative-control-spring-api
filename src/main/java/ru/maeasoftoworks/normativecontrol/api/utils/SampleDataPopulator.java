package ru.maeasoftoworks.normativecontrol.api.utils;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;
import ru.maeasoftoworks.normativecontrol.api.repositories.UsersRepository;

//@Component
//@DependsOn({ "dataSource" })
//public class SampleDataPopulator {
//    private final static Logger log = LoggerFactory.getLogger(SampleDataPopulator.class);
//
//    @Autowired
//    private UsersRepository usersRepository;
//
//    @PostConstruct
//    public void populateSampleData() {
//        myRepository.save(item);
//
//        log.info("Populated DB with sample data");
//    }
//}