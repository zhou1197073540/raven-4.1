package com.raven.wallet.dto;

import java.io.Serializable;

public class QueryDto implements Serializable {

    private String uid;
    private String startDate;
    private String endDate;
    private int type;   //初始化自动
    private String typeStr; //初始化自动

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTypeStr() {
        return typeStr;
    }

    public void setTypeStr(String typeStr) {
        this.typeStr = typeStr;
    }

    @Override
    public String toString() {
        return "QueryDto{" +
                ", uid='" + uid + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", type=" + type +
                ", typeStr='" + typeStr + '\'' +
                '}';
    }
}
