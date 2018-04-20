package com.c2b.coin.market.service;

import com.c2b.coin.common.DateUtil;
import com.c2b.coin.market.mapper.MatchMoneyMapper;
import com.c2b.coin.market.thread.KLineDataThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class TaskServiceBase {
  protected Logger logger = LoggerFactory.getLogger(this.getClass().getName());

  @Value("${file.path}")
  protected String filePath ;

  protected static String  FILE_SUFFIX = ".json";

  protected static Integer PRE_REALTIME_SECOND  = 61;

  @Resource(name = "redisTemplate")
  protected RedisTemplate<Object,Object> redisTemplate;

  @Autowired
  protected MatchMoneyMapper matchMoneyMapper;

  @Autowired
  protected StringRedisTemplate stringRedisTemplate;

  private SimpleDateFormat sdf_second = new SimpleDateFormat("yyyyMMddHHmmss");
  private SimpleDateFormat sdf_minute = new SimpleDateFormat("yyyyMMddHHmm00");
  private SimpleDateFormat sdf_day = new SimpleDateFormat("yyyyMMdd000000");
  private SimpleDateFormat sdf_day_6 = new SimpleDateFormat("yyyyMMdd");
  private SimpleDateFormat sdf_month = new SimpleDateFormat("yyyyMM01000000");

  public Boolean setex(final String key,final String value) {
    return redisTemplate.execute(new RedisCallback<Boolean>() {
      @Override
      public Boolean doInRedis(RedisConnection redisConnection) throws DataAccessException{
        byte keys[] = redisTemplate.getStringSerializer().serialize(key);
        byte values[] = redisTemplate.getStringSerializer().serialize(value);
        return redisConnection.setNX(keys,values);
      }
    });
  }

  protected String getPahtEnd(String pattern) {
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    return sdf.format(cal.getTime())+ "";
  }
  protected String getToday_Day(){
    Calendar cal = Calendar.getInstance();
    return sdf_day_6.format(cal.getTime());
  }

  protected long getEndTime_Second(){
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.SECOND,-1);
    return cal.getTimeInMillis();
  }

  protected long get1DayBeginTime_Second(){
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.HOUR_OF_DAY,-24);
    return cal.getTimeInMillis();
  }

  protected long getBeginTime_Second(){
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.SECOND,-PRE_REALTIME_SECOND);
    return cal.getTimeInMillis();
  }

  protected long getEndTime_Minute(){
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.MINUTE , -1);
    return cal.getTimeInMillis();
  }

  protected long getBeginTime_Minute(int cycle){
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.MINUTE , -cycle-1);
    return cal.getTimeInMillis();
  }

  protected long getEndTime_Day(){
    Calendar cal = Calendar.getInstance();
    return cal.getTimeInMillis();
  }

  protected long getBeginTime_Day(int cycle){
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DAY_OF_YEAR , -cycle);
    return cal.getTimeInMillis();
  }

  protected long getEndTime_Week(){
    Calendar cal = Calendar.getInstance();
    return cal.getTimeInMillis();
  }

  protected long getBeginTime_Week(int cycle){
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.WEEK_OF_YEAR , -cycle);
    return cal.getTimeInMillis();
  }

  protected long getEndTime_Month(){
    Calendar cal = Calendar.getInstance();
    return cal.getTimeInMillis();
  }

  protected long getBeginTime_Month(int cycle){
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DAY_OF_MONTH , -cycle);
    return cal.getTimeInMillis();
  }

  protected List<String> getTradePairs(){
    List<String> list = new ArrayList<String>();
    List<Map<String,Object>> tradePairs =  this.matchMoneyMapper.getTradePairs();
    for (Map map : tradePairs){
      list.add(map.get("commodity_coin_name")+"/"+map.get("money_coin_name"));
    }

    return list;
  }

  public void scheduled(int INTERVAL_TIME_SECONDS,String CACHE_LOCK,TimeUnit timeUnit){
    if(setex(CACHE_LOCK,DateUtil.getCurrentTimestamp()+"")){
      stringRedisTemplate.expire(CACHE_LOCK,INTERVAL_TIME_SECONDS, timeUnit);
      doJob(INTERVAL_TIME_SECONDS);
    }
  }
  public void doJob(){}

  public void doJob(int INTERVAL_TIME_SECONDS) {
    logger.debug("get "+INTERVAL_TIME_SECONDS+" lock!" + DateUtil.getCurrentTimestamp());
    List<String> list = getTradePairs();
    for(String tradePair: list){
      logger.debug("Trade Pairs:"+tradePair);
      new Thread(new KLineDataThread(stringRedisTemplate,matchMoneyMapper,
        INTERVAL_TIME_SECONDS+"MINUTES",getBeginTime_Minute(INTERVAL_TIME_SECONDS),getEndTime_Minute(),tradePair)).start();
    }
  }
}
