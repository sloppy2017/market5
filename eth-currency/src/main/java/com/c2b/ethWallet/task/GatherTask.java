package com.c2b.ethWallet.task;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import com.c2b.coin.common.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;

import com.c2b.ethWallet.client.Web3JClient;
import com.c2b.ethWallet.entity.IcoGatherRecord;
import com.c2b.ethWallet.entity.RechargeLog;
import com.c2b.ethWallet.entity.UserCoin;
import com.c2b.ethWallet.mapper.RechargeLogMapper;
import com.c2b.ethWallet.mapper.UserCoinMapper;
import com.c2b.ethWallet.service.EthWalletService;
import com.c2b.ethWallet.service.IcoGatherRecordService;
import com.c2b.ethWallet.util.AES;
import com.c2b.ethWallet.util.Constant;
import com.c2b.ethWallet.util.WalletConstant;

/**
 *
 * @ClassName: GatherTask
 * @Description: TODO(归集到热钱包)
 * @author dxm
 * @date 2017年8月21日 上午9:24:46
 *
 */
@Component
public class GatherTask {

  @Autowired
  private RechargeLogMapper rechargeLogMapper;

  @Autowired
  UserCoinMapper userCoinMapper;

  @Autowired
  private EthWalletService ethWalletService;

  @Autowired
  private IcoGatherRecordService gatherRecordService;

  Logger logger = LoggerFactory.getLogger(getClass());

  BigDecimal ethMinGatherVal = new BigDecimal(0.001);

  @Value("${ETH.hot.address}")
  private String ethWalletHotAddress;

  private static Web3j web3j = Web3JClient.getClient();

  @Scheduled(cron = "0 1/1 * * * ?")
  @Transactional
  public void doGether() {
    logger
        .info("-------------------gatherThread start-------------------------");
    List<RechargeLog> recordList = rechargeLogMapper
        .listRechargeRecordByGather();
    if (recordList == null || recordList.size() == 0) {
      logger.warn("rechargeRecordList is null");
    }
    for (RechargeLog record : recordList) {
      BigDecimal money = record.getMoney();
      if (money == null || money.compareTo(ethMinGatherVal) == -1) {
        logger.warn("orderid:{} gather amount:{} less {},we ignore it.",
            record.getId(), record.getMoney(), ethMinGatherVal);
        continue;
      }
      String address = record.getToAddress();
      if (StringUtils.isNotEmpty(address)) {
        BigDecimal balance = ethWalletService.getBalanceWei(address);
        if (balance == null || balance.toBigInteger().equals(BigInteger.ZERO)) {
          logger.warn("address："+address+"，balance is null or balance is zero");
          continue;
        }
      }
      try {
        UserCoin userCoin = userCoinMapper.getUserCoinByAddressAndCurrency(address, Constant.CURRENCY_ETH);
        if (userCoin == null) {
          logger.error("The userCoin of address:" + address + " is null!");
          continue;
        }
        String privateKey = AES.decrypt(userCoin.getPrivateKey());
//        String privateKey = userCoin.getPrivateKey();
        Credentials credentials = Credentials.create(privateKey);
        if (sendEtherTransaction(userCoin, record, credentials))
          continue;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    logger.info("-------------------gatherThread end-------------------------");
  }

  @Transactional
  public boolean sendEtherTransaction(UserCoin userCoin, RechargeLog record,
      Credentials credentials) {
    String txHashVal = null;
    String address = record.getToAddress();
    try {
      // 交易手续费（矿工费）
      /*BigInteger gasPrice = ethWalletService.getGasPrice();
      BigInteger gasCost = gasPrice.multiply(Transfer.GAS_LIMIT);*/
      // send transaction start
      BigDecimal actualBalance = ethWalletService.getBalanceWei(address);
      if (actualBalance == null
          || actualBalance.toBigInteger().equals(BigInteger.ZERO)) {
        logger.warn("account:{},address:{},balance is null or balance is zero",
            record.getByAccount(), address);
        return false;
      }
      BigInteger actualBalanceWei = actualBalance.toBigInteger();
      BigDecimal amount = record.getMoney();
      BigInteger sendAmountWei = Convert.toWei(amount.toString(), Unit.ETHER)
          .toBigInteger();
      logger.info("orderId:" + record.getOrderNo() + "\ttoAddress:" + address
          + "\tactualBalance:" + actualBalanceWei + "\treceivedAmountWei:"
          + sendAmountWei);
      if (actualBalanceWei.compareTo(sendAmountWei) == -1) {
        logger.warn("we change sendAmountWei from " + sendAmountWei + " to "
            + actualBalanceWei);
        sendAmountWei = actualBalanceWei;
      }
//      BigDecimal actualWei = new BigDecimal(actualBalanceWei.subtract(gasCost));
//      BigDecimal actualSendEther = Convert.toWei(actualWei, Unit.ETHER);
      /*logger.info("orderId:" + record.getOrderNo() + "\t originalSendEther:"
          + "\t actualSendEther:" + actualSendEther);*/
      txHashVal = ethWalletService.sendTransactionFromUser(address,
          ethWalletHotAddress, actualBalanceWei.toString());
      if (StringUtils.isNotEmpty(txHashVal)) {
        logger.info("Transaction ether originalAmount " + ",actualWei:"
            + actualBalanceWei + " from sourceAddress:" + address
            + " to HOTWALLET;The hash is " + txHashVal);
        EthTransaction ethTransaction = web3j
            .ethGetTransactionByHash(txHashVal).sendAsync().get();
        if (ethTransaction == null || ethTransaction.hasError()) {
          logger.error("ethGetTransactionByHash error, txHash:{}, error:{}",
              txHashVal, ethTransaction.getError().getMessage());
          return false;
        }

        Transaction transaction = ethTransaction.getTransaction().get();
        record.setStatus(WalletConstant.FINISH);
        record.setIsConcentrate(Constant.IS_CONCENTRATE);
        record.setConcentrateType(Constant.CONCENTRATE_TYPE_ETH);
        record.setConcentrateMsg(transaction.getHash());
        rechargeLogMapper.updateRechargeRecordByTxHash(record);
        IcoGatherRecord gatherRecord = new IcoGatherRecord();
        gatherRecord.setUserId(record.getByAccount());
        gatherRecord.setAmount(record.getMoney());
        gatherRecord.setCost(new BigDecimal(0));
        gatherRecord.setGatherStatus(Constant.GATHER_STATUS_ING);
        gatherRecord.setCurrency(Constant.CURRENCY_ETH);
        gatherRecord.setHash(transaction.getHash());
        gatherRecord.setNonce(transaction.getNonce().toString());
        gatherRecord.setBlockHash(transaction.getBlockHash());
        gatherRecord.setBlockNumber(transaction.getBlockNumber().toString());
        gatherRecord.setTransactionIndex(transaction.getTransactionIndex()
            .toString());
        gatherRecord.setFromAddress(address);
        gatherRecord.setToAddress(ethWalletHotAddress);
        gatherRecord.setValue(transaction.getValue().toString());
        gatherRecord.setGasPrice(transaction.getGasPrice().toString());
        gatherRecord.setGas(transaction.getGas().toString());
        gatherRecord.setInput(transaction.getInput());
        gatherRecord.setCreates(transaction.getCreates());
        gatherRecord.setPublicKey(transaction.getPublicKey());
        gatherRecord.setOrderNo(record.getOrderNo());
        gatherRecord.setCreateTime(DateUtil.getCurrentDate());
        gatherRecord.setUpdateTime(DateUtil.getCurrentDate());
        gatherRecordService.insert(gatherRecord);
        logger.info("The order:" + record.getOrderNo()
            + " sendtx success,wait for receipt block ! the txHashVal:"
            + txHashVal);
        return true;
      }
    } catch (Exception e) {
      e.printStackTrace();
      logger.error("orderId:{} Error processing transaction request, error:{}",
          record.getOrderNo(), e.getMessage());
    }
    return false;
  }
}
