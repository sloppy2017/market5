package com.c2b.coin.common;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class WYSign {
  private static String genSignature(String secretKey, Map<String, String> params) throws UnsupportedEncodingException {
    // 1. 参数名按照ASCII码表升序排序
    String[] keys = params.keySet().toArray(new String[0]);
    Arrays.sort(keys);

    // 2. 按照排序拼接参数名与参数值
    StringBuilder sb = new StringBuilder();
    for (String key : keys) {
      sb.append(key).append(params.get(key));
    }
    // 3. 将secretKey拼接到最后
    sb.append(secretKey);

    // 4. MD5是128位长度的摘要算法，转换为十六进制之后长度为32字符
    return DigestUtils.md5Hex(sb.toString().getBytes("UTF-8"));
  }

  public static Map<String,String> genParam(String secretId, String secretKey,String captchaId, String validate) throws UnsupportedEncodingException {
    Map<String,String> param = new HashMap<>();
    param.put("captchaId",captchaId);
    param.put("validate", validate);
    param.put("user","");
    param.put("secretId",secretId);
    param.put("version","v2");
    param.put("timestamp", String.valueOf(DateUtil.getCurrentTimestamp()));
    param.put("nonce", String.valueOf(DateUtil.getCurrentTimestamp()) + RandNumUtil.getRandNum(6));
    param.put("signature", genSignature(secretKey,param));
    return param;
  }
}
