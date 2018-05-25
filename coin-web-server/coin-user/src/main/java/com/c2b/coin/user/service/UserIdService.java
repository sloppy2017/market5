package com.c2b.coin.user.service;

import com.c2b.coin.common.RandNumUtil;
import com.coin.config.cache.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author MMM
 * @desc 订单号生成 唯一数中包含特定的内容，如把当前时间，如20110609132641，作为前缀等
 * @date 2017-03-03 9:57
 **/
@Service
public class UserIdService {
  private static final String SEQ_KEY = "user-id-key";
  @Autowired
  private RedisUtil redisUtil;

  public String next() {
    int seq = redisUtil.incr(SEQ_KEY).intValue();
    String random = RandNumUtil.getRandNum(3);
    String str = "C".concat(String.format("%1$ty%2$s%3$05d", new Date(), random, seq));
    return str;
  }
}
