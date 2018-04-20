package com.c2b.coin.sanchain.mapper;

import com.c2b.coin.sanchain.entity.DigitalCoin;
import com.coin.config.mybatis.BaseMapper;

public interface DigitalCoinMapper  extends BaseMapper<DigitalCoin> {


    DigitalCoin selectDigitalCoinByCoinName(String coinName);
}