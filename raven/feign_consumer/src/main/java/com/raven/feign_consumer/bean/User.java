package com.raven.feign_consumer.bean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class User {
    private String uid;
    private String username;
    private String password;
    private String phone_num;
    private String identity_id;
    private String age;
    private String name;
    private String income_level;
    private String bank_account;
    private String asset_info;
    private String area;
    private String risk_score;
    private String work;
    private String birth;
    private String sex;
    private String mail;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone_num() {
        return phone_num;
    }

    public void setPhone_num(String phone_num) {
        this.phone_num = phone_num;
    }

    public String getIdentity_id() {
        return identity_id;
    }

    public void setIdentity_id(String identity_id) {
        this.identity_id = identity_id;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIncome_level() {
        return income_level;
    }

    public void setIncome_level(String income_level) {
        this.income_level = income_level;
    }

    public String getBank_account() {
        return bank_account;
    }

    public void setBank_account(String bank_account) {
        this.bank_account = bank_account;
    }

    public String getAsset_info() {
        return asset_info;
    }

    public void setAsset_info(String asset_info) {
        this.asset_info = asset_info;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getRisk_score() {
        return risk_score;
    }

    public void setRisk_score(String risk_score) {
        this.risk_score = risk_score;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        Method[] methods = this.getClass().getMethods();
        try {
            for (Method method : methods) {
                if (method.getParameterTypes().length > 0) {
                    continue;
                }
                if (method.getName().startsWith("get") && !method.getName().equals("getClass")) {
                    Object value = method.invoke(this, null);
                    map.put(method.getName(), value.toString());
                }
            }

        } catch (InvocationTargetException ie) {

        } catch (IllegalAccessException e) {

        }
        return map;
    }
}
