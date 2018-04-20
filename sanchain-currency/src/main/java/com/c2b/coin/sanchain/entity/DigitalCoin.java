package com.c2b.coin.sanchain.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DigitalCoin {
    private Integer id;

    private String coinName;

    private String coinFullName;

    private Integer isEnabled;

    private Long createTime;

    private Long updateTime;

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
        this.coinName = coinName == null ? null : coinName.trim();
    }

    public String getCoinFullName() {
        return coinFullName;
    }

    public void setCoinFullName(String coinFullName) {
        this.coinFullName = coinFullName == null ? null : coinFullName.trim();
    }

    public Integer getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Integer isEnabled) {
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
        this.remark = remark == null ? null : remark.trim();
    }
}