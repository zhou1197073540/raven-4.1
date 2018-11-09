package com.riddler.usr.controller;

import com.alibaba.fastjson.JSONObject;
import com.riddler.usr.annotation.AuthCheck;
import com.riddler.usr.bean.*;
import com.riddler.usr.common.Auth;
import com.riddler.usr.common.Const;
import com.riddler.usr.common.MsgConstant;
import com.riddler.usr.dto.PointsDto;
import com.riddler.usr.dto.RespDto;
import com.riddler.usr.dto.ResponseBaseDTO;
import com.riddler.usr.feign.WalletRemoteAPI;
import com.riddler.usr.service.AsstesService;
import com.riddler.usr.service.UserService;
import com.riddler.usr.utils.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RestController
public class AssetsController {
    Logger logger= LoggerFactory.getLogger(AssetsController.class);
    @Autowired
    WalletRemoteAPI walletApi;
    @Autowired
    AsstesService assetsService;
    @Autowired
    UserService userService;
    @Autowired
    TokenUtil tokenUtil;

    @GetMapping("/assets_allocation/etf_daily_status")
    @ResponseBody
    @AuthCheck(Auth.NEED_LOGIN)
    public ResponseBaseDTO dailyStatus(@RequestHeader("Authorization") String token)
            throws Exception {
        ResponseBaseDTO dataDTO = new ResponseBaseDTO();
        String uid = tokenUtil.getProperty(token, "uid");
        List<Map<String, Object>> etfs = new ArrayList<Map<String, Object>>();
        List<Assets> assets = assetsService.getListAssets();
        if (assets != null && assets.size() > 0) {
            for (Assets asset : assets) {
                float profit = assetsService
                        .caculateOneProfitOrLoss(asset, uid);
                // float
                // cost_price=assetsService.calculateOneETFCost(asset,uid);
                String[] keys = {"assets_code", "price", "unit", "weight",
                        "profit"};
                Object[] vals = {asset.getAssets_code(),
                        asset.getLastest_price(), asset.getUnit(),
                        asset.getWeight(), profit};
                etfs.add(DataUtil.generateMap(keys, vals));
            }
            dataDTO.setStatus(MsgConstant.SUCCESS + "");
            dataDTO.setData(etfs);
            return dataDTO;
        }
        dataDTO.setStatus(MsgConstant.ERROR);
        return dataDTO;
    }

    @GetMapping("/assets_allocation/usr_wallet")
    @ResponseBody
    @AuthCheck(Auth.NEED_LOGIN)
    public ResponseBaseDTO usr_wallet(@RequestHeader("Authorization") String token)
            throws Exception {
        ResponseBaseDTO dataDTO = new ResponseBaseDTO();
        String uid = tokenUtil.getProperty(token, "uid");
        if (StringUtils.isBlank(uid))
            return null;
        Wallet wallet = userService.findMoney(uid);
        String rate = assetsService.getRmbRate();
        if (null != wallet && !StringUtils.isBlank(rate)) {
            dataDTO.setStatus(MsgConstant.SUCCESS);
            String[] keys = {"fyb", "rmb", "rate"};
            Object[] vals = {wallet.getFyb(), wallet.getRmb(), rate};
            dataDTO.setData(DataUtil.generateMap(keys, vals));
            return dataDTO;
        }
        dataDTO.setStatus(MsgConstant.ERROR);
        return dataDTO;
    }

    /**
     * 计算用户钱包中的沣沅币转换成总的美元
     *
     * @return
     * @throws Exception
     */
    @ResponseBody
    public ResponseBaseDTO userFybToDollar(@RequestBody JSONObject jo)
            throws Exception {
        ResponseBaseDTO dataDTO = new ResponseBaseDTO();
        String uid = jo.getString("token");
        // uid = "529ca8050a00180790cf88b63468826a";
        if (StringUtils.isBlank(uid))
            return null;
        Wallet wallet = userService.findMoney(uid);
        List<Assets> assets = assetsService.getListAssets();
        float onePrice = DataUtil.calculateOneFybToDollar(assets);
        if (assets.size() > 0) {
            dataDTO.setStatus(MsgConstant.SUCCESS);
            String[] keys = {"all_dollar", "date"};
            Object[] vals = {wallet.getFyb() * onePrice,
                    TimeUtil.getDateTime()};
            dataDTO.setData(DataUtil.generateMap(keys, vals));
            return dataDTO;
        }
        dataDTO.setStatus(MsgConstant.ERROR);
        return dataDTO;
    }

    @PostMapping("/assets_allocation/usr_dollar_wallet_history")
    @ResponseBody
    public ResponseBaseDTO historyDollar(@RequestBody JSONObject jo)
            throws Exception {
        ResponseBaseDTO dataDTO = new ResponseBaseDTO();
        String uid = jo.getString("token");
        // uid = "529ca8050a00180790cf88b63468826a";
        if (StringUtils.isBlank(uid))
            return null;
        List<DailyRecordVO> assets = assetsService.selectFybHistory(uid);
        if (assets.size() > 0) {
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            for (DailyRecordVO fy : assets) {
                String[] keys = {"fyb", "dollar", "date"};
                Object[] vals = {fy.getFyb(), fy.getUnit_price(), fy.getDate()};
                list.add(DataUtil.generateMap(keys, vals));
            }
            dataDTO.setStatus(MsgConstant.SUCCESS);
            dataDTO.setData(list);
            return dataDTO;
        }
        dataDTO.setStatus(MsgConstant.ERROR);
        return dataDTO;
    }

    @PostMapping("/assets_allocation/usr_rmb_wallet_history")
    @ResponseBody
    public ResponseBaseDTO historyRMB(@RequestBody JSONObject jo)
            throws Exception {
        ResponseBaseDTO dataDTO = new ResponseBaseDTO();
        String uid = jo.getString("token");
        // uid = "529ca8050a00180790cf88b63468826a";
        if (StringUtils.isBlank(uid))
            return null;
        List<DailyRecordVO> assets = assetsService.selectRMBHistory(uid);
        if (assets.size() > 0) {
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            for (DailyRecordVO fy : assets) {
                String[] keys = {"rmb", "date"};
                Object[] vals = {fy.getRmb(), fy.getDate()};
                list.add(DataUtil.generateMap(keys, vals));
            }
            dataDTO.setStatus(MsgConstant.SUCCESS);
            dataDTO.setData(list);
            return dataDTO;
        }
        dataDTO.setStatus(MsgConstant.ERROR);
        return dataDTO;
    }

    // ==============================改版数据接口===================================================
    @GetMapping("/assets_allocation/deadData")
    @ResponseBody
    public ResponseBaseDTO noLoginData() {
        ResponseBaseDTO dataDTO = new ResponseBaseDTO();
        try {
            List<Assets> assets = assetsService.getNewestAssets();// 获取最新的资产价格
            if (assets != null && assets.size() > 0) {
                String rate = assetsService.getRmbRate(); // 最新汇率

                String[] keys = {"assets", "dollar_rmb_rate"/*,
                        "default_history", "default_profit" */};
                Object[] vals = {assets, rate};

                dataDTO.setStatus(MsgConstant.SUCCESS);
                dataDTO.setData(DataUtil.generateMap(keys, vals));
            } else {
                dataDTO.setStatus(MsgConstant.ERROR);
            }
            return dataDTO;
        } catch (Exception e) {
            e.printStackTrace();
            dataDTO.setStatus(MsgConstant.ERROR);
            return dataDTO;
        }
    }

    private Object default_profit(List<Assets> assets) {
        List<Map<String, String>> list_VO = new ArrayList<Map<String, String>>();
        for (Assets asset : assets) {
            list_VO.add(DataUtil.generateMap("name:" + asset.getAssets_code(),
                    "num:" + Math.random() * 50));
        }
        return list_VO;
    }

    private Object default_history() {
        List<Map<String, String>> listVO = new ArrayList<Map<String, String>>();
        listVO.add(DataUtil.generateMap("fyb:12", "unit_price:720", "date:"
                + TimeUtil.getCurDateTime()));
        return listVO;
    }

    @GetMapping("/assets_allocation/needLoginData_old")
    @ResponseBody
    @AuthCheck(Auth.NEED_LOGIN)
    public ResponseBaseDTO needLoginData_old(@RequestHeader("Authorization") String token) {
        ResponseBaseDTO dataDTO = new ResponseBaseDTO();
        try {
            String uid = tokenUtil.getProperty(token, "uid");
            Wallet wallet = userService.findMoney(uid);
            List<Assets> assets = assetsService.getNewestAssets();// 获取最新的资产价格

            List<Map<String, String>> list_map = new ArrayList<Map<String, String>>();
            if (null == wallet || assets == null || assets.size() <= 0) {
                dataDTO.setStatus(MsgConstant.ERROR);
                return dataDTO;
            } else {
                // 计算ETF是否盈亏
                for (Assets asset : assets) {
                    float profit = assetsService.caculateOneProfitOrLoss(asset,
                            uid);
                    list_map.add(DataUtil.generateMap(
                            "name:" + asset.getAssets_code(), "num:" + profit));
                }
            }
            List<DailyRecordVO> list_vos = new ArrayList<DailyRecordVO>();
            List<DailyRecordVO> list_vo = assetsService.selectHistory(uid);
            list_vo.stream().filter(x -> x != null)
                    .sorted((a, b) -> a.getDate().compareTo(b.getDate()))
                    .forEach(x -> list_vos.add(x));

            List<Map<String, String>> listVO = new ArrayList<Map<String, String>>();
            for (DailyRecordVO vo : list_vos) {
                listVO.add(DataUtil.generateMap("fyb:" + vo.getFyb(),
                        "unit_price:" + vo.getUnit_price(),
                        "date:" + vo.getDate()));
            }

            String[] keys = {"wallet", "profit", "history"};
            Object[] vals = {
                    DataUtil.generateMap("fyb:" + wallet.getFyb(), "rmb:"
                            + wallet.getRmb()), list_map, listVO};

            dataDTO.setStatus(MsgConstant.SUCCESS);
            dataDTO.setData(DataUtil.generateMap(keys, vals));
            return dataDTO;
        } catch (Exception e) {
            e.printStackTrace();
            dataDTO.setStatus(MsgConstant.ERROR);
            return dataDTO;
        }
    }

    @GetMapping("/assets_allocation/needLoginData_get")
    @ResponseBody
    public ResponseBaseDTO needLoginData_get() {
        ResponseBaseDTO dataDTO = new ResponseBaseDTO();
        try {
//             String uid = jo.getString("token");
            String uid = "f5a5804d22335318befd4edcf732f2ee";
            Wallet wallet = userService.findMoney(uid);
            List<Assets> assets = assetsService.getNewestAssets();// 获取最新的资产价格

            List<Map<String, String>> list_map = new ArrayList<Map<String, String>>();
            // Map<String, String> assets_map=new HashMap<String, String>();

            if (assets == null || assets.size() <= 0) {
                dataDTO.setStatus(MsgConstant.ERROR);
                return dataDTO;
            } else {
                // 计算ETF是否盈亏
                for (Assets asset : assets) {
                    float profit = assetsService.caculateOneProfitOrLoss(asset,
                            uid);
                    list_map.add(DataUtil.generateMap(
                            "name:" + asset.getAssets_code(), "num:" + profit));
                }
            }
            List<DailyRecordVO> list_vos = new ArrayList<DailyRecordVO>();
            List<DailyRecordVO> list_vo = assetsService.selectHistory(uid);
            list_vo.stream().filter(x -> x != null)
                    .sorted((a, b) -> a.getDate().compareTo(b.getDate()))
                    .forEach(x -> list_vos.add(x));


            List<Map<String, String>> listVO = new ArrayList<Map<String, String>>();
            for (DailyRecordVO vo : list_vos) {
                listVO.add(DataUtil.generateMap("fyb:" + vo.getFyb(),
                        "unit_price:" + vo.getUnit_price(),
                        "date:" + vo.getDate()));
            }


            String[] keys = {"wallet", "profit", "history"};
            Object[] vals = {
                    DataUtil.generateMap("fyb:" + wallet.getFyb(), "rmb:"
                            + wallet.getRmb()), list_map, listVO};

            dataDTO.setStatus(MsgConstant.SUCCESS);
            dataDTO.setData(DataUtil.generateMap(keys, vals));
            return dataDTO;
        } catch (Exception e) {
            e.printStackTrace();
            dataDTO.setStatus(MsgConstant.ERROR);
            return dataDTO;
        }
    }

    // ==============================再次改版数据接口，再改我就要打人了===================================================

    @GetMapping("/assets_allocation/needLoginData")
    @ResponseBody
    @AuthCheck(Auth.NEED_LOGIN)
    public ResponseBaseDTO needLoginData(@RequestHeader("Authorization") String token) {
        ResponseBaseDTO dataDTO = new ResponseBaseDTO();
        try {
            String uid = tokenUtil.getProperty(token, "uid");
            List<Assets> assets = assetsService.getNewestAssets();// 获取最新的资产价格
            List<Map<String, String>> list_map = new ArrayList<Map<String, String>>();
            List<MoneyWeight> list_moneyweight = new ArrayList<>();
            if (assets == null || assets.size() <= 0) {
                dataDTO.setStatus(MsgConstant.ERROR);
                return dataDTO;
            } else {
                // 计算ETF盈亏情况
                for (Assets asset : assets) {
                    MoneyWeight profit = assetsService.caculateProfitLost(uid, asset);
                    if (null == profit) {
                        list_moneyweight.add(null);
                    } else {
                        list_map.add(DataUtil.generateMap(
                                "name:" + asset.getAssets_code(), "num:" + profit.getProfit()));
                        list_moneyweight.add(profit);
                    }
                }
            }
            //计算每个etf的总钱的占比
            List<Map<String, String>> etfMoneyRate = assetsService.cauETFManeyRate(list_moneyweight);

            //调用钱包，获取用户当前积分
            RespDto dto = null;
            try {
                dto = walletApi.getUsrPoints(uid);
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<AssetsRateVO> rateVO = assetsService.selectAssetsRateRecords(uid, dto);
            List<Map<String, String>> listVO = new ArrayList<Map<String, String>>();
            for (AssetsRateVO vo : rateVO) {
                listVO.add(DataUtil.generateMap("points:" + vo.getPoints(),
                        "date:" + vo.getDate()));
            }
            String[] keys = {"total_points", "profit", "history", "money_rate"};
            Object[] vals = {dto.getData(), list_map, listVO, etfMoneyRate};

            dataDTO.setStatus(MsgConstant.SUCCESS);
            dataDTO.setData(DataUtil.generateMap(keys, vals));
            return dataDTO;
        } catch (Exception e) {
            e.printStackTrace();
            dataDTO.setStatus(MsgConstant.ERROR);
            return dataDTO;
        }
    }

    //返回用户当前当前资产积分
    private String getUserAssetsPoint(String uid){
         RespDto dto = walletApi.getUsrPoints(uid);
         return dto.getData().toString();
    }

    /**
     * 资产组合页面最下面表格的接口
     */
    @GetMapping("/assets_allocation/assets_table_data")
    @ResponseBody
    @AuthCheck(Auth.NEED_LOGIN)
    public ResponseBaseDTO getAssetsInfo(@RequestHeader("Authorization") String token) throws Exception {
        String uid = tokenUtil.getProperty(token, "uid");
//        String uid="f5a5804d22335318befd4edcf732f2ee";
        //获取最新资产价格
        List<Assets> assets = assetsService.getNewestAssets();
        List<Map<String, String>> list_map = new ArrayList<Map<String, String>>();//资产盈亏
        if (assets == null || assets.size() <= 0) {
            return new ResponseBaseDTO("error", "原始资产为空");
        } else {
            // 计算每个ETF总钱的净盈亏
            for (Assets asset : assets) {
                MoneyWeight profit = assetsService.caculateProfitLost(uid, asset);
                if (profit!=null) {
                    list_map.add(DataUtil.generateMap(
                            "code:" + asset.getAssets_code(),
                            "cost:" + profit.getBuy_time_price(),//成本价
                            "last_price:" + asset.getLastest_price(),//当前价格
                            "profit:" + profit.getProfit()));//盈亏
                }
            }
        }
        List<UserAssetsPoints> etf_total=assetsService.selectPerEtfTotalSum();
        float sumAddUp=assetsService.selectSumAddUp();
        for (UserAssetsPoints one:etf_total){
            BigDecimal rate=new BigDecimal(one.getAdd_up()).divide(new BigDecimal(sumAddUp),4,BigDecimal.ROUND_HALF_EVEN);
            assetsService.mergeDatas(list_map, one,rate);
        }
        return new ResponseBaseDTO(MsgConstant.SUCCESS, list_map);
    }

    /**
     * 资产组合页面最上面的折线图接口
     */
    @GetMapping("/assets_allocation/usr_points_changes_history")
    @ResponseBody
    @AuthCheck(Auth.NEED_LOGIN)
    public ResponseBaseDTO usr_points_changes_history(@RequestHeader("Authorization") String token) throws Exception {
        String uid = tokenUtil.getProperty(token, "uid");
//        String uid="f5a5804d22335318befd4edcf732f2ee";
        List<UserAssetsPoints> userHistory = assetsService.selectAssetsHistoryRecords(uid);
        return new ResponseBaseDTO("success", userHistory);

//        ===========================================
//        //调用钱包，获取用户当前积分
//        RespDto dto = null;
//        try {
//            dto = walletApi.getUsrPoints(uid);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ResponseBaseDTO("error", "调用钱包接口异常");
//        }
//        if (!dto.getStatus().equals(Const.SUCCESS + "")) {
//            return new ResponseBaseDTO("error", "钱包中的资产积分不足");
//        }
//        List<AssetsRateVO> rateVO = assetsService.selectAssetsRateRecords(uid, dto);
//        if (null == rateVO) return new ResponseBaseDTO("error", null);
//        List<Map<String, String>> listVO = new ArrayList<Map<String, String>>();
//        for (AssetsRateVO vo : rateVO) {
//            listVO.add(DataUtil.generateMap("points:" + vo.getPoints(),
//                    "date:" + vo.getDate()));
//        }
//        return new ResponseBaseDTO("success", listVO);
    }

    /**
     * 资产积分转移
     * 可用积分转换投资积分，投资积分转换可用积分
     */
    @PostMapping("/assets_allocation/pointsTransfer")
    @ResponseBody
    @AuthCheck(Auth.NEED_LOGIN)
    public RespDto pointsTransfer(@RequestHeader("Authorization") String token, @RequestBody JSONObject json) {
        String uid = tokenUtil.getProperty(token, "uid");
//        String uid="955ab77f26c6aafc0524b01e3b76bebc";
        String type=json.getString("type");
        int points = Integer.parseInt(json.getString("points"));
        if(type.equals("1")){//可用积分转入到投资积分
        }else if (type.equals("2")){//投资积分转出到可用积分
            points=-points;
        }
        RespDto dto = null;
        try {
            PointsDto point = PointsDto.New()
                    .setUid(uid)
                    .setChange(points)
                    .setDate(TimeUtil.getCurDateTime())
                    .setTime(TimeUtil.getDateTime())
                    .setOid(HashUtil.generateSerialNum(uid, MsgConstant.ASSETS_FROM_POINTS+"")
                    );
            dto = walletApi.pointsTransfer(point);
            if(dto.getStatus().equals("100101")&&point.getChange()>0){
                calculateETFCost(point.getChange()+"",point.getUid(),point.getOid());
            }
            System.out.println("积分转移成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new RespDto("error", "调用钱包接口异常");
        }
        return dto;
    }
    /**
     * 计算ETF的成本价
     * @return
     */
    public void calculateETFCost(String point,String uid,String oid)throws Exception {
        if(point.isEmpty()||uid==null||uid.isEmpty())return ;
        String cur_date=TimeUtil.getDate(-1);//当前的日期是用昨天的，因为今天的etf股票没有出现，所以用昨天的
        String cur_points=assetsService.getCurUserAssetsPoints(uid);//用户当前资产积分,查出来的已经包括了刚刚转进去的积分，所以要减掉
        List<Assets> assets = assetsService.getNewestAssets();
        for(Assets asset:assets){
            if(StringUtil.isEmpty(cur_points)){
//                AssetsCost cos=new AssetsCost();
//                cos.setCode(asset.getAssets_code());
//                cos.setCost_price(asset.getLastest_price()+"");
//                cos.setDate(cur_date);
//                cos.setUid(uid);
//                assetsService.saveOrUpdateCost(cos);
            }else{
                String curPoint=String.valueOf(Integer.parseInt(cur_points)-Integer.parseInt(point));
                assetsService.calETFCost(oid,uid,curPoint,point,asset);
            }
        }
    }

    /**
     * 获取当前成本价
     * @return
     */
    @GetMapping("/assets/etf_cost_price")
    @ResponseBody
    public ResponseBaseDTO getCurCostPrice(HttpServletRequest request) throws Exception {
        String token=request.getParameter("token");
        String uid = tokenUtil.getProperty(token, "uid");
//        String uid=token;
        if(StringUtil.isEmpty(uid)) return new ResponseBaseDTO("error","token有误");
        List<AssetsCost> costs=assetsService.getAssetsCost(uid);
        if(costs.size()==0){
            List<AssetsCost> costs_=assetsService.saveOrUpdateCost(uid);
            logger.info("查询当前成本价的日期{}",costs_.get(0).getDate());
            return new ResponseBaseDTO("success",costs_);
        }else{
            logger.info("查询当前成本价的日期{}",costs.get(0).getDate());
            return new ResponseBaseDTO("success",costs);
        }
     }
    /**
     * 生成etf的盈亏
     */
    @GetMapping("/assets/etf_profit")
    @ResponseBody
    public ResponseBaseDTO getETFPorfit(HttpServletRequest request) throws Exception {
        String token=request.getParameter("token");
        String uid = tokenUtil.getProperty(token, "uid");
//        String uid=token;
        if(StringUtil.isEmpty(uid)) return new ResponseBaseDTO("error","token有误");
        List<Assets> assets = assetsService.getNewestAssets();
        List<EtfPL> pls=new ArrayList<>();
        for(Assets asset:assets){
            EtfPL pl= assetsService.calEtfPL(uid,asset);
            pls.add(pl);
        }
        return new ResponseBaseDTO("success",pls);
    }

}
