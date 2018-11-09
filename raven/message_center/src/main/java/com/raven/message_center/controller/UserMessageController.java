package com.raven.message_center.controller;

import com.raven.message_center.annotation.AuthCheck;
import com.raven.message_center.consts.Auth;
import com.raven.message_center.consts.Const;
import com.raven.message_center.dto.RespDto;
import com.raven.message_center.dto.UserMessageDto;
import com.raven.message_center.service.UserMessageService;
import com.raven.message_center.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class UserMessageController {

    @Autowired
    TokenUtil tokenUtil;

    @Autowired
    UserMessageService userMessageService;


    /**
     * 获取用户信息
     *
     * @param request
     * @return
     * @throws Exception
     */
    @AuthCheck(Auth.NEED_LOGIN)
    @RequestMapping(value = "/message/userReadMessages/{i:[\\d{1}]}", method = RequestMethod.GET)
    @ResponseBody
    public RespDto getUserMessages(@PathVariable("i") int isRead, HttpServletRequest request) throws Exception {
        if (isRead != 1 && isRead != 0) {
            return new RespDto(Const.WRONG_PARAM, Const.WRONG_PARAM_MSG);
        }
        String token = request.getHeader(Const.AUTHORIZATION);
        String uid = tokenUtil.getProperty(token, Const.UID);
        List<UserMessageDto> messages = userMessageService.getUserMessages(uid, isRead);
        RespDto dto = new RespDto(Const.SUCCESS, Const.SUCCESS_MSG, messages);
        return dto;
    }

    @AuthCheck(Auth.NEED_LOGIN)
    @RequestMapping(value = "/message/userReadMessages/", method = RequestMethod.GET)
    @ResponseBody
    public RespDto getUserAllMessages(HttpServletRequest request) throws Exception {
        String token = request.getHeader(Const.AUTHORIZATION);
        String uid = tokenUtil.getProperty(token, Const.UID);
        List<UserMessageDto> messages = userMessageService.getUserMessages(uid);
        RespDto dto = new RespDto(Const.SUCCESS, Const.SUCCESS_MSG, messages);
        return dto;
    }

    /**
     * 把信息状态更改为已读
     *
     * @param msgIds
     * @return
     * @throws Exception
     */
    @AuthCheck(Auth.NEED_LOGIN)
    @PostMapping("/message/userUnReadMessages")
    @ResponseBody
    public RespDto updateUserMessagesStatusByMsgId(@RequestBody List<String> msgIds) throws Exception {
        userMessageService.updateUserMessagesStatusByMsgId(msgIds);
        return new RespDto(Const.SUCCESS, Const.SUCCESS_MSG);
    }

}
