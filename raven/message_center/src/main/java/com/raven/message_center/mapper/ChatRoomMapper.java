package com.raven.message_center.mapper;

import com.raven.message_center.bean.ChatRoomBean;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ChatRoomMapper {

    @Transactional
    @Select("SELECT * FROM (SELECT mum.index_type AS indexType, " +
            "ui.username AS nickName, " +
            "mum.rise_fall AS riseFall, mum.star ,mum.reason, mum.comment, " +
            "mum.create_time AS createTime,img.portrait_imgs as image " +
            "FROM user_info ui " +
            "JOIN message_chat_room mum ON mum.uid = ui.uid " +
            "JOIN user_images img ON img.uid = ui.uid " +
            "ORDER BY createTime DESC LIMIT 10 ) t " +
            "ORDER BY createTime ASC")
    List<ChatRoomBean> getRecentMessages() throws Exception;

    @Transactional
    @Insert("insert into message_chat_room(" +
            "uid,index_type,rise_fall,star,reason,comment,create_time" +
            ") values( " +
            "#{uid}, #{indexType}, #{riseFall}::INT, " +
            "#{star}::numeric(2,1), #{reason}::INT, #{comment}, " +
            "#{createTime}::TIMESTAMP WITHOUT time ZONE)")
    void createNewMsg(ChatRoomBean bean) throws Exception;
}
