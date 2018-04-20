package com.c2b.coin.trade.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "marketing_activity_detail")
public class MarketingActivityDetail {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 营销活动名称
     */
    @Column(name = "activity_name")
    private String activityName;

    /**
     * 营销活动操作目标
     */
    @Column(name = "activity_target")
    private String activityTarget;

    /**
     * 活动id
     */
    @Column(name = "plan_id")
    private Integer planId;

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
     * 获取营销活动名称
     *
     * @return activity_name - 营销活动名称
     */
    public String getActivityName() {
        return activityName;
    }

    /**
     * 设置营销活动名称
     *
     * @param activityName 营销活动名称
     */
    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    /**
     * 获取营销活动操作目标
     *
     * @return activity_target - 营销活动操作目标
     */
    public String getActivityTarget() {
        return activityTarget;
    }

    /**
     * 设置营销活动操作目标
     *
     * @param activityTarget 营销活动操作目标
     */
    public void setActivityTarget(String activityTarget) {
        this.activityTarget = activityTarget;
    }

    /**
     * 获取活动id
     *
     * @return plan_id - 活动id
     */
    public Integer getPlanId() {
        return planId;
    }

    /**
     * 设置活动id
     *
     * @param planId 活动id
     */
    public void setPlanId(Integer planId) {
        this.planId = planId;
    }
}
