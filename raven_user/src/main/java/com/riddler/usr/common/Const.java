package com.riddler.usr.common;

public class Const {

    //操作成功
    public static final int SUCCESS = 100101;
    //钱包错误
    public static final int WALLET_ERROR = 100102;
    //没有
    public static final int NOT_FOUND = 100103;
    //余额不足
    public static final int NOT_ENOUGH = 100104;

    public static final int RE_LOGIN = 200002;
    public static final int NOT_AUTHORIZED = 200004;

    //操作成功
    public static final String SUCCESS_MSG = "操作成功";
    //钱包错误
    public static final String WALLET_ERROR_MSG = "钱包错误";
    //没有
    public static final String NOT_FOUND_MSG = "没有";
    //余额不足
    public static final String NOT_ENOUGH_MSG = "余额不足";

    public static final int INTERNAL_ERROR = 100999;
    public static final String INTERNAL_ERROR_MSG = "内部错误被我抓到了，请拨打10086";

    /**
     * kafka topics
     */
    //聊天
    public static final String CHAT_ROOM = "chatRoomTopic";
    //赌博zhuangbi用
    public static final String TAKER = "takerTopic";
    //小红点
    public static final String RED_SPOT = "redSpotNoticeTopic";

    /**
     * 验证码超时时间
     */
    public final static int V_CODE_EX_TIME = 120;
    public final static String SECRET_KEY  = "41981651554964896526";

}
