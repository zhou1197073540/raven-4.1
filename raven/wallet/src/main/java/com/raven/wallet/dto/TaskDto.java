package com.raven.wallet.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties({"type", "typeStr"})
public class TaskDto implements Serializable {

    private int change;
    private String oid;
    private String uid;
    private String serialNum;
    private String date;
    private String time;
    private String createDate;
    private String createTime;
    private int comment = 1;
    private String callbackUrl; //可能没有
    private int type;   //初始化自动
    private String typeStr; //初始化自动

    public TaskDto() {

    }

    public TaskDto(int type, String typeStr) {
        this.type = type;
        this.typeStr = typeStr;
    }

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

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public void setDtoType(int type, String typeStr) {
        this.typeStr = typeStr;
        this.type = type;
    }

    @Override
    public String toString() {
        return "TaskDto{" +
                "change=" + change +
                ", oid='" + oid + '\'' +
                ", uid='" + uid + '\'' +
                ", serialNum='" + serialNum + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", comment=" + comment +
                ", callbackUrl='" + callbackUrl + '\'' +
                ", createTime='" + createTime + '\'' +
                ", createDate='" + createDate + '\'' +
                ", type=" + type +
                ", typeStr='" + typeStr + '\'' +
                '}';
    }
}
