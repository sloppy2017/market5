package com.c2b.coin.api.controller.v1.trade;

import com.c2b.coin.api.annotation.Sign;
import com.c2b.coin.api.controller.BaseController;
import com.c2b.coin.common.AjaxResponse;
import com.c2b.coin.common.enumeration.ErrorMsgEnum;
import com.c2b.coin.trade.api.ConsignationClient;
import com.c2b.coin.web.common.rest.bean.ResponseBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v1/trade")
@Api(value = "/v1/trade", description = "交易")
public class ConsignationController extends BaseController {

  @Autowired
  private ConsignationClient consignationClient;

  @Sign
  @ApiOperation(value = "限价买入委托单")
  @PostMapping(value = "/limitPriceBuyConsignation")
  public ResponseBean limitPriceBuyConsignation(@RequestParam String bizType, @RequestParam String consignationPrice, @RequestParam String consignationCount) {
    AjaxResponse response = consignationClient.limitPriceBuyConsignation(Long.parseLong(getThreadContextMap().getUserId()), bizType, consignationPrice, consignationCount);
    if (response.isSuccess()) {
      return onSuccess();
    } else {
      return onFailure(ErrorMsgEnum.SERVER_BUSY);
    }
  }

  @ApiOperation(value = "限价卖出委托单")
  @PostMapping(value = "/limitPriceSellConsignation")
  public ResponseBean limitPriceSellConsignation(@RequestParam String bizType, @RequestParam String consignationPrice, @RequestParam String consignationCount) {
    AjaxResponse response = consignationClient.limitPriceSellConsignation(Long.parseLong(getThreadContextMap().getUserId()), bizType, consignationPrice, consignationCount);
    if (response.isSuccess()) {
      return onSuccess();
    } else {
      return onFailure(ErrorMsgEnum.SERVER_BUSY);
    }
  }

  @ApiOperation(value = "市价买入委托单")
  @PostMapping(value = "/marketPriceBuyConsignation")
  public ResponseBean marketPriceBuyConsignation(@RequestParam String bizType, @RequestParam String consignationCount) {
    AjaxResponse response = consignationClient.marketPriceBuyConsignation(Long.parseLong(getThreadContextMap().getUserId()), bizType, consignationCount);
    if (response.isSuccess()) {
      return onSuccess();
    } else {
      return onFailure(ErrorMsgEnum.SERVER_BUSY);
    }
  }

  @ApiOperation(value = "市价卖出委托单")
  @PostMapping(value = "/marketPriceSellConsignation")
  public ResponseBean marketPriceSellConsignation(@RequestParam String bizType, @RequestParam String consignationCount) {
    AjaxResponse response = consignationClient.marketPriceSellConsignation(Long.parseLong(getThreadContextMap().getUserId()), bizType, consignationCount);
    if (response.isSuccess()) {
      return onSuccess();
    } else {
      return onFailure(ErrorMsgEnum.SERVER_BUSY);
    }
  }

  @ApiOperation(value = "撤单")
  @PostMapping(value = "/revokeOrder")
  public ResponseBean revokeOrder(@RequestParam String consignationNo) {
    AjaxResponse response = consignationClient.revokeOrder(getThreadContextMap().getUserId(), consignationNo);
    if (response.isSuccess()) {
      return onSuccess();
    } else {
      return onFailure(ErrorMsgEnum.SERVER_BUSY);
    }
  }

  @ApiOperation(value = "查询所有委托单-订单管理调用（含分页）")
  @GetMapping(value = "/getBuySellOrder")
  public ResponseBean getBuySellOrder(@RequestParam int pageNo, @RequestParam int pageSize, @RequestParam int consignationStatus, @RequestParam int bizType) {
    AjaxResponse response = consignationClient.getBuySellOrder(getThreadContextMap().getUserId(), pageNo, pageSize, consignationStatus, bizType);
    if (response.isSuccess()) {
      return onSuccess();
    } else {
      return onFailure(ErrorMsgEnum.SERVER_BUSY);
    }
  }

}
