package com.raven.wallet.consts;

public enum COMMENT {

    POINTS_REGISTER(0), //注册
    //    POINTS_IN(1),   //积分转入
//    POINTS_OUT(2),  //积分转出
    POINTS_JOIN_GAMBLE(5),  //参加赌博
    POINTS_GAMBLE_WIN(6),   //赌博赢钱
    POINTS_LOGIN(7),    //登录
    POINTS_SIGNED_IN(8),    //签到
    POINTS_CHAT(9), //聊天（留言）
    POINTS_TO_ASSETS(10),   //积分转出（到资产）
    POINTS_FROM_ASSETS(14), //积分转入（从资产）
    POINTS_USE_BOT(11), //使用机器人
    POINTS_PICK_STOCK(12),  //参加选股
    POINTS_PICK_STOCK_SHARE(13),  //选股分钱
    ASSETS_FROM_POINTS(201),    //资产转入（从积分）
    ASSETS_TO_POINTS(202);  //资产转出（到积分）

    public final int name;

    COMMENT(int name) {
        this.name = name;
    }
}
