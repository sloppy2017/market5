package com.c2b.ethWallet.task.token.gather;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.request.RawTransaction;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionTimeoutException;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;
import org.web3j.utils.Convert.Unit;

import com.c2b.coin.common.DateUtil;
import com.c2b.ethWallet.client.Web3JClient;
import com.c2b.ethWallet.entity.IcoGatherRecord;
import com.c2b.ethWallet.entity.RechargeLog;
import com.c2b.ethWallet.entity.UserCoin;
import com.c2b.ethWallet.mapper.RechargeLogMapper;
import com.c2b.ethWallet.mapper.UserCoinMapper;
import com.c2b.ethWallet.service.EthTokenWalletService;
import com.c2b.ethWallet.service.EthWalletService;
import com.c2b.ethWallet.service.IcoGatherRecordService;
import com.c2b.ethWallet.task.token.unit.Transfer;
import com.c2b.ethWallet.util.AES;
import com.c2b.ethWallet.util.Constant;
import com.c2b.ethWallet.util.WalletConstant;

/**  
 * 类说明   
 *  充值资金归集到相应的token热钱包地址
 * @author Anne  
 * @date 2017年12月23日 
 */
@Component("tokenGather")
public class TOKENGather {
  
  Logger logger = LoggerFactory.getLogger(getClass());
  
  @Autowired
  private RechargeLogMapper rechargeLogMapper;

  @Autowired
  UserCoinMapper userCoinMapper;
  
  @Autowired
  private IcoGatherRecordService gatherRecordService;
  
  @Autowired
  private EthTokenWalletService ethTokenWalletService;
  
  @Autowired
  private EthWalletService ethWalletService;
  
  @Autowired
  private TOKENAsyncConfirmPackage tokenAsyncConfirmPackage;
  
  @Value("${zg.hotAddress}")
  private String zgHotAddress;

  @Value("${zg.hotWalletPrivateKey}")
  private String zgHotWalletPrivateKey;
  
  @Value("${zg.minGatherVal}")
  private BigDecimal zgMinGatherVal;
  
  @Value("${OMG.hotAddress}")
  private String OMGHotAddress;

  @Value("${OMG.hotWalletPrivateKey}")
  private String OMGHotWalletPrivateKey;
  
  @Value("${OMG.minGatherVal}")
  private BigDecimal OMGMinGatherVal;
  
  @Value("${SNT.hotAddress}")
  private String SNTHotAddress;

  @Value("${SNT.hotWalletPrivateKey}")
  private String SNTHotWalletPrivateKey;
  
  @Value("${SNT.minGatherVal}")
  private BigDecimal SNTMinGatherVal;
  
  @Value("${GNT.hotAddress}")
  private String GNTHotAddress;

  @Value("${GNT.hotWalletPrivateKey}")
  private String GNTHotWalletPrivateKey;
  
  @Value("${GNT.minGatherVal}")
  private BigDecimal GNTMinGatherVal;
  
  @Value("${POWR.hotAddress}")
  private String POWRHotAddress;

  @Value("${POWR.hotWalletPrivateKey}")
  private String POWRHotWalletPrivateKey;
  
  @Value("${POWR.minGatherVal}")
  private BigDecimal POWRMinGatherVal;
  
  @Value("${PKT.hotAddress}")
  private String PKTHotAddress;

  @Value("${PKT.hotWalletPrivateKey}")
  private String PKTHotWalletPrivateKey;
  
  @Value("${PKT.minGatherVal}")
  private BigDecimal PKTMinGatherVal;
  
  private static Web3j web3j = Web3JClient.getClient();
  
  BigDecimal amount = new BigDecimal("0.005");

  public void doTokenGather(String tokenName, RechargeLog rechargeLog){
    BigDecimal money = rechargeLog.getMoney();
    String currency = rechargeLog.getCurrency();
    BigDecimal tokenMinGatherVal = getTokenMinGatherValue(currency);
    if (money == null || money.compareTo(tokenMinGatherVal) == -1) {
      logger.warn("orderid:{} gather amount:{} less {},we ignore it.",
          rechargeLog.getId(), rechargeLog.getMoney(), tokenMinGatherVal);
      return;
    }
    String address = rechargeLog.getToAddress();
    if (StringUtils.isNotEmpty(address)) {
      
      BigDecimal balance = ethTokenWalletService.getTOKENBalance(address, currency);
      String balanceStr = balance.toPlainString();
      if (balance == null || balanceStr.equals("0")) {
        logger.warn("address："+address+"，balance is null or balance is zero");
        return;
      }
    }
    try {
      UserCoin userCoin = userCoinMapper.getUserCoinByAddressAndCurrency(address,currency);
      if (userCoin == null) {
        logger.error("currency:"+currency+",The userCoin of address:" + address + " is null!");
        return;
      }
      String privateKey = AES.decrypt(userCoin.getPrivateKey());
      Credentials credentials = Credentials.create(privateKey);
      if (sendTokenTransaction(userCoin, rechargeLog, credentials))
        return;
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public boolean sendTokenTransaction(UserCoin userCoin, RechargeLog rechargeLog, Credentials credentials){
    String txHashVal = null;
    String currency = rechargeLog.getCurrency();
    String gatherHotWallet = getTOKENHotAddress(currency);
    Address dstAddress = new Address(gatherHotWallet);
    BigInteger sendAmountWei = Convert.toWei(rechargeLog.getMoney(), Unit.ETHER).toBigInteger();
    Uint256 sendAmount = new Uint256(sendAmountWei);
    Function function = new Function("transfer", Arrays.<Type>asList(dstAddress, sendAmount), Collections.<TypeReference<?>>emptyList());
    String data = FunctionEncoder.encode(function);
    BigDecimal etherzero = BigDecimal.ZERO;
    Credentials sendCredentials = ethTokenWalletService.getETHTOKENCredentialsByAddress(rechargeLog.getToAddress(), rechargeLog.getCurrency());
    String contractAddress = ethTokenWalletService.getTOKENContractAddress(currency);
    //issue sendtx
    EthSendTransaction transactionResponse = null;
    //满足token归集的最小限额，进行归集操作，归集执行之前，先从热钱包向token账户转账，以备后面的token归集操作消耗eth的gas。
    BigInteger sendEth = Transfer.GAS_LIMIT.multiply(Transfer.GAS_PRICE);
    BigDecimal sendEthAmount = Convert.fromWei(sendEth.toString(), Unit.ETHER);
    logger.info("用于支付token的gas="+sendEthAmount);
    try {
      String payHash = sendEthTransactionFromUser(gatherHotWallet, rechargeLog.getToAddress(), sendEthAmount);
      logger.info("payHash="+payHash);
//      String payHash= "0xd5c2c23c84e024338ce58d53bc74b6df0c58ee19c360cb30faae900db7f41eba";
      TransactionReceipt payreceipt = web3j.ethGetTransactionReceipt(payHash).send().getResult();
      if(payreceipt!=null && payHash.equalsIgnoreCase(payreceipt.getTransactionHash())){
        transactionResponse = ethTokenWalletService.sendContractTx(sendCredentials, contractAddress, etherzero, Convert.Unit.ETHER,data);
      }else{
        for(int i=0;i<10;i++){
          payreceipt = web3j.ethGetTransactionReceipt(payHash).send().getResult();
          if(payreceipt!=null && payHash.equalsIgnoreCase(payreceipt.getTransactionHash())){
            transactionResponse = ethTokenWalletService.sendContractTx(sendCredentials, contractAddress, etherzero, Convert.Unit.ETHER,data);
            break;
          }
        }
      }
      if(!transactionResponse.hasError()){
          txHashVal = transactionResponse.getTransactionHash();
          logger.info("Transfer ether originalAmount "+rechargeLog.getMoney()+" from sourceAddress:"+rechargeLog.getToAddress()+" to HOTWALLET;The hash is "+txHashVal);
          if (StringUtils.isNotEmpty(txHashVal)) {
            logger.info("Transaction token actualWei:"
                + sendAmount + " from sourceAddress:" + rechargeLog.getToAddress()
                + " to HOTWALLET;The hash is " + txHashVal);
            
            EthTransaction ethTransaction = null;
            try {
              ethTransaction = web3j
                  .ethGetTransactionByHash(txHashVal).sendAsync().get();
            } catch (InterruptedException | ExecutionException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            if (ethTransaction == null || ethTransaction.hasError()) {
              logger.error("ethGetTransactionByHash error, txHash:{}, error:{}",
                  txHashVal, ethTransaction.getError().getMessage());
              return false;
            }

            Future<TransactionReceipt> receipt;
            try {
              receipt = Transfer.asyncGetTransactionReceipt(web3j, sendCredentials, transactionResponse);
              logger.info("Transfer ether token originalAmount "+rechargeLog.getMoney()+" from sourceAddress:"+rechargeLog.getToAddress()
                  +" to HOTWALLET;The hash is "+txHashVal);
              Map<String,Object> queueMap = new HashMap<String,Object>();
              queueMap.put("orderId", rechargeLog.getOrderNo());
              queueMap.put("txReceipt", receipt);
              queueMap.put("createdTime", System.currentTimeMillis());
              tokenAsyncConfirmPackage.queue.put(queueMap);
            } catch (InterruptedException | ExecutionException
                | TransactionTimeoutException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            
            Transaction transaction = ethTransaction.getTransaction().get();
            rechargeLog.setStatus(WalletConstant.UNCONCENTRATE);
            rechargeLog.setIsConcentrate(Constant.IS_CONCENTRATE);
            rechargeLog.setConcentrateType(currency);
            rechargeLog.setConcentrateMsg(transaction.getHash());
            rechargeLogMapper.updateRechargeRecordByTxHash(rechargeLog);
            IcoGatherRecord gatherRecord = new IcoGatherRecord();
            gatherRecord.setUserId(rechargeLog.getByAccount());
            gatherRecord.setAmount(rechargeLog.getMoney());
            gatherRecord.setCost(new BigDecimal(0));
            gatherRecord.setGatherStatus(Constant.GATHER_STATUS_ING);
            gatherRecord.setCurrency(currency);
            gatherRecord.setHash(transaction.getHash());
            gatherRecord.setNonce(transaction.getNonce().toString());
            gatherRecord.setBlockHash(transaction.getBlockHash());
            gatherRecord.setFromAddress(rechargeLog.getToAddress());
            gatherRecord.setToAddress(gatherHotWallet);
            gatherRecord.setValue(transaction.getValue().toString());
            gatherRecord.setGasPrice(transaction.getGasPrice().toString());
            gatherRecord.setGas(transaction.getGas().toString());
            gatherRecord.setInput(transaction.getInput());
            gatherRecord.setCreates(transaction.getCreates());
            gatherRecord.setPublicKey(transaction.getPublicKey());
            gatherRecord.setOrderNo(rechargeLog.getOrderNo());
            gatherRecord.setCreateTime(DateUtil.getCurrentDate());
            gatherRecord.setUpdateTime(DateUtil.getCurrentDate());
            gatherRecordService.insert(gatherRecord);
            logger.info("The order:"+rechargeLog.getOrderNo()+" sendtx success,wait for receipt block ! the txHashVal:"+txHashVal);
            return true;
      }
      throw new RuntimeException("Error processing transaction request: " +
              transactionResponse.getError().getMessage()+". orderId:"+rechargeLog.getOrderNo());
    }
    } catch (Exception e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    logger.info("orderId:"+rechargeLog.getOrderNo()+" sendTx whether success:"+transactionResponse.hasError()+",error="+transactionResponse.getError());
    return false;
 }
  
  public BigDecimal getTokenMinGatherValue(String currency){
    switch(currency){
      case "zg":
        return zgMinGatherVal;
      case "OMG":
        return OMGMinGatherVal;
      case "SNT":
        return SNTMinGatherVal;
      case "GNT":
        return GNTMinGatherVal;
      case "POWR":
        return POWRMinGatherVal;
      case "PKT":
        return PKTMinGatherVal;
      default:
        return OMGMinGatherVal;
    }   
  }
  
  public String getTOKENHotAddress(String currency) {
    switch (currency) {
    case "zg":
      return zgHotAddress;
    case "OMG":
      return OMGHotAddress;
    case "SNT":
      return SNTHotAddress;
    case "GNT":
      return GNTHotAddress;
    case "POWR":
      return POWRHotAddress;
    case "PKT":
      return PKTHotAddress;
    default:
      return OMGHotAddress;
    }
  }
  
  public String sendEthTransactionFromUser(String fromAddress, String toAddress,BigDecimal amount){
    Credentials credentials = ethTokenWalletService.getHotwalletCredentials(Constant.CURRENCY_ETH);
    BigInteger nonce = ethWalletService.getNonceValue(fromAddress);
    logger.info("send before :nonce="+nonce+",gasPrice="+Transfer.GAS_PRICE+",gasLimit="+Transfer.GAS_LIMIT+",toAddress="+toAddress+",value="+amount);
    amount = Convert.toWei(amount, Convert.Unit.ETHER);
    BigInteger sendAmount = amount.toBigInteger();
    RawTransaction rawTransaction = RawTransaction
        .createEtherTransaction(nonce, Transfer.GAS_PRICE,Transfer.GAS_LIMIT, toAddress, sendAmount);
    logger.info("ecKeyPair = " + credentials.getEcKeyPair()
        + ",privateKey = " + credentials.getEcKeyPair().getPrivateKey() + ","
        + Transfer.GAS_PRICE + ", " + Transfer.GAS_LIMIT.toString()
        + ", val.toBigInteger()=" + sendAmount);
    // sign & send our transaction
    byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction,
        credentials);
    String hexValue = Numeric.toHexString(signedMessage);
    EthSendTransaction ethSendTransaction;
    try {
      ethSendTransaction = web3j
          .ethSendRawTransaction(hexValue).sendAsync().get();
      if (ethSendTransaction != null && !ethSendTransaction.hasError()) {
        String txHash = ethSendTransaction.getTransactionHash();
        logger.info("txHash=" + txHash);
        if (txHash != null) {
          return txHash;
        } else {
          logger.error("from:" + fromAddress + "\tto:" + toAddress + "\tvalue:" + sendAmount
              + " sendTx failed: txHashVal is null");
        }
      } else {
        logger.error("from:" + fromAddress + "\tto:" + toAddress + "\tvalue:" + sendAmount
            + "! sendTx failed: ethSendTransaction.hasError(), error: "
            + ethSendTransaction.getError().getMessage());
      }
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      logger.error("from:" + fromAddress + "\tto:" + toAddress + "\tvalue:" + sendAmount
          + "! sendTx failed:" + e.toString());
    }
    return null;
}
}
