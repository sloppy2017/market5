package com.c2b.wallet.entity;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.c2b.wallet.entity.UserCoin;


public interface UserCoinMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserCoin record);

    int insertSelective(UserCoin record);

    UserCoin selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserCoin record);

    int updateByPrimaryKey(UserCoin record);
    
    UserCoin getUserCoinByAddress(@Param("address") String address);
    
    UserCoin getUserCoinByAccountAndCurrency(@Param("account") String account,@Param("currency") String currency);
    
    List<UserCoin> getAllUserCoin(@Param("currency") String currency);
}