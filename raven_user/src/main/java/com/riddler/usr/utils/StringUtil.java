package com.riddler.usr.utils;

import org.apache.commons.lang.StringUtils;

public class StringUtil {

    public static boolean isEmpty(String str){
        if(str==null||str.trim().isEmpty()) return true;
        return false;
    }
    public static boolean isEmpty(String... strs){
        boolean isEmpty=false;
        for(String str:strs){
            if(isEmpty(str)){
                isEmpty=true;
                break;
            }
        }
        return isEmpty;
    }

    public static String formateStr(String con) {
        if (StringUtils.isBlank(con)) return null;
        if (con.endsWith(".")) {
            con = con.substring(0, con.length() - 1);
        }
        return con.replace(".", ",");
    }

    public static boolean isNotEmpty(String str) {
        if (str != null && !"".equals(str)) {
            return true;
        }
        return false;
    }
    public static String formatStockCode(String code,String stockname){
        if(isEmpty(code)) return null;
        if(code.startsWith("sz")||code.startsWith("sh")) return code;
        String code_type="unknown";
        if(code.startsWith("0")||code.startsWith("3")){
            if(stockname.equals("上证指数")||code.equals("000300")||stockname.equals("中证500")||
                    stockname.equals("中证800")||stockname.equals("上证50")){
                code_type="sh"+code;
            }else{
                code_type="sz"+code;
            }
        }else if(code.startsWith("6")){
            code_type="sh"+code;
        }
        return code_type;
    }

    public static String rmStockCode(String code){
        if(isEmpty(code)) return null;
        if(code.equals("sh000905")||code.equals("sh000906")||code.equals("sh000016")||
                code.equals("sz399006")||code.equals("sz399005")||code.equals("sz399001")
                ||code.equals("sh000300")||code.equals("sh000001")){
            return null;
        }
        if(code.startsWith("sh")||code.startsWith("sz")){
            return code.substring(2,code.length());
        }
        return code;
    }

    public static void main(String[] args) {
        System.out.println(rmStockCode("sz123456"));
    }
}
