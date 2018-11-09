package com.riddler.usr.bean;

public class StockIndustry {
    private String industryID;
    private String ticker;
    private String code;
    private String stockname;
    private String pinyin_firstword;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIndustryID() {
        return industryID;
    }

    public void setIndustryID(String industryID) {
        this.industryID = industryID;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getStockname() {
        return stockname;
    }

    public void setStockname(String stockname) {
        this.stockname = stockname;
    }

    public String getPinyin_firstword() {
        return pinyin_firstword;
    }

    public void setPinyin_firstword(String pinyin_firstword) {
        this.pinyin_firstword = pinyin_firstword;
    }
}
