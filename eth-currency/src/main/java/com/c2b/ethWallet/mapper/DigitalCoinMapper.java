package com.c2b.ethWallet.mapper;

import com.c2b.ethWallet.entity.DigitalCoin;
import com.coin.config.mybatis.BaseMapper;

public interface DigitalCoinMapper extends BaseMapper<DigitalCoin> {
  DigitalCoin selectDigitalCoinByCoinName(String coinName);
}