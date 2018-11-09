package com.tenthousand.fileupload.mapper;

import com.tenthousand.fileupload.bean.IIRIReportIndexBean;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;


@Repository
public interface FileMapper {

    @Select("select count(*) from backend_IIRI_report")
    Integer getFileCount() throws Exception;

    @Insert("insert into backend_IIRI_report_index (id,fid,title,author,key_words,date,create_date,create_time) " +
            "values (default,#{fid},#{title},#{author},#{key_words},#{date}::date,#{create_date}::date,#{create_time}::timestamp without time zone)")
    void createIIRIReportIndex(IIRIReportIndexBean bean) throws Exception;

    @Update("update backend_IIRI_report_index set html_path = #{html_path},summary = #{summary} where fid = #{fid}")
    void updateHtmlIndexAndSummary(IIRIReportIndexBean bean) throws Exception;

    @Update("update backend_IIRI_report_index set img_path = #{img_path} where fid = #{fid}")
    void updateImageIndex(IIRIReportIndexBean bean) throws Exception;

    @Select("select concat(html_path,',',img_path) from backend_IIRI_report_index where fid = #{fid}")
    String getIndexByFid(IIRIReportIndexBean bean) throws Exception;

    @Update("update backend_IIRI_report_index set title = #{title},author=#{author}" +
            " ,key_words = #{key_words},date = #{date}::date where fid = #{fid}")
    void updateByFid(IIRIReportIndexBean bean) throws Exception;
}
