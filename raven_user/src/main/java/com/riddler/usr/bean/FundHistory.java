package com.riddler.usr.bean;

public class FundHistory {
    private String id;
    private String fund_code;
    private String unit_net_value;//单位净值
    private String total_net_value;//累计净值
    private String daily_growth_rate;//日增长率
    private String date;//日期

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

    public String getUnit_net_value() {
        return unit_net_value;
    }

    public void setUnit_net_value(String unit_net_value) {
        this.unit_net_value = unit_net_value;
    }

    public String getTotal_net_value() {
        return total_net_value;
    }

    public void setTotal_net_value(String total_net_value) {
        this.total_net_value = total_net_value;
    }

    public String getDaily_growth_rate() {
        return daily_growth_rate;
    }

    public void setDaily_growth_rate(String daily_growth_rate) {
        this.daily_growth_rate = daily_growth_rate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
