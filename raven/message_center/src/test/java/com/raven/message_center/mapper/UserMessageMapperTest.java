package com.raven.message_center.mapper;

import com.google.common.collect.Lists;
import com.raven.message_center.dto.UserMessageDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class UserMessageMapperTest {

    @Autowired
    UserMassageMapper mapper;

    @Test
    public void getUserUnReadMessagesByUidTest() throws Exception {
        List<UserMessageDto> dtos = mapper.getUserMessagesByUid("uid");
        dtos.stream().forEach(dto -> {
            System.out.println(dto.getMsg());
            System.out.println(dto.getMsgId());
            System.out.println(dto.getType());
            System.out.println(dto.getCreateTime());
            System.out.println(dto.getIsRead());
        });
    }

    @Test
    public void updateUserMessagesStatusByMsgId() throws Exception {
        List<String> list = Lists.newArrayList("msgId","msgId1","msgId2");
        mapper.updateUserMessagesStatusByMsgId(list);
    }

    @Test
    public void getUserImageByUid() throws Exception {
        String image = mapper.getUserImageByUid("f5a5804d22335318befd4edcf732f2ee");
        System.out.println(image);
    }
}
