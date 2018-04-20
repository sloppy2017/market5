package com.c2b.coin.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class PayPwd {
  @ApiModelProperty(name = "loginPwd", value = "登录密码", dataType = "string", required = false)
  private String loginPwd;
  @ApiModelProperty(name = "payPwd", value = "资金密码", dataType = "string", required = true)
  private String payPwd;
  @ApiModelProperty(name = "confirmPwd", value = "确认密码", dataType = "string", required = true)
  private String confirmPwd;

  public String getLoginPwd() {
    return loginPwd;
  }

  public void setLoginPwd(String loginPwd) {
    this.loginPwd = loginPwd;
  }

  public String getPayPwd() {
    return payPwd;
  }

  public void setPayPwd(String payPwd) {
    this.payPwd = payPwd;
  }

  public String getConfirmPwd() {
    return confirmPwd;
  }

  public void setConfirmPwd(String confirmPwd) {
    this.confirmPwd = confirmPwd;
  }
}
