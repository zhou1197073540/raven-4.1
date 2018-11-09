package com.raven.bean;

/**
 * Created by daniel.luo on 2017/7/14.
 */
public class OneStockInfo {
    private String ticker;
    private String industryID;
    private String stockName;
    private double close;
    private String pb;
    private String pe;

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getIndustryID() {
        return industryID;
    }

    public void setIndustryID(String industryID) {
        this.industryID = industryID;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public String getPb() {
        return pb;
    }

    public void setPb(String pb) {
        this.pb = pb;
    }

    public String getPe() {
        return pe;
    }

    public void setPe(String pe) {
        this.pe = pe;
    }
}
