package com.c2b.coin.user.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "user_access")
public class UserAccess {

  @Id
  @GeneratedValue(generator = "JDBC")
  private Integer id;

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "access_key_id")
  private String accessKeyId;

  @Column(name = "access_key_secret")
  private String accessKeySecret;

  @Column(name = "allow_ip")
  private String allowIp;

  private String remark;

  @Column(name = "expire_date")
  private Long expireDate;

  @Column(name = "create_time")
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

  public String getAccessKeySecret() {
    return accessKeySecret;
  }

  public void setAccessKeySecret(String accessKeySecret) {
    this.accessKeySecret = accessKeySecret;
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
