package com.c2b.coin.market.thread;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.c2b.coin.market.mapper.MatchMoneyMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RealTimeBlockDataThread extends RealTimeBase implements Runnable {

  static final Logger logger = LoggerFactory.getLogger(RealTimeBlockDataThread.class);
  private String currencyType,pathFront,pathEnd,suffix;
  private long beginTime,endTime;
  private MatchMoneyMapper matchMoneyMapper;

  public RealTimeBlockDataThread (String currencyType,String pathFront,String pathEnd,long beginTime,long endTime,
                                  String suffix , MatchMoneyMapper matchMoneyMapper){
    this.currencyType = currencyType;
    this.pathFront = pathFront;
    this.pathEnd = pathEnd;
    this.beginTime = beginTime;
    this.endTime = endTime;
    this.suffix = suffix;
    this.matchMoneyMapper = matchMoneyMapper;
  };

  public void setCurrencyType(String currencyType) {
    this.currencyType = currencyType;
  }

  public void setPathFront(String pathFront) {
    this.pathFront = pathFront;
  }

  public void setPathEnd(String pathEnd) {
    this.pathEnd = pathEnd;
  }

  public void setBeginTime(long beginTime) {
    this.beginTime = beginTime;
  }

  public void setEndTime(long endTime) {
    this.endTime = endTime;
  }

  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }

  public void setMatchMoneyMapper(MatchMoneyMapper matchMoneyMapper) {
    this.matchMoneyMapper = matchMoneyMapper;
  }

  @Override
  public void run() {
    String fileStr = pathFront + currencyType + pathEnd + suffix;
    String fileStr_pre = pathFront + currencyType.toUpperCase() + pathEnd + "_PRE" + suffix;
    String lastHistoryFilePath = pathFront + currencyType.toUpperCase() + "/TEMP" + suffix;
    String mergeContent = this.getRealtimeDataByTimeInterval();
    String key = pathEnd.replaceAll("/","");
    logger.debug("filestr:"+fileStr);
    if (createPathByCurrcyDate(fileStr) == false) return;
    if (writeToFile(fileStr, mergeContent) == false) return;
    mergeHistoryFile(lastHistoryFilePath ,fileStr_pre , JSONObject.parseObject(mergeContent),key);
  }

  private BigDecimal getRealTimePrice(String currencyType){

    List<Map<String,Object>> list =  this.matchMoneyMapper.getTopNRealTimePriceBycurrency(currencyType,1);
    if(list == null || list.size() == 0){
      return BigDecimal.ZERO;
    }else{
      return (BigDecimal) list.get(0).get("made_price");
    }
  }

  private  String pathEndToTempPath(){
    String[] str = pathEnd.split("/");
    String tempPath = "";
    for(int i = 0 ; i<str.length-1 ; i++){
      tempPath += str[i];
      tempPath += "/";
    }
    return tempPath;
  }
  private String getRealtimeDataByTimeInterval() {
    Map<String,Object> map =  this.matchMoneyMapper.getKDate(currencyType,beginTime,endTime);
    if(map!=null && map.size()>0){
      map.put("currency",currencyType);
      map.put("time",endTime+60000L);
      if(BigDecimal.ZERO.compareTo((BigDecimal)map.get("volum")) == 0) {
        BigDecimal price = getRealTimePrice(currencyType);
        map.put("open", price);
        map.put("close", price);
        map.put("lowest", price);
        map.put("highest", price);
      }
    }else{
      map = new HashMap<>();
    }

    return JSON.toJSONString(map);

  }

}
