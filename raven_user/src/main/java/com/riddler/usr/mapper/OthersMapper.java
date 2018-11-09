package com.riddler.usr.mapper;

import com.riddler.usr.bean.*;
import feign.Param;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
public interface OthersMapper {
    @Transactional
    @Select("select concat(ss.secuabbr,'(',tt.ticker,')') ticker,tt.tradedate,concat(round(tt.corrval::numeric, 2),'%') corrval,concat(round(tt.upval::numeric, 2),'%') upval,concat(round(tt.extra5::numeric, 2),'%') extra5,concat(round(tt.turnover::numeric, 2),'%') turnover,round(tt.\"close\"::numeric, 2) \"close\" \n" +
            " from secumain ss INNER JOIN \n" +
            " (select tradedate,ticker,corrval,upval,extra5,turnover,\"close\" from stock_abnorm_day where tradedate=(SELECT MAX(tradedate) FROM stock_abnorm_day) ORDER BY tradedate DESC,upval DESC LIMIT 50) tt \n" +
            " ON ss.secucode=tt.ticker ORDER BY upval DESC")
    List<Ticker> selectRiseStocks();

    @Transactional
    @Select("select concat(ss.secuabbr,'(',tt.ticker,')') ticker,tt.tradedate,concat(round(tt.corrval::numeric, 2),'%') corrval,concat(round(tt.upval::numeric, 2),'%') upval,concat(round(tt.extra5::numeric, 2),'%') extra5,concat(round(tt.turnover::numeric, 2),'%') turnover,round(tt.\"close\"::numeric, 2) \"close\" \n" +
            " from secumain ss INNER JOIN \n" +
            " (select tradedate,ticker,corrval,upval,extra5,turnover,\"close\" from stock_abnorm_day where tradedate=(SELECT MAX(tradedate) FROM stock_abnorm_day) ORDER BY tradedate,upval LIMIT 50) tt \n" +
            " ON ss.secucode=tt.ticker ORDER BY upval DESC")
    List<Ticker> selectFallStocks();

//    @Transactional
//    @Select("SELECT concat(theme,'(',round(rate::numeric, 2),'%)') theme from theme_real_info where ts >\n" +
//            "(select MAX(substr(ts,0,11)) date from theme_real_info)\n" +
//            "ORDER BY rate DESC limit 8")
//    List<String> selectLeadingTheme();

    @Transactional
    @Select("select concat (theme,'(',round(rate :: NUMERIC, 2),'%)') theme from theme_real_info order by ts desc ,rate desc limit 8")
    List<String> selectLeadingTheme();


    @Transactional
    @Select("SELECT * FROM lc_announcement where companycode IN( SELECT companycode from secumain where secucode=#{tickerCode}) ORDER BY infopubldate DESC LIMIT 50")
    List<TrickerAnalysis> selectTickerNotice(@Param("tickerCode") String tickerCode);

    @Transactional
    @Select("SELECT * FROM lc_news where ID IN\n" +
            "(SELECT ID from lc_news_se where code IN\n" +
            "(SELECT  innercode from secumain where secucode=#{tickerCode})) ORDER BY infopubldate DESC LIMIT 100")
    List<TrickerAnalysis> selectTickerNews(@Param("tickerCode") String tickerCode);

    @Transactional
    @Select("SELECT *  FROM  stock_daily_eval WHERE \"ticker\"=#{code} ORDER BY \"date\" DESC LIMIT 1")
    StockDally findByCode(@Param("code") String code);

    @Transactional
    @Select("select ticker from SW_STOCK_INDUSTRY where \"stockname\"=#{CNcode}")
    String selectByCodeName(@Param("CNcode") String CNcode);

    @Transactional
    @Select("select stockname from SW_STOCK_INDUSTRY where \"ticker\"=#{code}")
    String select2sw_stock_industry(@Param("code") String code);

    @Transactional
    @Select("select * from SW_STOCK_INDUSTRY ")
    List<StockIndustry> selectAll();

    @Transactional
    @Update("update SW_STOCK_INDUSTRY set pinyin_firstword=#{pinyin_firstword} where ticker=#{ticker}")
    void updatePinYin(StockIndustry stock);

    @Transactional
    @Select("select ticker,stockname,ticker_type code from sw_stock_industry where pinyin_firstword like #{code} or stockname LIKE #{code} or ticker LIKE #{code} limit 10")
    List<StockIndustry> findStockNotEnd(@Param("code")String code);

    @Transactional
    @Select("SELECT \"content\" FROM lc_announcement where id=#{id}")
    String selectContentByLc_announcement(@Param("id")String id);

    @Select("SELECT infopubldate publish_time,infotitle title,\"content\" FROM lc_announcement where id=#{id}")
    NewsVO selectContentsByLc_announcement(@Param("id")String id);

    @Transactional
    @Select("SELECT \"content\" FROM lc_news where id=#{id}")
    String selectContentByLc_news(@Param("id")String id);

    @Select("SELECT infopubldate publish_time,infotitle title,\"content\" FROM lc_news where id=#{id}")
    NewsVO selectTitleContentByLc_news(@Param("id")String id);
}
