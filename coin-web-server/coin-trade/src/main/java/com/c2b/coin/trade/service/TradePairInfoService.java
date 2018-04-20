package com.c2b.coin.trade.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSONObject;
import com.c2b.coin.trade.entity.TradePairInfo;
import com.c2b.coin.trade.mapper.TradePairInfoMapper;
import com.google.common.collect.Maps;

/**
 *
 * @author Anne
 * @date 2017.10.19
 */
@Service
public class TradePairInfoService {
  private Logger logger = LoggerFactory.getLogger(TradePairInfoService.class);
  @Autowired
  TradePairInfoMapper tradePairInfoMapper;

  @Cacheable( value = "TradePairInfo", key = "#root.methodName", unless = "#result eq null")
  public Map<Long, String> listTradePairInfo() {
    Map<Long, String> map = new HashMap<Long, String>();
    List<TradePairInfo> list = tradePairInfoMapper.listTradePairInfo();
    if (list == null || list.isEmpty()) {
      return null;
    }
    for (TradePairInfo tradePairInfo : list) {
      map.put(tradePairInfo.getId(), tradePairInfo.getCommodityCoinName() + "/"
          + tradePairInfo.getMoneyCoinName());
    }
    return map;
  }

  public List<Map<String, Object>> listCurrencies(){
    return tradePairInfoMapper.listCurrency();
  }

  public TradePairInfo getTradePairInfoByTradePairInfo(Long commodityCoin, Long moneyCoin){
    TradePairInfo tradePairInfo = new TradePairInfo();
    tradePairInfo.setCommodityCoin(commodityCoin);
    tradePairInfo.setMoneyCoin(moneyCoin);
    return tradePairInfoMapper.selectOne(tradePairInfo );
  }


  public String getTradePairInfoByPK(Long id){
    return JSONObject.toJSONString(getTradePairInfo(String.valueOf(id)));
  }

  @Cacheable( value = "TradePairInfo", key = "#id", unless = "#result eq null")
  public TradePairInfo getTradePairInfo(String id){
    TradePairInfo tradePairInfo = tradePairInfoMapper.getTradePairInfoValueByPK(Long.parseLong(id));
    logger.info("#######################{}", JSONObject.toJSONString(tradePairInfo));
    return tradePairInfo;
  }

  public Map<Long, String> listTradePairInfoByMarket(){
    List<Map<String, Object>> list = tradePairInfoMapper.listTradePair();
    Map mapvalue  = Maps.newHashMap();
    if (list == null || list.isEmpty()) {
      return null;
    }
    for(Map dbMap:list){
      mapvalue.put(dbMap.get("moneyCoin"), dbMap.get("moneyCoinName"));
    }
    return mapvalue;
  }
}
