package com.c2b.coin.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class RegDto {
  @ApiModelProperty(name = "username", value = "用户名", dataType = "string", required = true)
  private String username;
  @ApiModelProperty(name = "password", value = "密码", dataType = "string", required = false)
  private String password;
  @ApiModelProperty(name = "confirmPwd", value = "确认密码", dataType = "string", required = false)
  private String confirmPwd;
  @ApiModelProperty(name = "inviteCode", value = "邀请码", dataType = "string", required = false)
  private String inviteCode;
  @ApiModelProperty(name = "smsCode", value = "手机注册短信验证码", dataType = "string", required = false)
  private String smsCode;
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

  public String getConfirmPwd() {
    return confirmPwd;
  }

  public void setConfirmPwd(String confirmPwd) {
    this.confirmPwd = confirmPwd;
  }

  public String getInviteCode() {
    return inviteCode;
  }

  public void setInviteCode(String inviteCode) {
    this.inviteCode = inviteCode;
  }

  public String getValidate() {
    return validate;
  }

  public void setValidate(String validate) {
    this.validate = validate;
  }

  public String getSmsCode() {
    return smsCode;
  }

  public void setSmsCode(String smsCode) {
    this.smsCode = smsCode;
  }
}
