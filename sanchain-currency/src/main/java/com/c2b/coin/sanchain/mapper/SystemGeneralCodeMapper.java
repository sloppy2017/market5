package com.c2b.coin.sanchain.mapper;

import java.util.List;
import java.util.Map;

import com.c2b.coin.sanchain.entity.SystemGeneralCode;
import com.coin.config.mybatis.BaseMapper;



public interface SystemGeneralCodeMapper extends BaseMapper<SystemGeneralCode> {
    
    List<Map<String,String>> findCode(String groupCode);
    
}