package com.c2b.ethWallet.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.*;

@Table(name = "ico_gather_record")
public class IcoGatherRecord {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY,generator = "select uuid()")
    private String id;

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private String userId;

    /**
     * 归集金额
     */
    private BigDecimal amount;

    /**
     * 手续费
     */
    private BigDecimal cost;

    /**
     * 归集状态(0失败1成功2归集中)
     */
    @Column(name = "gather_status")
    private String gatherStatus;

    /**
     * 货币种类
     */
    private String currency;

    /**
     * 订单号
     */
    @Column(name = "order_no")
    private String orderNo;

    /**
     * 交易hash
     */
    private String hash;

    @Column(name = "from_address")
    private String fromAddress;

    @Column(name = "to_address")
    private String toAddress;

    private String nonce;

    /**
     * 区块hash
     */
    @Column(name = "block_hash")
    private String blockHash;

    /**
     * 区块号
     */
    @Column(name = "block_number")
    private String blockNumber;

    /**
     * 交易index
     */
    @Column(name = "transaction_index")
    private String transactionIndex;

    private String value;

    @Column(name = "gas_price")
    private String gasPrice;

    private String gas;

    private String input;

    private String creates;

    /**
     * 公钥
     */
    @Column(name = "public_key")
    private String publicKey;

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

    private String remark;

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
     * 获取用户id
     *
     * @return user_id - 用户id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 设置用户id
     *
     * @param userId 用户id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 获取归集金额
     *
     * @return amount - 归集金额
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * 设置归集金额
     *
     * @param amount 归集金额
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * 获取手续费
     *
     * @return cost - 手续费
     */
    public BigDecimal getCost() {
        return cost;
    }

    /**
     * 设置手续费
     *
     * @param cost 手续费
     */
    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    /**
     * 获取归集状态(0失败1成功2归集中)
     *
     * @return gather_status - 归集状态(0失败1成功2归集中)
     */
    public String getGatherStatus() {
        return gatherStatus;
    }

    /**
     * 设置归集状态(0失败1成功2归集中)
     *
     * @param gatherStatus 归集状态(0失败1成功2归集中)
     */
    public void setGatherStatus(String gatherStatus) {
        this.gatherStatus = gatherStatus;
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
     * 获取交易hash
     *
     * @return hash - 交易hash
     */
    public String getHash() {
        return hash;
    }

    /**
     * 设置交易hash
     *
     * @param hash 交易hash
     */
    public void setHash(String hash) {
        this.hash = hash;
    }

    /**
     * @return from_address
     */
    public String getFromAddress() {
        return fromAddress;
    }

    /**
     * @param fromAddress
     */
    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    /**
     * @return to_address
     */
    public String getToAddress() {
        return toAddress;
    }

    /**
     * @param toAddress
     */
    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    /**
     * @return nonce
     */
    public String getNonce() {
        return nonce;
    }

    /**
     * @param nonce
     */
    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    /**
     * 获取区块hash
     *
     * @return block_hash - 区块hash
     */
    public String getBlockHash() {
        return blockHash;
    }

    /**
     * 设置区块hash
     *
     * @param blockHash 区块hash
     */
    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    /**
     * 获取区块号
     *
     * @return block_number - 区块号
     */
    public String getBlockNumber() {
        return blockNumber;
    }

    /**
     * 设置区块号
     *
     * @param blockNumber 区块号
     */
    public void setBlockNumber(String blockNumber) {
        this.blockNumber = blockNumber;
    }

    /**
     * 获取交易index
     *
     * @return transaction_index - 交易index
     */
    public String getTransactionIndex() {
        return transactionIndex;
    }

    /**
     * 设置交易index
     *
     * @param transactionIndex 交易index
     */
    public void setTransactionIndex(String transactionIndex) {
        this.transactionIndex = transactionIndex;
    }

    /**
     * @return value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return gas_price
     */
    public String getGasPrice() {
        return gasPrice;
    }

    /**
     * @param gasPrice
     */
    public void setGasPrice(String gasPrice) {
        this.gasPrice = gasPrice;
    }

    /**
     * @return gas
     */
    public String getGas() {
        return gas;
    }

    /**
     * @param gas
     */
    public void setGas(String gas) {
        this.gas = gas;
    }

    /**
     * @return input
     */
    public String getInput() {
        return input;
    }

    /**
     * @param input
     */
    public void setInput(String input) {
        this.input = input;
    }

    /**
     * @return creates
     */
    public String getCreates() {
        return creates;
    }

    /**
     * @param creates
     */
    public void setCreates(String creates) {
        this.creates = creates;
    }

    /**
     * 获取公钥
     *
     * @return public_key - 公钥
     */
    public String getPublicKey() {
        return publicKey;
    }

    /**
     * 设置公钥
     *
     * @param publicKey 公钥
     */
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
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