package com.raven.message_center.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by zhouzhenyang on 2017/7/12.
 */
public class ChatRoomBean {

    @JsonIgnore
    private String uid;
    private String nickName;
    private String indexType;
    private String riseFall;
    private String star;
    private String reason;
    private String comment;
    private String createTime;
    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getIndexType() {
        return indexType;
    }

    public void setIndexType(String indexType) {
        this.indexType = indexType;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRiseFall() {
        return riseFall;
    }

    public void setRiseFall(String riseFall) {
        this.riseFall = riseFall;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
