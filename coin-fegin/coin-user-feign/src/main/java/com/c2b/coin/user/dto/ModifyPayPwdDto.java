package com.c2b.coin.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class ModifyPayPwdDto {
  @ApiModelProperty(name = "oldPwd", value = "旧密码", dataType = "string", required = true)
  private String oldPwd;
  @ApiModelProperty(name = "newPwd", value = "新密码", dataType = "string", required = true)
  private String newPwd;
  @ApiModelProperty(name = "confrimPwd", value = "确认密码", dataType = "string", required = true)
  private String confrimPwd;

  public String getOldPwd() {
    return oldPwd;
  }

  public void setOldPwd(String oldPwd) {
    this.oldPwd = oldPwd;
  }

  public String getNewPwd() {
    return newPwd;
  }

  public void setNewPwd(String newPwd) {
    this.newPwd = newPwd;
  }

  public String getConfrimPwd() {
    return confrimPwd;
  }

  public void setConfrimPwd(String confrimPwd) {
    this.confrimPwd = confrimPwd;
  }
}
