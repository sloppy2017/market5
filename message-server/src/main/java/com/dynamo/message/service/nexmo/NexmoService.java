package com.dynamo.message.service.nexmo;

import com.dynamo.message.util.RestClient;
import com.dynamo.message.vo.MessageVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class NexmoService {
  private Logger logger = LoggerFactory.getLogger(NexmoService.class);
  @Autowired
  RestClient restClient;
  @Value("${coin.sms.nexmo.smsUrl}")
  private String smsUrl;
  @Value("${coin.sms.nexmo.key}")
  private String key;
  @Value("${coin.sms.nexmo.secret}")
  private String secret;
  @Value("${coin.sms.nexmo.from}")
  private String from;

  public void sendSms(MessageVo messageVo, String content) {
    content = String.format(content, messageVo.getData().get("token"));
    Map<String, Object> param = new HashMap<>();
    param.put("api_key", key);
    param.put("api_secret", secret);
    param.put("type","unicode");
    param.put("to", messageVo.getRegionCode() + messageVo.getToUser());
    param.put("from", from);
    param.put("text", content);
    String result = restClient.postJsonWithBody(smsUrl, param, String.class);
    logger.info("香港运营商发送短信：{}", result);
  }
}
