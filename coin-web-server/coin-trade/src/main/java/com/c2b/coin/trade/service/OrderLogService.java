package com.c2b.coin.trade.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import tk.mybatis.mapper.entity.Example;

import com.alibaba.fastjson.JSONObject;
import com.c2b.coin.account.api.AccountClient;
import com.c2b.coin.common.AssetChangeConstant;
import com.c2b.coin.common.DateUtil;
import com.c2b.coin.common.MarketingActivityType;
import com.c2b.coin.common.enumeration.ConsignationTradeTypeEnum;
import com.c2b.coin.common.enumeration.ErrorMsgEnum;
import com.c2b.coin.trade.annotation.MarketingActivity;
import com.c2b.coin.trade.entity.ConsignationLog;
import com.c2b.coin.trade.entity.DigitalCoin;
import com.c2b.coin.trade.entity.OrderLog;
import com.c2b.coin.trade.entity.TradePairInfo;
import com.c2b.coin.trade.exceptions.TradeException;
import com.c2b.coin.trade.mapper.ConsignationLogMapper;
import com.c2b.coin.trade.mapper.OrderLogMapper;
import com.c2b.coin.trade.vo.ExchangeVO;
import com.c2b.coin.trade.vo.MatchInfoVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * @author Anne
 * @date 2017.10.19
 */
@Service
public class OrderLogService {
  @Autowired
  OrderLogMapper orderLogMapper;

  @Autowired
  AccountClient accountClient;

  @Autowired
  ConsignationLogMapper consignationLogMapper;
  @Autowired
  ConsignationService consignationService;
  @Autowired
  TradePairInfoService tradePairInfoService;
  @Autowired
  DigitalCoinService digitalCoinService;
  private static final Logger logger = LoggerFactory.getLogger(OrderLogService.class);
  private static final String MATCH_PLATFORM_BUY = "buy";
  private static final int TWENTYFOURHOURTIMESTAMP = 24 * 60 * 60 * 1000;

  public List<Map<String, Object>> listOrderLogForTwentyFour(String userId){
    long endTimeStamp = DateUtil.dateToUnixTimestamp();
    long startTimeStamp = endTimeStamp-TWENTYFOURHOURTIMESTAMP;
    return convertList(orderLogMapper.listOrderLogForTwentyFour(Integer.valueOf(userId), startTimeStamp, endTimeStamp));
  }

  public PageInfo orderListRealTime(String bizType,int pageNo, int pageSize) {
    PageHelper.startPage(pageNo, pageSize);
    List<Map<String, Object>> list = orderLogMapper.listOrderLog(bizType);
    PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(list);
    if(pageInfo.getTotal()>100){
      pageInfo.setTotal(100);
      int pages = 100/pageSize;
      pageInfo.setPages(pages);
    }
    return pageInfo;
  }

  private List<Map<String, Object>> convertList(List<Map<String, Object>> list){
    BigDecimal consignationPrice = new BigDecimal("0");
    BigDecimal consignationCount = new BigDecimal("0");
    String tradeType = "";
    if(!list.isEmpty()){
      for(Map<String, Object> map : list){
        consignationPrice = (BigDecimal)map.get("consignation_price");
        consignationCount = (BigDecimal)map.get("consignation_count");
        tradeType = (String) map.get("trade_type");
        map.put("unTradeCount", map.get("after_consignation_count"));
        BigDecimal consignationTotalMoney = getConsignationTotalMoney(consignationPrice, consignationCount, tradeType);
        map.put("consignationTotalMoney", consignationTotalMoney.toPlainString());
      }
    }
    return list;
  }

  public PageInfo listOrder(String userId, String bizType, String pageNo, String pageSize) {
    if ("0".equals(bizType)){
      bizType = null;
    }else {
      TradePairInfo tradePairInfo = tradePairInfoService.getTradePairInfo(bizType);
      bizType = tradePairInfo.getCommodityCoinName().concat("/").concat(tradePairInfo.getMoneyCoinName());
    }

    PageHelper.startPage(Integer.parseInt(pageNo), Integer.parseInt(pageSize), true, false);
    List<Map<String, Object>> list = orderLogMapper.listOrder(Long.valueOf(userId), bizType);
//    BigDecimal consignationPrice = new BigDecimal("0");
//    BigDecimal consignationCount = new BigDecimal("0");
//    String tradeType = "";
//    if(!list.isEmpty()){
//      for(Map<String, Object> map : list){
//        consignationPrice = (BigDecimal)map.get("consignation_price");
//        consignationCount = (BigDecimal)map.get("consignation_count");
//        tradeType = (String) map.get("trade_type");
//        BigDecimal consignationTotalMoney = getConsignationTotalMoney(consignationPrice, consignationCount, tradeType);
//        map.put("consignationTotalMoney", consignationTotalMoney.toPlainString());
//      }
//    }
    PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(list);
    return pageInfo;
  }

  public BigDecimal getConsignationTotalMoney(BigDecimal consignationPrice, BigDecimal consignationCount, String tradeType){
    BigDecimal consignationTotalMoney = new BigDecimal("0");
    ConsignationTradeTypeEnum consignationTradeTypeEnum = ConsignationTradeTypeEnum.getConsignationTradeTypeEnum(tradeType);
    switch (consignationTradeTypeEnum) {
      case MARKET_PRICE_BUY:
        consignationTotalMoney = consignationCount;
        return consignationTotalMoney;
      case MARKET_PRICE_SELL:
        consignationTotalMoney = consignationCount;
        return consignationTotalMoney;
      case LIMIT_PRICE_BUY:
      case LIMIT_PRICE_SELL:
        consignationTotalMoney = consignationPrice.multiply(consignationCount);
        return consignationTotalMoney;
      default:
        return consignationTotalMoney;
    }
  }

  private void checkOrderLog(List<String> consignationLogNos, String orderNo) throws TradeException {
    try {
      Example example = new Example(OrderLog.class);
      example.createCriteria().andIn("consignationNo",consignationLogNos).andEqualTo("orderNo", orderNo);
      List<OrderLog> orders = orderLogMapper.selectByExample(example);
      if (null != orders && orders.size() != 0) {
        throw new TradeException(ErrorMsgEnum.MATCH_FAIL);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 处理买家订单
   *
   * @param matchInfoVO
   * @param buyConsignationLog
   * @throws TradeException
   */
  @Transactional(rollbackFor = Exception.class)
  public void buyOrderLogDeal(MatchInfoVO matchInfoVO, ConsignationLog buyConsignationLog) throws TradeException {
    OrderLog buyOrderLog = initOrderLog(matchInfoVO, buyConsignationLog);
    buyOrderLog.setPoundage(matchInfoVO.getBuyMoney());
    buyOrderLog.setUserId(buyConsignationLog.getUserId().intValue());
    buyOrderLog.setMadeAveragePrice(buyConsignationLog.getMadeAveragePrice());
    buyOrderLog.setMadePrice(matchInfoVO.getMoney());
    buyOrderLog.setOrderType(Integer.valueOf(ConsignationTradeTypeEnum.getConsignationTradeTypeEnum("BUY_" + matchInfoVO.getBuyGenre().toUpperCase()).getStatusCode()));
    orderLogMapper.insert(buyOrderLog);
    updateConsignation(buyConsignationLog, matchInfoVO);

  }

  /**
   * 处理卖家订单
   *
   * @param matchInfoVO
   * @param sellConsignationLog
   * @throws TradeException
   */
  @Transactional(rollbackFor = Exception.class)
  public void sellOrderLogDeal(MatchInfoVO matchInfoVO, ConsignationLog sellConsignationLog) throws TradeException {
    OrderLog sellOrderLog = initOrderLog(matchInfoVO, sellConsignationLog);
    sellOrderLog.setPoundage(matchInfoVO.getSellMoney());
    sellOrderLog.setOrderType(Integer.valueOf(ConsignationTradeTypeEnum.getConsignationTradeTypeEnum("SELL_" + matchInfoVO.getSellGenre().toUpperCase()).getStatusCode()));
    sellOrderLog.setUserId(sellConsignationLog.getUserId().intValue());
    sellOrderLog.setMadePrice(matchInfoVO.getMoney());
    sellOrderLog.setMadeAveragePrice(sellConsignationLog.getMadeAveragePrice());
    orderLogMapper.insert(sellOrderLog);
    updateConsignation(sellConsignationLog, matchInfoVO);
  }


  @MarketingActivity(value ={MarketingActivityType.ALL_TRADE,MarketingActivityType.FIREST_RECHARGE_TRADE} )
  @Transactional(rollbackFor = Exception.class)
  public void dealTradeSuccessData(MatchInfoVO matchInfoVO) throws TradeException {
    ConsignationLog buyConsignationLog = consignationService.findBySeq(matchInfoVO.getBuySeq());
    ConsignationLog sellConsignationLog = consignationService.findBySeq(matchInfoVO.getSellSeq());
    if (null == buyConsignationLog || null == sellConsignationLog) {
      throw new TradeException(ErrorMsgEnum.MATCH_FAIL);
    }
    List<String> consignationNos = new ArrayList<>();
    consignationNos.add(buyConsignationLog.getConsignationNo());
    consignationNos.add(sellConsignationLog.getConsignationNo());
    checkOrderLog(consignationNos, matchInfoVO.getSeq());
    buyOrderLogDeal(matchInfoVO, buyConsignationLog);
    matchInfoVO.setSellGenre(matchInfoVO.getBuyGenre());
    sellOrderLogDeal(matchInfoVO, sellConsignationLog);
    DigitalCoin commodityDigitalCoin = digitalCoinService.findByName(matchInfoVO.getCurrency().split("/")[0]);
    DigitalCoin moneyDigitalCoin = digitalCoinService.findByName(matchInfoVO.getCurrency().split("/")[1]);
    if (commodityDigitalCoin == null || moneyDigitalCoin == null) {
      throw new TradeException(ErrorMsgEnum.MATCH_FAIL);
    }
    String changeJsonString = accountClient.tradePairAssetChange(buyConsignationLog.getUserId(),
      buyConsignationLog.getUsername(), sellConsignationLog.getUserId(), sellConsignationLog.getUsername(),
      matchInfoVO.getSeq(), moneyDigitalCoin.getId(), commodityDigitalCoin.getId(), matchInfoVO.getMoney().multiply(matchInfoVO.getCount()),
      matchInfoVO.getCount(), matchInfoVO.getSellMoney(), matchInfoVO.getBuyMoney());
    logger.info("seq="+matchInfoVO.getSeq()+",changeJsonString="+changeJsonString);
    JSONObject jsonObject = (JSONObject) JSONObject.parse(changeJsonString);
    if (!jsonObject.getBooleanValue("success")) {
      throw new TradeException(ErrorMsgEnum.MATCH_FAIL);
    }
  }


  public void endMatch(ExchangeVO vo) throws TradeException {
    String seq = vo.getSeq();//委托单号
    DigitalCoin commodityDigitalCoin = digitalCoinService.findByName(vo.getCurrency().split("/")[0]);
    DigitalCoin moneyDigitalCoin = digitalCoinService.findByName(vo.getCurrency().split("/")[1]);
    //根据ExchangeVo里面的type区分，如果是buy就是买，如果是sell就是卖。
    ConsignationLog consignationLog = consignationService.findBySeq(seq);
    if (commodityDigitalCoin == null || moneyDigitalCoin == null || null == consignationLog) {
      throw new TradeException(ErrorMsgEnum.MATCH_FAIL);
    }
    int currencyType = 0;
    if (vo.getType().equalsIgnoreCase(MATCH_PLATFORM_BUY)) {
      currencyType = moneyDigitalCoin.getId().intValue();
    } else {
      currencyType = commodityDigitalCoin.getId().intValue();
    }
    BigDecimal unFreezeAsset = getUnFreezeAsset(consignationLog, vo);
    if (unFreezeAsset.compareTo(BigDecimal.ZERO) != 0) {
      accountClient.assetChange(consignationLog.getUserId(), consignationLog.getUsername(), seq, AssetChangeConstant.UNFREEZE, currencyType, unFreezeAsset);
    }
    updateConsignationStatus(consignationLog, vo);
  }

  public BigDecimal getUnFreezeAsset(ConsignationLog consignationLog, ExchangeVO vo) {
    if (consignationLog.getTradeType().equals(ConsignationTradeTypeEnum.LIMIT_PRICE_BUY.getStatusCode())){
      return consignationLog.getConsignationPrice().multiply(consignationLog.getConsignationCount()).subtract(vo.getMakeMoney());
    }else if(consignationLog.getTradeType().equals(ConsignationTradeTypeEnum.MARKET_PRICE_BUY.getStatusCode())){
      return consignationLog.getConsignationCount().subtract(vo.getMakeMoney());
    }else{
      return consignationLog.getConsignationCount().subtract(vo.getMakeCount());
    }
  }

  private OrderLog initOrderLog(MatchInfoVO matchInfoVO, ConsignationLog consignationLog) {
    OrderLog orderLog = new OrderLog();
    orderLog.setOrderNo(matchInfoVO.getSeq());
    orderLog.setMadePrice(matchInfoVO.getChanger());
    orderLog.setMadeCount(matchInfoVO.getCount());
    orderLog.setConsignationNo(consignationLog.getConsignationNo());
    orderLog.setMadeTime(DateUtil.getCurrentTimestamp());
    orderLog.setBizType(matchInfoVO.getCurrency());
    orderLog.setTradeType(consignationLog.getTradeType());
    orderLog.setConsignationCount(consignationLog.getConsignationCount());
    orderLog.setConsignationPrice(consignationLog.getConsignationPrice());
    orderLog.setConsignationTime(consignationLog.getCreateTime());
    if (!consignationLog.getTradeType().equals(ConsignationTradeTypeEnum.MARKET_PRICE_BUY.getStatusCode())){
      //成交前委托量=总委托量-原已成交量
      orderLog.setPreConsignationCount(consignationLog.getConsignationCount().subtract(consignationLog.getMadeCount()));
      //成交后委托量=总委托量-原已成交量-本次已成交量
      orderLog.setAfterConsignationCount(consignationLog.getConsignationCount().subtract(consignationLog.getMadeCount()).subtract(matchInfoVO.getCount()));
    }else{
      //成交前委托量=总委托量-原已成交量
      orderLog.setPreConsignationCount(consignationLog.getConsignationCount().subtract(consignationLog.getMadeCount()));
      //成交后委托量=总委托量-原已成交量-本次已成交量
      orderLog.setAfterConsignationCount(consignationLog.getConsignationCount().subtract(consignationLog.getMadeCount()).subtract(matchInfoVO.getMoney()));
    }

    orderLog.setMadeAveragePrice(matchInfoVO.getChanger());
    return orderLog;
  }

  public void updateConsignation(ConsignationLog consignationLog, MatchInfoVO matchInfoVO) throws TradeException {
    /**
     * 要更新的字段有：已成交量、已成交总金额、最后成交时间、修改时间(除成交均价、委托状态之外)
     */
    consignationLog.setMadeCount(matchInfoVO.getCount());
    consignationLog.setMadePrice(matchInfoVO.getMoney());
    consignationLog.setMadeTime(matchInfoVO.getPairDate().getTime());
    consignationLog.setUpdateTime(DateUtil.getCurrentTimestamp());
    consignationLogMapper.matchAfterUpdateConsignation(consignationLog);
  }


  public void updateConsignationStatus(ConsignationLog consignationLog, ExchangeVO vo) throws TradeException {
    Integer consignationStatus = getSetConsignationStatusValue(vo, consignationLog);
    logger.info("consignationStatus = " + consignationStatus);
    consignationLog.setUpdateTime(DateUtil.getCurrentTimestamp());
    consignationLog.setMadeAveragePrice(vo.getAverageMoney());
    //市价未成交，设置dataStatus为1，表示已删除
    if(consignationStatus == -1){
      consignationLog.setDataStatus(1);
      consignationLogMapper.updateMarketConsignationWithEnd(consignationLog);
    }else{
      consignationLog.setConsignationStatus(consignationStatus);
      consignationLogMapper.updateConsignationWithEnd(consignationLog);

      //修改order_log madeAveragePrice 值
      OrderLog ol = new OrderLog();
      ol.setConsignationNo(consignationLog.getConsignationNo()); //修改订单号相同的 madeAveragePrice 值
      OrderLog obj = orderLogMapper.selectOne(ol);
      String orderNo = obj.getOrderNo();
      orderLogMapper.updateByOrderNo(consignationLog.getMadeAveragePrice(),orderNo);
    }

  }

  public Integer getSetConsignationStatusValue(ExchangeVO vo, ConsignationLog consignationLog) {
    BigDecimal consignationCount = consignationLog.getConsignationCount();
    if (consignationLog.getTradeType().equals(ConsignationTradeTypeEnum.MARKET_PRICE_BUY.getStatusCode())) {
      return getMarketStatus(vo.getMakeMoney(), consignationCount);
    }else if(consignationLog.getTradeType().equals(ConsignationTradeTypeEnum.MARKET_PRICE_SELL.getStatusCode())){
      return getMarketStatus(vo.getMakeCount(), consignationCount);
    }
    return getEndStatus(vo.getMakeCount(), consignationCount);
  }

  private Integer getEndStatus(BigDecimal matchNum, BigDecimal platformNum) {
    if (matchNum.subtract(platformNum).compareTo(BigDecimal.ZERO) == 0) {
      return 4;
    } else if (matchNum.subtract(platformNum).compareTo(BigDecimal.ZERO) == -1) {
      return 3;
    } else {
      return 2;
    }
  }

  private Integer getMarketStatus(BigDecimal matchNum, BigDecimal platformNum){
    if(matchNum.compareTo(BigDecimal.ZERO) == 0){
      return -1;
    }
    return 4;
  }
}
