package com.c2b.coin.sanchain.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.sanchain.client.api.exception.APIException;
import org.sanchain.client.api.util.Utils;
import org.sanchain.core.Amount;
import org.sanchain.core.Wallet;
import org.sanchain.core.types.known.tx.signed.SignedTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.c2b.coin.common.DateUtil;
import com.c2b.coin.common.MessageEnum;
import com.c2b.coin.common.enumeration.ErrorMsgEnum;
import com.c2b.coin.sanchain.constant.SanChainConstant;
import com.c2b.coin.sanchain.entity.UserCoin;
import com.c2b.coin.sanchain.entity.WithdrawLog;
import com.c2b.coin.sanchain.exception.SanchainException;
import com.c2b.coin.sanchain.mapper.UserCoinMapper;
import com.c2b.coin.sanchain.mapper.WithdrawLogMapper;
import com.c2b.coin.sanchain.service.ISanchainService;
import com.c2b.coin.sanchain.task.CollectTask;
import com.c2b.coin.sanchain.util.AES;
import com.c2b.coin.web.common.RedisUtil;

@Service
public class SanchainService implements ISanchainService{

	@Autowired
	private UserCoinMapper userCoinMapper;

	@Value("${sanchain.hotwallet.address}")
	private String hotwalletAddress;

	@Value("${sanchain.hotwallet.privateKey}")
	private String hotwalletPrivateKey;

	@Autowired
	private RedisUtil redisUtil;

	@Autowired
	private WithdrawLogMapper withdrawLogMapper;
	
	@Autowired
	private CollectTask collectTask;
	
	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;
	
	@Override
	public String createAddress(String account) throws IOException{
		//1.生成钱包地址
		Wallet wallet = new Wallet();
		String address=wallet.account().address;
		String seed=wallet.seed();
		UserCoin userCoin = new UserCoin();
		userCoin.setAccount(account);
		userCoin.setAddress(address );
		userCoin.setCurrency(SanChainConstant.CURRENCY_SANCHAIN);
		userCoin.setPrivateKey(AES.encrypt(seed.getBytes("UTF-8")));
		userCoin.setCreateTime(new Date());
		userCoinMapper.insertSelective(userCoin);
		//2.放入redis一份
		redisUtil.zset(REDIS_ADDRESS_KEY, address);
		return address;
	}

	@Override
	public String sendMoney(WithdrawLog withdrawLog) throws APIException {
		
		//计算矿工费用
		BigDecimal amount = withdrawLog.getMoney().stripTrailingZeros().multiply(new BigDecimal("1000000")).setScale(0,BigDecimal.ROUND_DOWN) ;
		
		BigDecimal fee = withdrawLog.getMoney().divide(new BigDecimal(1000));
		
		BigDecimal hotWalletBalance =getBalance(hotwalletAddress);
		if(hotWalletBalance.compareTo(withdrawLog.getMoney())<0) {
			taskExecutor.execute(() -> {
				collectTask.collectSanc();
			});
			throw new SanchainException(ErrorMsgEnum.SANC_HOTWALLETBALANCE_NOT_ENOUGH, "热钱包金额不足");
		}
		
		String amountStr = amount.stripTrailingZeros().toString();
		SignedTransaction sign = null;
		Integer lastLedgerIndex = Utils.ledgerClosed() + 3;
		//XXXX  这里 第三个参数  假设题币  为7.12345678个   那么  json串中  Amount参数应为  712345678  
		// 但是 Amout后面多了三个0 我去掉空0了  也不管用  需要深入它jar包中看看是怎么回事，或者咨询陶毅
		
//		{
//		    "result":{
//		        "tx_json":{
//		            "Account":"sJAAc5u9VEdhBRaMoGbLFvfuK16UEoQzN3",
//		            "Destination":"sJKWFFV7vZKCpghe8EXn3QGndKTeWGjNF1",
//		            "TransactionType":"Payment",
//		            "TxnSignature":"304502210083FD4792A061B96BD46E745ABB68373A16F35FFF2688F04EB4E3D6E4DAE100BA02205EF4B4E0F1B22A5631ED8297CBE8BEE617FDCBCCAE8A895CE984C8E7C1D10B5D",
//		            "SigningPubKey":"023EB41CF117ABCC905FEAEE9B9180B2E978099318540C456BF47592A6E20FA178",
//		            "Amount":"7009784530000",
//		            "Fee":"7009784530",
//		            "Flags":2147483648,
//		            "Sequence":7,
//		            "LastLedgerSequence":2015636,
//		            "hash":"B62A895FF30736F3DC4D1FECAB21AA78D59C78BBB375A5FC6C84FED42307DB54"
//		        },
//		        "engine_result_code":-187,
//		        "tx_blob":"12000022800000002400000007201B001EC194614000066017B784506840000001A1D0D2D27321023EB41CF117ABCC905FEAEE9B9180B2E978099318540C456BF47592A6E20FA1787447304502210083FD4792A061B96BD46E745ABB68373A16F35FFF2688F04EB4E3D6E4DAE100BA02205EF4B4E0F1B22A5631ED8297CBE8BEE617FDCBCCAE8A895CE984C8E7C1D10B5D8114C4DE30FA6AB6AD9FB39F6AE3A783A3AE0E4A7F9C8314BDFA85978D7EDB6D95AC650CAB5A91DCE0AF0550",
//		        "engine_result":"tefMAX_LEDGER",
//		        "engine_result_message":"Ledger sequence too high."
//		    },
//		    "id":0,
//		    "type":"response",
//		    "status":"success"
//		}
		
		sign = Utils.payment(
				AES.decrypt(hotwalletPrivateKey),
				withdrawLog.getToAddress(),
				Amount.fromString(amount.stripTrailingZeros().toString()),
				Utils.getSequence(hotwalletAddress),
				true,
				null, null, null, lastLedgerIndex
				);  //just sign local

		System.out.println(sign.hash.toHex());
		String res = Utils.sendTx(sign.tx_blob);  //broadcast
		System.out.println("res="+res);  //broadcast return
		//插入提币记录表
		WithdrawLog twithdrawLog = new WithdrawLog();
		twithdrawLog.setOrderNo(withdrawLog.getOrderNo());
		twithdrawLog.setAccount(withdrawLog.getAccount());
		twithdrawLog.setToAddress(withdrawLog.getToAddress());
		twithdrawLog.setMoney(withdrawLog.getMoney());
		twithdrawLog.setFree(fee);
		twithdrawLog.setCurrency(SanChainConstant.CURRENCY_SANCHAIN);
		twithdrawLog.setCreateTime(DateUtil.getCurrentDate());
		twithdrawLog.setTxHash(sign.hash.toHex());
		twithdrawLog.setStatus(SanChainConstant.SEND);
		withdrawLogMapper.insertSelective(twithdrawLog);
		return sign.tx_blob;


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
        return san.divide(new BigDecimal(1000000));
	}

}
