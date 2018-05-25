package com.c2b.coin.trade.rest;

import com.alibaba.fastjson.JSONObject;
import com.c2b.coin.common.AjaxResponse;
import com.c2b.coin.common.MarketingActivityType;
import com.c2b.coin.common.MarketingRandomCoinNumber;
import com.c2b.coin.common.enumeration.ErrorMsgEnum;
import com.c2b.coin.trade.mapper.MarketingActivityPlanMapper;
import com.c2b.coin.web.common.BaseRest;
import com.coin.config.cache.redis.RedisUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ROUND_HALF_DOWN;

@RestController
public class MarketingActivityApi extends BaseRest {

  @Autowired
  RedisUtil redisUtil;

  @Autowired
  private MarketingActivityPlanMapper marketingActivityPlanMapper ;

  @Value("${activity.free.BTC}")
  private String BTCNum;

  private static final Integer COIN_EVERYDAY_NUM = 3;

  @ApiOperation(value = "运营活动赠送虚拟货币的数量", httpMethod = "POST")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "coinName", value = "虚拟货币名称，例如BTC、ETH等", required = true, dataType = "string", paramType = "query")
  })
  @RequestMapping(value = "/nologin/activity/free/coin/",method = RequestMethod.POST,produces = "application/json")
  public AjaxResponse getMarketingActivityCoin(@RequestParam String coinName, HttpServletRequest request) {
    if (StringUtils.isEmpty(coinName) || !"ETH".equals(coinName.toUpperCase())
      || !"BTC".equals(coinName.toUpperCase())) {
      writeObj(ErrorMsgEnum.PARAM_ERROR);
    }
    BigDecimal money = null;

    String key = "FREE_COIN_NUM_" + coinName.toUpperCase();
    String id = "";

    if("BTC".equals(coinName.toUpperCase()) && StringUtils.isEmpty(getUserId()) == false){
      return writeObj(ErrorMsgEnum.USER_EXITS);
    }
    if ("ETH".equals(coinName.toUpperCase()) && StringUtils.isEmpty(getUserId())) {
      return writeObj(ErrorMsgEnum.USER_NOT_EXIST);
    }
    if ("BTC".equals(coinName.toUpperCase())) {
      money = MarketingRandomCoinNumber.generateBTCNumber();
      id = request.getSession().getId();
      logger.debug("sessionId = " + key);
    }
    if ("ETH".equals(coinName.toUpperCase())) {
      money = MarketingRandomCoinNumber.generateETHNumber();
      id = getUserId();
      logger.debug("key = " + key);
    }

    redisUtil.hset(key,id,money.toString());
    HashMap<String,Object> map = new HashMap<>();
    map.put(coinName.toUpperCase()+"_NUM",money);
    String json = JSONObject.toJSONString(map);
    JSONObject jsonObject = JSONObject.parseObject(json);
    return writeObj(jsonObject);
  }


  @ApiOperation(value = "得到未登录用户已获取的比特币数量", httpMethod = "POST")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "phone", value = "手机号码", required = true, dataType = "string", paramType = "query")
  })
  @RequestMapping(value = "/nologin/activity/given/btc", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public AjaxResponse getFreeBTC(String phone){
    if (StringUtils.isEmpty(phone)) writeObj(ErrorMsgEnum.PARAM_ERROR);
    String coinNumStr =  "";
    List<Map<String,Object>> list = this.marketingActivityPlanMapper.getGivenCoinByPhone(phone, MarketingActivityType.REGISTER);
    if(list != null && list.size()>0){
      return writeObj(list.get(0).get("asset"));
    }else{
      return writeObj(ErrorMsgEnum.USER_NOT_FOUND);
    }
  }


  @ApiOperation(value = "运营活动赠送BTC货币的剩余数量", httpMethod = "POST")
  @RequestMapping(value = "/nologin/activity/free/coin/left",method = RequestMethod.POST,produces = "application/json")
  public AjaxResponse getMarketingActivityCoin() {
    Date beginDate = null;
    DecimalFormat df=new DecimalFormat("0.000000");
    List<Map<String, Object>> marketingActivity = this.marketingActivityPlanMapper.findEffectivePlan();
    if (marketingActivity != null && marketingActivity.size() > 0) {
      List<Map<String, Object>> list = marketingActivity.stream().filter(map -> new Long(1).equals(map.get("id"))).collect(Collectors.toList());
      System.out.println(list.get(0).toString());
      beginDate = (Date) list.get(0).get("start_time");
    }
    BigDecimal beginTime = new BigDecimal(beginDate.getTime()/1000);
    BigDecimal currentTime = new BigDecimal(System.currentTimeMillis()/1000);
    if(beginTime.compareTo(currentTime)>0){
      return writeObj(df.format(new BigDecimal(new Integer(BTCNum).intValue()).setScale(6,BigDecimal.ROUND_HALF_DOWN).doubleValue()));
    }
    BigDecimal timeDiff = currentTime.subtract(beginTime).divide(new BigDecimal(60),6,ROUND_HALF_DOWN);
    BigDecimal minuteDay = new BigDecimal(24).multiply(new BigDecimal(60));
    BigDecimal lostCoins = new BigDecimal(COIN_EVERYDAY_NUM.intValue()).divide(minuteDay,6,ROUND_HALF_DOWN).multiply(timeDiff);

    logger.debug("begin:" + beginTime + " currentTime: "+ currentTime);
    BigDecimal coinNum = getCoinNumBigDecimal(lostCoins, new BigDecimal(new Integer(BTCNum).intValue()));

    return writeObj(df.format(coinNum.setScale(6,BigDecimal.ROUND_HALF_DOWN).doubleValue()));

  }

  private BigDecimal getCoinNumBigDecimal(BigDecimal lostCoins ,BigDecimal allBTCNum) {
    BigDecimal coinNum = allBTCNum.subtract(lostCoins);
    while (coinNum.compareTo(BigDecimal.ZERO) < 0 ){
      coinNum = getCoinNumBigDecimal(lostCoins,allBTCNum.add(new BigDecimal(new Integer(BTCNum).intValue())));
    }
    return coinNum;
  }

}
