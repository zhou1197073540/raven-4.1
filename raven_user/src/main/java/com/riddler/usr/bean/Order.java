package com.riddler.usr.bean;

/**
 * 预约
 */
public class Order {
    public String name;
    public String phone;
    public String mail;
    public String time;
    public String cates;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCates() {
        return cates;
    }

    public void setCates(String cates) {
        this.cates = cates;
    }
}
