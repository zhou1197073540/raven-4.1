package com.riddler.usr.utils;

import com.google.common.primitives.Bytes;
import io.jsonwebtoken.*;
import redis.clients.jedis.Jedis;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * Created by zhouzhenyang on 2017/7/5.
 */
public class TokenCreator {

    public String createToken(String uid, long ttlMillis) {

        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(HashUtil.EncodeByMD5(uid + TimeUtil.getTimeStamp() + Math.random()));
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder()
                .setId(uid)
                .setIssuedAt(Date.from((LocalDateTime.now().toInstant(ZoneOffset.UTC))))
                .signWith(signatureAlgorithm, signingKey);

        //过期时间
        if (ttlMillis >= 0) {
            long expMillis = Long.parseLong(TimeUtil.getTimeStamp()) + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        //Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }

    public boolean isValid(String token) {

        byte[] value = getUidAndKeyValueFromCache(token);
        if (String.valueOf(value).equals("nil")) {
            return false;
        }
        int index = Bytes.indexOf(value, new byte[]{'|'});
        byte[] apiKeySecretBytes = new byte[value.length - index];
        int y = 0;
        for (int i = index; i < value.length; i++) {
            apiKeySecretBytes[y] = value[i];
        }

        try {
            //解析JWT字符串中的数据，并进行最基础的验证
            Claims claims = Jwts.parser()
                    .setSigningKey(apiKeySecretBytes) //SECRET_KEY是加密算法对应的密钥，jwt可以自动判断机密算法
                    .parseClaimsJws(token) //jwt是JWT字符串
                    .getBody();
            return true;
        }

        //在解析JWT字符串时，如果密钥不正确，将会解析失败，抛出SignatureException异常，说明该JWT字符串是伪造的
        //在解析JWT字符串时，如果‘过期时间字段’已经早于当前时间，将会抛出ExpiredJwtException异常，说明本次请求已经失效
        catch (SignatureException | ExpiredJwtException e) {
            if (e instanceof ExpiredJwtException) {
                System.out.println("过期了");
            } else {
                //ignore for now
                System.out.println("秘钥错误");
            }
            return false;
        }
    }

    private void updatePrivateKeyToCache(String uid, String token, byte[] privateKey) {
        Jedis jconn = RedisUtil.getJedis();
        byte[] key = Bytes.concat(token.getBytes(), new byte[]{'_', 't'});
        byte[] value = Bytes.concat(uid.getBytes(), new byte[]{'|'}, privateKey);
        jconn.setex(key, 1 * 60 * 60 * 24 * 3, value);
    }

    private byte[] getUidAndKeyValueFromCache(String token) {
        Jedis jconn = RedisUtil.getJedis();
        byte[] key = Bytes.concat(token.getBytes(), new byte[]{'_', 't'});
        byte[] value = jconn.get(key);
        return value;
    }
}
