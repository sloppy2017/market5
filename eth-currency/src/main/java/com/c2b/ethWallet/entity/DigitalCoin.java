package com.c2b.ethWallet.entity;

import javax.persistence.*;

@Table(name = "digital_coin")
public class DigitalCoin {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 币种名称
     */
    @Column(name = "coin_name")
    private String coinName;

    /**
     * 币种全称
     */
    @Column(name = "coin_full_name")
    private String coinFullName;

    /**
     * 是否启用 1-启用  0-禁用
     */
    @Column(name = "is_enabled")
    private Integer isEnabled;

    @Column(name = "create_time")
    private Long createTime;

    @Column(name = "update_time")
    private Long updateTime;

    private String remark;

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
     * 获取币种名称
     *
     * @return coin_name - 币种名称
     */
    public String getCoinName() {
        return coinName;
    }

    /**
     * 设置币种名称
     *
     * @param coinName 币种名称
     */
    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    /**
     * 获取币种全称
     *
     * @return coin_full_name - 币种全称
     */
    public String getCoinFullName() {
        return coinFullName;
    }

    /**
     * 设置币种全称
     *
     * @param coinFullName 币种全称
     */
    public void setCoinFullName(String coinFullName) {
        this.coinFullName = coinFullName;
    }

    /**
     * 获取是否启用 1-启用  0-禁用
     *
     * @return is_enabled - 是否启用 1-启用  0-禁用
     */
    public Integer getIsEnabled() {
        return isEnabled;
    }

    /**
     * 设置是否启用 1-启用  0-禁用
     *
     * @param isEnabled 是否启用 1-启用  0-禁用
     */
    public void setIsEnabled(Integer isEnabled) {
        this.isEnabled = isEnabled;
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

    /**
     * @return update_time
     */
    public Long getUpdateTime() {
        return updateTime;
    }

    /**
     * @param updateTime
     */
    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * @return remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }
}