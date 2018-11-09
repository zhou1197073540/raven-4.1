package com.raven.wallet.utils;


import com.raven.wallet.dto.PointsDto;
import com.raven.wallet.dto.TaskDto;
import com.raven.wallet.service.WalletService;
import javafx.concurrent.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Method;
import java.util.concurrent.ThreadPoolExecutor;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class ReflectionTest {

    @Autowired
    ThreadClass threadClass;

    @Test
    public void createInvokeThreadTest() throws Exception {
        for (Method method : WalletService.class.getMethods()) {
            System.out.println(method.getName());
        }
        Thread t = threadClass.createThread(WalletService.class.getMethod("changeWallet", TaskDto.class), new PointsDto());
        t.start();
    }

    @Test
    public void cloneTest() throws Exception {
        TaskDto dto = new PointsDto();
        dto.setSerialNum("shit");
        TaskDto dto1 = ObjectUtil.clone(dto);
        dto1.setSerialNum("fucker");
        System.out.println(dto.getSerialNum() + dto1.getSerialNum());
    }
}
