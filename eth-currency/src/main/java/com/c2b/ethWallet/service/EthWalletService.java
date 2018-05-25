package com.c2b.ethWallet.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.c2b.coin.cache.redis.RedisUtil;
import org.bouncycastle.util.encoders.DecoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.RawTransaction;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import com.c2b.coin.common.DateUtil;
import com.c2b.ethWallet.client.Web3JClient;
import com.c2b.ethWallet.entity.UserCoin;
import com.c2b.ethWallet.entity.WithdrawLog;
import com.c2b.ethWallet.mapper.UserCoinMapper;
import com.c2b.ethWallet.mapper.WithdrawLogMapper;
import com.c2b.ethWallet.task.EtherHotWalletTool;
import com.c2b.ethWallet.util.AES;
import com.c2b.ethWallet.util.Constant;
import com.c2b.ethWallet.util.EtherWalletUtil;
import com.c2b.ethWallet.util.EtherWalletUtil.EtherWallet;
import com.c2b.ethWallet.util.WalletConstant;

/**
 *
 * @author Anne
 * @Description: TODO(以太坊钱包service)
 * @date 2017.10.27
 */
@Service
public class EthWalletService {

  @Autowired
  UserCoinMapper userCoinMapper;

  @Autowired
  private WithdrawLogMapper withdrawLogMapper;

  @Autowired
  RedisUtil redisUtil;

  Logger logger = LoggerFactory.getLogger(getClass());

  private static String REDIS_ETH_MEMBER_ADDRESS = "ETH_MEMBER_ADDRESS";

  private static Web3j web3j = Web3JClient.getClient();

  @Autowired
  private SysGencodeService sysGencodeService;

  @Autowired
  private EtherHotWalletTool etherHotWalletTool;
  /**
   *
   * @Title: isValidAddress
   * @Description: TODO(验证以太坊地址)
   * @param @param address
   * @param @return 设定文件
   * @return boolean 返回类型
   * @throws
   * @author Anne
   */
  public boolean isValidAddress(String address) {
    return EtherWalletUtil.isValidAddress(address);
  }

  /**
   * 创建以太坊钱包地址
   *
   * @param account
   *          账号
   * @return 钱包地址
   */
  public String createEthWallet(String account) {
    String address = null;
    try {
      if (this.checkExistAddress(account)) {
        logger
            .error("account:{} createEthWallet fail, because account is exist!", account);
        return null;
      }
      EtherWallet etherWallet = EtherWalletUtil.createWallet();
      if (etherWallet != null) {
        if (WalletUtils.isValidAddress(etherWallet.getAddress())) {
          address = "0x" + etherWallet.getAddress();
          if (address != null && !"".equals(address)) {
            redisUtil.zset(REDIS_ETH_MEMBER_ADDRESS, address);
            UserCoin userCoin = new UserCoin();
            userCoin.setAccount(account);
            userCoin.setCreateTime(DateUtil.getCurrentDate());
            userCoin.setCurrency("ETH");
            userCoin.setAddress(address);
            userCoin.setPrivateKey(AES.encrypt(etherWallet.getPrivateKeys()
                .toString().getBytes()));
            userCoin.setUpdateTime(DateUtil.getCurrentDate());
            if (userCoinMapper.insertSelective(userCoin) <= 0) {
              logger.error("account: " + account
                  + "insert userCoin table fail!");
            }
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      logger.error("createEthWallet error:{}", e.getMessage());
    }
    return address;
  }

  /**
   * 检查该用户是否已存在钱包地址
   *
   * @param userName
   * @return true：存在;false：不存在
   */
  public boolean checkExistAddress(String account) {
    boolean result = true;
    UserCoin userCoin = userCoinMapper.getUserCoinByAccountAndCurrency(account,
        Constant.CURRENCY_ETH);
    if (userCoin == null) {
      result = false;
    }
    return result;
  }

  /**
   * 根据账号获取钱包地址
   *
   * @param account
   *          账号
   * @return 钱包地址
   */
  public String getAddressByAccount(String account) {
    UserCoin userCoin = userCoinMapper.getUserCoinByAccountAndCurrency(account,
        Constant.CURRENCY_ETH);
    if (userCoin != null) {
      return userCoin.getAddress();
    } else {
      return null;
    }
  }

  /**
   *
   * @Title: getBalanceEther
   * @Description: TODO(根据地址获取以太坊余额，单位是ETHER)
   * @param address
   *          钱包地址
   * @return BigDecimal 返回钱包余额，单位是ETHER
   * @throws
   * @author Anne
   */
  public BigDecimal getBalanceEther(String address) {
    try {
      EthGetBalance ethGetBalance = web3j.ethGetBalance(address,
          DefaultBlockParameterName.LATEST).send();
      if (ethGetBalance != null) {
        return Convert.fromWei(new BigDecimal(ethGetBalance.getBalance()),
            Convert.Unit.ETHER);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   *
   * @Title: getBalanceWei
   * @Description: TODO(根据钱包地址获取以太坊余额，单位是WEI)
   * @param address
   *          钱包地址
   * @return BigDecimal 返回钱包余额，单位是WEI
   * @throws
   * @author Anne
   */
  public BigDecimal getBalanceWei(String address) {
    try {
      EthGetBalance ethGetBalance = web3j.ethGetBalance(address,
          DefaultBlockParameterName.LATEST).send();
      if (ethGetBalance != null) {
        return new BigDecimal(ethGetBalance.getBalance());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   *
   * @Title: getNonceValue
   * @Description: TODO(为每一个交易获取一个nonce值)
   * @param @param address 转账钱包地址
   * @return BigInteger 返回nonce值，用于标识交易的唯一
   * @throws
   * @author Anne
   */
  public static BigInteger getNonceValue(String address) {
    EthGetTransactionCount ethGetTransactionCount = null;
    try {
      ethGetTransactionCount = web3j
          .ethGetTransactionCount(address, DefaultBlockParameterName.LATEST)
          .sendAsync().get();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }

    BigInteger nonce = ethGetTransactionCount.getTransactionCount();
    return nonce;
  }

  /**
   *
   * @Title: getGasPrice
   * @Description: TODO(获取gasPrice值)
   * @return BigInteger 返回gasPrice值
   * @throws
   * @author Anne
   */
  public BigInteger getGasPrice() {
    EthGasPrice ethGasPrice = null;
    try {
      ethGasPrice = web3j.ethGasPrice().send();
    } catch (IOException e) {
      e.printStackTrace();
    }
    BigInteger gasPrice = ethGasPrice.getGasPrice();
    return gasPrice;
  }


  /**
   *
   * @Title: getHotwallet
   * @Description: TODO(获取热钱包信息)
   * @return EtherWallet 返回热钱包
   * @throws
   * @author Anne
   */
  public EtherWallet getHotwallet() {
    EtherWallet hotWallet = new EtherWallet();
    List<Map<String, String>> list = sysGencodeService
        .findByGroupCode(Constant.HOT_WALLET_CODE_GROUP);
    if (list == null || list.size() == 0) {
      logger.error("hot wallet uninitialized!");
      return null;
    }
    Map<String, String> paramMap = new HashMap<String, String>();
    for (Map<String, String> map : list) {
      paramMap.put(map.get("code_name"), map.get("code_value"));
    }
    if (paramMap.size() > 0) {
      String address = paramMap.get("hotWaletAddress");
      String privateKey = paramMap.get("hotWaletPrivateKey");
      String publicKey = paramMap.get("hotWaletPublicKey");
      hotWallet.setAddress(address);
      hotWallet.setPrivateKeys(privateKey);
      hotWallet.setPublicKey(publicKey);
    }
    return hotWallet;
  }

  /**
   *
   * @Title: sendTransactionFromWallet
   * @Description: TODO(主要用于公司钱包账户地址向用户地址转账交易，热钱包转向用户钱包。等同于提币交易)
   * @param @param fromAddress 公司钱包账户地址，比如主账户，热钱包地址，冷钱包地址等
   * @param @param toAddress 转账目标钱包地址
   * @param @param password 交易密码
   * @param @param amount 交易金额
   * @return String 返回交易hash
   * @throws
   * @author Anne
   */
  public String sendTransactionFromWallet(String fromAddress, String toAddress,
      String password, String amount) {
    BigInteger nonceValue = getNonceValue(Numeric.prependHexPrefix(fromAddress));
    EtherWallet etherWallet = this.getHotwallet();
    if (etherWallet == null) {
      logger
          .error("Hot wallets are not initialized or not saved to the database!");
      return null;
    }
    String privateKey = etherWallet.getPrivateKeys();
//    BigInteger pub = Sign.publicKeyFromPrivate(new BigInteger(privateKey));
    ECKeyPair ecKeyPair = ECKeyPair.create(new BigInteger(AES.decrypt(privateKey)));
    logger.info("privateKey=" + privateKey);
    Credentials credentials = Credentials.create(ecKeyPair);
    String txHash = doSendTransaction(credentials, nonceValue, getGasPrice(),
        Transfer.GAS_LIMIT, toAddress, new BigInteger(amount));
    return txHash;
  }

  /**
  *
 * @Title: sendTransactionFromUser
 * @Description: TODO(主要用于由用户向公司钱包交易，充值)
 * @param @param fromAddress 转账钱包地址
 * @param @param toAddress 转账目标钱包地址
 * @param @param amount 交易金额
 * @return String    返回交易hash
 * @throws
 * @author Anne
  */
 public String sendTransactionFromUser(String fromAddress, String toAddress,String amount){
     UserCoin userCoin =userCoinMapper.getUserCoinByAddressAndCurrency(fromAddress, Constant.CURRENCY_ETH);
     if(userCoin == null || userCoin.getAddress() == null){
         logger.error("fromAddress:{} not find userCoin data!", fromAddress);
         return null;
     }
     String privateKey = AES.decrypt(userCoin.getPrivateKey());
     ECKeyPair ecKeyPair = ECKeyPair.create(new BigInteger(privateKey));
     Credentials credentials = Credentials.create(ecKeyPair);
     String txHash = sendTx(credentials, fromAddress, toAddress, new BigDecimal(amount));
     logger.info("txHash = "+txHash);
     return txHash;
 }

 public UserCoin getUserCoinByAddress(String address, String currency){
   return userCoinMapper.getUserCoinByAddressAndCurrency(address, currency);
 }

  /**
   *
   * @Title: doSendTransaction
   * @Description: TODO(发送交易)
   * @param credentials
   *          钱包凭证
   * @param nonce
   *          是一个递增的数字值，用于标识唯一交易
   * @param gasPrice
   *          每个ETHER兑换多少个GAS
   * @param gasLimit
   *          执行交易的总花费
   * @param to
   *          转账目标钱包地址
   * @param value
   *          交易数量
   * @return String 返回交易hash
   * @throws
   * @author Anne
   */
  public String doSendTransaction(Credentials credentials, BigInteger nonce,
      BigInteger gasPrice, BigInteger gasLimit, String to, BigInteger value) {
    String txHash = null;
    // create our transaction
    RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
        nonce, gasPrice, gasLimit, to, value);
    // sign & send our transaction
    byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction,
        credentials);
    String hexValue = Numeric.toHexString(signedMessage);
    try {
      EthSendTransaction ethSendTransaction = web3j
          .ethSendRawTransaction(hexValue).sendAsync().get();
      if (ethSendTransaction != null) {
        txHash = ethSendTransaction.getTransactionHash();
        logger.info("txHash=" + txHash);
      }
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
    logger.info("tx end");
    return txHash;
  }

  @Transactional
  public synchronized Map<String, Object> sendMoney(WithdrawLog withdrawLog)
      throws ExecutionException, InterruptedException {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("code", "success");
    /**
     *
     * 1、判断用户账号是否存在。
     * 2、提币，记录提币信息
     * 3、正常返回hash，异常返回null
     *
     */
    UserCoin userCoin = userCoinMapper.getUserCoinByAccountAndCurrency(withdrawLog.getAccount(),Constant.CURRENCY_ETH);
    if (userCoin == null){
      map.put("code", "fail");
      logger.info("根据account："+withdrawLog.getAccount()+"查询UserCoin表不存在对应数据！");
      return map;
    }
    String txHash = "";
    BigDecimal transMoney = withdrawLog.getMoney();
    transMoney = Convert.toWei(transMoney, Convert.Unit.ETHER);
    try {
      txHash = sendToOuterUser(withdrawLog.getToAddress(), transMoney);
      WithdrawLog twithdrawLog = new WithdrawLog();
      twithdrawLog.setOrderNo(withdrawLog.getOrderNo());
      twithdrawLog.setAccount(withdrawLog.getAccount());
      twithdrawLog.setToAddress(withdrawLog.getToAddress());
      twithdrawLog.setMoney(withdrawLog.getMoney());
      twithdrawLog.setCurrency(Constant.CURRENCY_ETH);
      twithdrawLog.setCreateTime(DateUtil.getCurrentDate());
      twithdrawLog.setTxHash(txHash);
      twithdrawLog.setStatus(WalletConstant.SEND);
      if(withdrawLogMapper.insertSelective(twithdrawLog)>0){
        map.put("data", txHash);
      }else{
        logger.info("根据account："+withdrawLog.getAccount()+"提币保存成功！");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return map;
  }

  /**
   *
   * @Title: sendTx
   * @Description: TODO(发送交易，用于通过临时钱包向其他钱包转账)
   * @param credentials
   *          对临时钱包私钥key进行密码文Hex编码
   * @param from
   *          fromAddress转出钱包地址
   * @param to
   *          toAddress转入钱包地址
   * @param val
   *          交易金额
   * @return boolean 交易成功:返回交易哈希，否则返回空
   * @throws
   * @author Anne
   */
  public String sendTx(Credentials credentials, String from, String to,
      BigDecimal val) {
    try {
      BigInteger nonce = getNonceValue(from);// .add(BigInteger.ONE);
      logger.info("send before :nonce="+nonce+",gasPrice="+getGasPrice()+",gasLimit="+Transfer.GAS_LIMIT+",toAddress="+to+",value="+val.toBigInteger());
      // create our transaction
      BigInteger gasCost = getGasPrice().multiply(Transfer.GAS_LIMIT);
      BigDecimal sendValue = new BigDecimal(val.toBigInteger().subtract(gasCost));
      RawTransaction rawTransaction = RawTransaction
          .createEtherTransaction(nonce, getGasPrice(),
              Transfer.GAS_LIMIT, to, sendValue.toBigInteger());
      logger.info("ecKeyPair = " + credentials.getEcKeyPair()
          + ",privateKey = " + credentials.getEcKeyPair().getPrivateKey() + ","
          + Transfer.GAS_PRICE + ", " + Transfer.GAS_LIMIT.toString()
          + ", val.toBigInteger()" + sendValue.toBigInteger().toString());
      // sign & send our transaction
      byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction,
          credentials);
      String hexValue = Numeric.toHexString(signedMessage);
      logger.info("signedMessage="+signedMessage+",hexValue="+hexValue);
      EthSendTransaction ethSendTransaction = web3j
          .ethSendRawTransaction(hexValue).sendAsync().get();
      if (ethSendTransaction != null && !ethSendTransaction.hasError()) {
        String txHash = ethSendTransaction.getTransactionHash();
        logger.info("txHash=" + txHash);
        if (txHash != null) {
          return txHash;
        } else {
          logger.error("from:" + from + "\tto:" + to + "\tvalue:" + sendValue
              + "! sendTx failed: txHashVal is null");
        }
      } else {
        logger.error("from:" + from + "\tto:" + to + "\tvalue:" + sendValue
            + "! sendTx failed: ethSendTransaction.hasError(), error: "
            + ethSendTransaction.getError().getMessage());
      }
    } catch (Exception e) {
      e.printStackTrace();
      logger.error("from:" + from + "\tto:" + to + "\tvalue:" + val
          + "! sendTx failed:" + e.toString());
    }
    return null;
  }

  /**
   *
   * @Title: sendToOuterUser
   * @Description: TODO(发送交易到用户，提币)
   * @param toAddress
   *          钱包地址
   * @param amount
   *          交易金额
   * @throws Exception
   * @throws DecoderException
   * @throws
   * @author Anne
   */
  public String sendToOuterUser(String toAddress, BigDecimal amount)
      throws DecoderException, Exception {
    String txHashVal = null;
    EtherWallet info = this.getHotwallet();
    String privateKey = AES.decrypt(info.getPrivateKeys());
//    logger.info("解密后热钱包私钥="+privateKey);
//    BigInteger pub = Sign.publicKeyFromPrivate(new BigInteger(privateKey));
    BigInteger privateKeyIntValue = new BigInteger(privateKey, 16);
    ECKeyPair ecKeyPair = ECKeyPair.create(privateKeyIntValue);
    Credentials credentials = Credentials.create(ecKeyPair);
    txHashVal = this.sendTx(credentials, info.getAddress(), toAddress, amount);
    logger.info("sendTx, hotWallet:{} to userWallet:{} txHashVal:{}",
        info.getAddress(), toAddress, txHashVal);
    return txHashVal;
  }
}
