package com.c2b.coin.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class ForgetPwdDto {
  @ApiModelProperty(name = "username", value = "邮箱/手机", dataType = "string", required = true)
  private String username;

  @ApiModelProperty(name = "regiconCode", value = "手机区域码", dataType = "string", required = false)
  private String regiconCode;
  @ApiModelProperty(name = "validate", value = "二次验证信息", dataType = "string", required = true)
  private String validate;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getRegiconCode() {
    return regiconCode;
  }

  public void setRegiconCode(String regiconCode) {
    this.regiconCode = regiconCode;
  }

  public String getValidate() {
    return validate;
  }

  public void setValidate(String validate) {
    this.validate = validate;
  }
}
