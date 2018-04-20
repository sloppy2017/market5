package com.c2b.coin.user.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "marketing_activity_plan")
public class MarketingActivityPlan {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 活动名称
     */
    @Column(name = "activity_name")
    private String activityName;

    /**
     * 活动开始时间
     */
    @Column(name = "start_time")
    private Date startTime;

    /**
     * 活动截止时间
     */
    @Column(name = "end_time")
    private Date endTime;

    /**
     * 新建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 活动是否有效 0 无效 1 有效
     */
    @Column(name = "is_effective")
    private Byte isEffective;

    /**
     * 获取主键
     *
     * @return id - 主键
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取活动名称
     *
     * @return activity_name - 活动名称
     */
    public String getActivityName() {
        return activityName;
    }

    /**
     * 设置活动名称
     *
     * @param activityName 活动名称
     */
    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    /**
     * 获取活动开始时间
     *
     * @return start_time - 活动开始时间
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * 设置活动开始时间
     *
     * @param startTime 活动开始时间
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * 获取活动截止时间
     *
     * @return end_time - 活动截止时间
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * 设置活动截止时间
     *
     * @param endTime 活动截止时间
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * 获取新建时间
     *
     * @return create_time - 新建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置新建时间
     *
     * @param createTime 新建时间
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
     * 获取活动是否有效 0 无效 1 有效
     *
     * @return is_effective - 活动是否有效 0 无效 1 有效
     */
    public Byte getIsEffective() {
        return isEffective;
    }

    /**
     * 设置活动是否有效 0 无效 1 有效
     *
     * @param isEffective 活动是否有效 0 无效 1 有效
     */
    public void setIsEffective(Byte isEffective) {
        this.isEffective = isEffective;
    }
}
