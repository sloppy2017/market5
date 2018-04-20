package com.c2b.coin.account.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.c2b.coin.account.entity.WithdrawAddressLog;
import com.c2b.coin.account.exception.AssetChangeException;
import com.c2b.coin.account.service.IUserAccountAssetService;
import com.c2b.coin.account.service.IWithdrawService;
import com.c2b.coin.common.AjaxResponse;
import com.c2b.coin.common.enumeration.ErrorMsgEnum;
import com.c2b.coin.web.common.BaseRest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(description = "用户提币相关接口")
@RestController
@RequestMapping("/withdraw")
public class WithdrawAssetRest extends BaseRest {

  @Autowired
  private IWithdrawService withdrawService;
  @Autowired
  IUserAccountAssetService userAccountAssetService;


  @PostMapping()
  @ApiOperation(value = "提币接口", notes = "提币时调用")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "currencyType", value = "币种", required = true, dataType = "int", paramType = "query"),
    @ApiImplicitParam(name = "currencyName", value = "币种名称", required = true, dataType = "int", paramType = "query"),
    @ApiImplicitParam(name = "address", value = "地址", required = true, dataType = "String", paramType = "query"),
    @ApiImplicitParam(name = "remark", value = "备注标签", required = false, dataType = "int", paramType = "query"),
    @ApiImplicitParam(name = "amount", value = "提币金额", required = true, dataType = "int", paramType = "query"),
    @ApiImplicitParam(name = "password", value = "资金密码", required = true, dataType = "String", paramType = "query"),
    @ApiImplicitParam(name = "verifyType", value = "验证类型（google/sms 1/0）", required = true, paramType = "query", dataType = "string"),
    @ApiImplicitParam(name = "verifyCode", value = "验证码", required = true, paramType = "query", dataType = "string")
  })
  public Object withdraw(@RequestParam("currencyType") int currencyType, @RequestParam("currencyName") String currencyName, @RequestParam("address") String address,
                         @RequestParam("remark") String remark, @RequestParam("amount") BigDecimal amount, @RequestParam("password") String password,@RequestParam("verifyType")String verifyType,@RequestParam("verifyCode")String verifyCode) {
    long userId = Long.valueOf(this.getUserId());
    String userName = this.getUsername();
    if (amount==null||StringUtils.isEmpty(amount.toString()) || amount.compareTo(BigDecimal.ZERO) == 0){
      return writeJson(ErrorMsgEnum.WITHDRAW_IS_NULL);
    }
    //校验是否可以提币
    AjaxResponse ajaxResponse = this.withdrawService.checkPermission(userId);
    if (!ajaxResponse.isSuccess()) {
      return writeJson(ErrorMsgEnum.USER_AUTH_FAILURE);
    }
    //校验提币金额
    boolean lessThanLimit = this.withdrawService.checkAmountLimit(currencyName, amount);
    if (!lessThanLimit) {
      return writeFailureJsonWithParam(ErrorMsgEnum.WITHDRAW_OVER_LIMIT, withdrawService.getLimit(currencyName));
    }
    boolean greaterThenMax = this.withdrawService.checkAmountMax(userId,currencyName,amount);
    if (!greaterThenMax) {
      return writeFailureJsonWithParam(ErrorMsgEnum.WITHDRAW_OVER_DAILY_MAX, withdrawService.getDailyMax(currencyName));
    }
    if (!userAccountAssetService.checkAvailableAsset(userId, currencyType, amount)){
      return writeJson(ErrorMsgEnum.WITHDRAS_NOT_ENOUTH);
    }
    //2.校验用户资金密码
    AjaxResponse checkAssetPassword = this.withdrawService.checkAssetPassword(userId, password, verifyType, verifyCode);
    if (checkAssetPassword.isSuccess()) {//校验资金密码
      try {
        this.withdrawService.withdraw(userId, currencyType, currencyName, address, remark, amount, userName);
        return writeJson(null);
      } catch (Exception e) {
        e.printStackTrace();
        if(e instanceof AssetChangeException) {
        	AssetChangeException ace=(AssetChangeException) e;
        	return writeJson(ace.getMsg());
        }
        
        return writeJson(ErrorMsgEnum.WITHDRAW_ERROR);
      }
    } else {
      return writeJson(errorMsgEnumMap.get(checkAssetPassword.getError().getCode()));
    }
  }


  @PostMapping("/addAddress")
  @ApiOperation(value = "添加提币地址接口", notes = "在提币时,添加新的提币地址时调用")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "currencyType", value = "币种", required = true, dataType = "int", paramType = "query"),
    @ApiImplicitParam(name = "address", value = "地址", required = true, dataType = "String", paramType = "query"),
    @ApiImplicitParam(name = "remark", value = "备注标签", required = false, dataType = "int", paramType = "query")
  })
  public String addAddress(@RequestParam("currencyType") int currencyType, @RequestParam("address") String address, @RequestParam("remark") String remark) {
    long userId = Long.valueOf(this.getUserId());
    WithdrawAddressLog w = new WithdrawAddressLog();
    w.setUserId(userId);
    w.setCurrencyType(currencyType);
    w.setAddress(address);
    w.setRemark(remark);
    try {
      this.withdrawService.addAddress(w);
      return writeJson(null);
    } catch (Exception e) {
      e.printStackTrace();
      return writeJson(ErrorMsgEnum.WITHDRAW_ADDRESS_ADD_ERROR);
    }
  }

  @PostMapping("/addressList")
  @ApiOperation(value = "提币地址列表", notes = "提币下拉菜单调用")
  @ApiImplicitParam(name = "currencyType", value = "币种", required = true, dataType = "int", paramType = "query")
  public String getAddressList(@RequestParam("currencyType") Integer currencyType) {
    long userId = Long.valueOf(this.getUserId());
    return writeJson(this.withdrawService.getAddressList(currencyType, userId));
  }

  
  private static final Map<String, ErrorMsgEnum> errorMsgEnumMap = new HashMap<>();
  static {
    for (ErrorMsgEnum errorMsgEnum : ErrorMsgEnum.values()) {
    	errorMsgEnumMap.put(errorMsgEnum.getCode(),errorMsgEnum);
    }
  }
  
  @PostMapping("/deleteAddress")
  @ApiOperation(value="删除提币地址",notes= "删除提币地址时调用")
  @ApiImplicitParam(name="id",value="id",required=true,dataType="long",paramType="query")
  public String deleteAddress(@RequestParam("id") long id) {
	  this.withdrawService.deleteAddress(id);
	  return writeJson(null);
  }
  
  
}
