package com.raven.message_center.config.webSocket;

import com.raven.message_center.controller.ChatRoomHandler;
import com.raven.message_center.controller.RedSpotHandler;
import com.raven.message_center.controller.ZhuangbilityHandler;
import com.raven.message_center.exception.WSDefaultExceptionDecorator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.WebSocketHandler;
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
    ChatRoomHandler chatRoomHandler;
    @Autowired
    ZhuangbilityHandler zhuangbilityHandler;
    @Autowired
    RedSpotHandler redSpotHandler;
    @Autowired
    SpringWebSocketHandlerInterceptor springWebSocketHandlerInterceptor;

    /**
     * 注册webSocket接口
     * TODO allowedOrigin需要定义好
     * todo handler里还没有过期token处理
     *
     * @param registry
     */
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatRoomHandlerWithD(), "/webSocket/chatRoom")
                .addHandler(zhuangbilityHandlerWithD(), "/webSocket/takerShowOff")
                .addHandler(redSpotHandlerWithD(), "/webSocket/redSpot")
                .addInterceptors(springWebSocketHandlerInterceptor)
                .setAllowedOrigins("*");
        //todo 反正也没写不支持ws的代码好吧，sockjs是什么鬼，我不知道
//        registry.addHandler(webSocketHandler(), "/webSocket/sockJs/msgBoard").addInterceptors(new SpringWebSocketHandlerInterceptor()).setAllowedOrigins("*").withSockJS();
    }

    /**
     * 创建带有异常捕捉的handler
     *
     * @return
     */
    @Bean
    public WebSocketHandler chatRoomHandlerWithD() {
        return new WSDefaultExceptionDecorator(chatRoomHandler);
    }

    @Bean
    public WebSocketHandler zhuangbilityHandlerWithD() {
        return new WSDefaultExceptionDecorator(zhuangbilityHandler);
    }

    @Bean
    public WebSocketHandler redSpotHandlerWithD() {
        return new WSDefaultExceptionDecorator(redSpotHandler);
    }
}
