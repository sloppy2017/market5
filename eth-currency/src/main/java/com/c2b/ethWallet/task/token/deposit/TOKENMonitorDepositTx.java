package com.c2b.ethWallet.task.token.deposit;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import rx.Observable;
import rx.Subscription;

import com.alibaba.fastjson.JSONObject;
import com.c2b.coin.common.DateUtil;
import com.c2b.ethWallet.client.Web3JClient;
import com.c2b.ethWallet.entity.DigitalCoin;
import com.c2b.ethWallet.entity.RechargeLog;
import com.c2b.ethWallet.entity.UserCoin;
import com.c2b.ethWallet.enumeration.ETHTokenContractAddressEnum;
import com.c2b.ethWallet.mapper.DigitalCoinMapper;
import com.c2b.ethWallet.mapper.RechargeLogMapper;
import com.c2b.ethWallet.mapper.UserCoinMapper;
import com.c2b.ethWallet.mapper.WithdrawLogMapper;
import com.c2b.ethWallet.service.DepositService;
import com.c2b.ethWallet.service.EthTokenWalletService;
import com.c2b.ethWallet.service.WithDrawService;
import com.c2b.ethWallet.task.EtherHotWalletTool;
import com.c2b.ethWallet.util.OrderGenerater;
import com.c2b.ethWallet.util.WalletConstant;

/**
 * 类说明 监控区块信息，获取我们平台的交易信息并增加充值记录
 *
 * @author Anne
 * @date 2017年12月24日
 */
@Component("tokenMonitorDepositTx")
public class TOKENMonitorDepositTx {

  private Logger logger = LoggerFactory.getLogger(getClass());

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

  @Autowired
  private EthTokenWalletService ethTokenWalletService;

  @Value("${OMG.contractAddress}")
  private String OMGContractAddress;

  @Value("${SNT.contractAddress}")
  private String SNTContractAddress;

  @Value("${GNT.contractAddress}")
  private String GNTContractAddress;

  @Value("${POWR.contractAddress}")
  private String POWRContractAddress;

  @Value("${PKT.contractAddress}")
  private String PKTContractAddress;

  private static Web3j web3j = Web3JClient.getClient();

  public void doTokenScanDeposit(String tokenName) {
    logger.info("-------------------" + tokenName + "充值监控线程开始---------------------");
    startTokenScanDeposit(tokenName);
    logger.info("-------------------" + tokenName + "充值监控线程结束---------------------");
  }

  public void startTokenScanDeposit(String tokenName) {
    BigInteger lastBlock = etherHotWalletTool.getTOKENLastBlock();
    BigInteger maxBlock = getMaxNumber();
    logger.info("TOKEN get last Block:" + lastBlock + " from redis!");
    if (lastBlock.compareTo(BigInteger.ZERO) == 0) {
      logger.info("First run token monitor depositTx Thread,the lastBlock is 0");
      lastBlock = maxBlock.subtract(BigInteger.ONE);
    }
    // 如果应扫描块大于当前最大块则跳过
    if (lastBlock.compareTo(maxBlock) > 0){
      return ;
    }else {
      // 设置下一次应该扫描的块
      etherHotWalletTool.saveTOKENLastBlock(lastBlock.add(BigInteger.ONE));
    }
    saveTokenDeposit(tokenName,lastBlock);
  }

  public void saveTokenDeposit(String tokenName,BigInteger lastBlock) {
    try {
      int attempts = 3;
      final Event event = new Event("Transfer",
        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
        }, new TypeReference<Address>() {
        }), Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
      }));

      Observable<Transaction> observable = web3j.catchUpToLatestAndSubscribeToNewTransactionsObservable(DefaultBlockParameter.valueOf(lastBlock));
      Subscription subscription = observable.subscribe(tx -> {
        try {
          String dstAddr = tx.getTo();
          if (dstAddr != null) {
            String tokenAddress = ethTokenWalletService.getTOKENContractAddress(tokenName);
            if (dstAddr.equalsIgnoreCase(tokenAddress)) {
              try {
                TransactionReceipt receipt = null;
                loop:
                for (int i = 0; i < attempts; i++) {
                  try {
                    receipt = web3j.ethGetTransactionReceipt(tx.getHash()).send().getResult();
                    if (receipt != null)
                      break loop;
                    Thread.sleep(2 * 1000);
                  } catch (Exception e) {
                    logger.error("fetch txRecipt occur error! txHash:" + tx.getHash());
                    e.printStackTrace();
                  }
                }

                if (null == receipt) {
                  logger.warn("txhash:" + tx.getHash() + " can't obtain receipt");
                } else {
                  List<Log> ret = receipt.getLogs();
                  if (ret.size() == 1) {
                    Log txlog = ret.get(0);
                    List<String> topics = txlog.getTopics();
                    String encodedEventSignature = EventEncoder.encode(event);
                    if (topics.get(0).equalsIgnoreCase(encodedEventSignature)) {
                      List<Type> indexedValues = new ArrayList<>();
                      List<Type> nonIndexedValues = FunctionReturnDecoder.decode(txlog.getData(), event.getNonIndexedParameters());

                      List<TypeReference<Type>> indexedParameters = event.getIndexedParameters();
                      for (int i = 0; i < indexedParameters.size(); i++) {
                        Type value = FunctionReturnDecoder.decodeIndexedValue(topics.get(i + 1), indexedParameters.get(i));
                        indexedValues.add(value);
                      }

                      Uint256 amount = (Uint256) nonIndexedValues.get(0);
                      BigDecimal receivedToken = Convert.fromWei(amount.getValue().toString(), Convert.Unit.ETHER);

                      String fromAddress = indexedValues.get(0).toString().toLowerCase();

                      String toAddress = indexedValues.get(1).toString().toLowerCase();
                      int txIdx = txlog.getTransactionIndex().intValue();
                      String blkNum = txlog.getBlockNumber().toString();
                      String txHash = Numeric.cleanHexPrefix(txlog.getTransactionHash()).toLowerCase();

                      txHash = Numeric.prependHexPrefix(txHash);
                      if (this.etherHotWalletTool.isExistForTOKENDepositAddr(tokenName,toAddress)) {

                        logger.info("scan TOKEN deposit from:" + fromAddress + "\tto:" + toAddress + "\tamount:" + receivedToken + "\tblknum:" + blkNum + "\ttxhash:" + txHash);
                        saveRechangeOrder(fromAddress, toAddress, txHash, receivedToken, txIdx, blkNum, Convert.fromWei( new BigDecimal(tx.getGasPrice().multiply(receipt.getGasUsed())).toString(), Convert.Unit.ETHER), getTOKENType(tokenName), tokenName);
                      }
                    }
                  }
                }
              } catch (Exception e) {
                e.printStackTrace();
                logger.error("error " + e.getMessage() + "\ttxhash:" + tx.getHash());
              }
            }
          }
          logger.info("token blockNumber = " + lastBlock);
//          BigInteger blockNumber = tx.getBlockNumber().add(BigInteger.ONE);
//          BigInteger lastBlockNumber = getMaxNumber();
//          if (blockNumber.compareTo(lastBlockNumber) == 1) {
//            blockNumber = lastBlockNumber;
//          }
//          logger.info("token blockNumber = " + blockNumber);
//          this.etherHotWalletTool.saveTOKENLastBlock(blockNumber);
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
      doShutdownWork(subscription);

    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void doShutdownWork(Subscription subscription) {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        logger.info("call doShutdownWork");
        subscription.unsubscribe();
      }
    });
  }

  public String getTOKENType(String currency) {
    ETHTokenContractAddressEnum ethTokenContractAddressEnum = ETHTokenContractAddressEnum
      .getETHTokenContractAddressEnum(currency);
    switch (ethTokenContractAddressEnum) {
      case OMG_CONTRACT_ADDRESS:
        return ethTokenContractAddressEnum.getTokenName();
      case SNT_CONTRACT_ADDRESS:
        return ethTokenContractAddressEnum.getTokenName();
      case GNT_CONTRACT_ADDRESS:
        return ethTokenContractAddressEnum.getTokenName();
      case POWR_CONTRACT_ADDRESS:
        return ethTokenContractAddressEnum.getTokenName();
      case PKT_CONTRACT_ADDRESS:
        return ethTokenContractAddressEnum.getTokenName();
      default:
        return ethTokenContractAddressEnum.getTokenName();
    }
  }

  public boolean saveRechangeOrder(String fromAddress, String toAddress,
                                   String txhash, BigDecimal receivedEther, int txIdx, String blockNum,
                                   BigDecimal fee, String tokenType, String tokenName) {
    UserCoin userCoin = userCoinMapper.getUserCoinByAddressAndCurrency(
      toAddress, tokenName);
    if (userCoin == null || userCoin.getAccount() == null
      || "".equals(userCoin.getAccount())) {
      logger.error("TOKENMonitorDepositTxThread  toAddress=" + toAddress
        + " is not exist userCoin!");
      return false;
    }
    logger.debug("getAccount=========" + userCoin.getAccount());
    // 增加充值记录
    String currency = userCoin.getCurrency();
    RechargeLog rechargeLog = new RechargeLog();
    String orderNo = OrderGenerater.generateOrderNo();
    rechargeLog.setOrderNo(orderNo);
    rechargeLog.setToAccount(userCoin.getAccount());
    rechargeLog.setToAddress(toAddress);
    rechargeLog.setFromAddress(null);
    rechargeLog.setCurrency(currency);
    rechargeLog.setMoney(receivedEther);
    rechargeLog.setFree(fee);
    rechargeLog.setStatus(WalletConstant.SEND);
    rechargeLog.setCreateTime(DateUtil.getCurrentDate());
    rechargeLog.setTxHash(txhash);
    if (rechargeLogMapper.getRechargeLogByTxHashAndCurrency(currency, txhash) != null) {
      logger.error("此hash值" + txhash + "在充值记录表已存在");
      return false;
    }
    String tokenHotAddress = ethTokenWalletService
      .getTOKENHotAddress(rechargeLog.getCurrency());
    logger.info("fromAddress=" + fromAddress);
    if (rechargeLogMapper.insertSelective(rechargeLog) > 0
      && !fromAddress.equals(tokenHotAddress)) {
      logger.info("TOKEN insert RechargeLog success =====toAddress:"
        + toAddress + ",addCoinsReceive money===" + receivedEther);
      // 回调
      try {
        DigitalCoin digitalCoin = digitalCoinMapper
          .selectDigitalCoinByCoinName(currency);
        if (digitalCoin == null) {
          logger.error(currency + " data is not exist digitalCoin!");
          return false;
        }
        String broadcastCallbackString = depositService.broadcastCallback(
          rechargeLog.getToAddress(), digitalCoin.getId(),
          rechargeLog.getToAccount(), txhash, rechargeLog.getMoney());
        logger.info(currency + "充币广播回调 broadcastCallbackString="
          + broadcastCallbackString);
        JSONObject broadcastCallbackObject = JSONObject
          .parseObject(broadcastCallbackString);
        if (broadcastCallbackObject == null) {
          logger.error(currency + "充币广播回调返回值转JSONObject为空！");
          return false;
        }
        logger.info(currency + "充币广播txhash=" + txhash + "success!");
      } catch (Exception e) {
        logger.error(currency + "充币广播回调发生异常，address="
          + rechargeLog.getToAddress() + ",money=" + rechargeLog.getMoney()
          + ",account=" + rechargeLog.getToAccount() + "txhash = " + txhash);
        e.printStackTrace();
      }
      return true;
    } else {
      logger.info(currency + " insert RechargeLog fail =====address:"
        + toAddress + ",addCoinsReceive money===" + receivedEther);
      return false;
    }
  }

  public BigInteger getMaxNumber() {
    EthBlockNumber maxblockNumber = null;
    try {
      maxblockNumber = web3j.ethBlockNumber().sendAsync().get();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
    if (maxblockNumber == null) {
      logger.error("The maxBlockNumber is null, we retry later!");
      return BigInteger.ZERO;
    }
    return maxblockNumber.getBlockNumber();
  }
}
