package com.riddler.guide.dto;

import com.alibaba.fastjson.JSONObject;

public class WsDto {

    public String msgType;
    public Object data;

    public WsDto(String msgType, Object data) {
        this.msgType = msgType;
        this.data = data;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String toJSONString() {
        return JSONObject.toJSON(this).toString();
    }
}
