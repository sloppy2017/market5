package com.c2b.coin.market.service;

import com.c2b.coin.common.DateUtil;
import com.c2b.coin.market.thread.RealTimeBlockDataThread;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
public class DataService_15Minutes extends TaskServiceBase{

  public static String CACHE_LOCK = "cache.lock.15minutes";

  private static String MINUTES_PATH_NAME = "15MINUTES/";

  private static int INTERVAL_TIME_SECONDS = 2;

  @Scheduled(cron = "0 0/15 * * * ?")
  public void task() {
    logger.debug("go Task 15 Minute!" + DateUtil.getCurrentTimestamp());
    if(setex(CACHE_LOCK,DateUtil.getCurrentTimestamp()+"")){
      logger.debug("get 15 Minute lock!" + DateUtil.getCurrentTimestamp());
      stringRedisTemplate.expire(CACHE_LOCK,INTERVAL_TIME_SECONDS,TimeUnit.SECONDS);
      doJob();
    }else {
      logger.debug("NOT get 15 Minute lock!");
    }
  }

  @Override
  public void doJob() {
    List<String> list = super.getTradePairs();
    for(String tradePair: list){
      logger.debug("Trade Pairs:"+tradePair);
      new Thread(new RealTimeBlockDataThread(tradePair,filePath + MINUTES_PATH_NAME,
        this.getPahtEnd("/yyyy/MM/dd/HHmm"),getBeginTime_Minute(15),getEndTime_Minute(), FILE_SUFFIX, matchMoneyMapper)).start();
    }
  }
}
