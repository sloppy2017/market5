package com.c2b.coin.market.service;

import com.c2b.coin.common.DateUtil;
import com.c2b.coin.market.thread.KLineDataThread;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
public class Data_1Week extends TaskServiceBase{

  public static String CACHE_LOCK = "cache.lock.1week.rd";

  private static int INTERVAL_TIME_SECONDS = 70;

  @Scheduled(cron = "0 0 1 ? * MON")
  public void task() {
    logger.debug("go Task 1 WEEK!" + DateUtil.getCurrentTimestamp());
    scheduled(INTERVAL_TIME_SECONDS,CACHE_LOCK, TimeUnit.SECONDS);
  }

  @Override
  public void doJob(int INTERVAL_TIME_SECONDS) {
    logger.debug("go Task 1 WEEK lock!" + DateUtil.getCurrentTimestamp());
    List<String> list = getTradePairs();
    for(String tradePair: list){
      logger.debug("Trade Pairs:"+tradePair);
      new Thread(new KLineDataThread(stringRedisTemplate,matchMoneyMapper,
        "1WEEK",getBeginTime_Week(1),getEndTime_Week(),tradePair)).start();
    }
  }
}
