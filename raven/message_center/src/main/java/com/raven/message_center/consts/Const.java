package com.raven.message_center.consts;

public class Const {

    /**
     * login
     */
    public static final int RE_LOGIN = 200002;
    public static final int NOT_AUTHORIZED = 200004;

    /**
     * auth
     */
    public static final String AUTHORIZATION = "Authorization";

    /**
     * webSocket
     */
    public static final String SUB_PROTOCOL_TOKEN = "Sec-WebSocket-Protocol";
    public static final String UID = "uid";
    public static final String VISITOR = "visitor";
    public static final String DING = "ding";
    public static final String DONG = "dong";

    /**
     * kafka topics
     */
    //聊天
    public static final String CHAT_ROOM = "chatRoomTopic";
    //赌博zhuangbi用
    public static final String TAKER = "takerTopic";
    //小红点
    public static final String RED_SPOT_NOTICE = "redSpotNoticeTopic";

    /**
     * 钱包操作
     */
    //操作成功
    public static final int SUCCESS = 100101;

    public static final String SUCCESS_MSG = "操作成功";

    public static final int INTERNAL_ERROR = 100999;
    public static final String INTERNAL_ERROR_MSG = "内部错误被我抓到了，请拨打10086";


    /**
     * 信息记录
     */
    public static final String NOTE_TAKER = "isNoteTaker";

    /**
     * 服务信息
     */
    public static final int SERVICE_ERROR = 300002;
    public static final String SERVICE_ERROR_MSG = "服务不可用";

    /**
     * messasge_center
     */

    public static final int WRONG_PARAM = 100202;
    public static final String WRONG_PARAM_MSG = "参数错误";
}