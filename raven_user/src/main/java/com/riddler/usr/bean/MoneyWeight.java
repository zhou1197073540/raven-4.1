package com.riddler.usr.bean;

/**
 * 每个etf总价的所占比例
 */
public class MoneyWeight {
    private String code;
    private float rate;//当前最新日期的etf价格/买入etf的价格
    private float profit;//总价格的净利润
    private String buy_time_price;

    public String getCode() {

        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public float getProfit() {
        return profit;
    }

    public void setProfit(float profit) {
        this.profit = profit;
    }

    public String getBuy_time_price() {
        return buy_time_price;
    }

    public void setBuy_time_price(String buy_time_price) {
        this.buy_time_price = buy_time_price;
    }
}
