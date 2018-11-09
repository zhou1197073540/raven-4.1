package com.riddler.usr;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@MapperScan("com.riddler.usr.mapper")
@ServletComponentScan("com.riddler.usr.config.druid")
@RestController
public class RiddleUsrApplication {
    public static void main(String[] args) {
		SpringApplication.run(RiddleUsrApplication.class, args);
	}

	@GetMapping("/index")
	public String index(ModelMap map){
        System.out.println("=====");
        map.put("test","你大爷的");
        return "index";
    }

}
