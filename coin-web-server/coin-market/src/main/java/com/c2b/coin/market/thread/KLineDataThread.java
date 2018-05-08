package com.c2b.coin.market.thread;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.c2b.coin.market.mapper.MatchMoneyMapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KLineDataThread implements Runnable{

  static final Logger logger = LoggerFactory.getLogger(KLineDataThread.class);

  public static String REDIS_KEY_NOW = "KLINE_DATA_NOW_";
  public static String REDIS_KEY_BEFORE = "KLINE_DATA_BEFORE_";


  private String redisKey ,currencyType;
  private long beginTime,endTime;
  private StringRedisTemplate stringRedisTemplate;
  private MatchMoneyMapper matchMoneyMapper;

  private KLineDataThread(){}

  public KLineDataThread(StringRedisTemplate stringRedisTemplate , MatchMoneyMapper matchMoneyMapper ,
                         String redisKey ,long beginTime, long endTime,String currencyType){
    this.stringRedisTemplate = stringRedisTemplate;
    this.matchMoneyMapper = matchMoneyMapper;
    this.redisKey = redisKey;
    this.beginTime = beginTime;
    this.endTime = endTime;
    this.currencyType = currencyType;
  }

  @Override
  public void run() {

    Map nowDataMap = getRealtimeDataByTimeInterval();
    writeToRedis(REDIS_KEY_NOW+currencyType,redisKey, JSON.toJSONString(nowDataMap));
    String beforeData = (String)stringRedisTemplate.opsForHash().get(REDIS_KEY_BEFORE+currencyType,redisKey);
    if(StringUtils.isEmpty(beforeData)){
      writeToRedis(REDIS_KEY_BEFORE+currencyType , redisKey, "[{\""+endTime+"\":"+JSON.toJSONString(nowDataMap)+"}]");
    }else {
      List<HashMap> mergeList = JSONObject.parseArray(beforeData,HashMap.class);
      HashMap map = new HashMap();
      map.put(endTime,nowDataMap);
      mergeList.add(map);
      String newContent = mergeList.size() > 200 ?
        JSONObject.toJSONString(mergeList.subList(1,mergeList.size())) : JSONObject.toJSONString(mergeList);
      writeToRedis(REDIS_KEY_BEFORE+currencyType,redisKey, newContent);
    }
  }

  private Boolean writeToRedis(String hash,String redisKey, String message) {
    stringRedisTemplate.opsForHash().put(hash,redisKey,message);
    return true;
  }
  private BigDecimal getRealTimePrice(String currencyType){

    List<Map<String,Object>> list =  this.matchMoneyMapper.getTopNRealTimePriceBycurrency(currencyType,1);
    if(list == null || list.size() == 0){
      return BigDecimal.ZERO;
    }else{
      return (BigDecimal) list.get(0).get("made_price");
    }
  }

  private Map getRealtimeDataByTimeInterval() {
    Map<String,Object> map =  this.matchMoneyMapper.getKDate(currencyType,beginTime,endTime);
    if(map!=null && map.size()>0){
      map.put("currency",currencyType);
      map.put("time",endTime+60000L);
      if(BigDecimal.ZERO.compareTo((BigDecimal)map.get("volum")) == 0) {
        BigDecimal price = getRealTimePrice(currencyType);
        if(null ==price){
          map.put("open", "0");
          map.put("close", "0");
          map.put("lowest", "0");
          map.put("highest", "0");
        }else{
          map.put("open", price);
          map.put("close", price);
          map.put("lowest", price);
          map.put("highest", price);
        }
      }
    }else{
      map = new HashMap<>();
      map.put("currency","");
      map.put("time",endTime+60000L);
      map.put("open", "0");
      map.put("close", "0");
      map.put("lowest", "0");
      map.put("highest", "0");
    }
    return map;
  }
}
