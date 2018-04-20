package com.c2b.coin.account.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.c2b.coin.account.entity.AssetLog;
import com.c2b.coin.account.service.IWithdrawService;
import com.c2b.coin.common.enumeration.ErrorMsgEnum;
import com.c2b.coin.web.common.BaseRest;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(description="提币与其他服务调用相关接口")
@RestController
@RequestMapping("/client/withdraw")
public class WithDrawClientRest extends BaseRest{

	@Autowired
	private IWithdrawService withdrawServcie;

	@PostMapping("/confirmCallBack")
	@ApiOperation(value="确认监控时回调",notes="确认监控时回调")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "address", value = "地址", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "currencyType", value = "币种id", required = true, dataType = "int", paramType = "query"),
		@ApiImplicitParam(name = "userName", value = "用户名", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "hxId", value = "交易哈希", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "amount", value = "数量", required = true, dataType = "bigDecimal", paramType = "query"),
		@ApiImplicitParam(name = "orderNo", value = "订单号", required = true, dataType = "String", paramType = "query")
	})
	public String confirmCallback(@RequestParam("address") String address,@RequestParam("currencyType") int currencyType,
			@RequestParam("userName")String userName,@RequestParam("hxId")String hxId,@RequestParam("amount")BigDecimal amount,@RequestParam(value="orderNo")String orderNo) {
		try {
			this.withdrawServcie.confirmCallback(address,currencyType,userName,hxId,amount,orderNo);
			return writeJson(null);
		} catch (Exception e) {
			e.printStackTrace();
			return writeJson(ErrorMsgEnum.WITHDRAW_CONFIRMCALLBACK_ERROR);
		}
	}

	@PostMapping("/approvalWithDraw")
	@ApiOperation(value="确认监控时回调",notes="确认监控时回调")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "id", value = "充提币记录id", required = true, dataType = "int", paramType = "query"),
		@ApiImplicitParam(name = "currencyName", value = "币种名称", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "status", value = "审核状态", required = true, dataType = "int", paramType = "query")

	})
	public String approvalWithDraw(@RequestParam("id") int id,@RequestParam("currencyName")String currencyName,@RequestParam("status")int status) {
		try {
			this.withdrawServcie.approvalWithDraw(id,currencyName,status);
			return writeJson(null);
		} catch (Exception e) {
			e.printStackTrace();
			return writeJson(ErrorMsgEnum.WITHDRAW_ERROR);
		}
	}
	
	
	@GetMapping("/withDrawList")
	@ApiOperation(value="获取待审核的提币列表",notes="获取待审核的提币列表")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "pageNo", value = "第几页，默认是1", required = false, dataType = "int", paramType = "query"),
		@ApiImplicitParam(name = "pageSize", value = "分页大小,默认是20", required = false, dataType = "int", paramType = "query")
	})
	public String withDrawList(@RequestParam("pageNo") Integer pageNo,@RequestParam("pageSize") Integer pageSize){
		
		Page page=PageHelper.startPage(pageNo, pageSize, true, false);
		List<AssetLog> list= this.withdrawServcie.getWithDrawList();
		PageInfo<AssetLog> pageInfo=new PageInfo<>(list);
		return writeJson(pageInfo);
	}
	

}
