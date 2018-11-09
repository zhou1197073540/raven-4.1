package com.raven.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

public abstract class TaskDto implements Serializable {

    private int change;
    private String oid;
    private String uid;
    private String date;
    private String time;
    private int comment = 1;
    @JsonIgnoreProperties
    private String callbackUrl; //可能没有
    @JsonIgnoreProperties
    private int type;   //初始化自动
    @JsonIgnoreProperties
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

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
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
                ", oid='" + oid + '\'' +
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
