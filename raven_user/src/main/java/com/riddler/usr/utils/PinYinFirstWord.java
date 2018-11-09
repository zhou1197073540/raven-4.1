package com.riddler.usr.utils;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.io.UnsupportedEncodingException;

public class PinYinFirstWord {
    public static String toPinyin(String str){
        String convert = "";
        for (int j = 0,len = str.length(); j < len; j++) {
            char word = str.charAt(j);
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
            if (pinyinArray != null) {
                convert += pinyinArray[0].charAt(0);
            } else {
                convert += word;
            }
        }
        return convert;
    }

    public static void main(String[] args) {
        PinYinFirstWord cte = new PinYinFirstWord();
        System.out.println("获取拼音首字母："+ cte.toPinyin("周晨洗"));
    }
}
