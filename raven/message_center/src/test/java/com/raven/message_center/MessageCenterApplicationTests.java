package com.raven.message_center;

import com.raven.message_center.controller.ChatRoomHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.Properties;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MessageCenterApplicationTests {
    @Autowired
    Environment env;
    @Autowired
    Properties kafkaProducerProps;
    @Autowired
    Properties kafkaConsumerProps;
    @Autowired
    ChatRoomHandler chatRoomHandler;

    @Test
    public void contextLoads() {
    }

    @Test
    public void propertiesTest() {
        for (Map.Entry<Object, Object> entry : kafkaProducerProps.entrySet()) {
            System.out.println(entry.getKey() + "," + entry.getValue());
        }
    }

    @Test
    public void fuckYeahTest() {
        System.out.println("fuck" + env.getProperty("fuck"));
    }

}
