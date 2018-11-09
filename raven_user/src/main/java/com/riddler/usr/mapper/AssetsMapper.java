package com.riddler.usr.mapper;

import com.riddler.usr.bean.*;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
public interface AssetsMapper {
    @Transactional
    @Select("select rate from rmb_dollar_rate where \"type\"='USD/CNY' ORDER BY \"date\" DESC LIMIT 1")
    String getRate();

    @Transactional
    @Select("select * from assets_info WHERE flag='1'")
    List<Assets> getListAssets();

    @Transactional
    @Select("select fyb,unit_price,date\n" +
            "\t\tfrom user_daily_record\n" +
            "\t\twhere\n" +
            "\t\tuid=#{uid}\n" +
            "\t\tORDER BY date ASC")
    List<DailyRecordVO> selectFybHistory(@Param("uid") String uid);


    @Transactional
    @Select("select rmb,date from wallet_change_log where id IN\n" +
            "\t\t(\n" +
            "\t\tselect MAX(\"id\") id\n" +
            "\t\tfrom\n" +
            "\t\twallet_change_log\n" +
            "\t\twhere\n" +
            "\t\tuid=#{uid} GROUP BY \"date\")")
    List<DailyRecordVO> selectRMBHistory(@Param("uid")String uid);

    @Transactional
    @Select("select\n" +
            "\t\tuid,fyb,date,comment\n" +
            "\t\tfrom\n" +
            "\t\twallet_change_log\n" +
            "\t\twhere id IN\n" +
            "\t\t(\n" +
            "\t\tselect MAX(\"id\")\n" +
            "\t\tid\n" +
            "\t\tfrom\n" +
            "\t\twallet_change_log\n" +
            "\t\tGROUP BY uid)")
    List<DailyRecordVO> selectCurDate();

    @Transactional
    @Insert("insert into\n" +
            "\t\tuser_daily_record\n" +
            "\t\t(\"uid\",\"fyb\",\"unit_price\",\"date\")\n" +
            "\t\tvalues\n" +
            "\t\t(#{uid},#{fyb},#{unit_price},#{date})")
    void insertFybVO(DailyRecordVO vo);

    @Transactional
    @Select("select count(*) from user_daily_record\n" +
            "\t\twhere\n" +
            "\t\tuid=#{uid}\n" +
            "\t\tand\n" +
            "\t\t\"date\"::text=#{date}")
    int findByUidInFybRecord(DailyRecordVO vo);

    @Transactional
    @Select("select all_fyb from total_fyb")
    int getTotalFyb();

    @Transactional
    @Select("resultType=\"Assets\">\n" +
            "\t\tselect code,close lastest_price\n" +
            "\t\tfrom usa_etf_price\n" +
            "\t\twhere\n" +
            "\t\tcode=#{assets_code} ORDER BY date desc limit 2")
    List<Assets> selectUsa_ETF_Price(Assets ass);

    @Transactional
    @Select("SELECT id\n" +
            "\t\tFROM wallet_change_log\n" +
            "\t\tWHERE fyb='0' AND uid=#{uid}\n" +
            "\t\tORDER BY \"id\" DESC LIMIT 1")
    int findFYBZero(@Param("uid") String uid);

    @Transactional
    @Select("SELECT * from\n" +
            "\t\t(SELECT w.id,w.uid,w.fy_change,u.fyb_price as unit_price,w.date,w.comment from\n" +
            "\t\tfyb_history_price u\n" +
            "\t\tJOIN wallet_change_log w on u.date=w.date ) b\n" +
            "\t\twhere\n" +
            "\t\tb.uid=#{uid} and b.comment in ('3','4')")
    List<DailyRecordVO> findAllUserBuySaleRecord(@Param("uid") String uid);

    @Transactional
    @Select("SELECT * from\n" +
            "\t\t(SELECT w.id,w.uid,w.fy_change,u.fyb_price as unit_price,w.date,w.comment from\n" +
            "\t\tfyb_history_price u\n" +
            "\t\tJOIN wallet_change_log w on u.date=w.date ) b\n" +
            "\t\twhere\n" +
            "\t\tb.uid=#{uid} and b.comment in ('3','4') and id <![CDATA[>]]>${id}")
    List<DailyRecordVO> findZeroUserBuySaleRecord(Map<String, Object> map);

    @Transactional
    @Select("SELECT\n" +
            "\t\taa.assets_code,aa.buy_time_money,aa.buy_time,uu.\"close\" as lastest_price\n" +
            "\t\tfrom assets_info aa JOIN usa_etf_price uu on aa.assets_code=uu.code\n" +
            "\t\twhere aa.flag='1' and uu.\"date\"=(SELECT MAX(\"date\") from\n" +
            "\t\tusa_etf_price)")
    List<Assets> getNewestAssets();

    @Transactional
    @Select("select count(*) from fyb_history_price\n" +
            "\t\twhere\n" +
            "\t\tdate=#{date}")
    int selectFybPrice(Map<String, String> map);

    @Transactional
    @Insert("INSERT INTO\n" +
            "\t\tfyb_history_price(fyb_price,date)\n" +
            "\t\tVALUES\n" +
            "\t\t(#{fyb_price},#{date}::date)")
    void insertFybPrice(Map<String, String> map);

    @Transactional
    @Update("UPDATE\n" +
            "\t\tfyb_history_price SET fyb_price=#{fyb_price}\n" +
            "\t\tWHERE\n" +
            "\t\tdate=#{date}")
    void updateFybPrice(Map<String, String> map);

    @Transactional
    @Select("select DISTINCT(\"date\") from wallet_change_log\n" +
            "\t\twhere uid=#{uid}")
    List<String> selectDistDates(@Param("uid") String uid);

    @Transactional
    @Select("select w.uid,w.rmb,w.fyb,w.\"comment\",w.\"time\",f.fyb_price as unit_price,f.\"date\"\n" +
            "\t\tfrom fyb_history_price f JOIN wallet_change_log w on w.\"date\"=f.\"date\"\n" +
            "\t\twhere\n" +
            "\t\tw.uid=#{uid} and w.date=#{date}::date\n" +
            "\t\tORDER BY w.\"time\" desc limit 1")
    DailyRecordVO selectHistoryData(@Param("uid") String uid, @Param("date") String date);

    @Transactional
    @Select("select uid,rmb,fyb,date,comment,(select fyb_price from fyb_history_price ORDER BY date DESC limit 1 ) unit_price \n" +
            "\t\tfrom \n" +
            "\t\twallet_change_log \n" +
            "\t\twhere \n" +
            "\t\tuid=#{_parameter}\n" +
            "\t\tORDER BY date desc limit 1")
    DailyRecordVO selectDefaultDate(@Param("uid") String uid);

    @Transactional
    @Select("select \"close\" from usa_etf_price where code=#{assets_code} and date=#{buy_time}")
    Float selectPriceFromEtfPrice(Assets asset);

    @Transactional
    @Select("select a.assets_code,a.\"flag\",a.buy_time_money,a.buy_time,d.\"date\",d.rate,d.\"close\" from assets_info a JOIN daily_etf_rate d ON a.assets_code=d.code \n" +
            "where  d.\"date\">=#{date} and a.buy_time !='' ORDER BY d.\"date\" DESC")
    List<AssetsRateVO> selectAssetsRateRecords(@Param("date") String date);

    @Transactional
    @Select("select MIN(oid_date) from wallet_user_change_log where uid = #{uid} and \"comment\"='201' GROUP BY uid")
    String selectAssetsPointPutTime(@Param("uid") String uid);

    @Transactional
    @Select("select total_sum_points,\"date\" from wallet_user_daily_settlement where uid=#{uid} ORDER BY \"date\" desc limit 60")
    List<UserAssetsPoints> selectAssetsHistoryRecords(@Param("uid")String uid);

    @Transactional
    @Select("select SUM(add_up) from  daily_etf_rate where \"date\"=(select MAX(\"date\") FROM daily_etf_rate) and code IN(select assets_code from assets_info WHERE FLAG='1')")
    Float selectSumAddUp();

    @Transactional
    @Select("select code,add_up  from  daily_etf_rate where \"date\"=(select MAX(\"date\") FROM daily_etf_rate) and code IN(select assets_code from assets_info WHERE FLAG='1')")
    List<UserAssetsPoints> selectPerEtfTotalSum();

    @Transactional
    @Select("select sum(change::INT) from wallet_user_change_log where uid = #{uid} and type ='2'")
    Integer getCurUserAssetsPoints(@Param("uid") String uid);

    @Transactional
    @Select("select id,uid,code,cost_price,date from user_assets_cost where uid=#{uid} and code=#{code} order by id desc limit 1")
    AssetsCost selectNewsCostByUid(@Param ("uid") String uid, @Param("code") String code);

    @Transactional
    @Select("select \"close\" from usa_etf_price where code=#{code} and date=#{date}")
    String getETFBuyPrice(@Param ("code") String code,@Param("date")String date);

    @Transactional
    @Select("select * from user_assets_cost where uid=#{uid} and code=#{code}")
    AssetsCost selectCostByUidCode(AssetsCost cost);

    @Transactional
    @Insert("insert into user_assets_cost(uid,code,cost_price,date,oid,insert_time) values" +
            "(#{uid},#{code},#{cost_price},#{date},#{oid},#{insert_time})")
    void insertCost(AssetsCost cost);

    @Transactional
    @Update("update user_assets_cost set cost_price=#{cost_price},date=#{date} " +
            " where id=#{id}")
    void updateCost(AssetsCost cost);

    @Transactional
    @Select("SELECT * from user_assets_cost where oid=" +
            "(select oid from user_assets_cost where uid=#{uid} ORDER BY id DESC LIMIT 1)")
    List<AssetsCost> getAssetsCost(@Param("uid") String uid);

    @Transactional
    @Select("select oid_date from wallet_user_change_log " +
            "where uid=#{uid} and type='2' and comment='201' " +
            "ORDER BY id desc limit 1")
    String selectWalletChangeLogByUid(@Param("uid")String uid);

    @Transactional
    @Select("select * from user_assets_cost where uid=#{uid} and code=#{code}")
    AssetsCost selectCostByUidCodes(@Param("uid") String uid, @Param("code")String assets_code);
}
