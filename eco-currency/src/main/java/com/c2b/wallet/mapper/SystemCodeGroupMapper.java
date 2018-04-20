package com.c2b.wallet.mapper;

import com.c2b.wallet.entity.SystemCodeGroup;


public interface SystemCodeGroupMapper {
    int deleteByPrimaryKey(String id);

    int insert(SystemCodeGroup record);

    int insertSelective(SystemCodeGroup record);

    SystemCodeGroup selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(SystemCodeGroup record);

    int updateByPrimaryKey(SystemCodeGroup record);
    
    String insertSystemCodeGroup(SystemCodeGroup systemCodeGroup);
}