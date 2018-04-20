package com.c2b.coin.sanchain.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.*;

@Table(name = "ico_withdraw_log")
public class WithdrawLog {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY,generator = "select uuid()")
    private String id;

    /**
     * 用户账号
     */
    private String account;

    /**
     * 系统编号
     */
    private String syscode;

    /**
     * 提现金额
     */
    private BigDecimal money;

    /**
     * 状态(0-待审核 1-已审核 2-已发送,3-已确认)
     */
    private String status;

    /**
     * 货币种类
     */
    private String currency;

    /**
     * 提现地址
     */
    @Column(name = "to_address")
    private String toAddress;

    /**
     * 订单号
     */
    @Column(name = "order_no")
    private String orderNo;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 矿工费用
     */
    private BigDecimal free;

    /**
     * 交易hash
     */
    @Column(name = "tx_hash")
    private String txHash;

    /**
     * 提现json
     */
    @Column(name = "withdraw_msg")
    private String withdrawMsg;

    /**
     * 是否发送
     */
    @Column(name = "is_send")
    private String isSend;

    /**
     * 获取主键
     *
     * @return id - 主键
     */
    public String getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取用户账号
     *
     * @return account - 用户账号
     */
    public String getAccount() {
        return account;
    }

    /**
     * 设置用户账号
     *
     * @param account 用户账号
     */
    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * 获取系统编号
     *
     * @return syscode - 系统编号
     */
    public String getSyscode() {
        return syscode;
    }

    /**
     * 设置系统编号
     *
     * @param syscode 系统编号
     */
    public void setSyscode(String syscode) {
        this.syscode = syscode;
    }

    /**
     * 获取提现金额
     *
     * @return money - 提现金额
     */
    public BigDecimal getMoney() {
        return money;
    }

    /**
     * 设置提现金额
     *
     * @param money 提现金额
     */
    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    /**
     * 获取状态(0-待审核 1-已审核 2-已发送,3-已确认)
     *
     * @return status - 状态(0-待审核 1-已审核 2-已发送,3-已确认)
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置状态(0-待审核 1-已审核 2-已发送,3-已确认)
     *
     * @param status 状态(0-待审核 1-已审核 2-已发送,3-已确认)
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取货币种类
     *
     * @return currency - 货币种类
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * 设置货币种类
     *
     * @param currency 货币种类
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * 获取提现地址
     *
     * @return to_address - 提现地址
     */
    public String getToAddress() {
        return toAddress;
    }

    /**
     * 设置提现地址
     *
     * @param toAddress 提现地址
     */
    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    /**
     * 获取订单号
     *
     * @return order_no - 订单号
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * 设置订单号
     *
     * @param orderNo 订单号
     */
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * 获取创建时间
     *
     * @return create_time - 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取修改时间
     *
     * @return update_time - 修改时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置修改时间
     *
     * @param updateTime 修改时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 获取矿工费用
     *
     * @return free - 矿工费用
     */
    public BigDecimal getFree() {
        return free;
    }

    /**
     * 设置矿工费用
     *
     * @param free 矿工费用
     */
    public void setFree(BigDecimal free) {
        this.free = free;
    }

    /**
     * 获取交易hash
     *
     * @return tx_hash - 交易hash
     */
    public String getTxHash() {
        return txHash;
    }

    /**
     * 设置交易hash
     *
     * @param txHash 交易hash
     */
    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    /**
     * 获取提现json
     *
     * @return withdraw_msg - 提现json
     */
    public String getWithdrawMsg() {
        return withdrawMsg;
    }

    /**
     * 设置提现json
     *
     * @param withdrawMsg 提现json
     */
    public void setWithdrawMsg(String withdrawMsg) {
        this.withdrawMsg = withdrawMsg;
    }

    /**
     * 获取是否发送
     *
     * @return is_send - 是否发送
     */
    public String getIsSend() {
        return isSend;
    }

    /**
     * 设置是否发送
     *
     * @param isSend 是否发送
     */
    public void setIsSend(String isSend) {
        this.isSend = isSend;
    }
}