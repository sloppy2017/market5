package com.c2b.coin.user.vo;

public class UserAccessVo {

  private Integer id;

  private Long userId;

  private String accessKeyId;

  private String allowIp;

  private String remark;

  private Long expireDate;

  private Long createTime;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getAccessKeyId() {
    return accessKeyId;
  }

  public void setAccessKeyId(String accessKeyId) {
    this.accessKeyId = accessKeyId;
  }

  public String getAllowIp() {
    return allowIp;
  }

  public void setAllowIp(String allowIp) {
    this.allowIp = allowIp;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public Long getExpireDate() {
    return expireDate;
  }

  public void setExpireDate(Long expireDate) {
    this.expireDate = expireDate;
  }

  public Long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Long createTime) {
    this.createTime = createTime;
  }

}
