package com.c2b.coin.market.service;

import com.c2b.coin.common.DateUtil;
import com.c2b.coin.market.thread.KLineDataThread;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
public class Data_15Minutes extends TaskServiceBase{

  public static String CACHE_LOCK = "cache.lock.15minutes.rd";

  private static int INTERVAL_TIME_SECONDS = 15;

  @Scheduled(cron = "0 0/15 * * * ?")
  public void task() {
    logger.debug("go Task "+INTERVAL_TIME_SECONDS+" Minute!" + DateUtil.getCurrentTimestamp());
    scheduled(INTERVAL_TIME_SECONDS,CACHE_LOCK ,TimeUnit.SECONDS);
  }
}
