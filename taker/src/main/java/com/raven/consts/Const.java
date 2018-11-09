package com.raven.consts;

public class Const {

    public static final int RE_LOGIN = 200002;
    public static final int NOT_AUTHORIZED = 200004;

    //操作成功
    public static final int SUCCESS = 100101;
    //钱包错误
    public static final int WALLET_ERROR = 100102;
    //没有
    public static final int NOT_FOUND = 100103;
    //余额不足
    public static final int NOT_ENOUGH = 100104;
    //不支持的操作
    public static final int NOT_SUPPORT = 100105;

    //操作成功
    public static final String SUCCESS_MSG = "操作成功";
    //钱包错误
    public static final String WALLET_ERROR_MSG = "钱包错误";
    //没有
    public static final String NOT_FOUND_MSG = "没有";
    //余额不足
    public static final String NOT_ENOUGH_MSG = "余额不足";
    //操作不被支持 e.g.积分转资产积分不支持反向转
    public static final String NOT_SUPPORT_MSG = "不支持的操作";

    /**
     * 钱包dto的type
     */
    public static final int POINTS = 1;
    public static final int ASSETS = 2;

    /**
     * 扣分说明
     */
    public static final int ROBOT_ONE = -3;
    public static final int CHOOSE_STOCK = -3;

    /**
     * 消息功能对应的topic
     */
    public static final String TAKER_TOPIC = "takerTopic";
    public static final String RED_SPOT = "redSpotNoticeTopic";

    /**
     * webSocket
     */
    public static final String SUB_PROTOCOL_TOKEN = "Sec-WebSocket-Protocol";
    public static final String UID = "uid";
    public static final String VISITOR = "visitor";
    public static final String DING = "ding";
    public static final String DONG = "dong";
}
