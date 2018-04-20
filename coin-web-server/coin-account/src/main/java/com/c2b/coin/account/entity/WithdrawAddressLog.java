package com.c2b.coin.account.entity;

import javax.persistence.*;

@Table(name = "withdraw_address_log")
public class WithdrawAddressLog {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * user_id
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 币种
     */
    @Column(name = "currency_type")
    private Integer currencyType;

    /**
     * 地址
     */
    private String address;

    /**
     * 备注
     */
    private String remark;
    /**
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取user_id
     *
     * @return user_id - user_id
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置user_id
     *
     * @param userId user_id
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 获取币种
     *
     * @return currency_type - 币种
     */
    public Integer getCurrencyType() {
        return currencyType;
    }

    /**
     * 设置币种
     *
     * @param currencyType 币种
     */
    public void setCurrencyType(Integer currencyType) {
        this.currencyType = currencyType;
    }

    /**
     * 获取地址
     *
     * @return address - 地址
     */
    public String getAddress() {
        return address;
    }

    /**
     * 设置地址
     *
     * @param address 地址
     */
    public void setAddress(String address) {
        this.address = address;
    }

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
    
    
}