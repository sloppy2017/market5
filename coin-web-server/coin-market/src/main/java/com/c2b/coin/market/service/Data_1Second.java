package com.c2b.coin.market.service;

import com.c2b.coin.common.DateUtil;
import com.c2b.coin.market.thread.KLineDataThread;
import com.c2b.coin.market.thread.KLineRealtimeThread;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
public class Data_1Second extends TaskServiceBase{

  public static String CACHE_LOCK = "cache.lock.1second.rd";

  private static int INTERVAL_TIME_SECONDS = 500;

  @Scheduled(cron = "* * * * * ?")
  public void task() {
    logger.debug("go Task "+INTERVAL_TIME_SECONDS+" Minute!" + DateUtil.getCurrentTimestamp());
    scheduled(INTERVAL_TIME_SECONDS,CACHE_LOCK, TimeUnit.MILLISECONDS);
  }

  @Override
  public void doJob(int INTERVAL_TIME_SECONDS) {
    logger.debug("go Task 1 Second lock!" + DateUtil.getCurrentTimestamp());
    List<String> list = getTradePairs();
    for(String tradePair: list){
      logger.debug("Trade Pairs:"+tradePair);
      new Thread(new KLineRealtimeThread(stringRedisTemplate,
        "REALTIME",getEndTime_Second(),tradePair)).start();
    }
  }
}
