package com.c2b.coin.user.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="user_operation_log")
public class UserOperationLog
{

  @Id
  @GeneratedValue(generator="JDBC")
  private Long id;
  private String username;

  @Column(name="user_id")
  private Long userId;
  private String operation;
  private String ua;
  private String ip;
  private String method;

  @Column(name="create_time")
  private Long createTime;
  private String param;
  private String address;

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

  public Long getUserId()
  {
    return this.userId;
  }

  public void setUserId(Long userId)
  {
    this.userId = userId;
  }

  public String getOperation()
  {
    return this.operation;
  }

  public void setOperation(String operation)
  {
    this.operation = operation;
  }

  public String getUa()
  {
    return this.ua;
  }

  public void setUa(String ua)
  {
    this.ua = ua;
  }

  public String getIp()
  {
    return this.ip;
  }

  public void setIp(String ip)
  {
    this.ip = ip;
  }

  public String getMethod()
  {
    return this.method;
  }

  public void setMethod(String method)
  {
    this.method = method;
  }

  public Long getCreateTime()
  {
    return this.createTime;
  }

  public void setCreateTime(Long createTime)
  {
    this.createTime = createTime;
  }

  public String getParam()
  {
    return this.param;
  }

  public void setParam(String param)
  {
    this.param = param;
  }

  public String getAddress() {
    return this.address;
  }

  public void setAddress(String address) {
    this.address = address;
  }
}