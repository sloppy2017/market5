package com.c2b.coin.api.interceptor;

import com.alibaba.fastjson.JSON;
import com.c2b.coin.api.annotation.Sign;
import com.c2b.coin.api.controller.BaseController;
import com.c2b.coin.common.Constants;
import com.c2b.coin.common.EncryptUtil;
import com.c2b.coin.common.URLCodeUtil;
import com.c2b.coin.common.enumeration.ErrorMsgEnum;
import com.c2b.coin.web.common.IPUtils;
import com.c2b.coin.web.common.RedisUtil;
import com.c2b.coin.web.common.rest.bean.ResponseBean;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.protocol.HTTP;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 签名认证
 *
 * @auther: tangwei
 * @date: 2018/5/17
 */
public class SignInterceptor implements HandlerInterceptor {

  @Autowired
  protected MessageSource messageSource;

  @Autowired
  protected RedisUtil redisUtil;

  /**
   * @param httpServletRequest
   * @param httpServletResponse
   * @param handler
   * @return
   * @throws Exception
   */
  @Override
  public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
    String key = IPUtils.getIpAddr(httpServletRequest);
    long n = redisUtil.incr(key);
    if (n == 1) {
      redisUtil.expire(key, 10);
    } else if (n > 100) {
      writeError(httpServletResponse, ErrorMsgEnum.TOO_MANY_REQUEST);
      return false;
    }

    HandlerMethod handlerMethod = (HandlerMethod) handler;
    Sign signAnnotation = handlerMethod.getMethod().getAnnotation(Sign.class);
    if (signAnnotation == null) { //不需要签名
      return true;
    }

    String accessKeyId = StringUtils.defaultString(httpServletRequest.getParameter("AccessKeyId"), "");
    String timestampEncode = StringUtils.defaultString(httpServletRequest.getParameter("Timestamp"), "");
    String timestamp = URLCodeUtil.decode(timestampEncode);
    String signature = URLCodeUtil.decode(StringUtils.defaultString(httpServletRequest.getParameter("Signature"), ""));
    String signatureMethod = StringUtils.defaultString(httpServletRequest.getParameter("SignatureMethod"), "");
    String signatureVersion = StringUtils.defaultString(httpServletRequest.getParameter("SignatureVersion"), "");

    //accessKeyId格式校验
    if (!checkAccessKeyId(accessKeyId)) {
      writeError(httpServletResponse, ErrorMsgEnum.INVALID_ACCESS_KEY_ID_NOT_FOUND);
      return false;
    }

    //时间戳格式校验(UTC)
    if (!checkTimestamp(timestamp)) {
      writeError(httpServletResponse, ErrorMsgEnum.INVALID_TIMESTAMP_FORMAT);
      return false;
    }

    if (!signatureMethod.equals(HmacAlgorithms.HMAC_SHA_256.getName())) {
      writeError(httpServletResponse, ErrorMsgEnum.INVALID_SIGNATURE_METHOD_NOT_FOUND);
      return false;
    }

    if (!signatureVersion.equals("1")) {
      writeError(httpServletResponse, ErrorMsgEnum.INVALID_SIGNATURE_VERSION_NOT_FOUND);
      return false;
    }

    //签名格式校验
    if (!checkSignature(signature)) {
      writeError(httpServletResponse, ErrorMsgEnum.SIGNATURE_DOES_NOT_MATCH);
      return false;
    }

    //判断时间是否已过期
    if (isTimeout(timestamp)) {  //客户端传的本地时间，有可能比服务器时间快或则慢，但不能超过正负15分钟，否则认为时间非法
      writeError(httpServletResponse, ErrorMsgEnum.INVALID_TIMESTAMP_EXPIRED);
      return false;
    }

    Map<String, String> userAcess = redisUtil.hgetall(Constants.REDIS_USER_ACCESS_KEY + accessKeyId);
    if (userAcess == null) {
      writeError(httpServletResponse, ErrorMsgEnum.INVALID_ACCESS_KEY_ID_NOT_FOUND);
      return false;
    }

    String allowIp = userAcess.get("allowIp");
    if (StringUtils.isNotEmpty(allowIp)) {
      allowIp += ",";
      if (!allowIp.contains(IPUtils.getIpAddr(httpServletRequest) + ",")) {
        writeError(httpServletResponse, ErrorMsgEnum.INVALID_IP_ADDRESS_NOT_FOUND);
        return false;
      }
    }

    StringBuilder encryptText;
    if (HttpMethod.GET.matches(httpServletRequest.getMethod())) {
      //对于GET请求，所有参数都需要进行签名运算
      encryptText = createSignAllStringBuilder(httpServletRequest);
    } else if (HttpMethod.POST.matches(httpServletRequest.getMethod())) {
      //对于POST请求，需要进行签名运算的只有AccessKeyId、SignatureMethod、SignatureVersion、Timestamp四个参数，其它参数放在body中。
      encryptText = createSignStringBuilder(httpServletRequest);
      encryptText
        .append("AccessKeyId=").append(accessKeyId)
        .append("&SignatureMethod=").append(signatureMethod)
        .append("&SignatureVersion=").append(signatureVersion)
        .append("&Timestamp=").append(timestampEncode);
    } else {
      writeError(httpServletResponse, ErrorMsgEnum.BAD_REQUEST);
      return false;
    }

    String encryptKey = userAcess.get("accessKeySecret");
    String encryptSignature = EncryptUtil.encryptHmacSHA(HmacAlgorithms.HMAC_SHA_256, encryptKey, encryptText.toString());
    if (!signature.equals(encryptSignature)) {
      writeError(httpServletResponse, ErrorMsgEnum.SIGNATURE_DOES_NOT_MATCH);
      return false;
    }

    BaseController.getThreadContextMap().putUserId(userAcess.get("userId"));
    BaseController.getThreadContextMap().putUserName(userAcess.get("userName"));
    return true;
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView model) throws Exception {
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) throws Exception {
    BaseController.getThreadContextMap().clear();
  }

  private StringBuilder createSignStringBuilder(HttpServletRequest httpServletRequest) {
    return new StringBuilder().append(httpServletRequest.getMethod()).append("\n").append(httpServletRequest.getServerName()).append("\n").append(httpServletRequest.getRequestURI()).append("\n");
  }

  private StringBuilder createSignAllStringBuilder(HttpServletRequest httpServletRequest) {
    StringBuilder encryptText = createSignStringBuilder(httpServletRequest);
    List<String> parameterNames = getParameterNames(httpServletRequest);
    for (int i = 0; i < parameterNames.size(); i++) {
      String name = parameterNames.get(i);
      if (name.equals("Signature")) {
        continue;
      } else {
        if (i > 0) {
          encryptText.append("&");
        }
        encryptText.append(name).append("=").append(httpServletRequest.getParameter(name));
      }
    }
    return encryptText;
  }

  private List<String> getParameterNames(HttpServletRequest httpServletRequest) {
    List<String> parameterNames = Collections.list(httpServletRequest.getParameterNames());
    Collections.sort(parameterNames);
    return parameterNames;
  }

  private static final Pattern accessKeyIdPattern = Pattern.compile("[\\-a-z0-9]+");

  private boolean checkAccessKeyId(String accessKeyId) {
    return accessKeyIdPattern.matcher(accessKeyId).matches();
  }

  /**
   * 验证日期格式（yyyy-MM-dd'T'HH:mm:ss'Z'）
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

  /**
   * 检查客户端时间与服务器时间相差不能超过abs(15)分钟，否则认为时间非法
   *
   * @param timestamp
   * @return
   */
  private boolean isTimeout(String timestamp) {
    return Math.abs(new Date().getTime() - toGMT8(timestamp).getTime()) > 900000;
  }

  private static final DateTimeFormatter utcFormat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");

  private Date toGMT8(String utcTime) {
    return DateTime.parse(utcTime, utcFormat).plusHours(8).toDate();//utc to gmt8
  }

  private void writeError(HttpServletResponse response, ErrorMsgEnum errorMsgEnum) throws IOException {
    response.setHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8");
    response.getWriter().append(JSON.toJSONString(ResponseBean.onFailure(errorMsgEnum.getCode(), messageSource.getMessage(ErrorMsgEnum.SERVER_BUSY.name(), null, LocaleContextHolder.getLocale()))));
  }

}
