package com.c2b.coin.user.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="change_log")
public class ChangeLog
{

  @Id
  @GeneratedValue(generator="JDBC")
  private Long id;

  @Column(name="user_id")
  private Long userId;
  private String username;

  @Column(name="old_value")
  private String oldValue;

  @Column(name="new_value")
  private String newValue;
  private Integer type;
  private Long createtime;
  private String remark;

  @Column(name="change_result")
  private Integer changeResult;

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

  public String getOldValue()
  {
    return this.oldValue;
  }

  public void setOldValue(String oldValue)
  {
    this.oldValue = oldValue;
  }

  public String getNewValue()
  {
    return this.newValue;
  }

  public void setNewValue(String newValue)
  {
    this.newValue = newValue;
  }

  public Integer getType()
  {
    return this.type;
  }

  public void setType(Integer type)
  {
    this.type = type;
  }

  public Long getCreatetime()
  {
    return this.createtime;
  }

  public void setCreatetime(Long createtime)
  {
    this.createtime = createtime;
  }

  public String getRemark()
  {
    return this.remark;
  }

  public void setRemark(String remark)
  {
    this.remark = remark;
  }

  public Integer getChangeResult()
  {
    return this.changeResult;
  }

  public void setChangeResult(Integer changeResult)
  {
    this.changeResult = changeResult;
  }
}