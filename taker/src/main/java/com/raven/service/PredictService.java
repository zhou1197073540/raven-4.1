package com.raven.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.raven.mapper.OddsMapper;
import com.raven.mapper.PredictMapper;
import com.raven.utils.RedisUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import com.google.common.collect.Lists;
import com.raven.bean.*;
import com.raven.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.MINUTES;

/**
 * Created by daniel.luo on 2017/6/2.
 */
@Service
public class PredictService {
    @Autowired
    PredictMapper predictMapper;

    @Autowired
    OddsMapper oddsMapper;

    public int getUserUserCountByUid(String uid) throws Exception {
        return predictMapper.getUserCountByUid(uid);
    }

    /**
     * @param status 客户预测的状态（up/down）
     * @param date   预测的哪一天
     * @param type   预测的哪个指数
     * @return 预测时丰源币币数
     */
    public List<String> getAllAmount(String status, String date, String type) throws Exception {
        Map<String, String> mapParam = new HashMap<String, String>();
        mapParam.put("buy_status", status);
        mapParam.put("p_date", date);
        mapParam.put("s_type", type);
        List<String> listRet = oddsMapper.getAllAmountByDay(mapParam);
        return listRet;
    }

    /**
     * @param status 客户预测的状态（up/down）
     * @param type   预测的哪个指数
     * @return 预测积分数
     */
    public List<String> getAllQuarterAmount(String status, String type, String p_pos) throws Exception {
        Map<String, String> mapParam = new HashMap<String, String>();
        mapParam.put("buy_status", status);
        mapParam.put("s_type", type);
        mapParam.put("p_pos", p_pos);
        List<String> listRet = oddsMapper.getAllAmountByQuarter(mapParam);
        return listRet;
    }

    public String getLatestPrice(String s_type) throws Exception {
        return oddsMapper.getLatestIndex(s_type);
    }

    /**
     * @param s_type: 指数类型
     * @return 1:涨 -1:跌 0:不涨不跌 2:出错
     */
    public int judgeWin(String s_type) throws Exception {
        List<String> listRet = oddsMapper.getTwoRecentIndex(s_type);
        if (listRet.size() == 2) {
            double ret = Double.parseDouble(listRet.get(0)) - Double.parseDouble(listRet.get(1));
            if (ret > 0.0) return 1;
            else if (ret < 0.0) return -1;
            else return 0;
        } else {
            return 2;
        }
    }

    public void updateWinOrLose(Map<String, String> mapParam) throws Exception {
        oddsMapper.setWinOrLose(mapParam);
    }

    public void updateQuarterWinOrLose(Map<String, String> mapParam) throws Exception {
        oddsMapper.setQuarterWinOrLose(mapParam);
    }

    public void updateJudgePos(String pos, String status) throws Exception {
        Map<String, String> mapParam = new HashMap<String, String>();
        mapParam.put("pos", pos);
        mapParam.put("status", status);
        oddsMapper.updateJudgePos(mapParam);
    }

    public void updateDraw(Map<String, String> mapParam) throws Exception {
        oddsMapper.setDraw(mapParam);
    }

    public void updateQuarterDraw(Map<String, String> mapParam) throws Exception {
        oddsMapper.setQuarterDraw(mapParam);
    }

    public int addPredictRecord(Map<String, String> mapParam) throws Exception {
        return oddsMapper.addPredict(mapParam);
    }

    public String getLastClose(String s_type) throws Exception {
        String lastClose = "";
        LocalTime now = LocalTime.now();
        switch (s_type) {
            case "上证指数": {
                if (now.isAfter(LocalTime.of(15, 40)) || now.isBefore(LocalTime.of(9, 30))) {
                    lastClose = oddsMapper.getLatestIndex(s_type);
                }
                break;
            }
            case "香港恒生": {
                break;
            }
            case "日经225": {
                break;
            }
            case "美国标普500": {
                break;
            }
        }
        return lastClose;
    }

    public List<String> getTwoRecentClose(String s_type) throws Exception {
        return oddsMapper.getTwoRecentIndex(s_type);
    }

    public List<PredictRecord> getPredictRecord(String user_id) throws Exception {
        return oddsMapper.getRecord(user_id);
    }

    public List<String> getOnePick(String uid) throws Exception {
        return oddsMapper.getOnePick(uid);
    }

    public String getLastPCollection() throws Exception {
        return oddsMapper.getLastPCollection();
    }

    public String getOidByLastCollection(Map<String, String> mapParam) throws Exception {
        return oddsMapper.getOidByLastCollection(mapParam);
    }

    public List<String> getChangeByOid(String oid) throws Exception {
        return oddsMapper.getChangeByOid(oid);
    }

    public void updateCommonRatePm(String date) throws Exception {
        // update common
        String commonP = oddsMapper.getCommonPortfolio();
        String[] commonPArr = commonP.split(",", -1);
        JSONArray joCP = new JSONArray();
        JSONObject resJO = new JSONObject();
        Jedis jedis = RedisUtil.getJedis();
        String str_50 = jedis.get("50_rates");
        if (StringUtils.isBlank(str_50)) return;
        JSONArray ja_50 = JSONArray.parseArray(str_50);
        for (int i = 0; i < commonPArr.length; i++) {
            JSONObject jOne = getOneRate(ja_50, commonPArr[i]);
            joCP.add(jOne);
        }
        System.out.println(JSONObject.toJSONString(joCP));
        jedis.set("common_rates", JSONObject.toJSONString(joCP));
        jedis.close();
    }

    public JSONArray personRateCal(String portfolio) throws Exception {
        String[] personPArr = portfolio.split(",", -1);
        JSONArray joPP = new JSONArray();
        JSONObject resJO = new JSONObject();
        Jedis jedis = RedisUtil.getJedis();
        String str_50 = jedis.get("50_rates");
        if (StringUtils.isBlank(str_50)) return null;
        JSONArray ja_50 = JSONArray.parseArray(str_50);
        for (int i = 0; i < personPArr.length; i++) {
            JSONObject jOne = getOneRate(ja_50, personPArr[i]);
            joPP.add(jOne);
        }
        System.out.println(JSONObject.toJSONString(joPP));
        jedis.close();
        return joPP;
    }

    public JSONObject getOneRate(JSONArray ja, String ticker) throws Exception {
        String code = ticker.replace("\n", "").replace("\r", "");
        JSONObject ret = new JSONObject();
        for (int i = 0; i < ja.size(); i++) {
            if (code.equals(ja.getJSONObject(i).getString("ticker"))) {
                ret = ja.getJSONObject(i);
                break;
            }
        }
        return ret;
    }

    public List<PredictRecord> getQuarterPredictRecord(String user_id) throws Exception {
        return oddsMapper.getQuarterRecord(user_id);
    }

    public int addPortfolio25(Map<String, String> mapParam) throws Exception {
        int flag = oddsMapper.checkPicked(mapParam);
        if (flag == 1) {
            return oddsMapper.updatePortfolio25(mapParam);
        } else if (flag == 0) return oddsMapper.addPortfolio25(mapParam);
        else {
            //TODO 加日志，出现一个用户同一期多个选择组合了，删除多余的
            return -1;
        }
    }

    public List<String> getPickRecord(String uid) throws Exception {
        return oddsMapper.getPickReocrd(uid);
    }

    public String getLastQuarterClose(String ticker) throws Exception {
        return String.valueOf(oddsMapper.getLatestQuarter(ticker));
    }

    public int addQuarterPredictRecord(Map<String, String> mapParam) throws Exception {
        return oddsMapper.addQuarterPredict(mapParam);
    }

    public boolean isExchangeDay(String date) throws Exception {
        int count = oddsMapper.getDateNum(date);
        if (count > 0) return false;
        else return true;
    }

    /**
     * 计算预测点位generatePredictPos
     *
     * @param curTime 时间数组，第0个：小时，1：分钟，2：小时*100+分钟
     * @param date    日期
     * @return
     */
    public String generatePPos(int[] curTime, String date) throws Exception {
        String p_pos = "";
        String dateStr = date;
        if (isExchangeDay(dateStr)) {
            if ((curTime[2] >= 945 && curTime[2] < 1115) ||
                    (curTime[2] >= 1315 && curTime[2] < 1445)) {
                int tempM = curTime[1] + 30 - (curTime[1] % 15);
                if (tempM == 60) {
                    p_pos = dateStr + " " + String.format("%02d", curTime[0] + 1) + ":00:00";
                } else if (tempM == 75) {
                    p_pos = dateStr + " " + String.format("%02d", curTime[0] + 1) + ":15:00";
                } else
                    p_pos = dateStr + " " + String.format("%02d", curTime[0]) + ":" + String.format("%02d", tempM) + ":00";
            } else if (curTime[2] >= 1115 && curTime[2] < 1315) {
                p_pos = dateStr + " " + "13:30:00";
            } else if (curTime[2] < 945) {
                p_pos = dateStr + " " + "10:00:00";
            } else {
                while (true) {
                    dateStr = TimeUtil.nextDate(dateStr);
                    if (isExchangeDay(dateStr)) {
                        p_pos = dateStr + " " + "10:00:00";
                        break;
                    }
                }
            }
        } else {//不在交易日，日期往后推一天，接着判断
            dateStr = TimeUtil.nextDate(dateStr);
            generatePPos(curTime, dateStr);
        }
        return p_pos;
    }

    /**
     * 计算判断输赢的点generateJudgePos
     *
     * @param curTime 时间数组，第0个：小时，1：分钟，2：小时*100+分钟
     * @return
     */
    public String generateJPos(int[] curTime, String dateStr) throws Exception {
        String j_pos = "";
        if ((curTime[2] >= 1000 && curTime[2] < 1145) ||
                (curTime[2] >= 1330 && curTime[2] < 1515)) {
            int tempM = curTime[1] - (curTime[1] % 15);
            j_pos = dateStr + " " + String.format("%02d", curTime[0]) + ":" + String.format("%02d", tempM) + ":00";
        }
        return j_pos;
    }

    public double getQuarterAmount(String status, String type, String p_pos) throws Exception {
        List<String> listRet = getAllQuarterAmount(status, type, p_pos);
        System.out.println("length of " + status + " : " + listRet.size());
        for (String one : listRet) {
            System.out.println(one);
        }
        return listRet.stream()
                .map(x -> Double.parseDouble(x))
                .reduce(0.0, (x, y) -> (x + y));
    }

    public Map<String, String> doCalculateQuarter(String oneType, String p_pos) throws Exception {
        double upAmount = getQuarterAmount("up", oneType, p_pos);
        double downAmount = getQuarterAmount("down", oneType, p_pos);
        double upOdds = (downAmount + 1.0) * 0.95 / (upAmount + 1.0) + 1.0;
        double downOdds = (upAmount + 1.0) * 0.95 / (downAmount + 1.0) + 1.0;
        Map<String, String> map = new HashMap<String, String>();
        map.put("s_type", oneType);
        map.put("buy_up", Double.toString((double) (Math.round(upOdds * 100) / 100.0)));
        map.put("buy_down", Double.toString((double) (Math.round(downOdds * 100) / 100.0)));
        return map;
    }

    public static String getBeforePos(String oriDateTime) throws Exception {
        LocalDateTime fmtTime = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            fmtTime = LocalDateTime.parse(oriDateTime, formatter);
        } catch (DateTimeParseException ex) {
            System.out.printf("%s is not parsable!%n", oriDateTime);
            ex.printStackTrace();
        }
        return fmtTime.minus(15, MINUTES).format(formatter);
    }

    public int[] convertTimeInt(String timeStr) throws Exception {
        String pattern = "(\\d+):(\\d+):\\d+";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(timeStr);
        int hour = 0, minute = 0;
        if (m.groupCount() == 2 && m.find()) {
            hour = Integer.parseInt(m.group(1));
            minute = Integer.parseInt(m.group(2));
        }
        System.out.println("hour : " + hour);
        System.out.println("minute : " + minute);
        int[] timeArray = {hour, minute, hour * 100 + minute};
        return timeArray;
    }

    public List<String> getStatusByJPos(String pos) throws Exception {
        return oddsMapper.getStatusByJPos(pos);
    }

    public boolean checkIndexHisExist(String date, String ticker) throws Exception {
        Map<String, String> mapParam = new HashMap<String, String>();
        mapParam.put("ticker", ticker);
        mapParam.put("date", date);
        int count = oddsMapper.checkIndexHis(mapParam);
        if (count > 0) return true;
        else return false;
    }

//    /**
//     * @param ticker: 指数类型
//     * @return 1:成功，-1:出错
//     */
//    public int judgeQuarterWin(String ticker, String p_pos, String j_before) throws Exception {
//        String s_type = "";
//        if (ticker.equals("000001.SH")) s_type = "上证指数";
////        List<Double> listRet = oddsMapper.getQuarterTwoRecentPrice(ticker);
//        Map<String, String> mapParamJPOS = new HashMap<String, String>();
//        mapParamJPOS.put("ticker", ticker);
//        mapParamJPOS.put("date", p_pos);
//        double close_index = oddsMapper.getQuarterPriceByPos(mapParamJPOS);
//        Map<String, String> mapParamJBefore = new HashMap<String, String>();
//        mapParamJBefore.put("ticker", ticker);
//        mapParamJBefore.put("date", j_before);
//        double last_index = oddsMapper.getQuarterPriceByPos(mapParamJBefore);
//        double ret = close_index - last_index;
//        String result = "";
//        if (ret > 0.0) result = "up";
//        else if (ret < 0.0) result = "down";
//        else result = "draw";
//        Map<String, String> mapParam = new HashMap<String, String>();
//        mapParam.put("result", result);
//        mapParam.put("close_index", String.valueOf(close_index));
//        mapParam.put("last_index", String.valueOf(last_index));
//        mapParam.put("s_type", s_type);
//        mapParam.put("p_pos", p_pos);
//        if ("up".equals(result) || "down".equals(result)) {
//            updateQuarterWinOrLose(mapParam);
//            System.out.println("gengxin!! .result : " + result + " pos : " + p_pos);
//            updateJudgePos(p_pos, "done");
//            // 开始分钱!!!!
//            rewardMoney(s_type, p_pos, result);
//        } else if ("draw".equals(result)) {
//            updateQuarterDraw(mapParam);
//            System.out.println("gengxin!! .result : " + result + " pos : " + p_pos);
//            updateJudgePos(p_pos, "done");
//            rewardEveryone(s_type, p_pos);
//        }
//        return 1;
//    }
    public List<RewardMan> getRewardMen(String oneType, String pos) throws Exception {
        Map<String, String> mapParam = new HashMap<String, String>();
        mapParam.put("s_type", oneType);
        mapParam.put("p_pos", pos);
        return oddsMapper.getRewardMen(mapParam);
    }

    public List<RewardMan> getEveryoneOnePos(String oneType, String pos) throws Exception {
        Map<String, String> mapParam = new HashMap<String, String>();
        mapParam.put("s_type", oneType);
        mapParam.put("p_pos", pos);
        return oddsMapper.getRewardMen(mapParam);
    }

//    public void rewardEveryone(String oneType, String pos) throws Exception {
    // TODO 需要和钱包端调试
//        List<RewardMan> listMen = getEveryoneOnePos(oneType, pos);
//        for (RewardMan oneMan : listMen) {
//            WalletTask task = mq.createTask();
//            task.setUid(oneMan.getUser_id());
//            task.setFyb(oneMan.getAmount());
//            task.setOid(oneMan.getSerial_num());
//            // 0,注册 1,充值, 2,提现, 3,卖出fyb,4买入fyb, 5,赌博花费 6,赌博赚钱, 7,赌博退钱
//            task.setComment("7");
//            task.setStatus(-1);
//            mq.sendTask(task);
//        }
//    }

//    public void rewardMoney(String oneType, String pos, String result) throws Exception {
//        double odds = 0d;
//        Map<String, String> map = doCalculateQuarter(oneType, pos);
//        if ("up".equals(result)) {
//            odds = Double.parseDouble(map.get("buy_up"));
//        } else odds = Double.parseDouble(map.get("buy_down"));
//        List<RewardMan> listMen = getRewardMen(oneType, pos);
//        for (RewardMan oneMan : listMen) {
    // TODO 奖励要被奖励的人，积分
//            WalletTask task = mq.createTask();
//            task.setUid(oneMan.getUser_id());
//            task.setFyb(String.valueOf(odds * Double.parseDouble(oneMan.getAmount())));
//            task.setOid(oneMan.getSerial_num());
//            // 0,注册 1,充值, 2,提现, 3,卖出fyb,4买入fyb, 5,赌博花费 6,赌博赚钱
//            task.setComment("6");
//            task.setStatus(-1);
//            mq.sendTask(task);
//        }
//    }

    public int addNewJPos(String pos) throws Exception {
        return oddsMapper.addNewJPos(pos);
    }

    public PredictVal predictRobotOne() throws Exception {
        return oddsMapper.predictRobotOne();
    }

    /**
     * 获取最大period对应的stock
     *
     * @return
     */
    public String getStocksByMaxPeriod() throws Exception {
        String strStocks = oddsMapper.getStocksByMaxPeriod();
        if (strStocks == null) return null;
        String[] stocks = strStocks.split(",");
        Map<String, Integer> stockCount = new HashMap<>();
        for (int i = 0; i < stocks.length; i++) {
            if (stockCount.containsKey(stocks[i])) {
                stockCount.put(stocks[i], stockCount.get(stocks[i]) + 1);
            } else {
                stockCount.put(stocks[i], 1);
            }
        }

        List<Map.Entry<String, Integer>> list = Lists.newArrayList(stockCount.entrySet());
        list.sort(((o1, o2) -> o2.getValue() - o1.getValue()));
        String retStocks = null;
        if (list.size() >= 25) {
            retStocks = list.subList(0, 25).stream().map(kv -> kv.getKey()).reduce((s1, s2) -> s1 + "," + s2).get();
        } else {
            retStocks = list.stream().map(kv -> kv.getKey()).reduce((s1, s2) -> s1 + "," + s2).get();
        }
        return retStocks;
    }

    public void updatePeriodCursor(PeriodCursorBean bean) throws Exception {
        oddsMapper.updatePeriodCursor(bean);
    }

    public void insertNewPeriodCursor(PeriodCursorBean bean) throws Exception {
        oddsMapper.insertNewPeriodCursor(bean);
    }

    public PeriodCursorBean getPeriodCursorByStatus(String status) throws Exception {
        return oddsMapper.getPeriodCursorByStatus(status);
    }

    public List<StockBean> getPeriodStockPriceByDate(String date) throws Exception {
        List<StockBean> stocks = oddsMapper.getPeriodStockPriceByDate(date);
        return stocks;
    }

    public Map<String, Double> getRate(List<StockBean> startL, List<StockBean> endL) throws Exception {
        Map<String, Double> rates = new HashMap<>();
        startL.stream().forEach(b1 ->
                rates.put(b1.getStock(), endL.stream().filter(b2 -> b1.getStock().equals(b2.getStock())).map(
                        b2 -> (b2.getClosePrice() - b1.getOpenPrice()) / b1.getOpenPrice()
                ).findFirst().get())
        );
        return rates;
    }

    public String getUserName(String uid) throws Exception {
        List<String> listRet = predictMapper.getUserName(uid);
        if (listRet.size() > 0) {
            return listRet.get(0);
        } else return "";
    }

    public PredictRecord getPredictDetail(String serialNum) throws Exception {
        List<PredictRecord> listRet = predictMapper.getOnePredict(serialNum);
        if (listRet.size() > 0) {
            return listRet.get(0);
        } else return null;
    }

    public String getCurPeriod() throws Exception {
        return predictMapper.getCurPeriod();
    }

    public List<OneStockHistory> getOneStockHistory(String ticker) throws Exception {
        return predictMapper.getOneStockHistory(ticker);
    }

    public String  getStockEval(String ticker) throws Exception {
        String like="%"+ticker+"%";
        return predictMapper.getStockEval(ticker,like);
    }

    public Map<String, String> makeEvalData(String content) throws Exception {
        Map<String, String> res = new HashMap<String, String>();
        int posHot = content.indexOf("在所属");
        res.put("hot", content.substring(0, posHot));
        int posIndustry = content.indexOf("公司财务情况");
        res.put("industry", content.substring(posHot, posIndustry));
        int posFlow = content.indexOf("近期资金方面");
        int posModel = content.indexOf("量化模型预测");
        if (content.contains("无分析师预测")) {
            int posTemp = content.indexOf("无分析师预测");
            res.put("basic", content.substring(posIndustry, posTemp));
            res.put("analysis", "无分析师预测");
        } else {
            int posStart = content.indexOf("盈利能力水平");
            int posAim = content.indexOf("分析师目标价");
            for (int i = posAim; i > posStart; i--) {
                if ('.' == content.charAt(i)) {
                    res.put("basic", content.substring(posIndustry, i));
                    res.put("analysis", content.substring(i + 1, posFlow));
                    break;
                }
            }
        }
        res.put("flow", content.substring(posFlow, posModel));
        res.put("level", content.substring(posModel, content.length()));
        return res;
    }

    public List<NetValue> getNetValueHistory() throws Exception {
        return predictMapper.getNetValueHistory();
    }

    public List<FocusRank> getFocusRank() throws Exception {
        return predictMapper.getFocusRank();
    }

    public List<OneThemeStock> getThemeStocks(String theme) throws Exception {
        return predictMapper.getThemeStocks(theme);
    }

    public String getStockNameByTicker(String ticker) throws Exception {
        return predictMapper.getStockNameByTicker(ticker);
    }
}
