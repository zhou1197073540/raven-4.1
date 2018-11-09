package com.raven.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.raven.common.MsgConstant;
import com.raven.consts.Const;
import com.raven.service.DailyIndexService;
import com.raven.utils.KafkaUtil;
import com.raven.utils.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class IndexValueHandler implements WebSocketHandler {
    @Autowired
    ObjectMapper mapper;

    @Autowired
    KafkaUtil kafkaUtil;

    @Autowired
    TokenUtil tokenUtil;

    @Autowired
    DailyIndexService dailyIndexService;
//    @Autowired
//    private SocketService socketService;

    private static Logger logger = LoggerFactory
            .getLogger(IndexValueHandler.class);

    public static final Map<String, WebSocketSession> userSocketSessionMap;

    static {
        userSocketSessionMap = new ConcurrentHashMap<String, WebSocketSession>();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session)
            throws Exception {
        // 存储链接用户
        saveUserInfo(session);
        // 链接成功
        logger.info(getUserId(session)
                + " connect to the websocket success..... 在线人数：" + userSocketSessionMap.size());
    }

    @Override
    public void handleMessage(WebSocketSession session,
                              WebSocketMessage<?> message) throws Exception {
        // TODO receive type index
//        socketService.handleUsrMessage(session,message);
        String fromId = getUserId(session);
        String msg = (String) message.getPayload();
        System.out.println("<<<<<<<<<<<<<<<<<<" + msg);
        JSONObject jo = null;
        try {
            jo = JSONObject.parseObject(msg);
        } catch (Exception e) {

        }
        if (null != jo) {
            String code = jo.getString("code");
            JSONObject resJo = new JSONObject();
            if (!StringUtils.isEmpty(code)) {
                resJo = dailyIndexService.getIndexHistoryData(code);
                resJo.put("status", "success");
                resJo.put("type", code);
                resJo.put("length", "history");
            } else {
                resJo.put("status", "failed");
            }
            sendMessageToUser(fromId, new TextMessage(JSONObject.toJSONString(resJo)));
        } else {
            JSONObject resJo = new JSONObject();
            resJo.put("status", "failed");
            sendMessageToUser(fromId, new TextMessage(JSONObject.toJSONString(resJo)));
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session,
                                     Throwable exception) throws Exception {
        if (session.isOpen()) {
            session.close();
        }
        logger.debug("websocket connection closed......");
        removeSession(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session,
                                      CloseStatus closeStatus) throws Exception {
        logger.debug("websocket connection closed......");
        removeSession(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 给所有在线用户发送消息
     *
     * @param message
     */
    public static void sendMessageToUsers(TextMessage message) {
        Iterator<Map.Entry<String, WebSocketSession>> it = userSocketSessionMap
                .entrySet().iterator();
        ExecutorService server = Executors.newFixedThreadPool(10);
        while (it.hasNext()) {
            final Map.Entry<String, WebSocketSession> entry = it.next();
            if (entry.getValue().isOpen()) {
                Runnable run = new Runnable() {
                    @Override
                    public void run() {
                        if (entry.getValue().isOpen()) {
                            try {
                                entry.getValue().sendMessage(message);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                server.execute(run);
            }
        }
    }

    /**
     * 给某个用户发送消息
     *
     * @param userName
     * @param message
     * @throws IOException
     */
    public static void sendMessageToUser(String userName, TextMessage message)
            throws IOException {
        WebSocketSession session = userSocketSessionMap.get(userName);
        if (session != null && session.isOpen()) {
            session.sendMessage(message);
        }
    }

    private void saveUserInfo(WebSocketSession session) {
        // 记录登录用户
        String uid = (String) session.getAttributes()
                .get(Const.UID);
        if (!StringUtils.isEmpty(uid)) {
            uid = uid + MsgConstant.SOCKET_USER;
            if (userSocketSessionMap.get(uid) == null) {
                userSocketSessionMap.put(uid, session);
            }
        } else {
            // 记录游客用户
            String visitor = (String) session.getAttributes().get(
                    Const.VISITOR);
            if (!StringUtils.isEmpty(visitor)) {
                String visitorID = session.getId() + Const.VISITOR;
                if (userSocketSessionMap.get(visitorID) == null) {
                    userSocketSessionMap.put(visitorID, session);
                }
            }
        }
    }

    private void removeSession(WebSocketSession session) {
        String uid = (String) session.getAttributes()
                .get(Const.UID);
        if (!StringUtils.isEmpty(uid)) {
            userSocketSessionMap.remove(uid + Const.UID);
        } else {
            String visitor = (String) session.getAttributes().get(
                    Const.VISITOR);
            if (!StringUtils.isEmpty(visitor)) {
                userSocketSessionMap.remove(session.getId()
                        + Const.VISITOR);
            }
        }
    }

    public String getUserId(WebSocketSession session) {
        String uid = (String) session.getAttributes()
                .get(Const.UID);
        if (!StringUtils.isEmpty(uid)) {
            return uid + Const.UID;
        } else {
            String visitor = (String) session.getAttributes().get(
                    Const.VISITOR);
            if (!StringUtils.isEmpty(visitor)) {
                return session.getId() + Const.VISITOR;
            }
        }
        return null;
    }
}
