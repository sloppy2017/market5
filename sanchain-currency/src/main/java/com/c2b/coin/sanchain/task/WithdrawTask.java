package com.c2b.coin.sanchain.task;

import java.net.URISyntaxException;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.sanchain.client.api.exception.APIException;
import org.sanchain.client.api.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.c2b.coin.account.api.WithDrawClient;
import com.c2b.coin.sanchain.constant.SanChainConstant;
import com.c2b.coin.sanchain.entity.DigitalCoin;
import com.c2b.coin.sanchain.entity.UserCoin;
import com.c2b.coin.sanchain.entity.WithdrawLog;
import com.c2b.coin.sanchain.mapper.DigitalCoinMapper;
import com.c2b.coin.sanchain.mapper.UserCoinMapper;
import com.c2b.coin.sanchain.mapper.WithdrawLogMapper;

/**
 * 提币监控线程
 * @author lenovo
 *
 */
@Component
public class WithdrawTask {

	Logger logger = Logger.getLogger(getClass());

	@Autowired
	private WithdrawLogMapper withdrawLogMapper;

	@Autowired
	private WithDrawClient withdrawClient;
	
	@Autowired
	private DigitalCoinMapper digitalCoinMapper;
	
	@Autowired
	private UserCoinMapper userCoinMapper;
	
	@Scheduled(fixedDelay = 600000,initialDelay=100)
	public void monitorTask() throws InterruptedException, APIException, JSONException, URISyntaxException {
		DigitalCoin digitalCoin = digitalCoinMapper.selectDigitalCoinByCoinName(SanChainConstant.CURRENCY_SANCHAIN);
		List<WithdrawLog> withdrawList = this.withdrawLogMapper.listSANCSend();
		for (WithdrawLog withdrawLog : withdrawList) {
			String hxId = withdrawLog.getTxHash();
			UserCoin userCoin = userCoinMapper.getUserCoinByAccountAndCurrency(
					withdrawLog.getAccount(), SanChainConstant.CURRENCY_SANCHAIN);
			boolean success = Utils.checkTxnSuccess(hxId);
			if(success) {
				withdrawLog.setStatus(SanChainConstant.FINISH);
				this.withdrawLogMapper.updateByPrimaryKey(withdrawLog);
				//通知提币确认接口
				withdrawClient.confirmCallback(userCoin.getAddress(), digitalCoin.getId(), withdrawLog.getAccount(), hxId, withdrawLog.getMoney(), withdrawLog.getOrderNo());
			}
		}
		
	}


	
}
