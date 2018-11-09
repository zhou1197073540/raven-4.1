package com.riddler.usr.wechatUtil;

import com.riddler.usr.utils.WechatUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class WeChatTests {
    @Test
    public void testGenerateQRTicket() {
        new WechatUtil().generateQRTicket(123);
    }
}
