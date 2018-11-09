package com.raven.message_center.service;

import com.raven.message_center.consts.Const;
import com.raven.message_center.dto.RespDto;
import com.raven.message_center.dto.TaskDto;
import org.springframework.stereotype.Service;

@Service
public class WalletHystrixService implements WalletRemoteService {

    @Override
    public RespDto changePoints(TaskDto pointsDto) {
        //todo send mail
        return new RespDto(Const.SERVICE_ERROR, Const.SERVICE_ERROR_MSG);
    }
}
