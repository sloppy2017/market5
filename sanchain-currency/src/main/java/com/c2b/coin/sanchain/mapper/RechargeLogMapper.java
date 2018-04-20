package com.c2b.coin.sanchain.mapper;

import javax.persistence.Table;

import org.apache.ibatis.annotations.Param;

import com.c2b.coin.sanchain.entity.RechargeLog;
import com.coin.config.mybatis.BaseMapper;




@Table
public interface RechargeLogMapper  extends BaseMapper<RechargeLog> {
    
    RechargeLog findRechargeLogByTxHash(@Param("txHash")String txHash);
    
    int updateRechargeLogByTxHash(RechargeLog rechargeLog);
    
    int updateIsSend(@Param("txHash")String txHash,@Param("isSend")String isSend);
    
    
}