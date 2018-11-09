package com.riddler.usr.service;

import com.riddler.usr.bean.*;
import com.riddler.usr.dto.RespDto;
import com.riddler.usr.mapper.AssetsMapper;
import com.riddler.usr.mapper.OthersMapper;
import com.riddler.usr.utils.ResponseUtil;
import com.riddler.usr.utils.StringUtil;
import com.riddler.usr.utils.TimeUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class OthersService {
    private static final Logger logger = LoggerFactory.getLogger(OthersService.class);
    @Autowired
    OthersMapper othersMapper;

    public List<Ticker> selectRiseStocks() {
        return othersMapper.selectRiseStocks();
    }

    public List<String> selectLeadingTheme() {
        return othersMapper.selectLeadingTheme();
    }

    public List<Ticker> selectFallStocks() {
        return othersMapper.selectFallStocks();
    }

    public List<TrickerAnalysis> selectTickerNotice(String tickerCode) {
        return othersMapper.selectTickerNotice(tickerCode);
    }

    public List<TrickerAnalysis> selectTickerNews(String tickerCode) {
        return othersMapper.selectTickerNews(tickerCode);
    }

    public Map<String,String> getJsonStr(String code) {
        StockDally stock=othersMapper.findByCode(code);
        if(null==stock) return null;
        return ResponseUtil.strToMap(stock.getEvaluation());
    }

    public String selectCodeNum(String code) {
        return othersMapper.selectByCodeName(code);
    }

    public Map<String,String> select2sw_stock_industry(String code) {
        String ticker_name=othersMapper.select2sw_stock_industry(code);
        if(StringUtils.isBlank(ticker_name)) return null;
        Map<String, String> map=new HashMap<String, String>();
        map.put("stock_num", code);
        map.put("stock_name", ticker_name);
        return map;
    }

    public List<StockIndustry> selectAll() {
        return othersMapper.selectAll();
    }

    public void updatePinYin(StockIndustry stock) {
        othersMapper.updatePinYin(stock);
    }

    public List<StockIndustry> findStockNotEnd(String code) {
        return othersMapper.findStockNotEnd(code+"%");
    }

    public String selectContentByLc_announcement(String id) {
        return othersMapper.selectContentByLc_announcement(id);
    }
    public NewsVO selectContentsByLc_announcement(String id) {
        return othersMapper.selectContentsByLc_announcement(id);
    }

    public String selectContentByLc_news(String id) {
        return othersMapper.selectContentByLc_news(id);
    }
    public NewsVO selectTitleContentByLc_news(String id) {
        return othersMapper.selectTitleContentByLc_news(id);
    }
}
