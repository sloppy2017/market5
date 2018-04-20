package com.c2b.coin.market.thread;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.HashMap;
import java.util.List;

public class RealTimeBase {
  static final Logger logger = LoggerFactory.getLogger(RealTimeBase.class);

  public Boolean createPathByCurrcyDate(String fileStr) {
    File file = new File(fileStr);

    if (!file.getParentFile().exists()) {
      //如果目标文件所在的目录不存在，则创建父目录
      logger.debug(" target file dirctory not exist,ready to create!");
      if (!file.getParentFile().mkdirs()) {
        logger.debug("target file dirctory create fail！");
        return false;
      }
    }
    //创建目标文件
    try {
      if (file.createNewFile()) {
        logger.debug("create File " + fileStr + " success!");
        return true;
      } else {
        logger.debug("create file " + fileStr + " fail!");
        return false;
      }
    } catch (IOException e) {
      e.printStackTrace();
      logger.debug("create file " + fileStr + " fail!" + e.getMessage());
      return false;
    }
  }

  public Boolean writeToFile(String fileStr, String message) {
    try {
      PrintWriter pw = new PrintWriter(new FileWriter(new File(fileStr), false));
      pw.write(message);
      pw.close();
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public Boolean  mergeHistoryFile(String tempHistoryFilePath , String historyFilePath, JSON mergeContent, String key){
    String content = null;
    try {
      content = this.readFileByChars(tempHistoryFilePath);
    } catch (Exception e) {
      e.printStackTrace();
    }
    HashMap mergeMap = new HashMap();
//    mergeMap.put(key, JSONObject.parseObject(mergeContent));
    if(mergeContent instanceof JSONObject){
      mergeMap.put(key, mergeContent);
    }else if(mergeContent instanceof JSONArray){
      content = mergeContent.toJSONString();
    }

    List<HashMap> listHistData = null;
    if(StringUtils.isEmpty(content)){
      logger.debug("mergeHistoryFile empty : "+tempHistoryFilePath);
      writeToFile(historyFilePath, "["+JSONObject.toJSONString(mergeMap)+"]");
      writeToFile(tempHistoryFilePath, "["+JSONObject.toJSONString(mergeMap)+"]");
      return true;
    }else{
      listHistData = JSONObject.parseArray(content,HashMap.class);
      if(listHistData!=null && listHistData.size()>0 && mergeMap.isEmpty() == false){
        List<HashMap> mergeList = JSONObject.parseArray("["+JSONObject.toJSONString(mergeMap)+"]",HashMap.class);
        listHistData.addAll(mergeList);
      }else{
        return false;
      }
    }
    logger.debug("mergeHistoryFile full : "+historyFilePath);
    String newContent = listHistData.size() > 200 ?
      JSONObject.toJSONString(listHistData.subList(1,listHistData.size())) : JSONObject.toJSONString(listHistData);
    writeToFile(tempHistoryFilePath, newContent);
    writeToFile(historyFilePath, newContent);
    return true;
  }

  public String readFileByChars(String fileName) {
    StringBuilder result = new StringBuilder();
    try{
      BufferedReader br = new BufferedReader(new FileReader(fileName));//构造一个BufferedReader类来读取文件
      String s = null;
      while((s = br.readLine())!=null){//使用readLine方法，一次读一行
        result.append(System.lineSeparator()+s);
      }
      br.close();
    }catch(Exception e){
      e.printStackTrace();
    }
    return result.toString();
  }
}
