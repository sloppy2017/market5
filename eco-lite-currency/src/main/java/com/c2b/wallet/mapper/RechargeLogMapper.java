package com.c2b.wallet.mapper;

import org.apache.ibatis.annotations.Param;

import com.c2b.wallet.entity.RechargeLog;



public interface RechargeLogMapper {
    int deleteByPrimaryKey(String id);

    int insert(RechargeLog rechargeLog);

    int insertSelective(RechargeLog rechargeLog);

    RechargeLog selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(RechargeLog rechargeLog);

    int updateByPrimaryKey(RechargeLog rechargeLog);
    
    RechargeLog findRechargeLogByTxHash(@Param("txHash")String txHash);
    
    int updateRechargeLogByTxHash(RechargeLog rechargeLog);
    
    int updateIsSend(@Param("txHash")String txHash,@Param("isSend")String isSend);
}