package com.c2b.coin.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class GoogleBindDto {
  @ApiModelProperty(name = "code", value = "谷歌验证码", dataType = "string", required = true)
  private String code;
  @ApiModelProperty(name = "loginPwd", value = "登录密码", dataType = "string", required = true)
  private String password;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
