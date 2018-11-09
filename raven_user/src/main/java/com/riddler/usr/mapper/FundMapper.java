package com.riddler.usr.mapper;

import com.riddler.usr.bean.*;
import feign.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
public interface FundMapper {
    @Transactional
    @Select("select ff.*,round(fo.weight::numeric, 2) weight from fund_information  ff INNER JOIN fof_weights_web fo ON ff.fund_code=fo.code")
    List<FundBaseInfo> selectFundCombine();

    @Transactional
    @Select("select * from fund_history where fund_code=#{fund_code} ORDER BY \"date\" DESC LIMIT 600")
    List<FundHistory> selectFundHisttoryNetValue(@Param("fund_code") String fund_code);

    @Transactional
    @Select("SELECT * from fund_quarter_details where fund_code=#{fund_code} AND position_type=#{position_type} and quarter_type=\n" +
            "(select MAX(quarter_type) from fund_quarter_details where fund_code=#{fund_code} AND position_type=#{position_type})\n" +
            "ORDER BY net_value_proportion DESC LIMIT 10")
    List<FundQuarter> tickerBondPosition(Map<String,String> map);

    @Transactional
    @Select("select * from fund_information where fund_code=#{code}")
    FundBaseInfo selectByFundCode(@Param("code") String code);

    @Transactional
    @Select("select * FROM (select name,round(tv::numeric,3) tv,round(benchmark::numeric,3) benchmark,substr(\"date\"::TEXT,0,20) \"date\" from fof_net_vals ORDER BY \"date\" DESC LIMIT 1000) ff ORDER BY date ASC")
    List<FofNetValue> selectFofNetValue();

    @Transactional
    @Select("select round(p_change::numeric,2) p_change from equd_data_day where ticker =#{tricker_code} ORDER by tradedate desc limit 1")
    String selectP_change(FundQuarter fund);

    @Transactional
    @Select("SELECT tradedate,name,round(mdd::numeric,3) mdd,round(sharp::numeric,3) sharp,\n" +
            "round(rate::numeric,3) rate,round(accrate::numeric,3) accrate,round(mrate::numeric,3) mrate,round(brate::numeric,3) brate \n" +
            "from fof_performance ORDER BY tradedate DESC LIMIT 1")
    FofPerformance selectFofPerformance();

    @Transactional
    @Select("select sigdate::TEXT as date, round(rate::NUMERIC, 3) as rate, round(mktrate::NUMERIC, 3) as mkt from zengqiang_vals order by date")
    List<FofRateMkt> getFofRateMktHis();

}
