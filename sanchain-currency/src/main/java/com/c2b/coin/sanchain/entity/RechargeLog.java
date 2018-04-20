package com.c2b.coin.sanchain.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.*;

@Table(name = "ico_recharge_log")
public class RechargeLog {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY,generator = "select uuid()")
    private String id;

    /**
     * 来源地址
     */
    @Column(name = "from_address")
    private String fromAddress;

    /**
     * 系统编号
     */
    private String syscode;

    /**
     * 充值金额
     */
    private BigDecimal money;

    /**
     * 状态(0-待审核 1-已审核 2-已发送,3-已确认)
     */
    private String status;

    /**
     * 货币种类:1.比特币，2.以太坊，3.以太经典，4.莱特币
     */
    private String currency;

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
     * 操作账号
     */
    @Column(name = "by_account")
    private String byAccount;

    /**
     * 矿工费
     */
    private BigDecimal free;

    /**
     * 交易hash
     */
    @Column(name = "tx_hash")
    private String txHash;

    /**
     * 充值地址
     */
    @Column(name = "to_address")
    private String toAddress;

    /**
     * 充值账号
     */
    @Column(name = "to_account")
    private String toAccount;

    /**
     * 是否已归集 0未归集，1已归集
     */
    @Column(name = "is_concentrate")
    private String isConcentrate;

    /**
     * 归集币种类型（1.比特币，2.以太坊，3.以太经典，4.莱特币，5三界宝）
     */
    @Column(name = "concentrate_type")
    private String concentrateType;

    /**
     * 归集结果
     */
    @Column(name = "concentrate_msg")
    private String concentrateMsg;

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
     * 获取来源地址
     *
     * @return from_address - 来源地址
     */
    public String getFromAddress() {
        return fromAddress;
    }

    /**
     * 设置来源地址
     *
     * @param fromAddress 来源地址
     */
    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
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
     * 获取充值金额
     *
     * @return money - 充值金额
     */
    public BigDecimal getMoney() {
        return money;
    }

    /**
     * 设置充值金额
     *
     * @param money 充值金额
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
     * 获取货币种类:1.比特币，2.以太坊，3.以太经典，4.莱特币
     *
     * @return currency - 货币种类:1.比特币，2.以太坊，3.以太经典，4.莱特币
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * 设置货币种类:1.比特币，2.以太坊，3.以太经典，4.莱特币
     *
     * @param currency 货币种类:1.比特币，2.以太坊，3.以太经典，4.莱特币
     */
    public void setCurrency(String currency) {
        this.currency = currency;
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
     * 获取操作账号
     *
     * @return by_account - 操作账号
     */
    public String getByAccount() {
        return byAccount;
    }

    /**
     * 设置操作账号
     *
     * @param byAccount 操作账号
     */
    public void setByAccount(String byAccount) {
        this.byAccount = byAccount;
    }

    /**
     * 获取矿工费
     *
     * @return free - 矿工费
     */
    public BigDecimal getFree() {
        return free;
    }

    /**
     * 设置矿工费
     *
     * @param free 矿工费
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
     * 获取充值地址
     *
     * @return to_address - 充值地址
     */
    public String getToAddress() {
        return toAddress;
    }

    /**
     * 设置充值地址
     *
     * @param toAddress 充值地址
     */
    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    /**
     * 获取充值账号
     *
     * @return to_account - 充值账号
     */
    public String getToAccount() {
        return toAccount;
    }

    /**
     * 设置充值账号
     *
     * @param toAccount 充值账号
     */
    public void setToAccount(String toAccount) {
        this.toAccount = toAccount;
    }

    /**
     * 获取是否已归集 0未归集，1已归集
     *
     * @return is_concentrate - 是否已归集 0未归集，1已归集
     */
    public String getIsConcentrate() {
        return isConcentrate;
    }

    /**
     * 设置是否已归集 0未归集，1已归集
     *
     * @param isConcentrate 是否已归集 0未归集，1已归集
     */
    public void setIsConcentrate(String isConcentrate) {
        this.isConcentrate = isConcentrate;
    }

    /**
     * 获取归集币种类型（1.比特币，2.以太坊，3.以太经典，4.莱特币，5三界宝）
     *
     * @return concentrate_type - 归集币种类型（1.比特币，2.以太坊，3.以太经典，4.莱特币，5三界宝）
     */
    public String getConcentrateType() {
        return concentrateType;
    }

    /**
     * 设置归集币种类型（1.比特币，2.以太坊，3.以太经典，4.莱特币，5三界宝）
     *
     * @param concentrateType 归集币种类型（1.比特币，2.以太坊，3.以太经典，4.莱特币，5三界宝）
     */
    public void setConcentrateType(String concentrateType) {
        this.concentrateType = concentrateType;
    }

    /**
     * 获取归集结果
     *
     * @return concentrate_msg - 归集结果
     */
    public String getConcentrateMsg() {
        return concentrateMsg;
    }

    /**
     * 设置归集结果
     *
     * @param concentrateMsg 归集结果
     */
    public void setConcentrateMsg(String concentrateMsg) {
        this.concentrateMsg = concentrateMsg;
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