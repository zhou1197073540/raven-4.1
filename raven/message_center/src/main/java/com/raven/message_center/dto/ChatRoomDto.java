package com.raven.message_center.dto;

import com.raven.message_center.bean.ChatRoomBean;

public class ChatRoomDto {

    private String authorization;
    private ChatRoomBean data;

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public ChatRoomBean getData() {
        return data;
    }

    public void setData(ChatRoomBean data) {
        this.data = data;
    }
}
