package com.c2b.coin.api.enums;

import com.c2b.coin.web.common.enums.IResponseCode;

/**
 * 返回状态码
 *
 * @auther: tangwei
 * @date: 2018/5/17
 */
public class ApiResponseCode {

  public enum SignError implements IResponseCode {


    BadRequest("bad-request", "请求无效"),
    TooManyRequest("too-many-request", "请求次数太多"),
    InvalidAccessKeyIdNotFound("invalid-access-key-id-notfound", "无效的AccessKeyId,请检查是否正确"),
    InvalidTimeStampFormat("invalid-timestamp.format", "时间戳格式不正确,应为yyyy-MM-dd'T'HH:mm:ss,注意是(UTC时区)"),
    InvalidTimeStampExpired("invalid-timestamp.expired", "时间戳时间和服务器时间不在15分钟内"),
    InvalidSignatureMethodNotFound("invalid-signature-method-notfound", "无效的SignatureMethod,请检查是否正确"),
    InvalidSignatureVersionNotFound("invalid-signature-version-notfound", "无效的SignatureVersion,请检查是否正确"),
    InvalidIpAddressNotFound("invalid-ip-notfound", "无效的IP地址"),
    SignatureDoesNotMatch("signature-does-not-match", "签名不匹配");

    private String code;
    private String message;

    SignError(String code, String message) {
      this.code = code;
      this.message = message;
    }

    @Override
    public String getCode() {
      return code;
    }

    @Override
    public String getMessage() {
      return message;
    }
  }

}
