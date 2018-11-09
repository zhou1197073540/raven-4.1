package com.raven.wallet.consts;

public class Const {

    /**
     * 钱包change_log status code
     */
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
    //操作不被支持
    public static final String NOT_SUPPORT_MSG = "不支持的操作";

    /**
     * 钱包dto的type
     */
    public static final int POINTS = 1;
    public static final String POINTS_STR = "_points";
    public static final int ASSETS = 2;
    public static final String ASSETS_STR = "_assets";
    public static final int RMB = 3;
    public static final String RMB_STR = "_rmb";

    /**
     * 钱包冷冻解冻
     */
    public static final int FROZEN = 0;
    public static final int UNFROZEN = 1;
}
