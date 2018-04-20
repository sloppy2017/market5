package com.c2b.coin.user.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="user_reg_info")
public class UserRegInfo
{

  @Id
  @GeneratedValue(generator="JDBC")
  private Long id;

  @Column(name="user_id")
  private Long userId;
  private String username;
  private Long createtime;
  private String channel;
  private String terminal;

  @Column(name="device_id")
  private String deviceId;
  private String rom;

  @Column(name="rom_version")
  private String romVersion;
  private String ip;
  private String remark;

  @Column(name="invite_code")
  private String inviteCode;

  @Column(name="activiy_code")
  private Long activiyCode;

  @Column(name="app_version")
  private String appVersion;

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

  public Long getCreatetime()
  {
    return this.createtime;
  }

  public void setCreatetime(Long createtime)
  {
    this.createtime = createtime;
  }

  public String getChannel()
  {
    return this.channel;
  }

  public void setChannel(String channel)
  {
    this.channel = channel;
  }

  public String getTerminal()
  {
    return this.terminal;
  }

  public void setTerminal(String terminal)
  {
    this.terminal = terminal;
  }

  public String getDeviceId()
  {
    return this.deviceId;
  }

  public void setDeviceId(String deviceId)
  {
    this.deviceId = deviceId;
  }

  public String getRom()
  {
    return this.rom;
  }

  public void setRom(String rom)
  {
    this.rom = rom;
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

  public String getRemark()
  {
    return this.remark;
  }

  public void setRemark(String remark)
  {
    this.remark = remark;
  }

  public String getInviteCode()
  {
    return this.inviteCode;
  }

  public void setInviteCode(String inviteCode)
  {
    this.inviteCode = inviteCode;
  }

  public Long getActiviyCode()
  {
    return this.activiyCode;
  }

  public void setActiviyCode(Long activiyCode)
  {
    this.activiyCode = activiyCode;
  }

  public String getAppVersion()
  {
    return this.appVersion;
  }

  public void setAppVersion(String appVersion)
  {
    this.appVersion = appVersion;
  }
}