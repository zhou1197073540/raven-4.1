package com.riddler.usr.dto;

public class ResponseBaseDTO {
    private String status;
    private Object data;   //存放给老黄需要的数据
    private String msg;//存放调用钱包返回的消息

    public String getStatus() {
        return status;
    }

    public ResponseBaseDTO setStatus(String status) {
        this.status = status;
        return this;
    }

    public Object getData() {
        return data;
    }

    public ResponseBaseDTO setData(Object data) {
        this.data = data;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public ResponseBaseDTO setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public ResponseBaseDTO(String status, Object data) {
        this.status = status;
        this.data = data;
    }

    public ResponseBaseDTO(String status, String msg, Object data) {
        this.status = status;
        this.data = data;
        this.msg = msg;
    }

    public ResponseBaseDTO() {
        super();
    }

    public static ResponseBaseDTO New() {
        return new ResponseBaseDTO();
    }
}
