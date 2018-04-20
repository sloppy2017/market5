package com.c2b.coin.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class ResetPwdDto {
  @ApiModelProperty(name = "password", value = "密码", dataType = "string", required = true)
  private String password;
  @ApiModelProperty(name = "confirmPwd", value = "确认密码", dataType = "string", required = true)
  private String confirmPwd;
  @ApiModelProperty(name = "token", value = "token", dataType = "string", required = true)
  private String token;

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getConfirmPwd() {
    return confirmPwd;
  }

  public void setConfirmPwd(String confirmPwd) {
    this.confirmPwd = confirmPwd;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
