package com.raven.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniel.luo on 2017/7/14.
 */
public class OneStockModel {
    private String code;
    private String industryID;
    private String stockName;
    private String close;
    private String pb;
    private String pe;
    private List<OneStockHistory> history = new ArrayList<OneStockHistory>();

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

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<OneStockHistory> getHistory() {
        return history;
    }

    public void setHistory(List<OneStockHistory> history) {
        this.history = history;
    }
}
