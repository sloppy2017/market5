package com.c2b.coin.user.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="login_log")
public class LoginLog
{

  @Id
  @GeneratedValue(generator="JDBC")
  private Long id;

  @Column(name="user_id")
  private Long userId;
  private String username;

  @Column(name="login_time")
  private Long loginTime;

  @Column(name="device_id")
  private String deviceId;
  private String brower;
  private String terminal;

  @Column(name="rom_version")
  private String romVersion;
  private String ip;

  @Column(name="login_address")
  private String loginAddress;
  private String remark;

  public Long getId()
  {
    return this.id;
  }

  public void setId(Long id)
  {
    this.id = id;
  }

  public Long getUserId()
  {
    return this.userId;
  }

  public void setUserId(Long userId)
  {
    this.userId = userId;
  }

  public String getUsername()
  {
    return this.username;
  }

  public void setUsername(String username)
  {
    this.username = username;
  }

  public Long getLoginTime()
  {
    return this.loginTime;
  }

  public void setLoginTime(Long loginTime)
  {
    this.loginTime = loginTime;
  }

  public String getDeviceId()
  {
    return this.deviceId;
  }

  public void setDeviceId(String deviceId)
  {
    this.deviceId = deviceId;
  }

  public String getBrower()
  {
    return this.brower;
  }

  public void setBrower(String brower)
  {
    this.brower = brower;
  }

  public String getTerminal()
  {
    return this.terminal;
  }

  public void setTerminal(String terminal)
  {
    this.terminal = terminal;
  }

  public String getRomVersion()
  {
    return this.romVersion;
  }

  public void setRomVersion(String romVersion)
  {
    this.romVersion = romVersion;
  }

  public String getIp()
  {
    return this.ip;
  }

  public void setIp(String ip)
  {
    this.ip = ip;
  }

  public String getLoginAddress()
  {
    return this.loginAddress;
  }

  public void setLoginAddress(String loginAddress)
  {
    this.loginAddress = loginAddress;
  }

  public String getRemark()
  {
    return this.remark;
  }

  public void setRemark(String remark)
  {
    this.remark = remark;
  }
}