package com.riddler.guide.common;

import javax.websocket.Session;

public class Constant {
    public static final String WEBSOCKET_ID= "webSocketID_";

    public static final String KUAIXUN_WEB = "kuaixun_web";
    public static final String GROUP_ID = "web";
    public static final String TRADE_GUIDE = "trade_guide";

    public static final String SOCKET_USER="_socket_user"; //socket已登录
    public static final String SOCKET_VISITOR="_socket_visitor";//socket游客

    public static final String HISTORY_MSG_TYPE="-1"; //用户发送的信息
    public static final String USER_MSG_TYPE="0"; //用户发送的信息
    public static final String KAFKA_MSG_TYPE="1"; //kafka发送的信息

}
