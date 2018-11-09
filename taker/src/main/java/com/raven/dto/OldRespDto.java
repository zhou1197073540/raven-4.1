package com.raven.dto;

import java.io.Serializable;

@Deprecated
public class OldRespDto implements Serializable {

    private boolean isSuccess;
    private String data;

    public OldRespDto() {
    }

    public OldRespDto(boolean isSuccess, String data) {
        this.isSuccess = isSuccess;
        this.data = data;
    }


    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}
