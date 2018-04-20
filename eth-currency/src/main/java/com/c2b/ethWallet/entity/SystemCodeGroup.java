package com.c2b.ethWallet.entity;

import javax.persistence.*;

@Table(name = "system_code_group")
public class SystemCodeGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY,generator = "select uuid()")
    private String id;

    /**
     * 英文key
     */
    @Column(name = "group_code")
    private String groupCode;

    /**
     * 中文名称
     */
    @Column(name = "group_name")
    private String groupName;

    /**
     * 中文描述
     */
    private String description;

    /**
     * 是否只读，暂时不用
     */
    private String readonly;

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
     * 获取英文key
     *
     * @return group_code - 英文key
     */
    public String getGroupCode() {
        return groupCode;
    }

    /**
     * 设置英文key
     *
     * @param groupCode 英文key
     */
    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    /**
     * 获取中文名称
     *
     * @return group_name - 中文名称
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * 设置中文名称
     *
     * @param groupName 中文名称
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * 获取中文描述
     *
     * @return description - 中文描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置中文描述
     *
     * @param description 中文描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取是否只读，暂时不用
     *
     * @return readonly - 是否只读，暂时不用
     */
    public String getReadonly() {
        return readonly;
    }

    /**
     * 设置是否只读，暂时不用
     *
     * @param readonly 是否只读，暂时不用
     */
    public void setReadonly(String readonly) {
        this.readonly = readonly;
    }
}