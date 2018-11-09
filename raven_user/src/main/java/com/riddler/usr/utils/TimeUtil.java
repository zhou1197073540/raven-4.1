package com.riddler.usr.utils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import static java.time.ZoneId.SHORT_IDS;

/**
 * Created by daniel.luo on 2017/5/25.
 */
public class TimeUtil {
    public static String convertTime(String oriTime) {
        LocalDate fmtTime = null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH);
            fmtTime = LocalDate.parse(oriTime, formatter);
            System.out.printf("Successfully parsed String %s, date is %s%n", oriTime, fmtTime);
        } catch (DateTimeParseException ex) {
            System.out.printf("%s is not parsable!%n", oriTime);
            ex.printStackTrace();
        }
        if (fmtTime == null) return "";
        else return fmtTime.toString();
    }

    public static LocalDate convertDateTime(String oriTime) {
        LocalDate fmtTime = null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH);
            fmtTime = LocalDate.parse(oriTime, formatter);
            System.out.printf("Successfully parsed String %s, date is %s%n", oriTime, fmtTime);
        } catch (DateTimeParseException ex) {
            System.out.printf("%s is not parsable!%n", oriTime);
            ex.printStackTrace();
        }
        if (fmtTime == null) return null;
        else return fmtTime;
    }

    /**
     * 把一个字符串类型转换成下一天的date
     *
     * @param oriDate 如："2017-07-24"
     * @return 如"2017-07-25"
     */
    public static String nextDate(String oriDate) {
        LocalDate fmtTime = null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            fmtTime = LocalDate.parse(oriDate, formatter);
        } catch (DateTimeParseException ex) {
            System.out.printf("%s is not parsable!%n", oriDate);
            ex.printStackTrace();
        }
        return fmtTime.plusDays(1).toString();
    }

    /**
     * @param delta （因为美股收盘的时候是凌晨四点，我们当时已经是第二天）所以添加天的偏移量delta，-1代表前一天，0代表当天，1代表后一天
     * @return dateStr
     */
    public static String getDate(int delta) {
        LocalDate today = LocalDate.now();
        if (delta < 0) return today.minusDays(Math.abs(delta)).toString();
        else if (delta > 0) return today.plusDays(delta).toString();
        else return today.toString();
    }

    public static String getDateByTimeZone(String timeZone) {
        LocalDate date = LocalDate.now(ZoneId.of(SHORT_IDS.get(timeZone)));
        return date.toString();
    }

    /**
     * 当前日期返回 yyyy-MM-dd HH:mm:ss 格式
     *
     * @return 例如：2017-06-15 15:50:43
     */
    public static String getDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime time = LocalDateTime.now();
        return time.format(formatter);
    }

    /**
     * 当前日期返回 yyyy-MM-dd 格式
     *
     * @return 例如：2017-06-15
     */
    public static String getCurDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime time = LocalDateTime.now();
        return time.format(formatter);
    }

    /**
     * 获取当前的时间戳，精确到毫秒如：1497926312513
     *
     * @return
     */
    public static String getTimeStamp() {
        Timestamp ts = Timestamp.valueOf(LocalDateTime.now());
        return String.valueOf(ts.getTime());
    }

    public static long getTimeStampAsLong() {
        return Timestamp.valueOf(LocalDateTime.now()).getTime();
    }

    public static void sleep(long miliseconds) {
        try {
            Thread.sleep(miliseconds);
        } catch (Exception e) {

        }
    }

    /**
     *
     * @param date 2018-05-29
     * @param date  1(下一天),-1(上一天)
     * @return
     */
    public static String getDateByAdd(String date,int add){
     return null;
    }


    public static void main(String[] args) {
        System.out.println(convertTime("2018-05-29"));
    }
}
