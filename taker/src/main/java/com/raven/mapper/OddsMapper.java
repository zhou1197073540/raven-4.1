package com.raven.mapper;

import com.raven.bean.*;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by daniel.luo on 2017/9/8.
 */
@Repository
public interface OddsMapper {
    @Transactional
    @Select("SELECT amount FROM \"STOCK_PREDICT_BEHAVIOR\" " +
            "WHERE buy_status=#{buy_status} and p_date::text=#{p_date} and s_type=#{s_type}")
    List<String> getAllAmountByDay(Map<String, String> mapParam);

    @Transactional
    @Select("SELECT amount FROM \"stock_quarter_predict_behavior\" " +
            "WHERE buy_status=#{buy_status} AND p_pos=#{p_pos} AND s_type=#{s_type}")
    List<String> getAllAmountByQuarter(Map<String, String> mapParam);

    @Transactional
    @Select("SELECT \"s_close\" FROM \"STOCK_DAILY_INDEX\" " +
            "WHERE \"s_type\"=#{s_type} ORDER BY \"s_date\" DESC LIMIT 2")
    List<String> getTwoRecentIndex(String s_type);

    @Transactional
    @Select("SELECT \"close\" FROM \"index_history_data\" WHERE \"ticker\"=#{ticker} ORDER BY \"date\" DESC LIMIT 2")
    List<Double> getQuarterTwoRecentPrice(String ticker);

    @Transactional
    @Update("UPDATE stock_predict_behavior\n" +
            "        set order_status =\n" +
            "            CASE buy_status WHEN #{result} THEN 'win'\n" +
            "            ELSE 'lose'\n" +
            "            END, close_index=#{close_index}, last_index=#{last_index}\n" +
            "        WHERE s_type=#{s_type}")
    int setWinOrLose(Map<String, String> mapParam);

    @Transactional
    @Update("UPDATE \"stock_quarter_predict_behavior\"\n" +
            "        set \"order_status\" =\n" +
            "        CASE \"buy_status\" WHEN #{result} THEN 'win'\n" +
            "        ELSE 'lose'\n" +
            "        END, \"close_index\"=#{close_index}, \"last_index\"=#{last_index}\n" +
            "        WHERE \"s_type\"=#{s_type} AND \"p_pos\"=#{p_pos}")
    int setQuarterWinOrLose(Map<String, String> mapParam);

    @Transactional
    @Update("UPDATE \"stock_quarter_predict_judge_pos\"\n" +
            "        SET \"status\" = #{status}\n" +
            "        WHERE \"pos\" = #{pos}")
    int updateJudgePos(Map<String, String> mapParam);

    @Transactional
    @Update("UPDATE stock_predict_behavior\n" +
            "        set order_status =#{result}, close_index=#{close_index}, last_index=#{last_index}\n" +
            "        WHERE s_type=#{s_type}")
    int setDraw(Map<String, String> mapParam);

    @Transactional
    @Update("UPDATE \"stock_quarter_predict_behavior\"\n" +
            "        set \"order_status\" =#{result}, \"close_index\"=#{close_index}, \"last_index\"=#{last_index}\n" +
            "        WHERE \"s_type\"=#{s_type} AND \"p_pos\"=#{p_pos}")
    public int setQuarterDraw(Map<String, String> mapParam);

    @Transactional
    @Select("SELECT s_close\n" +
            "        FROM stock_daily_index\n" +
            "        WHERE s_type=#{s_type}\n" +
            "        ORDER BY s_date DESC LIMIT 1")
    public String getLatestIndex(String s_type);

    @Transactional
    @Insert("INSERT INTO \"stock_predict_behavior\" (\n" +
            "              \"user_id\",\n" +
            "              \"serial_num\",\n" +
            "              \"buy_status\",\n" +
            "              \"amount\",\n" +
            "              \"action_time\",\n" +
            "              \"p_date\",\n" +
            "              \"s_type\",\n" +
            "              \"last_index\",\n" +
            "              \"order_status\"\n" +
            "          )\n" +
            "          VALUES (\n" +
            "            #{user_id},\n" +
            "            #{serial_num},\n" +
            "            #{buy_status},\n" +
            "            #{amount}::DECIMAL,\n" +
            "            #{action_time}::timestamp,\n" +
            "            #{p_date},\n" +
            "            #{s_type},\n" +
            "            #{last_index},\n" +
            "            #{order_status}\n" +
            "\t      )")
    int addPredict(Map<String, String> mapParam);

    @Transactional
    @Select("SELECT\n" +
            "          \"buy_status\",\n" +
            "          \"amount\",\n" +
            "          \"action_time\",\n" +
            "          \"s_type\",\n" +
            "          \"last_index\",\n" +
            "          \"close_index\",\n" +
            "          \"order_status\"\n" +
            "      FROM \"stock_predict_behavior\"\n" +
            "      WHERE user_id=#{user_id} ORDER BY \"action_time\" DESC LIMIT 20")
    List<PredictRecord> getRecord(String user_id);

    @Transactional
    @Select("select \"portfolio\" from \"portfolio_collection\" where \"user_id\" = #{uid} and \"period\" = " +
            "(select \"period\" from \"portfolio_period_cursor\" where \"status\" = '1')")
    List<String> getOnePick(String uid);

    @Transactional
    @Select("select max(period) from \"portfolio_period_cursor\" where \"status\" = '2'")
    String getLastPCollection();

    @Transactional
    @Select("select \"serial_num\" from \"portfolio_collection\" where \"period\" = #{period} and \"user_id\" = #{user_id}")
    String getOidByLastCollection(Map<String, String> mapParam);

    @Transactional
    @Select("select change from \"wallet_user_change_log\" where \"comment\" = '13' and \"oid\" = #{oid}")
    List<String> getChangeByOid(String oid);

    @Transactional
    @Select("select \"portfolio\" from \"portfolio_period_cursor\" where \"status\" = '1'")
    String getCommonPortfolio();

    @Transactional
    @Select("SELECT \"close\"\n" +
            "        FROM \"index_history_data\"\n" +
            "        WHERE \"ticker\" = #{ticker}\n" +
            "        ORDER BY \"date\" DESC LIMIT 1")
    Double getLatestQuarter(String ticker);

    @Transactional
    @Insert("INSERT INTO \"stock_quarter_predict_behavior\" (\n" +
            "        \"user_id\",\n" +
            "        \"serial_num\",\n" +
            "        \"buy_status\",\n" +
            "        \"amount\",\n" +
            "        \"action_time\",\n" +
            "        \"p_pos\",\n" +
            "        \"s_type\",\n" +
            "        \"order_status\"\n" +
            "        )\n" +
            "        VALUES (\n" +
            "        #{user_id},\n" +
            "        #{serial_num},\n" +
            "        #{buy_status},\n" +
            "        #{amount}::DECIMAL,\n" +
            "        #{action_time}::timestamp,\n" +
            "        #{p_pos},\n" +
            "        #{s_type},\n" +
            "        #{order_status}\n" +
            "        )")
    int addQuarterPredict(Map<String, String> mapParam);

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
            "        WHERE user_id=#{user_id} ORDER BY \"action_time\" DESC LIMIT 20")
    List<PredictRecord> getQuarterRecord(String user_id);

    @Transactional
    @Insert("INSERT INTO \"portfolio_collection\" (\n" +
            "        \"user_id\",\n" +
            "        \"serial_num\",\n" +
            "        \"action_time\",\n" +
            "        \"portfolio\",\n" +
            "        \"period\"\n" +
            "        )\n" +
            "        VALUES (\n" +
            "        #{user_id},\n" +
            "        #{serial_num},\n" +
            "        #{action_time}::timestamp,\n" +
            "        #{portfolio},\n" +
            "        #{period}\n" +
            "        )")
    int addPortfolio25(Map<String, String> mapParam);

    @Transactional
    @Select("SELECT \"portfolio\" from \"portfolio_collection\" WHERE \"user_id\" = #{uid}")
    List<String> getPickReocrd(String uid);

    @Transactional
    @Select("SELECT COUNT(*)\n" +
            "        FROM \"portfolio_collection\"\n" +
            "        WHERE \"user_id\" = #{user_id} AND \"period\" = #{period}")
    int checkPicked(Map<String, String> mapParam);

    @Transactional
    @Update("UPDATE \"portfolio_collection\"\n" +
            "        SET \"portfolio\" = #{portfolio}\n" +
            "        WHERE \"user_id\" = #{user_id} AND \"period\" = #{period}")
    public int updatePortfolio25(Map<String, String> mapParam);

    @Transactional
    @Select("SELECT COUNT(*)\n" +
            "        FROM \"no_exchange_day\"\n" +
            "        WHERE \"date\" = #{date}")
    int getDateNum(String date);

    @Transactional
    @Select("SELECT \"status\"\n" +
            "        FROM \"stock_quarter_predict_judge_pos\"\n" +
            "        WHERE \"pos\" = #{pos}")
    List<String> getStatusByJPos(String pos);

    @Transactional
    @Select("SELECT COUNT(*)\n" +
            "        FROM \"index_history_data\"\n" +
            "        WHERE \"date\" = #{date} AND \"ticker\" = #{ticker}")
    int checkIndexHis(Map<String, String> mapParam);

    @Transactional
    @Insert("INSERT INTO\n" +
            "        \"stock_quarter_predict_judge_pos\"\n" +
            "        VALUES (DEFAULT, #{pos}, 'wait_price')")
    int addNewJPos(String pos);

    @Transactional
    @Select("SELECT \"close\"\n" +
            "        FROM \"index_history_data\"\n" +
            "        WHERE \"date\" = #{date} AND \"ticker\" = #{ticker} LIMIT 1")
    Double getQuarterPriceByPos(Map<String, String> mapParam);

    @Transactional
    @Select("SELECT datetime,dnprob,upprob,predindex \n" +
            "\t\tFROM \n" +
            "\t\tindex_predict_val \n" +
            "\t\tORDER BY datetime DESC LIMIT 1")
    PredictVal predictRobotOne();

    @Transactional
    @Select("SELECT string_agg(portfolio,',')\n" +
            "        FROM\n" +
            "        portfolio_collection\n" +
            "        WHERE period =\n" +
            "        (\n" +
            "        select max(period) from portfolio_period_cursor\n" +
            "        where status = '1'\n" +
            "        )")
    String getStocksByMaxPeriod();

    @Transactional
    @Update("UPDATE\n" +
            "        portfolio_period_cursor\n" +
            "        SET\n" +
            "        portfolio = #{portfolio},\n" +
            "        status = '1'\n" +
            "        where status = '0'")
    int updatePeriodCursor(PeriodCursorBean bean) throws Exception;

    @Transactional
    @Insert("INSERT INTO\n" +
            "      portfolio_period_cursor\n" +
            "      (id,period,start_date,end_date,status)\n" +
            "      VALUES\n" +
            "      (\n" +
            "      DEFAULT,\n" +
            "      (select lpad(trim(to_char(to_number(case when max(period) is null then '000000' else max(period) end,'999999') + 1,'999999')),6,'0') from portfolio_period_cursor),\n" +
            "      #{startDate}::DATE,\n" +
            "      #{endDate}::DATE,\n" +
            "      '0'\n" +
            "      )")
    int insertNewPeriodCursor(PeriodCursorBean bean) throws Exception;

    @Transactional
    @Select("SELECT\n" +
            "            ppc.stck AS stock,\n" +
            "            shd.open As openPrice,\n" +
            "            shd.close AS closePrice,\n" +
            "            shd.date As date\n" +
            "        FROM (\n" +
            "            SELECT\n" +
            "                regexp_split_to_table(ppc.portfolio, ',') stck\n" +
            "            FROM\n" +
            "                portfolio_period_cursor ppc\n" +
            "            WHERE\n" +
            "                ppc.status = '1'\n" +
            "        ) as ppc\n" +
            "        JOIN stock_history_data_500 shd\n" +
            "        ON shd.code = ppc.stck\n" +
            "        AND shd.date = #{_parameter}\n" +
            "        group by stock,openPrice,closePrice,date")
    List<StockBean> getPeriodStockPriceByDate(String date) throws Exception;

    @Transactional
    @Select("SELECT\n" +
            "\t\t  period,start_date as startDate,end_date as endDate,portfolio as portfolio\n" +
            "\t\tFROM\n" +
            "\t\t  portfolio_period_cursor\n" +
            "\t\tWHERE\n" +
            "\t\t  status = #{_parameter}")
    PeriodCursorBean getPeriodCursorByStatus(String status) throws Exception;

    @Transactional
    @Select("SELECT \"user_id\", \"serial_num\", \"amount\"\n" +
            "        FROM \"stock_quarter_predict_behavior\"\n" +
            "        WHERE \"s_type\" = #{s_type} AND \"p_pos\" = #{p_pos} AND \"order_status\" = 'win'")
    List<RewardMan> getRewardMen(Map<String, String> mapParam);

    @Transactional
    @Select("SELECT \"user_id\", \"serial_num\", \"amount\"\n" +
            "        FROM \"stock_quarter_predict_behavior\"\n" +
            "        WHERE \"s_type\" = #{s_type} AND \"p_pos\" = #{p_pos}")
    List<RewardMan> getRewardEveryone(Map<String, String> mapParam);
}
