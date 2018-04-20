package com.c2b.coin.trade.service;

import com.c2b.coin.trade.entity.DigitalCoin;
import com.c2b.coin.trade.mapper.DigitalCoinMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class DigitalCoinService {

  @Autowired
  DigitalCoinMapper digitalCoinMapper;

  @Cacheable(value = "DigitalCoin", key = "#coinName", unless = "#result eq null")
  public DigitalCoin findByName(String coinName){
    return digitalCoinMapper.getDigitalCoinByCoinName(coinName);
  }

}
