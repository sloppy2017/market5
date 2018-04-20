package com.c2b.coin.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * @author <a href="mailto:guo_xp@163.com">guoxinpeng</a>
 */
public class DateUtil {
  /**
   * 定义常量
   **/
  public static final String DATE_JFP_STR = "yyyyMM";
  public static final String DATE_FULL_STR = "yyyy-MM-dd HH:mm:ss";
  public static final String DATE_SMALL_STR = "yyyy-MM-dd";
  public static final String DATE_KEY_STR = "yyyyMMddHHmmss";

  /**
   * 使用预设格式提取字符串日期
   *
   * @param strDate 日期字符串
   * @return
   */
  public static Date parse(String strDate) {
    return parse(strDate, DATE_FULL_STR);
  }

  /**
   * 使用用户格式提取字符串日期
   *
   * @param strDate 日期字符串
   * @param pattern 日期格式
   * @return
   */
  public static Date parse(String strDate, String pattern) {
    SimpleDateFormat df = new SimpleDateFormat(pattern);
    try {
      return df.parse(strDate);
    } catch (ParseException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * 两个时间比较
   *
   * @return
   */
  public static int compareDateWithNow(Date date1) {
    Date date2 = getCurrentDate();
    int rnum = date1.compareTo(date2);
    return rnum;
  }

  /**
   * 两个时间比较(时间戳比较)
   *
   * @param date
   * @return
   */
  public static int compareDateWithNow(long date) {
    long date2 = dateToUnixTimestamp();
    if (date > date2) {
      return 1;
    } else if (date < date2) {
      return -1;
    } else {
      return 0;
    }
  }

  /**
   * 获取系统当前时间
   *
   * @return
   */
  public static String getNowTime() {
    SimpleDateFormat df = new SimpleDateFormat(DATE_FULL_STR);
    return df.format(getCurrentDate());
  }

  /**
   * 获取系统当前时间
   *
   * @return
   */
  public static String getNowTime(String type) {
    SimpleDateFormat df = new SimpleDateFormat(type);
    return df.format(getCurrentDate());
  }

  public static String formateDate(Date date, String type) {
    SimpleDateFormat df = new SimpleDateFormat(type);
    return df.format(date);
  }
  public static String formateFullDate(Date date, Locale locale){
    SimpleDateFormat dateFormat = null;
    if (!locale.toLanguageTag().equals("zh-CN") && !locale.toLanguageTag().equals("zh-TW")){
      dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss 'GMT'",Locale.ENGLISH);
    }else {
      dateFormat = new SimpleDateFormat(DATE_FULL_STR);
    }
    return dateFormat.format(date);
  }
  /**
   * 获取系统当前计费期
   *
   * @return
   */
  public static String getJFPTime() {
    SimpleDateFormat df = new SimpleDateFormat(DATE_JFP_STR);
    return df.format(getCurrentDate());
  }

  /**
   * 将指定的日期转换成Unix时间戳
   *
   * @param  date 需要转换的日期 yyyy-MM-dd HH:mm:ss
   * @return long 时间戳
   */
  public static long dateToUnixTimestamp(String date) {
    long timestamp = 0;
    try {
      timestamp = new SimpleDateFormat(DATE_FULL_STR).parse(date).getTime();
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return timestamp;
  }

  public static long dateStartToUnixTimestamp(){
    String date = formateDate(getCurrentDate(), DATE_SMALL_STR);
    return dateToUnixTimestamp(date.concat(" 00:00:00"));
  }
  public static long dateEndToUnixTimestamp(){
    String date = formateDate(getCurrentDate(), DATE_SMALL_STR);
    return dateToUnixTimestamp(date.concat(" 23:59:59"));
  }

  /**
   * 将指定的日期转换成Unix时间戳
   *
   * @param  date 需要转换的日期 yyyy-MM-dd
   * @return long 时间戳
   */
  public static long dateToUnixTimestamp(String date, String dateFormat) {
    long timestamp = 0;
    try {
      timestamp = new SimpleDateFormat(dateFormat).parse(date).getTime();
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return timestamp;
  }

  /**
   * 将当前日期转换成Unix时间戳
   *
   * @return long 时间戳
   */
  public static long dateToUnixTimestamp() {
    long timestamp = getCurrentTimestamp();
    return timestamp;
  }

  /**
   * 将Unix时间戳转换成日期
   *
   * @param timestamp 时间戳
   * @return String 日期字符串
   */
  public static String unixTimestampToDate(long timestamp) {
    SimpleDateFormat sd = new SimpleDateFormat(DATE_FULL_STR);
    sd.setTimeZone(TimeZone.getTimeZone("GMT+8"));
    return sd.format(new Date(timestamp));
  }

  //获取本周的开始时间
  public static Date getBeginDayOfWeek() {
    Date date = getCurrentDate();
    if (date == null) {
      return null;
    }
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
    if (dayofweek == 1) {
      dayofweek += 7;
    }
    cal.add(Calendar.DATE, 2 - dayofweek);
    return getDayStartTime(cal.getTime());
  }

  //获取本周的结束时间
  public static Date getEndDayOfWeek() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(getBeginDayOfWeek());
    cal.add(Calendar.DAY_OF_WEEK, 6);
    Date weekEndSta = cal.getTime();
    return getDayEndTime(weekEndSta);
  }

  //获取本月的开始时间
  public static Date getBeginDayOfMonth() {
    Calendar calendar = Calendar.getInstance();
    calendar.set(getNowYear(), getNowMonth() - 1, 1);
    return getDayStartTime(calendar.getTime());
  }

  //获取本月的结束时间
  public static Date getEndDayOfMonth() {
    Calendar calendar = Calendar.getInstance();
    calendar.set(getNowYear(), getNowMonth() - 1, 1);
    int day = calendar.getActualMaximum(5);
    calendar.set(getNowYear(), getNowMonth() - 1, day);
    return getDayEndTime(calendar.getTime());
  }

  //获取某个日期的开始时间
  public static Date getDayStartTime(Date d) {
    Calendar calendar = Calendar.getInstance();
    if (null != d) {
      calendar.setTime(d);
    }
    calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return new Date(calendar.getTimeInMillis());
  }

  //获取某个日期的结束时间
  public static Date getDayEndTime(Date d) {
    Calendar calendar = Calendar.getInstance();
    if (null != d) {
      calendar.setTime(d);
    }
    calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
    calendar.set(Calendar.MILLISECOND, 999);
    return new Date(calendar.getTimeInMillis());
  }

  //获取今年是哪一年
  public static Integer getNowYear() {
    Date date = getCurrentDate();
    GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
    gc.setTime(date);
    return Integer.valueOf(gc.get(1));
  }

  //获取本月是哪一月
  public static int getNowMonth() {
    Date date = getCurrentDate();
    GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
    gc.setTime(date);
    return gc.get(2) + 1;
  }

  public static Date getBeginDayOfYesterday() {
    Calendar cal = new GregorianCalendar();
    cal.setTime(getCurrentDate());
    cal.add(Calendar.DAY_OF_MONTH, -1);
    return cal.getTime();
  }

  //获取当月天数
  public static int getCurrentMonthLastDay() {
    Calendar a = Calendar.getInstance();
    a.set(Calendar.DATE, 1);//把日期设置为当月第一天
    a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
    int maxDate = a.get(Calendar.DATE);
    return maxDate;
  }

  public static Integer getDaysByDates(String beginDate, String endDate) {
    try {
      Date begin = new SimpleDateFormat("yyyy-MM-dd").parse(beginDate);
      Date end = new SimpleDateFormat("yyyy-MM-dd").parse(endDate);
      int days = (int) ((end.getTime() - begin.getTime()) / 86400000);
      return days + 1;
    } catch (ParseException e) {
      e.printStackTrace();
      return null;
    }
  }
  public static String secToTime(int time) {
    String timeStr = null;
    int hour = 0;
    int minute = 0;
    int second = 0;
    if (time <= 0) {
      return "00:00";
    } else {
      minute = time / 60;
      if (minute < 60) {
        second = time % 60;
        timeStr = unitFormat(minute) + ":" + unitFormat(second);
      } else {
        hour = minute / 60;
        if (hour > 99) {
          return "99:59:59";
        }
        minute = minute % 60;
        second = time - hour * 3600 - minute * 60;
        timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
      }
    }
    return timeStr;
  }

  public static String unitFormat(int i) {
    String retStr = null;
    if (i >= 0 && i < 10) {
      retStr = "0" + Integer.toString(i);
    } else {
      retStr = "" + i;
    }
    return retStr;
  }

  public static Date getCurrentDate(){
//    ZoneId zoneId = ZoneId.of(ZoneId.SHORT_IDS.get("CTT"));
//    LocalDateTime localDateTime = LocalDateTime.now(zoneId);
//    Instant instant = localDateTime.atZone(zoneId).toInstant();
//    return Date.from(instant);
    return new Date();
  }

  public static Long getCurrentTimestamp(){
//    ZoneId zoneId = ZoneId.of(ZoneId.SHORT_IDS.get("CTT"));
//    LocalDateTime localDateTime = LocalDateTime.now(zoneId);
//    Instant instant = localDateTime.atZone(zoneId).toInstant();
//    return Date.from(instant).getTime();
    return System.currentTimeMillis();
  }

  public static void main(String[] args) {
//    Integer days = getDaysByDates("2017-03-10", "2017-03-10");
//    System.out.println(days);
//    ZoneId zoneId = ZoneId.of(ZoneId.SHORT_IDS.get("CTT"));
//    LocalDateTime localDateTime = LocalDateTime.now(zoneId);
//    System.out.println(new Date());
//    ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, zoneId);
//    Instant instant = localDateTime.atZone(zoneId).toInstant();
//    java.util.Date date = Date.from(instant);
//    System.out.println(date.getTime());
//    System.out.println(System.currentTimeMillis());
//    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss 'GMT'",Locale.ENGLISH);
//    String date = dateFormat.format(new Date());
//    System.out.println(date);
    System.out.println(getCurrentTimestamp()/1000 * 1000);
  }
}
