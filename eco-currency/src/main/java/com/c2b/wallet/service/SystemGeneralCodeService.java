package com.c2b.wallet.service;


import com.c2b.wallet.entity.SystemGeneralCode;
import com.c2b.wallet.mapper.SystemGeneralCodeMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SystemGeneralCodeService {

    Logger log = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private SystemGeneralCodeMapper systemGeneralCodeMapper;
    
    public List<Map<String,String>> findByGroupCode(String groupCode) {
        return systemGeneralCodeMapper.findCode(groupCode);
    }
    
    public int insert(SystemGeneralCode code){
    	return systemGeneralCodeMapper.insert(code);
    }
	
}
