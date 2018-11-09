package com.raven.wallet;

import org.apache.catalina.core.AprLifecycleListener;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableEurekaClient
@MapperScan("com.raven.wallet.mapper")
@ServletComponentScan("com.raven.wallet.config")
public class WalletApplication {

    AprLifecycleListener arpLifecycle = null;

    public static void main(String[] args) {
        SpringApplication.run(WalletApplication.class, args);
    }

    @Bean
    @LoadBalanced
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

//    @Bean
//    public TomcatEmbeddedServletContainerFactory tomcatEmbeddedServletContainerFactory() {
//        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
//        if (enableApr) {
//            arpLifecycle = new AprLifecycleListener();
//            tomcat.setProtocol("org.apache.coyote.http11.Http11AprProtocol");
//        }
//        return tomcat;
//    }


}
