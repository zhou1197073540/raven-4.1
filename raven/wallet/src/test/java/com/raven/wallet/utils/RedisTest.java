package com.raven.wallet.utils;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class RedisTest {

    @Test
    public void redisSetTest() {
        Jedis r_conn = RedisUtil.getJedis();
        try {
            Map<String, String> result = r_conn.hgetAll("fucker");
            Long time = r_conn.ttl("fucker");
            if (result != null) {
                System.out.println("size:" + result.size());
            }
            System.out.println("ttl:" + time);
        } finally {
            r_conn.close();
        }
    }

    @Test
    public void redisCloseTest() {
        Jedis r_conn = RedisUtil.getJedis();
        System.out.println(r_conn.isConnected());
        r_conn.close();
        try {
            r_conn.close();
        } catch (JedisException e) {
            if (e.getMessage().contains("Could not return the resource to the pool")) {
                System.out.println("fucker~~!!!");
                //ignore
            }
            throw e;
        }
        System.out.println(r_conn.isConnected());
    }

    @Test
    public void redisDumpUnserialTest() {
        Jedis r_conn = RedisUtil.getJedis();
        byte[] bytes = r_conn.dump("926bbe9894ab9dc1d7f54bd3f650049d.login.token");
        System.out.println(bytes);
    }
}
