package com.raven.common;

public class MsgConstant {
    public final static String ERROR = "error";
    public final static String SUCCESS = "success";
    public static final String WS_UID = "web_socket_uid";
    public static final int REGISTER = 0;        //注册
    public static final int RECHARGE = 1;        //充值
    public static final int WITHDRAWALS = 2;        //提现
    public static final int SALE_FYB = 3;        //充值
    public static final int BUY_FYB = 4;            //充值
    public static final int GAMBLING_LOSS = 5;    //赌博亏损
    public static final int GAMBLING_ADD = 6;    //赌博赚钱
    /**
     * 公司总账户的uid，记录的是公司当前所有的沣沅币，数据库中的表是wallet
     */
    public static final String COMPANY_UID = "9bbc9f913c7080756302312a0578bf90";
    public static final int REGISTER_SEND_FYB = 1000;//注册送沣沅币
    public static final int REGISTER_SEND_RMB = 1000;//注册送人民币


    /**
     * exchange_user_record表status字段状态
     */
    public static final int EX_WAITING = 0;
    public static final int EX_FINISH = 1;
    public static final int EX_CANCEL = 99;

    /**
     * ExchangeUserCreateDealParamsDTO data的key
     */
    public static final String UID = "uid";
    public static final String PRICE = "price";
    public static final String AMOUNT = "amount";
    public static final String SERIAL_NUM = "serialNum";
    public static final String DEAL_TYPE = "dealType";


    /**
     * 交易类型
     */
    public static final String BUY = "b";
    public static final String SELL = "s";

    /**
     * webSocket 用户标识
     */
    public static final String SOCKET_USER="_socket_user"; //socket已登录

    public static final String WEBSOCKET_ID= "webSocketID_";

    public static final String KUAIXUN_WEB = "kuaixun_web";
    public static final String GROUP_ID = "web";
    public static final String TRADE_GUIDE = "trade_guide";


    public static final String SOCKET_VISITOR="_socket_visitor";//socket游客

    public static final String HISTORY_MSG_TYPE="-1"; //用户发送的信息
    public static final String USER_MSG_TYPE="0"; //用户发送的信息
    public static final String KAFKA_MSG_TYPE="1"; //kafka发送的信息
}
