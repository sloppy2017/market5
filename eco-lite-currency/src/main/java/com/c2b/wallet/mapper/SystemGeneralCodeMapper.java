package com.c2b.wallet.mapper;

import java.util.List;
import java.util.Map;

import com.c2b.wallet.entity.SystemGeneralCode;

public interface SystemGeneralCodeMapper {
    int deleteByPrimaryKey(String id);

    int insert(SystemGeneralCode record);

    int insertSelective(SystemGeneralCode record);

    SystemGeneralCode selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(SystemGeneralCode record);

    int updateByPrimaryKey(SystemGeneralCode record);
    
    List<Map<String,String>> findCode(String groupCode);
    
}