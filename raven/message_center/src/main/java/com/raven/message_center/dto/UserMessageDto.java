package com.raven.message_center.dto;

import java.io.Serializable;

/**
 * 用户信息
 */
public class UserMessageDto implements Serializable {

    private static final long serialVersionUID = 8848982325001311929L;
    private int type;
    private String msgId;
    private String msg;
    private String createTime;
    private String isRead;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }
}
