package com.c2b.ethWallet.service;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.c2b.ethWallet.entity.SystemCodeGroup;
import com.c2b.ethWallet.mapper.SystemCodeGroupMapper;

@Service
public class SystemCodeGroupService {

    Logger log = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private SystemCodeGroupMapper systemCodeGroupMapper;
    
    public int insert(SystemCodeGroup systemCodeGroup){
    	return systemCodeGroupMapper.insert(systemCodeGroup);
    }
	
    
    
}
