package com.raven.wallet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class ThreadPoolService {

    @Autowired
    Environment env;

    private static boolean init;
    private static ThreadPoolExecutor threadPoolExecutor;

    @EventListener(ContextRefreshedEvent.class)
    public void init() {
        if (!init) {
            threadPoolExecutor = new ThreadPoolExecutor(
                    10,
                    200,
                    3,
                    TimeUnit.HOURS, new LinkedBlockingQueue());
            init = true;
        }
    }

    public static ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }
}
