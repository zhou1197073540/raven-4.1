package com.raven.message_center.controller;

import com.raven.message_center.config.webSocket.CustomWebSocketHandlerTemplate;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class ZhuangbilityHandler extends CustomWebSocketHandlerTemplate {

    public ZhuangbilityHandler() {
        super(new ConcurrentHashMap<String, WebSocketSession>(), new ConcurrentHashMap<String, WebSocketSession>(), "zhuangbi");
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        isHeartBeat(session, message);
    }
}
