package com.c2b.coin.common;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author <a href="mailto:guo_xp@163.com">guoxinpeng</a>
 * @version 1.0 2016/12/8 17:47
 * @projectname new-pay
 * @packname com.yingu.service.config.util
 */
public interface CommonConst {

  /** 每秒的毫秒数 */
  int MILL_SECONDS_PER_SECOND = 1000;
  /** 每分钟秒数 */
  int SECONDS_PER_MINUTE = 60;
  /** 每分钟毫秒数 */
  int MILL_SECONDS_PER_MINUTE = MILL_SECONDS_PER_SECOND * SECONDS_PER_MINUTE;
  /** 每小时分钟数 */
  int MINUTES_PER_HOUR = 60;
  /** 每小时秒数 */
  int SECONDS_PER_HOUR = MINUTES_PER_HOUR * SECONDS_PER_MINUTE;
  /** 每小时毫秒数 */
  int MILL_SECONDS_PER_HOUR = SECONDS_PER_HOUR * MILL_SECONDS_PER_SECOND;
  /** 每天小时数 */
  int HOURS_PER_DAY = 24;
  /** 每天分钟数 */
  int MINUTES_PER_DAY = HOURS_PER_DAY * MINUTES_PER_HOUR;
  /** 每天秒数 */
  int SECONDS_PER_DAY = MINUTES_PER_DAY * SECONDS_PER_MINUTE;
  /** 每天毫秒数 */
  int MILL_SECONDS_SECOND_PER_DAY = SECONDS_PER_DAY * MILL_SECONDS_PER_SECOND;

  /** 默认整数值 */
  int DFT_INTEGER_VAL = 0;
  /** 默认字节值 */
  byte DFT_BYTE_VAL = 0;
  /** 默认短整数值 */
  short DFT_SHORT_VAL = 0;
  /** 默认长整数值 */
  long DFT_LONG_VAL = 0L;
  /** 默认单精度浮点数值 */
  float DFT_FLOAT_VAL = 0.0F;
  /** 默认双精度浮点数值 */
  double DFT_DOUBLE_VAL = 0.0D;
  /** 默认布尔值 */
  boolean DFT_BOOLEAN_VAL = false;
  /** 默认字符串值 */
  String DFT_STRING_VAL = "".intern();
  /** 默认字符串值-NULL */
  String DFT_NULL_STRING_VAL = "NULL".intern();
  /** 默认大整数数值 */
  BigInteger DFT_BIGINTEGER_VAL = new BigInteger("0");
  /** 默认大小数数值 */
  BigDecimal DFT_BIGDECIMAL_VAL = new BigDecimal("0");
  /** 默认日期数值 */
  Date DFT_DATE_VAL = null;
  /** 默认时间戳值 */
  Timestamp DFT_TIMESTAMP_VAL = null;

}
