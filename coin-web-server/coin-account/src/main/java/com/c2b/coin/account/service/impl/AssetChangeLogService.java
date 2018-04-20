package com.c2b.coin.account.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.c2b.coin.account.entity.AssetChangeLog;
import com.c2b.coin.account.entity.AssetLog;
import com.c2b.coin.account.entity.DigitalCoin;
import com.c2b.coin.account.entity.UserAccount;
import com.c2b.coin.account.exception.AssetChangeException;
import com.c2b.coin.account.feign.UserClient;
import com.c2b.coin.account.mapper.AssetChangeLogMapper;
import com.c2b.coin.account.mapper.AssetLogMapper;
import com.c2b.coin.account.mapper.DigitalCoinMapper;
import com.c2b.coin.account.mapper.UserAccountMapper;
import com.c2b.coin.account.service.IAssetChangeLogService;
import com.c2b.coin.account.service.IDigitalCoinWithDrawAndDeposit;
import com.c2b.coin.account.util.SpringUtil;
import com.c2b.coin.common.AssetChangeConstant;
import com.c2b.coin.common.DateUtil;
import com.c2b.coin.common.enumeration.ErrorMsgEnum;

@Service
public class AssetChangeLogService implements IAssetChangeLogService{

	@Autowired
	private AssetChangeLogMapper assetChangeLogMapper;
	@Autowired
	private UserAccountMapper userAccountMapper;

	@Autowired
	private DigitalCoinMapper digitalCoinMapper;
	@Autowired
	private UserClient userClient;

	@Autowired
	private AssetLogMapper assetLogMapper;

	@Value(value = "${wallet.orderNoCreate}")
	private String orderNoUrl;

	@Autowired
	private RestTemplate restTemplate;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void assetChange(long userId, String userName, String orderNo, int bizType, int currencyType,
			BigDecimal amount) {
		// 根据变动类型，调用不同的逻辑
		AssetChangeLog assetChangeLog=new AssetChangeLog();
		assetChangeLog.setUserId(userId);
		assetChangeLog.setUsername(userName);
		assetChangeLog.setOrderNo(orderNo);
		assetChangeLog.setBizType(bizType);
		assetChangeLog.setCurrencyType(currencyType);
		assetChangeLog.setChangeAsset(amount);
		assetChangeLog.setUpdateTime(DateUtil.getCurrentTimestamp());
		switch (bizType) {
		case AssetChangeConstant.FREEZE:
			//冻结逻辑
			this.freeze(assetChangeLog);
			break;
		case AssetChangeConstant.UNFREEZE:
			//解冻逻辑
			this.unfreeze(assetChangeLog);
			break;
		default:
			break;
		}
		//
	}

	/**
	 * 解冻逻辑
	 * @param assetChangeLog
	 */
	private void unfreeze(AssetChangeLog assetChangeLog) {
		// 1.使用悲观锁去查询资金账户信息
		UserAccount userAccount=this.userAccountMapper.selectByUserIdAndCurrencyTypeForUpdate(assetChangeLog.getUserId(),assetChangeLog.getCurrencyType());
		//校验冻结金额是否够用
		BigDecimal changeAsset=assetChangeLog.getChangeAsset();
		if(userAccount.getFreezingAmount().compareTo(changeAsset)<0) {
			throw new AssetChangeException(ErrorMsgEnum.EXCEED_FREEZING_AMOUNT, "解冻的金额超限");
		}
		//2.计算变动后金额
		BigDecimal preBalance=userAccount.getTotalAmount();
		BigDecimal afterBalance=preBalance;
		//3.插入资产变更记录
		assetChangeLog.setPreBalance(preBalance);
		assetChangeLog.setAfterBalance(afterBalance);
		this.assetChangeLogMapper.insert(assetChangeLog);
		//4.对用户资产做出变更
		userAccount.setAvailableAmount(userAccount.getAvailableAmount().add(changeAsset));
		userAccount.setFreezingAmount(userAccount.getFreezingAmount().subtract(changeAsset));
		userAccount.setUpdateTime(DateUtil.getCurrentTimestamp());
		this.userAccountMapper.updateByPrimaryKey(userAccount);
	}

	/**
	 * 冻结逻辑
	 * @param assetChangeLog
	 */
	private void freeze(AssetChangeLog assetChangeLog) {
		// 1.使用悲观锁去查询资金账户信息
		UserAccount userAccount=this.userAccountMapper.selectByUserIdAndCurrencyTypeForUpdate(assetChangeLog.getUserId(),assetChangeLog.getCurrencyType());
		// 2.校验可以余额是否够用
		if(userAccount==null) {
			throw new AssetChangeException(ErrorMsgEnum.ASSET_BALANCE_NOT_ENOUGH, "可用余额不够");
		}
		BigDecimal changeAsset=assetChangeLog.getChangeAsset();
		if(userAccount.getAvailableAmount().compareTo(changeAsset)<0) {
			throw new AssetChangeException(ErrorMsgEnum.ASSET_BALANCE_NOT_ENOUGH, "可用余额不够");
		}
		// 3.计算变动后的金额
		BigDecimal preBalance=userAccount.getTotalAmount();
		BigDecimal afterBalance=preBalance;
		//4.插入资产变更记录
		assetChangeLog.setPreBalance(preBalance);
		assetChangeLog.setAfterBalance(afterBalance);
		this.assetChangeLogMapper.insert(assetChangeLog);
		//5.对用户资产做出变更
		userAccount.setAvailableAmount(userAccount.getAvailableAmount().subtract(changeAsset));
		userAccount.setFreezingAmount(userAccount.getFreezingAmount().add(changeAsset));
		userAccount.setUpdateTime(DateUtil.getCurrentTimestamp());
		this.userAccountMapper.updateByPrimaryKey(userAccount);

	}

	/**
	 * 减少逻辑
	 * @param assetChangeLog
	 * @param i
	 */
	private void decrease(UserAccount userAccount,AssetChangeLog assetChangeLog) {
		// 3.计算变动后的金额
		BigDecimal changeAsset = assetChangeLog.getChangeAsset();
		BigDecimal preBalance=userAccount.getTotalAmount();
		BigDecimal afterBalance=preBalance.subtract(changeAsset);
		//4.补充资产变更记录
		assetChangeLog.setPreBalance(preBalance);
		assetChangeLog.setAfterBalance(afterBalance);
		//5.对用户资产做出变更
		userAccount.setFreezingAmount(userAccount.getFreezingAmount().subtract(changeAsset));
		userAccount.setTotalAmount(userAccount.getTotalAmount().subtract(changeAsset));
		userAccount.setUpdateTime(DateUtil.getCurrentTimestamp());
		this.userAccountMapper.updateByPrimaryKey(userAccount);
	}

	/**
	 * 增加逻辑
	 * @param assetChangeLog
	 */
	private void increase(UserAccount userAccount,AssetChangeLog assetChangeLog) {
		//1.使用悲观锁去查询资金账户信息
		//2.计算变动后金额
		// 扣除交易费率
		BigDecimal changeAsset=assetChangeLog.getChangeAsset();
		BigDecimal preBalance=userAccount.getTotalAmount();
		BigDecimal afterBalance=preBalance.add(changeAsset);
		//3.补充资产变更记录
		assetChangeLog.setPreBalance(preBalance);
		assetChangeLog.setAfterBalance(afterBalance);
		//4.对用户资产做出变更
		userAccount.setAvailableAmount(userAccount.getAvailableAmount().add(changeAsset));
		userAccount.setTotalAmount(userAccount.getTotalAmount().add(changeAsset));
		userAccount.setUpdateTime(DateUtil.getCurrentTimestamp());
		this.userAccountMapper.updateByPrimaryKey(userAccount);
	}



	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void tradePairAssetChange(long buyerUserId, String buyerUserName, long sellerUserId, String sellerUserName,
			String orderNo, int baseCurrencyType, int targetCurrencyType, BigDecimal baseAmount,
			BigDecimal targetAmount,BigDecimal baseRate,BigDecimal targetRate) {
		//1.将一个交易对拆成买卖双方的四个资金账户，并获取四个资金账户信息（撮合成功不用校验账户）
		List<UserAccount> accountList=this.buildAccountList(buyerUserId,sellerUserId,baseCurrencyType,targetCurrencyType);
		//2.构造出四条资产变更记录
		List<AssetChangeLog> assetChangeLogList=this.buildChangeLog(buyerUserId, buyerUserName, sellerUserId, sellerUserName, orderNo, baseCurrencyType, targetCurrencyType, baseAmount, targetAmount,baseRate, targetRate);
		//3.处理资产变更
		this.handleAssetChange(accountList,assetChangeLogList,baseRate,targetRate);
		//4.插入资产变更记录
		this.assetChangeLogMapper.insertList(assetChangeLogList);


	}

	private void handleAssetChange(List<UserAccount> accountList, List<AssetChangeLog> assetChangeLogList, BigDecimal baseRate, BigDecimal targetRate) {
		// 处理资产变更
		//这里的集合已经按约定顺序排列好，顺序为1.买方基础 2.买方标的 3.卖方基础 4.卖方标的
		//TODO 这里扣减手续费
		this.decrease(accountList.get(0), assetChangeLogList.get(0));
		this.increase(accountList.get(1), assetChangeLogList.get(1));
		this.increase(accountList.get(2), assetChangeLogList.get(2));
		this.decrease(accountList.get(3), assetChangeLogList.get(3));
	}

	/**
	 * 构造资产变更记录集合
	 * @param buyerUserId
	 * @param buyerUserName
	 * @param sellerUserId
	 * @param sellerUserName
	 * @param orderNo
	 * @param baseCurrencyType
	 * @param targetCurrencyType
	 * @param baseAmount
	 * @param targetAmount
	 * @return
	 */
	private List<AssetChangeLog> buildChangeLog(long buyerUserId, String buyerUserName, long sellerUserId, String sellerUserName,
			String orderNo, int baseCurrencyType, int targetCurrencyType, BigDecimal baseAmount,
			BigDecimal targetAmount,BigDecimal baseRate,BigDecimal targetRate) {
		//买方基础货币减少
		AssetChangeLog buyerBase =this.createAssetChangeLog(buyerUserId, buyerUserName, orderNo, AssetChangeConstant.DECREASE, baseCurrencyType, baseAmount,new BigDecimal("0"));
		//买方标的货币增加
		AssetChangeLog buyerTarget =this.createAssetChangeLog(buyerUserId, buyerUserName, orderNo, AssetChangeConstant.INCREASE, targetCurrencyType, targetAmount.subtract(targetRate),targetRate);
		//卖方基础货币增加
		AssetChangeLog sellerBase =this.createAssetChangeLog(sellerUserId, sellerUserName, orderNo, AssetChangeConstant.INCREASE, baseCurrencyType, baseAmount.subtract(baseRate),baseRate);
		//卖方标的货币减少
		AssetChangeLog sellerTarget =this.createAssetChangeLog(sellerUserId, sellerUserName, orderNo, AssetChangeConstant.DECREASE, targetCurrencyType, targetAmount,new BigDecimal("0"));
		List<AssetChangeLog> list=new ArrayList<>();
		list.add(buyerBase);
		list.add(buyerTarget);
		list.add(sellerBase);
		list.add(sellerTarget);
		return list;
	}

	private AssetChangeLog createAssetChangeLog(long userId,String userName,String orderNo,int bizType,int currencyType,BigDecimal amount,BigDecimal poundage) {
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

  /**
   * 构造资金账户集合
   * @param buyerUserId
   * @param sellerUserId
   * @param baseCurrencyType
   * @param targetCurrencyType
   * @return
   */
	private List<UserAccount> buildAccountList(long buyerUserId, long sellerUserId, int baseCurrencyType,
			int targetCurrencyType) {

		//获取买方基础资金账户
		UserAccount buyerBaseAccount=this.userAccountMapper.selectByUserIdAndCurrencyTypeForUpdate(buyerUserId,baseCurrencyType );
		buyerBaseAccount=this.checkAccount(buyerBaseAccount,buyerUserId,baseCurrencyType);
		//获得买方标的资金账户
		UserAccount buyerTargetAccount=this.userAccountMapper.selectByUserIdAndCurrencyTypeForUpdate(buyerUserId, targetCurrencyType);
		buyerTargetAccount=this.checkAccount(buyerTargetAccount, buyerUserId, targetCurrencyType);
		//获得卖方基础资金账户
		UserAccount sellerBaseAccount=this.userAccountMapper.selectByUserIdAndCurrencyTypeForUpdate(sellerUserId, baseCurrencyType);
		sellerBaseAccount=this.checkAccount(sellerBaseAccount, sellerUserId, baseCurrencyType);
		//获得卖方标的资金账户
		UserAccount sellerTargetAccount=this.userAccountMapper.selectByUserIdAndCurrencyTypeForUpdate(sellerUserId, targetCurrencyType);
		sellerTargetAccount=this.checkAccount(sellerTargetAccount, sellerUserId, targetCurrencyType);
		List<UserAccount> list=new ArrayList<>();
		list.add(buyerBaseAccount);
		list.add(buyerTargetAccount);
		list.add(sellerBaseAccount);
		list.add(sellerTargetAccount);
		return list;
	}

	private UserAccount checkAccount(UserAccount account, long userId, int currencyType) {
		if(account==null) {
			account = new UserAccount();
			//初始化一个账户
			//TODO 根据用户id获取用户名
			Map<String,Object> map=userClient.findBuserByUserId(userId);
			String userName=map.get("username").toString();
			DigitalCoin coin=this.digitalCoinMapper.selectByPrimaryKey(currencyType);
			String currencyName=coin.getCoinName();
			account.setCurrencyName(currencyName);
			IDigitalCoinWithDrawAndDeposit digitalCoinWithDrawAndDeposit=(IDigitalCoinWithDrawAndDeposit) SpringUtil.getBean(currencyName+"WithDrawAndDeposit");
			String address=digitalCoinWithDrawAndDeposit.getAddress(userName);
			account.setUserId(userId);
			account.setCurrencyType(currencyType);
			account.setCurrencyName(currencyName);
			BigDecimal zero=new BigDecimal("0");
			account.setAvailableAmount(zero);
			account.setTotalAmount(zero);
			account.setFreezingAmount(zero);
			account.setAccountType(1);
			account.setCreatetime(DateUtil.getCurrentTimestamp());
			account.setUpdateTime(DateUtil.getCurrentTimestamp());
			account.setAccountAddress(address);
			//插入数据库
			this.userAccountMapper.insert(account);
		}
		return account;
	}

	/**
	 *  减免手续费接口
	 */
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void discountPoundage(long buyerUserId, String buyerUserName, long sellerUserId, String sellerUserName,
			String orderNo, int baseCurrencyType, int targetCurrencyType, BigDecimal baseAmount,
			BigDecimal targetAmount,String remark) {
    System.out.println(baseCurrencyType + ":" + baseAmount + "============" + targetCurrencyType + ":" + targetAmount);
		//1.获取买方标币/卖方基币
		List<UserAccount> accountList=new ArrayList<UserAccount>();
		//获得买方标的资金账户
		UserAccount buyerTargetAccount=this.userAccountMapper.selectByUserIdAndCurrencyTypeForUpdate(buyerUserId, targetCurrencyType);
		buyerTargetAccount=this.checkAccount(buyerTargetAccount, buyerUserId, targetCurrencyType);
		//获得卖方基础资金账户
		UserAccount sellerBaseAccount=this.userAccountMapper.selectByUserIdAndCurrencyTypeForUpdate(sellerUserId, baseCurrencyType);
		sellerBaseAccount=this.checkAccount(sellerBaseAccount, sellerUserId, baseCurrencyType);
		accountList.add(buyerTargetAccount);
		accountList.add(sellerBaseAccount);
		//5.在充提币记录表增加记录
		List<AssetLog> assetLogList=this.createAssetLogList(accountList,buyerUserName,sellerUserName,baseAmount,targetAmount, remark);
		this.assetLogMapper.insertList(assetLogList);
		//2.构造出两条资产变更记录
		List<AssetChangeLog> assetChangeLogList=this.buildDiscountPoundageChangeLog(buyerUserId, buyerUserName, sellerUserId, sellerUserName, orderNo, baseCurrencyType, targetCurrencyType, baseAmount, targetAmount);
		//3.处理资产变更
		this.handleDiscountPoundageAssetChange(accountList,assetChangeLogList);
		//4.插入资产变更记录
		this.assetChangeLogMapper.insertList(assetChangeLogList);


	}

	private List<AssetLog> createAssetLogList(List<UserAccount> accountList, String buyerUserName,String sellerUserName,BigDecimal baseAmount, BigDecimal targetAmount,String remark) {
		// 1.生成AssetlogList
		AssetLog buyerAssetLog=this.createAssetLog(accountList.get(0),buyerUserName,targetAmount,remark);
		AssetLog sellerAssetLog=this.createAssetLog(accountList.get(1),sellerUserName,baseAmount, remark);
		List<AssetLog> assetLogList=new ArrayList();
		assetLogList.add(buyerAssetLog);
		assetLogList.add(sellerAssetLog);
		return assetLogList;
	}

	private AssetLog createAssetLog(UserAccount userAccount, String userName, BigDecimal amount,String remark) {
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
		assetLog.setOperationType(AssetChangeConstant.ACTIVITY_DISCOUNT);
		assetLog.setCurrencyType(userAccount.getCurrencyType());
		assetLog.setCurrencyName(userAccount.getCurrencyName());
		assetLog.setAsset(amount);
		assetLog.setPoundage(BigDecimal.ZERO);
		assetLog.setActualAsset(amount);
		assetLog.setRemark(remark);
		assetLog.setAfterAsset(userAccount.getTotalAmount().add(amount));
		assetLog.setResult(AssetChangeConstant.RESULT_CONFIRM);
		assetLog.setResultResult(AssetChangeConstant.RESULT_RESULT_CONFIRM);
		assetLog.setAuditResult(AssetChangeConstant.AUDIT_RESULT_SUCCESS);
		assetLog.setStatus(AssetChangeConstant.STATUS_SUCCESS);
		return assetLog;
	}

	private List<AssetChangeLog> buildDiscountPoundageChangeLog(long buyerUserId, String buyerUserName,
			long sellerUserId, String sellerUserName, String orderNo, int baseCurrencyType, int targetCurrencyType,
			BigDecimal baseAmount, BigDecimal targetAmount) {
		//买方标的货币增加
		AssetChangeLog buyerTarget =this.createAssetChangeLog(buyerUserId, buyerUserName, orderNo, AssetChangeConstant.INCREASE, targetCurrencyType, targetAmount,new BigDecimal("0"));
		//卖方基础货币增加
		AssetChangeLog sellerBase =this.createAssetChangeLog(sellerUserId, sellerUserName, orderNo, AssetChangeConstant.INCREASE, baseCurrencyType, baseAmount,new BigDecimal("0"));
		List<AssetChangeLog> list=new ArrayList<>();
		list.add(buyerTarget);
		list.add(sellerBase);
		return list;
	}

	private void handleDiscountPoundageAssetChange(List<UserAccount> accountList,
			List<AssetChangeLog> assetChangeLogList) {
		this.increase(accountList.get(0), assetChangeLogList.get(0));
		this.increase(accountList.get(1), assetChangeLogList.get(1));
	}




}
