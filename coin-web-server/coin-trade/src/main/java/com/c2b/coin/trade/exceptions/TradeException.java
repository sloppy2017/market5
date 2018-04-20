package com.c2b.coin.trade.exceptions;

import com.c2b.coin.common.enumeration.ErrorMsgEnum;

public class TradeException extends Exception {
  ErrorMsgEnum errorMsgEnum;
  public TradeException(final ErrorMsgEnum errorMsgEnum) {

    super(errorMsgEnum.getCode());
    this.errorMsgEnum = errorMsgEnum;
  }

  public ErrorMsgEnum getErrorMsgEnum() {
    return errorMsgEnum;
  }

  public void setErrorMsgEnum(ErrorMsgEnum errorMsgEnum) {
    this.errorMsgEnum = errorMsgEnum;
  }
}
