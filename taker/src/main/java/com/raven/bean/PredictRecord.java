package com.raven.bean;

/**
 * Created by daniel.luo on 2017/6/9.
 */
public class PredictRecord {
    private String s_type;
    private String amount;
    private String action_time;
    private String buy_status;
    private String last_index;
    private String close_index;
    private String order_status;

    public String getS_type() {
        return s_type;
    }

    public void setS_type(String s_type) {
        this.s_type = s_type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAction_time() {
        return action_time;
    }

    public void setAction_time(String action_time) {
        this.action_time = action_time;
    }

    public String getBuy_status() {
        return buy_status;
    }

    public void setBuy_status(String buy_status) {
        this.buy_status = buy_status;
    }

    public String getLast_index() {
        return last_index;
    }

    public void setLast_index(String last_index) {
        this.last_index = last_index;
    }

    public String getClose_index() {
        return close_index;
    }

    public void setClose_index(String close_index) {
        this.close_index = close_index;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }
}
