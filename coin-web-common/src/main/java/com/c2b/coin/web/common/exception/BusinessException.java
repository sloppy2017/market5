package com.c2b.coin.web.common.exception;

import com.c2b.coin.common.ParamMessageUtil;
import com.c2b.coin.common.enumeration.ErrorMsgEnum;

/**
 * 自定义业务异常(重写并直接返回this将无法返回异常栈的信息，但可以降低异常开销，所以该异常只用于返回特定的业务异常)
 *
 * @auther: tangwei
 * @date: 2018/5/18
 */
public class BusinessException extends RuntimeException {

  protected ErrorMsgEnum errorMsgEnum;

  public BusinessException(String message) {
    super(message);
  }

  public BusinessException(ErrorMsgEnum errorMsgEnum) {
    this.errorMsgEnum = errorMsgEnum;
  }

  public BusinessException(String message, Object... arguments) {
    super(ParamMessageUtil.format(message, arguments));
  }

  public ErrorMsgEnum getErrorMsgEnum() {
    return errorMsgEnum;
  }

  public void setErrorMsgEnum(ErrorMsgEnum errorMsgEnum) {
    this.errorMsgEnum = errorMsgEnum;
  }

  @Override
  public Throwable fillInStackTrace() {
    return this;
  }

  @Override
  public String toString() {
    String s = getClass().getName();
    String message;
    if (this.errorMsgEnum != null) {
      message = errorMsgEnum.getCode();
    } else {
      message = getLocalizedMessage();
    }
    return (message != null) ? (s + ": " + message) : s;
  }

}
