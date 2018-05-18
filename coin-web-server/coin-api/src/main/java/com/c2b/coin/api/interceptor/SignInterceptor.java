package com.c2b.coin.api.interceptor;

import com.alibaba.fastjson.JSON;
import com.c2b.coin.api.annotation.Sign;
import com.c2b.coin.api.enums.RestResponseCode;
import com.c2b.coin.common.EncryptUtil;
import com.c2b.coin.web.common.rest.bean.RestResponseBean;
import com.c2b.coin.web.common.enums.IResponseCode;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.protocol.HTTP;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * 签名认证
 *
 * @auther: tangwei
 * @date: 2018/5/17
 */
public class SignInterceptor implements HandlerInterceptor {

  /**
   *
   * @param httpServletRequest
   * @param httpServletResponse
   * @param handler
   * @return
   * @throws Exception
   */
  @Override
  public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
    HandlerMethod handlerMethod = (HandlerMethod) handler;
    Sign signAnnotation = handlerMethod.getMethod().getAnnotation(Sign.class);
    if (signAnnotation == null) { //不需要签名
      return true;
    }

    String accessKeyId = StringUtils.defaultString(httpServletRequest.getParameter("AccessKeyId"), "");
    String timestamp = StringUtils.defaultString(httpServletRequest.getParameter("Timestamp"), "");
    String signature = StringUtils.defaultString(httpServletRequest.getParameter("Signature"), "");
    String signatureMethod = StringUtils.defaultString(httpServletRequest.getParameter("SignatureMethod"), "");
    String signatureVersion = StringUtils.defaultString(httpServletRequest.getParameter("SignatureVersion"), "");

    //accessKeyId格式校验
    if (!checkAccessKeyId(accessKeyId)) {
      writeError(httpServletResponse, RestResponseCode.SignError.InvalidAccessKeyIdNotFound);
      return false;
    }

    //时间戳格式校验
    if (!checkTimestamp(timestamp)) {
      writeError(httpServletResponse, RestResponseCode.SignError.InvalidTimeStampFormat);
      return false;
    }

    if (!signatureMethod.equals(HmacAlgorithms.HMAC_SHA_256.getName())) {
      writeError(httpServletResponse, RestResponseCode.SignError.InvalidSignatureMethodNotFound);
      return false;
    }

    if (!signatureVersion.equals("1")) {
      writeError(httpServletResponse, RestResponseCode.SignError.InvalidSignatureVersionNotFound);
      return false;
    }

    //签名格式校验
    if (!checkSignature(signature)) {
      writeError(httpServletResponse, RestResponseCode.SignError.SignatureDoesNotMatch);
      return false;
    }

    //判断时间是否已过期
    if (isTimeout(timestamp)) {  //客户端传的本地时间，有可能比服务器时间快或则慢，但不能超过正负15分钟，否则认为时间非法
      writeError(httpServletResponse, RestResponseCode.SignError.InvalidTimeStampExpired);
      return false;
    }


    String encryptKey = "";
    String encryptText = "timestamp=" + timestamp;
    String encryptSignature = EncryptUtil.encryptHmacSHA(HmacAlgorithms.HMAC_SHA_256, encryptKey, encryptText);
    if (!signature.equals(encryptSignature)) {
      writeError(httpServletResponse, RestResponseCode.SignError.SignatureDoesNotMatch);
      return false;
    }
    return true;
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView model) throws Exception {
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) throws Exception {
  }

  private static final Pattern accessKeyIdPattern = Pattern.compile("[\\-a-z0-9]+");
  private boolean checkAccessKeyId(String accessKeyId) {
    return accessKeyIdPattern.matcher(accessKeyId).matches();
  }

  /**
   * 验证日期格式（YYYY-MM-DD HH:mm:ss）
   *
   * @param s
   * @return
   */
  private static final Pattern timestampPattern = Pattern.compile("((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29))T(0\\d{1}|1\\d{1}|2[0-3]):([0-5]\\d{1}):([0-5]\\d{1})");
  private boolean checkTimestamp(String timestamp) {
    return timestampPattern.matcher(timestamp).matches();
  }

  private static final Pattern signaturePattern = Pattern.compile("[a-zA-Z0-9/=+]+");
  private boolean checkSignature(String signature) {
    return signaturePattern.matcher(signature).matches();
  }

  private boolean isTimeout(String timestamp) {
    return Math.abs(new Date().getTime() - toUTC(timestamp).getTime()) > 900000;
  }

  private static final DateTimeFormatter utcFormat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
  private Date toUTC(String utcTime) {
    return DateTime.parse(utcTime, utcFormat).toDate();
  }

  private void writeError(HttpServletResponse response, IResponseCode responseCode) throws IOException {
    response.setHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8");
    response.getWriter().append(JSON.toJSONString(RestResponseBean.onFailure(responseCode)));
  }

}
