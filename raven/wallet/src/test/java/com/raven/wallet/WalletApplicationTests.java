package com.raven.wallet;

import com.raven.wallet.mapper.WalletMapper;
import com.raven.wallet.utils.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WalletApplicationTests {

    @Autowired
    private Environment env;

    @Autowired
    WalletMapper walletMapper;

    @Test
    public void contextLoads() {
        System.out.println(env.getProperty("redis.address"));
        System.out.println(env.getProperty("redis.port"));
        System.out.println(env.getProperty("redis.maxActive"));
        System.out.println(env.getProperty("redis.maxIdle"));
        System.out.println(env.getProperty("redis.maxWaitMillis"));
        System.out.println(env.getProperty("redis.timeout"));
        System.out.println(env.getProperty("redis.testOnBorrow"));
    }

    @Test
    public void redisCloseTest() throws Exception {
        Jedis r_conn = RedisUtil.getJedis();
        System.out.println(r_conn.get("fucker!").length());
    }

}
