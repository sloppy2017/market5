package com.c2b.coin.user.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="user_info")
public class UserInfo
{

  @Id
  @GeneratedValue(generator="JDBC")
  private Long id;
  private String username;
  private String password;

  @Column(name="pay_pwd")
  private String payPwd;
  private String email;
  private String mobile;

  @Column(name="google_sk")
  private String googleSk;
  private Long createtime;

  @Column(name="two_valid")
  private Integer twoValid;

  @Column(name="user_status")
  private Integer userStatus;

  @Column(name="last_login_time")
  private Long lastLoginTime;

  @Column(name="is_auth")
  private Integer isAuth;

  @Column(name="auth_time")
  private Long authTime;

  @Column(name="last_update_time")
  private Long lastUpdateTime;

  @Column(name="reg_address")
  private String regAddress;
  private Integer status;
  private String remark;

  @Column(name="safety_level")
  private Integer safetyLevel;

  @Column(name="invite_code")
  private String inviteCode;

  @Column(name="google_bind_status")
  private Integer googleBindStatus;

  @Column(name="region_code")
  private String regionCode;

  public Long getId()
  {
    return this.id;
  }

  public void setId(Long id)
  {
    this.id = id;
  }

  public String getUsername()
  {
    return this.username;
  }

  public void setUsername(String username)
  {
    this.username = username;
  }

  public String getPassword()
  {
    return this.password;
  }

  public void setPassword(String password)
  {
    this.password = password;
  }

  public String getPayPwd()
  {
    return this.payPwd;
  }

  public void setPayPwd(String payPwd)
  {
    this.payPwd = payPwd;
  }

  public String getEmail()
  {
    return this.email;
  }

  public void setEmail(String email)
  {
    this.email = email;
  }

  public String getMobile()
  {
    return this.mobile;
  }

  public void setMobile(String mobile)
  {
    this.mobile = mobile;
  }

  public String getGoogleSk()
  {
    return this.googleSk;
  }

  public void setGoogleSk(String googleSk)
  {
    this.googleSk = googleSk;
  }

  public Long getCreatetime()
  {
    return this.createtime;
  }

  public void setCreatetime(Long createtime)
  {
    this.createtime = createtime;
  }

  public Integer getTwoValid()
  {
    return this.twoValid;
  }

  public void setTwoValid(Integer twoValid)
  {
    this.twoValid = twoValid;
  }

  public Integer getUserStatus()
  {
    return this.userStatus;
  }

  public void setUserStatus(Integer userStatus)
  {
    this.userStatus = userStatus;
  }

  public Long getLastLoginTime()
  {
    return this.lastLoginTime;
  }

  public void setLastLoginTime(Long lastLoginTime)
  {
    this.lastLoginTime = lastLoginTime;
  }

  public Integer getIsAuth()
  {
    return this.isAuth;
  }

  public void setIsAuth(Integer isAuth)
  {
    this.isAuth = isAuth;
  }

  public Long getAuthTime()
  {
    return this.authTime;
  }

  public void setAuthTime(Long authTime)
  {
    this.authTime = authTime;
  }

  public Long getLastUpdateTime()
  {
    return this.lastUpdateTime;
  }

  public void setLastUpdateTime(Long lastUpdateTime)
  {
    this.lastUpdateTime = lastUpdateTime;
  }

  public String getRegAddress()
  {
    return this.regAddress;
  }

  public void setRegAddress(String regAddress)
  {
    this.regAddress = regAddress;
  }

  public Integer getStatus()
  {
    return this.status;
  }

  public void setStatus(Integer status)
  {
    this.status = status;
  }

  public String getRemark()
  {
    return this.remark;
  }

  public void setRemark(String remark)
  {
    this.remark = remark;
  }

  public Integer getSafetyLevel()
  {
    return this.safetyLevel;
  }

  public void setSafetyLevel(Integer safetyLevel)
  {
    this.safetyLevel = safetyLevel;
  }

  public String getInviteCode()
  {
    return this.inviteCode;
  }

  public void setInviteCode(String inviteCode)
  {
    this.inviteCode = inviteCode;
  }

  public Integer getGoogleBindStatus()
  {
    return this.googleBindStatus;
  }

  public void setGoogleBindStatus(Integer googleBindStatus)
  {
    this.googleBindStatus = googleBindStatus;
  }

  public String getRegionCode() {
    return this.regionCode;
  }

  public void setRegionCode(String regionCode) {
    this.regionCode = regionCode;
  }
}