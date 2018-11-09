package com.riddler.usr.service;

import com.riddler.usr.bean.*;
import com.riddler.usr.mapper.FundMapper;
import com.riddler.usr.mapper.OthersMapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FundService {
    private static final Logger logger = LoggerFactory.getLogger(FundService.class);
    @Autowired
    FundMapper fundMapper;


    public List<FundBaseInfo> selectFundCombine() {
        return fundMapper.selectFundCombine();
    }

    public List<FundHistory> selectFundHisttoryNetValue(String code) {
        return fundMapper.selectFundHisttoryNetValue(code);
    }

    public List<FundQuarter> tickerBondPosition(String fund_code,String position_type) {
        Map<String,String> map=new HashMap<>();
        map.put("fund_code",fund_code);
        map.put("position_type",position_type);
        List<FundQuarter> list= fundMapper.tickerBondPosition(map);
        for(FundQuarter fund:list){
            String change=fundMapper.selectP_change(fund);
            if(StringUtils.isBlank(change)) change="0.00";
            fund.setP_change(change);
        }
        return list;
    }

    public FundBaseInfo selectByFundCode(String code) {
        return fundMapper.selectByFundCode(code);
    }

    public List<FofNetValue> selectFofNetValue() {
        return fundMapper.selectFofNetValue();
    }

    public FofPerformance selectFofPerformance() {
        return fundMapper.selectFofPerformance();
    }

    public List<FofRateMkt> getFofRateMktHis() throws Exception {
        return fundMapper.getFofRateMktHis();
    }
}
