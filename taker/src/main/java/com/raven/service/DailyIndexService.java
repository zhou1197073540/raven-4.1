package com.raven.service;

import com.alibaba.fastjson.JSONObject;
import com.raven.bean.*;
import com.raven.mapper.PredictMapper;
import com.raven.utils.TimeUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by daniel.luo on 2017/5/25.
 */
@Service
public class DailyIndexService {
    @Autowired
    PredictMapper predictMapper;

    public StockDailyValue setValues(StockDailyValue value, List<String> listRet, String typeName) {
        LocalDate dateStr = TimeUtil.convertDateTime((String) listRet.get(0));
        if (dateStr == null) {
            return null;
        } else {
            Date date = Date.valueOf(dateStr);
            if (listRet.size() < 4) {
                return null;
            } else {
                value.setOpen(listRet.get(1).toString().replace(",", ""));
                value.setHigh(listRet.get(2).toString().replace(",", ""));
                value.setLow(listRet.get(3).toString().replace(",", ""));
                value.setClose(listRet.get(4).toString().replace(",", ""));
                value.setDate(date);
                value.setS_type(typeName);
                return value;
            }
        }
    }

    public int saveIndex(List<String> listRet, String typeName) {
        StockDailyValue value = setValues(new StockDailyValue(), listRet, typeName);
        int ret;
        if (value == null) {
            // 暂时先不做记录处理
            return 0;
        }
        try {
            ret = predictMapper.saveDailyValue(value);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return ret;
    }

    public String getMaxDate(String type) {
        String dateMax = "";
        try {
            dateMax = predictMapper.getMaxDate(type);
            System.out.println("dateMax : " + dateMax);
        } catch (Exception jle) {
            jle.printStackTrace();
        }
        return dateMax;
    }

    public boolean isHave(String date, String type) {
        Map<String, String> mapParam = new HashMap<String, String>();
        mapParam.put("s_date", date);
        mapParam.put("s_type", type);
        int count = 0;
        try {
            count = predictMapper.checkExist(mapParam);
        } catch (Exception jle) {
            jle.printStackTrace();
        }
        return count == 0 ? false : true;
    }

    public List<IndexValue> get180IndexHistory(String ticker) {
        return predictMapper.get180Index(ticker);
    }

    /**
     * 外汇数据
     */
    public List<IndexValue> get180IndexHistoryOfForeignCoin(String code) {
        return predictMapper.get180IndexHistoryOfForeignCoin(code);
    }

    public List<OneStockInfo> get50Ticker() {
        return predictMapper.get50Ticker();
    }

    public List<OneStockHistory> get180StockHistory(Map<String, String> mapParam) {
        return predictMapper.get180Stock(mapParam);
    }

    public JSONObject getStock180() {
        String date = TimeUtil.getDate(-180);
        System.out.println(date);
        List<OneStockModel> listModel = new ArrayList<OneStockModel>();
        List<OneStockInfo> stocks = get50Ticker();
        stocks.stream().forEach(x -> {
            x.setPb(String.format("%.2f", Double.parseDouble(x.getPb())));
            x.setPe(String.format("%.2f", Double.parseDouble(x.getPe())));
        });
        for (OneStockInfo one : stocks) {
            OneStockModel oneStockModel = new OneStockModel();
            oneStockModel.setStockName(one.getStockName());
            oneStockModel.setIndustryID(one.getIndustryID());
            oneStockModel.setCode(one.getTicker());
            oneStockModel.setClose(String.valueOf(one.getClose()));
            oneStockModel.setPb(one.getPb());
            oneStockModel.setPe(one.getPe());
            Map<String, String> mapParam = new HashMap<String, String>();
            mapParam.put("code", one.getTicker());
            mapParam.put("date", date);
            System.out.println("data : " + mapParam.get("date"));
            System.out.println("code : " + mapParam.get("code"));
            List<OneStockHistory> listHis = get180StockHistory(mapParam);
            oneStockModel.setHistory(listHis);
            listModel.add(oneStockModel);
        }
        JSONObject jo = new JSONObject();
        jo.put("stock_list", listModel);
        return jo;
    }

    public JSONObject getIndexHistoryData(String code) {
        List<IndexValue> listRet = null;
        if (checkCodeIsForeignCoin(code)) {//判断是不是外汇
            listRet = get180IndexHistoryOfForeignCoin(code);
        } else {
            listRet = get180IndexHistory(code);
        }
        JSONObject resJo = new JSONObject();
        resJo.put("index_list", listRet);
        return resJo;
    }

    /**
     * 判断是不是外汇
     */
    public boolean checkCodeIsForeignCoin(String code) {
        if (StringUtils.isEmpty(code)) return false;
        if (code.contains("/") || code.contains("int_hangseng") || code.contains("int_nikkei")
                || code.contains("int_nasdaq") || code.contains("sh000001")) {
            return true;
        }
        return false;
    }
}
