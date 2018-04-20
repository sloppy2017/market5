package com.c2b.wallet.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.c2b.wallet.entity.UserCoin;


public interface UserCoinMapper {
    int deleteByPrimaryKey(String id);

    int insert(UserCoin userCoin);

    int insertSelective(UserCoin userCoin);

    UserCoin selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(UserCoin userCoin);

    int updateByPrimaryKey(UserCoin userCoin);
    
    UserCoin getUserCoinByAddress(@Param("address") String address);
    
    UserCoin getUserCoinByAccountAndCurrency(@Param("account") String account,@Param("currency") String currency);
    
    List<UserCoin> getAllUserCoin(@Param("currency") String currency);
}