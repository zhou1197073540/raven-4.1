package com.raven.message_center.mapper;

import com.raven.message_center.dto.UserMessageDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserMassageMapper {

    @Select("select msgid, type, msg, operation,create_time as createTime, isRead from message_red_spot where uid = #{uid} and isread = #{isRead} and isdelete=0 order by create_time desc")
    List<UserMessageDto> getUserMessagesByUidAndIsRead(@Param("uid") String uid, @Param("isRead") int isRead) throws Exception;

    @Select("select msgid, type, msg, operation,create_time as createTime,isRead from message_red_spot where uid = #{uid} and isdelete=0 order by create_time desc")
    List<UserMessageDto> getUserMessagesByUid(@Param("uid") String uid) throws Exception;

    @Transactional
    @Update("<script>" +
            "update message_red_spot set isRead = 1 where msgId in" +
            "<foreach open='(' separator=',' close=')' collection='msgIds' item='msgId'>" +
            "#{msgId}" +
            "</foreach>" +
            "</script>")
    void updateUserMessagesStatusByMsgId(@Param("msgIds") List<String> msgIds) throws Exception;

    @Select("select portrait_imgs as img from user_images where uid = #{uid}")
    String getUserImageByUid(@Param("uid") String uid) throws Exception;
}
