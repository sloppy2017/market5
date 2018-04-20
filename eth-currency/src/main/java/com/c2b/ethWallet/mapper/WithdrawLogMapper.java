package com.c2b.ethWallet.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.c2b.ethWallet.entity.WithdrawLog;
import com.coin.config.mybatis.BaseMapper;

public interface WithdrawLogMapper extends BaseMapper<WithdrawLog> {

  List<WithdrawLog> listETHApprove();

  List<WithdrawLog> listETHSend();
  
  List<WithdrawLog> listTOKENSend(String currency);

  boolean updateWithdrawRecordByTxHash(WithdrawLog withdrawLog);
  
  int updateIsSend(@Param("txHash")String txHash,@Param("isSend")String isSend);
  
  WithdrawLog getWithdrawLogByHash(String txHash);
}