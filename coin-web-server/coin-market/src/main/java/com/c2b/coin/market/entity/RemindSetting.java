package com.c2b.coin.market.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Table(name = "remind_setting")
public class RemindSetting {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * 设备id
     */
    @Column(name = "device_id")
    private String deviceId;

    /**
     * 上限
     */
    @Column(name = "upper_limit")
    private BigDecimal upperLimit;

    /**
     * 下限
     */
    @Column(name = "lower_limit")
    private BigDecimal lowerLimit;

    /**
     * 系统
     */
    private Integer system;

    /**
     * user_id
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 状态
     */
    private Integer status;

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
     * 获取设备id
     *
     * @return device_id - 设备id
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * 设置设备id
     *
     * @param deviceId 设备id
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * 获取上限
     *
     * @return upper_limit - 上限
     */
    public BigDecimal getUpperLimit() {
        return upperLimit;
    }

    /**
     * 设置上限
     *
     * @param upperLimit 上限
     */
    public void setUpperLimit(BigDecimal upperLimit) {
        this.upperLimit = upperLimit;
    }

    /**
     * 获取下限
     *
     * @return lower_limit - 下限
     */
    public BigDecimal getLowerLimit() {
        return lowerLimit;
    }

    /**
     * 设置下限
     *
     * @param lowerLimit 下限
     */
    public void setLowerLimit(BigDecimal lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    /**
     * 获取系统
     *
     * @return system - 系统
     */
    public Integer getSystem() {
        return system;
    }

    /**
     * 设置系统
     *
     * @param system 系统
     */
    public void setSystem(Integer system) {
        this.system = system;
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
     * 获取状态
     *
     * @return status - 状态
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置状态
     *
     * @param status 状态
     */
    public void setStatus(Integer status) {
        this.status = status;
    }
}
