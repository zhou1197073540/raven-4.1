package com.raven.iirinstitute.controller;

import com.raven.iirinstitute.consts.Const;
import com.raven.iirinstitute.dto.RespDto;
import com.raven.iirinstitute.service.IIRIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class IIRIController {

    @Autowired
    IIRIService iiriService;

    @RequestMapping("/reports_index/{page}")
    @ResponseBody
    public RespDto getIIRIReportIndex(@PathVariable("page") int page) throws Exception {
        List<Map<String, Object>> rets = iiriService.getIIRIReportIndexByPage(page);
        return new RespDto(Const.SUCCESS, Const.SUCCESS_MSG, rets);
    }

    @RequestMapping("/reports_index")
    @ResponseBody
    public RespDto getIIRIReportIndex0() throws Exception {
        return getIIRIReportIndex(1);
    }

    @RequestMapping("/reports_count")
    @ResponseBody
    public RespDto getReportsCount() throws Exception {
        Integer count = iiriService.getIIRIReportCount();
        return new RespDto(Const.SUCCESS, Const.SUCCESS_MSG, count);
    }
}
