package com.riddler.usr.utils;

import com.yunpian.sdk.YunpianClient;
import com.yunpian.sdk.model.Result;
import com.yunpian.sdk.model.SmsSingleSend;

import java.util.Map;

/**
 * Created by daniel.luo on 2017/8/21.
 */
public class SmsUtil {
    public static final String APIKEY = "81606ad043a99579e925436285badc16";

    public static int singleSend(String code, String mobile) throws Exception {
        YunpianClient client = new YunpianClient(APIKEY);
        String text = "【谋智科技】您的验证码是" + code + "。请在2分钟内完成输入，如非本人操作，请忽略本短信。";
        Map<String, String> params = client.newParam(2);
        params.put("text", text);
        params.put("mobile", mobile);
        client.init();
        Result<SmsSingleSend> ret = client.sms().single_send(params);
        client.close();
        return ret.getCode();
    }
}