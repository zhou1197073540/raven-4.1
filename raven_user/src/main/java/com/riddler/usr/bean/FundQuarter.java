package com.riddler.usr.bean;

public class FundQuarter {
    private String id;
    private String fund_code;
    private String tricker_code;
    private String tricker_name;
    private String quarter_type;//季度类型（20173,20172,20171）
    private String position_type;//持仓类型（基金持仓，债券持仓）
    private String net_value_proportion;//占净值比率
    private String holding_shares;//持股数（万股）
    private String market_value;//持仓市值（万元）

    private String p_change;

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

    public String getTricker_code() {
        return tricker_code;
    }

    public void setTricker_code(String tricker_code) {
        this.tricker_code = tricker_code;
    }

    public String getTricker_name() {
        return tricker_name;
    }

    public void setTricker_name(String tricker_name) {
        this.tricker_name = tricker_name;
    }

    public String getQuarter_type() {
        return quarter_type;
    }

    public void setQuarter_type(String quarter_type) {
        this.quarter_type = quarter_type;
    }

    public String getPosition_type() {
        return position_type;
    }

    public void setPosition_type(String position_type) {
        this.position_type = position_type;
    }

    public String getNet_value_proportion() {
        return net_value_proportion;
    }

    public void setNet_value_proportion(String net_value_proportion) {
        this.net_value_proportion = net_value_proportion;
    }

    public String getHolding_shares() {
        return holding_shares;
    }

    public void setHolding_shares(String holding_shares) {
        this.holding_shares = holding_shares;
    }

    public String getMarket_value() {
        return market_value;
    }

    public void setMarket_value(String market_value) {
        this.market_value = market_value;
    }

    public String getP_change() {
        return p_change;
    }

    public void setP_change(String p_change) {
        this.p_change = p_change;
    }

}
