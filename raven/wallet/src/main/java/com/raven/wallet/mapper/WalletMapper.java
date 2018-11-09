package com.raven.wallet.mapper;


import com.raven.wallet.bean.WalletUserFrozenBean;
import com.raven.wallet.dto.QueryDto;
import com.raven.wallet.dto.TaskDto;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
public interface WalletMapper {

    @Transactional
    @Select("select sum(change::INT) from wallet_user_change_log where uid = #{uid}" +
            "and type = #{type}")
    Integer getUserMoneyByUidAndType(TaskDto dto) throws Exception;

    @Insert("insert into wallet_user_change_log (id,uid,serial_num,oid,oid_date,oid_time,change,create_date,create_time,comment,type)" +
            "values(" +
            "default,#{uid},#{serialNum},#{oid},#{date}::date,#{time}::timestamp without time zone,#{change},#{createDate}::date,#{createTime}::timestamp without time zone,#{comment},#{type})")
    void addWalletUserChangeLog(TaskDto dto) throws Exception;

    @Transactional
    @Select("select sum(change::INT) from wallet_user_frozen where uid = #{uid}" +
            "and type = #{type} and frozen_status = 0")
    Integer getUserFrozenMoneyByUidAndType(TaskDto dto) throws Exception;

    @Transactional
    @Insert("insert into wallet_user_frozen (id,uid,serial_num,oid,oid_date,oid_time,change,create_date,create_time,comment,type,frozen_status)" +
            "values(" +
            "default,#{uid},#{serialNum},#{oid},#{date}::date,#{time}::timestamp without time zone,#{change},#{createDate}::date,#{createTime}::timestamp without time zone,#{comment},#{type},0)")
    void addWalletUserFrozen(TaskDto dto) throws Exception;

    @Transactional
    @Update("update wallet_user_frozen set frozen_status = #{param2} where oid=#{param1}")
    int updateWalletUserFrozenStatusByOid(String oid, int frozenStatus) throws Exception;

    @Transactional
    @Select("select uid,change,serial_num as serialNum from wallet_user_frozen where oid = #{param1} and frozen_status = #{param2}")
    WalletUserFrozenBean getWalletUserFrozenByOidAndFrozenStatus(String oid, int frozenStatus) throws Exception;

    @Transactional
    @Select("select uid,change,serial_num as points,create_date as date,create_time as time from wallet_user_change_log " +
            "where uid = #{uid} and comment='1'")
    List<Map<String, Object>> getUserPointsListByUid(@Param("uid") String uid) throws Exception;

    @Transactional
    @Select("select uid,change,create_date as date,create_time as time,comment from wallet_user_change_log " +
            "where uid = #{uid} and type=#{type}")
    List<Map<String, Object>> getUserMoneyListByUidAndType(TaskDto dto) throws Exception;


    @Select("select sum(change::int) from wallet_user_change_log where uid = #{uid} and type = #{type}" +
            "and oid_date between #{startDate}::date and #{endDate}::date")
    Integer getUserMoneyByUidAndTypeAndDateRange(QueryDto dto) throws Exception;
}
