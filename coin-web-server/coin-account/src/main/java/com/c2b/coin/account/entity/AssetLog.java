package com.c2b.coin.account.entity;

import java.math.BigDecimal;
import javax.persistence.*;

@Table(name = "asset_log")
public class AssetLog {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * user_id
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * user_name
     */
    private String username;

    /**
     * 平台订单号
     */
    @Column(name = "order_no")
    private String orderNo;

    /**
     * 三方订单号
     */
    @Column(name = "external_order_no")
    private String externalOrderNo;

    /**
     * 申请时间
     */
    private Long createtime;

    @Column(name="operation_type")
    private Integer operationType;
    
    /**
     * 操作币种
     */
    @Column(name = "currency_type")
    private Integer currencyType;

    
    @Column(name="currency_name")
    private String currencyName;
    /**
     * 操作数量
     */
    private BigDecimal asset;

    /**
     * 手续费
     */
    private BigDecimal poundage;

    /**
     * 实际到账
     */
    @Column(name = "actual_asset")
    private BigDecimal actualAsset;

    /**
     * 操作后数量
     */
    @Column(name = "after_asset")
    private BigDecimal afterAsset;

    /**
     * 地址
     */
    private String address;

    /**
     * 操作结果
     */
    private Integer result;

    /**
     * 三方返回结果
     */
    @Column(name = "result_result")
    private String resultResult;

    /**
     * 审核结果
     */
    @Column(name = "audit_result")
    private Integer auditResult;

    /**
     * 数据状态
     */
    private Integer status;

    /**
     * remark
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
     * 获取user_name
     *
     * @return username - user_name
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置user_name
     *
     * @param username user_name
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取平台订单号
     *
     * @return order_no - 平台订单号
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * 设置平台订单号
     *
     * @param orderNo 平台订单号
     */
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * 获取三方订单号
     *
     * @return external_order_no - 三方订单号
     */
    public String getExternalOrderNo() {
        return externalOrderNo;
    }

    /**
     * 设置三方订单号
     *
     * @param externalOrderNo 三方订单号
     */
    public void setExternalOrderNo(String externalOrderNo) {
        this.externalOrderNo = externalOrderNo;
    }

    /**
     * 获取申请时间
     *
     * @return createtime - 申请时间
     */
    public Long getCreatetime() {
        return createtime;
    }

    /**
     * 设置申请时间
     *
     * @param createtime 申请时间
     */
    public void setCreatetime(Long createtime) {
        this.createtime = createtime;
    }
    
    public Integer getOperationType() {
		return operationType;
	}

	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}

	/**
     * 获取操作币种
     *
     * @return currency_type - 操作币种
     */
    public Integer getCurrencyType() {
        return currencyType;
    }

    /**
     * 设置操作币种
     *
     * @param currencyType 操作币种
     */
    public void setCurrencyType(Integer currencyType) {
        this.currencyType = currencyType;
    }

    /**
     * 获取操作数量
     *
     * @return asset - 操作数量
     */
    public BigDecimal getAsset() {
        return asset;
    }

    /**
     * 设置操作数量
     *
     * @param asset 操作数量
     */
    public void setAsset(BigDecimal asset) {
        this.asset = asset;
    }

    /**
     * 获取手续费
     *
     * @return poundage - 手续费
     */
    public BigDecimal getPoundage() {
        return poundage;
    }

    /**
     * 设置手续费
     *
     * @param poundage 手续费
     */
    public void setPoundage(BigDecimal poundage) {
        this.poundage = poundage;
    }

    /**
     * 获取实际到账
     *
     * @return actual_asset - 实际到账
     */
    public BigDecimal getActualAsset() {
        return actualAsset;
    }

    /**
     * 设置实际到账
     *
     * @param actualAsset 实际到账
     */
    public void setActualAsset(BigDecimal actualAsset) {
        this.actualAsset = actualAsset;
    }

    /**
     * 获取操作后数量
     *
     * @return after_asset - 操作后数量
     */
    public BigDecimal getAfterAsset() {
        return afterAsset;
    }

    /**
     * 设置操作后数量
     *
     * @param afterAsset 操作后数量
     */
    public void setAfterAsset(BigDecimal afterAsset) {
        this.afterAsset = afterAsset;
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

    /**
     * 获取操作结果
     *
     * @return result - 操作结果
     */
    public Integer getResult() {
        return result;
    }

    /**
     * 设置操作结果
     *
     * @param result 操作结果
     */
    public void setResult(Integer result) {
        this.result = result;
    }

    /**
     * 获取三方返回结果
     *
     * @return result_result - 三方返回结果
     */
    public String getResultResult() {
        return resultResult;
    }

    /**
     * 设置三方返回结果
     *
     * @param resultResult 三方返回结果
     */
    public void setResultResult(String resultResult) {
        this.resultResult = resultResult;
    }

    /**
     * 获取审核结果
     *
     * @return audit_result - 审核结果
     */
    public Integer getAuditResult() {
        return auditResult;
    }

    /**
     * 设置审核结果
     *
     * @param auditResult 审核结果
     */
    public void setAuditResult(Integer auditResult) {
        this.auditResult = auditResult;
    }

    /**
     * 获取数据状态
     *
     * @return status - 数据状态
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置数据状态
     *
     * @param status 数据状态
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取remark
     *
     * @return remark - remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置remark
     *
     * @param remark remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}
    
}