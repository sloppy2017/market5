package com.c2b.ethWallet.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.c2b.ethWallet.entity.RechargeLog;
import com.coin.config.mybatis.BaseMapper;

public interface RechargeLogMapper extends BaseMapper<RechargeLog> {
  List<RechargeLog> listRechargeRecordByGather();

  int updateRechargeRecordByTxHash(RechargeLog rechargeRecord);

  RechargeLog getIcoRechargeRecordByOrderId(String ordid);
  
  int updateIsSend(@Param("txHash") String txHash,
      @Param("isSend") String isSend);
  
  List<RechargeLog> listETHSend();
  
  List<RechargeLog> listTOKENSend(String currency);
  
  RechargeLog getRechargeLogByTxHash(String txHash);
  
  @Select("select * from ico_recharge_log where currency=#{currency} and tx_hash=#{txHash}")
  RechargeLog getRechargeLogByTxHashAndCurrency(@Param(value = "currency") String currency, @Param("txHash") String txHash);
  
  List<RechargeLog> listTOKENRechargeByGather(String currency);
  
  @Select("select * from ico_recharge_log where currency=#{currency} and order_no=#{orderNo}")
  RechargeLog getTokenRechargeLogByOrderNoAndCurrency(@Param(value = "currency") String currency, @Param("orderNo") String orderNo);
}