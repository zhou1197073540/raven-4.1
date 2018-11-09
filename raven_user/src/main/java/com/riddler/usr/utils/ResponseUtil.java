package com.riddler.usr.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResponseUtil{
	public final static Pattern name_pattern=Pattern.compile("(\\d+)(.*?)具有");
	public final static Pattern hot_pattern=Pattern.compile("具有:(.*?)在所属");
	public final static Pattern valuation_pattern=Pattern.compile("(在所属.*?)公司财务");
	public final static Pattern base_plane_pattern=Pattern.compile("(公司财务.*?)(当前|无分析师)");
	public final static Pattern analysis_result_pattern=Pattern.compile("((当前.*?分析师|无分析师).*?)近期资金");
	public final static Pattern fund_flow_pattern=Pattern.compile("(近期资金.*?)量化模型");
	public final static Pattern rate_pattern=Pattern.compile("量化模型(.*)");

	public static Map<String, String> strToMap(String evaluation) {
		Map<String, String> map=new HashMap<String, String>();
		try {
			Matcher name_matcher=name_pattern.matcher(evaluation);
			if(name_matcher.find()){
				
				map.put("stock_num", StringUtil.formateStr(name_matcher.group(1)));
				map.put("stock_name", StringUtil.formateStr(name_matcher.group(2)));
			}
			Matcher hot_matcher=hot_pattern.matcher(evaluation);
			if(hot_matcher.find()) map.put("hot", StringUtil.formateStr(hot_matcher.group(1)));
			
			Matcher valuation_matcher=valuation_pattern.matcher(evaluation);
			if(valuation_matcher.find()) StringUtil.formateStr(map.put("valuation", valuation_matcher.group(1)));
			
			Matcher base_matcher=base_plane_pattern.matcher(evaluation);
			if(base_matcher.find()) StringUtil.formateStr(map.put("base_plane", base_matcher.group(1)));
			
			Matcher analysis_matcher=analysis_result_pattern.matcher(evaluation);
			if(analysis_matcher.find()) StringUtil.formateStr(map.put("analysis_result", analysis_matcher.group(1)));
			
			Matcher fund_matcher=fund_flow_pattern.matcher(evaluation);
			if(fund_matcher.find()) StringUtil.formateStr(map.put("fund_flow", fund_matcher.group(1)));
			
			Matcher rate_matcher=rate_pattern.matcher(evaluation);
			if(rate_matcher.find()) StringUtil.formateStr(map.put("rate", rate_matcher.group(1)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	
	
}
