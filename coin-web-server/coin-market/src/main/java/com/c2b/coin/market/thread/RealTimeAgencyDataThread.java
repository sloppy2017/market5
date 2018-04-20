package com.c2b.coin.market.thread;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.c2b.coin.common.DateUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RealTimeAgencyDataThread extends RealTimeBase implements Runnable{

  protected String currencyType,pathFront,pathEnd,redisKey,suffix;
  protected StringRedisTemplate stringRedisTemplate;

  public RealTimeAgencyDataThread(String currencyType, String pathFront, String pathEnd, String redisKey, String suffix, StringRedisTemplate stringRedisTemplate) {
    this.currencyType = currencyType;
    this.pathFront = pathFront;
    this.pathEnd = pathEnd;
    this.redisKey = redisKey;
    this.suffix = suffix;
    this.stringRedisTemplate = stringRedisTemplate;
  }

  public void setCurrencyType(String currencyType) {
    this.currencyType = currencyType;
  }

  public void setPathFront(String pathFront) {
    this.pathFront = pathFront;
  }

  public void setPathEnd(String pathEnd) {
    this.pathEnd = pathEnd;
  }

  public void setRedisKey(String redisKey) {
    this.redisKey = redisKey;
  }

  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }

  public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
    this.stringRedisTemplate = stringRedisTemplate;
  }

  private String getRealtimeDataSeconds() {
    String message = this.stringRedisTemplate.opsForValue().get(redisKey+currencyType.toUpperCase());
    if(StringUtils.isEmpty(message) || "[]".equals(message)){
      return "[{\"count\":0.00,\"money\":0.00,\"time\":\"\",\"sumCount\":\"0.00\"}]";
    }else{
      return  message;
    }
  }

  @Override
  public void run() {
    logger.debug("RealTimeAgencyDataThread start : ");
    long start = DateUtil.getCurrentTimestamp();
    String realtimeJson =  this.getRealtimeDataSeconds();
    String fileStr = pathFront + currencyType.toUpperCase() + pathEnd + suffix;
//    String fileStr_pre = pathFront + currencyType.toUpperCase() + pathEnd + "_PRE" + suffix;
//    String lastHistoryFilePath = pathFront + currencyType.toUpperCase() + "/TEMP" + suffix;
//    String key = pathEnd.replaceAll("/","");
    if (createPathByCurrcyDate(fileStr) == false) return;
    if (writeToFile(fileStr, realtimeJson) == false) return;
//    mergeHistoryFile(lastHistoryFilePath ,fileStr_pre , JSONArray.parseArray(realtimeJson),key);
    long end = DateUtil.getCurrentTimestamp();
    logger.debug("RealTimeAgencyData Thread time : " + (end-start));
  }
}
