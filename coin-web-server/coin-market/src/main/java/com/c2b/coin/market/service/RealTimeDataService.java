package com.c2b.coin.market.service;

import com.c2b.coin.common.DateUtil;
import com.c2b.coin.market.thread.RealTimeDataThread;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
public class RealTimeDataService extends TaskServiceBase{

  public static String CACHE_LOCK = "cache.lock";

  @Value("${key.redis.data.realtime.trade}")
  private String REDIS_REALTIME_KEY;

  private static String REAL_TIME_PATH_NAME = "REAL_TIME/";

  @Scheduled(cron = "* * * * * ?")
  public void realTimeSecond() {
    logger.debug("go task!" + DateUtil.getCurrentTimestamp());
    if(setex(CACHE_LOCK,DateUtil.getCurrentTimestamp()+"")){
      logger.debug("get realTime lock!" + DateUtil.getCurrentTimestamp());
      stringRedisTemplate.expire(CACHE_LOCK,500,TimeUnit.MILLISECONDS);
      doJob();
    }else {
      logger.debug("NOT get task lock!");
    }
  }


  public void doJob() {
    List<String> list = super.getTradePairs();
    for(String tradePair: list){
      logger.debug("Trade Pairs:"+tradePair);
      new Thread(new RealTimeDataThread(tradePair,filePath + REAL_TIME_PATH_NAME,
        this.getPahtEnd("/yyyy/MM/dd/HHmmss"),REDIS_REALTIME_KEY, super.FILE_SUFFIX ,
        stringRedisTemplate, matchMoneyMapper)).start();
    }
  }

}
