package com.riddler.usr.service;

import java.math.BigDecimal;
import java.util.*;

import com.riddler.usr.bean.*;
import com.riddler.usr.dto.RespDto;
import com.riddler.usr.dto.ResponseBaseDTO;
import com.riddler.usr.mapper.AssetsMapper;
import com.riddler.usr.utils.HashUtil;
import com.riddler.usr.utils.StringUtil;
import com.riddler.usr.utils.TimeUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class AsstesService {
    private static final Logger logger = LoggerFactory.getLogger(AsstesService.class);
    @Autowired
    AssetsMapper assetsMapper;


    public Object getData() {
        return null;
    }

    public String getRmbRate() throws Exception {
        return assetsMapper.getRate();
    }

    public List<Assets> getListAssets() throws Exception {
        return assetsMapper.getListAssets();
    }

    public List<DailyRecordVO> selectFybHistory(String uid) throws Exception {
        return assetsMapper.selectFybHistory(uid);
    }

    public List<DailyRecordVO> selectRMBHistory(String uid) throws Exception {
        return assetsMapper.selectRMBHistory(uid);
    }

    public List<DailyRecordVO> selectCurDate() throws Exception {
        return assetsMapper.selectCurDate();
    }

    public void insertOrUpdate(DailyRecordVO vo) throws Exception {
        if (findByUidInFybRecord(vo) <= 0) {
            assetsMapper.insertFybVO(vo);
            System.out.println(vo.getUid() + "用户计算完成，已入库");
        } else {
            System.out.println(vo.getUid() + " 用户计算完成，库中已存在。");
        }
    }

    public int findByUidInFybRecord(DailyRecordVO vo) throws Exception {
        return assetsMapper.findByUidInFybRecord(vo);
    }

    public int getTotalFyb() throws Exception {
        return assetsMapper.getTotalFyb();
    }

    public float cacluteUsa_ETF_Price(Assets ass) throws Exception {
        List<Assets> as = assetsMapper.selectUsa_ETF_Price(ass);
        if (as.size() == 2) {
            BigDecimal b1 = new BigDecimal(Float.toString(as.get(0).getLastest_price()));
            BigDecimal b2 = new BigDecimal(Float.toString(as.get(1).getLastest_price()));
            return b1.subtract(b2).floatValue();
        }
        return 0;
    }

    /**
     * 计算总的ETF的盈亏
     * [计算方式：从沣沅币交易后为0开始，记录用户的沣沅币的变化（包括了对赌），
     * 成本价 PP1：(P1V1+P2V2-P3V3)/(V1+V2-V3)其中，P:价格，V：数量  P1、P2买入，P3卖出
     * ETF（例如：QQQ，SPY等）盈亏计算PP2：w1*p1+w2*p2 ,w:份额比率 ，PL:价格
     * 盈亏PL:(PP2-PP1)/(V1+V2-V3)  如果V1+V2-V3=0，值就显示为0
     * ]
     *
     * @param assets
     * @param PP2    一个沣沅币的价格
     * @return 盈亏的数量
     */
    public float caculateAllProfitOrLoss(List<Assets> assets, float PP2, String uid) throws Exception {
        List<DailyRecordVO> userRecodes = getAllFYBRecord(uid);
        float[] result = cacuTotalPP1(userRecodes);
        if (result[1] != 0) {
            BigDecimal result0_ = new BigDecimal(Float.toString(result[0]));
            BigDecimal result1_ = new BigDecimal(Float.toString(result[1]));
            float PP1 = result0_.divide(result1_, 4, BigDecimal.ROUND_HALF_UP).floatValue();
            BigDecimal res = new BigDecimal(Float.toString(PP2)).subtract(new BigDecimal(Float.toString(PP1)));
            return res.divide(result1_, 4, BigDecimal.ROUND_HALF_UP).floatValue();
        }
        return 0;
    }

    /**
     * 计算单个ETF盈亏情况
     * 计算方式：(当前价格-成本价)*权重
     * 成本价 PP1：(P1V1+P2V2-P3V3)/(V1+V2-V3)其中，P:价格，V：数量  P1、P2买入，P3卖出
     *
     * @param assets 单个ETF资产,例如：QQQ
     * @param uid    用户uid
     * @return
     */
    public float caculateOneProfitOrLoss(Assets assets, String uid) throws Exception {
        float PP1 = calculateOneETFCost(assets, uid);
        if (PP1 == 0) {
            System.out.println(assets + "=======0");
            return 0;
        }
        BigDecimal price_ = new BigDecimal(Float.toString(assets.getLastest_price()));
        BigDecimal PP1_ = new BigDecimal(Float.toString(PP1));
        BigDecimal weight_ = new BigDecimal(Float.toString(assets.getWeight()));
        BigDecimal res = price_.subtract(PP1_);
        return res.multiply(weight_).floatValue();
    }

    /**
     * 计算一个ETF的历史成本价
     *
     * @return
     */
    public float calculateOneETFCost(Assets assets, String uid) throws Exception {
        List<DailyRecordVO> userRecodes = getAllFYBRecord(uid);
        float[] result = cacuTotalPP1(userRecodes);
        float PP1 = 0;//每个ETF的成本价
        if (result[1] != 0) {
            BigDecimal result0_ = new BigDecimal(Float.toString(result[0]));
            BigDecimal result1_ = new BigDecimal(Float.toString(result[1]));
            PP1 = result0_.divide(result1_, 4, BigDecimal.ROUND_HALF_UP).floatValue();
        } else {
            return 0;
        }
        return PP1;
    }

    private float[] cacuTotalPP1(List<DailyRecordVO> userRecodes) throws Exception {
        float PP1_total = 0;
        int num1 = 0;
        for (DailyRecordVO vo : userRecodes) {
            if ("3".equals(vo.getComment())) {
                PP1_total += vo.getUnit_price() * vo.getFy_change();
                num1 += vo.getFy_change();
            } else if ("4".equals(vo.getComment())) {
                PP1_total -= vo.getUnit_price() * vo.getFy_change();
                num1 -= vo.getFy_change();
            }
        }
        float[] total = new float[2];
        total[0] = PP1_total;
        total[1] = num1;
        return total;
    }

    /**
     * 找出用户从0开始的购买记录(user_daily_record，wallet_change_log)
     *
     * @return
     */
    private List<DailyRecordVO> getAllFYBRecord(String uid) throws Exception {
        int id = assetsMapper.findFYBZero(uid);
        if (id <= 0) {
            return assetsMapper.findAllUserBuySaleRecord(uid);
        } else {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("uid", uid);
            map.put("id", id);
            return assetsMapper.findZeroUserBuySaleRecord(map);
        }
    }

    /**
     * 找出用户从0开始的购买记录(fyb_history_price，wallet_change_log)
     *
     * @return
     */
    private List<DailyRecordVO> getFybFromZero(String uid) throws Exception {
        int id = assetsMapper.findFYBZero(uid);
        if (id <= 0) {
            return assetsMapper.findAllUserBuySaleRecord(uid);
        } else {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("uid", uid);
            map.put("id", id);
            return assetsMapper.findZeroUserBuySaleRecord(map);
        }
    }

    /**
     * 获取最新的购买的资产ETF价格
     *
     * @return
     */
    public List<Assets> getNewestAssets() throws Exception {
        return assetsMapper.getNewestAssets();
    }

    public void saveOrUpdate(Map<String, String> map) throws Exception {
        int num = assetsMapper.selectFybPrice(map);
        if (num <= 0) {
            assetsMapper.insertFybPrice(map);
            System.out.println(map.get("date") + "==" + map.get("fyb_price") + ",--->入库");
        } else {
            assetsMapper.updateFybPrice(map);
            System.out.println(map.get("date") + "==" + map.get("fyb_price") + ",--->已存在");
        }
    }

    /**
     * @param uid
     * @return 获取用户历史数据
     */
    public List<DailyRecordVO> selectHistory(String uid) throws Exception {
        List<String> dates = assetsMapper.selectDistDates(uid);
        List<DailyRecordVO> list = new ArrayList<DailyRecordVO>();
        if (dates.size() > 0) {
            for (String date : dates) {
                DailyRecordVO vo = assetsMapper.selectHistoryData(uid, date);
                if (null != vo) list.add(vo);
            }
        }
        if (list.size() <= 0) {
            //还没有生成当天的沣沅币的价格（也就是刚刚注册的用户，但是fyb_history_price没有生成记录）
            DailyRecordVO vo = assetsMapper.selectDefaultDate(uid);
            if (null != vo) list.add(vo);
        }
        if (list.size() <= 0) {
            //如果还没有数据，就给一条死数据
            DailyRecordVO vo = new DailyRecordVO();
            vo.setDate(TimeUtil.getCurDateTime());
            vo.setUnit_price(120.00);
            list.add(vo);
        }
        return list;
    }
    /*=========================再次改版后的代码================================*/

    /**
     * 计算从买入的时候到当前日期的盈亏情况（净利润）
     * 计算方式：将当前的ETF价格/买入ETF时的价格  的结果在乘以买入时改ETF的总价格
     * 需注意的是：etf只会记录单价，而买的是总价，总价的变化是跟据单价的比率改变而改变的（强调注意）
     *
     * @return 返回比率和当前的净利润
     */
    public MoneyWeight caculateProfitLost(String uid, Assets asset) {
        float cur_price = asset.getLastest_price();
        String date = assetsMapper.selectAssetsPointPutTime(uid);//查找转入时间
        if (StringUtils.isBlank(date)) return null;
        asset.setBuy_time(date);
        float buy_price = assetsMapper.selectPriceFromEtfPrice(asset);  //买入时的价格
        BigDecimal rate = new BigDecimal(cur_price).divide(new BigDecimal(buy_price), 4, BigDecimal.ROUND_HALF_EVEN)
                .subtract(new BigDecimal(1));
        //计算当前盈亏（算的是净值）

        float cur_money = new BigDecimal(asset.getBuy_time_money()).multiply(rate).floatValue();
//        System.out.println(cur_price+","+buy_price+","+cur_money);

        MoneyWeight weight = new MoneyWeight();
        weight.setRate(rate.add(new BigDecimal(1)).floatValue());
        weight.setProfit(cur_money);
        weight.setCode(asset.getAssets_code());
        weight.setBuy_time_price(buy_price + "");
        return weight;
    }


    /**
     * 计算每天ETF总价值和上一个交易日的总价值的比率，得到的比率用来计算积分
     *
     * @return
     */
    public List<AssetsRateVO> selectAssetsRateRecords(String uid, RespDto dto) {
        String date = assetsMapper.selectAssetsPointPutTime(uid);//查找转入时间
        if (StringUtils.isBlank(date)) return null;
        List<AssetsRateVO> vos = assetsMapper.selectAssetsRateRecords(date);
        for (AssetsRateVO vo : vos) {
            //将买入资产的时间换成用户积分转入资产积分的时间
            if (StringUtils.isNotBlank(date)) vo.setBuy_time(date);
        }
        Map<String, BigDecimal> money_rate_map = calTotalMenoyRate(vos);
        //计算从开始买，每日递增的积分
        Set<String> keys = money_rate_map.keySet();
        Iterator<String> iterator = keys.iterator();
        List<AssetsRateVO> final_result = new ArrayList<>();
        while (iterator.hasNext()) {
            AssetsRateVO avo = new AssetsRateVO();
            String key = iterator.next();
            avo.setDate(key);
            avo.setBuy_time_money(money_rate_map.get(key).toString());
            final_result.add(avo);
        }
        calPointByTotaMoneyRate(final_result, dto);//根据排好序列的list，计算每天的points
        return final_result;
    }

    /**
     * 根据排好序列的list，计算每天的points
     * 计算方式是：今天总钱/昨天的总钱，结果去乘以今天的point，得到的就是point相对应的值
     */
    private void calPointByTotaMoneyRate(List<AssetsRateVO> final_result, RespDto dto) {
        if (final_result.size() <= 1) return;
        String points = dto.getData().toString();
        final_result.get(0).setPoints(points);
        for (int i = 1; i < final_result.size(); i++) {
            try {
                AssetsRateVO cur_etf = final_result.get(i);// 交易日的价格
                AssetsRateVO last_etf = final_result.get(i - 1); // 上一个交易日的价格
                if (StringUtils.isEmpty(cur_etf.getDate()) || StringUtils.isEmpty(last_etf.getDate()))
                    continue;
                int numDay = cur_etf.getDate().compareTo(last_etf.getDate());
                if (numDay == 3 || numDay == 1) {
                    BigDecimal cur_price = new BigDecimal(cur_etf.getBuy_time_money());
                    BigDecimal last_price = new BigDecimal(last_etf.getBuy_time_money());
                    BigDecimal rate = cur_price.divide(last_price, 4, BigDecimal.ROUND_HALF_EVEN);
                    final_result.get(i).setTotal_money_rate(rate.toString());
                    final_result.get(i).setPoints(rate.multiply(new BigDecimal(last_etf.getPoints()))
                            .setScale(4, BigDecimal.ROUND_HALF_UP).toString());
                } else {
                    BigDecimal cur_price = new BigDecimal(cur_etf.getBuy_time_money());
                    BigDecimal last_price = new BigDecimal(last_etf.getBuy_time_money());
                    BigDecimal rate = cur_price.divide(last_price, 4, BigDecimal.ROUND_HALF_EVEN);
                    BigDecimal avg_rate_before = rate.subtract(new BigDecimal(1));
                    BigDecimal avg_rate = avg_rate_before.divide(new BigDecimal(numDay), 4, BigDecimal.ROUND_HALF_EVEN);
                    BigDecimal avg_rate_after = avg_rate.add(new BigDecimal(1));
                    final_result.get(i).setTotal_money_rate(avg_rate_after.toString());
                    final_result.get(i).setPoints(avg_rate_after.multiply(new BigDecimal(last_etf.getPoints()))
                            .setScale(4, BigDecimal.ROUND_HALF_UP).toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private Map<String, BigDecimal> calTotalMenoyRate(List<AssetsRateVO> vos) {
        Map<String, BigDecimal> map = new TreeMap<String, BigDecimal>(
                new Comparator<String>() {
                    public int compare(String obj1, String obj2) {//升序排序
                        return obj1.compareTo(obj2);
                    }
                });
        if (vos.size() > 0) {
            for (AssetsRateVO vo : vos) {
                try {
                    if (vo.getDate().equals(vo.getBuy_time())) {
                        BigDecimal total_num = map.get(vo.getDate());
                        if (null == total_num) total_num = new BigDecimal(0);
                        map.put(vo.getDate(), new BigDecimal(vo.getBuy_time_money()).add(total_num));
                    } else {
                        if (map.containsKey(vo.getDate())) {
                            BigDecimal rate = new BigDecimal(vo.getRate()).add(new BigDecimal(1));
                            BigDecimal cur_num = rate.multiply(new BigDecimal(vo.getBuy_time_money()));//当前vo的涨跌的价格
                            BigDecimal total_num = map.get(vo.getDate()); //当天所有vo的涨跌总和
                            map.put(vo.getDate(), cur_num.add(total_num));
                        } else {
                            BigDecimal rate = new BigDecimal(vo.getRate()).add(new BigDecimal(1));
                            BigDecimal cur_num = rate.multiply(new BigDecimal(vo.getBuy_time_money()));//当前vo的涨跌的价格
                            BigDecimal total_num = new BigDecimal("0"); //开始map中没有，为0
                            map.put(vo.getDate(), cur_num.add(total_num));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }

    /**
     * 计算每个ETF钱的比重
     */
    public List<Map<String, String>> cauETFManeyRate(List<MoneyWeight> list_moneyweight) {
        if (list_moneyweight.size() <= 0) return null;
        float total_rate = 0;
        for (MoneyWeight one : list_moneyweight) {
            total_rate += one.getRate();
        }
        List<Map<String, String>> list_map = new ArrayList<>();
        for (MoneyWeight one : list_moneyweight) {
            Map<String, String> map = new HashMap<>();
            BigDecimal rate = new BigDecimal(one.getRate()).divide(new BigDecimal(total_rate), 4, BigDecimal.ROUND_HALF_EVEN);
            map.put(one.getCode(), rate.toString());
            list_map.add(map);
        }
        return list_map;
    }

    /**
     * 将第二个map中的code值合并到第一个map中
     */
    public void mergeData(List<Map<String, String>> list_map, List<Map<String, String>> etfMoneyRate) {
        if (list_map != null && etfMoneyRate != null) {
            for (Map<String, String> one : list_map) {
                String code = one.get("code");
                a:
                for (Map<String, String> two : etfMoneyRate) {
                    if (two.containsKey(code)) {
                        one.put("money_percent", two.get(code));
                        break a;
                    }
                }
            }
        }
    }

    public void mergeDatas(List<Map<String, String>> list_map, UserAssetsPoints one_user, BigDecimal rate) {
        if (list_map != null) {
            for (Map<String, String> one : list_map) {
                String code = one.get("code");
                if (one_user.getCode().equals(code)) {
                    one.put("money_percent", rate.toString());
                    break;
                }
            }
        }
    }

    public List<UserAssetsPoints> selectAssetsHistoryRecords(String uid) {
        List<UserAssetsPoints> list=assetsMapper.selectAssetsHistoryRecords(uid);
        Collections.reverse(list);
        return list;
    }

    public float selectSumAddUp() {
        return assetsMapper.selectSumAddUp();
    }

    public List<UserAssetsPoints> selectPerEtfTotalSum() {
        return assetsMapper.selectPerEtfTotalSum();
    }

    public String getCurUserAssetsPoints(String uid) {
        return assetsMapper.getCurUserAssetsPoints(uid) + "";
    }

    /**
     * 计算规则是：(a0+a1)*P0P1/(a0P1+a1P0)
     *
     * @param uid
     * @param cur_points 当前用户资产积分
     * @param points     此次用户转移的资产积分
     * @param asset
     * @return
     */
    public AssetsCost calETFCost(String oid, String uid, String cur_points, String points, Assets asset) {
        String cur_date = TimeUtil.getDate(-1);//当前的日期是用昨天的，因为今天的etf股票没有出现，所以用昨天的
        String news_price = String.valueOf(asset.getLastest_price()); //etf最新价格P1
        AssetsCost cost = assetsMapper.selectNewsCostByUid(uid, asset.getAssets_code());
        String news_date = "", buy_price = "";//上一次计算成本后的日期
        if (cost == null) {
            String changeDate=assetsMapper.selectWalletChangeLogByUid(uid);
            if(changeDate==null){
                news_date = asset.getBuy_time();
            }else{
                news_date = changeDate;
            }
            buy_price = assetsMapper.getETFBuyPrice(asset.getAssets_code(), news_date);//上一次计算成本的价格P0
        } else {
            buy_price = cost.getCost_price();
        }
        String costfinal = calCost(cur_points, buy_price, points, news_price);
        AssetsCost cost0 = new AssetsCost();
        cost0.setCode(asset.getAssets_code());
        cost0.setCost_price(costfinal);
        cost0.setDate(cur_date);
        cost0.setUid(uid);
        cost0.setOid(oid);
        cost0.setInsert_time(TimeUtil.getDateTime());
        if (costfinal != null) {
            assetsMapper.insertCost(cost0);
            //        saveOrUpdateCost(cost0);
        }
        return cost0;
    }

    public boolean saveOrUpdateCost(AssetsCost cost) {
        AssetsCost cc = assetsMapper.selectCostByUidCode(cost);
        if (cc == null) {
            assetsMapper.insertCost(cost);
        } else {
            assetsMapper.updateCost(cc);
        }
        return true;

    }

    private String calCost(String cur_points, String buy_price, String points, String news_price) {
        if (StringUtil.isEmpty(cur_points, buy_price, points, news_price)) return null;
        BigDecimal curPoint = new BigDecimal(cur_points);//a0
        BigDecimal buyPrice = new BigDecimal(buy_price);//p0
        BigDecimal point = new BigDecimal(points);//a1
        BigDecimal newsPrice = new BigDecimal(news_price);//P1
        BigDecimal c1 = curPoint.add(point).multiply(buyPrice).multiply(newsPrice);
        BigDecimal cc1 = curPoint.multiply(newsPrice);
        BigDecimal cc2 = point.multiply(buyPrice);
        return c1.divide(cc1.add(cc2), 2, BigDecimal.ROUND_HALF_EVEN).toString();
    }

    public List<AssetsCost> getAssetsCost(String uid) {
        return assetsMapper.getAssetsCost(uid);
    }

    public List<AssetsCost> saveOrUpdateCost(String uid) throws Exception {
        List<Assets> assets=assetsMapper.getNewestAssets();
        if (assets.size()<=0){
            throw new Exception("查询最新资产价格出问题");
        }
        String date=assetsMapper.selectWalletChangeLogByUid(uid);
        if(StringUtil.isEmpty(date)){
            date=assets.get(0).getBuy_time();
        }
        List<AssetsCost> costs=new ArrayList<>();
        String ooid=HashUtil.EncodeByMD5(TimeUtil.getTimeStamp()+"临时生成的oid，因为查询要用到");
        for(Assets ass:assets){
            String buy_price = assetsMapper.getETFBuyPrice(ass.getAssets_code(), date);//上一次计算成本的价格P0
            if(StringUtil.isEmpty(buy_price)){
                buy_price = assetsMapper.getETFBuyPrice(ass.getAssets_code(), ass.getBuy_time());//上一次计算成本的价格P0
            }
            AssetsCost cost0 = new AssetsCost();
            cost0.setCode(ass.getAssets_code());
            cost0.setCost_price(buy_price);
            cost0.setDate(date);
            cost0.setUid(uid);
            cost0.setOid(ooid);
            cost0.setInsert_time(TimeUtil.getDateTime());
            if (buy_price != null) {
                assetsMapper.insertCost(cost0);
            }
            costs.add(cost0);
        }
        return costs;
    }

    public EtfPL calEtfPL(String uid, Assets asset) {
        int cur_points=assetsMapper.getCurUserAssetsPoints(uid);
        AssetsCost cost=assetsMapper.selectCostByUidCodes(uid,asset.getAssets_code());
        if(cost!=null&&cur_points>0){
            logger.info("查询盈亏的日期{}",cost.getDate());
           BigDecimal p0= new BigDecimal(asset.getLastest_price()).subtract(new BigDecimal(cost.getCost_price()));
           BigDecimal n10=new BigDecimal(cur_points).divide(new BigDecimal(10),2,BigDecimal.ROUND_HALF_EVEN);
           BigDecimal n0=n10.divide(new BigDecimal(cost.getCost_price()),2,BigDecimal.ROUND_HALF_EVEN);
           String pf=p0.multiply(n0).setScale(2,BigDecimal.ROUND_HALF_EVEN).toString();
            EtfPL pl=new EtfPL();
            pl.setCode(asset.getAssets_code());
            pl.setPl(pf);
            return pl;
        }
        return null;
    }

}
