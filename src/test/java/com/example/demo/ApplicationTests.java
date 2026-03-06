package com.example.demo;

import com.example.demo.service.DataCleanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTests {

    @Autowired
    DataCleanService dataCleanService;

    @Test
    void contextLoads() {
    }

    @Test
    void sendFlume() {
        dataCleanService.cleanAndSend();
    }

}
