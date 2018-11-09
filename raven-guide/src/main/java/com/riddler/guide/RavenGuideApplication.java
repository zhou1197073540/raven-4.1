package com.riddler.guide;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@EnableEurekaClient
@EnableScheduling
@SpringBootApplication
public class RavenGuideApplication {

	public static void main(String[] args) {
        SpringApplication.run(RavenGuideApplication.class, args);
    }

    @RequestMapping("/")
    public String index() {
        return "index";
    }

}
