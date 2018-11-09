package com.raven;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
//@RestController
@EnableEurekaClient
@EnableFeignClients(basePackages = "com.raven.service")
@MapperScan("com.raven.mapper")
@ServletComponentScan("com.raven.config")
public class TakerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TakerApplication.class, args);
    }
}
