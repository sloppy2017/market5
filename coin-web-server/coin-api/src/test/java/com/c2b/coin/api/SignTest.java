package com.c2b.coin.api;

import com.c2b.coin.common.EncryptUtil;
import com.c2b.coin.common.ParamMessageUtil;
import com.c2b.coin.common.URLCodeUtil;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class SignTest {

  @Test
  public void generate() {
    String accessKeyId = "f9b17c60-cb71-47f0-bc20-b40fa43eb0ac";
    String encryptKey = "jTEKWHzJPkST652dKioqkBtEfaEPDnmf";
    String timestamp = URLCodeUtil.encode(new DateTime().minusHours(8).toString("yyyy-MM-dd'T'HH:mm:ss"));

    String requestMethod = "GET";
    String serverName = "192.168.1.104";
    String requestURI = "/v1/account/get";
    String encryptText = ParamMessageUtil.format(
      requestMethod + "\n" +
        serverName + "\n" +
        requestURI + "\n" +
      "AccessKeyId={}&SignatureMethod=HmacSHA256&SignatureVersion=1&Timestamp={}"
      , accessKeyId, timestamp);
    System.out.println(encryptText);

    String signature = URLCodeUtil.encode(EncryptUtil.encryptHmacSHA(HmacAlgorithms.HMAC_SHA_256, encryptKey, encryptText));
    System.out.println(signature);
  }

}
