package com.riddler.usr.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by daniel.luo on 2017/3/6.
 */
@Component
public final class RedisUtil {

    @Autowired
    private Environment env;

    private static JedisPool jedisPool = null;
    private static boolean init = false;
    Logger logger = LoggerFactory.getLogger(RedisUtil.class);

    /**
     * 初始化Redis连接池
     */
    @EventListener(ContextRefreshedEvent.class)
    public void init() {
        if (!init) {
            logger.info("redis pool init start");
            String address = env.getProperty("redis.address");
            int port = Integer.parseInt(env.getProperty("redis.port"));
            //可用连接实例的最大数目，默认值为8；
            //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
            int maxActive = Integer.parseInt(env.getProperty("redis.maxActive"));
            //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
            int maxIdle = Integer.parseInt(env.getProperty("redis.maxIdle"));
            //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
            int maxWait = Integer.parseInt(env.getProperty("redis.maxWaitMillis"));
            int timeout = Integer.parseInt(env.getProperty("redis.timeout"));
            //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
            boolean testOnBorrow = Boolean.parseBoolean(env.getProperty("redis.testOnBorrow"));

            try {
                JedisPoolConfig config = new JedisPoolConfig();
                config.setMaxTotal(maxActive);
                config.setMaxIdle(maxIdle);
                config.setMaxWaitMillis(maxWait);
                config.setTestOnBorrow(testOnBorrow);
                jedisPool = new JedisPool(config, address, port, timeout);
                logger.info("redis pool init over");
            } catch (Exception e) {
                logger.error("redis pool init error,\n", e);
            }
            init = true;
        }
    }

    /**
     * 获取Jedis实例
     *
     * @return
     */
    public synchronized static Jedis getJedis() {
        try {
            if (jedisPool != null) {
                Jedis resource = jedisPool.getResource();
                return resource;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
