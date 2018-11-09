package com.raven.utils;

import java.security.MessageDigest;
import java.util.Random;

public class HashUtil {
    /**
     * 用MD5加密成  账单号
     *
     * @param strs
     * @return
     */
    public static String EncodeByMD5(String strs) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            byte[] btInput = strs.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 生成用户交易记录的流水单号
     *
     * @param uid  用户uid
     * @param type 交易类型：0:注册,1:充值, 2:提现, 3:卖出fyb,4:买入fyb, 5:赌博花费 6:赌博赚钱
     * @return
     */
    public static String generateSerialNum(String uid, String type) {
        String ts = TimeUtil.getTimeStamp();
        return EncodeByMD5(uid + ts + type);
    }

    /**
     * 生成长度为size的随机数
     *
     * @return
     */
    public static String generateRandomStr(int size) {
        try {
            if (size > 32) throw new Exception("size的长度不能超过32..");
            int randomNum = new Random().nextInt(999);
            String md5Str = EncodeByMD5(randomNum + TimeUtil.getTimeStamp());
            return md5Str.substring(0, size);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 返回两个0-9的随机数
     * @return
     */
    public static String generateTwoRandom() {
        int randomNum1 = new Random().nextInt(10);
        int randomNum2 = new Random().nextInt(10);
        if (randomNum1 == 0 && randomNum2 == 0) randomNum2 += 1;
        return randomNum1 + "" + randomNum2;
    }

    public static void main(String[] args) {
        System.out.println(generateSerialNum("18770076907", "0"));
        ;
    }

}
