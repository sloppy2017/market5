package com.c2b.coin.account.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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
import com.c2b.coin.account.entity.DigitalCoin;
import com.c2b.coin.account.entity.UserAccount;
import com.c2b.coin.account.feign.UserClient;
import com.c2b.coin.account.mapper.AssetChangeLogMapper;
import com.c2b.coin.account.mapper.AssetLogMapper;
import com.c2b.coin.account.mapper.DigitalCoinMapper;
import com.c2b.coin.account.mapper.UserAccountMapper;
import com.c2b.coin.account.service.IActivityAssetService;
import com.c2b.coin.account.service.IDigitalCoinWithDrawAndDeposit;
import com.c2b.coin.account.service.IMessageSendService;
import com.c2b.coin.account.util.MqUtil;
import com.c2b.coin.account.util.SpringUtil;
import com.c2b.coin.account.vo.MessageVo;
import com.c2b.coin.common.AssetChangeConstant;
import com.c2b.coin.common.Constants;
import com.c2b.coin.common.DateUtil;
import com.c2b.coin.common.MarketingActivityType;
import com.c2b.coin.common.MessageEnum;

@Service
public class ActivityAssetService implements IActivityAssetService{

	@Autowired
	private IMessageSendService messageSendService;

	@Autowired
	MqUtil mqUtil;
	@Resource(name="messageQueue")
	Queue messageQueue;
	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;
	@Resource(name="assetLogQueue")
	Queue assetLogQueue;
	@Autowired
	private UserAccountMapper userAccountMapper;
	@Autowired
	private RestTemplate restTemplate;
	@Value(value = "${wallet.orderNoCreate}")
	private String orderNoUrl;
	@Autowired
	private UserClient userClient;
	@Autowired
	private AssetLogMapper assetLogMapper;
	@Autowired
	private AssetChangeLogMapper assetChangeLogMapper;
	@Autowired
  private DigitalCoinMapper digitalCoinMapper;
	@Autowired
  private UserAccountAssetService userAccountAssetService;

	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor = Exception.class)
	public void addActivityAsset(long userId, String userName, int currencyType, BigDecimal amount,int operationType, String remark) {
		//获取用户资产
		UserAccount	userAccount = this.userAccountMapper.selectByUserIdAndCurrencyTypeForUpdate(userId, currencyType);
		// 未找到账户，初始化账户
		if (null == userAccount){
      DigitalCoin digitalCoin = digitalCoinMapper.selectByPrimaryKey(currencyType);
      userAccount = userAccountAssetService.addUserAccount(currencyType, digitalCoin.getCoinName(), userId, userName, this.getAddressFromWallet(digitalCoin.getCoinName(), userName));
    }
		//1.生成assetLog
		AssetLog assetLog = this.createAssetLog(userAccount, currencyType, userName, amount,operationType, remark);
		this.assetLogMapper.insert(assetLog);
		//2.插入资产变更记录
		AssetChangeLog assetChangeLog=this.createAssetChangeLog(userId, userName, assetLog.getOrderNo(), AssetChangeConstant.INCREASE, currencyType, amount);
		BigDecimal preBalance=userAccount.getTotalAmount();
		BigDecimal afterBalance=preBalance.add(amount);
		assetChangeLog.setPreBalance(preBalance);
		assetChangeLog.setAfterBalance(afterBalance);
		this.assetChangeLogMapper.insert(assetChangeLog);
		//3.变更用户资产
		userAccount.setTotalAmount(userAccount.getTotalAmount().add(amount));
		userAccount.setAvailableAmount(userAccount.getAvailableAmount().add(amount));
		userAccountMapper.updateByPrimaryKey(userAccount);
		//4.发送消息和mq
		//发送消息
		messageSendService.insertToDB(userAccount.getUserId(), userName, Constants.USER_NEWS_DEPOSIT, amount, assetLog.getAfterAsset());
    UserAccount finalUserAccount = userAccount;

    taskExecutor.execute(() -> {
      MessageVo messageVo = null;
      if (remark.equals(MarketingActivityType.REGISTER)){
        messageVo = MessageVo.initMessageVo(userId, MessageEnum.SMS_ACTIVITY_REGISSTER_CHARGE_COIN, userName, finalUserAccount.getCurrencyName(), amount);
      }else{
        messageVo = MessageVo.initMessageVo(userId, MessageEnum.SMS_ACTIVITY_TRANSFER_CHARGE_COIN, userName, finalUserAccount.getCurrencyName(), amount);
      }
			mqUtil.sendMessageQueue(messageQueue,messageVo);
		});
		sendAssetLogMQ(assetLog);
	}

  @Override
  public List<Map<String, Object>> getActivityCoin(String userId, String activityName) {
    List<Map<String, Object>> list = new ArrayList<>();
    list.addAll(assetLogMapper.selectActivityCoin(Long.parseLong(userId), activityName));
    return list;
  }

  private void sendAssetLogMQ(final AssetLog assetLog) {
		taskExecutor.execute(() -> {
			mqUtil.sendMessageQueue(assetLogQueue, assetLog);
		});
	}

	private AssetLog createAssetLog(UserAccount userAccount,int currencyType, String userName, BigDecimal amount,int operationType, String remark) {
		AssetLog assetLog = new AssetLog();
		assetLog.setUserId(userAccount.getUserId());
		assetLog.setUsername(userName);
		//1.生成唯一的订单号
		String res = this.restTemplate.getForObject(orderNoUrl, String.class);
		Map map = (Map) JSON.parse(res);
		String orderNo = map.get("id").toString();
		assetLog.setOrderNo(orderNo);
		assetLog.setExternalOrderNo(orderNo);
		assetLog.setCreatetime(DateUtil.getCurrentTimestamp());
		assetLog.setOperationType(operationType);
		assetLog.setCurrencyType(currencyType);
		assetLog.setCurrencyName(userAccount.getCurrencyName());
		assetLog.setAsset(amount);
		assetLog.setPoundage(new BigDecimal("0"));
		assetLog.setActualAsset(amount);
		assetLog.setRemark(remark);
		assetLog.setAfterAsset(userAccount.getTotalAmount().add(amount));
		assetLog.setResult(AssetChangeConstant.RESULT_CONFIRM);
		assetLog.setResultResult(AssetChangeConstant.RESULT_RESULT_CONFIRM);
		assetLog.setAuditResult(AssetChangeConstant.AUDIT_RESULT_SUCCESS);
		assetLog.setStatus(AssetChangeConstant.STATUS_SUCCESS);
		return assetLog;
	}

	private AssetChangeLog createAssetChangeLog(long userId,String userName,String orderNo,int bizType,int currencyType,BigDecimal amount) {
		AssetChangeLog assetChangeLog=new AssetChangeLog();
		assetChangeLog.setUserId(userId);
		assetChangeLog.setUsername(userName);
		assetChangeLog.setOrderNo(orderNo);
		assetChangeLog.setBizType(bizType);
		assetChangeLog.setCurrencyType(currencyType);
		assetChangeLog.setChangeAsset(amount);
		assetChangeLog.setUpdateTime(DateUtil.getCurrentTimestamp());
		return assetChangeLog;
	}
	private String getAddressFromWallet(String currencyName, String userName) {
		IDigitalCoinWithDrawAndDeposit digitalCoinWithDrawAndDeposit = (IDigitalCoinWithDrawAndDeposit) SpringUtil.getBean(currencyName + "WithDrawAndDeposit");
		String address = digitalCoinWithDrawAndDeposit.getAddress(userName);
		return address;
	}
}
