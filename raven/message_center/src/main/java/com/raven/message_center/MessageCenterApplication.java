package com.raven.message_center;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

@EnableEurekaClient
@SpringBootApplication
@MapperScan("com.raven.message_center.mapper")
@EnableFeignClients(basePackages = "com.raven.message_center.service")
public class MessageCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessageCenterApplication.class, args);
    }
}
