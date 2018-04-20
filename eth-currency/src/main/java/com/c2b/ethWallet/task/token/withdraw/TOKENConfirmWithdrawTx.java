package com.c2b.ethWallet.task.token.withdraw;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.c2b.ethWallet.util.WalletConstant;
import com.github.pagehelper.StringUtil;

/**  
 * 类说明   
 *  提币确认交易线程
 * @author Anne  
 * @date 2017年12月23日 
 */
@Component("tokenConfirmWithdrawTx")
public class TOKENConfirmWithdrawTx{

  private Logger logger = LoggerFactory.getLogger(getClass());
  
  @Autowired
  private WithdrawLogMapper withdrawLogMapper;

  @Autowired
  private WithDrawService withDrawService;

  @Autowired
  private DigitalCoinMapper digitalCoinMapper;

  @Autowired
  UserCoinMapper userCoinMapper;

  private Integer confirmNum = 12;
  
  int attempts = 20;
  int sleepDuration = 15000;

  //TODO 要不要添加最大提现额度
  public void doTokenConfirmWork(String tokenName, List<WithdrawLog> withdrawLogList) {
    logger.info(tokenName+"提币确认线程开始执行");
    BigInteger maxBlock = this.getMaxBlockNumber();
    logger.info(tokenName+"wait confirm withdrawLog listSize:" + withdrawLogList.size());
    for (WithdrawLog withdrawLog : withdrawLogList) {
      tokenConfirmWithdrawTransaction(withdrawLog, maxBlock);
    }
    logger.info(tokenName+"提币确认线程执行结束");
  }
  
  public synchronized void tokenConfirmWithdrawTransaction(WithdrawLog withdrawLog,
      BigInteger maxBlock) {
    BigInteger txBlockNum = BigInteger.ZERO;
    String txHash = withdrawLog.getTxHash();
    if (StringUtil.isEmpty(txHash)) {
      logger.debug("can't find txHash ,return!");
      return;
    }
    Web3j web3j = Web3JClient.getClient();
    String fromAddress = "";
    try {
      logger
          .info("TOKENConfirmWithdrawTx tokenConfirmWithdrawTransaction start txHash="
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
        String currency = withdrawLog.getCurrency();
        DigitalCoin digitalCoin = digitalCoinMapper
            .selectDigitalCoinByCoinName(currency);
        if (digitalCoin == null) {
          logger.error(currency+"币种信息不存在！");
          return;
        }
        // 回调
        try {
          UserCoin userCoin = userCoinMapper.getUserCoinByAccountAndCurrency(
              withdrawLog.getAccount(), currency);
          if (userCoin == null || StringUtils.isEmpty(userCoin.getAddress())) {
            logger.error("根据account:"+withdrawLog.getAccount()+"和币种:"+currency+"获取userCoin address失败！");
            return;
          }
          logger.info("orderNo="+withdrawLog.getOrderNo());
          String withdrawCallback = withDrawService.confirmCallback(
              userCoin.getAddress(), digitalCoin.getId(),
              withdrawLog.getAccount(), txHash, withdrawLog.getMoney(), withdrawLog.getOrderNo());
          logger.info("orderNo="+withdrawLog.getOrderNo()+","+currency+"提币确认回调结果withdrawCallback="
              + withdrawCallback);
          JSONObject withdrawObject = JSONObject.parseObject(withdrawCallback);
          if (withdrawObject == null) {
            logger.error(currency+"提币确认回调返回值是空！");
            return;
          }
          if (withdrawObject.getBooleanValue("success")) {
            withdrawLogMapper.updateIsSend(txHash, "1");// 回调已发送
          } else {
            withdrawLogMapper.updateIsSend(txHash, "0");// 回调异常
          }

        } catch (Exception e) {
          logger.error(currency+"提币确认回调发生异常，address="
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
  
  private BigInteger getMaxBlockNumber() {
    Web3j web3j = Web3JClient.getClient();
    EthBlockNumber maxblockNumber = null;
    try {
      maxblockNumber = web3j.ethBlockNumber().sendAsync().get();
      logger.debug("The max Block Number is :"
          + maxblockNumber.getBlockNumber());
    } catch (InterruptedException e) {
      logger
          .error("TOKENConfirmWithdrawTx  get maxBlockNumber is fail, we retry later!");
      e.printStackTrace();
    } catch (ExecutionException e) {
      logger
          .error("TOKENConfirmWithdrawTx  get maxBlockNumber is fail, we retry later!");
      e.printStackTrace();
    }
    if (maxblockNumber == null) {
      logger.error("The maxBlockNumber is null, we retry later!");
      return BigInteger.ZERO;
    }
    return maxblockNumber.getBlockNumber();
  }

}
