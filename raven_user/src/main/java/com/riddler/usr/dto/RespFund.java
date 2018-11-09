package com.riddler.usr.dto;

import com.riddler.usr.bean.FundBaseInfo;

public class RespFund extends ResponseBaseDTO{
   private FundBaseInfo fund_one;

    public FundBaseInfo getFund_one() {
        return fund_one;
    }

    public void setFund_one(FundBaseInfo fund_one) {
        this.fund_one = fund_one;
    }

    public RespFund(String status, Object data, FundBaseInfo fund_one) {
        super(status, data);
        this.fund_one = fund_one;
    }
}
