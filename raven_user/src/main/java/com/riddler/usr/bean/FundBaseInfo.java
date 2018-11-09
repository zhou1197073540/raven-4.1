package com.riddler.usr.bean;

public class FundBaseInfo {
    private String id;
    private String fund_code;
    private String fund_name;
    private String fund_type;//基金类型
    private String fund_scale;//基金规模
    private String fund_manager;//基金经理
    private String establishment_day;//成立日
    private String administrator;//管理人
    private String grade;//基金评级
    private String manager_code;//经理代码
    private String service_charge;
    private String weight;

    public String getService_charge() {
        return service_charge;
    }

    public void setService_charge(String service_charge) {
        this.service_charge = service_charge;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFund_code() {
        return fund_code;
    }

    public void setFund_code(String fund_code) {
        this.fund_code = fund_code;
    }

    public String getFund_name() {
        return fund_name;
    }

    public void setFund_name(String fund_name) {
        this.fund_name = fund_name;
    }

    public String getFund_type() {
        return fund_type;
    }

    public void setFund_type(String fund_type) {
        this.fund_type = fund_type;
    }

    public String getFund_scale() {
        return fund_scale;
    }

    public void setFund_scale(String fund_scale) {
        this.fund_scale = fund_scale;
    }

    public String getFund_manager() {
        return fund_manager;
    }

    public void setFund_manager(String fund_manager) {
        this.fund_manager = fund_manager;
    }

    public String getEstablishment_day() {
        return establishment_day;
    }

    public void setEstablishment_day(String establishment_day) {
        this.establishment_day = establishment_day;
    }

    public String getAdministrator() {
        return administrator;
    }

    public void setAdministrator(String administrator) {
        this.administrator = administrator;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getManager_code() {
        return manager_code;
    }

    public void setManager_code(String manager_code) {
        this.manager_code = manager_code;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}
