package com.c2b.coin.account.entity;

import javax.persistence.*;

@Table(name = "digital_coin")
public class DigitalCoin {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;
    
    @Column(name = "coin_name")
    private String coinName;
    
    @Column(name = "coin_full_name")
    private String coinFullName;
    
    @Column(name = "is_enabled")
    private int isEnabled;
    
    @Column(name = "create_time")
    private Long createTime;
    
    @Column(name = "update_time")
    private Long updateTime;
    
    @Column(name = "remark")
    private String remark;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCoinName() {
		return coinName;
	}

	public void setCoinName(String coinName) {
		this.coinName = coinName;
	}

	public String getCoinFullName() {
		return coinFullName;
	}

	public void setCoinFullName(String coinFullName) {
		this.coinFullName = coinFullName;
	}

	
	public int getIsEnabled() {
		return isEnabled;
	}

	public void setIsEnabled(int isEnabled) {
		this.isEnabled = isEnabled;
	}

	

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
    
    
    
   
}