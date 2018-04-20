package com.c2b.coin.market.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.c2b.coin.common.AjaxResponse;
import com.c2b.coin.market.service.MarketService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@RestController
public class MarketApiImpl implements MarketApi {

  private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

  @Autowired
  private MarketService marketService;

  @Value("${web.nginx.front}")
  protected String frontPath ;

  @ApiOperation(value = "一个交易对的最新价格信息", httpMethod = "POST")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "commodity", value = "交易币，例如LTC、omg等", required = true, dataType = "string", paramType = "path"),
    @ApiImplicitParam(name = "money", value = "基币，例如BTC、ETH等", required = true, dataType = "string", paramType = "path")
  })
  @RequestMapping(value = "/nologin/realtime/price/{commodity}/{money}",method = RequestMethod.POST,produces = "application/json")
  public AjaxResponse getRealTimePrice(@PathVariable(name="commodity") String commodity, @PathVariable(name="money") String money){
    BigDecimal price = this.marketService.getRealTimePrice(commodity,money);
    return AjaxResponse.success(price);
  }

  @ApiOperation(value = "所有交易对的最新价格信息", httpMethod = "POST")
  @RequestMapping(value = "/nologin/all/realtime/price",method = RequestMethod.POST,produces = "application/json")
  public Callable<AjaxResponse> getRealTimePrice() {
    return () -> {
        Map price = marketService.getAllRealTimePrice();
        return AjaxResponse.success(price);
    };
  }

  @ApiOperation(value = "一个交易对儿最近24小时最高、最低、最新价格、成交量、涨幅信息", httpMethod = "POST")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "commodity", value = "交易币，例如LTC、omg等", required = true, dataType = "string", paramType = "path"),
    @ApiImplicitParam(name = "money", value = "基币，例如BTC、ETH等", required = true, dataType = "string", paramType = "path")
  })
  @RequestMapping(value = "/nologin/realtime/index/{commodity}/{money}",method = RequestMethod.POST,produces = "application/json")
  public Callable<AjaxResponse> getRealTimeIndexPrice(@PathVariable(name="commodity") String commodity, @PathVariable(name="money") String money){
    return () -> {
      String json = this.marketService.get24HNewsInfo(commodity+"/"+money);
      if("".equals(json)){
        return AjaxResponse.failure("FAILED!");
      }
      JSONObject jsonObject = JSONObject.parseObject(json);
      return AjaxResponse.success(jsonObject);
    };
  }

  @ApiOperation(value = "所有交易对儿最近24小时最高、最低、最新价格、成交量、涨幅信息", httpMethod = "POST")
  @RequestMapping(value = "/nologin/all/realtime/index",method = RequestMethod.POST,produces = "application/json")
  public Callable<AjaxResponse> getAllRealTimeIndexPrice(){
    return () -> {
      String json = this.marketService.getAll24HNewsInfo();
      if("".equals(json)){
        return AjaxResponse.failure("FAILED!");
      }
      JSONObject jsonObject = JSONObject.parseObject(json);
      return AjaxResponse.success(jsonObject);
    };
  }

  @RequestMapping(value = "/nologin/realtime/{type}/index/{commodity}/{money}",method = RequestMethod.POST,produces = "application/json")
  @ApiOperation(value = "实时买卖盘口信息", httpMethod = "POST")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "type", value = "类型：买盘：buy 卖盘：sell", required = true, dataType = "string", paramType = "path"),
    @ApiImplicitParam(name = "commodity", value = "交易币，例如LTC、omg等", required = true, dataType = "string", paramType = "path"),
    @ApiImplicitParam(name = "money", value = "基币，例如BTC、ETH等", required = true, dataType = "string", paramType = "path"),
    @ApiImplicitParam(name = "max", value = "显示最大值，不超过100", required = false, dataType = "string", paramType = "query")
  })
  public Callable<AjaxResponse> getRealTimeSellPrice(@PathVariable(name = "type") String type, @PathVariable(name = "commodity") String commodity,
                                     @PathVariable(name = "money") String money, @RequestParam(name = "max",required = false) String max){
    if(StringUtils.isEmpty(max)){
      max = "100";
    }
    String newMax = max;
    return () -> {
      if(type.toUpperCase().equals("SELL") == false && type.toUpperCase().equals("BUY") == false){
        return AjaxResponse.failure("type is wrong!");
      }
      if(StringUtils.isEmpty(commodity) || StringUtils.isEmpty(money)){
        return AjaxResponse.failure("commodity or money is wrong!");
      }
      String currencyType = commodity.toUpperCase() + "/" + money.toUpperCase();
      JSONArray jsonArray = this.marketService.getAgencyDataByType(currencyType ,type.toUpperCase(), Integer.valueOf(newMax));
      return AjaxResponse.success(jsonArray);
    };
  }


  @RequestMapping(value = "/nologin/kline/{type1}/{type2}/{commodity}/{money}",method = RequestMethod.POST,produces = "application/json")
  @ApiOperation(value = "K线数据接口", httpMethod = "POST")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "type1", value = "类型1：实时：rt 1分钟：1m 5分钟：5m 15分钟：15m 30分钟：30m 60分钟：60m 日线：1d 周线：1w 月线：1mo", required = true, dataType = "string", paramType = "path"),
    @ApiImplicitParam(name = "type2", value = "类型2：历史：before 当前：now ", required = true, dataType = "string", paramType = "path"),
    @ApiImplicitParam(name = "commodity", value = "交易币，例如LTC、omg等", required = true, dataType = "string", paramType = "path"),
    @ApiImplicitParam(name = "money", value = "基币，例如BTC、ETH等", required = true, dataType = "string", paramType = "path")
  })
  public Callable<AjaxResponse> getKLineDiagramData(@PathVariable(name="type1") String type1,
                                          @PathVariable(name="type2") String type2,
                                          @PathVariable(name="commodity") String commodity,
                                          @PathVariable(name="money") String money){

    return () -> {
      if(type1.toUpperCase().equals("RT") == false && type1.toUpperCase().equals("5M") == false && type1.toUpperCase().equals("15M") == false
        && type1.toUpperCase().equals("30M") == false && type1.toUpperCase().equals("60M") == false && type1.toUpperCase().equals("1D") == false
        && type1.toUpperCase().equals("1W") == false && type1.toUpperCase().equals("1MO") == false && type1.toUpperCase().equals("1M") == false){
        return AjaxResponse.failure("type is wrong!");
      }
      if(type2.toUpperCase().equals("BEFORE") == false && type2.toUpperCase().equals("NOW") == false){
        return AjaxResponse.failure("type is wrong!");
      }
      try{
        StringBuilder stringBuilder = marketService.getJsonByFile(type1,commodity,money);
        if("BEFORE".equals(type2.toUpperCase())){
          stringBuilder.append("_PRE").append(".json");
        }else  if("NOW".equals(type2.toUpperCase())){
          stringBuilder.append(".json");
        }
        String json = marketService.requestJson(frontPath+stringBuilder.toString());
        if("BEFORE".equals(type2.toUpperCase())){
          List<HashMap> jsonArray = null;
          if(json.startsWith("[")){
            jsonArray =  JSONObject.parseArray(json,HashMap.class);
          }else{
            jsonArray = JSONObject.parseArray("["+json+"]",HashMap.class);
          }
          return AjaxResponse.success(jsonArray);
        }else  if("NOW".equals(type2.toUpperCase())){
          StringBuilder strbud = marketService.handleNowData(type1,json);
          logger.debug(strbud.toString());
          JSONObject jsonObject = JSONObject.parseObject(strbud.toString());
          return  AjaxResponse.success(jsonObject);
        }else{
          return AjaxResponse.failure("error");
        }
      }catch(Exception e){
        e.printStackTrace();
        logger.error(e.getMessage());
        return AjaxResponse.failure("error");
      }
    };


  }

  @RequestMapping(value = "/nologin/klineNew/{type1}/{type2}/{commodity}/{money}",method = RequestMethod.POST,produces = "application/json")
  @ApiOperation(value = "K线数据接口", httpMethod = "POST")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "type1", value = "类型1：实时：rt 1分钟：1m 5分钟：5m 15分钟：15m 30分钟：30m 60分钟：60m 日线：1d 周线：1w 月线：1mo", required = true, dataType = "string", paramType = "path"),
    @ApiImplicitParam(name = "type2", value = "类型2：历史：before 当前：now ", required = true, dataType = "string", paramType = "path"),
    @ApiImplicitParam(name = "commodity", value = "交易币，例如LTC、omg等", required = true, dataType = "string", paramType = "path"),
    @ApiImplicitParam(name = "money", value = "基币，例如BTC、ETH等", required = true, dataType = "string", paramType = "path")
  })
  public Callable<AjaxResponse> getKLineDiagramDataNew(@PathVariable(name="type1") String type1,
                                                    @PathVariable(name="type2") String type2,
                                                    @PathVariable(name="commodity") String commodity,
                                                    @PathVariable(name="money") String money) {

    return () -> {
      if(type1.toUpperCase().equals("RT") == false && type1.toUpperCase().equals("5M") == false && type1.toUpperCase().equals("15M") == false
        && type1.toUpperCase().equals("30M") == false && type1.toUpperCase().equals("60M") == false && type1.toUpperCase().equals("1D") == false
        && type1.toUpperCase().equals("1W") == false && type1.toUpperCase().equals("1MO") == false && type1.toUpperCase().equals("1M") == false){
        return AjaxResponse.failure("type is wrong!");
      }
      if(type2.toUpperCase().equals("BEFORE") == false && type2.toUpperCase().equals("NOW") == false){
        return AjaxResponse.failure("type is wrong!");
      }

      try {
        String json = marketService.requestJsonByRedis(type1, type2, commodity, money);
        if("BEFORE".equals(type2.toUpperCase())){
          List<HashMap> jsonArray = null;
          if(json.startsWith("[")){
            jsonArray =  JSONObject.parseArray(json,HashMap.class);
          }else{
            jsonArray = JSONObject.parseArray("["+json+"]",HashMap.class);
          }
          return AjaxResponse.success(jsonArray);
        }else  if("NOW".equals(type2.toUpperCase())){
          JSONObject jsonObject = JSONObject.parseObject(json);
          return  AjaxResponse.success(jsonObject);
        }else{
          return AjaxResponse.failure("error");
        }
      }catch ( Exception e ){
        e.printStackTrace();
        return AjaxResponse.failure("error");
      }
    };
  }

}
