package com.c2b.coin.sanchain.entity;

import javax.persistence.*;

@Table(name = "system_general_code")
public class SystemGeneralCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY,generator = "select uuid()")
    private String id;

    /**
     * 外键，关联t_code_group的id
     */
    @Column(name = "group_id")
    private String groupId;

    /**
     * 常量值
     */
    @Column(name = "code_value")
    private String codeValue;

    /**
     * 常量key
     */
    @Column(name = "code_name")
    private String codeName;

    /**
     * 描述
     */
    private String description;

    /**
     * 是否只读
     */
    private String readonly;

    /**
     * 上限
     */
    private String uplimit;

    /**
     * 下限
     */
    private String lowlimit;

    /**
     * 是否可用1-可用 0-不可用
     */
    private String remark1;

    /**
     * 扩展字段
     */
    private String remark2;

    /**
     * 扩展字段
     */
    private String remark3;

    /**
     * 扩展字段
     */
    private String remark4;

    /**
     * 扩展字段
     */
    private String remark5;

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取外键，关联t_code_group的id
     *
     * @return group_id - 外键，关联t_code_group的id
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * 设置外键，关联t_code_group的id
     *
     * @param groupId 外键，关联t_code_group的id
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * 获取常量值
     *
     * @return code_value - 常量值
     */
    public String getCodeValue() {
        return codeValue;
    }

    /**
     * 设置常量值
     *
     * @param codeValue 常量值
     */
    public void setCodeValue(String codeValue) {
        this.codeValue = codeValue;
    }

    /**
     * 获取常量key
     *
     * @return code_name - 常量key
     */
    public String getCodeName() {
        return codeName;
    }

    /**
     * 设置常量key
     *
     * @param codeName 常量key
     */
    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    /**
     * 获取描述
     *
     * @return description - 描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置描述
     *
     * @param description 描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取是否只读
     *
     * @return readonly - 是否只读
     */
    public String getReadonly() {
        return readonly;
    }

    /**
     * 设置是否只读
     *
     * @param readonly 是否只读
     */
    public void setReadonly(String readonly) {
        this.readonly = readonly;
    }

    /**
     * 获取上限
     *
     * @return uplimit - 上限
     */
    public String getUplimit() {
        return uplimit;
    }

    /**
     * 设置上限
     *
     * @param uplimit 上限
     */
    public void setUplimit(String uplimit) {
        this.uplimit = uplimit;
    }

    /**
     * 获取下限
     *
     * @return lowlimit - 下限
     */
    public String getLowlimit() {
        return lowlimit;
    }

    /**
     * 设置下限
     *
     * @param lowlimit 下限
     */
    public void setLowlimit(String lowlimit) {
        this.lowlimit = lowlimit;
    }

    /**
     * 获取是否可用1-可用 0-不可用
     *
     * @return remark1 - 是否可用1-可用 0-不可用
     */
    public String getRemark1() {
        return remark1;
    }

    /**
     * 设置是否可用1-可用 0-不可用
     *
     * @param remark1 是否可用1-可用 0-不可用
     */
    public void setRemark1(String remark1) {
        this.remark1 = remark1;
    }

    /**
     * 获取扩展字段
     *
     * @return remark2 - 扩展字段
     */
    public String getRemark2() {
        return remark2;
    }

    /**
     * 设置扩展字段
     *
     * @param remark2 扩展字段
     */
    public void setRemark2(String remark2) {
        this.remark2 = remark2;
    }

    /**
     * 获取扩展字段
     *
     * @return remark3 - 扩展字段
     */
    public String getRemark3() {
        return remark3;
    }

    /**
     * 设置扩展字段
     *
     * @param remark3 扩展字段
     */
    public void setRemark3(String remark3) {
        this.remark3 = remark3;
    }

    /**
     * 获取扩展字段
     *
     * @return remark4 - 扩展字段
     */
    public String getRemark4() {
        return remark4;
    }

    /**
     * 设置扩展字段
     *
     * @param remark4 扩展字段
     */
    public void setRemark4(String remark4) {
        this.remark4 = remark4;
    }

    /**
     * 获取扩展字段
     *
     * @return remark5 - 扩展字段
     */
    public String getRemark5() {
        return remark5;
    }

    /**
     * 设置扩展字段
     *
     * @param remark5 扩展字段
     */
    public void setRemark5(String remark5) {
        this.remark5 = remark5;
    }
}