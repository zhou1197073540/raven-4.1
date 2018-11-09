package com.riddler.usr.bean;

public class Ticker {
    private int rank;
    private String tradedate;//日期
    private String ticker;//股票代码
    private String corrval;//大盘关联度
    private String upval;//上涨趋势
    private String extra5;//近期超额收益
    private String turnover;//近期换手率
    private String close;////当前价格

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getTradedate() {
        return tradedate;
    }

    public void setTradedate(String tradedate) {
        this.tradedate = tradedate;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getCorrval() {
        return corrval;
    }

    public void setCorrval(String corrval) {
        this.corrval = corrval;
    }

    public String getUpval() {
        return upval;
    }

    public void setUpval(String upval) {
        this.upval = upval;
    }

    public String getExtra5() {
        return extra5;
    }

    public void setExtra5(String extra5) {
        this.extra5 = extra5;
    }

    public String getTurnover() {
        return turnover;
    }

    public void setTurnover(String turnover) {
        this.turnover = turnover;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }
}
