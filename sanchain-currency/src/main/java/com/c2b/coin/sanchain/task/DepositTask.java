package com.c2b.coin.sanchain.task;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sanchain.client.api.exception.APIException;
import org.sanchain.client.api.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.c2b.coin.account.api.DepositClient;
import com.c2b.coin.common.DateUtil;
import com.c2b.coin.sanchain.constant.SanChainConstant;
import com.c2b.coin.sanchain.entity.DigitalCoin;
import com.c2b.coin.sanchain.entity.RechargeLog;
import com.c2b.coin.sanchain.entity.UserCoin;
import com.c2b.coin.sanchain.mapper.DigitalCoinMapper;
import com.c2b.coin.sanchain.mapper.RechargeLogMapper;
import com.c2b.coin.sanchain.mapper.UserCoinMapper;
import com.c2b.coin.sanchain.service.ISanchainService;
import com.c2b.coin.sanchain.util.OrderGenerater;
import com.coin.config.cache.redis.RedisUtil;

/**
 * 充币监控线程
 * @author lenovo
 *
 */
@Component
public class DepositTask {

	Logger logger = Logger.getLogger(getClass());

	@Autowired
	private RedisUtil redisUtil;

	@Autowired
	private UserCoinMapper userCoinMapper;

	@Value("${sanchain.depositStartIndex}")
	private int initStartIndex;

	@Autowired
	private DepositClient depositClient;

	@Autowired
	private DigitalCoinMapper digitalCoinMapper;

	@Autowired
	private RechargeLogMapper rechargeLogMapper;

	/**
	 * 每10分钟去调度一次
	 * @throws InterruptedException
	 * @throws APIException
	 * @throws JSONException
	 * @throws URISyntaxException
	 */
	@Scheduled(fixedDelay = 600000,initialDelay=100)
	public void monitorDeposit() throws InterruptedException, APIException, JSONException, URISyntaxException {

		DigitalCoin digitalCoin=digitalCoinMapper.selectDigitalCoinByCoinName("SANC");
		//初始化leger数据
		initAddress();
		//初始化钱包地址到redis
		int ledgerIndex = initLeger();
		int ledgerCurrentIndex = Utils.ledgerClosed();
		String completeLedgers = Utils.completeLedgers();
		//检测比对区块数据和用户地址
		if( ledgerCurrentIndex > ledgerIndex ) {
			int startIndex = ledgerIndex;
			startIndex = Utils.nextLedgerIndex(completeLedgers, startIndex);
			while (startIndex <= ledgerCurrentIndex) {
				if(startIndex<0){
					logger.error("wrong start index number.");
					return;
				}
				logger.info("get ledger: " + startIndex);
				String ledger = Utils.ledger(startIndex);
				if(ledger != null){
					JSONObject ledgerJson = new JSONObject(ledger);
					if(ledgerJson.has("status") && ledgerJson.getString("status").equals("success")) {
						JSONObject ledgerResultData = ledgerJson.getJSONObject("result").getJSONObject("ledger");
						JSONArray txArray = ledgerResultData.getJSONArray("transactions");
						if (txArray != null && txArray.length() > 0) {
							Long closeTime = ledgerResultData.getLong("close_time");
							int txLength = txArray.length();
							for (int i = 0; i < txLength; i++) {
								JSONObject tx = txArray.getJSONObject(i);
								logger.info("origin txn data="+tx.toString());
								JSONObject txData = parseTxData(tx);
								if (txData != null) {
									logger.info("scan txn:"+txData.toString());
									String account = txData.getString("Account");
									String destination = txData.getString("Destination");
									if(!redisUtil.isMember(ISanchainService.REDIS_ADDRESS_KEY,destination)){
										logger.info("not in listen account list, txn:"+txData.toString());
										continue;
									}
									txData.put("closeTime", closeTime);
									String txType = tx.getString("TransactionType");
									String hxId = tx.getString("hash");
									BigDecimal amount = new BigDecimal(txData.getString("Amount"))
											.divide(new BigDecimal(1000000),6,BigDecimal.ROUND_HALF_UP);
									BigDecimal fee = new BigDecimal(txData.getString("Fee"))
											.divide(new BigDecimal(1000000),6,BigDecimal.ROUND_HALF_UP);
									try {
										saveElecoinOrder(digitalCoin,destination, hxId, amount,  String.valueOf(startIndex), fee);

									} catch (Exception e) {
										e.printStackTrace();
									}

									try {
										changeElecoinOrder(digitalCoin,destination, hxId, amount,  String.valueOf(startIndex), fee);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						} else {
							logger.info("ledger: " + startIndex + " is done." + " no tx found.");
						}
						redisUtil.set(ISanchainService.SANCHAIN_DEPOSIT_LEGERHEIGHT,startIndex+"");
						startIndex = Utils.nextLedgerIndex(completeLedgers, startIndex);
					}else{
						logger.info("ledger:" + startIndex + " not done." + " no ledger found. Retry...");
					}
				}else{
					logger.info("ledger: " + startIndex + " not done." + " timeout to get data. Retry...");
				}
				Thread.sleep(1000);
			}
		}
	}











	private void initAddress() {
		//查询redis中是否有区块数据
		Set<byte[]> address_set=redisUtil.smember(ISanchainService.REDIS_ADDRESS_KEY);
		if(address_set==null||address_set.size()==0) {
			//如果没有区块号，获取最新区块放入redis
			List<UserCoin> userCoinList = userCoinMapper.getAllUserCoin(SanChainConstant.CURRENCY_SANCHAIN);
			for (UserCoin userCoin : userCoinList) {
				redisUtil.zset(ISanchainService.REDIS_ADDRESS_KEY, userCoin.getAddress());
			}
		}
	}

	private int initLeger() {
		//先查询块高
		int startLegerHeight=0;
		String legerHeight=redisUtil.get(ISanchainService.SANCHAIN_DEPOSIT_LEGERHEIGHT);
		if(legerHeight==null|"".equals(legerHeight)) {
			startLegerHeight=initStartIndex;
		}else {
			startLegerHeight=Integer.valueOf(legerHeight);
		}
		return startLegerHeight;
	}
	private  JSONObject parseTxData(JSONObject resultData) throws URISyntaxException, InterruptedException, JSONException {
		String txType = resultData.getString("TransactionType");
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("Account", resultData.get("Account"));
		dataMap.put("Fee", resultData.get("Fee"));
		dataMap.put("hash", resultData.get("hash"));
		String txnResult = resultData.getJSONObject("metaData").getString("TransactionResult");
		dataMap.put("TransactionResult", txnResult);
		logger.info("get tx hash:" + dataMap.get("hash") + ", TransactionResult=" + dataMap.get("TransactionResult"));
		if(txnResult == null || !txnResult.equalsIgnoreCase("tesSUCCESS")){   //todo validated
			return null;
		}
		if (txType.equalsIgnoreCase("Payment")) {
			dataMap.put("Amount", resultData.get("Amount"));
			dataMap.put("Destination", resultData.get("Destination"));
			return new JSONObject(dataMap);
		}
		return null;
	}


	private boolean changeElecoinOrder(DigitalCoin digitalCoin, String destination, String hxId, BigDecimal amount,
			String valueOf, BigDecimal fee) {
		UserCoin userCoin = userCoinMapper.getUserCoinByAddress(destination);
		if (userCoin == null || userCoin.getAccount() == null
				|| "".equals(userCoin.getAccount())) {
			logger.error("MonitorDepositTxThread  toAddress="+destination+" is not exist userCoin!");
			return false;
		}
		logger.debug("getAccount=========" + userCoin.getAccount());

		// 修改充值记录
		RechargeLog rechargeLog = this.rechargeLogMapper.findRechargeLogByTxHash(hxId);
		if(null==rechargeLog) {
			logger.error("MonitorDepositTxThread  toAddress="+destination+"  txhash = "+hxId+" is not exist rechargeLog!");
			return false;
		}
		if(SanChainConstant.FINISH.equals(rechargeLog.getStatus())) {
			logger.error("MonitorDepositTxThread  toAddress="+destination+"  txhash = "+hxId+" already callback confirm!");
			return false;
		}
		rechargeLog.setStatus(SanChainConstant.FINISH);
		if(rechargeLogMapper.updateByPrimaryKey(rechargeLog)>0){
			// 回调
			try {
				if(digitalCoin == null){
					logger.error("SANC data is not exist digitalCoin!");
					return false;
				}
				String broadcastCallbackString = depositClient.confirmCallback(rechargeLog.getToAddress(),
						digitalCoin.getId(), rechargeLog.getToAccount(), hxId,
						rechargeLog.getMoney());
				logger.info("SANC充币广播回调 broadcastCallbackString="+broadcastCallbackString);
				if(broadcastCallbackString == null||StringUtils.isEmpty(broadcastCallbackString)){
					logger.error(" SANC充币广播回调返回值转JSONObject为空！");
					return false;
				}
				logger.info("SANC充币广播txhash="+hxId+"success!");
			} catch (Exception e) {
				logger.error("-------SANC充币广播回调发生异常，address=" + rechargeLog.getToAddress()
				+ ",money=" + rechargeLog.getMoney() + ",account="
				+ rechargeLog.getToAccount()+"txhash = "+hxId);
				e.printStackTrace();
			}
			return true;
		}else{
			logger.info("insert RechargeLog fail =====address:" + destination + ",addCoinsReceive money==="
					+ amount);
			return false;
		}
	}

	public boolean saveElecoinOrder(DigitalCoin digitalCoin,String toAddress, String txhash,
			BigDecimal receivedSanc, String blockNum, BigDecimal fee) {
		UserCoin userCoin = userCoinMapper.getUserCoinByAddress(toAddress);
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
		rechargeLog.setFromAddress(null);
		rechargeLog.setCurrency(SanChainConstant.CURRENCY_SANCHAIN);
		rechargeLog.setMoney(receivedSanc);
		rechargeLog.setFree(fee);
		rechargeLog.setStatus(SanChainConstant.SEND);
		rechargeLog.setCreateTime(DateUtil.getCurrentDate());
		rechargeLog.setTxHash(txhash);
		if(rechargeLogMapper.findRechargeLogByTxHash(txhash)!=null){
			logger.error("此hash值"+txhash+"在充值记录表已存在");
			return false;
		}
		if(rechargeLogMapper.insertSelective(rechargeLog)>0){
			logger.info("insert RechargeLog success =====address:" + toAddress + ",addCoinsReceive money==="
					+ receivedSanc);
			// 回调
			try {
				if(digitalCoin == null){
					logger.error("SANC data is not exist digitalCoin!");
					return false;
				}
				String broadcastCallbackString = depositClient.broadcastCallback(rechargeLog.getToAddress(),
						digitalCoin.getId(), rechargeLog.getToAccount(), txhash,
						rechargeLog.getMoney());
				logger.info("SANC充币广播回调 broadcastCallbackString="+broadcastCallbackString);
				if(broadcastCallbackString == null||StringUtils.isEmpty(broadcastCallbackString)){
					logger.error(" SANC充币广播回调返回值转JSONObject为空！");
					return false;
				}
				logger.info("SANC充币广播txhash="+txhash+"success!");
			} catch (Exception e) {
				logger.error("-------SANC充币广播回调发生异常，address=" + rechargeLog.getToAddress()
				+ ",money=" + rechargeLog.getMoney() + ",account="
				+ rechargeLog.getToAccount()+"txhash = "+txhash);
				e.printStackTrace();
			}
			return true;
		}else{
			logger.info("insert RechargeLog fail =====address:" + toAddress + ",addCoinsReceive money==="
					+ receivedSanc);
			return false;
		}
	}


}
