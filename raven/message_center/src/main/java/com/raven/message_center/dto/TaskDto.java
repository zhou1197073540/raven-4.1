package com.raven.message_center.dto;

import java.io.Serializable;

public class TaskDto implements Serializable {

    private static final long serialVersionUID = 3445536004283527212L;

    private int change;
    private String serialNum;
    private String uid;
    private String date;
    private String time;
    private int comment = 1;
    private String callbackUrl; //可能没有
    private int type;   //初始化自动
    private String typeStr; //初始化自动

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getComment() {
        return comment;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }

    public String getTypeStr() {
        return typeStr;
    }

    public void setTypeStr(String typeStr) {
        this.typeStr = typeStr;
    }

    public int getChange() {
        return change;
    }

    public void setChange(int change) {
        this.change = change;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "TaskDto{" +
                "change=" + change +
                ", serialNum='" + serialNum + '\'' +
                ", uid='" + uid + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", comment=" + comment +
                ", callbackUrl='" + callbackUrl + '\'' +
                ", type=" + type +
                ", typeStr='" + typeStr + '\'' +
                '}';
    }
}
