package com.riddler.usr.utils;

public class WechatUtil {
    //临时二维码生成ticket
    public static String QR_ticket = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=%s";

    /**
     * 生成二维码的ticket
     */
    public static String generateQRTicket(int userId) {
        String jsonstr = new MapToJsonString().add("expire_seconds", 1800)
                .add("action_name", "QR_SCENE")
                .add("action_info",
                        new MapToJsonString().add("scene",
                                new MapToJsonString().add("scene_id", userId))).toJSONString();
        String access_token=AccessTokenUtil.getAccessToken();
        try {
            String html = SampleHttpUtil.doHttpsRequestJSON(String.format(QR_ticket,access_token ), jsonstr);
            return  DataUtil.getValfromJsonstr(html,"ticket");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
