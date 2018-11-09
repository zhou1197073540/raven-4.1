package com.raven.iirinstitute;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
@MapperScan("com.raven.iirinstitute.mapper")
public class IirinstituteApplication {

    public static void main(String[] args) {
        SpringApplication.run(IirinstituteApplication.class, args);
    }
}
