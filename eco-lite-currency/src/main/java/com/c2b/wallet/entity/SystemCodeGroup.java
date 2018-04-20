package com.c2b.wallet.entity;

import lombok.Data;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@ToString // 所有属性toString
@Data //设置所有属性的 get set 方法
//@NoArgsConstructor //无参构造方法
//@AllArgsConstructor // 全部参数构造方法
@JsonInclude(Include.NON_EMPTY)
public class SystemCodeGroup {
    private String id;

    private String groupCode;

    private String groupName;

    private String description;

    private String readonly;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode == null ? null : groupCode.trim();
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName == null ? null : groupName.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getReadonly() {
        return readonly;
    }

    public void setReadonly(String readonly) {
        this.readonly = readonly == null ? null : readonly.trim();
    }
}