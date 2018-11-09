package com.riddler.usr.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class PointsDto{

    private int change;
    private String uid;
    private String date;
    private String time;
    private int comment = 1;
    private String oid;

    @JsonIgnoreProperties
    private String callbackUrl; //可能没有

    public int getChange() {
        return change;
    }

    public PointsDto setChange(int change) {
        this.change = change;
        return this;
    }



    public String getUid() {
        return uid;
    }

    public PointsDto setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public String getDate() {
        return date;
    }

    public PointsDto setDate(String date) {
        this.date = date;
        return this;
    }

    public String getTime() {
        return time;
    }

    public PointsDto setTime(String time) {
        this.time = time;
        return this;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public PointsDto setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
        return this;
    }

    public String getOid() {
        return oid;
    }

    public PointsDto setOid(String oid) {
        this.oid = oid;
        return this;
    }

    public int getComment() {
        return comment;
    }

    public PointsDto setComment(int comment) {
        this.comment = comment;
        return this;
    }

    public static PointsDto  New() {
        return  new PointsDto();
    }


}
