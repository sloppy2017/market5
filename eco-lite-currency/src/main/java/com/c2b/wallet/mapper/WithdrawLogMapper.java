package com.c2b.wallet.mapper;

import org.apache.ibatis.annotations.Param;

import com.c2b.wallet.entity.WithdrawLog;



public interface WithdrawLogMapper {
    int deleteByPrimaryKey(String id);

    int insert(WithdrawLog withdrawLog);

    int insertSelective(WithdrawLog withdrawLog);

    WithdrawLog selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(WithdrawLog withdrawLog);

    int updateByPrimaryKey(WithdrawLog withdrawLog);
    
    WithdrawLog findWithdrawLogByTxHash(@Param("txHash")String txHash);
    
    int updateWithdrawLogByTxHash(WithdrawLog withdrawLog);
    
    int updateIsSend(@Param("txHash")String txHash,@Param("isSend")String isSend);
}