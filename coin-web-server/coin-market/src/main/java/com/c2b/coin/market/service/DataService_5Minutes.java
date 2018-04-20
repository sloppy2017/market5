package com.c2b.coin.market.service;

import com.c2b.coin.common.DateUtil;
import com.c2b.coin.market.thread.RealTimeBlockDataThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
public class DataService_5Minutes extends TaskServiceBase{


  public static String CACHE_LOCK = "cache.lock.5minutes";

  private static String MINUTES_PATH_NAME = "5MINUTES/";

  private static int INTERVAL_TIME_SECONDS = 5;

  @Scheduled(cron = "0 0/5 * * * ?")
  public void task() {
    logger.debug("go Task 5 Minute!" + DateUtil.getCurrentTimestamp());
    if(setex(CACHE_LOCK,DateUtil.getCurrentTimestamp()+"")){
      logger.debug("get 5 Minute lock!" + DateUtil.getCurrentTimestamp());
      stringRedisTemplate.expire(CACHE_LOCK,INTERVAL_TIME_SECONDS,TimeUnit.SECONDS);
      doJob();
    }else {
      logger.debug("NOT get 5 Minute lock!");
    }
  }

  @Override
  public void doJob() {
    List<String> list = super.getTradePairs();
    for(String tradePair: list){
      logger.debug("Trade Pairs:"+tradePair);
      new Thread(new RealTimeBlockDataThread(tradePair,filePath + MINUTES_PATH_NAME,
        this.getPahtEnd("/yyyy/MM/dd/HHmm"),getBeginTime_Minute(5),getEndTime_Minute(), FILE_SUFFIX, matchMoneyMapper)).start();
    }
  }
}
