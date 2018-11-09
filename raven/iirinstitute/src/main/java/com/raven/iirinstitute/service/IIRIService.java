package com.raven.iirinstitute.service;

import com.raven.iirinstitute.mapper.IIRIMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class IIRIService {

    @Autowired
    IIRIMapper iiriMapper;

    public Integer getIIRIReportCount() throws Exception {
        Integer count = iiriMapper.getIIRIReportCount();
        return (count == null) ? 0 : count;
    }

    public List<Map<String, Object>> getIIRIReportIndexByPage(int page) throws Exception {
        int limit = 4;
        int offset = (page - 1 < 0) ? 0 : page - 1;
        return iiriMapper.getIIRIReportIndex(limit, offset);
    }
}
