package com.c2b.coin.trade.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "message_transfer_error")
public class MessageTransferError {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 消息内容
     */
    @Column(name = "message_text")
    private String messageText;

    /**
     * 消息类型 1-交易明细  2-订单最终状态
     */
    private Integer type;

    @Column(name = "create_time")
    private Long createTime;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取消息内容
     *
     * @return message_text - 消息内容
     */
    public String getMessageText() {
        return messageText;
    }

    /**
     * 设置消息内容
     *
     * @param messageText 消息内容
     */
    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    /**
     * 获取消息类型 1-交易明细  2-订单最终状态
     *
     * @return type - 消息类型 1-交易明细  2-订单最终状态
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置消息类型 1-交易明细  2-订单最终状态
     *
     * @param type 消息类型 1-交易明细  2-订单最终状态
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * @return create_time
     */
    public Long getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
