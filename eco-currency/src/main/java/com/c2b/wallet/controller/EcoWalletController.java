package com.c2b.wallet.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.wallet.Wallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.c2b.EcoWalletApplication;
import com.c2b.config.CoreMessageSource;
import com.c2b.constant.CodeConstant;
import com.c2b.constant.Constant;
import com.c2b.util.Validator;
import com.c2b.wallet.entity.UserCoin;
import com.c2b.wallet.entity.WithdrawLog;
import com.c2b.wallet.mapper.UserCoinMapper;
import com.c2b.wallet.service.DepositService;
import com.c2b.wallet.service.EcoWalletService;
import com.c2b.wallet.service.SysGencodeService;
import com.c2b.wallet.service.WithDrawService;

@RestController
@RequestMapping("/client/btc")
@Api(value = "比特币相关接口")
public class EcoWalletController {

	Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private EcoWalletService ecoWalletService;

    @Autowired
    private CoreMessageSource messageSource;
    
    @Autowired
    private SysGencodeService sysGencodeService;
    
    @Autowired
    private UserCoinMapper userCoinMapper;
    
    @Autowired
    private WithDrawService withDrawService;
    
    @Autowired
    private DepositService depositService;
    


    @ApiOperation(value = "校验地址是否为比特币地址", notes = "创建比特币钱包例如：http://127.0.0.1:8001/api/btc/validAddress/hkjh12h4124124214hk")
    @GetMapping("isValidAddress/{address}")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "path", name = "address", value = "地址", dataType = "String", required = true)
    })
    public AjaxResponse isValidAddress(@PathVariable("address") String address){
        try {
        	Address.fromBase58(EcoWalletApplication.params, address);
            return AjaxResponse.success(messageSource.getMessage("btcAddressSuccess"), address);
        } catch (Exception e) {
            return AjaxResponse.falied(messageSource.getMessage("btcAddressError"), address);
        }
    }

    @ApiOperation(value = "创建比特币钱包", notes = "创建比特币钱包例如：http://127.0.0.1:8001/api/btc/create/user1")
    @PostMapping("/create")
    /*@ApiImplicitParams({
        @ApiImplicitParam(paramType = "path", name = "account", value = "账号", dataType = "String", required = true)
    })*/
    public AjaxResponse createWallet(@RequestParam("account") String account) {
    	Map<String,Object> data = new HashMap<String, Object>();
    	UserCoin userCoin = userCoinMapper.getUserCoinByAccountAndCurrency(account,Constant.CURRENCY_BTC);
        if (userCoin != null ) {
        	return AjaxResponse.falied(messageSource.getMessage("existAddress", userCoin.getAddress()), CodeConstant.PARAM_ERROR);
        }
        NetworkParameters params = EcoWalletApplication.params;
        String address = ecoWalletService.createWallet(params, account);
        
        data.put("address", address);
        data.put("userName", account);
        return AjaxResponse.success("比特币钱包创建成功", data);
    }

    @ApiOperation(value = "查询主账户比特币钱包余额", notes = "查询比特币钱包例如：http://127.0.0.1:8001/balance")
    @GetMapping("/balance")
    public AjaxResponse bananceWallet() {

        try {
            Wallet wallet = ecoWalletService.dumpWallet();
            logger.info("wallet.toString()="+wallet.toString());
            return AjaxResponse.success("查询成功", wallet.getBalance().toFriendlyString());
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.falied(e.getMessage());
        }
    }
    
    
    @ApiOperation(value = "提BTC")
    @PostMapping("/sendMoney")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "withdrawLog", value = "提币参数", required = true, dataType = "WithdrawLog")
    })
	public AjaxResponse sendMoney(HttpServletRequest request,@RequestBody WithdrawLog withdrawLog){
        
    	if(withdrawLog==null||StringUtils.isBlank(withdrawLog.getAccount())||StringUtils.isBlank(withdrawLog.getToAddress())){
    		return AjaxResponse.falied(messageSource.getMessage("paramError"), CodeConstant.PARAM_ERROR);
        }
    	UserCoin userCoin = userCoinMapper.getUserCoinByAccountAndCurrency(withdrawLog.getAccount(), Constant.CURRENCY_BTC);
    	if(userCoin == null){
    		return AjaxResponse.falied(messageSource.getMessage("accountError"), CodeConstant.PARAM_ERROR);
    	}
    	if(userCoin.getAddress().equals(withdrawLog.getToAddress())){
        	return AjaxResponse.falied(messageSource.getMessage("ownAddressError"), CodeConstant.PARAM_ERROR);
        }
    	
    	UserCoin userCoin1 = userCoinMapper.getUserCoinByAddress(withdrawLog.getToAddress());
    	if(userCoin1 != null){
    		return AjaxResponse.falied("请勿给系统内地址提币", CodeConstant.PARAM_ERROR);
    	}
    	//校验地址是否有误
    	try {
        	Address.fromBase58(EcoWalletApplication.params, withdrawLog.getToAddress());
        } catch (Exception e) {
            return AjaxResponse.falied(messageSource.getMessage("btcAddressError"), withdrawLog.getToAddress());
        }
    	
     	String uplimit = "";
     	String lowlimit = "";
         List<Map<String, String>> codeList = sysGencodeService.findByGroupCode("WITHDRAW_LIMIT");
         for(Map<String, String> mapObj:codeList){
             if("BTC_LIMIT".equals(String.valueOf(mapObj.get("code_name")))){
             	uplimit = mapObj.get("uplimit").toString();
             	lowlimit = mapObj.get("lowlimit").toString();
             }
         }
         if(withdrawLog.getMoney().compareTo(new BigDecimal(lowlimit))<0){
         	return AjaxResponse.falied(messageSource.getMessage("coinLowLimit",Constant.CURRENCY_BTC,lowlimit), CodeConstant.PARAM_ERROR);
         }
         
         if(!StringUtils.isEmpty(uplimit)&&withdrawLog.getMoney().compareTo(new BigDecimal(uplimit))>0){
         	return AjaxResponse.falied(messageSource.getMessage("coinUpLimit",Constant.CURRENCY_BTC,uplimit), CodeConstant.PARAM_ERROR);
         }
         
        /* String btc_fee = "";//手续费（包含矿工费）
         List<Map<String, String>> codeList1 = sysGencodeService.findByGroupCode("WITHDRAW_SERVICE_CHARGE");
         for(Map<String, String> mapObj:codeList1){
             if("BTC_FEE".equals(String.valueOf(mapObj.get("code_name")))){
            	 btc_fee = mapObj.get("code_value").toString();
             }
         }*/
         String hash = ecoWalletService.sendMoney(EcoWalletApplication.params, withdrawLog);
         if(hash == null){
        	 return AjaxResponse.falied(messageSource.getMessage("coinFailure"), CodeConstant.UPDATE_FAIL);
         }
         return AjaxResponse.success(messageSource.getMessage("coinSuccess"), hash);
    	
        /*Coin value = null;
        try {
            value = Coin.parseCoin(withdrawLog.getMoney().toString());
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.falied(e.getMessage());
        }
        
        try {
        	value = value.subtract(Coin.parseCoin(btc_fee));
            NetworkParameters params = EcoWalletApplication.params;
            Address to = Address.fromBase58(params, withdrawLog.getToAddress());
            EcoWalletApplication.kit.wallet().sendCoins( EcoWalletApplication.kit.peerGroup(), to, value);
            
            return AjaxResponse.success("提币发送成功",EcoWalletApplication.kit.wallet().getBalance().toFriendlyString());
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.falied(e.getMessage());
        }*/
    }
    
    @ApiOperation(value = "根据账号查询地址")
    @GetMapping("getAddress")
    /*@ApiImplicitParams({
        @ApiImplicitParam(paramType = "path", name = "account", value = "账号", dataType = "String", required = true)
    })*/
    public AjaxResponse getAddress(@RequestParam("account") String account){
    	UserCoin userCoin = userCoinMapper.getUserCoinByAccountAndCurrency(account, Constant.CURRENCY_BTC);
    	if(userCoin == null){
    		return AjaxResponse.falied(messageSource.getMessage("accountError"), CodeConstant.PARAM_ERROR);
    	}
        return AjaxResponse.success("查询成功", userCoin.getAddress());
    }
    
    /*@ApiOperation(value = "提币回调测试")
    @PostMapping("/withdraw")
    public String confirmCallback(@RequestParam("address") String address,@RequestParam("currencyType") int currencyType,
			@RequestParam("userName")String userName,@RequestParam("hxId")String hxId,@RequestParam("amount")BigDecimal amount) {
    	logger.info("--------address="+address);
    	String confirmCallback = withDrawService.confirmCallback(address, currencyType, userName, hxId, amount);
    	logger.info("---confirmCallback="+confirmCallback);
    	return confirmCallback;
    }
    
    @ApiOperation(value = "充值广播回调测试")
    @PostMapping("/depositBroadcastCallback")
    public String depositBroadcastCallback(@RequestParam("address") String address,@RequestParam("currencyType") int currencyType,
			@RequestParam("userName")String userName,@RequestParam("hxId")String hxId,@RequestParam("amount")BigDecimal amount) {
    	logger.info("--------address="+address);
    	String broadcastCallback = depositService.broadcastCallback(address, currencyType, userName, hxId, amount);
    	logger.info("---broadcastCallback="+broadcastCallback);
    	return broadcastCallback;
    }
    
    @ApiOperation(value = "充值广播回调测试")
    @PostMapping("/depositconfirmCallback")
    public String depositconfirmCallback(@RequestParam("address") String address,@RequestParam("currencyType") int currencyType,
			@RequestParam("userName")String userName,@RequestParam("hxId")String hxId,@RequestParam("amount")BigDecimal amount) {
    	logger.info("--------address="+address);
    	String depositconfirmCallback = depositService.confirmCallback(address, currencyType, userName, hxId, amount);
    	logger.info("---depositconfirmCallback="+depositconfirmCallback);
    	return depositconfirmCallback;
    }*/

}
