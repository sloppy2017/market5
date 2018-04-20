package com.c2b.coin.user.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="user_auth")
public class UserAuth
{

  @Id
  @GeneratedValue(generator="JDBC")
  private Long id;

  @Column(name="user_id")
  private Long userId;
  private String username;

  @Column(name="card_type")
  private Integer cardType;

  @Column(name="card_id")
  private String cardId;

  @Column(name="card_address")
  private String cardAddress;
  private Integer sex;

  @Column(name="first_name")
  private String firstName;

  @Column(name="last_name")
  private String lastName;
  private String region;

  @Column(name="img_url")
  private String imgUrl;
  private Long createtime;

  @Column(name="update_time")
  private Long updateTime;

  @Column(name="auth_status")
  private Integer authStatus;
  private String ip;
  private String operator;

  @Column(name="operator_id")
  private Long operatorId;
  private String remark;
  private String error;

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

  public Integer getCardType()
  {
    return this.cardType;
  }

  public void setCardType(Integer cardType)
  {
    this.cardType = cardType;
  }

  public String getCardId()
  {
    return this.cardId;
  }

  public void setCardId(String cardId)
  {
    this.cardId = cardId;
  }

  public String getCardAddress()
  {
    return this.cardAddress;
  }

  public void setCardAddress(String cardAddress)
  {
    this.cardAddress = cardAddress;
  }

  public Integer getSex()
  {
    return this.sex;
  }

  public void setSex(Integer sex)
  {
    this.sex = sex;
  }

  public String getFirstName()
  {
    return this.firstName;
  }

  public void setFirstName(String firstName)
  {
    this.firstName = firstName;
  }

  public String getLastName()
  {
    return this.lastName;
  }

  public void setLastName(String lastName)
  {
    this.lastName = lastName;
  }

  public String getRegion()
  {
    return this.region;
  }

  public void setRegion(String region)
  {
    this.region = region;
  }

  public String getImgUrl()
  {
    return this.imgUrl;
  }

  public void setImgUrl(String imgUrl)
  {
    this.imgUrl = imgUrl;
  }

  public Long getCreatetime()
  {
    return this.createtime;
  }

  public void setCreatetime(Long createtime)
  {
    this.createtime = createtime;
  }

  public Long getUpdateTime()
  {
    return this.updateTime;
  }

  public void setUpdateTime(Long updateTime)
  {
    this.updateTime = updateTime;
  }

  public Integer getAuthStatus()
  {
    return this.authStatus;
  }

  public void setAuthStatus(Integer authStatus)
  {
    this.authStatus = authStatus;
  }

  public String getIp()
  {
    return this.ip;
  }

  public void setIp(String ip)
  {
    this.ip = ip;
  }

  public String getOperator()
  {
    return this.operator;
  }

  public void setOperator(String operator)
  {
    this.operator = operator;
  }

  public Long getOperatorId()
  {
    return this.operatorId;
  }

  public void setOperatorId(Long operatorId)
  {
    this.operatorId = operatorId;
  }

  public String getRemark()
  {
    return this.remark;
  }

  public void setRemark(String remark)
  {
    this.remark = remark;
  }

  public String getError() {
    return this.error;
  }

  public void setError(String error) {
    this.error = error;
  }
}