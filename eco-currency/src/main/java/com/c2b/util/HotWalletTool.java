package com.c2b.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;
import org.web3j.utils.Numeric;

import com.c2b.client.Web3JClient;
import com.c2b.constant.Constant;
import com.c2b.util.EtherWalletUtil.EtherWallet;
import com.c2b.wallet.entity.SystemGeneralCode;
import com.c2b.wallet.mapper.SystemCodeGroupMapper;
import com.c2b.wallet.service.SysGencodeService;
import com.c2b.wallet.service.SystemGeneralCodeService;
@Service
public class HotWalletTool {
	
	/**
	 * 做三件事：
	 * 	1、创建热钱包接口，返回一个地址，并保存公钥私钥，钱包名称和密码等信息；
	 * 	2、根据钱包地址获取余额接口，返回余额。
	 * 	3、获取热钱包信息接口
	 */
	long userIdHotwallet = -1;
	
	@Autowired
	private SystemGeneralCodeService systemGeneralCodeService;
	
	@Autowired
    private SysGencodeService sysGencodeService;
	
	@Autowired
    private SystemCodeGroupMapper systemCodeGroupMapper;
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private static Web3j web3j = Web3JClient.getClient();
	
	/*public String createEtherHotWallet(String passWord) throws Exception{
		EtherWallet etherWallet = EtherWalletUtil.createWallet();
		File destinationDirectory = new File("C:\\geth\\chain\\keystore");
		String directory = WalletUtils.generateFullNewWalletFile(
				passWord, destinationDirectory );
		String source = destinationDirectory.getAbsolutePath()+"\\"+directory;
		Credentials credentials = WalletUtils.loadCredentials(passWord, source);
    	SystemCodeGroup systemCodeGroup = new SystemCodeGroup();
    	systemCodeGroup.setGroupCode(Constant.HOT_WALLET_CODE_GROUP);
    	systemCodeGroup.setGroupName("热钱包");
    	systemCodeGroup.setDescription("热钱包数据");
    	systemCodeGroup.setReadonly("N");
    	String groupID = systemCodeGroupMapper.insertSystemCodeGroup(systemCodeGroup);
    	String groupID = "7a670e3f8a2e11e794060017fa00cc08";
    	
    	SystemGeneralCode hotWalletAddress = new SystemGeneralCode();
    	hotWalletAddress.setCodeName("hotWaletAddress");
    	hotWalletAddress.setCodeValue(etherWallet.getAddress());
    	hotWalletAddress.setDescription("热钱包地址");
    	hotWalletAddress.setReadonly("N");
    	hotWalletAddress.setGroupId(groupID);
    	systemGeneralCodeService.insert(hotWalletAddress);
    	
    	SystemGeneralCode hotWalletPrivateKey = new SystemGeneralCode();
    	hotWalletPrivateKey.setCodeName("hotWaletPrivateKey");
    	hotWalletPrivateKey.setCodeValue(etherWallet.getPrivateKeys());
    	hotWalletPrivateKey.setDescription("热钱包私钥");
    	hotWalletPrivateKey.setReadonly("N");
    	hotWalletPrivateKey.setGroupId(groupID);
    	systemGeneralCodeService.insert(hotWalletPrivateKey);
    	
    	SystemGeneralCode hotWalletType = new SystemGeneralCode();
    	hotWalletType.setCodeName("hotWaletType");
    	hotWalletType.setCodeValue("ETH");
    	hotWalletType.setDescription("以太坊的热钱包");
    	hotWalletType.setReadonly("N");
    	hotWalletType.setGroupId(groupID);
    	systemGeneralCodeService.insert(hotWalletType);

    	SystemGeneralCode hotWallethotType = new SystemGeneralCode();
    	hotWallethotType.setCodeName("hotWaletPublicKey");
    	hotWallethotType.setCodeValue(etherWallet.getPublicKey());
    	hotWallethotType.setDescription("热钱包公钥");
    	hotWallethotType.setReadonly("N");
    	hotWallethotType.setGroupId(groupID);
    	systemGeneralCodeService.insert(hotWallethotType);
    	
    	SystemGeneralCode hotWalletUserId = new SystemGeneralCode();
    	hotWalletUserId.setCodeName("hotWaletUserId");
    	hotWalletUserId.setCodeValue(""+userIdHotwallet);
    	hotWalletUserId.setDescription("以太坊热钱包userID");
    	hotWalletUserId.setReadonly("N");
    	hotWalletUserId.setGroupId(groupID);
    	systemGeneralCodeService.insert(hotWalletUserId);
    	return etherWallet.getAddress();
	}*/
	
	/**
	 * 
	* @Title: getHotWalletBalance 
	* @Description: TODO(根据热钱包地址获取钱包余额) 
	* @param address 热钱包地址
	* @return BigDecimal    返回热钱包余额 
	* @throws 
	* @author Anne
	 */
	/*public static BigDecimal getHotWalletBalance(String address){
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
	}*/
	
}
