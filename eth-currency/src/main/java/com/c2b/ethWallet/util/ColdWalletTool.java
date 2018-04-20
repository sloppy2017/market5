/*package com.c2b.ethWallet.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;
import org.web3j.utils.Numeric;

import com.c2b.ethWallet.client.Web3JClient;
*//**
 * 
* @ClassName: ColdWalletTool 
* @Description: TODO(以太坊冷钱包工具类) 
* @author dxm 
* @date 2017年8月22日 下午6:26:30 
*
 *//*
@Component
public class ColdWalletTool {
	
	*//**
	 * 做三件事：
	 * 	1、创建冷钱包接口，返回一个地址，并保存公钥私钥，钱包名称和密码等信息
	 * 	2、根据钱包地址获取余额接口，返回余额
	 * 	3、获取冷钱包信息接口
	 *//*
	long userIdHotwallet = -1;
	
	@Autowired
	private SystemGeneralCodeService systemGeneralCodeService;
	
	@Autowired
	private SystemCodeGroupService systemCodeGroupService;
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private static Web3j web3j = Web3JClient.getClient();
	
	*//**
	 * 
	* @Title: createEtherColdWallet 
	* @Description: TODO(创建以太坊冷钱包并返回钱包地址) 
	* @return String    返回钱包地址 
	* @throws Exception    异常
	* @author Anne
	 *//*
	public String createEtherColdWallet() throws Exception{
		EtherWallet wallet = EtherWalletUtil.createWallet();
    	
    	if(wallet==null)
    		throw new Exception(
    				"create ether account failed,please retry later!");
    	SystemCodeGroup systemCodeGroup = new SystemCodeGroup();
    	systemCodeGroup.setGroupCode(Constant.COLD_WALLET_CODE_GROUP);
    	systemCodeGroup.setGroupName("冷钱包");
    	systemCodeGroup.setDescription("冷钱包数据");
    	systemCodeGroup.setReadonly("N");
    	//String groupID = "0d3d9c9ead8e4dbdbfcdfc56cba10027";
    	
    	SystemGeneralCode coldWalletAddress = new SystemGeneralCode();
    	coldWalletAddress.setCodeName("coldWaletAddress");
    	coldWalletAddress.setCodeValue(wallet.getAddress());
    	coldWalletAddress.setDescription("冷钱包地址");
    	coldWalletAddress.setReadonly("N");
    	coldWalletAddress.setGroupId(groupID);
    	systemGeneralCodeService.insert(coldWalletAddress);
    	
    	SystemGeneralCode coldWalletPrivateKey = new SystemGeneralCode();
    	coldWalletPrivateKey.setCodeName("coldWaletPrivateKey");
    	coldWalletPrivateKey.setCodeValue(wallet.getPrivateKeys());
    	coldWalletPrivateKey.setDescription("冷钱包私钥");
    	coldWalletPrivateKey.setReadonly("N");
    	coldWalletPrivateKey.setGroupId(groupID);
    	systemGeneralCodeService.insert(coldWalletPrivateKey);
    	
    	SystemGeneralCode coldWalletPublicKey = new SystemGeneralCode();
    	coldWalletPrivateKey.setCodeName("coldWalletPublicKey");
    	coldWalletPrivateKey.setCodeValue(wallet.getPublicKey());
    	coldWalletPrivateKey.setDescription("冷钱包公钥");
    	coldWalletPrivateKey.setReadonly("N");
    	coldWalletPrivateKey.setGroupId(groupID);
    	systemGeneralCodeService.insert(coldWalletPublicKey);
    	
    	SystemGeneralCode coldWalletType = new SystemGeneralCode();
    	coldWalletType.setCodeName("coldWaletType");
    	coldWalletType.setCodeValue("ETH");
    	coldWalletType.setDescription("以太坊的冷钱包");
    	coldWalletType.setReadonly("N");
    	coldWalletType.setGroupId(groupID);
    	systemGeneralCodeService.insert(coldWalletType);

    	SystemGeneralCode coldWalletColdType = new SystemGeneralCode();
    	coldWalletColdType.setCodeName("coldHotWaletType");
    	coldWalletColdType.setCodeValue("cold");
    	coldWalletColdType.setDescription("冷钱包");
    	coldWalletColdType.setReadonly("N");
    	coldWalletColdType.setGroupId(groupID);
    	systemGeneralCodeService.insert(coldWalletColdType);
    	
    	SystemGeneralCode coldWalletInitVector = new SystemGeneralCode();
    	coldWalletInitVector.setCodeName("coldWaletInitVector");
    	coldWalletInitVector.setCodeValue(wallet.getIv());
    	coldWalletInitVector.setDescription("以太坊冷钱包IV");
    	coldWalletInitVector.setReadonly("N");
    	coldWalletInitVector.setGroupId(groupID);
    	systemGeneralCodeService.insert(coldWalletInitVector);
    	
    	SystemGeneralCode coldWalletSalt = new SystemGeneralCode();
    	coldWalletSalt.setCodeName("coldWaletSalt");
    	coldWalletSalt.setCodeValue(wallet.getSalt());
    	coldWalletSalt.setDescription("以太坊冷钱包Salt");
    	coldWalletSalt.setReadonly("N");
    	coldWalletSalt.setGroupId(groupID);
    	systemGeneralCodeService.insert(coldWalletSalt);
    	
    	SystemGeneralCode coldWalletCipherText = new SystemGeneralCode();
    	coldWalletCipherText.setCodeName("coldWaletSeed");
    	coldWalletCipherText.setCodeValue(wallet.getCipherText());
    	coldWalletCipherText.setDescription("以太坊冷钱包Seed");
    	coldWalletCipherText.setReadonly("N");
    	coldWalletCipherText.setGroupId(groupID);
    	systemGeneralCodeService.insert(coldWalletCipherText);

    	SystemGeneralCode coldWalletStatus = new SystemGeneralCode();
    	coldWalletStatus.setCodeName("coldWaletStatus");
    	coldWalletStatus.setCodeValue(Constant.STATUS_VALID_VALUE);
    	coldWalletStatus.setDescription("以太坊冷钱包Status");
    	coldWalletStatus.setReadonly("N");
    	coldWalletStatus.setGroupId(groupID);
    	systemGeneralCodeService.insert(coldWalletStatus);
    	
    	SystemGeneralCode coldWalletCreate = new SystemGeneralCode();
    	coldWalletCreate.setCodeName("coldWaletCreateTime");
    	coldWalletCreate.setCodeValue(""+System.currentTimeMillis());
    	coldWalletCreate.setDescription("以太坊冷钱包创建时间");
    	coldWalletCreate.setReadonly("N");
    	coldWalletCreate.setGroupId(groupID);
    	systemGeneralCodeService.insert(coldWalletCreate);
    	
    	SystemGeneralCode coldWalletUpdate = new SystemGeneralCode();
    	coldWalletUpdate.setCodeName("coldWaletUpdateTime");
    	coldWalletUpdate.setCodeValue(""+System.currentTimeMillis());
    	coldWalletUpdate.setDescription("以太坊冷钱包修改时间");
    	coldWalletUpdate.setReadonly("N");
    	coldWalletUpdate.setGroupId(groupID);
    	systemGeneralCodeService.insert(coldWalletUpdate);
    	
    	SystemGeneralCode coldWalletUserId = new SystemGeneralCode();
    	coldWalletUserId.setCodeName("coldWaletUserId");
    	coldWalletUserId.setCodeValue(""+userIdHotwallet);
    	coldWalletUserId.setDescription("以太坊冷钱包userID");
    	coldWalletUserId.setReadonly("N");
    	coldWalletUserId.setGroupId(groupID);
    	systemGeneralCodeService.insert(coldWalletUserId);
    	return wallet.getAddress();
	}
	
	*//**
	 * 
	* @Title: getColdWalletBalance 
	* @Description: TODO(根据地址获取冷钱包余额) 
	* @param address 冷钱包地址
	* @return BigDecimal    返回余额 
	* @throws 
	* @author Anne
	 *//*
	public BigDecimal getColdWalletBalance(String address){
		try {
			if(!Numeric.containsHexPrefix(address))
				address = Numeric.prependHexPrefix(address);
			BigInteger balance = web3j.ethGetBalance(address, 
					DefaultBlockParameterName.LATEST).send().getBalance();
			return Convert.fromWei(balance.toString(), Unit.ETHER);
		} catch (IOException e) {
			e.printStackTrace();
			return BigDecimal.ZERO;
		}
	}
	
	*//**
	 * 
	* @Title: getColdWallet 
	* @Description: TODO(获取冷钱包对象信息)
	* @return EtherWallet    返回冷钱包 
	* @throws 
	* @author Anne
	 *//*
	public EtherWallet getColdWallet(){
		EtherWallet coldWallet = new EtherWallet();
		List<Map<String, String>> list = systemGeneralCodeService.findByGroupCode(
				Constant.COLD_WALLET_CODE_GROUP);
		if(list == null || list.size() == 0){
			logger.error("cold wallet uninitialized!");
			return null;
		}
		for(Map<String, String> map : list){
			if(map.get("coldWaletAddress")!=null){
				String address = map.get("coldWaletAddress");
				String seed = map.get("coldWaletSeed");
				String iv = map.get("coldWaletInitVector");
				String salt = map.get("coldWaletSalt");
				logger.info("address:"+address+"\tseed:"+seed+"\tpwd:"+salt+"\tiv:"+iv);
				coldWallet.setAddress(address);
				coldWallet.setCipherText(seed);
				coldWallet.setIv(iv);
				coldWallet.setSalt(salt);
			}
		}
		return coldWallet;
	}
	
	*//**
	 * 
	* @Title: getAccountSeed 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @param address    设定文件 
	* @return void    返回类型 
	* @throws 
	* @author Anne
	 *//*
	private void getAccountSeed(String address){
		try {
			EtherWallet wallet = getColdWallet();
			if(wallet != null){
				String seed = EtherWalletUtil.decryptPrivateKey(wallet.getSalt(), wallet.getIv(), 
						wallet.getCipherText());
				System.out.println("address:"+wallet.getAddress()+"\tseed:"+seed+"\tpwd:"
						+wallet.getSalt());
			}else{
				System.out.println("Don't found coldwallet info for address:"+address);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		ColdWalletTool coldWalletTool = new ColdWalletTool();
		coldWalletTool.createEtherColdWallet();
		coldWalletTool.getAccountSeed("34b70562cdb9cbd0af5b9e6b4ccffe5a74b9a1a6");
	}
}
*/