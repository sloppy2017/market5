package com.c2b.coin.account.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jms.Queue;

import org.apache.commons.lang3.StringUtils;
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
import com.c2b.coin.account.entity.WithdrawAddressLog;
import com.c2b.coin.account.exception.AssetChangeException;
import com.c2b.coin.account.feign.UserClient;
import com.c2b.coin.account.mapper.AssetChangeLogMapper;
import com.c2b.coin.account.mapper.AssetLogMapper;
import com.c2b.coin.account.mapper.UserAccountMapper;
import com.c2b.coin.account.mapper.WithdrawAddressLogMapper;
import com.c2b.coin.account.service.IDigitalCoinWithDrawAndDeposit;
import com.c2b.coin.account.service.IMessageSendService;
import com.c2b.coin.account.service.IWithdrawService;
import com.c2b.coin.account.util.MqUtil;
import com.c2b.coin.account.util.SpringUtil;
import com.c2b.coin.account.vo.MessageVo;
import com.c2b.coin.common.AjaxResponse;
import com.c2b.coin.common.AssetChangeConstant;
import com.c2b.coin.common.Constants;
import com.c2b.coin.common.DateUtil;
import com.c2b.coin.common.MessageEnum;
import com.c2b.coin.common.enumeration.ErrorMsgEnum;

@Service
public class WithdrawService implements IWithdrawService {

	@Autowired
	private WithdrawAddressLogMapper withdrawAddressLogMapper;
	@Autowired
	private AssetLogMapper assetLogMapper;
	@Autowired
	private UserAccountMapper userAccountMapper;
	@Autowired
	private AssetChangeLogMapper assetChangeLogMapper;
	@Autowired
	private UserClient userClient;

	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;

	@Autowired
	private IMessageSendService messageSendService;

	@Value(value = "${wallet.orderNoCreate}")
	private String orderNoUrl;

	@Value(value = "${wallet.withDraw}")
	private String withDrawUrl;

	@Autowired
	MqUtil mqUtil;

	@Resource(name="messageQueue")
	Queue messageQueue;

	@Resource(name="assetLogQueue")
	Queue assetLogQueue;

	@Override
	public void addAddress(WithdrawAddressLog w) {
		this.withdrawAddressLogMapper.insert(w);
	}

	@Override
	public List<WithdrawAddressLog> getAddressList(Integer currencyType, long userId) {
		WithdrawAddressLog w = new WithdrawAddressLog();
		w.setCurrencyType(currencyType);
		w.setUserId(userId);
		return this.withdrawAddressLogMapper.select(w);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void withdraw(long userId, int currencyType, String currencyName, String address, String remark, BigDecimal amount, String userName) {
		//查询出操作前的币种数量
		//获取唯一订单号
		this.checkUserAcount(currencyType,address);

		String orderNo = this.getOrderNo();
		//插入充币/提币表,设置状态待审核
		AssetLog assetLog = new AssetLog();
		assetLog.setUserId(userId);
		assetLog.setUsername(userName);
		assetLog.setOrderNo(orderNo);
		assetLog.setCreatetime(DateUtil.getCurrentTimestamp());
		assetLog.setOperationType(AssetChangeConstant.WITHDRAW);
		assetLog.setCurrencyType(currencyType);
		assetLog.setCurrencyName(currencyName);
		assetLog.setAsset(amount);
		//计算手续费和真实到账
		this.calculateFee(assetLog, amount);
		//冻结用户资产
		this.freezingAsset(assetLog);
		assetLog.setAddress(address);
		assetLog.setRemark(remark);
		assetLog.setResult(AssetChangeConstant.RESULT_PENDING);
		assetLog.setResultResult(AssetChangeConstant.RESULT_RESULT_PENDING);
		assetLog.setStatus(AssetChangeConstant.STATUS_WAITING);
		assetLog.setAuditResult(AssetChangeConstant.AUDIT_RESULT_WAITING);


		//插入提币地址
		WithdrawAddressLog w = new WithdrawAddressLog();
		w.setCurrencyType(currencyType);
		w.setUserId(userId);
		w.setRemark(remark);
		w.setAddress(address);
		this.addAddress(w);
		//插入提现记录
		//通知提币接口
		try {
			this.noticeWithDrawService(assetLog.getCurrencyName(), assetLog);
		} catch (Exception e) {
			e.printStackTrace();
		}
//		if(hxId==null||StringUtils.isEmpty(hxId.trim())) {
//			throw new AssetChangeException(ErrorMsgEnum.WITHDRAW_NETWORK_BUSY, "区块链网络拥堵");
//		}

//		assetLog.setExternalOrderNo(hxId);
		this.assetLogMapper.insert(assetLog);


		//发送mq
		taskExecutor.execute(() -> {
			MessageVo messageVo = MessageVo.initMessageVo(userId,MessageEnum.EMAIL_WITHDRAW_APPLY, userName, assetLog.getCurrencyName(), amount);
			mqUtil.sendMessageQueue(messageQueue,messageVo);
		});

	}


	private void checkUserAcount(int currencyType, String address) {
		// TODO Auto-generated method stub
		UserAccount userAccount=new UserAccount();
		userAccount.setCurrencyType(currencyType);
		userAccount.setAccountAddress(address);
		List<UserAccount> userAccountList = this.userAccountMapper.select(userAccount);
		if(userAccountList.size()!=0) {
			throw new AssetChangeException(ErrorMsgEnum.WITHDRAW_ADDRESS_IS_PLATFORM, "提币账户不能为平台账户");
		}
	}

	private String noticeWithDrawService(String currencyName, AssetLog assetLog) {
		// 根据币种名拿取对应的实例::策略模式
		IDigitalCoinWithDrawAndDeposit digitalCoinWithDraw = (IDigitalCoinWithDrawAndDeposit) SpringUtil.getBean(currencyName + "WithDrawAndDeposit");
		return digitalCoinWithDraw.noticeWithDrawService(currencyName, assetLog);
	}

	/**
	 * 冻结用户资产
	 *
	 * @param assetLog
	 */
	private void freezingAsset(AssetLog assetLog) {
		// 1.冻结用户资产
		UserAccount userAccount = this.userAccountMapper.selectByUserIdAndCurrencyTypeForUpdate(assetLog.getUserId(), assetLog.getCurrencyType());
		BigDecimal amount = assetLog.getAsset();

		// 2.校验冻结金额
		if (userAccount.getAvailableAmount().compareTo(amount) == -1) {
			throw new AssetChangeException(ErrorMsgEnum.WITHDRAW_ERROR, "提币超出可用余额");
		}
		userAccount.setAvailableAmount(userAccount.getAvailableAmount().subtract(amount));
		userAccount.setFreezingAmount(userAccount.getFreezingAmount().add(amount));
		//插入用户资产
		this.userAccountMapper.updateByPrimaryKey(userAccount);
		//设置提现后资产
		assetLog.setAfterAsset(userAccount.getTotalAmount().subtract(amount));

	}

	/**
	 * 计算手续费
	 *
	 * @param assetLog
	 * @param amount
	 */
	private void calculateFee(AssetLog assetLog, BigDecimal amount) {
		//计算手续费
		IDigitalCoinWithDrawAndDeposit digitalCoinWithDraw = (IDigitalCoinWithDrawAndDeposit) SpringUtil.getBean(assetLog.getCurrencyName() + "WithDrawAndDeposit");
		BigDecimal poundage = digitalCoinWithDraw.caculateFee();
		assetLog.setPoundage(poundage);
		assetLog.setActualAsset(amount.subtract(poundage));
	}

	/**
	 * 获取全局唯一的订单号
	 *
	 * @return
	 */
	private String getOrderNo() {
		String res = this.restTemplate.getForObject(orderNoUrl, String.class);
		Map map = (Map) JSON.parse(res);
		String orderNo = map.get("id").toString();
		if(null==orderNo||StringUtils.isEmpty(orderNo)) {
			throw new AssetChangeException(ErrorMsgEnum.WITHDRAW_ERROR, "提币单号获取失败");
		}
		return orderNo;
	}

	@Override
	public AjaxResponse checkAssetPassword(long userId, String password,String verifyType,String verifyCode) {
		return userClient.checkPayPwdAndVerifyCode(userId, password, verifyType, verifyCode);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void confirmCallback(String address, int currencyType, String userName, String hxId, BigDecimal amount, String orderNo) {
		// 1.查询账户信息
		UserAccount userAccount = this.userAccountMapper.selectByAddressAndCurrencyTypeForUpdate(address, currencyType);

		// 2.修改充提币状态
		AssetLog assetLog = new AssetLog();
		assetLog.setCurrencyType(currencyType);
		assetLog.setOrderNo(orderNo);
		assetLog = this.assetLogMapper.selectOne(assetLog);
		assetLog.setResult(AssetChangeConstant.RESULT_CONFIRM);
		assetLog.setResultResult(AssetChangeConstant.RESULT_RESULT_CONFIRM);
		assetLog.setAuditResult(AssetChangeConstant.AUDIT_RESULT_SUCCESS);
		assetLog.setStatus(AssetChangeConstant.STATUS_SUCCESS);
		assetLog.setExternalOrderNo(hxId);
		this.assetLogMapper.updateByPrimaryKey(assetLog);
		// 3.修改账户信息
		userAccount.setFreezingAmount(userAccount.getFreezingAmount().subtract(assetLog.getAsset()));
		userAccount.setTotalAmount(userAccount.getTotalAmount().subtract(assetLog.getAsset()));
		userAccount.setUpdateTime(DateUtil.getCurrentTimestamp());
		this.userAccountMapper.updateByPrimaryKey(userAccount);
		// 4.插入变更记录
		this.assetChange(userName, assetLog.getOrderNo(), AssetChangeConstant.WITHDRAW, amount, userAccount);

		//发送消息
		messageSendService.insertToDB(userAccount.getUserId(), userName, Constants.USER_NEWS_WITHDRAW, amount, assetLog.getAfterAsset());

		//发送mq
		taskExecutor.execute(() -> {
			MessageVo messageVo = MessageVo.initMessageVo(userAccount.getUserId(), MessageEnum.EMAIL_WITHDRAW_SUCCESS, userName, userAccount.getCurrencyName(), amount);
			mqUtil.sendMessageQueue(messageQueue,messageVo);
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

	@Override
	public AjaxResponse checkPermission(long userId) {

		return userClient.checkWithdraw(userId);
	}

	@Override
	public boolean checkAmountLimit(String currencyName, BigDecimal amount) {
		IDigitalCoinWithDrawAndDeposit digitalCoinWithDrawAndDeposit = (IDigitalCoinWithDrawAndDeposit) SpringUtil.getBean(currencyName + "WithDrawAndDeposit");
		return digitalCoinWithDrawAndDeposit.checkAmountLimit(amount);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void approvalWithDraw(int id, String currencyName, int status) {
		AssetLog assetLog = null;
		switch (status) {
		case AssetChangeConstant.RESULT_CONFIRM:
			//审核通过
			assetLog = this.assetLogMapper.selectByPrimaryKey(id);
			assetLog.setResult(AssetChangeConstant.RESULT_CONFIRM);
			assetLog.setResultResult(AssetChangeConstant.RESULT_RESULT_CONFIRM);
			assetLog.setAuditResult(AssetChangeConstant.AUDIT_RESULT_SUCCESS);
			assetLog.setStatus(AssetChangeConstant.STATUS_CONFIRMING);
			String hxId = this.noticeWithDrawService(currencyName, assetLog);
			assetLog.setExternalOrderNo(hxId);
			this.assetLogMapper.updateByPrimaryKey(assetLog);
			break;

		case AssetChangeConstant.RESULT_FAILURE:
			assetLog = this.assetLogMapper.selectByPrimaryKey(id);
			assetLog.setResult(AssetChangeConstant.RESULT_FAILURE);
			assetLog.setResultResult(AssetChangeConstant.RESULT_RESULT_FAILURE);
			assetLog.setAuditResult(AssetChangeConstant.AUDIT_RESULT_FAILURE);
			this.assetLogMapper.updateByPrimaryKey(assetLog);
			break;
		default:
			break;
		}
	}

	@Override
	public List<AssetLog> getWithDrawList() {
		AssetLog assetLog = new AssetLog();
		assetLog.setOperationType(AssetChangeConstant.WITHDRAW);
		assetLog.setResult(AssetChangeConstant.RESULT_PENDING);
		return this.assetLogMapper.select(assetLog);
	}

	@Override
	public boolean checkAmountMax(Long userId, String currencyName,
			BigDecimal amount) {
		IDigitalCoinWithDrawAndDeposit digitalCoinWithDrawAndDeposit = (IDigitalCoinWithDrawAndDeposit) SpringUtil.getBean(currencyName + "WithDrawAndDeposit");
		return digitalCoinWithDrawAndDeposit.checkAmountMax(userId, amount);
	}

	@Override
	public BigDecimal getDailyMax(String currencyName) {
		IDigitalCoinWithDrawAndDeposit digitalCoinWithDrawAndDeposit = (IDigitalCoinWithDrawAndDeposit) SpringUtil.getBean(currencyName + "WithDrawAndDeposit");
		return digitalCoinWithDrawAndDeposit.getDailyMax();
	}

	@Override
	public BigDecimal getLimit(String currencyName) {
		IDigitalCoinWithDrawAndDeposit digitalCoinWithDrawAndDeposit = (IDigitalCoinWithDrawAndDeposit) SpringUtil.getBean(currencyName + "WithDrawAndDeposit");
		return digitalCoinWithDrawAndDeposit.getLimit();
	}

	@Override
	public void deleteAddress(long id) {
		this.withdrawAddressLogMapper.deleteByPrimaryKey(id);
	}

}
