package com.raven.message_center.service;

import com.raven.message_center.bean.RedSpotReceiveBean;
import com.raven.message_center.consts.Const;
import com.raven.message_center.mapper.RedSpotMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class RedSpotService {

    @Autowired
    Environment env;

    @Autowired
    RedSpotMapper redSpotMapper;

    public int checkRedSpotMsg(String uid) throws Exception {
        return redSpotMapper.getUserUnreadMsgCount(uid);
    }

    public void addRedSpotMsg(RedSpotReceiveBean bean) throws Exception {
        if ("1".equals(env.getProperty(Const.NOTE_TAKER))) {
            redSpotMapper.addRedSpotMsg(bean);
        }
    }
}
