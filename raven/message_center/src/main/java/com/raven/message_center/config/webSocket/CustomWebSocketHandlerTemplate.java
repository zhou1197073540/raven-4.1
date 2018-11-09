package com.raven.message_center.config.webSocket;

import com.raven.message_center.consts.Const;
import com.raven.message_center.utils.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhouzhenyang on 2017/6/6.
 */
public abstract class CustomWebSocketHandlerTemplate extends TextWebSocketHandler {

    @Autowired
    TokenUtil tokenUtil;

    private ConcurrentHashMap<String, WebSocketSession> users;
    private ConcurrentHashMap<String, WebSocketSession> visitors;
    private Logger logger = LoggerFactory.getLogger(CustomWebSocketHandlerTemplate.class);
    private String type;
    private AtomicInteger uCount = new AtomicInteger(0);
    private AtomicInteger vCount = new AtomicInteger(0);

    public CustomWebSocketHandlerTemplate() {
        // TODO Auto-generated constructor stub
    }

    public CustomWebSocketHandlerTemplate(
            ConcurrentHashMap<String, WebSocketSession> users, ConcurrentHashMap<String, WebSocketSession> visitors, String type) {
        this.users = users;
        this.visitors = visitors;
        this.type = type;
    }

    /**
     * 连接成功时候，会触发页面上onopen方法
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        session.getAttributes().put("type", type);
        if (session.getAttributes().get(Const.UID) != null) {
            users.put(session.getId(), session);
            logger.info("users new connection added {} count : {}", type, uCount.addAndGet(1));
        } else {
            visitors.put(session.getId(), session);
            logger.info("visitors new connection added {} count : {}", type, vCount.addAndGet(1));
        }
    }

    /**
     * 关闭连接时触发
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        remove(session);
    }

    /**
     * js调用webSocket.send时候，会调用该方法
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    }

    /**
     * 叮咚心跳检查
     *
     * @param session
     * @param message
     * @throws Exception
     */
    public boolean isHeartBeat(WebSocketSession session, TextMessage message) throws Exception {
        if (Const.DING.equals(message.getPayload())) {
            session.sendMessage(new TextMessage(Const.DONG));
            return true;
        }
        return false;
    }

    private void renewToken(WebSocketSession session, TextMessage message) {
//        tokenUtil.renewToken()
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error("transport error {} session error: ", type, exception);
        remove(session);
        if (session.isOpen()) {
            session.close();
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 给某个用户发送消息
     *
     * @param message
     */
    public void sendMessageToUser(String uid, TextMessage message) {
        for (WebSocketSession user : users.values()) {
            if (user.getAttributes().get(Const.UID).equals(uid)) {
                sendMessage(user, message);
            }
        }
    }

    /**
     * 给所有在线用户发送消息
     *
     * @param message
     */
    public void sendMessageToUsers(TextMessage message) {
        for (WebSocketSession user : users.values()) {
            sendMessage(user, message);
        }
        for (WebSocketSession visitor : visitors.values()) {
            sendMessage(visitor, message);
        }
    }

    /**
     * 发送消息
     *
     * @param session
     * @param message
     */
    private void sendMessage(WebSocketSession session, TextMessage message) {
        try {
            if (session.isOpen()) {
                session.sendMessage(message);
            } else {
                remove(session);
                session.close();
            }
        } catch (IOException e) {
            remove(session);
        }
    }

    public void remove(WebSocketSession session) {
        if (session.getAttributes().get(Const.UID) != null) {
            if (users.remove(session.getId()) != null) {
                logger.info("user session closed {} size : {}", type, uCount.addAndGet(-1));
            }
        } else {
            if (visitors.remove(session.getId()) != null) {
                logger.info("visitor session closed {} size : {}", type, vCount.addAndGet(-1));
            }
        }
    }
}
