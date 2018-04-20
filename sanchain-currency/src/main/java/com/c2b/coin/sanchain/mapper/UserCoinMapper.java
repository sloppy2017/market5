package com.c2b.coin.sanchain.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.c2b.coin.sanchain.entity.UserCoin;
import com.coin.config.mybatis.BaseMapper;




public interface UserCoinMapper  extends BaseMapper<UserCoin> {
    UserCoin getUserCoinByAddress(@Param("address") String address);
    
    UserCoin getUserCoinByAccountAndCurrency(@Param("account") String account,@Param("currency") String currency);
    
    List<UserCoin> getAllUserCoin(@Param("currency") String currency);
}