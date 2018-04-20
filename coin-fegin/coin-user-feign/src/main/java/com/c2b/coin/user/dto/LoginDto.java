package com.c2b.coin.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class LoginDto {
  @ApiModelProperty(name = "username", value = "用户名", dataType = "string", required = true)
  private String username;
  @ApiModelProperty(name = "password", value = "密码", dataType = "string", required = true)
  private String password;
  @ApiModelProperty(name = "validate", value = "二次验证信息", dataType = "string", required = true)
  private String validate;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }


  public String getValidate() {
    return validate;
  }

  public void setValidate(String validate) {
    this.validate = validate;
  }
}
