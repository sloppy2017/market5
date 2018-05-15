package com.c2b.ethWallet.task;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import com.c2b.coin.common.DateUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.utils.Convert;

import com.alibaba.fastjson.JSONObject;
import com.c2b.ethWallet.client.Web3JClient;
import com.c2b.ethWallet.entity.DigitalCoin;
import com.c2b.ethWallet.entity.RechargeLog;
import com.c2b.ethWallet.entity.UserCoin;
import com.c2b.ethWallet.mapper.DigitalCoinMapper;
import com.c2b.ethWallet.mapper.RechargeLogMapper;
import com.c2b.ethWallet.mapper.UserCoinMapper;
import com.c2b.ethWallet.mapper.WithdrawLogMapper;
import com.c2b.ethWallet.service.DepositService;
import com.c2b.ethWallet.service.WithDrawService;
import com.c2b.ethWallet.util.Constant;
import com.c2b.ethWallet.util.OrderGenerater;
import com.c2b.ethWallet.util.WalletConstant;

/**
 *
 * @ClassName: MonitorDepositTxThread
 * @Description: TODO(监控区块信息，将交易信息是我们平台的记录，增加充值记录)
 * @author 焦博韬
 * @date 2017年10月12日 上午10:49:07
 *
 */
@Component("monitorDepositTxThread")
public class MonitorDepositTxThread implements Runnable {

  private Logger logger = Logger.getLogger(MonitorDepositTxThread.class);

  @Autowired
  private EtherHotWalletTool etherHotWalletTool;

  @Autowired
  private RechargeLogMapper rechargeLogMapper;

  @Autowired
  private WithdrawLogMapper withdrawLogMapper;

  @Autowired
  private UserCoinMapper userCoinMapper;

  @Autowired
  private DepositService depositService;

  @Autowired
  private DigitalCoinMapper digitalCoinMapper;

  @Autowired
  private WithDrawService withDrawService;

  @Value("ETH.hot.address")
  private String ethHotWallet;

  @Value("wallet.address.cold")
  private String walletAddressCold;

  private static Web3j web3j = Web3JClient.getClient();

  private BigInteger lastBlock;

  @Scheduled(cron = "0 1/3 * * * ?")
  @Override
  public void run() {
    logger
        .debug("============================充值交易检测线程！开始！================================");
    lastBlock = etherHotWalletTool.getLastBlock();
    if (lastBlock.compareTo(new BigInteger("5617000")) < 0) {
      lastBlock = new BigInteger("5617000");
    }
    logger.info("get last Block:" + lastBlock + " from redis!");
    startEtherScanDeposit();
    logger
        .debug("============================充值交易检测线程！结束！================================");
  }

  public void startEtherScanDeposit() {
    if (lastBlock.compareTo(BigInteger.ZERO) == 0) {
      logger.info("First run,the lastBlock is 0");
      saveEtherDespoist(DefaultBlockParameterName.LATEST);
      // reset lastBlock
      // lastBlock = this.etherHotWalletTool.getLastBlock();

    } else if (lastBlock.compareTo(BigInteger.ZERO) == 1) {
      EthBlockNumber maxblockNumber = null;
      try {
        maxblockNumber = web3j.ethBlockNumber().sendAsync().get();
      } catch (InterruptedException e) {
        logger.error("MonitorDepositTxThread startEtherScanDeposit method get maxBlockNumber is fail, we retry later!");
        e.printStackTrace();
      } catch (ExecutionException e) {
        logger.error("MonitorDepositTxThread startEtherScanDeposit method get maxBlockNumber is fail, we retry later!");
        e.printStackTrace();
      }
      if (maxblockNumber == null) {
        logger.error("The maxBlockNumber is null, we retry later!");
        return;
      }

      BigInteger endBlock = maxblockNumber.getBlockNumber();
      logger.info("MonitorDepositTxThread begin scan block from startBlock:" + lastBlock.longValue()
          + " to endBlock:" + endBlock.longValue());

      for (; lastBlock.compareTo(endBlock) < 0; lastBlock = lastBlock
          .add(BigInteger.ONE)) {
        DefaultBlockParameter param = new DefaultBlockParameterNumber(lastBlock);
        saveEtherDespoist(param);
      }
    }
  }

  /**
   * 根据块号遍历所有的区块，然后拿到每个块里面的交易，最后根据交易hash判断是否是我们这边的交易
   * 是：创建充币记录，状态设置为已发送，然后发送一个充币广播回调。 否：不做任何处理
   *
   * @param param
   *          区块信息
   */
  public void saveEtherDespoist(DefaultBlockParameter param) {
    try {
      EthBlock block = web3j.ethGetBlockByNumber(param, true).send();
      for (EthBlock.TransactionResult<Transaction> txResult : block.getResult()
          .getTransactions()) {
        Transaction tx = txResult.get();
        if (tx.getTo() == null) {
          continue;
        }
        String fromAddress = tx.getFrom().toLowerCase();
        String toAddress = tx.getTo().toLowerCase();
        BigDecimal receivedEther = Convert.fromWei(tx.getValue().toString(),
            Convert.Unit.ETHER);
        int txIdx = tx.getTransactionIndex().intValue();
        String blkNum = tx.getBlockNumber().toString();
        String txHash = tx.getHash().toLowerCase();

        logger.debug("MonitorDepositTxThread fromAddress=" + fromAddress + " toAddress=" + toAddress
            + " receivedEther=" + receivedEther + " txIdx=" + txIdx
            + " blkNum=" + blkNum + " txHash=" + txHash);
        logger.debug("gas=" + tx.getGas() + " gasprice=" + tx.getGasPrice()
            + "Fee: " + tx.getGasPrice().multiply(tx.getGas()));
        logger.debug(tx.getGasPriceRaw() + "=============" + tx.getGasRaw());

        if (this.etherHotWalletTool.isExistForDepositAddr(toAddress) && withdrawLogMapper.getWithdrawLogByHash(txHash) == null) {
          logger.info("MonitorDepositTxThread scan deposit for addr:" + toAddress+"  txHash="+txHash);

          /**
           * 注：充值额度低于10000000000000000wei(0.01Ether)时，不记录到充值记录表中，以免归集的时候，归集不到。
           */
//          if (receivedEther.compareTo(new BigDecimal("0.01")) >= 0) {
            saveElecoinOrder(fromAddress,toAddress, txHash, receivedEther, txIdx, blkNum,
                Convert.fromWei(
                    new BigDecimal(tx.getGasPrice().multiply(tx.getGas()))
                        .toString(), Convert.Unit.ETHER));
//          }
        }
      }
      this.etherHotWalletTool.saveLastBlock(block.getBlock().getNumber());
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  /**
   * 插入充币记录并发送充币广播回调
   *
   * @param toAddress
   *          转入地址
   * @param txhash
   *          交易hash
   * @param receivedEther
   *          转账金额
   * @param txIdx
   *          交易索引
   * @param blockNum
   *          区块号
   * @param fee
   *          手续费
   * @return 成功或失败
   */
  public boolean saveElecoinOrder(String fromAddress, String toAddress, String txhash,
      BigDecimal receivedEther, int txIdx, String blockNum, BigDecimal fee) {
    UserCoin userCoin = userCoinMapper.getUserCoinByAddressAndCurrency(toAddress, Constant.CURRENCY_ETH);
    if (userCoin == null || userCoin.getAccount() == null
        || "".equals(userCoin.getAccount())) {
      logger.error("MonitorDepositTxThread  toAddress="+toAddress+" is not exist userCoin!");
      return false;
    }
    logger.debug("getAccount=========" + userCoin.getAccount());

    // 增加充值记录
    RechargeLog rechargeLog = new RechargeLog();
    String orderNo = OrderGenerater.generateOrderNo();
    rechargeLog.setOrderNo(orderNo);
    rechargeLog.setToAccount(userCoin.getAccount());
    rechargeLog.setToAddress(toAddress);
    rechargeLog.setFromAddress(fromAddress);
    rechargeLog.setCurrency(Constant.CURRENCY_ETH);
    rechargeLog.setMoney(receivedEther);
    rechargeLog.setFree(fee);
    rechargeLog.setStatus(WalletConstant.SEND);
    rechargeLog.setCreateTime(DateUtil.getCurrentDate());
    rechargeLog.setTxHash(txhash);
    if(rechargeLogMapper.getRechargeLogByTxHash(txhash)!=null){
      logger.error("此hash值"+txhash+"在充值记录表已存在");
      return false;
    }
    logger.info("fromAddress="+fromAddress+",ethHotWallet="+ethHotWallet);
    if(rechargeLogMapper.insertSelective(rechargeLog)>0 && !fromAddress.equals(ethHotWallet)){
      logger.info("insert RechargeLog success =====address:" + toAddress + ",addCoinsReceive money==="
          + receivedEther);
      // 回调
      try {
        DigitalCoin digitalCoin = digitalCoinMapper
            .selectDigitalCoinByCoinName(Constant.CURRENCY_ETH);
        if(digitalCoin == null){
          logger.error("ETH data is not exist digitalCoin!");
          return false;
        }
        String broadcastCallbackString = depositService.broadcastCallback(rechargeLog.getToAddress(),
            digitalCoin.getId(), rechargeLog.getToAccount(), txhash,
            rechargeLog.getMoney());
        logger.info("ETH充币广播回调 broadcastCallbackString="+broadcastCallbackString);
        JSONObject broadcastCallbackObject = JSONObject
            .parseObject(broadcastCallbackString);
        if(broadcastCallbackObject == null){
          logger.error(" ETH充币广播回调返回值转JSONObject为空！");
          return false;
        }
        logger.info("ETH充币广播txhash="+txhash+"success!");
      } catch (Exception e) {
        logger.error("-------ETH充币广播回调发生异常，address=" + rechargeLog.getToAddress()
            + ",money=" + rechargeLog.getMoney() + ",account="
            + rechargeLog.getToAccount()+"txhash = "+txhash);
        e.printStackTrace();
      }
      return true;
    }else{
      logger.info("insert RechargeLog fail =====address:" + toAddress + ",addCoinsReceive money==="
          + receivedEther);
      return false;
    }
  }
}
