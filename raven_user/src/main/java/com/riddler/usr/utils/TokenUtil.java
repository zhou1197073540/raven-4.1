package com.riddler.usr.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class TokenUtil {

    public final String NEW_TOKEN = "";
    private static final Logger logger = LoggerFactory.getLogger(TokenUtil.class);

    /**
     * 创建token
     *
     * @param uid
     * @return
     */
    public String createToken(String uid) {
        return HashUtil.EncodeByMD5(uid + String.valueOf(Instant.now().getEpochSecond()));
    }

    /**
     * 将token上传到redis
     *
     * @param token
     * @param uMap    用户信息map
     * @param seconds token寿命
     */
    public void uploadToken(String token, Map<String, String> uMap, int seconds) {
        Jedis r_conn = RedisUtil.getJedis();
        String loginToken = token + ".login.token";
        try {
            r_conn.hmset(loginToken, uMap);
            r_conn.expire(loginToken, seconds);
        } finally {
            r_conn.close();
        }
    }

    /**
     * 更新token
     *
     * @param token
     * @return
     *          null：过期token
     *          NEW_TOKEN：新token
     *          其他：无需续命
     */
    public String[] renewToken(String token) {
        Jedis r_conn = RedisUtil.getJedis();
        String loginToken = token + ".login.token";
        Map<String, String> userInfo = r_conn.hgetAll(loginToken);
        try {
            if (isValid(token)) {
                String[] ret = new String[2];
                Long timeLeft = r_conn.ttl(loginToken);
                logger.info("timeLeft:{}", timeLeft);
                //token快过期
                if (timeLeft <= 60 * 30 && timeLeft != -2) {
                    logger.info("token is expiring,timeLeft:{}, token:{}", timeLeft, loginToken);
                    if (r_conn.hget(loginToken, "expired") == null) {
                        uploadToken(token, userInfo, 60 * 5);
                        r_conn.hset(loginToken, "expired", "1");
                        String newLoginToken = HashUtil.EncodeByMD5(userInfo.get("uid") + String.valueOf(Instant.now().getEpochSecond()));
                        uploadToken(newLoginToken, userInfo, 60 * 60 * 5);
                        ret[0] = NEW_TOKEN;
                        ret[1] = newLoginToken;
                        return ret;
                    } else {
                        return ret;
                    }
                    //健康token
                } else if (timeLeft > 60 * 30) {
                    return ret;

                    //极限情况，很遗憾还是过期了
                } else {
                    return null;
                }
            }
        } finally {
            r_conn.close();
        }
        //过期token
        return null;
    }

    /**
     * 从redis中删除token
     *
     * @param token
     */
    public void removeToken(String token) {
        String loginToken = token + ".login.token";
        Jedis r_conn = RedisUtil.getJedis();
        try {
            r_conn.del(loginToken);
        } finally {
            r_conn.close();
        }
    }


    /**
     * 根据token和key(field)获取value值
     * e.g: getProperty("token","uid") 获取当前token对应uid的值
     *
     * @param token
     * @param field
     * @return
     */
    public String getProperty(String token, String field) {
        String loginToken = token + ".login.token";
        Jedis r_conn = RedisUtil.getJedis();
        try {
            String value = r_conn.hget(loginToken, field);
            return value;
        } finally {
            r_conn.close();
        }
    }

    /**
     * 检查当前token是否可用
     * @param token
     * @return
     */
    private boolean isValid(String token) {
        Jedis r_conn = RedisUtil.getJedis();
        String loginToken = token + ".login.token";
        try {
            if (r_conn.exists(loginToken)) {
                return true;
            }
        } finally {
            r_conn.close();
        }
        return false;
    }

    public static void main(String[] args){
        Map<String,String> map=new HashMap<>();
        map.put("uid","zhouchenxisss");
        map.put("username","我是光头强");
        TokenUtil tokenUtil=new TokenUtil();
        tokenUtil.uploadToken("112233",map,5*60*60);
        String getstr= tokenUtil.getProperty("112233","username");
        System.out.println(getstr);
    }



}
