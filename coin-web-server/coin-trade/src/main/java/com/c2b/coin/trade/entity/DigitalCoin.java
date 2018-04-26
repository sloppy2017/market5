package com.c2b.coin.trade.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

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
     * 币种简称
     */
    @Column(name = "coin_full_name")
    private String coinFullName;

    /**
     * 兑换汇率 （人民币：币种）
     */
    @Column(name = "coin_rate")
    private String coinRate;

    /**
     * 是否启用
     */
    @Column(name = "is_enabled")
    private Integer isEnabled;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

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

	public void setCoinFullName(String coinFullName) {
		this.coinFullName = coinFullName;
	}
    
    /**
     * 获取兑换汇率 （人民币：币种）
     *
     * @return coin_rate - 兑换汇率 （人民币：币种）
     */
    public String getCoinRate() {
        return coinRate;
    }

   
	/**
     * 设置兑换汇率 （人民币：币种）
     *
     * @param coinRate 兑换汇率 （人民币：币种）
     */
    public void setCoinRate(String coinRate) {
        this.coinRate = coinRate;
    }

    /**
     * 获取是否启用
     *
     * @return is_enabled - 是否启用
     */
    public Integer getIsEnabled() {
        return isEnabled;
    }

    /**
     * 设置是否启用
     *
     * @param isEnabled 是否启用
     */
    public void setIsEnabled(Integer isEnabled) {
        this.isEnabled = isEnabled;
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
