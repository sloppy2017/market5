package com.c2b.ethWallet.task;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;
import org.web3j.utils.Numeric;

import com.c2b.ethWallet.client.Web3JClient;
import com.c2b.ethWallet.service.SysGencodeService;
import com.c2b.ethWallet.util.Constant;
import com.c2b.ethWallet.util.EtherWalletUtil.EtherWallet;

/**
 * 
* @ClassName: TransferToColdWalletTask 
* @Description: TODO(将超过一定阈值的热钱包的金额转入冷钱包) 
* @author dxm 
* @date 2017年8月22日 下午3:56:03 
*
 */
@Component
public class TransferToColdWalletTask {

	Logger log = LoggerFactory.getLogger(getClass());
	
	private static Web3j web3j = Web3JClient.getClient();
	
	/**
	 * 设置热钱包转冷钱包的阈值500L
	 */
	BigDecimal thresholdVal = new BigDecimal(500L);
	
	@Autowired
    private SysGencodeService sysGencodeService;
	
	/**
	 * 
	* @Title: getBalance 
	* @Description: TODO(根据热钱包地址获取热钱包余额) 
	* @param @param address
	* @param @return    设定文件 
	* @return BigDecimal    返回类型 
	* @throws 
	* @author Anne
	 */
	private BigDecimal getBalance(String address){
		try {
			if(!Numeric.containsHexPrefix(address))
				address = Numeric.prependHexPrefix(address);
			BigInteger balance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send().getBalance();
			return Convert.fromWei(balance.toString(), Unit.ETHER);
		} catch (IOException e) {
			e.printStackTrace();
			return BigDecimal.ZERO;
		}
	}
	
	/**
	 * 
	* @Title: sendTx 
	* @Description: TODO(超出热钱包阈值的余额转入冷钱包) 
	* @param @param systemGeneralCode 热钱包对象
	* @param @param to 冷钱包地址
	* @param @param amount    转账金额 
	* @return void    返回类型 
	* @throws 
	* @author Anne
	 */
	private void sendTx(EtherWallet hotwallet, String to, BigDecimal amount){
		String fromAddress = null;
		String privateKey = "";
		String publicKey = "";
		try {
			if(hotwallet!=null){
				fromAddress = hotwallet.getAddress();
				privateKey = hotwallet.getPrivateKeys();
				publicKey = hotwallet.getPublicKey();
				ECKeyPair ecKeyPair = new ECKeyPair(new BigInteger(privateKey), new BigInteger(publicKey));
				Credentials sendCredentials = Credentials.create(ecKeyPair);
				TransactionReceipt transactionReceipt = Transfer.sendFunds(web3j, sendCredentials,
						to, amount, Convert.Unit.ETHER);
				String txHashVal = transactionReceipt.getTransactionHash();
				if(txHashVal!=null){
					log.info("from:"+fromAddress+"\tto:"+to+"\tvalue:"+amount+"! sendTx success,tx:"+txHashVal);
				}else{
					log.error("from:"+fromAddress+"\tto:"+to+"\tvalue:"+amount+"! sendTx failed");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("from:"+fromAddress+"\tto:"+to+"\tvalue:"+amount+"! sendTx failed");
		}
	}
	
//	@Scheduled(cron = "0 0 0 * * ?")
	public void transferToColdWallet(){
		log.info("begin transfer eth hotwallet balance to coldwallet");
		try {
			EtherWallet hotwallet = this.getHotwallet();
			EtherWallet coldWallet = this.getColdWallet();
			BigDecimal deltaVal = new BigDecimal(100L);
			if(hotwallet!=null && coldWallet!=null){
				BigDecimal balance = getBalance(hotwallet.getAddress());
				String to = coldWallet.getAddress();
				boolean ret = balance.compareTo(thresholdVal)==1 && 
						balance.subtract(thresholdVal).compareTo(deltaVal) == 1;
				log.info("check hotwallet balance:"+balance+" compare result:"+ret);
				if(ret){
					BigDecimal valEther = balance.subtract(thresholdVal);
					this.sendTx(hotwallet, to, valEther);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.toString());
		}
		log.info("end transfer eth hotwallet balance to coldwallet");
	}
	
	/**
	 * 
	* @Title: getHotwallet 
	* @Description: TODO(获取热钱包信息)
	* @return EtherWallet    返回热钱包 
	* @throws 
	* @author Anne
	 */
	public EtherWallet getHotwallet(){
		EtherWallet hotWallet = new EtherWallet();
		List<Map<String, String>> list = sysGencodeService.findByGroupCode(
				Constant.HOT_WALLET_CODE_GROUP);
		if(list == null || list.size() == 0){
			log.error("hot wallet uninitialized!");
			return null;
		}
		Map<String, String> paramMap = new HashMap<String, String>();
		for(Map<String, String> map : list){
			paramMap.put(map.get("code_name"), map.get("code_value"));
		}
		if(paramMap.size()>0){
			String address = paramMap.get("hotWaletAddress");
			String privateKey = paramMap.get("hotWaletPrivateKey");
			String publicKey = paramMap.get("hotWaletPublicKey");
			hotWallet.setAddress(address);
			hotWallet.setPrivateKeys(privateKey);;
			hotWallet.setPublicKey(publicKey);
		}
		return hotWallet;
	}
	
	/**
	 * 
	* @Title: getColdWallet 
	* @Description: TODO(获取冷钱包对象信息)
	* @return EtherWallet    返回冷钱包 
	* @throws 
	* @author Anne
	 */
	public EtherWallet getColdWallet(){
		EtherWallet coldWallet = new EtherWallet();
		List<Map<String, String>> list = sysGencodeService.findByGroupCode(
				Constant.COLD_WALLET_CODE_GROUP);
		if(list == null || list.size() == 0){
			log.error("cold wallet uninitialized!");
			return null;
		}
		for(Map<String, String> map : list){
			if(map.get("coldWaletAddress")!=null){
				String address = map.get("coldWaletAddress");
				String seed = map.get("coldWaletSeed");
				String iv = map.get("coldWaletInitVector");
				String salt = map.get("coldWaletSalt");
				log.info("address:"+address+"\tseed:"+seed+"\tpwd:"+salt+"\tiv:"+iv);
				coldWallet.setAddress(address);
				coldWallet.setCipherText(seed);
				coldWallet.setIv(iv);
				coldWallet.setSalt(salt);
			}
		}
		return coldWallet;
	}
}
