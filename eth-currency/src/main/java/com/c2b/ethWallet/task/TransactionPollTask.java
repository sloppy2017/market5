package com.c2b.ethWallet.task;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.c2b.coin.common.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import com.c2b.ethWallet.client.Web3JClient;
import com.c2b.ethWallet.entity.WithdrawLog;
import com.c2b.ethWallet.mapper.WithdrawLogMapper;
import com.c2b.ethWallet.util.WalletConstant;

/**
 * 类说明
 *  查询出所有SEND状态的提币申请记录数据列表，遍历，查看是否被打包，然后更新记录数据
 * @author Anne
 * @date 2017年10月29日
 */
public class TransactionPollTask {

  private static Logger logger = LoggerFactory
      .getLogger(TransactionPollTask.class);
  private static Web3j web3j = Web3JClient.getClient();

  @Autowired
  private WithdrawLogMapper withdrawLogMapper;

//  @Scheduled(cron = "0 * * * * ?")
  public void transactionPoll() {
    logger.info("eth 确认打包task  start!");
    // 1：查询待轮询的hash
    List<WithdrawLog> list = withdrawLogMapper.listETHSend();

    logger.info("eth 确认打包task 任务队列长度：" + list.size());
    EthGetTransactionReceipt ethGetTransactionReceipt;

    // 2: 轮询查询结果
    for (WithdrawLog withdrawLog : list) {
      String hash = withdrawLog.getTxHash();
      for (int i = 0; i < 10; i++) {
        try {
          ethGetTransactionReceipt = web3j.ethGetTransactionReceipt(hash)
              .send();
          logger.info("tx start--------------hash=" + hash);
          if (ethGetTransactionReceipt.getTransactionReceipt().isPresent()) {
            // 通过打包对象TransactionReceipt来确认交易是否被广播并确认
            TransactionReceipt transactionReceipt = ethGetTransactionReceipt
                .getTransactionReceipt().get();
            if (transactionReceipt != null) {
              hash = transactionReceipt.getTransactionHash();
              logger.info("tx success:hash=" + hash);
              withdrawLog.setStatus(WalletConstant.FINISH);
              withdrawLog.setUpdateTime(DateUtil.getCurrentDate());
              // 3:更新 hash交易结果
              boolean result = withdrawLogMapper
                  .updateWithdrawRecordByTxHash(withdrawLog);
              logger.info("eth确认打包,更新 hash交易结果：" + result);
              break;
            }
          } else {
            // try again, ad infinitum
            logger.warn("tx fail:hash=" + hash);
            continue;
          }
        } catch (IOException e) {
          logger.error("eth确认打包异常", e);
          logger.error("错误的hash=" + hash);
        }
      }

    }
  }
}
