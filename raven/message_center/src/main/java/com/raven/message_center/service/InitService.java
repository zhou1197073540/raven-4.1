package com.raven.message_center.service;

import com.raven.message_center.listener.ChatRoomListener;
import com.raven.message_center.listener.RedSpotListener;
import com.raven.message_center.listener.ZhuangbilityListener;
import com.raven.message_center.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class InitService {

    @Autowired
    ChatRoomListener chatRoomListener;
    @Autowired
    RedSpotListener redSpotListener;
    @Autowired
    ZhuangbilityListener zhuangbilityListener;
    @Autowired
    RedisUtil redisUtil;

    private static boolean init = true;
    private static ThreadPoolExecutor threadPoolExecutor;

    @EventListener(ContextRefreshedEvent.class)
    public void init() {
        if (init) {
            threadPoolExecutor = new ThreadPoolExecutor(
                    10,
                    200,
                    3,
                    TimeUnit.HOURS, new LinkedBlockingQueue());

            threadPoolExecutor.execute(chatRoomListener);
            threadPoolExecutor.execute(redSpotListener);
            threadPoolExecutor.execute(zhuangbilityListener);
            redisUtil.init();
            init = false;
        }
    }

    public static ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }
}
