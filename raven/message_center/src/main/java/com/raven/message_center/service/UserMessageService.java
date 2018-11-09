package com.raven.message_center.service;

import com.raven.message_center.dto.UserMessageDto;
import com.raven.message_center.mapper.UserMassageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserMessageService {

    @Autowired
    UserMassageMapper userMassageMapper;

    public List<UserMessageDto> getUserMessages(String uid, int isRead) throws Exception {
        return userMassageMapper.getUserMessagesByUidAndIsRead(uid,isRead);
    }

    public List<UserMessageDto> getUserMessages(String uid) throws Exception {
        return userMassageMapper.getUserMessagesByUid(uid);
    }

    public void updateUserMessagesStatusByMsgId(List<String> msgIds) throws Exception {
        userMassageMapper.updateUserMessagesStatusByMsgId(msgIds);
    }

}