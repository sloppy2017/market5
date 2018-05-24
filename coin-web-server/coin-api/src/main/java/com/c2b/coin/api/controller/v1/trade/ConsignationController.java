package com.c2b.coin.api.controller.v1.trade;

import com.c2b.coin.api.annotation.Sign;
import com.c2b.coin.api.controller.v1.ApiBaseController;
import com.c2b.coin.common.AjaxResponse;
import com.c2b.coin.trade.api.ConsignationClient;
import com.c2b.coin.web.common.rest.bean.ResponseBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v1/trade")
@Api(value = "/v1/trade", description = "交易")
public class ConsignationController extends ApiBaseController {

  @Autowired
  private ConsignationClient consignationClient;

  @Sign
  @ApiOperation(value = "限价买入委托单")
  @PostMapping(value = "/limitPriceBuyConsignation")
  public ResponseBean limitPriceBuyConsignation(@RequestParam long userId, @RequestParam String bizType, @RequestParam String consignationPrice, @RequestParam String consignationCount) {
    AjaxResponse a = consignationClient.limitPriceBuyConsignation(userId, bizType, consignationPrice, consignationCount);
    return onSuccess();
  }

  @ApiOperation(value = "限价卖出委托单")
  @PostMapping(value = "/limitPriceSellConsignation")
  public ResponseBean limitPriceSellConsignation(@RequestParam long userId, @RequestParam String bizType, @RequestParam String consignationPrice, @RequestParam String consignationCount) {
    AjaxResponse a = consignationClient.limitPriceSellConsignation(userId, bizType, consignationPrice, consignationCount);
    return onSuccess();
  }

  @ApiOperation(value = "市价买入委托单")
  @PostMapping(value = "/marketPriceBuyConsignation")
  public ResponseBean marketPriceBuyConsignation(@RequestParam long userId, @RequestParam String bizType, @RequestParam String consignationCount) {
    AjaxResponse a = consignationClient.marketPriceBuyConsignation(userId, bizType, consignationCount);
    return onSuccess();
  }

  @ApiOperation(value = "市价卖出委托单")
  @PostMapping(value = "/marketPriceSellConsignation")
  public ResponseBean marketPriceSellConsignation(@RequestParam long userId, @RequestParam String bizType, @RequestParam String consignationCount) {
    AjaxResponse a = consignationClient.marketPriceSellConsignation(userId, bizType, consignationCount);
    return onSuccess();
  }

}
