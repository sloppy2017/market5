package com.c2b.coin.account.controller;

import com.c2b.coin.account.service.IActivityAssetService;
import com.c2b.coin.common.AjaxResponse;
import com.c2b.coin.common.enumeration.ErrorMsgEnum;
import com.c2b.coin.web.common.BaseRest;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/activity")
public class ActivityRest extends BaseRest{

	@Autowired
	private IActivityAssetService activityAssetService;
  @PostMapping("coin/{activityName}")
  @ApiOperation(value="获取活动送币信息",notes="获取活动送币信息")
  @ApiImplicitParam(name = "activityName", value = "活动名称", required = true, dataType = "String", paramType = "path")
  public String getOnlineActivityCoin(@PathVariable(value = "activityName") String activityName){
    return writeJson(activityAssetService.getActivityCoin(getUserId(),activityName));
  }

}
