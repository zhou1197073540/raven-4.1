package com.riddler.usr.bean;

public class FofPerformance {
    private String name;
    private String mdd;//最大回撤
    private String sharp;//夏普率
    private String rate;//年化收益
    private String accrate;//累计收益
    private String mrate;//本月收益
    private String brate;//沪深
    private String tradedate; //日期

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMdd() {
        return mdd;
    }

    public void setMdd(String mdd) {
        this.mdd = mdd;
    }

    public String getSharp() {
        return sharp;
    }

    public void setSharp(String sharp) {
        this.sharp = sharp;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getAccrate() {
        return accrate;
    }

    public void setAccrate(String accrate) {
        this.accrate = accrate;
    }

    public String getMrate() {
        return mrate;
    }

    public void setMrate(String mrate) {
        this.mrate = mrate;
    }

    public String getBrate() {
        return brate;
    }

    public void setBrate(String brate) {
        this.brate = brate;
    }

    public String getTradedate() {
        return tradedate;
    }

    public void setTradedate(String tradedate) {
        this.tradedate = tradedate;
    }
}
