package com.c2b.ethWallet.task;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthTransaction;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.c2b.coin.common.DateUtil;
import com.c2b.ethWallet.client.Web3JClient;
import com.c2b.ethWallet.entity.DigitalCoin;
import com.c2b.ethWallet.entity.UserCoin;
import com.c2b.ethWallet.entity.WithdrawLog;
import com.c2b.ethWallet.mapper.DigitalCoinMapper;
import com.c2b.ethWallet.mapper.UserCoinMapper;
import com.c2b.ethWallet.mapper.WithdrawLogMapper;
import com.c2b.ethWallet.service.WithDrawService;
import com.c2b.ethWallet.util.Constant;
import com.c2b.ethWallet.util.WalletConstant;
import com.github.pagehelper.StringUtil;

/**
 *
 * @ClassName: ConfirmWithdrawTxThread
 * @Description: TODO(提币确认交易线程)
 * @author 焦博韬
 * @date 2017年10月12日 上午10:51:05
 *
 */
@Component("confirmWithdrawTxThread")
public class ConfirmWithdrawTxThread implements Runnable {

  private Logger logger = Logger.getLogger(ConfirmWithdrawTxThread.class);

  @Autowired
  private WithdrawLogMapper withdrawLogMapper;

  @Autowired
  private WithDrawService withDrawService;

  @Autowired
  private DigitalCoinMapper digitalCoinMapper;

  @Autowired
  UserCoinMapper userCoinMapper;

  private Integer confirmNum = 12;

  @Scheduled(cron = "0 1/3 * * * ?")
  @Override
  public void run() {
    logger.info("提币确认工作线程！ 开始！");
    BigInteger maxBlock = this.getMaxBlockNumber();
    List<WithdrawLog> records = null;
    try {
      // 获取所有已发送状态的ETH提币记录列表
      records = withdrawLogMapper.listETHSend();
    } catch (Exception e) {
      e.printStackTrace();
    }

    logger.info("wait confirm withdrawLog listSize:" + records.size());
    for (WithdrawLog withdrawLog : records) {
      confirmWithdrawTransaction(withdrawLog, maxBlock);
    }

    logger.info("提币确认工作线程！结束！");
  }

  public synchronized void confirmWithdrawTransaction(WithdrawLog withdrawLog,
      BigInteger maxBlock) {
    BigInteger txBlockNum = BigInteger.ZERO;
    String txHash = withdrawLog.getTxHash();
    if (StringUtil.isEmpty(txHash)) {
      logger.info("can't find txHash ,return!");
      logger.debug("can't find txHash ,return!");
      return;
    }
    Web3j web3j = Web3JClient.getClient();
    String fromAddress = "";
    try {
      logger
          .info("ConfirmWithdrawTxThread confirmWithdrawTransaction start txHash="
              + txHash);
      EthTransaction ethTransaction = web3j.ethGetTransactionByHash(txHash)
          .sendAsync().get();
      txBlockNum = ethTransaction.getTransaction().get().getBlockNumber();
      fromAddress = ethTransaction.getTransaction().get().getFrom();
      BigInteger free = ethTransaction.getTransaction().get().getGas()
          .multiply(ethTransaction.getTransaction().get().getGasPrice());
      withdrawLog.setFree(new BigDecimal(free.longValue()));
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }
    int height = maxBlock.subtract(txBlockNum).intValue();
    logger.info("txHash= " + txHash + "\tfromaddress:" + fromAddress
        + "\tfromAccount:" + withdrawLog.getAccount() + "\ttxBlockNum:"
        + txBlockNum + "\tmaxBlock:" + maxBlock + "\theight:" + height);
    if (height >= confirmNum) {
      try {
        withdrawLog.setStatus(WalletConstant.FINISH);
        withdrawLog.setUpdateTime(DateUtil.getCurrentDate());
        boolean updateResult = withdrawLogMapper
            .updateWithdrawRecordByTxHash(withdrawLog);
        if (updateResult) {
          logger.info("txHash= " + txHash + "withdrawLog updated success!");
        } else {
          logger.info("txHash= " + txHash + "withdrawLog updated fail !");
        }
        DigitalCoin digitalCoin = digitalCoinMapper
            .selectDigitalCoinByCoinName(Constant.CURRENCY_ETH);
        if (digitalCoin == null) {
          logger.error(" ETH 币种信息不存在！");
          return;
        }
        // 回调
        try {
          UserCoin userCoin = userCoinMapper.getUserCoinByAccountAndCurrency(
              withdrawLog.getAccount(), Constant.CURRENCY_ETH);
          if (userCoin == null || StringUtils.isEmpty(userCoin.getAddress())) {
            logger.error("根据account获取userCoin address失败！");
            return;
          }
          logger.info("orderNo="+withdrawLog.getOrderNo());
          String withdrawCallback = withDrawService.confirmCallback(
              userCoin.getAddress(), digitalCoin.getId(),
              withdrawLog.getAccount(), txHash, withdrawLog.getMoney(), withdrawLog.getOrderNo());
          logger.info("orderNo="+withdrawLog.getOrderNo()+",ETH提币确认回调结果withdrawCallback="
              + withdrawCallback);
          JSONObject withdrawObject = JSONObject.parseObject(withdrawCallback);
          if (withdrawObject == null) {
            logger.error(" ETH提币确认回调返回值是空！");
            return;
          }
          if (withdrawObject.getBooleanValue("success")) {
            withdrawLogMapper.updateIsSend(txHash, "1");// 回调已发送
          } else {
            withdrawLogMapper.updateIsSend(txHash, "0");// 回调异常
          }

        } catch (Exception e) {
          logger.error("-------ETH提币确认回调发生异常，address="
              + withdrawLog.getToAddress() + ",money=" + withdrawLog.getMoney()
              + ",account=" + withdrawLog.getAccount() + ",txHash=" + txHash);
          withdrawLogMapper.updateIsSend(txHash, "0");// 回调异常
          e.printStackTrace();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      logger.info("txHash= " + txHash + "\tfromaddress:" + fromAddress
          + "\tfromAccount:" + withdrawLog.getAccount() + "\theight:" + height);
    }
  }

  public BigInteger getMaxBlockNumber() {
    Web3j web3j = Web3JClient.getClient();
    EthBlockNumber maxblockNumber = null;
    try {
      maxblockNumber = web3j.ethBlockNumber().sendAsync().get();
      logger.debug("The max Block Number is :"
          + maxblockNumber.getBlockNumber());
    } catch (InterruptedException e) {
      logger
          .error("ConfirmWithdrawTxThread  get maxBlockNumber is fail, we retry later!");
      e.printStackTrace();
    } catch (ExecutionException e) {
      logger
          .error("ConfirmWithdrawTxThread  get maxBlockNumber is fail, we retry later!");
      e.printStackTrace();
    }
    if (maxblockNumber == null) {
      logger.error("The maxBlockNumber is null, we retry later!");
      return BigInteger.ZERO;
    }
    return maxblockNumber.getBlockNumber();
  }
}
