package com.riddler.guide.socket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebMvc
@EnableWebSocket
public class WebSocketConfig extends WebMvcConfigurerAdapter implements
		WebSocketConfigurer {

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(myWebSocketHandle(), "/webSocketServer")
				.addInterceptors(new HandShakeInterceptor()).setAllowedOrigins("*");
		registry.addHandler(myWebSocketHandle(), "/webSocketServer/sockjs")
				.addInterceptors(new HandShakeInterceptor()).setAllowedOrigins("*").withSockJS();
	}
	
	@Bean
	public MyWebSocketHandler myWebSocketHandle() {
		return new MyWebSocketHandler();
	}

}
