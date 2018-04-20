package com.c2b.coin.market.thread;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.c2b.coin.market.mapper.MatchMoneyMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RealTimeDataThread extends RealTimeBase implements Runnable {

  protected String currencyType,pathFront,pathEnd,redisKey,suffix;
  protected StringRedisTemplate stringRedisTemplate;
  protected MatchMoneyMapper matchMoneyMapper;

  public RealTimeDataThread(String currencyType, String pathFront, String pathEnd,String redisKey,
                            String suffix, StringRedisTemplate stringRedisTemplate,
                            MatchMoneyMapper matchMoneyMapper){
    this.currencyType = currencyType;
    this.pathFront = pathFront;
    this.pathEnd = pathEnd;
    this.redisKey = redisKey;
    this.suffix = suffix;
    this.stringRedisTemplate = stringRedisTemplate;
    this.matchMoneyMapper = matchMoneyMapper;
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

  public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
    this.stringRedisTemplate = stringRedisTemplate;
  }

  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }

  public void setMatchMoneyMapper(MatchMoneyMapper matchMoneyMapper) {
    this.matchMoneyMapper = matchMoneyMapper;
  }

  @Override
  public void run() {
    String fileStr = pathFront + currencyType.toUpperCase() + pathEnd + suffix;
    String fileStr_pre = pathFront + currencyType.toUpperCase() + pathEnd + "_PRE" + suffix;
    String lastHistoryFilePath = pathFront + currencyType.toUpperCase() + "/TEMP" + suffix;
    logger.debug("fileStr_pre : "+fileStr_pre);
    String key = pathEnd.replaceAll("/","");
//    String mergeContent = this.getHistoryDataSeconds();
    if (createPathByCurrcyDate(fileStr) == false) return;
    if (writeToFile(fileStr, this.getRealtimeDataSeconds()) == false) return;
    if (createPathByCurrcyDate(fileStr_pre) == false) return;
//    if (writeToFile(fileStr_pre, this.getHistoryDataSeconds()) == false) return;
    mergeHistoryFile(lastHistoryFilePath ,fileStr_pre , JSONObject.parseObject(this.getRealtimeDataSeconds()),key);


  }

  private String getRealtimeDataSeconds() {
    String message = this.stringRedisTemplate.opsForValue().get(redisKey+currencyType.toUpperCase());
    if(StringUtils.isEmpty(message)){
      return "{\"currency\":\""+currencyType+"\",\"money\":\"0\"}";
    }else{
      HashMap map = JSONObject.parseObject(message,HashMap.class);
      map.put("time",new Date().getTime());
      return  message;
    }
  }

//  private String getHistoryDataSeconds() {
//    SimpleDateFormat sdf_second = new SimpleDateFormat("yyyyMMddHHmmss");
//    BigDecimal amount = BigDecimal.ZERO;
//    Date date = null;
//    HashMap resultMap = null;
//    HashMap jsonMap = new HashMap();
//    List<Map<String,Object>> list =  this.matchMoneyMapper.getTopNRealTimePriceBycurrency(currencyType,60);
//    if(list == null || list.size() == 0){
//      logger.debug("getHistoryDataSeconds is null!!!");
//      return "{\"currency\":\""+currencyType+"\",\"money\":\"0\"}";
//    }
//    for(Map map : list){
//      resultMap = new HashMap<String,Object>();
//      amount = (BigDecimal) map.get("money");
//      date = (Date) map.get("time");
//      resultMap.put("currency",currencyType);
//      resultMap.put("money",amount);
//      jsonMap.put(sdf_second.format(date.getTime()) ,resultMap);
//    }
//    return JSON.toJSONString(jsonMap);
//  }
}
