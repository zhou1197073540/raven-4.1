package com.riddler.usr.common;

public class MsgConstant {
    public final static String ERROR = "error";
    public final static String REMOTE_ERROR="remote_error";
    public final static String SUCCESS = "success";
    public static final String WS_UID = "web_socket_uid";
    public static final int REGISTER = 0;        //注册
    public static final int RECHARGE = 1;        //充值
    public static final int WITHDRAWALS = 2;        //提现
    public static final int GAMBLING_LOSS = 5;    //赌博亏损
    public static final int GAMBLING_ADD = 6;    //赌博赚钱
    public static final int LOGIN = 7;    //登录
    public static final int SIGN=8;         //签到
    public static final int ASSETS_FROM_POINTS=201;
    public static final int ASSETS_TO_POINTS=202;
    /**
     * 公司总账户的uid，记录的是公司当前所有的沣沅币，数据库中的表是wallet
     */
    public static final String COMPANY_UID = "9bbc9f913c7080756302312a0578bf90";
    public static final int REGISTER_SEND_POINTS = 500;//注册送积分
    public static final int FIRST_LOGIN_SEND_POINTS = 50;//首次登录送积分
    public static final int SIGN_SEND_POINTS = 50;//签到送积分


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

}
