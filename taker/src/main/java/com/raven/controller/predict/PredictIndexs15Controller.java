package com.raven.controller.predict;

import com.alibaba.druid.sql.dialect.mysql.ast.MysqlForeignKey;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.raven.annotation.AuthCheck;
import com.raven.bean.*;
import com.raven.common.MsgConstant;

import com.raven.consts.Auth;
import com.raven.dto.PointsDto;
import com.raven.dto.RespDto;
import com.raven.dto.ResponseBaseDTO;
import com.raven.service.DailyIndexService;
import com.raven.service.PredictService;
import com.raven.service.UserService;

import com.raven.utils.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.raven.consts.Const.*;

/**
 * Created by daniel.luo on 2017/7/12.
 */
//@Controller
@RestController

public class PredictIndexs15Controller {
    @Autowired
    private PredictService predictService;

    @Autowired
    private UserService userService;

    @Autowired
    private DailyIndexService dailyIndexService;

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    KafkaUtil kafkaUtil;

    @Autowired
    private Environment env;

    /**
     * 获取用户积分
     *
     * @param uid
     * @return
     * @throws Exception
     */
    @GetMapping("/count/{uid}")
    @ResponseBody
    public ResponseBaseDTO getPoints(@PathVariable String uid) throws Exception {
        ResponseBaseDTO responseBaseDTO = new ResponseBaseDTO();
        int ret = predictService.getUserUserCountByUid(uid);
        System.out.println("ret : " + ret);
        responseBaseDTO.setData(ret);
        responseBaseDTO.setStatus(MsgConstant.SUCCESS);
        return responseBaseDTO;
    }


    @GetMapping("/remote_count/{uid}")
    public void testTwoService(@PathVariable String uid) throws Exception {
        System.out.println("diao dao");
        userService.getPoints(uid);
    }

    /**
     * 预测页和交易指南页
     * @param jo
     * @param request
     * @return
     * @throws Exception
     */
    @ResponseBody
    @PostMapping("/predict/index_history")
    public ResponseBaseDTO getIndexHistory(@RequestBody JSONObject jo,
                                           HttpServletRequest request) throws Exception {
        JSONObject data = jo.getJSONObject("data");
        String code = data.getString("code");
        System.out.println("index code : " + code);
        ResponseBaseDTO responseBaseDTO = new ResponseBaseDTO();
//        List<IndexValue> listRet=null;
//        if(dailyIndexService.checkCodeIsForeignCoin(code)){//判断是不是外汇
//            listRet=dailyIndexService.get180IndexHistoryOfForeignCoin(code);
//        }else{
//            listRet = dailyIndexService.get180IndexHistory(code);
//        }
        JSONObject resJo = dailyIndexService.getIndexHistoryData(code);
        responseBaseDTO.setData(resJo);
        responseBaseDTO.setStatus(MsgConstant.SUCCESS);
        return responseBaseDTO;
    }

    /**
     * 周选股活动里面的每支股票的过去180的情况的获取函数
     * @return
     * @throws Exception
     */
    public JSONObject doGet180() throws Exception {
        Jedis jedis = RedisUtil.getJedis();
        JSONObject jo = null;
        jo = JSON.parseObject(jedis.get("stock_180"));
        if (jo == null) {
            jo = dailyIndexService.getStock180();
            jedis.set("stock_180", JSON.toJSONString(jo));
        }
        jedis.close();
        return jo;
    }

    /**
     * 周选股活动里面的每支股票的过去180的情况的获取接口
     * @return
     * @throws Exception
     */
    @ResponseBody
    @GetMapping("/predict/stock_180")
    public ResponseBaseDTO get180() throws Exception {
        ResponseBaseDTO responseBaseDTO = new ResponseBaseDTO();
        responseBaseDTO.setData(doGet180());
        responseBaseDTO.setStatus(MsgConstant.SUCCESS);
        return responseBaseDTO;
    }

    /**
     * 接收前端传过来的预测内容
     * @param jo
     * @param request
     * @return
     * @throws Exception
     */
    @AuthCheck(Auth.NEED_LOGIN)
    @ResponseBody
    @PostMapping("/predict/user_quarter_predict")
    public ResponseBaseDTO reciveQuarterPredict(
            @RequestBody JSONObject jo,
            HttpServletRequest request) throws Exception {
        String dateStr = TimeUtil.getDate(0);
        String timeStr = TimeUtil.getDateTime();
        int curTime[] = predictService.convertTimeInt(timeStr);
        String p_pos = "";
        p_pos = predictService.generatePPos(curTime, dateStr);
        ResponseBaseDTO responseBaseDTO = new ResponseBaseDTO();
        // TODO 以后这里会到redis里面验证下当前token是否已失效
        // 上面提及的已经在AuthCheck里面做了验证
        System.out.println(jo);
        JSONObject data = jo.getJSONObject("data");
        String s_type = data.getString("s_type");
        String amount = data.getString("amount");
        String buy_status = data.getString("buy_status");
        String token = request.getHeader("Authorization");
        String uid = tokenUtil.getProperty(token, "uid");
        System.out.println("recive uid : " + uid);
        System.out.println(s_type);
        System.out.println(amount);
        System.out.println(buy_status);
        //insert to predict_record
        Map<String, String> mapParam = new HashMap<String, String>();
        String serial_num = HashUtil.generateSerialNum(uid, "5");
        // 冻结相应积分
        // feign wallet
        PointsDto pd = new PointsDto();
        pd.setChange(Integer.parseInt(amount));
        pd.setOid(serial_num);
        pd.setUid(uid);
        pd.setDate(dateStr);
        pd.setTime(timeStr);
        pd.setComment(5);
        String callbackUrl = "http://"
                + env.getProperty("spring.application.name")
                + "/feign/callback";
        pd.setCallbackUrl(callbackUrl);
//        RespDto ret = userService.changePoints(pd);
        RespDto ret = userService.freezePoints(pd);
        if (100101 == ret.getStatus()) {
            mapParam.put("user_id", uid);
            mapParam.put("serial_num", serial_num);
            mapParam.put("s_type", s_type);
            mapParam.put("amount", amount);
            mapParam.put("p_pos", p_pos);
            mapParam.put("action_time", timeStr);
            mapParam.put("buy_status", buy_status);
            mapParam.put("order_status", "wait_close");
            if (predictService.addQuarterPredictRecord(mapParam) == 1) {
                responseBaseDTO.setStatus(MsgConstant.SUCCESS);
                responseBaseDTO.setData("操作成功");
            } else {
                responseBaseDTO.setStatus(MsgConstant.ERROR);
                responseBaseDTO.setData("服务器繁忙，请稍后重试");
            }
        } else if (100104 == ret.getStatus()) {
            responseBaseDTO.setStatus(MsgConstant.ERROR);
            responseBaseDTO.setData("余额不足");
        }
        return responseBaseDTO;
    }

    /**
     * 前端获取对赌记录的接口，含coin值
     * @param request
     * @return
     * @throws Exception
     */
    @AuthCheck(Auth.NEED_LOGIN)
    @ResponseBody
    @GetMapping("/predict/quarter_predict_record")
    public ResponseBaseDTO getQuarterPredictRecord(HttpServletRequest request) throws Exception {
        ResponseBaseDTO responseBaseDTO = new ResponseBaseDTO();
        String token = request.getHeader("Authorization");
        String uid = tokenUtil.getProperty(token, "uid");
        System.out.println("token : " + token);
        System.out.println("haha");
        System.out.println("Record uid : " + uid);
        RespDto wallet = userService.getPoints(uid);
        System.out.println(wallet.getStatus() + wallet.getMsg() + wallet.getData());

        if (wallet == null) {
            responseBaseDTO.setData("invalid coin");
            responseBaseDTO.setStatus(MsgConstant.ERROR);
            return responseBaseDTO;
        } else {
            int status = wallet.getStatus();
            String coin = null;
            if (status == 100101) {
                coin = wallet.getData().toString();
            }
            System.out.println("coin : " + coin);
            List<PredictRecord> listRet = predictService.getQuarterPredictRecord(uid);
            for (PredictRecord pr : listRet) {
                System.out.println(pr.getAction_time());
            }
            JSONObject resJo = new JSONObject();
            resJo.put("coin", coin);
            resJo.put("predict_list", listRet);
            responseBaseDTO.setData(resJo);
            responseBaseDTO.setStatus(MsgConstant.SUCCESS);
            return responseBaseDTO;
        }
    }

    /**
     * 前端获取赔率接口
     * @return
     * @throws Exception
     */
    @ResponseBody
    @GetMapping("/predict/quarter_predict_status")
    public ResponseBaseDTO getQuarterPredictStatus() throws Exception {
        ResponseBaseDTO responseBaseDTO = new ResponseBaseDTO();
        Jedis jedis = RedisUtil.getJedis();
        String ret = jedis.get("上证指数" + "_quarter_odds");
        jedis.close();
        responseBaseDTO.setStatus(MsgConstant.SUCCESS);
        responseBaseDTO.setData(JSONObject.parseObject(ret));
        return responseBaseDTO;
    }

    /**
     * 接收用户选股内容接口
     * @param jo
     * @param request
     * @return
     * @throws Exception
     */
    @AuthCheck(Auth.NEED_LOGIN)
    @ResponseBody
    @PostMapping("/predict/user_portfolio25")
    public ResponseBaseDTO recivePortfolio25(
            @RequestBody JSONObject jo,
            HttpServletRequest request) throws Exception {
        ResponseBaseDTO responseBaseDTO = new ResponseBaseDTO();
        LocalTime startTime = LocalTime.of(18, 0, 0);
        LocalTime endTime = LocalTime.of(22, 0, 0);
        LocalDate today = LocalDate.now();
        LocalTime curTime = LocalTime.now();
        String week = today.getDayOfWeek().toString();
        System.out.println(week);
        boolean proceedFlag = true;
        if ("MONDAY|TUESDAY|WEDNESDAY|THURSDAY".contains(week)) {
            proceedFlag = false;
        } else if ("FRIDAY".equals(week)) {
            if (curTime.isAfter(startTime)) proceedFlag = true;
            else proceedFlag = false;
        } else if ("SUNDAY".equals(week)) {
            if (curTime.isBefore(endTime)) proceedFlag = true;
            else proceedFlag = false;
        } else proceedFlag = true;
        if (proceedFlag) {
            String timeStr = TimeUtil.getDateTime();
            JSONObject data = jo.getJSONObject("data");
            String portfolio = data.getString("portfolio");
            String token = request.getHeader("Authorization");
            String uid = tokenUtil.getProperty(token, "uid");
            System.out.println("recive uid : " + uid);
            System.out.println(portfolio);
            System.out.println(uid);
            //insert to portfolio_collection
            //101 位50选25
            String serial_num = HashUtil.generateSerialNum(uid, "101");
//        if (portfolio.split(",", -1).length != 25) {
//            responseBaseDTO.setStatus(MsgConstant.SUCCESS);
//            responseBaseDTO.setData("股票数目不对");
//            return responseBaseDTO;
//        }
            Map<String, String> mapParam = new HashMap<String, String>();
            mapParam.put("user_id", uid);
            mapParam.put("serial_num", serial_num);
            mapParam.put("action_time", timeStr);
            mapParam.put("portfolio", portfolio);
            String period = predictService.getCurPeriod();
            mapParam.put("period", period);

            if (predictService.addPortfolio25(mapParam) == 1) {
                responseBaseDTO.setStatus(MsgConstant.SUCCESS);
                responseBaseDTO.setData("操作成功");
            } else {
                responseBaseDTO.setStatus(MsgConstant.ERROR);
                responseBaseDTO.setData("服务器繁忙，请稍后重试");
            }
        } else {
            responseBaseDTO.setStatus(MsgConstant.ERROR);
            responseBaseDTO.setData("当前时段不能选股,每周五18:00-周日22:00开启选股活动");
        }
        return responseBaseDTO;
    }

    /**
     * 用户自己的周选股活动选的股票号结果
     * @param request
     * @return
     * @throws Exception
     */
    @AuthCheck(Auth.NEED_LOGIN)
    @ResponseBody
    @GetMapping("/predict/pick_record")
    public ResponseBaseDTO pickRecord(HttpServletRequest request) throws Exception {
        // TODO 时间判断
        ResponseBaseDTO responseBaseDTO = new ResponseBaseDTO();
        String token = request.getHeader("Authorization");
        String uid = tokenUtil.getProperty(token, "uid");
        System.out.println("recive uid : " + uid);
        JSONObject resJo = doGet180();
        //TODO 查出已选股票
        List<String> listRet = predictService.getPickRecord(uid);
        if (listRet.size() == 1) {
            resJo.put("stocks_selected", listRet.get(0));
            responseBaseDTO.setStatus(MsgConstant.SUCCESS);
        } else {
            resJo.put("stocks_selected", null);
            responseBaseDTO.setStatus(MsgConstant.ERROR);
        }
        responseBaseDTO.setData(resJo);
        return responseBaseDTO;
    }

    /**
     * 周选股活动的展示接口，展示上个星期的公共组合的收益，还有如果你上周也选了一组，你的收益情况
     * @param request
     * @return
     * @throws Exception
     */
    @AuthCheck(Auth.NEED_LOGIN)
    @ResponseBody
    @GetMapping("/predict/weekly_detail")
    public ResponseBaseDTO weeklyDetail(HttpServletRequest request) throws Exception {
        // TODO 时间判断
        String date = TimeUtil.getDate(0);
        ResponseBaseDTO responseBaseDTO = new ResponseBaseDTO();
        String token = request.getHeader("Authorization");
        String uid = tokenUtil.getProperty(token, "uid");
        System.out.println("recive uid : " + uid);
        Jedis jedis = RedisUtil.getJedis();
        List<String> listPick = predictService.getOnePick(uid);
        JSONObject resJo = new JSONObject();
        //只展示公共选择
        JSONArray jaCommon = JSONObject.parseArray(jedis.get("common_rates"));
        if (jaCommon == null) {
            predictService.updateCommonRatePm(date);
            jaCommon = JSONObject.parseArray(jedis.get("common_rates"));
        }
//        resJo.put("common", JSONObject.toJSONString(jaCommon));
        resJo.put("common", jaCommon);
        resJo.put("common_total", jedis.get("common_total"));
        if (listPick.size() > 0) {
            //加个人加排名
            String portfolio = listPick.get(0);
            JSONArray jaPerson = predictService.personRateCal(portfolio);
            if (jaPerson != null) {
//                resJo.put("person", JSONObject.toJSONString(jaPerson));
                resJo.put("person", jaPerson);
                JSONObject joRank = JSONObject.parseObject(jedis.get(uid + ".rate.rank"));
                if (joRank != null) {
                    resJo.put("person_rank", joRank.getIntValue("rank"));
                    resJo.put("person_total", joRank.getString("total"));
                } else {
                    //周一的时候，没有排名，也没有组合涨跌幅
                    resJo.put("rank", null);
                    resJo.put("total", null);
                }
            }
        }
        String lastPeriod = predictService.getLastPCollection();
        Map<String, String> mapParam = new HashMap<String, String>();
        mapParam.put("user_id", uid);
        mapParam.put("period", lastPeriod);
        String oid = predictService.getOidByLastCollection(mapParam);
        if (StringUtils.isBlank(oid)) {
            resJo.put("last_period", "0");
        } else {
            List<String> listRet = predictService.getChangeByOid(oid);
            if (listRet.size() > 0) resJo.put("last_period", listRet.get(0));
            else resJo.put("last_period", "0");
        }
        responseBaseDTO.setData(resJo);
        responseBaseDTO.setStatus(MsgConstant.SUCCESS);
        return responseBaseDTO;
    }

    /**
     * 花积分进行机器人辅助预测
     * @param request
     * @return
     * @throws Exception
     */
    @AuthCheck(Auth.NEED_LOGIN)
    @ResponseBody
    @GetMapping("/predict/robot_one")
    public ResponseBaseDTO robot_one(HttpServletRequest request) throws Exception {
        ResponseBaseDTO responseBaseDTO = new ResponseBaseDTO();
        String dateStr = TimeUtil.getDate(0);
        String timeStr = TimeUtil.getDateTime();
        String token = request.getHeader("Authorization");
        String uid = tokenUtil.getProperty(token, "uid");
        // 扣除积分
        String serial_num = HashUtil.generateSerialNum(uid, "11");
        PointsDto pd = new PointsDto();
        pd.setChange(ROBOT_ONE);
        pd.setOid(serial_num);
        pd.setUid(uid);
        pd.setDate(dateStr);
        pd.setTime(timeStr);
        pd.setComment(11);
        String callbackUrl = "http://"
                + env.getProperty("spring.application.name")
                + "/feign/callback";
        pd.setCallbackUrl(callbackUrl);
        RespDto ret = userService.changePoints(pd);
        if (100101 == ret.getStatus()) {
            PredictVal result = predictService.predictRobotOne();
            if (null != result) {
                JSONObject resJo = new JSONObject();
                RespDto wallet = userService.getPoints(uid);
                if (wallet != null) {
                    if (100101 == wallet.getStatus()) {
                        resJo.put("coin", wallet.getData().toString());
                        resJo.put("point", result);
                        responseBaseDTO.setStatus(MsgConstant.SUCCESS);
                        responseBaseDTO.setData(resJo);
                    }
                }
            } else {
                responseBaseDTO.setStatus(MsgConstant.ERROR);
                responseBaseDTO.setData("服务器繁忙，请稍后重试");
            }
        } else if (100104 == ret.getStatus()) {
            responseBaseDTO.setStatus(MsgConstant.ERROR);
            responseBaseDTO.setData("余额不足");
        }
        return responseBaseDTO;
    }

    /**
     * 预测模块wallet扣除后的回调，这里会用来处理红点消息的发送
     * @param dto
     * @throws Exception
     */
    @PutMapping("/feign/callback")
    public void walletCallback(@RequestBody JSONObject dto) throws Exception {
        System.out.println("callback!!!");
        System.out.println(dto.getString("status") + dto.getString("msg") + dto.getJSONObject("data").getString("uid"));
        int status = dto.getInteger("status");
        JSONObject td = dto.getJSONObject("data");
        String change = td.getString("change").replace("-", "");
        String serialNum = td.getString("serialNum");
        String uid = td.getString("uid");
        String date = td.getString("date");
        String time = td.getString("time");
        int comment = td.getInteger("comment");
        if (100101 != status) {
            if (5 == comment) {
                // 作废对应此次操作，把之前下的注去掉 cut_by_points
            } else {

            }
        } else {
            if (5 == comment) {  // 赌博冻结
                String userName = predictService.getUserName(uid);
                PredictRecord onePredict = predictService.getPredictDetail(serialNum);
                if (StringUtils.isNotBlank(userName) && onePredict != null) {
                    String buyStatus = onePredict.getBuy_status().equals("up") ? "涨" : "跌";
                    kafkaUtil.sendMessage(TAKER_TOPIC, userName + "下注" +
                            onePredict.getAmount() + "积分,买" + buyStatus + "!");
                }
            } else if (6 == comment) {  // 预测成功
                String userName = predictService.getUserName(uid);
                PredictRecord onePredict = predictService.getPredictDetail(serialNum);
                if (StringUtils.isNotBlank(userName) && onePredict != null) {
                    String buyStatus = onePredict.getBuy_status().equals("up") ? "涨" : "跌";
                    kafkaUtil.sendMessage(TAKER_TOPIC, userName + "买" + buyStatus +
                            ",预测成功,赢得" + change + "积分!");
                    RedSpot rs = new RedSpot();
                    rs.setUid(uid);
                    rs.setCreateTime(TimeUtil.getDateTime());
                    rs.setMsg("恭喜您在预测指数活动中,预测成功,赢得" + onePredict.getAmount() + "积分");
                    rs.setMsgId(HashUtil.generateSerialNum(uid, String.valueOf(comment)));
                    rs.setType(comment);
                    kafkaUtil.sendMessage(RED_SPOT, JSON.toJSONString(rs));
                }
            } else if (50 == comment) {
                String userName = predictService.getUserName(uid);
                PredictRecord onePredict = predictService.getPredictDetail(serialNum);
                if (StringUtils.isNotBlank(userName) && onePredict != null) {
                    String buyStatus = onePredict.getBuy_status().equals("up") ? "涨" : "跌";
                    kafkaUtil.sendMessage(TAKER_TOPIC, userName + "买" + buyStatus +
                            ",预测失败,失去" + change + "积分!");
                    RedSpot rs = new RedSpot();
                    rs.setUid(uid);
                    rs.setCreateTime(TimeUtil.getDateTime());
                    rs.setMsg("很遗憾您在预测指数活动中,预测失败,失去" + onePredict.getAmount() + "积分");
                    rs.setMsgId(HashUtil.generateSerialNum(uid, String.valueOf(comment)));
                    rs.setType(comment);
                    kafkaUtil.sendMessage(RED_SPOT, JSON.toJSONString(rs));
                }
            } else if (13 == comment) {  // 周周赢，分钱消息
                // TODO red spot
                RedSpot rs = new RedSpot();
                rs.setUid(uid);
                rs.setCreateTime(TimeUtil.getDateTime());
                rs.setMsg("恭喜您在周周赢活动中，赢得人民币" + change + "元");
                rs.setMsgId(HashUtil.generateSerialNum(uid, String.valueOf(comment)));
                rs.setType(comment);
                kafkaUtil.sendMessage(RED_SPOT, JSON.toJSONString(rs));
            }
        }
    }


    /**
     * 获取某支股票的历史数据
     * @param request
     * @return
     * @throws Exception
     */
    //    @AuthCheck(Auth.NEED_LOGIN)
    @ResponseBody
    @GetMapping("/predict/stockHistory")
    public ResponseBaseDTO getOneStockHistory(HttpServletRequest request) throws Exception {
        ResponseBaseDTO responseBaseDTO = new ResponseBaseDTO();
        String code = request.getParameter("code");
        List<OneStockHistory> listRet = predictService.getOneStockHistory(code);
        responseBaseDTO.setData(listRet);
        responseBaseDTO.setStatus(MsgConstant.SUCCESS);
        return responseBaseDTO;
    }

    /**
     * 个股诊断
     * @param request
     * @return
     * @throws Exception
     */
    @ResponseBody
    @GetMapping("/predict/stockDailyEval")
    public ResponseBaseDTO getStockDailyEval(HttpServletRequest request) throws Exception {
        ResponseBaseDTO responseBaseDTO = new ResponseBaseDTO();
        String code = request.getParameter("code");
        if (StringUtils.isBlank(code)) {
            responseBaseDTO.setData("请传入证券号");
            responseBaseDTO.setStatus(MsgConstant.ERROR);
        } else {
            String content = predictService.getStockEval(code);
            if(content==null||content.isEmpty()){
                responseBaseDTO.setData("该股票不存在");
                responseBaseDTO.setStatus(MsgConstant.ERROR);
            }else{
                Map<String, String> data = new HashMap<String, String>();
                data = predictService.makeEvalData(content);
                data.put("name", predictService.getStockNameByTicker(code));
                responseBaseDTO.setData(data);
                responseBaseDTO.setStatus(MsgConstant.SUCCESS);
            }
        }
        return responseBaseDTO;
    }

    @ResponseBody
    @GetMapping("/predict/netValueHistory")
    public ResponseBaseDTO getNetValueHistory(HttpServletRequest request) throws Exception {
        ResponseBaseDTO responseBaseDTO = new ResponseBaseDTO();
        List<NetValue> listRet = predictService.getNetValueHistory();
        responseBaseDTO.setData(listRet);
        responseBaseDTO.setStatus(MsgConstant.SUCCESS);
        return responseBaseDTO;
    }

    /**
     * 获取热搜榜
     * @param request
     * @return
     * @throws Exception
     */
    @ResponseBody
    @GetMapping("/predict/focusRank")
    public ResponseBaseDTO getFocusRank(HttpServletRequest request) throws Exception {
        ResponseBaseDTO responseBaseDTO = new ResponseBaseDTO();
        List<FocusRank> listRet = predictService.getFocusRank();
        responseBaseDTO.setData(listRet);
        responseBaseDTO.setStatus(MsgConstant.SUCCESS);
        return responseBaseDTO;
    }

    /**
     * 根据主题获取领涨主题关联的股票
     * @param request
     * @return
     * @throws Exception
     */
    @ResponseBody
    @GetMapping("/predict/themeStocks")
    public ResponseBaseDTO getThemeStocks(HttpServletRequest request) throws Exception {
        ResponseBaseDTO responseBaseDTO = new ResponseBaseDTO();
        String theme = request.getParameter("theme");
        if (StringUtils.isBlank(theme)) {
            responseBaseDTO.setData("请附带主题");
            responseBaseDTO.setStatus(MsgConstant.ERROR);
        } else {
            List<OneThemeStock> listRet = predictService.getThemeStocks(theme);
            responseBaseDTO.setData(listRet);
            responseBaseDTO.setStatus(MsgConstant.SUCCESS);
        }
        return responseBaseDTO;
    }
}
