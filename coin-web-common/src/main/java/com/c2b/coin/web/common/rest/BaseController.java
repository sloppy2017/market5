package com.c2b.coin.web.common.rest;

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
 * @author tangwei
 */
public class BaseController {

  protected static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

  @Autowired
  protected MessageSource messageSource;

	public ResponseBean onFailure() {
		return ResponseBean.onFailure();
	}

	public ResponseBean onFailure(ErrorMsgEnum errorMsgEnum) {
	  if (errorMsgEnum != null) {
	    return onFailure(errorMsgEnum.getCode(), messageSource.getMessage(ErrorMsgEnum.SERVER_BUSY.name(), null, LocaleContextHolder.getLocale()));
    }
		return onFailure();
	}

	public ResponseBean onFailure(String errCode, String errMsg) {
		return ResponseBean.onFailure(errCode, errMsg);
	}

  public ResponseBean onSuccess() {
    return onSuccess(null);
  }

	public <T> ResponseBean<T> onSuccess(T data) {
		return ResponseBean.onSuccess(data);
	}

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
