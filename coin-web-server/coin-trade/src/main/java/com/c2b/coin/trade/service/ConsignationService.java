package com.c2b.coin.trade.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.c2b.coin.account.api.AccountClient;
import com.c2b.coin.common.AjaxResponse;
import com.c2b.coin.common.AssetChangeConstant;
import com.c2b.coin.common.DateUtil;
import com.c2b.coin.common.enumeration.ConsignationStatusEnum;
import com.c2b.coin.common.enumeration.ConsignationTradeTypeEnum;
import com.c2b.coin.common.enumeration.ErrorMsgEnum;
import com.c2b.coin.trade.client.RestClient;
import com.c2b.coin.trade.entity.ConsignationLog;
import com.c2b.coin.trade.entity.TradePairInfo;
import com.c2b.coin.trade.exceptions.IllegalParamException;
import com.c2b.coin.trade.exceptions.TradeException;
import com.c2b.coin.trade.mapper.ConsignationLogMapper;
import com.c2b.coin.trade.vo.ExchangeVO;
import com.c2b.coin.trade.vo.ResultCallbackVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;

/**
 * @author Anne
 * @date 2017.10.19
 */
@Service
public class ConsignationService {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  ConsignationLogMapper consignationLogMapper;

  @Autowired
  TradePairInfoService tradePairInfoService;

  @Autowired
  TradeMqProduce tradeMqProduce;

  @Autowired
  protected MessageSource messageSource;

  @Autowired
  AccountClient accountClient;

  @Autowired
  RestClient restCient;

  @Value("${coin.seq.url}")
  private String seqUrl;

  @Value("${coin.storm.ip}")
  private String revokeIP;

  @Value("${coin.storm.port}")
  private int revokePort;

  @Autowired
  OrderLogService orderLogService;

  /**
   * 正则表达式：验证金额,最多保留4位小数
   */
  public static final String REGEX_MONEY4 = "^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,4})?$";

  /**
   * 正则表达式：验证金额,最多保留4位小数
   */
  public static final String REGEX_MONEY6 = "^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,6})?$";

  /**
   * 下委托单
   *
   * @param userId
   *          用户ID
   * @param userName
   *          用户名
   * @param bizType
   *          交易对主键
   * @param tradeType
   *          交易类型
   * @param consignationPrice
   *          委托价格
   * @param consignationCount
   *          委托数量
   * @return 下单结果
   */
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public AjaxResponse palceConsignationOrder(Long userId, String userName,
      String bizType, String tradeType, BigDecimal consignationPrice,
      BigDecimal consignationCount, String type) throws TradeException {
    // 验证订单交易对参数信息并返回需要进行增加或者扣减的币种ID
    int currencyType = checkOrderParams(bizType, tradeType);
    logger.info("currencyType=" + currencyType);
    // 默认是卖家的交易量
    BigDecimal amount = consignationCount;
    // 如果不是卖家，再根据交易类型变更交易量数据
    if (tradeType.trim().equals(
        ConsignationTradeTypeEnum.LIMIT_PRICE_BUY.getStatusCode())) {
      if (null != consignationPrice
          && consignationPrice.compareTo(BigDecimal.ZERO) > 0) {
        amount = consignationPrice.multiply(consignationCount);
      }
    }
    String orderNo = getOrderNo();
    // 更新用户资产以及插入资产变更记录接口
    invokeUserAssetChange(userId, userName, orderNo, currencyType, amount);
    // 下单
    doPlaceConsignationOrder(userId, userName, bizType, tradeType,
        consignationPrice, consignationCount, orderNo);
    // 发送mq
    String genre = getTradeType(tradeType);// 默认是限价交易
    String currency = getCurrency(bizType);
    tradeMqProduce.sendMessage(userId.toString(), consignationCount,
        consignationPrice, genre, currency, type, orderNo);
    return writeObj(null);

  }

  /**
   * 获取交易类型
   *
   * @param tradeType
   * @return
   */
  public String getTradeType(String tradeType) {
    ConsignationTradeTypeEnum consignationTradeTypeEnum = ConsignationTradeTypeEnum
        .getConsignationTradeTypeEnum(tradeType);
    switch (consignationTradeTypeEnum) {
    case MARKET_PRICE_BUY:
    case MARKET_PRICE_SELL:
      return "MarketTransactions";
    default:
      return "PriceDeal";
    }
  }

  /**
   * 获取交易对信息
   *
   * @param bizType
   * @return
   */
  public String getCurrency(String bizType) {
    TradePairInfo tradePairInfo = tradePairInfoService
        .getTradePairInfo(bizType);
    return tradePairInfo.getCommodityCoinName() + "/"
        + tradePairInfo.getMoneyCoinName();
  }

  /**
   * 根据交易对主键验证交易对信息数据
   *
   * @param bizType
   *          交易对ID
   * @param tradeType
   *          交易类型
   * @return 成功：币种ID，失败：失败信息
   */
  public int checkOrderParams(String bizType, String tradeType)
      throws TradeException {
    TradePairInfo tradePairInfo = tradePairInfoService
        .getTradePairInfo(bizType);
    if (tradePairInfo == null) {
      logger.error("根据交易对主键：" + bizType + "未查到交易对信息！");
      throw new TradeException(ErrorMsgEnum.TRADE_PAIR_NOT_FOUND);
    }
    // 对于限价来说，默认是买，下面如果是限价卖，再改为commodityCoin；
    return getCurrencyTypeValue(tradePairInfo, tradeType);
  }

  /**
   *
   * @param tradePairInfo
   * @param tradeType
   * @return
   */
  private int getCurrencyTypeValue(TradePairInfo tradePairInfo, String tradeType) {
    ConsignationTradeTypeEnum consignationTradeTypeEnum = ConsignationTradeTypeEnum
        .getConsignationTradeTypeEnum(tradeType);
    switch (consignationTradeTypeEnum) {
    case LIMIT_PRICE_SELL:
    case MARKET_PRICE_SELL:
      return tradePairInfo.getCommodityCoin().intValue();
    default:
      return tradePairInfo.getMoneyCoin().intValue();
    }
  }

  /**
   * @param userId
   *          用户ID
   * @param userName
   *          用户名
   * @param bizType
   *          交易对ID
   * @param tradeType
   *          交易类型
   * @param consignationPrice
   *          委托价
   * @param consignationCount
   *          委托量
   * @return 成功：委托单对象；失败：失败信息
   */
  @Transactional(rollbackFor = Exception.class)
  public ConsignationLog doPlaceConsignationOrder(Long userId, String userName,
      String bizType, String tradeType, BigDecimal consignationPrice,
      BigDecimal consignationCount, String orderNo) throws TradeException {
    ConsignationLog consignationLog = new ConsignationLog();
    consignationLog.setBizType(bizType);
    consignationLog.setTradeType(tradeType);
    ConsignationTradeTypeEnum consignationTradeTypeEnum = ConsignationTradeTypeEnum
        .getConsignationTradeTypeEnum(tradeType);
    switch (consignationTradeTypeEnum) {
    case MARKET_PRICE_BUY:
      consignationLog.setConsignationPrice(consignationCount);
    case MARKET_PRICE_SELL:
      consignationLog.setConsignationCount(consignationCount);
    default:
      consignationLog.setConsignationPrice(consignationPrice);
      consignationLog.setConsignationCount(consignationCount);
    }
    consignationLog.setUserId(userId);
    consignationLog.setUsername(userName);
    consignationLog.setConsignationNo(orderNo);
    if (!addConsignation(consignationLog)) {
      logger.error("执行下委托单持久化失败！");
      throw new TradeException(ErrorMsgEnum.CONSIGN_SAVE_FAIL);
    }
    return consignationLog;
  }

  public String getOrderNo() throws TradeException {
    String consignationNoJSON = restCient.get(seqUrl, null, String.class);
    JSONObject jsonObject = JSONObject.parseObject(consignationNoJSON);
    if (jsonObject == null || !jsonObject.containsKey("id")) {
      logger.error("生成单号调用结果为空，下委托单失败！");
      throw new TradeException(ErrorMsgEnum.GENERATE_ORDERNO_FAIL);
    }
    String consignationNo = (String) jsonObject.get("id");
    return consignationNo;
  }

  /**
   * @param userId
   *          用户ID
   * @param userName
   *          用户名 委托量
   * @param orderNo
   *          订单号
   * @param currencyType
   *          币种ID
   * @param amount
   *          交易额
   * @return 返回调用更新用户资产以及插入资产变更记录接口结果
   */
  public AjaxResponse invokeUserAssetChange(Long userId, String userName,
      String orderNo, int currencyType, BigDecimal amount)
      throws TradeException {
    String fronzeJsonString = accountClient.assetChange(userId, userName,
        orderNo, AssetChangeConstant.FREEZE, currencyType, amount);
    logger.info("fronzeJsonString=" + fronzeJsonString);
    JSONObject fzJsonObject = JSONObject.parseObject(fronzeJsonString);
    if (fzJsonObject == null || !fzJsonObject.getBooleanValue("success")) {
      logger.error("更新用户资产以及插入资产变更记录接口调用结果返回值为空，下委托单失败！");
      throw new TradeException(ErrorMsgEnum.FROZEN_ASSET_FAIL);
    }
    return writeObj(null);
  }

  /**
   * @param consignationLog
   *          委托单数据
   * @return 添加结果：true-成功；false-失败
   */
  @Transactional(rollbackFor = Exception.class)
  public boolean addConsignation(ConsignationLog consignationLog) {
    consignationLog.setDataStatus(0);
    consignationLog.setMadeCount(BigDecimal.ZERO);
    consignationLog.setMadePrice(BigDecimal.ZERO);
    long time = DateUtil.getCurrentTimestamp();
    consignationLog.setConsignationStatus(ConsignationStatusEnum.CONSIGNING
        .getStatusCode());
    consignationLog.setCreateTime(time);
    consignationLog.setMadeTime(time);
    consignationLog.setUpdateTime(time);
    consignationLog.setMadeAveragePrice(BigDecimal.ZERO);
    if (consignationLogMapper.insert(consignationLog) > 0) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * 根据用户ID查询委托单信息列表
   *
   * @param userId
   *          用户ID
   * @return 委托单信息列表
   */
  public PageInfo listConsignationOrderByUserId(String userId,int consignationStatus, int bizType, int pageNo, int pageSize) {
    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    if (0 == consignationStatus) {// 查询所有
      PageHelper.startPage(pageNo, pageSize);
      list = consignationLogMapper.listConsignationOrderByUserId(userId,
          bizType);
    } else {// 根据状态查询
      PageHelper.startPage(pageNo, pageSize);
      list = consignationLogMapper.listConsignationOrderByUserIdAndStatus(
          userId, consignationStatus, bizType);
    }
    String consignationPrice = "0";
    String consignationCount = "0";
    String madeCount = "0";
    Integer tradeType = 0;
    if (list != null && list.size() > 0 && !list.isEmpty()) {
      for (Map<String, Object> map : list) {
        String biz_type = String.valueOf(map.get("biz_type"));
        map.put("biz_type", getCurrency(biz_type));
        consignationPrice = ((String) map.get("consignation_price") == null) ? "0"
            : (String) map.get("consignation_price");
        consignationCount = (String) map.get("consignation_count");
        tradeType = (Integer) map.get("trade_type");
        madeCount = (String) map.get("made_count");
        map.put(
            "unTradeCount",
            new BigDecimal(consignationCount).subtract(
                new BigDecimal(madeCount)).toPlainString());
        BigDecimal consignationTotalMoney = orderLogService
            .getConsignationTotalMoney(new BigDecimal(consignationPrice),
                new BigDecimal(consignationCount),
                String.valueOf(tradeType.intValue()));
        map.put("consignationTotalMoney",
            consignationTotalMoney.toPlainString());
      }
    }
    PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(list);
    return pageInfo;
  }

  public List<Map<String, String>> listUnTradeOrder(String userId) {
    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    list = consignationLogMapper.listUnTradeOrder(Long.parseLong(userId));
    if (list != null && list.size() > 0 && !list.isEmpty()) {
      for (Map<String, String> map : list) {
        String biz_type = String.valueOf(map.get("biz_type"));
        long timestampValue = Long.parseLong(String.valueOf(map
            .get("create_time")));
        BigDecimal unTradeCount = new BigDecimal(map.get("consignation_count"))
            .subtract(new BigDecimal(map.get("made_count")));
        map.put("biz_type", getCurrency(biz_type));
        map.put("create_time", DateUtil.unixTimestampToDate(timestampValue));
        map.put("unTradeCount", unTradeCount.toPlainString());
        BigDecimal consignationTotalMoney = orderLogService
            .getConsignationTotalMoney(
                new BigDecimal(map.get("consignation_price")), new BigDecimal(
                    map.get("consignation_count")), String.valueOf(map
                    .get("trade_type")));
        map.put("consignationTotalMoney",
            consignationTotalMoney.toPlainString());
      }
    }
    return list;
  }

  /**
   * 调用撮合交易的撤单验证接口
   *
   * @param exchangeVO
   *          参数VO
   * @return ResultCallbackVo 撤单结果VO
   */
  public ResultCallbackVO invokeRevokeOrder(String consignationNo) {
    ExchangeVO exchangeVO = new ExchangeVO();
    exchangeVO.setSeq(consignationNo);
    String execute = null;
    try {
      logger.info("revokeIP:" + revokeIP + ",revokePort=" + revokePort);
//      @SuppressWarnings("resource")
//      DRPCClient client = new DRPCClient(Utils.readDefaultConfig(), revokeIP,
//          revokePort);
//      // 服务的地址和端口
//      execute = client.execute("exchangeCallBackSpout",
//          JSONObject.toJSONString(exchangeVO));
      logger.info("调用撮合交易撤单接口返回结果execute：" + execute);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return JSONObject.parseObject(execute, ResultCallbackVO.class);
  }

  public BigDecimal getUfzAmount(ResultCallbackVO resultCallbackVO,
      ConsignationLog consignationLog) {
    ConsignationTradeTypeEnum consignationTradeTypeEnum = ConsignationTradeTypeEnum
        .getConsignationTradeTypeEnum(consignationLog.getTradeType());
    switch (consignationTradeTypeEnum) {
    case LIMIT_PRICE_BUY:
      return consignationLog.getConsignationCount()
          .multiply(consignationLog.getConsignationPrice())
          .subtract(resultCallbackVO.getSumMoney());
    default:
      return resultCallbackVO.getResidueCount();
    }
  }

  /**
   * 撤单 撤单逻辑： 调用撮合引擎接口返回能否撤单 不能：返回撤单失败，正在撮合交易 能： 修改委托单状态为撤单 修改失败，返回撤单失败
   * 修改成功，则调用更新用户资产以及插入资产变更记录接口 根据接口调用返回结果判断： 失败，返回撤单失败 成功，返回撤单成功
   *
   * @param consignationNo
   *          委托单号
   * @return 撤单结果：true-成功；false-失败
   */
  @Transactional(rollbackFor = Exception.class)
  public Object revokeOrder(final String consignationNo)
      throws IllegalParamException {
    if ("0".equals(consignationNo)) {
      throw new IllegalParamException(
          "consignationNo error in method revokeOrder of "
              + this.getClass().getName());
    }
    logger.info("revoke orderNo:" + consignationNo);
    ConsignationLog consignationLog = findBySeq(consignationNo);
    if (null == consignationLog
        || consignationLog.getConsignationStatus() == ConsignationStatusEnum.REVOKE
            .getStatusCode()) {
      logger.info("revoke orderNo:" + consignationNo + "已撤单成功，不可重复撤单！");
      return ErrorMsgEnum.REVOKE_FAIL;
    }
    TradePairInfo tradePairInfo = tradePairInfoService
        .getTradePairInfo(consignationLog.getBizType());
    if (tradePairInfo == null) {
      logger.info("bizType:" + consignationLog.getBizType() + "不存在！");
      return ErrorMsgEnum.TRADE_PAIR_NOT_FOUND;
    }
    int commodityCoin = getCurrencyTypeValue(tradePairInfo,
        consignationLog.getTradeType());
    ResultCallbackVO resultCallbackVO = invokeRevokeOrder(consignationNo);
    if (resultCallbackVO == null) {
      logger.info("revoke orderNo:" + consignationNo + "调用撮合引擎接口失败，撤单失败！");
      return ErrorMsgEnum.REVOKE_FAIL;
    }
    boolean callBack = resultCallbackVO.getCallBack();
    if (callBack == false) {// 撤单失败，返回失败结果，不做其他任何操作
      int code = resultCallbackVO.getCode();
      logger.info("revoke orderNo:" + consignationNo
          + "撤单失败，resultCallbackVO code is :" + code);
      return ErrorMsgEnum.REVOKE_FAIL;
    }
    // result = true;106撮合引擎未收到交易记录，撤单成功；
    // 撤单成功，做以下操作：1、更新委托表数据，包括已成交量等信息；2、调用account接口更新用户资产信息等。
    logger.info("revoke orderNo:" + consignationNo
        + "update consignationLog start");
    consignationLog = updateConsignationLogData(consignationLog,
        resultCallbackVO);
    if (consignationLogMapper.updateConsignation(consignationLog) <= 0) {
      logger.info("revoke orderNo:" + consignationNo
          + "update consignationLog fail");
      return ErrorMsgEnum.REVOKE_FAIL;
    }
    logger.info("revoke orderNo:" + consignationNo
        + "update consignationLog end");
    BigDecimal ufzAmount = getUfzAmount(resultCallbackVO, consignationLog);
    logger.info("revoke orderNo:" + consignationNo + ",userid:"
        + consignationLog.getUserId() + ",username:"
        + consignationLog.getUsername() + ",orderNo:" + consignationNo
        + ",currencyType:" + commodityCoin + ",ufzAmount:" + ufzAmount
        + ",invoke assetChange interface start");
    String unfzJson = accountClient.assetChange(consignationLog.getUserId(),
        consignationLog.getUsername(), consignationNo,
        AssetChangeConstant.UNFREEZE, commodityCoin, ufzAmount);
    logger.info("revoke orderNo:" + consignationNo
        + "invoke assetChange interface result is " + unfzJson);
    JSONObject unfzJsonObject = JSONObject.parseObject(unfzJson);
    if (null == unfzJsonObject) {
      logger.info("revoke orderNo:" + consignationNo
          + "更新用户资产以及插入资产变更记录接口调用结果返回值为空，撤单失败！");
      return ErrorMsgEnum.REVOKE_FAIL;
    }
    if (!unfzJsonObject.containsKey("success")
        || !unfzJsonObject.getBooleanValue("success")) {
      logger.info("revoke orderNo:" + consignationNo + "更新用户资产失败，撤单失败！");
      return ErrorMsgEnum.REVOKE_FAIL;
    }
    logger.info("revoke orderNo:" + consignationNo + "撤单成功！");
    return null;
  }

  public ConsignationLog updateConsignationLogData(
      ConsignationLog consignationLog, ResultCallbackVO resultCallbackVO) {
    consignationLog.setMadeCount(resultCallbackVO.getSumCount());
    consignationLog.setMadePrice(resultCallbackVO.getSumMoney());
    consignationLog.setMadeAveragePrice(resultCallbackVO.getAverageMoney());
    consignationLog.setUpdateTime(DateUtil.getCurrentTimestamp());
    return consignationLog;
  }

  public ConsignationLog findBySeq(String seq) {
    ConsignationLog record = new ConsignationLog();
    record.setConsignationNo(seq);
    return consignationLogMapper.selectOne(record);
  }

  public Object revokeAllOrder(String userId) {
    List<Map<String, Object>> list = consignationLogMapper.listConsignationOrderByUserIdForRevokeOrder(userId);
    final Map<String, Object> res = Maps.newHashMap();
    res.put("result", ErrorMsgEnum.REVOKE_FAIL);
    list.parallelStream().forEach(map -> {
      try {
        Object revokeMap = revokeOrder((String) map.get("consignation_no"));
        if(null==revokeMap){
          res.put("result", null);
        }
      } catch (IllegalParamException e) {
        e.printStackTrace();
      }
    });
    return res.get("result");
  }

  
  
  
  protected AjaxResponse writeObj(Object object) {
    if (null != object && object instanceof ErrorMsgEnum) {
      ErrorMsgEnum errorMsgEnum = (ErrorMsgEnum) object;
      return AjaxResponse.failure(errorMsgEnum.getCode(),messageSource.getMessage(errorMsgEnum.name(),
          null, getLocal()));
    } else {
      return AjaxResponse.success(object);
    }
  }

  protected Locale getLocal() {
    return LocaleContextHolder.getLocale();
  }

  /**
   * 校验数量，最多可以保留四位小数
   *
   * @param str
   * @return
   */
  public boolean isMoney4(String str) {
    return Pattern.matches(REGEX_MONEY4, str);
  }

  /**
   * 校验金额，最多可以保留六位小数
   *
   * @param str
   * @return
   */
  public boolean isMoney6(String str) {
    return Pattern.matches(REGEX_MONEY6, str);
  }

  public boolean preTen(String str){
    if(StringUtils.isEmpty(str)){
      return false;
    }
    if(str.split("\\.")[0].length() == 0 || str.split("\\.")[0].length()>10){
      return false;
    }
    return true;
  }
  
  /**
   * //市价买入，验证金额小数点后六位数字
   * //市价卖出，验证数量小数点后四位数字
   * //限价买卖，验证金额小数点后六位且数量小数点后四位数字
   * @param tradeType
   * @param consignationPrice
   * @param consignationCount
   * @return
   */
  public boolean checkPriceAndCount(String tradeType, String consignationPrice,
      String consignationCount) {
    ConsignationTradeTypeEnum consignationTradeTypeEnum = ConsignationTradeTypeEnum
        .getConsignationTradeTypeEnum(tradeType);
    switch (consignationTradeTypeEnum) {
    case MARKET_PRICE_BUY:
      if(!preTen(consignationCount) || !isMoney6(consignationCount)){
        return false;
      }else{
        return true;
      }
    case MARKET_PRICE_SELL:
      if(!preTen(consignationCount) || !isMoney4(consignationCount)){
        return false;
      }else{
        return true;
      }
    case LIMIT_PRICE_BUY:
    case LIMIT_PRICE_SELL:
      if (new BigDecimal(consignationPrice).compareTo(BigDecimal.ZERO) <= 0 
        || !preTen(consignationPrice)
        || !preTen(consignationCount)
        || !isMoney6(consignationPrice) 
        || !isMoney4(consignationCount)) {
        return false;
      }
    default:
      return true;
    }
  }

}
