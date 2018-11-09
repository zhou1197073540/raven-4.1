package com.riddler.usr.dto;

import java.io.Serializable;

public class RespDto implements Serializable {

    private static final long serialVersionUID = -5445762954238303616L;

    private String status;
    private String msg;
    private Object data;

    public RespDto() {
    }

    public RespDto(String status) {
        this.status = status;
    }

    public RespDto(String status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }
    public RespDto(String status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
