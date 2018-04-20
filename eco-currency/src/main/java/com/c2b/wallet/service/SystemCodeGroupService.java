package com.c2b.wallet.service;


import com.c2b.wallet.entity.SystemCodeGroup;
import com.c2b.wallet.mapper.SystemCodeGroupMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SystemCodeGroupService {

    Logger log = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private SystemCodeGroupMapper systemCodeGroupMapper;
    
    public String insert(SystemCodeGroup systemCodeGroup){
    	return systemCodeGroupMapper.insertSystemCodeGroup(systemCodeGroup);
    }
	
    
    
}
