package com.c2b.ethWallet.task.token.deposit;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.Transaction;

import com.alibaba.fastjson.JSONObject;
import com.c2b.coin.common.DateUtil;
import com.c2b.ethWallet.client.Web3JClient;
import com.c2b.ethWallet.entity.DigitalCoin;
import com.c2b.ethWallet.entity.RechargeLog;
import com.c2b.ethWallet.mapper.DigitalCoinMapper;
import com.c2b.ethWallet.mapper.RechargeLogMapper;
import com.c2b.ethWallet.service.DepositService;
import com.c2b.ethWallet.service.EthTokenWalletService;
import com.c2b.ethWallet.util.WalletConstant;

/**  
 * 类说明   
 *  充币确认交易线程
 * @author Anne  
 * @date 2017年12月23日 
 */
@Component("tokenConfirmDepositTx")
public class TOKENConfirmDepositTx {

  private Logger logger = LoggerFactory.getLogger(getClass());
  
  private Web3j web3j = Web3JClient.getClient();

  int confirmNum = 12;

  @Autowired
  private RechargeLogMapper rechargeLogMapper;

  @Autowired
  private DigitalCoinMapper digitalCoinMapper;

  @Autowired
  private DepositService depositService;
  
  @Autowired
  private EthTokenWalletService ethTokenWalletService;

  public void doConfirmDepositWork(String tokenName, List<RechargeLog> rechargeLogList) {
    logger.info(tokenName+"充币确认交易线程开始执行");
    // 获取最新区块号
    BigInteger maxBlock = this.getMaxBlockNumber();
    logger.info(tokenName+" wait confirm depositOrder listSize:" + rechargeLogList.size());
    // 遍历所有充币记录数据
    for (RechargeLog rechargeLog : rechargeLogList) {
      doConfirmDepositWorkStart(rechargeLog, maxBlock);
    }
    logger.info(tokenName+"充币确认交易线程执行结束");
  }

  public void doConfirmDepositWorkStart(RechargeLog rechargeLog, BigInteger maxBlock){
    try{
      if (rechargeLog == null
          || !WalletConstant.SEND.equals(rechargeLog.getStatus())) {
        return;
      }
      String txHash = rechargeLog.getTxHash();
      if (StringUtils.isEmpty(txHash)) {
        return;
      }
      EthTransaction ethTransaction = web3j.ethGetTransactionByHash(txHash)
          .send();
      if (ethTransaction == null || ethTransaction.getError() != null) {
        logger.error(
            "TOKEN getTransactionInfo by txHash ethTransaction object is null, "
                + "errorMassage:{}", ethTransaction.getError().getMessage());
        return;
      }
      Transaction transaction = ethTransaction.getTransaction().get();
      if (transaction == null) {
        logger.error(" by txHash transaction object is null");
        return;
      }
      // 获取交易时区块号，用于判断确认块数
      BigInteger txBlockNumber = transaction.getBlockNumber();
      if (txBlockNumber == null) {
        logger.error("txBlockNumber is null");
      }
      // 计算块高度
      int height = maxBlock.subtract(txBlockNumber).intValue();
      logger.info("orderNo:" + rechargeLog.getOrderNo() + "\t txBlockNum:"
          + txBlockNumber + "\tmaxBlock:" + maxBlock + "\theight:" + height
          + "\theight:" + height);
      // 满足12个块确认条件，更新充币记录数据
      if (height >= confirmNum) {
        rechargeLog.setStatus(WalletConstant.FINISH);
        rechargeLog.setUpdateTime(DateUtil.getCurrentDate());
        String tokenHotAddress = ethTokenWalletService.getTOKENHotAddress(rechargeLog.getCurrency());
        String fromAddress = transaction.getFrom();
        logger.info("fromAddress="+fromAddress);
        //  更新成功，发送充币确认回调
        if (rechargeLogMapper.updateRechargeRecordByTxHash(rechargeLog) > 0 && !fromAddress.equals(tokenHotAddress)) {
          String currency = rechargeLog.getCurrency();
          // 回调
          DigitalCoin digitalCoin = digitalCoinMapper
              .selectDigitalCoinByCoinName(currency);
          if (digitalCoin == null) {
            logger.error(currency+"币种信息不存在！");
            return;
          }
          try {
            String rechargeCallback = depositService.confirmCallback(
                rechargeLog.getToAddress(), digitalCoin.getId(),
                rechargeLog.getToAccount(), transaction.getHash(),
                rechargeLog.getMoney());
            logger.info(currency+"充币确认回调rechargeCallback=" + rechargeCallback);
            
            // 解析充币确认回调返回值，变更is_send字段
            JSONObject rechargeObject = JSONObject
                .parseObject(rechargeCallback);

            if (rechargeObject == null) {
              logger.error(currency+"充币确认回调返回值转JSONObject为空！");
              return;
            }

            // 变更is_send字段
            if (rechargeObject.getBooleanValue("success")) {
              rechargeLogMapper.updateIsSend(transaction.getHash(), "1");// 回调已发送
            } else {
              rechargeLogMapper.updateIsSend(transaction.getHash(), "0");// 回调异常
            }

          } catch (Exception e) {
            logger.error(currency+"充币确认回调发生异常，address="
                + rechargeLog.getToAddress() + ",money="
                + rechargeLog.getMoney() + ",account="
                + rechargeLog.getToAccount()+"errorMessage="+e.getMessage());
            rechargeLogMapper.updateIsSend(transaction.getHash(), "0");// 回调异常
            e.printStackTrace();
          }
          logger.info("The order:" + rechargeLog.getOrderNo()
              + " confirm success!");
        }
      }
    }catch(Exception e){
      e.printStackTrace();
      logger.error(rechargeLog.getCurrency()+"充币确认线程异常，"+e.toString() + "message:" + e.getMessage());
    }
  }
  
  private BigInteger getMaxBlockNumber() {
    EthBlockNumber maxblockNumber = null;
    try {
      maxblockNumber = web3j.ethBlockNumber().send();
      logger.debug("The max Block Number is :"
          + maxblockNumber.getBlockNumber());
    } catch (IOException e1) {
      e1.printStackTrace();
    }
    if (maxblockNumber == null) {
      logger.error("The maxBlockNumber is null, we retry later!");
      return BigInteger.ZERO;
    }
    return maxblockNumber.getBlockNumber();
  }
}
