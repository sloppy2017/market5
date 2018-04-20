package com.c2b.coin.account.service.impl;

import java.math.BigDecimal;
import java.util.Map;

import javax.annotation.Resource;
import javax.jms.Queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.c2b.coin.account.entity.AssetChangeLog;
import com.c2b.coin.account.entity.AssetLog;
import com.c2b.coin.account.entity.UserAccount;
import com.c2b.coin.account.feign.UserClient;
import com.c2b.coin.account.mapper.AssetChangeLogMapper;
import com.c2b.coin.account.mapper.AssetLogMapper;
import com.c2b.coin.account.mapper.UserAccountMapper;
import com.c2b.coin.account.service.IDepositService;
import com.c2b.coin.account.service.IDigitalCoinWithDrawAndDeposit;
import com.c2b.coin.account.service.IMessageSendService;
import com.c2b.coin.account.service.IUserAccountAssetService;
import com.c2b.coin.account.util.MqUtil;
import com.c2b.coin.account.util.SpringUtil;
import com.c2b.coin.account.vo.MessageVo;
import com.c2b.coin.common.AssetChangeConstant;
import com.c2b.coin.common.Constants;
import com.c2b.coin.common.DateUtil;
import com.c2b.coin.common.MessageEnum;

@Service
public class DepositService implements IDepositService {

  @Autowired
  private UserAccountMapper userAccountMapper;
  @Value(value = "${wallet.getDepositAddress}")
  private String depositAddressUrl;
  @Value(value = "${wallet.orderNoCreate}")
  private String orderNoUrl;
  @Autowired
  private RestTemplate restTemplate;
  @Autowired
  private AssetLogMapper assetLogMapper;
  @Autowired
  private AssetChangeLogMapper assetChangeLogMapper;

  @Autowired
  private IUserAccountAssetService userAccountAssetServcie;

  @Autowired
  private IMessageSendService messageSendService;
  @Resource(name="messageQueue")
  Queue messageQueue;

  @Autowired
  UserClient userClient;
  @Autowired
  private ThreadPoolTaskExecutor taskExecutor;
  @Resource(name = "assetLogQueue")
  Queue assetLogQueue;
  @Autowired
  MqUtil mqUtil;


  /**
   * 添加用户资金账户账户地址
   *
   * @param currencyName
   * @param userName
   */

  private String getAddressFromWallet(String currencyName, String userName) {
    IDigitalCoinWithDrawAndDeposit digitalCoinWithDrawAndDeposit = (IDigitalCoinWithDrawAndDeposit) SpringUtil.getBean(currencyName + "WithDrawAndDeposit");
    String address = digitalCoinWithDrawAndDeposit.getAddress(userName);
    return address;
  }

  @Override
  public String getAddress(long userId, String userName, int currencyType, String currencyName) {
    // 1.获取用户账户
    UserAccount userAccount = new UserAccount();
    userAccount.setUserId(userId);
    userAccount.setCurrencyType(currencyType);
    userAccount = this.userAccountMapper.selectOne(userAccount);
    // 2.对账户进行校验
    if (userAccount == null || userAccount.getAccountAddress() == null) {
      String address = this.getAddressFromWallet(currencyName, userName);
      if (address == null) {
        throw new RuntimeException("充币地址获取异常，检查区块链");
      }
      userAccount = userAccountAssetServcie.addUserAccount(currencyType, currencyName, userId, userName, address);
    }
    return userAccount.getAccountAddress();
  }


  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void broadcastCallback(String address, int currencyType, String userName, String hxId, BigDecimal amount) {
    //1.查询账户信息
    UserAccount userAccount = new UserAccount();
    userAccount.setAccountAddress(address);
    userAccount.setCurrencyType(currencyType);
    userAccount = this.userAccountMapper.selectOne(userAccount);
    //2.创建充值记录表
    AssetLog assetLog = this.createAssetLog(userAccount, address, currencyType, userName, hxId, amount);
    //3.插入充值记录
    this.assetLogMapper.insert(assetLog);
    sendAssetLogMQ(assetLog);
  }

  private AssetLog createAssetLog(UserAccount userAccount, String address, int currencyType, String userName, String hxId, BigDecimal amount) {
    AssetLog assetLog = new AssetLog();
    assetLog.setUserId(userAccount.getUserId());
    assetLog.setUsername(userName);
    //1.生成唯一的订单号
    String res = this.restTemplate.getForObject(orderNoUrl, String.class);
    Map map = (Map) JSON.parse(res);
    String orderNo = map.get("id").toString();
    assetLog.setOrderNo(orderNo);
    assetLog.setExternalOrderNo(hxId);
    assetLog.setCreatetime(DateUtil.getCurrentTimestamp());
    assetLog.setOperationType(AssetChangeConstant.DEPOSIT);
    assetLog.setCurrencyType(currencyType);
    assetLog.setCurrencyName(userAccount.getCurrencyName());
    assetLog.setAsset(amount);
    assetLog.setPoundage(new BigDecimal("0"));
    assetLog.setActualAsset(amount);
    assetLog.setAfterAsset(userAccount.getTotalAmount().add(amount));
    assetLog.setAddress(address);
    assetLog.setResult(AssetChangeConstant.RESULT_PENDING);
    assetLog.setResultResult(AssetChangeConstant.RESULT_RESULT_PENDING);
    assetLog.setAuditResult(AssetChangeConstant.AUDIT_RESULT_WAITING);
    assetLog.setStatus(AssetChangeConstant.STATUS_WAITING);
    return assetLog;
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void confirmCallback(String address, int currencyType, String userName, String hxId, BigDecimal amount) {
    //1.修改充值信息
    AssetLog assetLog = new AssetLog();
    assetLog.setExternalOrderNo(hxId);
    assetLog.setCurrencyType(currencyType);
    assetLog = this.assetLogMapper.selectOne(assetLog);
    assetLog.setResult(AssetChangeConstant.RESULT_CONFIRM);
    assetLog.setResultResult(AssetChangeConstant.RESULT_RESULT_CONFIRM);
    assetLog.setAuditResult(AssetChangeConstant.AUDIT_RESULT_SUCCESS);
    assetLog.setStatus(AssetChangeConstant.STATUS_SUCCESS);
    this.assetLogMapper.updateByPrimaryKey(assetLog);
    //2.获取账户资金
    UserAccount userAccount = userAccountMapper.selectByAddressAndCurrencyTypeForUpdate(address, currencyType);
    //3.插入资产变动信息
    this.assetChange(userName, assetLog.getOrderNo(), AssetChangeConstant.DEPOSIT, amount, userAccount);
    //3.修改资金账互数据
    userAccount.setTotalAmount(userAccount.getTotalAmount().add(amount));
    userAccount.setAvailableAmount(userAccount.getAvailableAmount().add(amount));
    userAccount.setUpdateTime(DateUtil.getCurrentTimestamp());
    this.userAccountMapper.updateByPrimaryKey(userAccount);

    //发送消息
    messageSendService.insertToDB(userAccount.getUserId(), userName, Constants.USER_NEWS_DEPOSIT, amount, assetLog.getAfterAsset());
    taskExecutor.execute(() -> {
      //发送mq
      MessageVo messageVo = MessageVo.initMessageVo(userAccount.getUserId(), MessageEnum.EMAIL_CHARGE_COIN, userName, userAccount.getCurrencyName(), amount);
      mqUtil.sendMessageQueue(messageQueue, messageVo);
    });
    sendAssetLogMQ(assetLog);

  }

  private void sendAssetLogMQ(final AssetLog assetLog) {
    taskExecutor.execute(() -> {
      mqUtil.sendMessageQueue(assetLogQueue, assetLog);
    });
  }

  private void assetChange(String userName, String orderNo, int bizType,
                           BigDecimal amount, UserAccount userAccount) {

    AssetChangeLog assetChangeLog = new AssetChangeLog();
    assetChangeLog.setUserId(userAccount.getUserId());
    assetChangeLog.setUsername(userName);
    assetChangeLog.setOrderNo(orderNo);
    assetChangeLog.setCurrencyType(userAccount.getCurrencyType());
    assetChangeLog.setBizType(bizType);
    assetChangeLog.setAccountId(userAccount.getId());
    assetChangeLog.setPreBalance(userAccount.getTotalAmount());
    assetChangeLog.setAfterBalance(assetChangeLog.getPreBalance().add(amount));
    assetChangeLog.setUpdateTime(DateUtil.getCurrentTimestamp());
    assetChangeLog.setChangeAsset(amount);
    this.assetChangeLogMapper.insert(assetChangeLog);

  }

}
