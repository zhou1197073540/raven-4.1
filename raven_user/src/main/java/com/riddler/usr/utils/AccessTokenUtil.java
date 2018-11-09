package com.riddler.usr.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONObject;
import redis.clients.jedis.Jedis;

public class AccessTokenUtil {

    private static String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
    public final static String redis_access_token="xiaoxinzhitou_access_token";

    public final static String appid = "wx347437ff565fbd34";

    public final static String secret = "17728e38d89436153b83da75cbad05a8";

    public static String access_token = "";

    public static final Logger log = LoggerFactory.getLogger(AccessTokenUtil.class);

    public static String getAccessToken() {
        Jedis jedis = RedisUtil.getJedis();
        boolean exist = jedis.exists(redis_access_token);
        String accessToken = "";
        if (exist) {
            accessToken = jedis.get(redis_access_token);
            log.info("old access_token ======:{}", accessToken);
        } else {
            accessToken = setAccessToken();
            log.info("generate new access_token =========>:{}", accessToken);
        }
        jedis.close();
        return accessToken;
    }

    private static String setAccessToken() {
        try {
            String html = SampleHttpUtil.getResult(String.format(url, appid, secret));
            JSONObject json = JSONObject.fromObject(html);
            Jedis jedis = RedisUtil.getJedis();
            if (json.containsKey("access_token")) {
                String token = json.getString("access_token");
                jedis.setex(redis_access_token, 6000, token);
                access_token = token;
                jedis.close();
                return token;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        String accessToken= AccessTokenUtil.getAccessToken();
        System.out.println("====== " + accessToken);
        System.out.println(">>>>>>>> " + getAccessToken());
    }
}
