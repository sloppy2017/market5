package com.dynamo.message.service.emay;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dynamo.message.util.RestClient;
import com.dynamo.message.vo.MessageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class YMSmsService {
  @Value("${coin.sms.emay.singleSmsUrl}")
  private String singleSmsUrl;
  @Value("${coin.sms.emay.appId}")
  private String appId;
  @Value("${coin.sms.emay.secretKey}")
  private String secretKey;
  @Value("${coin.sms.emay.tf}")
  private String tf;
  private String algorithm = "AES/ECB/PKCS5Padding";
  @Autowired
  RestClient restClient;

  public void sendActiveSms(MessageVo messageVo, String content){
    content=String.format(content, messageVo.getData().get("token"));
    sendSingleSms(content,messageVo.getToUser(),"UTF-8");
  }

  /**
   * 发送单条短信
   */
  public void sendSingleSms(String content, String mobile, String encode) {
    System.out.println("=============begin setSingleSms==================");
    SmsSingleRequest pamars = new SmsSingleRequest();
    pamars.setContent(content);
    pamars.setMobile(mobile);
    ResultModel result = request(pamars, encode);
    System.out.println("result code :" + result.getCode());
    if ("SUCCESS".equals(result.getCode())) {
      SmsResponse response = JSONObject.parseObject(result.getResult(), SmsResponse.class);
      if (response != null) {
        System.out.println("data : " + response.getMobile() + "," + response.getSmsId() + "," + response.getCustomSmsId());
      }
    }
    System.out.println("=============end setSingleSms==================");
  }

  /**
   * 公共请求方法
   */
  public ResultModel request(Object content, String encode) {
    Map<String, String> headers = new HashMap<String, String>();
    EmayHttpRequestBytes request = null;
    try {
      headers.put("appId", appId);
      headers.put("encode", encode);
      String requestJson = JSON.toJSONString(content);
      System.out.println("result json: " + requestJson);
      byte[] bytes = requestJson.getBytes(encode);
      System.out.println("request data size : " + bytes.length);

      headers.put("gzip", "on");
      bytes = com.dynamo.message.service.emay.GZIPUtils.compress(bytes);
      byte[] parambytes = AES.encrypt(bytes, secretKey.getBytes(), algorithm);
      System.out.println("request data size [en] : " + parambytes.length);
      request = new EmayHttpRequestBytes(singleSmsUrl, encode, "POST", headers, null, parambytes);
    } catch (Exception e) {
      System.out.println("加密异常");
      e.printStackTrace();
    }
    EmayHttpClient client = new EmayHttpClient();
    String code = null;
    String result = null;
    try {
      EmayHttpResponseBytes res = client.service(request, new EmayHttpResponseBytesPraser());
      if (res == null) {
        System.out.println("请求接口异常");
        return new ResultModel(code, result);
      }
      if (res.getResultCode().equals(EmayHttpResultCode.SUCCESS)) {
        if (res.getHttpCode() == 200) {
          code = res.getHeaders().get("result");
          if (code.equals("SUCCESS")) {
            byte[] data = res.getResultBytes();
            data = AES.decrypt(data, secretKey.getBytes(), algorithm);
            data = com.dynamo.message.service.emay.GZIPUtils.decompress(data);
            result = new String(data, encode);
          }
        } else {
          System.out.println("请求接口异常,请求码:" + res.getHttpCode());
        }
      } else {
        System.out.println("请求接口网络异常:" + res.getResultCode().getCode());
      }
    } catch (Exception e) {
      System.out.println("解析失败");
      e.printStackTrace();
    }
    ResultModel re = new ResultModel(code, result);
    return re;
  }
}
