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
import com.c2b.coin.trade.service.TradePairInfoService;
import com.c2b.coin.web.common.BaseRest;

/**
 *
 * @author Anne
 * @date 2017.10.19
 */
@Api("交易对查询相关服务")
@RestController
@RequestMapping("/nologin")
public class TradePairInfoRest extends BaseRest {

  @Autowired
  TradePairInfoService tradePairInfoService;

  @Autowired
  OrderLogService orderLogService;

  @ApiOperation(value = "币币交易对列表", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, notes="接口返回值字段含义：左列表示交易对主键，即biztype；右列表示币种交易对信息，前面是标第货币，后面是基础货币。")
  @RequestMapping(value = "/listTradePairInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public AjaxResponse listTradePairInfo() {
    return writeObj(tradePairInfoService.listTradePairInfo());
  }

  @ApiOperation(value = "获取可用的币种信息列表", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, notes="接口返回值字段含义：coin_name '币种名称',coin_full_name '币种全称',is_enabled '是否启用 1-启用  0-禁用',create_time '创建时间',update_time '更新时间'")
  @RequestMapping(value = "/listCurrencies", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public AjaxResponse listCurrencies() {
    return writeObj(tradePairInfoService.listCurrencies());
  }

  @ApiOperation(value = "根据交易币种id获取交易对信息", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, notes="接口返回值字段含义：commodity_coin '标第货币id',commodity_coin_name '标第货币名称',money_coin '基础货币id',money_coin_name '基础货币名称',data_status '数据状态(0：未删除 1：已删除)',create_time '创建时间',update_time '修改时间'")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "commodityCoin", value = "商品币ID", required = true, paramType = "query", dataType = "Long"),
      @ApiImplicitParam(name = "moneyCoin", value = "金钱币ID", required = true, paramType = "query", dataType = "Long") })
  @RequestMapping(value = "/getTradePairInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public AjaxResponse getTradePairInfo(@RequestParam Long commodityCoin,
      @RequestParam Long moneyCoin) {
    return writeObj(tradePairInfoService.getTradePairInfoByTradePairInfo(
        commodityCoin, moneyCoin));
  }

  @ApiOperation(value = "根据主键获取交易对信息", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, notes="接口返回值字段含义：commodity_coin '标第货币id',commodity_coin_name '标第货币名称',money_coin '基础货币id',money_coin_name '基础货币名称',data_status '数据状态(0：未删除 1：已删除)',create_time '创建时间',update_time '修改时间'")
  @ApiImplicitParams({ @ApiImplicitParam(name = "id", value = "交易对主键", required = true, paramType = "query", dataType = "Long") })
  @RequestMapping(value = "/getTradePairInfoByPK", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public AjaxResponse getTradePairInfoByPK(@RequestParam Long id) {
    return writeObj(tradePairInfoService.getTradePairInfoByPK(id));
  }

  @ApiOperation(value = "实时成交记录（含分页）", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, notes = "返回值字段含义：consignation_no '委托单号',order_no '订单号',made_price '成交价',made_count '成交量', made_time '成交时间',biz_type '成交对', trade_type '交易类型',pre_consignation_count '成交前委托量',after_consignation_count '成交后委托量', consignation_count '总委托量',consignation_price '委托价',made_average_price '成交均价',consignation_time '委托时间',poundage '成交手续费',order_type '订单类型(限价交易/市价交易)',user_id '用户ID',unTradeCount '未成交'，consignationTotalMoney '委托总额'")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "bizType", value = "交易对", required = true, paramType = "query", dataType = "String"),
      @ApiImplicitParam(name = "pageNo", value = "页码", required = true, paramType = "query", dataType = "int"),
      @ApiImplicitParam(name = "pageSize", value = "每页显示数量", required = true, paramType = "query", dataType = "int") })
  @RequestMapping(value = "/listRealTimeOrderLog", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public AjaxResponse listRealTimeOrderLog(@RequestParam String bizType, @RequestParam int pageNo,
      @RequestParam int pageSize) {
    return writeObj(orderLogService.orderListRealTime(bizType, pageNo, pageSize));
  }

  @ApiOperation(value = "IOS获取市场数据", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, notes = "返回值字段含义：id '市场币种ID', name '市场币种名称', data '该市场下所有交易币种数据', data下的commodityCoin '交易币种ID', data下的commodityCoinName '交易币种名称'")
  @RequestMapping(value = "/listTradePairInfoByMarket", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public AjaxResponse listTradePairInfoByMarket(){
    return writeObj(tradePairInfoService.listTradePairInfoByMarket());
  }
}
