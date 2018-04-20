package com.c2b.ethWallet.mapper;

import org.apache.ibatis.annotations.Param;

import com.c2b.ethWallet.entity.UserCoin;
import com.coin.config.mybatis.BaseMapper;

public interface UserCoinMapper extends BaseMapper<UserCoin> {
  UserCoin getUserCoinByAccountAndCurrency(@Param("account") String account,
      @Param("currency") String currency);
  
  UserCoin getUserCoinByAddress(@Param("address") String address);
  
  UserCoin getUserCoinByAddressAndCurrency(@Param("address") String address,
      @Param("currency") String currency);
}