package com.raven.bean;

/**
 * Created by luoxudong on 2017/12/13.
 */
public class FocusRank {
    private String ticker;
    private String stock_name;
    private String rank;
    private String focus_count;
    private String trend;

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getStock_name() {
        return stock_name;
    }

    public void setStock_name(String stock_name) {
        this.stock_name = stock_name;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getFocus_count() {
        return focus_count;
    }

    public void setFocus_count(String focus_count) {
        this.focus_count = focus_count;
    }

    public String getTrend() {
        return trend;
    }

    public void setTrend(String trend) {
        this.trend = trend;
    }
}
