package com.c2b.wallet.service;


import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.c2b.wallet.mapper.SystemGeneralCodeMapper;

@Service
public class SysGencodeService {

    Logger log = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private SystemGeneralCodeMapper systemGeneralCodeMapper;
    
    public List<Map<String,String>> findByGroupCode(String groupCode) {
        return systemGeneralCodeMapper.findCode(groupCode);
    }
	
}
