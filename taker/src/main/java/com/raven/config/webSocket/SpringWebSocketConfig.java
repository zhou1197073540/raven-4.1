package com.raven.config.webSocket;

import com.raven.controller.IndexValueHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
/**
 * Created by zhouzhenyang on 2017/6/6.
 */

@Configuration
@EnableWebMvc
@EnableWebSocket
public class SpringWebSocketConfig extends WebMvcConfigurerAdapter implements WebSocketConfigurer {

    @Autowired
    SpringWebSocketHandlerInterceptor springWebSocketHandlerInterceptor;

    /**
     * 注册webSocket接口
     * @param registry
     */
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myWebSocketHandle(), "/webSocket/indexValue")
                .addInterceptors(springWebSocketHandlerInterceptor)
                .setAllowedOrigins("*");
//        registry.addHandler(myWebSocketHandle(), "/webSocketServer/sockjs")
//                .addInterceptors(springWebSocketHandlerInterceptor).setAllowedOrigins("*").withSockJS();
    }

    @Bean
    public IndexValueHandler myWebSocketHandle() {
        return new IndexValueHandler();
    }


}
