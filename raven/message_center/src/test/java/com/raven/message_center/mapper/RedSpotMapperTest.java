package com.raven.message_center.mapper;


import com.raven.message_center.bean.RedSpotReceiveBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class RedSpotMapperTest {

    @Autowired
    RedSpotMapper mapper;

    @Test
    public void addRedSpotMsgTest() throws Exception {
        System.out.println("fucker");
//        RedSpotReceiveBean bean = new RedSpotReceiveBean();
//        bean.setMsg("fucker");
//        bean.setMsgId("msgId2");
//        bean.setUid("uid");
//        bean.setOperation("fuck you");
//        bean.setType(1);
//        bean.setCreateTime(LocalDateTime.now().toString());
//        mapper.addRedSpotMsg(bean);
    }
}
