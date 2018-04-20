package com.c2b.coin.market.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "match_money")
public class MatchMoney {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    private String currency;

    /**
     * 时间
     */
    private Date time;

    /**
     * 价格
     */
    private BigDecimal money;

    private Date date;

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
     * @return currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * @param currency
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * 获取时间
     *
     * @return time - 时间
     */
    public Date getTime() {
        return time;
    }

    /**
     * 设置时间
     *
     * @param time 时间
     */
    public void setTime(Date time) {
        this.time = time;
    }

    /**
     * 获取价格
     *
     * @return money - 价格
     */
    public BigDecimal getMoney() {
        return money;
    }

    /**
     * 设置价格
     *
     * @param money 价格
     */
    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    /**
     * @return date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date
     */
    public void setDate(Date date) {
        this.date = date;
    }
}
