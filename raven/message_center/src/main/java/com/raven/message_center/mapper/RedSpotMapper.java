package com.raven.message_center.mapper;

import com.raven.message_center.bean.RedSpotReceiveBean;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RedSpotMapper {

    @Transactional
    @Select("select count(*) from message_red_spot where uid = #{uid} and isRead = 0")
    int getUserUnreadMsgCount(@Param("uid") String uid) throws Exception;

    @Transactional
    @Insert("INSERT INTO message_red_spot (msgid, uid, type, msg, operation , isread, isdelete,create_time) " +
            "VALUES (#{msgId}, #{uid}, #{type}, #{msg}, #{operation} , 0, 0,#{createTime}::timestamp)")
    void addRedSpotMsg(RedSpotReceiveBean bean) throws Exception;
}
