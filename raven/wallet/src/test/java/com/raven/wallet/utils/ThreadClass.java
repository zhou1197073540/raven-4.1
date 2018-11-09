package com.raven.wallet.utils;

import com.raven.wallet.consts.Const;
import com.raven.wallet.dto.RespDto;
import com.raven.wallet.dto.TaskDto;
import com.raven.wallet.exception.WalletSQLException;
import com.raven.wallet.mapper.WalletMapper;
import com.raven.wallet.service.WalletService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;

@Component
public class ThreadClass {

    private static final Logger log = LoggerFactory.getLogger(WalletService.class);

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    WalletService walletService;

    public Thread createThread(Method method, TaskDto dto) throws Exception {
        Thread t = new Thread(() -> {
            try {
                method.invoke(walletService, dto);
            } catch (WalletSQLException wse) {
                log.error("walletError", wse);
                if (!StringUtils.isBlank(wse.getDto().getCallbackUrl())) {
                    restTemplate.put(wse.getDto().getCallbackUrl(), new RespDto(Const.WALLET_ERROR, Const.WALLET_ERROR_MSG, wse.getDto()));
                }
            } catch (Exception e) {
                log.error("unhandledError", e);
            }
        });
        t.setUncaughtExceptionHandler((thread, error) ->
                log.error("thread Error", thread, error)
        );
        return t;
    }
}
