package com.raven.message_center.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.raven.message_center.bean.ChatRoomBean;
import com.raven.message_center.config.webSocket.CustomWebSocketHandlerTemplate;
import com.raven.message_center.consts.Const;
import com.raven.message_center.service.ChatRoomService;
import com.raven.message_center.utils.KafkaUtil;
import com.raven.message_center.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatRoomHandler extends CustomWebSocketHandlerTemplate {

    @Autowired
    ChatRoomService chatRoomService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    KafkaUtil kafkaUtil;

    @Autowired
    TokenUtil tokenUtil;

    private static final TypeFactory typeFactory = TypeFactory.defaultInstance();

    public ChatRoomHandler() {
        super(new ConcurrentHashMap<String, WebSocketSession>(), new ConcurrentHashMap<String, WebSocketSession>(), "chatRoom");
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        List<ChatRoomBean> msgs = chatRoomService.getRecentMessages();
        session.sendMessage(new TextMessage(mapper.writeValueAsString(msgs)));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        if (!isHeartBeat(session, message)) {
            if (isUser(session)) {
                //todo webSocket token续命
                List<ChatRoomBean> beans = mapper.readValue(message.getPayload(), typeFactory.constructCollectionType(List.class, ChatRoomBean.class));
                ChatRoomBean bean = beans.get(0);
                bean.setUid(session.getAttributes().get(Const.UID).toString());
                String image = chatRoomService.getImageByUid(bean.getUid());
                bean.setImage(image);
                kafkaUtil.sendMessage(Const.CHAT_ROOM, mapper.writeValueAsString(bean));
                chatRoomService.changePoints(bean);
                chatRoomService.createNewMsg(bean);
            }
        }
    }

    private boolean isUser(WebSocketSession session) {
        if (session.getAttributes().containsKey(Const.UID)) {
            return true;
        }
        return false;
    }
}
