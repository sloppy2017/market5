package com.c2b.ethWallet.entity;

import java.util.Date;
import javax.persistence.*;

@Table(name = "user_coin")
public class UserCoin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY,generator = "select uuid()")
    private String id;

    private String account;

    private String password;

    /**
     * 币种类型（1.比特币，2.以太坊，3.以太经典，4.莱特币）
     */
    private String currency;

    /**
     * 钱包地址
     */
    private String address;

    @Column(name = "private_key")
    private String privateKey;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return account
     */
    public String getAccount() {
        return account;
    }

    /**
     * @param account
     */
    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取币种类型（1.比特币，2.以太坊，3.以太经典，4.莱特币）
     *
     * @return currency - 币种类型（1.比特币，2.以太坊，3.以太经典，4.莱特币）
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * 设置币种类型（1.比特币，2.以太坊，3.以太经典，4.莱特币）
     *
     * @param currency 币种类型（1.比特币，2.以太坊，3.以太经典，4.莱特币）
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * 获取钱包地址
     *
     * @return address - 钱包地址
     */
    public String getAddress() {
        return address;
    }

    /**
     * 设置钱包地址
     *
     * @param address 钱包地址
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return private_key
     */
    public String getPrivateKey() {
        return privateKey;
    }

    /**
     * @param privateKey
     */
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    /**
     * @return create_time
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return update_time
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * @param updateTime
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}