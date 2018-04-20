package com.c2b.coin.user.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="sms_log")
public class SmsLog
{

  @Id
  @GeneratedValue(generator="JDBC")
  private Long id;
  private String mobile;

  @Column(name="sms_type")
  private Integer smsType;
  private String content;
  private Integer status;

  @Column(name="return_msg")
  private String returnMsg;
  private String channel;

  public Long getId()
  {
    return this.id;
  }

  public void setId(Long id)
  {
    this.id = id;
  }

  public String getMobile()
  {
    return this.mobile;
  }

  public void setMobile(String mobile)
  {
    this.mobile = mobile;
  }

  public Integer getSmsType()
  {
    return this.smsType;
  }

  public void setSmsType(Integer smsType)
  {
    this.smsType = smsType;
  }

  public String getContent()
  {
    return this.content;
  }

  public void setContent(String content)
  {
    this.content = content;
  }

  public Integer getStatus()
  {
    return this.status;
  }

  public void setStatus(Integer status)
  {
    this.status = status;
  }

  public String getReturnMsg()
  {
    return this.returnMsg;
  }

  public void setReturnMsg(String returnMsg)
  {
    this.returnMsg = returnMsg;
  }

  public String getChannel()
  {
    return this.channel;
  }

  public void setChannel(String channel)
  {
    this.channel = channel;
  }
}