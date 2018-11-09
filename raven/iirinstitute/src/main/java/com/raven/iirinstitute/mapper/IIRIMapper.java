package com.raven.iirinstitute.mapper;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface IIRIMapper {


    @Select("select count(id) from backend_iiri_report_index")
    Integer getIIRIReportCount() throws Exception;

    @Select("select title,author,key_words,date,summary,html_path,img_path from backend_iiri_report_index order by id desc limit #{param1} offset #{param2}")
    List<Map<String, Object>> getIIRIReportIndex(int limit, int offset) throws Exception;

}
