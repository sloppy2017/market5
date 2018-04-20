package com.c2b.coin.sanchain.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.sanchain.client.api.exception.APIException;
import org.sanchain.client.api.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.c2b.coin.common.AjaxResponse;
import com.c2b.coin.common.enumeration.ErrorMsgEnum;
import com.c2b.coin.sanchain.constant.SanChainConstant;
import com.c2b.coin.sanchain.entity.UserCoin;
import com.c2b.coin.sanchain.entity.WithdrawLog;
import com.c2b.coin.sanchain.exception.SanchainException;
import com.c2b.coin.sanchain.mapper.UserCoinMapper;
import com.c2b.coin.sanchain.service.ISanchainService;
import com.c2b.coin.sanchain.service.SysGencodeService;
import com.c2b.coin.web.common.BaseRest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;



@Api(description="sanchain其他服务的调用接口")
@RestController
@RequestMapping("/client/sanchain")
public class SanchainWalletController extends BaseRest{

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UserCoinMapper userCoinMapper;

	@Autowired
	private ISanchainService sanchainService;

	@Autowired
	private SysGencodeService sysGencodeService;


	@Value("${sanchain.hotwallet.address}")
	private String hotwalletAddress;
	
	
	/**
	 * 创建地址接口
	 * @param account
	 * @return
	 */
	@ApiOperation(value="插入资产变更记录",notes="插入资产变更记录时调用")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "account", value = "用户名", required = true, dataType = "String", paramType = "query")
	})
	@PostMapping("/create")
	public AjaxResponse createWallet(@RequestParam("account") String account) {
		//1.先查库
		Map<String, Object> data = new HashMap<String, Object>();
		UserCoin userCoin = userCoinMapper.getUserCoinByAccountAndCurrency(account, SanChainConstant.CURRENCY_SANCHAIN);
		if (userCoin != null) {
			return writeObj(userCoin.getAddress());
		}else {
			//2.生成钱包地址
			try {
				String address = this.sanchainService.createAddress(account);
				return writeObj(address);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("生成sanchain地址失败，原因："+e.getMessage());
				return writeObj(ErrorMsgEnum.DEPOSIT_ADDRESS_ADD_ERROR);
			}
		}
	}
	//  这里可以获取所有用户账户，然后调用Utils.getUserInfo
	//详见AutoSanGatherThread 88行
	@GetMapping("/balance")
	public AjaxResponse bananceWallet() {
		try {
			//导入数据库里所有key
			logger.info("---------ltc----导入数据库中的所有key------------");
			List<UserCoin> userCoinList = userCoinMapper.getAllUserCoin(SanChainConstant.CURRENCY_SANCHAIN);
			BigDecimal totalAmount = BigDecimal.ZERO;
			if (userCoinList != null) {
				for (UserCoin userCoin : userCoinList) {
					if (userCoin.getPrivateKey() != null && userCoin.getAddress() != null) {
						try {
							String address=userCoin.getAddress();
							BigDecimal san=getBalance(address);
							totalAmount=totalAmount.add(san);
						}catch (Exception e) {
							logger.info(e.getMessage(), e);
						}
					}
				}
			}
			
			BigDecimal hotAmount=getBalance(hotwalletAddress);
			BigDecimal total=hotAmount.add(totalAmount);
			Map<String,Object> resultMap=new HashMap<String,Object>();
			resultMap.put("account", totalAmount);
			resultMap.put("hotwallet", hotAmount);
			resultMap.put("total", total);
			return writeObj(resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			return writeObj(ErrorMsgEnum.SANC_CHECK_BALANCE_ERROR);
		}
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

	@PostMapping("/sendMoney")
	public AjaxResponse sendMoney(HttpServletRequest request, @RequestBody WithdrawLog withdrawLog) {
		try {
			if (withdrawLog == null || StringUtils.isBlank(withdrawLog.getAccount()) || withdrawLog.getMoney() == null ||
					StringUtils.isBlank(withdrawLog.getToAddress())) {
				return writeObj(ErrorMsgEnum.PARAM_ERROR);
			}
			UserCoin userCoin = userCoinMapper.getUserCoinByAccountAndCurrency(withdrawLog.getAccount(), SanChainConstant.CURRENCY_SANCHAIN);
			if (userCoin == null) {
				return writeObj(ErrorMsgEnum.SANC_ACCOUNT_NOT_EXIST);
			}
			if (userCoin.getAddress().equals(withdrawLog.getToAddress())) {
				return writeObj(ErrorMsgEnum.SANC_CANT_WITHDRAW_OWN);
			}
			UserCoin userCoin1 = userCoinMapper.getUserCoinByAddress(withdrawLog.getToAddress());
			if (userCoin1 != null) {
				return writeObj(ErrorMsgEnum.SANC_CANT_WITHDRAW_SYSTEM);
			}
			//校验地址是否有误
			int flag=Utils.checkCoralAccount(withdrawLog.getToAddress());

			if(flag!=1) {
				return writeObj(ErrorMsgEnum.SANC_ADDRESS_ERROR);
			}

			String uplimit = "";
			String lowlimit = "";
			List<Map<String, String>> codeList = sysGencodeService.findByGroupCode("WITHDRAW_LIMIT");
			for (Map<String, String> mapObj : codeList) {
				if ("SAN_LIMIT".equals(String.valueOf(mapObj.get("code_name")))) {
					uplimit = mapObj.get("uplimit").toString();
					lowlimit = mapObj.get("lowlimit").toString();
				}
			}
			if (withdrawLog.getMoney().compareTo(new BigDecimal(lowlimit)) < 0) {
				//				return AjaxResponse.falied(messageSource.getMessage("coinLowLimit", SanChainConstant.CURRENCY_SANCHAIN, lowlimit), CodeConstant.PARAM_ERROR);
				return writeObj(ErrorMsgEnum.SANC_LOWER_LIMIT_ERROR);
			}

			if (!StringUtils.isEmpty(uplimit) && withdrawLog.getMoney().compareTo(new BigDecimal(uplimit)) > 0) {
				//				return AjaxResponse.falied(messageSource.getMessage("coinUpLimit", SanChainConstant.CURRENCY_SANCHAIN, uplimit), CodeConstant.PARAM_ERROR);
				return writeObj(ErrorMsgEnum.SANC_OVER_LIMIT_ERROR);
			}

			String san_fee = "";//手续费（包含矿工费）
			List<Map<String, String>> codeList1 = sysGencodeService.findByGroupCode("WITHDRAW_SERVICE_CHARGE");
			for (Map<String, String> mapObj : codeList1) {
				if ("SAN_FEE".equals(String.valueOf(mapObj.get("code_name")))) {
					san_fee = mapObj.get("code_value").toString();
				}
			}
			String hash = sanchainService.sendMoney(withdrawLog);
			return writeObj(hash);
		} catch (Exception e) {
			if(e instanceof SanchainException) {
				return writeObj(ErrorMsgEnum.SANC_HOTWALLETBALANCE_NOT_ENOUGH);
			}
			
			e.printStackTrace();
		}
		return writeObj(ErrorMsgEnum.SANC_WITHDRAW_FAIL);
	}

}
