package com.raven.feign_consumer;

import com.raven.feign_consumer.annotation.AuthCheck;
import com.raven.feign_consumer.consts.Auth;
import com.raven.feign_consumer.dto.PointsDto;
import com.raven.feign_consumer.dto.RespDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.persistence.PersistenceUnit;
import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootApplication
@RestController
@EnableEurekaClient
@EnableFeignClients(basePackages = "com.raven.feign_consumer")
public class FeignConsumerApplication {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    WalletRemoteService service;

    @Autowired
    Environment env;

    public static void main(String[] args) {
        SpringApplication.run(FeignConsumerApplication.class, args);
    }

    @GetMapping("/feign/points/{uid}")
    @ResponseBody
    public RespDto getPoints(@PathVariable String uid) throws Exception {
        RespDto dto = service.getPoints(uid);
        return dto;
    }

    @GetMapping("/feign/auth")
    @ResponseBody
    @AuthCheck(Auth.NEED_LOGIN)
    public String authTest() {
        return "穿了";
    }

    @GetMapping("/feign/points_list/{uid}")
    @ResponseBody
    public RespDto getPointsRecords(@PathVariable String uid) throws Exception {
        return service.getPointsList(uid);
    }

    @GetMapping("/feign/change_point/{serial}")
    @ResponseBody
    public RespDto changePoints(@PathVariable(value = "serial") String serial) throws Exception {
        PointsDto dto = new PointsDto();
        dto.setUid("uid");
        dto.setChange(100);
        dto.setSerialNum(serial);
        dto.setDate(LocalDate.now().toString());
        dto.setTime(LocalDateTime.now().toString());
        dto.setComment(1);
        String callbackUrl = "http://"
                + env.getProperty("spring.application.name")
                + "/feign/callback";
        dto.setCallbackUrl(callbackUrl);
        RespDto retDto = service.changePoints(dto);
        return retDto;
    }

    @PutMapping("/feign/callback")
    public String walletCallback(@RequestBody RespDto dto) throws Exception {
        System.out.println("fucker!!!");
        System.out.println(dto.getStatus() + dto.getMsg() + dto.getData().toString());
        return "yeah";
    }

    @Bean
    @Primary
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}