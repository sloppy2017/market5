package com.c2b.coin.api.controller.v1;

import com.c2b.coin.api.thread.ThreadContextMap;
import com.c2b.coin.common.enumeration.ErrorMsgEnum;
import com.c2b.coin.web.common.exception.BusinessException;
import com.c2b.coin.web.common.rest.bean.ResponseBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * BaseController
 *
 * @Auther: tangwei
 * @Date: 2018/5/24
 */
public class BaseController extends com.c2b.coin.web.common.rest.BaseController {

  private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

  private static ThreadContextMap threadContextMap;

  static {
    init();
  }

  static void init() {
    threadContextMap = null;
    if (threadContextMap == null) {
      threadContextMap = new ThreadContextMap();
    }
  }

  public static ThreadContextMap getThreadContextMap() {
    return threadContextMap;
  }

  @Autowired
  protected MessageSource messageSource;

  @ExceptionHandler(Exception.class)
  @ResponseBody
  public ResponseEntity<?> handleControllerException(Throwable ex) {
    ResponseBean result = new ResponseBean();
    if (ex instanceof BusinessException) {
      BusinessException be = (BusinessException) ex;
      result.setErrCode(be.getErrorMsgEnum().getCode());
      result.setErrMsg(messageSource.getMessage(be.getErrorMsgEnum().name(), null, LocaleContextHolder.getLocale()));
      LOGGER.error(result.getErrMsg() + "[" + result.getErrCode() + "]");
      return new ResponseEntity<>(result, HttpStatus.OK);
    } else {
      result.setErrCode(ErrorMsgEnum.SERVER_BUSY.getCode());
      result.setErrMsg(messageSource.getMessage(ErrorMsgEnum.SERVER_BUSY.name(), null, LocaleContextHolder.getLocale()));
      LOGGER.error(result.getErrMsg(), ex);
      return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

}
