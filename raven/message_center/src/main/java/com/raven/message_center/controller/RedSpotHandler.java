package com.raven.message_center.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raven.message_center.config.webSocket.CustomWebSocketHandlerTemplate;
import com.raven.message_center.consts.Const;
import com.raven.message_center.service.RedSpotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class RedSpotHandler extends CustomWebSocketHandlerTemplate {

    @Autowired
    RedSpotService redSpotService;

    public RedSpotHandler() {
        super(new ConcurrentHashMap<String, WebSocketSession>(), new ConcurrentHashMap<String, WebSocketSession>(), "redSpot");
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        if (session.getAttributes().get(Const.UID) != null) {
            int count = redSpotService.checkRedSpotMsg(session.getAttributes().get(Const.UID).toString());
            if (count > 0) {
                String msg = "看到这条信息的少年，你很有发展";
                session.sendMessage(new TextMessage(msg));
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        isHeartBeat(session, message);
    }
}
