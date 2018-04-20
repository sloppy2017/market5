package com.c2b.coin.sanchain.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.c2b.coin.sanchain.entity.WithdrawLog;
import com.coin.config.mybatis.BaseMapper;





public interface WithdrawLogMapper extends BaseMapper<WithdrawLog> {
    
    WithdrawLog findWithdrawLogByTxHash(@Param("txHash")String txHash);
    
    int updateWithdrawLogByTxHash(WithdrawLog withdrawLog);
    
    int updateIsSend(@Param("txHash")String txHash,@Param("isSend")String isSend);
    
    List<WithdrawLog> listSANCSend();
}