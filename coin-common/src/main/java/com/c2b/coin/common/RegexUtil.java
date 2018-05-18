package com.c2b.coin.common;

import java.util.regex.Pattern;

/**
 * 正则工具类
 *
 * @author tangwei
 */
public class RegexUtil {

  private RegexUtil() {
  }

  /**
   * 验证ID的格式
   *
   * @param s
   * @return
   */
  private static final Pattern idPattern = Pattern.compile("[0-9]{1,20}");

  public static boolean isId(String s) {
    return idPattern.matcher(s).matches();
  }

  /**
   * 验证ID的格式
   *
   * @param s
   * @return
   */
  private static final Pattern idsPattern = Pattern.compile("([0-9]{1,20})(,[0-9]{1,20})*");

  public static boolean isIds(String s) {
    return idsPattern.matcher(s).matches();
  }

  /**
   * 验证是否布尔
   *
   * @param s
   * @return
   */
  private static final Pattern booleanPattern = Pattern.compile("true|false");

  public static boolean isBoolean(String s) {
    return booleanPattern.matcher(s).matches();
  }

  /**
   * 验证是否1或者0
   *
   * @param s
   * @return
   */
  private static final Pattern yesOrNoPattern = Pattern.compile("1|0");

  public static boolean isYesOrNo(String s) {
    return yesOrNoPattern.matcher(s).matches();
  }

  /**
   * 验证OSS仓库类型
   *
   * @param s
   * @return
   */
  private static final Pattern bucketPattern = Pattern.compile("gamecircle-pic|gamecircle-avatar|gamecircle-video|gamecircle-voice");

  public static boolean bucketRegex(String s) {
    return bucketPattern.matcher(s).matches();
  }

  /**
   * 验证是否包含特殊字符
   *
   * @param s
   * @return
   */
  private static final Pattern specialPattern = Pattern.compile("^([a-zA-Z0-9])+([a-zA-Z0-9_.])*");

  public static boolean specialRegex(String s) {
    return specialPattern.matcher(s).matches();
  }

  /**
   * 验证是否整数
   *
   * @param s
   * @return
   */
  private static final Pattern numberPattern = Pattern.compile("(-)?[\\d]+");

  public static boolean isNumber(String s) {
    return numberPattern.matcher(s).matches();
  }

  /**
   * 验证是否小数点数字
   *
   * @param s
   * @return
   */
  private static final Pattern decimalPattern = Pattern.compile("(-)?[\\d]+(.[\\d]+)?");

  public static boolean isDecimalNumber(String s) {
    return decimalPattern.matcher(s).matches();
  }

  /**
   * 验证日期格式（YYYY-MM-DD）
   *
   * @param s
   * @return
   */
  private static final Pattern datePattern = Pattern.compile("(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29)");

  public static boolean dateRegex(String s) {
    return datePattern.matcher(s).matches();
  }

  /**
   * 验证日期格式（YYYY-MM-DD HH:mm:ss）
   *
   * @param s
   * @return
   */
  private static final Pattern dateTimePattern = Pattern.compile("((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29)) (0\\d{1}|1\\d{1}|2[0-3]):([0-5]\\d{1}):([0-5]\\d{1})");

  public static boolean dateTimeRegex(String s) {
    return dateTimePattern.matcher(s).matches();
  }

  /**
   * 验证手机格式
   *
   * @param s
   * @return
   */
  private static final Pattern mobilePattern = Pattern.compile("^(1[\\d]{2})\\d{8}$");

  public static boolean mobileRegex(String s) {
    return mobilePattern.matcher(s).matches();
  }

  /**
   * 验证邮箱格式
   *
   * @param s
   * @return
   */
  private static final Pattern emailPattern = Pattern.compile("^[A-Za-z0-9]+([-_.][A-Za-z0-9]+)*@([A-Za-z0-9]+[-.])+[A-Za-z0-9]{2,5}$");

  public static boolean emailRegex(String s) {
    return emailPattern.matcher(s).matches();
  }

  /**
   * 验证url地址格式
   *
   * @param s
   * @return
   */
  private static final Pattern urlPattern = Pattern.compile("((http[s]{0,1}|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|((www.)|[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)");

  public static boolean urlRegex(String s) {
    return urlPattern.matcher(s).matches();
  }

  /**
   * 验证ip地址
   *
   * @param s
   * @return
   */
  private static final Pattern ipPattern = Pattern.compile("((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))");

  public static boolean ipRegex(String s) {
    return ipPattern.matcher(s).matches();
  }

  /**
   * 验证ip地址
   *
   * @param s
   * @return
   */
  private static final Pattern ipsPattern = Pattern.compile("(((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))){1}(,((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))*");

  public static boolean ipsRegex(String s) {
    return ipsPattern.matcher(s).matches();
  }

}
