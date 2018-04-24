//package com.c2b.ethWallet.service;
//
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.math.BigInteger;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.ExecutionException;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.web3j.abi.FunctionEncoder;
//import org.web3j.abi.TypeReference;
//import org.web3j.abi.datatypes.Address;
//import org.web3j.abi.datatypes.Function;
//import org.web3j.abi.datatypes.Type;
//import org.web3j.abi.datatypes.generated.Uint256;
//import org.web3j.crypto.Credentials;
//import org.web3j.crypto.ECKeyPair;
//import org.web3j.crypto.WalletUtils;
//import org.web3j.protocol.Web3j;
//import org.web3j.protocol.core.methods.response.EthSendTransaction;
//import org.web3j.protocol.core.methods.response.TransactionReceipt;
//import org.web3j.protocol.exceptions.TransactionTimeoutException;
//import org.web3j.tx.RawTransactionManager;
//import org.web3j.utils.Convert;
//import org.web3j.utils.Numeric;
//
//import com.c2b.coin.common.DateUtil;
//import com.c2b.coin.web.common.RedisUtil;
//import com.c2b.ethWallet.client.Web3JClient;
//import com.c2b.ethWallet.entity.UserCoin;
//import com.c2b.ethWallet.entity.WithdrawLog;
//import com.c2b.ethWallet.enumeration.ETHTokenContractAddressEnum;
//import com.c2b.ethWallet.mapper.UserCoinMapper;
//import com.c2b.ethWallet.mapper.WithdrawLogMapper;
//import com.c2b.ethWallet.task.EtherHotWalletTool;
//import com.c2b.ethWallet.task.token.unit.Transfer;
//import com.c2b.ethWallet.util.AES;
//import com.c2b.ethWallet.util.Constant;
//import com.c2b.ethWallet.util.DSToken;
//import com.c2b.ethWallet.util.EtherWalletUtil;
//import com.c2b.ethWallet.util.EtherWalletUtil.EtherWallet;
//import com.c2b.ethWallet.util.WalletConstant;
//
///**
// *
// * @author Anne
// * @Description: TODO(以太坊TOKEN钱包service)
// * @date 2017.12.22
// */
//@Service
//public class EthTokenWalletService {
//
//  @Autowired
//  UserCoinMapper userCoinMapper;
//
//  @Autowired
//  private WithdrawLogMapper withdrawLogMapper;
//
//  @Autowired
//  RedisUtil redisUtil;
//
//  Logger logger = LoggerFactory.getLogger(getClass());
//
//  private static String REDIS_ETH_TOKEN_MEMBER_ADDRESS = "ETH_TOKEN_MEMBER_ADDRESS";
//  
//  private static String REDIS_ETH_MEMBER_ADDRESS = "ETH_MEMBER_ADDRESS";
//
//  private static Web3j web3j = Web3JClient.getClient();
//
//  @Autowired
//  private SysGencodeService sysGencodeService;
//
//  @Autowired
//  private EtherHotWalletTool etherHotWalletTool;
//
//  @Value("${OMG.contractAddress}")
//  private String OMGContractAddress;
//
//  @Value("${OMG.hotAddress}")
//  private String OMGHotAddress;
//
//  @Value("${OMG.hotWalletPrivateKey}")
//  private String OMGHotWalletPrivateKey;
//
//  @Value("${SNT.contractAddress}")
//  private String SNTContractAddress;
//
//  @Value("${SNT.hotAddress}")
//  private String SNTHotAddress;
//
//  @Value("${SNT.hotWalletPrivateKey}")
//  private String SNTHotWalletPrivateKey;
//
//  @Value("${GNT.contractAddress}")
//  private String GNTContractAddress;
//
//  @Value("${GNT.hotAddress}")
//  private String GNTHotAddress;
//
//  @Value("${GNT.hotWalletPrivateKey}")
//  private String GNTHotWalletPrivateKey;
//
//  @Value("${POWR.contractAddress}")
//  private String POWRContractAddress;
//
//  @Value("${POWR.hotAddress}")
//  private String POWRHotAddress;
//
//  @Value("${POWR.hotWalletPrivateKey}")
//  private String POWRHotWalletPrivateKey;
//
//  @Value("${PKT.contractAddress}")
//  private String PKTContractAddress;
//
//  @Value("${PKT.hotAddress}")
//  private String PKTHotAddress;
//
//  @Value("${PKT.hotWalletPrivateKey}")
//  private String PKTHotWalletPrivateKey;
//  
//  @Value("${zg.contractAddress}")
//  private String zgContractAddress;
//
//  @Value("${zg.hotAddress}")
//  private String zgHotAddress;
//
//  @Value("${zg.hotWalletPrivateKey}")
//  private String zgHotWalletPrivateKey;
//  
//  int attempts = 20;
//  int sleepDuration = 15000;
//
//  /**
//   *
//   * @Title: isValidAddress
//   * @Description: TODO(验证以太坊TOKEN地址)
//   * @param @param address
//   * @param @return 设定文件
//   * @return boolean 返回类型
//   * @throws
//   * @author Anne
//   */
//  public boolean isValidAddress(String address) {
//    return EtherWalletUtil.isValidAddress(address);
//  }
//
//  /**
//   * 创建以太坊TOKEN钱包地址
//   *
//   * @param account
//   *          账号
//   * @param currency
//   *          TOKEN币种名称
//   * @return 钱包地址
//   */
//  public String createEthTokenWallet(String account, String currency) {
//    String address = null;
//    UserCoin ethUserCoin = userCoinMapper.getUserCoinByAccountAndCurrency(account,
//        Constant.CURRENCY_ETH);
//    if (ethUserCoin != null) {
//      if (this.checkExistAddress(account, currency)) {
//        logger
//            .error(
//                "account:{} currency:{} createEthWallet fail, because account is exist!",
//                account, currency);
//        return null;
//      }else{
//        redisUtil.zset(REDIS_ETH_TOKEN_MEMBER_ADDRESS + "_" + currency,
//            ethUserCoin.getAddress());
//        UserCoin userCoin = new UserCoin();
//        userCoin.setAccount(account);
//        userCoin.setCreateTime(DateUtil.getCurrentDate());
//        userCoin.setCurrency(currency);
//        userCoin.setAddress(ethUserCoin.getAddress());
//        userCoin.setPrivateKey(ethUserCoin.getPrivateKey());
//        userCoin.setUpdateTime(DateUtil.getCurrentDate());
//        if (userCoinMapper.insertSelective(userCoin) <= 0) {
//          logger.error("account: " + account
//              + "insert userCoin table fail!");
//        }
//      }
//    }else{
//      try {
//        EtherWallet etherTokenWallet = EtherWalletUtil.createWallet();
//        if (etherTokenWallet != null) {
//          if (WalletUtils.isValidAddress(etherTokenWallet.getAddress())) {
//            address = Numeric.prependHexPrefix(etherTokenWallet.getAddress());
//            if (address != null && !"".equals(address)) {
//              
//              redisUtil.zset(REDIS_ETH_MEMBER_ADDRESS, address);
//              UserCoin userCoin = new UserCoin();
//              userCoin.setAccount(account);
//              userCoin.setCreateTime(DateUtil.getCurrentDate());
//              userCoin.setCurrency("ETH");
//              userCoin.setAddress(address);
//              userCoin.setPrivateKey(AES.encrypt(etherTokenWallet.getPrivateKeys()
//                  .toString().getBytes()));
//              userCoin.setUpdateTime(DateUtil.getCurrentDate());
//              if (userCoinMapper.insertSelective(userCoin) <= 0) {
//                logger.error("account: " + account
//                    + "insert ETH userCoin table fail!");
//              }else{
//                redisUtil.zset(REDIS_ETH_TOKEN_MEMBER_ADDRESS + "_" + currency,
//                    address);
//                UserCoin tokenUserCoin = new UserCoin();
//                tokenUserCoin.setAccount(account);
//                tokenUserCoin.setCreateTime(DateUtil.getCurrentDate());
//                tokenUserCoin.setCurrency(currency);
//                tokenUserCoin.setAddress(address);
//                tokenUserCoin.setPrivateKey(AES.encrypt(etherTokenWallet
//                    .getPrivateKeys().toString().getBytes()));
//                tokenUserCoin.setUpdateTime(DateUtil.getCurrentDate());
//                if (userCoinMapper.insertSelective(tokenUserCoin) <= 0) {
//                  logger.error("account: " + account
//                      + "insert "+currency+" userCoin table fail!");
//                }
//              }
//            }
//          }
//        }
//      } catch (Exception e) {
//        e.printStackTrace();
//        logger.error("createEthTokenWallet error:{}", e.getMessage());
//      }
//    }
//    return address;
//  }
//
//  /**
//   * 检查该用户是否已存在TOKEN钱包地址
//   *
//   * @param userName
//   * @param currency
//   *          币种名称
//   * @return true：存在;false：不存在
//   */
//  public boolean checkExistAddress(String account, String currency) {
//    boolean result = true;
//    UserCoin userCoin = userCoinMapper.getUserCoinByAccountAndCurrency(account,
//        currency);
//    if (userCoin == null) {
//      result = false;
//    }
//    return result;
//  }
//
//  /**
//   * 根据账号和币种名称获取钱包地址
//   *
//   * @param account
//   *          账号
//   * @param currency
//   *          币种名称
//   * @return 钱包地址
//   */
//  public String getAddressByAccountAndCurrency(String account, String currency) {
//    UserCoin userCoin = userCoinMapper.getUserCoinByAccountAndCurrency(account,
//        currency);
//    if (userCoin != null) {
//      return userCoin.getAddress();
//    } else {
//      return null;
//    }
//  }
//
//  public UserCoin getUserCoinByAddress(String address, String currency){
//    return userCoinMapper.getUserCoinByAddressAndCurrency(address, currency);
//  }
//  
//  /**
//   * 
//   * @param TOKENAddress
//   *          token钱包地址
//   * @return token钱包地址余额
//   */
//  public BigDecimal getTOKENBalance(String TOKENAddress, String currency) {
//    Credentials credential = getETHTOKENCredentialsByAddress(TOKENAddress, currency);
//    UserCoin userCoin = userCoinMapper.getUserCoinByAddressAndCurrency(TOKENAddress, currency);
//    if (userCoin == null || userCoin.getAccount() == null) {
//      logger.error("fromAddress:{} not find userCoin data!", TOKENAddress);
//      return null;
//    }
//    String tokencurrency = userCoin.getCurrency();
//    String tokenContractAddress = getTOKENContractAddress(tokencurrency);
//    DSToken dsToken = DSToken.load(tokenContractAddress, web3j, credential,
//        Transfer.GAS_PRICE, Transfer.GAS_LIMIT);
//    Uint256 balance = null;
//    try {
//      balance = dsToken.balanceOf(new Address(TOKENAddress)).get();
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    } catch (ExecutionException e) {
//      e.printStackTrace();
//    }
//    return Convert.fromWei(balance.getValue().toString(), Convert.Unit.ETHER);
//  }
//  
//  /**
//   * 
//   * @param currency
//   *          token币种名称
//   * @return token合约地址
//   */
//  public String getTOKENContractAddress(String currency){
//    switch(ETHTokenContractAddressEnum.getETHTokenContractAddressEnum(currency)){
//    case zg_CONTRACT_ADDRESS:
//      return zgContractAddress;
//    case OMG_CONTRACT_ADDRESS:
//      return OMGContractAddress;
//    case SNT_CONTRACT_ADDRESS:
//      return SNTContractAddress;
//    case GNT_CONTRACT_ADDRESS:
//      return GNTContractAddress;
//    case POWR_CONTRACT_ADDRESS:
//      return POWRContractAddress;
//    case PKT_CONTRACT_ADDRESS:
//      return PKTContractAddress;
//    default:
//       return OMGContractAddress;
//    }
//  }
//  
//  /**
//   * 
//   * @param address
//   *          token地址
//   * @return 证书
//   */
//  public Credentials getETHTOKENCredentialsByAddress(String address, String currency) {
//    UserCoin userCoin = userCoinMapper.getUserCoinByAddressAndCurrency(address, currency);
//    if (userCoin == null || userCoin.getAccount() == null) {
//      logger.error("fromAddress:{} not find userCoin data!", address);
//      return null;
//    }
//    String privateKey = userCoin.getPrivateKey();
//    ECKeyPair ecKeyPair = ECKeyPair.create(new BigInteger(AES
//        .decrypt(privateKey)));
//    return Credentials.create(ecKeyPair);
//  }
//
//  /**
//   * 
//   * @param currency
//   *          token币种名称
//   * @return token热钱包地址
//   */
//  public String getTOKENHotAddress(String currency) {
//    switch (currency) {
//    case "zg":
//      return zgHotAddress;
//    case "OMG":
//      return OMGHotAddress;
//    case "SNT":
//      return SNTHotAddress;
//    case "GNT":
//      return GNTHotAddress;
//    case "POWR":
//      return POWRHotAddress;
//    case "PKT":
//      return PKTHotAddress;
//    default:
//      return OMGHotAddress;
//    }
//  }
//
//  /**
//   * 
//   * @param currency
//   *          token币种名称
//   * @return token热钱包私钥
//   */
//  public String getTOKENHotWalletPrivateKey(String currency) {
//    switch (currency) {
//    case "zg":
//      return zgHotWalletPrivateKey;
//    case "OMG":
//      return OMGHotWalletPrivateKey;
//    case "SNT":
//      return SNTHotWalletPrivateKey;
//    case "GNT":
//      return GNTHotWalletPrivateKey;
//    case "POWR":
//      return POWRHotWalletPrivateKey;
//    case "PKT":
//      return PKTHotWalletPrivateKey;
//    default:
//      return OMGHotWalletPrivateKey;
//    }
//  }
//
//  /**
//   * 
//   * @param currency
//   *          token币种名称
//   * @return token热钱包余额
//   */
//  public BigDecimal getTOKENHotWalletBalance(String currency) {
//    String TOKENHotAddress = getTOKENHotAddress(currency);
//    String TOKENHotWalletPrivateKey = getTOKENHotWalletPrivateKey(currency);
//    ECKeyPair ecKeyPair = ECKeyPair.create(new BigInteger(AES
//        .decrypt(TOKENHotWalletPrivateKey)));
//    Credentials credential = Credentials.create(ecKeyPair);
//    String tokenContractAddress = getTOKENContractAddress(currency);
//    DSToken dsToken = DSToken.load(tokenContractAddress, web3j, credential,
//        Transfer.GAS_PRICE, Transfer.GAS_LIMIT);
//    Uint256 balance = null;
//    try {
//      balance = dsToken.balanceOf(new Address(TOKENHotAddress)).get();
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    } catch (ExecutionException e) {
//      e.printStackTrace();
//    }
//    if (balance == null)
//      return null;
//    return Convert.fromWei(balance.getValue().toString(), Convert.Unit.ETHER);
//  }
//
//  /**
//   * 
//   * @param currency token币种名称
//   * @return 获取热钱包证书
//   */
//  public Credentials getHotwalletCredentials(String currency){
//    String tokenHotWalletPrivateKey = getTOKENHotWalletPrivateKey(currency);
//    ECKeyPair ecKeyPair = ECKeyPair.create(new BigInteger(AES.decrypt(tokenHotWalletPrivateKey)));
//    Credentials credentials = Credentials.create(ecKeyPair);
//    return  credentials;
//  }
//  
//  /**
//   * 
//   * @param withdrawLog
//   */
//  @Transactional
//  public synchronized Map<String, Object> sendTokenMoney(WithdrawLog withdrawLog) throws ExecutionException, InterruptedException {
//    /**
//    *
//    * 1、判断用户账号是否存在。 2、提币，记录提币信息 3、正常返回hash，异常返回null
//    *
//    */
//    Map<String, Object> map = new HashMap<String, Object>();
//    map.put("code", "success");
//    UserCoin userCoin = userCoinMapper.getUserCoinByAccountAndCurrency(
//        withdrawLog.getAccount(), withdrawLog.getCurrency());
//    if (userCoin == null) {
//      map.put("code", "fail");
//      logger.info("根据account："+withdrawLog.getAccount()+"和currency："+withdrawLog.getCurrency()+"查询UserCoin表不存在对应数据！");
//      return map;
//    }
//    BigDecimal actualWei = Convert.toWei(withdrawLog.getMoney(), Convert.Unit.ETHER);
//    //prepare
//    Address dstAddress = new Address(withdrawLog.getToAddress());
//    BigInteger sendAmountWei =actualWei.toBigInteger();
//    Uint256 amount = new Uint256(sendAmountWei);
//    Function function = new Function("transfer", Arrays.<Type>asList(dstAddress, amount), Collections.<TypeReference<?>>emptyList());
//    String data = FunctionEncoder.encode(function);
//    BigDecimal etherzero = BigDecimal.ZERO;
//
//    //issue sendtx
//    Credentials credential = getHotwalletCredentials(withdrawLog.getCurrency());
//    String tokenAddress = getTOKENContractAddress(withdrawLog.getCurrency());
//    EthSendTransaction transactionResponse = null;
//    try {
//        transactionResponse = sendContractTx(credential, tokenAddress, etherzero, Convert.Unit.ETHER, data);
//    } catch (InterruptedException e1) {
//        e1.printStackTrace();
//    } catch (ExecutionException e1) {
//        e1.printStackTrace();
//    } catch (TransactionTimeoutException e1) {
//        e1.printStackTrace();
//    }
//    //check tx isvalid or occure error
//    if (!transactionResponse.hasError()) {
//        String txHashVal = transactionResponse.getTransactionHash();
//        try {
//          WithdrawLog tokenWithdrawLog = new WithdrawLog();
//          tokenWithdrawLog.setOrderNo(withdrawLog.getOrderNo());
//          tokenWithdrawLog.setAccount(withdrawLog.getAccount());
//          tokenWithdrawLog.setToAddress(withdrawLog.getToAddress());
//          tokenWithdrawLog.setMoney(withdrawLog.getMoney());
//          tokenWithdrawLog.setCurrency(withdrawLog.getCurrency());
//          tokenWithdrawLog.setCreateTime(DateUtil.getCurrentDate());
//          tokenWithdrawLog.setTxHash(txHashVal);
//          tokenWithdrawLog.setStatus(WalletConstant.SEND);
//          if (withdrawLogMapper.insertSelective(tokenWithdrawLog) > 0) {
//            TransactionReceipt receipt = Transfer.getTransactionReceipt(web3j, credential, transactionResponse,attempts,sleepDuration);
//            if(receipt!=null && txHashVal.equalsIgnoreCase(receipt.getTransactionHash())){
//                logger.info("Withdraw request token: "+withdrawLog.getCurrency()+" amount:" + withdrawLog.getMoney() + " txHash：" + txHashVal + " for order:" + withdrawLog.getOrderNo() + " success.");
//                map.put("data", txHashVal);
//                return map;
//            }
//          } else {
//            logger.info("根据account："+withdrawLog.getAccount()+"和currency："+withdrawLog.getCurrency()+"提币保存成功！");
//          }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    } else {
//      logger.info("transfer toAddress:{}  Error processing transaction request:{}", withdrawLog.getToAddress(), transactionResponse.getError().getMessage());
//    }
//    return map;
//}
//  
//  /**
//   * 调用web3j的API发送交易
//   * @param credentials
//   * @param toAddress
//   * @param value
//   * @param unit
//   * @param data
//   * @return
//   * @throws InterruptedException
//   * @throws ExecutionException
//   * @throws TransactionTimeoutException
//   */
//  public EthSendTransaction sendContractTx(Credentials credentials,
//      String toAddress, BigDecimal value, Convert.Unit unit, String data)
//      throws InterruptedException, ExecutionException,
//      TransactionTimeoutException {
//    RawTransactionManager transactionManager = new RawTransactionManager(web3j,
//        credentials);
//    BigDecimal weiValue = Convert.toWei(value, unit);
//    if (!Numeric.isIntegerValue(weiValue)) {
//      throw new UnsupportedOperationException(
//          "Non decimal Wei value provided: " + value + " " + unit.toString()
//              + " = " + weiValue + " Wei");
//    } else {
//      EthSendTransaction ethSendTransaction = null;
//      try {
//        ethSendTransaction = transactionManager.sendTransaction(
//            Transfer.GAS_PRICE,
//            Transfer.GAS_LIMIT, 
//            toAddress, 
//            data, 
//            weiValue.toBigIntegerExact());
//      } catch (IOException e) {
//        e.printStackTrace();
//      }
//      return ethSendTransaction;
//    }
//  }
//
//}
