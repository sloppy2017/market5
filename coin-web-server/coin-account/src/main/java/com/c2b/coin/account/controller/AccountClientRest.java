package com.c2b.coin.account.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.c2b.coin.account.exception.AssetChangeException;
import com.c2b.coin.account.service.IAssetChangeLogService;
import com.c2b.coin.account.service.IUserAccountAssetService;
import com.c2b.coin.common.AjaxResponse;
import com.c2b.coin.common.enumeration.ErrorMsgEnum;
import com.c2b.coin.web.common.BaseRest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(description = "资产与其他服务的调用接口")
@RestController
@RequestMapping("/client/account")
public class AccountClientRest extends BaseRest {

  @Autowired
  private IAssetChangeLogService assetChangeLogService;

  @Autowired
  private IUserAccountAssetService userAccountAssetService;

  @GetMapping("/asset/total")
  @ApiOperation(value = "获取资产总计接口", notes = "'totalBTC':'总资产折算BTC','totalUSD':'总资产折算usd','availableBTC':'可用资产折算BTC','availableUSD':'可用资产折算usd','freezingBTC','冻结资产折算BTC','freezingUSD','冻结资产折算usd'")
  public String getAssetTotal(@RequestParam long userId) {
    return writeJson(this.userAccountAssetService.getAssetTotal(userId));
  }

  @GetMapping("/asset/list")
  @ApiOperation(value = "获取总资产接口", notes = "'currencyType':'货币类型标识','currencyName','货币名称','currencyFullName':'货币全称','totalAmount':'货币总量','availableAmount':'可用数量','freezingAmount':'冻结数量'")
  public String getAssetList(@RequestParam long userId) {
    return writeJson(this.userAccountAssetService.getAssetList(userId));
  }

  @PostMapping("/assetChange")
  @ApiOperation(value = "插入资产变更记录", notes = "插入资产变更记录时调用")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "long", paramType = "query"),
    @ApiImplicitParam(name = "userName", value = "用户名称", required = true, dataType = "String", paramType = "query"),
    @ApiImplicitParam(name = "orderNo", value = "订单号", required = true, dataType = "int", paramType = "query"),
    @ApiImplicitParam(name = "bizType", value = "变动类型", required = true, dataType = "int", paramType = "query"),
    @ApiImplicitParam(name = "currencyType", value = "币种类型", required = true, dataType = "int", paramType = "query"),
    @ApiImplicitParam(name = "amount", value = "变动数量", required = true, dataType = "int", paramType = "query")
  })
  public String assetChange(long userId, String userName, String orderNo, int bizType, int currencyType, BigDecimal amount) {
    try {
      this.assetChangeLogService.assetChange(userId, userName, orderNo, bizType, currencyType, amount);
      return writeJson(null);
    } catch (Exception e) {
      if (e instanceof AssetChangeException) {
        AssetChangeException ace = (AssetChangeException) e;
        return writeJson(ace.getMsg());
      }
      return writeJson(ErrorMsgEnum.ASSET_CHANGE_ERROR);
    }
  }

  @PostMapping("/checkAvailableAsset")
  @ApiOperation(value = "校验可用余额", notes = "校验可用余额")
  public boolean checkAvailableAsset(long userId, int currencyType, BigDecimal amount) {
    return this.userAccountAssetService.checkAvailableAsset(userId, currencyType, amount);
  }

  @PostMapping("/tradePairAssetChange")
  @ApiOperation(value = "交易对撮合成功后资产变动接口", notes = "交易对撮合成功后调用")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "buyerUserId", value = "买方用户id", required = true, dataType = "long", paramType = "query"),
    @ApiImplicitParam(name = "buyerUserName", value = "买方用户名称", required = true, dataType = "String", paramType = "query"),
    @ApiImplicitParam(name = "sellerUserId", value = "卖方用户id", required = true, dataType = "long", paramType = "query"),
    @ApiImplicitParam(name = "sellerUserName", value = "卖方用户名称", required = true, dataType = "String", paramType = "query"),
    @ApiImplicitParam(name = "orderNo", value = "订单号", required = true, dataType = "int", paramType = "query"),
    @ApiImplicitParam(name = "baseCurrencyType", value = "基础货币id", required = true, dataType = "int", paramType = "query"),
    @ApiImplicitParam(name = "targetCurrencyType", value = "标的货币id", required = true, dataType = "int", paramType = "query"),
    @ApiImplicitParam(name = "baseAmount", value = "基础货币变动数量", required = true, dataType = "int", paramType = "query"),
    @ApiImplicitParam(name = "targetAmount", value = "标的货币变动数量", required = true, dataType = "int", paramType = "query"),
    @ApiImplicitParam(name = "baseRate", value = "基础货币交易费率", required = true, dataType = "int", paramType = "query"),
    @ApiImplicitParam(name = "targetRate", value = "标的货币交易费率", required = true, dataType = "int", paramType = "query")
  })
  public String tradePairAssetChange(long buyerUserId, String buyerUserName, long sellerUserId, String sellerUserName, String orderNo, int baseCurrencyType, int targetCurrencyType, BigDecimal baseAmount, BigDecimal targetAmount, BigDecimal baseRate, BigDecimal targetRate) {
    try {
      this.assetChangeLogService.tradePairAssetChange(buyerUserId, buyerUserName, sellerUserId, sellerUserName, orderNo, baseCurrencyType, targetCurrencyType, baseAmount, targetAmount, baseRate, targetRate);
      return writeJson(null);
    } catch (Exception e) {
      if (e instanceof AssetChangeException) {
        AssetChangeException ace = (AssetChangeException) e;
        return writeJson(ace.getMsg());
      }
      return writeJson(ErrorMsgEnum.ASSET_CHANGE_ERROR);
    }
  }

  @PostMapping("/discountPoundage")
  @ApiOperation(value = "交易手续费减免接口", notes = "交易对撮合成功后调用")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "buyerUserId", value = "买方用户id", required = true, dataType = "long", paramType = "query"),
    @ApiImplicitParam(name = "buyerUserName", value = "买方用户名称", required = true, dataType = "String", paramType = "query"),
    @ApiImplicitParam(name = "sellerUserId", value = "卖方用户id", required = true, dataType = "long", paramType = "query"),
    @ApiImplicitParam(name = "sellerUserName", value = "卖方用户名称", required = true, dataType = "String", paramType = "query"),
    @ApiImplicitParam(name = "orderNo", value = "订单号", required = true, dataType = "int", paramType = "query"),
    @ApiImplicitParam(name = "baseCurrencyType", value = "基础货币id", required = true, dataType = "int", paramType = "query"),
    @ApiImplicitParam(name = "targetCurrencyType", value = "标的货币id", required = true, dataType = "int", paramType = "query"),
    @ApiImplicitParam(name = "baseAmount", value = "基础货币变动数量(卖方)", required = true, dataType = "int", paramType = "query"),
    @ApiImplicitParam(name = "targetAmount", value = "标的货币变动数量(买方)", required = true, dataType = "int", paramType = "query"),
    @ApiImplicitParam(name = "remark", value = "备注信息（存在活动唯一id信息）", required = true, dataType = "String", paramType = "query")
  })
  public AjaxResponse discountPoundage(long buyerUserId, String buyerUserName, long sellerUserId, String sellerUserName, String orderNo, int baseCurrencyType, int targetCurrencyType, BigDecimal baseAmount, BigDecimal targetAmount, String remark) {
    try {
      this.assetChangeLogService.discountPoundage(buyerUserId, buyerUserName, sellerUserId, sellerUserName, orderNo, baseCurrencyType, targetCurrencyType, baseAmount, targetAmount, remark);
      return writeObj(null);
    } catch (Exception e) {
      return writeObj(ErrorMsgEnum.ASSET_CHANGE_ERROR);
    }
  }

}
