package com.c2b.coin.account.controller;

import com.c2b.coin.account.annotation.MarketingActivity;
import com.c2b.coin.account.service.IDepositService;
import com.c2b.coin.common.MarketingActivityType;
import com.c2b.coin.common.enumeration.ErrorMsgEnum;
import com.c2b.coin.web.common.BaseRest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Api(description="充币与其他服务调用相关接口")
@RestController
  @RequestMapping("/client/deposit")
public class DepositClientRest extends BaseRest{

	@Autowired
	private IDepositService depositService;



	@PostMapping("/broadcastCallback")
	@ApiOperation(value="广播监控时回调",notes="广播监控时回调")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "address", value = "地址", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "currencyType", value = "币种id", required = true, dataType = "int", paramType = "query"),
		@ApiImplicitParam(name = "userName", value = "用户名", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "hxId", value = "交易哈希", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "amount", value = "数量", required = true, dataType = "bigDecimal", paramType = "query")

	})
	public String broadcastCallback(@RequestParam("address") String address,@RequestParam("currencyType") int currencyType,
			@RequestParam("userName")String userName,@RequestParam("hxId")String hxId,@RequestParam("amount")BigDecimal amount){
		try {
			this.depositService.broadcastCallback(address,currencyType,userName,hxId,amount);
			return writeJson(null);
		} catch (Exception e) {
			e.printStackTrace();
			return writeJson(ErrorMsgEnum.DEPOSIT_BROADCASTCALLBACK_ERROR);
		}
	}


	@PostMapping("/confirmCallback")
	@ApiOperation(value="确认监控时回调",notes="确认监控时回调")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "address", value = "地址", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "currencyType", value = "币种id", required = true, dataType = "int", paramType = "query"),
		@ApiImplicitParam(name = "userName", value = "用户名", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "hxId", value = "交易哈希", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "amount", value = "数量", required = true, dataType = "bigDecimal", paramType = "query")

	})
  @MarketingActivity(value={MarketingActivityType.FIREST_RECHARGE_TRADE})
	public String confirmCallback(@RequestParam("address") String address,@RequestParam("currencyType") int currencyType,
			@RequestParam("userName")String userName,@RequestParam("hxId")String hxId,@RequestParam("amount")BigDecimal amount){
		try {
			this.depositService.confirmCallback(address,currencyType,userName,hxId,amount);
			return writeJson(null);
		} catch (Exception e) {
			e.printStackTrace();
			return writeJson(ErrorMsgEnum.DEPOSIT_CONFIRMCALLBACK_ERROR);
		}
	}



}
