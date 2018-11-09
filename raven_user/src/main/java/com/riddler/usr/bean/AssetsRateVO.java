package com.riddler.usr.bean;

public class AssetsRateVO {
    private String assets_code;
    private String flag;
    private String buy_time_money;//买时单个etf的总价
    private String buy_time;
    private String date;
    private String rate;
    private String close;
    private String total_money_rate;//相比昨天，当天的上涨或下跌价格的比率
    private String points;
    private String money_weight;

    public String getAssets_code() {
        return assets_code;
    }

    public void setAssets_code(String assets_code) {
        this.assets_code = assets_code;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getBuy_time_money() {
        return buy_time_money;
    }

    public void setBuy_time_money(String buy_time_money) {
        this.buy_time_money = buy_time_money;
    }

    public String getBuy_time() {
        return buy_time;
    }

    public void setBuy_time(String buy_time) {
        this.buy_time = buy_time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public String getTotal_money_rate() {
        return total_money_rate;
    }

    public void setTotal_money_rate(String total_money_rate) {
        this.total_money_rate = total_money_rate;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }


    public String getMoney_weight() {
        return money_weight;
    }

    public void setMoney_weight(String money_weight) {
        this.money_weight = money_weight;
    }
}
