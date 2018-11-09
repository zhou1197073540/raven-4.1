package com.raven.mapper;


import com.raven.bean.*;
import com.raven.dto.TaskDto;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
public interface PredictMapper {
    @Transactional
    @Select("select count(*) from \"user_info\" where \"uid\" = #{uid}")
    int getUserCountByUid(@Param("uid") String uid) throws Exception;

    @Transactional
    @Insert("INSERT INTO \"STOCK_DAILY_INDEX\"" +
            "(s_open, s_high, s_low, s_close, s_date, s_type) " +
            "VALUES (#{open}, #{high}, #{low}, #{close}, #{date}, #{s_type})")
    int saveDailyValue(StockDailyValue value);

    @Transactional
    @Select("SELECT max(s_date) FROM \"STOCK_DAILY_INDEX\" WHERE s_type=#{type}")
    String getMaxDate(@Param("type") String type);

    @Transactional
    @Select("SELECT COUNT(*) FROM \"STOCK_DAILY_INDEX\" WHERE \"s_date\"::text=#{s_date} and \"s_type\"=#{s_type}")
    int checkExist(Map<String, String> mapParam);

    @Transactional
    @Select("SELECT " +
            "\"date\", " +
            "\"open\", " +
            "\"close\", " +
            "\"high\", " +
            "\"low\" FROM \"index_history_data\" " +
            "WHERE \"ticker\" = #{ticker} ORDER BY \"date\" DESC LIMIT 180")
    List<IndexValue> get180Index(@Param("ticker") String ticker);

    @Transactional
    @Select("select rate as close,date from RMB_DOLLAR_RATE where \"type\"=#{code} ORDER BY \"date\" DESC LIMIT 180")
    List<IndexValue> get180IndexHistoryOfForeignCoin(@Param("code") String code);

    @Transactional
    @Select("SELECT " +
            "\"ticker\", " +
            "\"industryID\", " +
            "\"stockName\", " +
            "\"close\", " +
            "\"pb\", " +
            "\"pe\" " +
            "FROM \"portfolio_candidates_500\"")
    List<OneStockInfo> get50Ticker();

    @Transactional
    @Select("SELECT " +
            "\"tradedate\" as \"date\", " +
            "\"open\", " +
            "\"close\", " +
            "\"high\", " +
            "\"low\" " +
            "FROM \"equd_data_day\" WHERE \"ticker\" = #{code} AND \"tradedate\" >= #{date}")
    List<OneStockHistory> get180Stock(Map<String, String> mapParam);

    @Transactional
    @Select("SELECT \"username\" from \"user_info\" WHERE \"uid\" = #{uid}")
    List<String> getUserName(String uid);

    @Transactional
    @Select("SELECT\n" +
            "        \"buy_status\",\n" +
            "        \"amount\",\n" +
            "        \"action_time\",\n" +
            "        \"s_type\",\n" +
            "        \"last_index\",\n" +
            "        \"close_index\",\n" +
            "        \"order_status\"\n" +
            "        FROM \"stock_quarter_predict_behavior\"\n" +
            "        WHERE \"serial_num\" = #{serial_num}")
    List<PredictRecord> getOnePredict(String serial_num);

    @Transactional
    @Select("SELECT MAX(period) from \"portfolio_period_cursor\"")
    String getCurPeriod();

    @Transactional
    @Select("SELECT \"tradedate\" as \"date\",\"open\",\"close\",\"high\",\"low\",\"volume\",\"p_change\" as \"rate\" FROM \"equd_data_day\"\n" +
            "WHERE \"ticker\" = #{ticker} ORDER BY \"tradedate\" DESC LIMIT 100")
    List<OneStockHistory> getOneStockHistory(@Param("ticker") String ticker);

    @Transactional
    @Select("select \"evaluation\" from \"stock_daily_eval\" where \"ticker\" = #{ticker} or evaluation LIKE #{like} order by \"date\" desc limit 1")
    String getStockEval(@Param("ticker") String ticker,@Param("like") String like);

    @Transactional
    @Select("select benchmark, portfolio_netvalue as netvalue, date from history_netvalue where strategy = 'strat1' order by date desc limit 1000")
    List<NetValue> getNetValueHistory();

    @Transactional
    @Select("select ticker, stock_name, rank, focus_count, trend from xueqiu_focus_rank where date = (select max(date) from xueqiu_focus_rank)")
    List<FocusRank> getFocusRank();

    @Transactional
    @Select("select ticker, stock_name as name from stock_theme_related where theme = #{theme} order by replace(percent, '%', '')::INT desc limit 5")
    List<OneThemeStock> getThemeStocks(@Param("theme") String theme);

    @Transactional
    @Select("select stockname from sw_stock_industry where ticker = #{ticker} limit 1")
    String getStockNameByTicker(@Param("ticker") String ticker);
}
