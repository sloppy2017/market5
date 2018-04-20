package com.c2b.ethWallet.task;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import com.c2b.coin.common.DateUtil;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.Transaction;

import com.alibaba.fastjson.JSONObject;
import com.c2b.ethWallet.client.Web3JClient;
import com.c2b.ethWallet.entity.DigitalCoin;
import com.c2b.ethWallet.entity.RechargeLog;
import com.c2b.ethWallet.mapper.DigitalCoinMapper;
import com.c2b.ethWallet.mapper.RechargeLogMapper;
import com.c2b.ethWallet.service.DepositService;
import com.c2b.ethWallet.util.Constant;
import com.c2b.ethWallet.util.WalletConstant;

/**
 *
 * @ClassName: ConfirmDepositTxThread
 * @Description: TODO(充币确认交易线程)
 * @author Anne
 * @date 2017年10月30日 下午16:26:05
 *
 */
@Component("confirmDepositTxThread")
public class ConfirmDepositTxThread implements Runnable {

  private Logger logger = LoggerFactory.getLogger(ConfirmDepositTxThread.class);

  private Web3j web3j = Web3JClient.getClient();

  int confirmNum = 12;

  @Autowired
  private RechargeLogMapper rechargeLogMapper;

  @Autowired
  private DigitalCoinMapper digitalCoinMapper;

  @Autowired
  private DepositService depositService;
  
  @Value("ETH.hot.address")
  private String ethHotWallet;

  @Scheduled(cron = "0 1/3 * * * ?")
  @Override
  public void run() {
    try {
      logger.info("begin confirm deposit tx");

      // 查询所有SEND状态的充币记录列表
      List<RechargeLog> list = rechargeLogMapper.listETHSend();

      // 获取最新区块号
      BigInteger maxBlock = this.getMaxBlockNumber();

      logger.info("wait confirm depositOrder listSize:" + list.size());

      // 遍历所有充币记录数据
      for (RechargeLog rechargeLog : list) {
        if (rechargeLog == null
            || !WalletConstant.SEND.equals(rechargeLog.getStatus())) {
          continue;
        }

        String txHash = rechargeLog.getTxHash();
        if (StringUtils.isEmpty(txHash)) {
          continue;
        }

        EthTransaction ethTransaction = web3j.ethGetTransactionByHash(txHash)
            .send();

        if (ethTransaction == null || ethTransaction.getError() != null) {
          logger.error(
              "getTransactionInfo by txHash ethTransaction object is null, "
                  + "errorMassage:{}", ethTransaction.getError().getMessage());
          continue;
        }

        Transaction transaction = ethTransaction.getTransaction().get();

        if (transaction == null) {
          logger.error(" by txHash transaction object is null");
          continue;
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

        if (height >= confirmNum) {// 满足12个块确认条件，更新充币记录数据

          String fromAddress = transaction.getFrom();
          logger.info("fromAddress="+fromAddress+",ethHotWallet="+ethHotWallet);
          rechargeLog.setStatus(WalletConstant.FINISH);
          rechargeLog.setUpdateTime(DateUtil.getCurrentDate());
          if (rechargeLogMapper.updateRechargeRecordByTxHash(rechargeLog) > 0  && !fromAddress.equals(ethHotWallet)) {// 更新成功，发送充币确认回调
            // 回调
            DigitalCoin digitalCoin = digitalCoinMapper
                .selectDigitalCoinByCoinName(Constant.CURRENCY_ETH);
            if (digitalCoin == null) {
              logger.error(" ETH 币种信息不存在！");
              continue;
            }
            try {

              String rechargeCallback = depositService.confirmCallback(
                  rechargeLog.getToAddress(), digitalCoin.getId(),
                  rechargeLog.getToAccount(), transaction.getHash(),
                  rechargeLog.getMoney());

              logger.info("ETH充币确认回调rechargeCallback=" + rechargeCallback);

              // 解析充币确认回调返回值，变更is_send字段
              JSONObject rechargeObject = JSONObject
                  .parseObject(rechargeCallback);

              if (rechargeObject == null) {
                logger.error(" ETH充币确认回调返回值转JSONObject为空！");
                continue;
              }

              // 变更is_send字段
              if (rechargeObject.getBooleanValue("success")) {
                rechargeLogMapper.updateIsSend(transaction.getHash(), "1");// 回调已发送
              } else {
                rechargeLogMapper.updateIsSend(transaction.getHash(), "0");// 回调异常
              }

            } catch (Exception e) {
              logger.error("-------ETH充币确认回调发生异常，address="
                  + rechargeLog.getToAddress() + ",money="
                  + rechargeLog.getMoney() + ",account="
                  + rechargeLog.getToAccount());
              rechargeLogMapper.updateIsSend(transaction.getHash(), "0");// 回调异常
              e.printStackTrace();
            }
            logger.info("The order:" + rechargeLog.getOrderNo()
                + " confirm success!");
          }
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
      logger.error(e.toString() + "message:" + e.getMessage());
    }
    logger.info("end confirm deposit tx");
  }

  public BigInteger getMaxBlockNumber() {
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
