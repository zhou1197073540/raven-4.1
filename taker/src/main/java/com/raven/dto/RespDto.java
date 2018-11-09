package com.raven.dto;

import java.io.Serializable;

public class RespDto implements Serializable {

    private static final long serialVersionUID = -5445762954238303616L;

    private int status;
    private String msg;
    private Object data;

    public RespDto() {
    }

    public RespDto(int status) {
        this.status = status;
    }

    public RespDto(int status, String msg) {

    }

    public RespDto(int status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
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
