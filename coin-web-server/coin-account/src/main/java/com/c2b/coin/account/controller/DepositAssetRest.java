package com.c2b.coin.account.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.c2b.coin.account.service.IDepositService;
import com.c2b.coin.common.enumeration.ErrorMsgEnum;
import com.c2b.coin.web.common.BaseRest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(description="用户充值相关接口")
@RestController
@RequestMapping("/deposit")
public class DepositAssetRest extends BaseRest {
	
	@Autowired
	private IDepositService depositService;
	
//	@PostMapping("/addAddress")
//	@ApiOperation(value="新增充币地址",notes="获取充币地址时调用")
//	@ApiImplicitParam(name = "currencyType", value = "币种类型", required = true, dataType = "int", paramType = "query")
//	public String addAddress(int currencyType) {
//		long userId=Long.valueOf(this.getUserId());
//		try {
//			this.depositService.addAddress(userId,currencyType);
//			return writeJson(null);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return writeJson(ErrorMsgEnum.DEPOSIT_ADDRESS_ADD_ERROR);
//		}
//	}
	
	@PostMapping("/getAddress")
	@ApiOperation(value="获取充币地址",notes="返回值为地址串")
	@ApiImplicitParam(name = "currencyType", value = "币种类型", required = true, dataType = "int", paramType = "query")
	@ApiResponse(code=2008,message="GET_DEPOSIT_ADDRESS_ERROR,获取地址失败")
	public String getAddress(@RequestParam("currencyType") int currencyType,@RequestParam("currencyName")String currencyName) {
		long userId=Long.valueOf(this.getUserId());
		String userName=this.getUsername();
		try {
			String address=this.depositService.getAddress(userId,userName,currencyType,currencyName);
			return writeJson(address);
		} catch (Exception e) {
			e.printStackTrace();
			return writeJson(ErrorMsgEnum.GET_DEPOSIT_ADDRESS_ERROR);
		}
	}
	 
}
