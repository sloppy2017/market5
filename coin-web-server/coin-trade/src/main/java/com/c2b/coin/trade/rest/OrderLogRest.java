package com.c2b.coin.trade.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.c2b.coin.common.AjaxResponse;
import com.c2b.coin.trade.service.OrderLogService;
import com.c2b.coin.web.common.BaseRest;

/**
 *
 * @author Anne
 * @date 2017.10.19
 */
@Api("历史成交记录表查询相关服务")
@RestController
public class OrderLogRest extends BaseRest {

	@Autowired
	OrderLogService orderLogService;

	@ApiOperation(value = "历史成交记录列表--我的24H成交调用",  httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, notes="返回值字段含义：consignation_status '委托状态 1-委托中 2-未成交 3-部分成交 4-全部成交 5-已撤单',made_count '成交量', create_time '成交时间',biz_type '成交对', trade_type '交易类型',after_consignation_count '成交后委托量', consignation_count '总委托量',consignation_price '委托价',made_average_price '成交均价', unTradeCount '未成交'，consignationTotalMoney '委托总额'")
	@RequestMapping(value = "/listOrderLog", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public AjaxResponse listOrderLog(){
	    return writeObj(orderLogService.listOrderLogForTwentyFour(getUserId()));
	}

  @ApiOperation(value = "历史成交记录列表(分页)-订单管理【已成交】查询调用",  httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, notes="返回值字段含义：consignation_status '委托状态 1-委托中 2-未成交 3-部分成交 4-全部成交 5-已撤单',made_count '成交量', create_time '成交时间',biz_type '成交对', trade_type '交易类型',after_consignation_count '成交后委托量', consignation_count '总委托量',consignation_price '委托价',made_average_price '成交均价', unTradeCount '未成交'，consignationTotalMoney '委托总额'")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "bizType", value = "交易对", required = true, paramType = "query", dataType = "String") })
  @RequestMapping(value = "/listOrder", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public AjaxResponse listOrderLog(@RequestParam String bizType, @RequestParam(value = "pageNo") String pageNo, @RequestParam(value = "pageSize") String pageSize){
    return writeObj(orderLogService.listOrder(getUserId(), bizType, pageNo, pageSize));
  }

}
