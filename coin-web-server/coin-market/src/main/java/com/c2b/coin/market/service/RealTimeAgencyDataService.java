package com.c2b.coin.market.service;

import com.c2b.coin.common.DateUtil;
import com.c2b.coin.market.thread.RealTimeAgencyDataThread;
import com.c2b.coin.market.thread.RealTimeDataThread;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
public class RealTimeAgencyDataService extends TaskServiceBase{

  public static String CACHE_LOCK_LIST = "cache.lock.list";

  @Value("${key.redis.data.realtime.buylist}")
  private String REDIS_REALTIME_BUY_KEY;

  @Value("${key.redis.data.realtime.selllist}")
  private String REDIS_REALTIME_SELL_KEY;

  private static String REAL_AGENCY_BUY_PATH_NAME = "REAL_AGENCY_BUY/";
  private static String REAL_AGENCY_SELL_PATH_NAME = "REAL_AGENCY_SELL/";

  @Scheduled(cron = "* * * * * ?")
  public void taskSell() {
    logger.debug("go taskSell!" + DateUtil.getCurrentTimestamp());
    Long begin = DateUtil.getCurrentTimestamp();
    if(setex(CACHE_LOCK_LIST,DateUtil.getCurrentTimestamp()+"")){
      logger.debug("get taskSell lock!" + DateUtil.getCurrentTimestamp());
      stringRedisTemplate.expire(CACHE_LOCK_LIST,500,TimeUnit.MILLISECONDS);
      doJob();
    }else {
      logger.debug("NOT get taskSell lock!");
    }
    Long end = DateUtil.getCurrentTimestamp();
    logger.debug("time : " + (end-begin));
  }

  @Override
  public void doJob() {
    List<String> list = super.getTradePairs();
    for(String tradePair: list){
      logger.debug("Trade Pairs:"+tradePair);
      new Thread(new RealTimeAgencyDataThread(tradePair,filePath + REAL_AGENCY_BUY_PATH_NAME,
        this.getPahtEnd("/yyyy/MM/dd/HHmmss") ,REDIS_REALTIME_BUY_KEY , FILE_SUFFIX ,stringRedisTemplate)).start();
      new Thread(new RealTimeAgencyDataThread(tradePair,filePath + REAL_AGENCY_SELL_PATH_NAME,
        this.getPahtEnd("/yyyy/MM/dd/HHmmss") ,REDIS_REALTIME_SELL_KEY ,FILE_SUFFIX ,stringRedisTemplate)).start();
    }
  }

}
