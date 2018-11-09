package com.riddler.usr.utils;

import com.alibaba.fastjson.JSONObject;
import com.riddler.usr.bean.AccountRecords;
import com.riddler.usr.bean.Assets;
import com.riddler.usr.bean.FundQuarter;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DataUtil {
	
	public static Map<String,Object> generateMap(String[] keys,Object[] vals){
		if(null==keys||null==vals||keys.length!=vals.length) return null;
		Map<String,Object> map=new HashMap<String, Object>();
		for(int i=0;i<keys.length;i++){
			map.put(keys[i]+"", vals[i]);
		}
		return map;
	}
    public static Map<String,String> generateMap(String[] keys,String[] vals){
        if(null==keys||null==vals||keys.length!=vals.length) return null;
        Map<String,String> map=new HashMap<String, String>();
        for(int i=0;i<keys.length;i++){
            map.put(keys[i]+"", vals[i]);
        }
        return map;
    }
	
	/**
	 * @param assets 买入的etf资产
	 * @return 计算出1个沣沅币价值的美元
	 */
	public static float calculateOneFybToDollar(List<Assets> assets) {
		if(assets.size()==0) return 0;
		float assetsPrice=0;
		for(Assets as:assets){
			assetsPrice+=as.getLastest_price()*as.getWeight();
		}
		return assetsPrice;
	}
	/**
	 * 计算人民币和在个人资产中的所占比率
	 * @param rmb 个人可支配的人民币
	 * @return
	 */
	public static float caculateRMBRate(String rmb, float dollarToRMB) {
		float rmbs=Float.parseFloat(rmb);
		return rmbs/(dollarToRMB+rmbs);
	}
	
	public static Map<String,String> generateMap(String... strs){
		Map<String,String> map=new HashMap<String, String>();
 		for(String str:strs){
 			if(str.contains(":")){
 				String[] ns=str.split(":");
 				map.put(ns[0], ns[1]);
 			}
		}
		return map;
	}

    public static String formatComment(String comment) {
	    if(null==comment) return null;
	    if("0".equals(comment)){
	        return "注册";
        }else if("1".equals(comment)){
            return "积分充值";
        }else if("2".equals(comment)){
            return "积分提现";
        }else if("3".equals(comment)){
            return "资产积分充值";
        }else if("4".equals(comment)){
            return "资产积分提现";
        }else if("5".equals(comment)){
            return "预测积分冻结";
        }else if("6".equals(comment)){
            return "预测成功";
        }else if("7".equals(comment)){
            return "登录";
        }else if("8".equals(comment)){
            return "签到";
        }else if("9".equals(comment)){
            return "留言";
        }else if("10".equals(comment)){
            return "积分转资产积分";
        }else if("11".equals(comment)){
            return "机器人辅助扣分";
        }else if("12".equals(comment)){
            return "参加选股活动扣分";
        }else if("13".equals(comment)){
            return "选股活动分钱";
        }else if("50".equals(comment)){
            return "预测失败";
        }else if("50".equals(comment)){
            return "预测积分解冻";
        }else if("201".equals(comment)){
            return "积分转入资产积分";
        }else if("202".equals(comment)){
            return "资产积分转入积分";
        }else if("203".equals(comment)){
            return "资产积分收益变化值";
        }
        return "其他";
    }

    public static String getValfromJsonstr(String jsonstr,String key){
	    try {
            JSONObject object= (JSONObject) JSONObject.parse(jsonstr);
            if(object.containsKey(key)){
                return object.getString(key);
            }
        }catch (Exception e){
            return "";
        }
        return "";
    }


    public static float addNetValueProportion(List<FundQuarter> list) {
        if (list.size()==0) return 0;
        float total_net_value=0;
        for (FundQuarter jj : list) {
            jj.setQuarter_type(quarter2date(jj.getQuarter_type()));
            float jj_net_value = Float.parseFloat(jj.getNet_value_proportion().replace("%",""));
            total_net_value += jj_net_value;
        }
        BigDecimal total_value =new BigDecimal(total_net_value);
        return  total_value.setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();
    }
    public static String quarter2date(String quarter){
        if(StringUtils.isBlank(quarter)) return "";
        String year=quarter.substring(0,4);
        String month=quarter.substring(4,quarter.length());
        if("1".equals(month)){
            return year+"-03-31";
        }else if("2".equals(month)){
            return year+"-06-30";
        }else if("3".equals(month)){
            return year+"-09-30";
        }else if("4".equals(month)){
            return year+"-12-31";
        }else{
            return "";
        }
    }

    public static void main(String[] args){
        String quarter="20171";
        String year=quarter.substring(0,4);
        String month=quarter.substring(4,quarter.length());
        System.out.println(year);
        System.out.println(month);
    }
}




