package com.c2b.coin.user.service;

import com.alibaba.fastjson.JSONObject;
import com.c2b.coin.common.WYSign;
import com.c2b.coin.user.util.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@Service
public class WYYunDunService {
  @Autowired
  private RestClient restClient;
  @Value("${coin.wangyi.secretId}")
  private String secretId;
  @Value("${coin.wangyi.secretKey}")
  private String secretKey;
  @Value(("${coin.wangyi.yundunurl}"))
  private String wangyiUrl;
  @Value("${coin.wangyi.captchaId}")
  private String captchId;

  public boolean checkValid(String validate) throws UnsupportedEncodingException {
    Map<String, String> parma = WYSign.genParam(secretId, secretKey, captchId, validate);
    String result = restClient.postForm(wangyiUrl, parma, String.class);
    JSONObject jsonObject = JSONObject.parseObject(result);
    if (!"true".equals(jsonObject.getString("result"))) {
      return false;
    }
    return true;
  }
}
