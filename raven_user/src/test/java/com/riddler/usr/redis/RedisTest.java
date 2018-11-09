package com.riddler.usr.redis;

import com.riddler.usr.service.OthersService;
import com.riddler.usr.utils.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class RedisTest {
    @Autowired
    OthersService othersService;

    @Test
    public void redisSetTest() {
        Jedis jedis=RedisUtil.getJedis();
//        jedis.set("test_test","zzhou");
//        jedis.expire("test_test",100);
        long time=jedis.pttl("test_test");
        System.out.println(time);
    }

}
