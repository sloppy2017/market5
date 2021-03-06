package com.c2b.coin.trade.client;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;

/**
 * RestTemplate调用REST资源
 *
 * @author <a href="mailto:guo_xp@163.com">guoxinpeng</a>
 * @version 1.0 2016/11/30 10:14
 * @projectname new-pay
 * @packname com.unis.service.config.rest
 */
@Component
public class RestClient {
  private static Logger logger = LoggerFactory.getLogger(RestClient.class);
  
  @Autowired
  private RestTemplate restTemplate;

  public <T> T postJsonWithBody(String url, Object obj, Class<T> responseType, String authorization) throws HttpServerErrorException {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Accept", "application/json");
    httpHeaders.add("Content-Type", "application/json;charset=utf-8");
    httpHeaders.add("Content-Length", "256");
    httpHeaders.add("Authorization", authorization);
    String json = com.alibaba.fastjson.JSONObject.toJSONString(obj);
    HttpEntity<String> formEntity = new HttpEntity<String>(json, httpHeaders);

    logger.info("PostWithBody url = {}, json = {}", url, json);
    String response = restTemplate.postForObject(url, formEntity, String.class);
    logger.info("PostWithBody url = {}, response = {}", url, response);
    if (responseType.getName() == String.class.getName())
      return (T) response;
    T result = JSONObject.parseObject(response, responseType);
    return result;
  }

  public <T> T post(String url, Map<String, Object> param, Class<T> responseType) throws HttpServerErrorException {
    MultiValueMap<String, String> requestEntity = new LinkedMultiValueMap<>();
    param.keySet().stream().forEach(key -> requestEntity.add(key, String.valueOf(param.get(key))));
    T t = restTemplate.postForObject(url, requestEntity, responseType);
    logger.info("返回结果：{}", t);
    return t;
  }

  public <T> T postMap(String url, Map<String, Object> param, Class<T> responseType) throws HttpServerErrorException {
    HttpHeaders headers = new HttpHeaders();
    MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
    headers.setContentType(type);
    headers.add("Accept", MediaType.APPLICATION_JSON.toString());

    String json = JSONObject.toJSONString(param);
    logger.info("url:【{}】,请求参数:【{}】", url, param);
    HttpEntity<String> formEntity = new HttpEntity<String>(json, headers);
    T t = restTemplate.postForObject(url, formEntity, responseType);
    logger.info("返回结果：{}", t);
    return t;
  }

  public <T> T get(String url, Map<String, Object> param, Class<T> responseType) throws HttpServerErrorException {
    if(param == null || param.isEmpty()){
      return restTemplate.getForObject(url, responseType);
    }else{
      return restTemplate.getForObject(url, responseType, param);
    }
  }


}
