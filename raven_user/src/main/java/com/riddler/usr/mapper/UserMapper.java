package com.riddler.usr.mapper;

import com.riddler.usr.bean.*;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
public interface UserMapper {
    @Transactional
    @Select("select a.*,b.* from user_info a LEFT JOIN user_images b on a.uid=b.uid where a.uid=#{uid}")
    User findByUserId(@Param("uid") String uid);

    @Transactional
    @Update("update user_info set\n" +
            "\t\tname=#{name},username=#{username},birth=#{birth},sex=#{sex},\n" +
            "\t\twork=#{work},phone_num=#{phone_num},\n" +
            "\t\tmail=#{mail},area=#{area}\n" +
            "\t\twhere\n" +
            "\t\tuid=#{uid}")
    int updateUserInfo(User user);

    @Transactional
    @Select("SELECT \"uid\",username \n" +
            "\t\tFROM user_info\n" +
            "\t\twhere \"phone_num\"=#{username} AND \"password\"=#{password}")
    List<User> loginVerifyNP(Map<String, String> mapParam);

    @Transactional
    @Select("SELECT count(*)\n" +
            "\t\tFROM user_info\n" +
            "\t\twhere \"phone_num\"=#{phone_num}")
    int isExistPhone(Map<String, String> mapParam);

    @Transactional
    @Insert("INSERT INTO user_info\n" +
            "\t\t(phone_num,password,uid,username)\n" +
            "\t\tvalues\n" +
            "\t\t(#{phone_num},#{password},#{uid},#{username})")
    int registerPhone(Map<String, String> mapParam);

    @Transactional
    @Insert("INSERT INTO deposit_withdraw_behavior\n" +
            "\t\t(user_id,identify_name,serial_num,phone_num,\n" +
            "\t\tverify_code,rmb_change,type,create_time,status,bank_account)\n" +
            "\t\tvalues\n" +
            "\t\t(#{user_id},#{identify_name},#{serial_num},#{phone_num},\n" +
            "\t\t#{verify_code},#{rmb_change},#{type},#{create_time}::timestamp,\n" +
            "\t\t'wait_confirm',#{bank_account})")
    int insertRecharge(DepositModel deposit);

    @Transactional
    @Select("SELECT u.uid,u.\"name\",u.phone_num,w.\"password\" wallet_pwd,w.rmb \n" +
            "\t\tFROM user_info u \n" +
            "\t\tJOIN wallet w on u.uid=w.uid \n" +
            "\t\tWHERE u.uid=#{uid}\n" +
            "\t\tLIMIT 1")
    UserWalletVO selectUserWalletVoByUid(@Param("uid") String uid);

    @Transactional
    @Select("select * from wallet where uid=#{uid}")
    Wallet findMoney(@Param("uid") String uid);

    @Transactional
    @Select("select count(*) from wallet_user_change_log where uid=#{uid}  and create_date=#{date}::date and \"comment\"=#{comment}")
    int checkUsrIsFirstLogin(@Param("uid") String uid, @Param("date") String date, @Param("comment") String login);

    @Transactional
    @Insert("INSERT INTO user_adivse(user_name,phone_number,content,mail,image_url,contact_time) VALUES(#{user_name},#{phone_number},#{content},#{mail},#{image_url},#{contact_time})")
    boolean saveUsrAdvise(UsrAdvise usrAdvise);

    @Transactional
    @Select("select count(*) from wallet_user_change_log where uid=#{uid}  and create_date=#{date}::date and \"comment\"=#{comment}")
    int checkUsrIsFirstSign(@Param("uid")String uid,@Param("date")String date,@Param("comment")String sign);

    @Transactional
    @Update("update user_info set password=#{password} where phone_num=#{phone_num}")
    int updateUsrPassword(@Param("phone_num") String phone_num, @Param("password") String password);

    @Select("SELECT count(*) " +
            "FROM \"user_risk_questionary\" " +
            "WHERE \"user_id\"=#{user_id}")
    int selectRiskLevelCount(String user_id);

    @Transactional
    @Insert("INSERT INTO \"user_risk_questionary\" (" +
            "\"user_id\", " +
            "\"answer\", " +
            "\"create_time\", " +
            "\"score\", " +
            "\"status\") " +
            "VALUES (" +
            "#{user_id}, " +
            "#{answer}, " +
            "#{create_time}::timestamp, " +
            "NULL, " +
            "NULL)")
    int addRiskAnswer(Map<String, String> mapParam);

    @Transactional
    @Update("UPDATE \"user_info\" " +
            "SET \"name\" = #{username}, " +
            "\"identity_id\" = #{identity_id} " +
            "WHERE \"uid\" = #{uid}")
    int updateNameIdCard(Map<String, String> mapParam);

    @Transactional
    @Update("UPDATE \"user_info\" " +
            "SET \"bank_account\" = #{bank_account} " +
            "WHERE \"uid\" = #{uid}")
    int updateBandCard(Map<String, String> mapParam);

    @Transactional
    @Select("select name, identity_id, bank_account from \"user_info\" where uid = #{uid}")
    List<SafeVerify> getOneSafeInfo(String uid);

    @Transactional
    @Select("select count(*) from user_info where phone_num=#{phone_num}")
    int checkPhoneIsExist(@Param("phone_num") String phone_num);

    @Transactional
    @Select("select change,create_time,\"comment\" from wallet_user_change_log \n" +
            "where \"uid\"=#{uid} and \"comment\" in (${comment}) \n" +
            "ORDER BY create_time desc limit 20")
    List<AccountRecords> getPoint_records_by_comment(@Param("uid")String uid,@Param("comment") String comment);

    @Transactional
    @Select("select change,create_time,\"comment\" from wallet_user_change_log \n" +
            "where \"uid\"=#{uid} and \"comment\" in (${comment}) and oid_date::TEXT>=#{start_date} and oid_date::TEXT<=#{end_date} \n" +
            "ORDER BY create_time desc limit 100")
    List<AccountRecords> getPoint_records_by_comment_date(@Param("uid")String uid,@Param("comment") String comment,@Param("start_date")String start_date,@Param("end_date")String end_date);

    @Transactional
    @Select("select change,create_time,\"comment\" from wallet_user_change_log where \"uid\"=#{uid} and type=1 ORDER BY create_time desc limit 100")
    List<AccountRecords> getPoint_all_records(@Param("uid")String uid);

    @Transactional
    @Select("select change,create_time,\"comment\" from wallet_user_change_log where" +
            " \"uid\"=#{uid} and type=1 and oid_date::TEXT>=#{start_date} and oid_date::TEXT<=#{end_date} " +
            "ORDER BY create_time desc limit 100")
    List<AccountRecords> getPoint_all_records_date(@Param("uid")String uid,@Param("start_date")String start_date,@Param("end_date")String end_date);

    @Transactional
    @Select("select * from wallet_user_change_log \n" +
            "where \"uid\"=#{uid} and create_date<=#{end_time} and create_date>=#{start_time}\n" +
            "ORDER BY create_time desc")
    List<AccountRecords> find_point_record_by_time(@Param("uid") String uid, @Param("start_time")String start_time, @Param("end_time")String end_time);

    @Transactional
    @Select("select count(*) from user_images where uid=#{uid}")
    int findUserImageByUid(@Param("uid")String uid);

    @Transactional
    @Insert("INSERT INTO user_images(uid,${img_type}) VALUES(#{uid},#{image_url})")
    int insertUserImage(@Param("uid") String uid, @Param("img_type") String img_type,@Param("image_url")String image_url);

    @Transactional
    @Update("update user_images set ${img_type}=#{image_url} where uid=#{uid}")
    boolean updateUserImage(@Param("uid")String uid,@Param("img_type")String img_type, @Param("image_url")String image_url);

    @Transactional
    @Update("update user_info set last_login_time=#{last_login_time} where uid=#{uid}")
    void updateUserInfoLoginTime(@Param("uid") String uid,@Param("last_login_time") String last_login_time);

    @Transactional
    @Insert("INSERT INTO user_reservation_consultation(name,phone,mail,time,cates) VALUES(#{name},#{phone},#{mail},#{time},#{cates})")
    int saveReservationConsultation(Order order);

    @Transactional
    @Select("select id from user_info where uid=#{uid}")
    int findUserIdByUid(@Param("uid")String uid);

    @Transactional
    @Select("select * from user_info where uid=#{uid}")
    User findUserByUid(@Param("uid")String uid);

    @Transactional
    @Select("select count(*) from user_wx_info where user_id=#{user_id}")
    Integer findIdByWXInfo(@Param("user_id")int user_id);

    @Transactional
    @Select("select * from risk_qa")
    List<OneQuestion> getRiskQA();

    @Transactional
    @Insert("insert into \"risk_level\" VALUES (#{uid}," +
            "#{answer}, #{greedy}, #{fear}, #{update_time}," +
            "#{create_time})")
    int addRiskScore(Map<String, String> mapParam);

    @Transactional
    @Insert("insert into user_appointment(name,time,phone_number,type,date) values(#{name},#{time},#{phone_number},#{type},#{date})")
    Integer saveUsrAppointment(Appointment appointment);

    @Transactional
    @Select("select * from sw_stock_industry")
    List<StockIndustry> getAllStock();

    @Transactional
    @Update("update sw_stock_industry set ticker_type=#{code} where ticker=#{ticker}")
    void updateStockIndustry(StockIndustry s);

    @Update("update sw_stock_industry set pinyin_firstword=#{pinyin_firstword} where ticker=#{ticker}")
    void updateStockIndustryPinYin(StockIndustry s);
}











