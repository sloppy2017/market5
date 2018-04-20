package com.c2b.coin.web.common;

import com.alibaba.fastjson.JSON;
import com.c2b.coin.common.AjaxResponse;
import com.c2b.coin.common.Constants;
import com.c2b.coin.common.enumeration.ErrorMsgEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BaseRest {
  protected Logger logger = LoggerFactory.getLogger(BaseRest.class);
  @Autowired
  protected MessageSource messageSource;
  @Autowired
  protected HttpServletRequest request;
  @Autowired
  protected HttpServletResponse response;

  protected String getUserId() {
    return getHeader(Constants.HTTP_HEADER_USER_ID, null);
  }

  protected String getUsername() {
    return getHeader(Constants.HTTP_HEADER_USER_NAME, null);
  }

  protected String getActivityId() {
    return getHeader(Constants.HTTP_HEADER_ACTIVITY_ID, Constants.DEFAULT_ACTIVITY_ID);
  }

  protected String getDeviceId() {
    return getHeader(Constants.HTTP_HEADER_DEVICE_ID, Constants.DEFAULT_DEVICE_ID);
  }

  protected String getChannel() {
    return getHeader(Constants.HTTP_HEADER_CHANNEL_NAME, Constants.DEFAULT_CHANNEL_NAME);
  }
  protected String getToken() {
    return getHeader(Constants.HTTP_HEADER_TOKEN, Constants.DEFAULT_HTTP_HEADER_TOKEN);
  }
  protected String getAppVersion() {
    return getHeader(Constants.HTTP_HEADER_APP_VERSION, Constants.DEFAULT_APP_VERSION);
  }

  protected String writeJson(Object obj) {
    if (null != obj && obj instanceof ErrorMsgEnum) {
      ErrorMsgEnum errorMsgEnum = (ErrorMsgEnum) obj;
      return JSON.toJSONString(AjaxResponse.failure(errorMsgEnum.getCode(),messageSource.getMessage(errorMsgEnum.name(), null, getLocal())));
    } else {
      return JSON.toJSONString(AjaxResponse.success(obj));
    }
  }

  protected AjaxResponse writeObj(Object object){
    if (null != object && object instanceof ErrorMsgEnum){
      ErrorMsgEnum errorMsgEnum = (ErrorMsgEnum) object;
      return AjaxResponse.failure(errorMsgEnum.getCode(),messageSource.getMessage(errorMsgEnum.name(), null, getLocal()));
    }else {
      return AjaxResponse.success(object);
    }
  }

  protected AjaxResponse writeFailureObjWithParam(Object object, Object... param){
    if (null != object && object instanceof ErrorMsgEnum){
      ErrorMsgEnum errorMsgEnum = (ErrorMsgEnum) object;
      return AjaxResponse.failure(errorMsgEnum.getCode(),String.format(messageSource.getMessage(errorMsgEnum.name(), null, getLocal()), param));
    }else {
      return AjaxResponse.success(object);
    }
  }
  protected String writeFailureJsonWithParam(Object object, Object... param){
    if (null != object && object instanceof ErrorMsgEnum){
      ErrorMsgEnum errorMsgEnum = (ErrorMsgEnum) object;
      return JSON.toJSONString(AjaxResponse.failure(errorMsgEnum.getCode(),String.format(messageSource.getMessage(errorMsgEnum.name(), null, getLocal()), param)));
    }else {
      return JSON.toJSONString(AjaxResponse.success(object));
    }
  }

  protected Locale getLocal() {
    return LocaleContextHolder.getLocale();
  }

  private String getHeader(String headerName, String defaultValue) {
    String value = request.getHeader(headerName);
    return StringUtils.isEmpty(value) ? defaultValue : value;
  }

  @ExceptionHandler
  @ResponseBody
  protected String exception(HttpServletRequest request, HttpServletResponse res, Exception e) {
    logger.error(e.getMessage());
    logger.error("异常处理：IP:{},URI:{},ServletPath:{},Method:{}", IPUtils.getIpAddr(request), request.getRequestURI(), request.getServletPath(), request.getMethod());
    return writeJson(ErrorMsgEnum.SERVER_BUSY);
  }
}
