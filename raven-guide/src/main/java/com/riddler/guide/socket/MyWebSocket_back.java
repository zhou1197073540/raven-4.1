//package com.riddler.guide.socket;
//
//import com.alibaba.fastjson.JSON;
//import com.riddler.guide.bean.SocketMessage;
//import com.riddler.guide.common.Constant;
//import com.riddler.guide.service.SocketService_old;
//import org.springframework.context.ConfigurableApplicationContext;
//import org.springframework.stereotype.Component;
//
//import javax.websocket.*;
//import javax.websocket.server.ServerEndpoint;
//import java.io.IOException;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//@ServerEndpoint(value = "/websocket")
//@Component
//public class MyWebSocket {
//    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
//    private static int onlineCount = 0;
//
//    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
//    public static final Map<String, Session> userSocketSessionMap
//            = new ConcurrentHashMap<String,Session>();
//
//    private SocketService_old socketService;
//    private static ConfigurableApplicationContext context;
//
//    public static void setApplicationContext(ConfigurableApplicationContext applicationContext) {
//        MyWebSocket.context = applicationContext;
//    }
//
//    /**
//     * 连接建立成功调用的方法
//     */
//    @OnOpen
//    public void onOpen(Session session) {
//        addOnlineCount();           //在线数加1
//        System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
//        try {
//            String visitorID = Constant.WEBSOCKET_ID+session.getId();
//            if (userSocketSessionMap.get(visitorID) == null) {
//                userSocketSessionMap.put(visitorID, session);
//            }
//            socketService= (SocketService_old) context.getBean("socketService_old");
//            SocketMessage msg = new SocketMessage();
//            msg.setType(Constant.HISTORY_MSG_TYPE);
//            msg.setData(socketService.getHistoryNews());
//
//            session.getAsyncRemote().sendText(JSON.toJSONString(msg));
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("IO异常");
//        }
//    }
//
//    /**
//     * 连接关闭调用的方法
//     */
//    @OnClose
//    public void onClose(Session session) {
//        userSocketSessionMap.remove(getSocketId(session));  //从set中删除
//        subOnlineCount();           //在线数减1
//        System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
//    }
//
//    /**
//     * 收到客户端消息后调用的方法
//     * @param message 客户端发送过来的消息
//     */
//    @OnMessage
//    public void onMessageToUsrs(Session session,String message) throws IOException {
//        System.out.println("来自客户端的消息:" + message);
//        socketService= (SocketService_old) context.getBean("socketService_old");
//        socketService.handleUsrMessage(session,message);
//    }
//
//    /**
//     * 发生错误时调用
//     */
//    @OnError
//    public void onError(Session session, Throwable error) {
//        System.out.println("发生错误");
//        userSocketSessionMap.remove(getSocketId(session));  //从set中删除
//        error.printStackTrace();
//    }
//
//    public static void sendMessageToUsers(String message){
//        Iterator<Entry<String,Session>> it = userSocketSessionMap
//                .entrySet().iterator();
//        ExecutorService server = Executors.newFixedThreadPool(10);
//        while (it.hasNext()) {
//            final Entry<String, Session> entry = it.next();
//            if (entry.getValue().isOpen()) {
//                Runnable run = new Runnable() {
//                    @Override
//                    public void run() {
//                        if (entry.getValue().isOpen()) {
//                            try {
//                                entry.getValue().getAsyncRemote().sendText(message);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                };
//                server.execute(run);
//            }
//        }
//    }
//
//
//    public String getSocketId(Session session){
//        return Constant.WEBSOCKET_ID+session.getId();
//    }
//
//
//    public static synchronized int getOnlineCount() {
//        return onlineCount;
//    }
//
//    public static synchronized void addOnlineCount() {
//        MyWebSocket.onlineCount++;
//    }
//
//    public static synchronized void subOnlineCount() {
//        MyWebSocket.onlineCount--;
//    }
//}
