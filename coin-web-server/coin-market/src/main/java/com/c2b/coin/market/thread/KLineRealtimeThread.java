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

public class KLineRealtimeThread implements Runnable{

  static final Logger logger = LoggerFactory.getLogger(KLineRealtimeThread.class);


  public static String REALTIME_REDIS_KEY = "com.coin.match.real.time.money.key.";
  public static String REDIS_KEY_BEFORE = "KLINE_DATA_BEFORE_";


  private String redisKey ,currencyType;
  private long endTime;
  private StringRedisTemplate stringRedisTemplate;

  private KLineRealtimeThread(){}

  public KLineRealtimeThread(StringRedisTemplate stringRedisTemplate ,
                             String redisKey , long endTime, String currencyType){
    this.stringRedisTemplate = stringRedisTemplate;
    this.redisKey = redisKey;
    this.endTime = endTime;
    this.currencyType = currencyType;
  }

  @Override
  public void run() {

    String realtimeData = (String)stringRedisTemplate.opsForValue().get(REALTIME_REDIS_KEY+currencyType);
    String beforeData = (String)stringRedisTemplate.opsForHash().get(REDIS_KEY_BEFORE+currencyType,redisKey);
    if(StringUtils.isEmpty(beforeData)){
      writeToRedis(REDIS_KEY_BEFORE+currencyType , redisKey, "[{\""+endTime+"\":"+realtimeData+"}]");
    }else {
      List<HashMap> mergeList = JSONObject.parseArray(beforeData,HashMap.class);
      HashMap map = new HashMap();
      map.put(endTime,JSONObject.parseObject(realtimeData,HashMap.class));
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

}
