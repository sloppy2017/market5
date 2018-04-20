package com.c2b.coin.account.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.c2b.coin.account.service.IActivityAssetService;
import com.c2b.coin.common.AjaxResponse;
import com.c2b.coin.common.enumeration.ErrorMsgEnum;
import com.c2b.coin.web.common.BaseRest;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/client/activity")
public class ActivityClientRest extends BaseRest{

	@Autowired
	private IActivityAssetService activityAssetService;

	@PostMapping("addActivityAsset")
	@ApiOperation(value="增加活动增币时调用接口",notes="增加活动增币时调用接口")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "currencyType", value = "币种id", required = true, dataType = "int", paramType = "query"),
		@ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "long", paramType = "query"),
		@ApiImplicitParam(name = "userName", value = "用户名", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "amount", value = "数量", required = true, dataType = "bigDecimal", paramType = "query"),
		@ApiImplicitParam(name = "operationType", value = "类型，1:充币，2:提币，3:注册送币 4:活动送币", required = true, dataType = "int", paramType = "query"),
    @ApiImplicitParam(name = "remark", value = "备注（活动信息）", required = true, dataType = "String", paramType = "query")
	})
	public AjaxResponse addActivityAsset(@RequestParam("userId") long userId,@RequestParam("currencyType") int currencyType,
			@RequestParam("userName")String userName,@RequestParam("amount")BigDecimal amount,@RequestParam("operationType")int operationType,@RequestParam("remark")String remark) {
		try {
			this.activityAssetService.addActivityAsset(userId,userName,currencyType,amount,operationType,remark);
			return writeObj(null);
		} catch (Exception e) {
			e.printStackTrace();
			return writeObj(ErrorMsgEnum.ASSET_CHANGE_ERROR);
		}
	}
}
