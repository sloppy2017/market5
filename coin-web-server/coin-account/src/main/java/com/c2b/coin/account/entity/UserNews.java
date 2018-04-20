package com.c2b.coin.account.entity;

import javax.persistence.*;

@Table(name = "user_news")
public class UserNews {
    /**
     * id
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * 消息类型
     */
    private Integer type;

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 消息title
     */
    private String title;

    /**
     * 消息content
     */
    private String content;

    /**
     * 是否置顶
     */
    @Column(name = "is_top")
    private Integer isTop;

    /**
     * 是否已读
     */
    @Column(name = "is_read")
    private Integer isRead;

    /**
     * 消息发送时间
     */
    private Long createtime;

    /**
     * 是否删除
     */
    @Column(name = "is_del")
    private Integer isDel;

    /**
     * 数据状态
     */
    private Integer status;

    /**
     * 获取id
     *
     * @return id - id
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置id
     *
     * @param id id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取消息类型
     *
     * @return type - 消息类型
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置消息类型
     *
     * @param type 消息类型
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * 获取用户id
     *
     * @return user_id - 用户id
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置用户id
     *
     * @param userId 用户id
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 获取用户名
     *
     * @return username - 用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名
     *
     * @param username 用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取消息title
     *
     * @return title - 消息title
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置消息title
     *
     * @param title 消息title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取消息content
     *
     * @return content - 消息content
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置消息content
     *
     * @param content 消息content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 获取是否置顶
     *
     * @return is_top - 是否置顶
     */
    public Integer getIsTop() {
        return isTop;
    }

    /**
     * 设置是否置顶
     *
     * @param isTop 是否置顶
     */
    public void setIsTop(Integer isTop) {
        this.isTop = isTop;
    }

    /**
     * 获取是否已读
     *
     * @return is_read - 是否已读
     */
    public Integer getIsRead() {
        return isRead;
    }

    /**
     * 设置是否已读
     *
     * @param isRead 是否已读
     */
    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    /**
     * 获取消息发送时间
     *
     * @return createtime - 消息发送时间
     */
    public Long getCreatetime() {
        return createtime;
    }

    /**
     * 设置消息发送时间
     *
     * @param createtime 消息发送时间
     */
    public void setCreatetime(Long createtime) {
        this.createtime = createtime;
    }

    /**
     * 获取是否删除
     *
     * @return is_del - 是否删除
     */
    public Integer getIsDel() {
        return isDel;
    }

    /**
     * 设置是否删除
     *
     * @param isDel 是否删除
     */
    public void setIsDel(Integer isDel) {
        this.isDel = isDel;
    }

    /**
     * 获取数据状态
     *
     * @return status - 数据状态
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置数据状态
     *
     * @param status 数据状态
     */
    public void setStatus(Integer status) {
        this.status = status;
    }
}