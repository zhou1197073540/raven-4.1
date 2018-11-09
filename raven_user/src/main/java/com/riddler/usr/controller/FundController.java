package com.riddler.usr.controller;

import com.alibaba.fastjson.JSONObject;
import com.riddler.usr.annotation.AuthCheck;
import com.riddler.usr.bean.*;
import com.riddler.usr.common.Auth;
import com.riddler.usr.common.MsgConstant;
import com.riddler.usr.dto.RespFund;
import com.riddler.usr.dto.ResponseBaseDTO;
import com.riddler.usr.service.FundService;
import com.riddler.usr.service.OthersService;
import com.riddler.usr.service.UserService;
import com.riddler.usr.utils.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.riddler.usr.common.Const.V_CODE_EX_TIME;

@RestController
public class FundController {
    public static final Logger logger = LoggerFactory.getLogger(AccessTokenUtil.class);
    @Autowired
    FundService fundService;

    /**
     * FOF开放基金组合
     */
    @GetMapping("/fund/fundCombine")
    @ResponseBody
    public ResponseBaseDTO fundCombine() {
        List<FundBaseInfo> bases = fundService.selectFundCombine();
        return new ResponseBaseDTO(MsgConstant.SUCCESS, bases);
    }

    /**
     * FOF基金历史净值
     */
    @PostMapping("/fund/fundHisttoryNetValue")
    @ResponseBody
    public RespFund fundHisttoryNetValue(@RequestBody JSONObject object) {
        String code = object.getString("fund_code");
        if (StringUtils.isBlank(code)) {
            return new RespFund(MsgConstant.ERROR, "fund_code为空", null);
        }
        List<FundHistory> bases = fundService.selectFundHisttoryNetValue(code);
        FundBaseInfo fund_one = fundService.selectByFundCode(code);
        return new RespFund(MsgConstant.SUCCESS, bases, fund_one);
    }

    /**
     * 股票持仓，债券持仓
     */
    @PostMapping("/fund/ticker_bond_position")
    @ResponseBody
    public ResponseBaseDTO tickerBondPosition(@RequestBody JSONObject object) {
        String fund_code = object.getString("fund_code");
        if (StringUtils.isBlank(fund_code)) {
            return new ResponseBaseDTO(MsgConstant.ERROR, "fund_code为空");
        }
        List<FundQuarter> jjcc = fundService.tickerBondPosition(fund_code, "jjcc");
        float total_jjcc = DataUtil.addNetValueProportion(jjcc);
        List<FundQuarter> zqcc = fundService.tickerBondPosition(fund_code, "zqcc");
        float total_zqcc = DataUtil.addNetValueProportion(zqcc);


        return new ResponseBaseDTO(MsgConstant.SUCCESS, DataUtil.generateMap(new String[]{"jjcc", "zqcc", "total_jjcc_net_value", "total_zqcc_net_value"},
                new Object[]{jjcc, zqcc, total_jjcc, total_zqcc}));
    }

    /**
     * fof净值
     */
    @GetMapping("/fof/net_value")
    @ResponseBody
    public ResponseBaseDTO fofNetValue() {
        List<FofNetValue> fof = fundService.selectFofNetValue();
        return new ResponseBaseDTO(MsgConstant.SUCCESS, fof);
    }

    /**
     * fof 本月收益
     */
    @GetMapping("/fof/fof_performance")
    @ResponseBody
    public ResponseBaseDTO fofPerformance() {
        FofPerformance fof = fundService.selectFofPerformance();
        return new ResponseBaseDTO(MsgConstant.SUCCESS, fof);
    }

    /**
     * fof收益历史
     */
    @GetMapping("/fof/fof_ratemkt_his")
    @ResponseBody
    public ResponseBaseDTO fofRateMktHis() throws Exception {
        List<FofRateMkt> listRet = fundService.getFofRateMktHis();
        return new ResponseBaseDTO(MsgConstant.SUCCESS, "ratemkt_success", listRet);
    }
}
