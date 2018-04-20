package com.c2b.coin.user.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="user_news")
public class UserNews
{

  @Id
  @GeneratedValue(generator="JDBC")
  private Long id;
  private Integer type;

  @Column(name="user_id")
  private Long userId;
  private String username;
  private String title;
  private String content;

  @Column(name="is_top")
  private Integer isTop;

  @Column(name="is_read")
  private Integer isRead;
  private Long createtime;

  @Column(name="is_del")
  private Integer isDel;
  private Integer status;

  public Long getId()
  {
    return this.id;
  }

  public void setId(Long id)
  {
    this.id = id;
  }

  public Integer getType()
  {
    return this.type;
  }

  public void setType(Integer type)
  {
    this.type = type;
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

  public String getTitle()
  {
    return this.title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public String getContent()
  {
    return this.content;
  }

  public void setContent(String content)
  {
    this.content = content;
  }

  public Integer getIsTop()
  {
    return this.isTop;
  }

  public void setIsTop(Integer isTop)
  {
    this.isTop = isTop;
  }

  public Integer getIsRead()
  {
    return this.isRead;
  }

  public void setIsRead(Integer isRead)
  {
    this.isRead = isRead;
  }

  public Long getCreatetime()
  {
    return this.createtime;
  }

  public void setCreatetime(Long createtime)
  {
    this.createtime = createtime;
  }

  public Integer getIsDel()
  {
    return this.isDel;
  }

  public void setIsDel(Integer isDel)
  {
    this.isDel = isDel;
  }

  public Integer getStatus()
  {
    return this.status;
  }

  public void setStatus(Integer status)
  {
    this.status = status;
  }
}