package com.c2b.coin.sanchain.task;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.sanchain.client.api.exception.APIException;
import org.sanchain.client.api.util.Utils;
import org.sanchain.core.Amount;
import org.sanchain.core.types.known.tx.signed.SignedTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.c2b.coin.sanchain.constant.SanChainConstant;
import com.c2b.coin.sanchain.entity.UserCoin;
import com.c2b.coin.sanchain.mapper.UserCoinMapper;
import com.c2b.coin.sanchain.util.AES;

/**
 * 归集任务类
 * @author lenovo
 *
 */
@Component
public class CollectTask {

	Logger logger = Logger.getLogger(getClass());

	@Autowired
	private UserCoinMapper userCoinMapper;

	@Value("${sanchain.hotwallet.address}")
	private String hotwalletAddress;

	@Value("${sanchain.hotwallet.privateKey}")
	private String hotwalletPrivateKey;

	@Value("${sanchain.collectLimit}")
	private BigDecimal collectLimit;
	/**
	 * 归集逻辑
	 * @throws APIException 
	 */
	@Scheduled(fixedDelay = 1800000,initialDelay=100)
	public void collectSanc() {
		//1.查出所有账户地址和私钥
		List<UserCoin> userCoinList=userCoinMapper.getAllUserCoin(SanChainConstant.CURRENCY_SANCHAIN);
		for (UserCoin userCoin : userCoinList) {
			int i = 3;
			String address = userCoin.getAddress();
			try {
				//				if(userCoin.getCurrency().compareTo(anotherString))
				//				获取用户钱包余额
				BigDecimal balance = getBalance(address);
				BigDecimal fee = balance.divide(new BigDecimal("1000"));
				balance = balance.subtract(fee).stripTrailingZeros().setScale(0, BigDecimal.ROUND_DOWN);
				if(balance.divide(new BigDecimal(1000000)).compareTo(collectLimit)>0) {
					payment(userCoin, balance,i);
				}
			} catch (APIException e) {
				logger.error("地址:"+address+ "   内无金额");
			} catch (Exception e) {
				logger.error("地址:"+address + "  发生异常"+e.getMessage());
			}
			i=i+1;
		}
	}

	private void payment(UserCoin userCoin,BigDecimal balance, int i) throws APIException {
		SignedTransaction sign = null;
		Integer lastLedgerIndex = Utils.ledgerClosed() +3;
		//XXXX  这里 第三个参数  假设题币  为7.12345678个   那么  json串中  Amount参数应为  712345678  
		// 但是 Amout后面多了三个0 我去掉空0了  也不管用  需要深入它jar包中看看是怎么回事，或者咨询陶毅
		
//		{
//	    "result":{
//	        "tx_json":{
//	            "Account":"sJAAc5u9VEdhBRaMoGbLFvfuK16UEoQzN3",
//	            "Destination":"sJKWFFV7vZKCpghe8EXn3QGndKTeWGjNF1",
//	            "TransactionType":"Payment",
//	            "TxnSignature":"304502210083FD4792A061B96BD46E745ABB68373A16F35FFF2688F04EB4E3D6E4DAE100BA02205EF4B4E0F1B22A5631ED8297CBE8BEE617FDCBCCAE8A895CE984C8E7C1D10B5D",
//	            "SigningPubKey":"023EB41CF117ABCC905FEAEE9B9180B2E978099318540C456BF47592A6E20FA178",
//	            "Amount":"7009784530000",
//	            "Fee":"7009784530",
//	            "Flags":2147483648,
//	            "Sequence":7,
//	            "LastLedgerSequence":2015636,
//	            "hash":"B62A895FF30736F3DC4D1FECAB21AA78D59C78BBB375A5FC6C84FED42307DB54"
//	        },
//	        "engine_result_code":-187,
//	        "tx_blob":"12000022800000002400000007201B001EC194614000066017B784506840000001A1D0D2D27321023EB41CF117ABCC905FEAEE9B9180B2E978099318540C456BF47592A6E20FA1787447304502210083FD4792A061B96BD46E745ABB68373A16F35FFF2688F04EB4E3D6E4DAE100BA02205EF4B4E0F1B22A5631ED8297CBE8BEE617FDCBCCAE8A895CE984C8E7C1D10B5D8114C4DE30FA6AB6AD9FB39F6AE3A783A3AE0E4A7F9C8314BDFA85978D7EDB6D95AC650CAB5A91DCE0AF0550",
//	        "engine_result":"tefMAX_LEDGER",
//	        "engine_result_message":"Ledger sequence too high."
//	    },
//	    "id":0,
//	    "type":"response",
//	    "status":"success"
//	}
		sign = Utils.payment(
				AES.decrypt(userCoin.getPrivateKey()),
				hotwalletAddress,
				Amount.fromString(balance.toString()),
				Utils.getSequence(userCoin.getAddress()),
				true,
				null, null, null, lastLedgerIndex
				);  //just sign local

		System.out.println(sign.hash.toHex());
		String res = Utils.sendTx(sign.tx_blob);  //broadcast
		System.out.println("res="+res);  
	}


	private  BigDecimal getBalance(String address) throws APIException {
		BigDecimal san=BigDecimal.ZERO;
		JSONObject jsonObject;

		String accountInfo = Utils.getAccountInfo(address);

		if (StringUtils.isEmpty(accountInfo)) {
			return san;
		}

		jsonObject = JSONObject.parseObject(accountInfo);
		if (!jsonObject.containsKey("result") || !jsonObject.getJSONObject("result").containsKey("account_data")) {
			return san;
		}
		san = new BigDecimal(jsonObject.getJSONObject("result").getJSONObject("account_data").getString("Balance"));
		return san;
	}
}
