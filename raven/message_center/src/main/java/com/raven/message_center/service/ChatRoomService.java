package com.raven.message_center.service;

import com.raven.message_center.bean.ChatRoomBean;
import com.raven.message_center.consts.Const;
import com.raven.message_center.dto.RespDto;
import com.raven.message_center.dto.TaskDto;
import com.raven.message_center.mapper.ChatRoomMapper;
import com.raven.message_center.mapper.UserMassageMapper;
import com.raven.message_center.utils.HashUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by zhouzhenyang on 2017/7/12.
 */
@Service
public class ChatRoomService {

    private static final Logger log = LoggerFactory.getLogger(ChatRoomService.class);

    @Autowired
    ChatRoomMapper chatRoomMapper;
    @Autowired
    UserMassageMapper userMassageMapper;

    @Autowired
    WalletRemoteService walletRemoteService;

    public String getImageByUid(String uid) throws Exception {
        return userMassageMapper.getUserImageByUid(uid);
    }

    public List<ChatRoomBean> getRecentMessages() throws Exception {
        return chatRoomMapper.getRecentMessages();
    }

    public void createNewMsg(ChatRoomBean bean) throws Exception {
        if (bean.getComment().getBytes("utf-8").length > 420) {
            bean.setComment(bean.getComment().substring(0, 420));
        }
        chatRoomMapper.createNewMsg(bean);
    }

    /**
     * 留言 comment=9
     *
     * @param bean
     */
    public void changePoints(ChatRoomBean bean) {
        TaskDto dto = new TaskDto();
        dto.setChange(5);
        dto.setComment(9);
        dto.setSerialNum(HashUtil.EncodeByMD5(bean.getUid() + Instant.now().getEpochSecond()));
        dto.setDate(LocalDate.now().toString());
        dto.setTime(LocalDateTime.now().toString());
        dto.setUid(bean.getUid());
        RespDto respDto = walletRemoteService.changePoints(dto);
        if (respDto.getStatus() != Const.SUCCESS) {
            log.error("wallet send error: {},{}", respDto.getStatus(), dto.toString());
        }
    }
}
