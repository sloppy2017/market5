package com.c2b.wallet.service.impl;

import com.c2b.util.EtherWalletUtil;

import org.springframework.stereotype.Service;

@Service
public class EthValidAddressImpl {

    /**
     * 
    * @Title: isValidAddress 
    * @Description: TODO(验证以太坊地址) 
    * @param @param address
    * @param @return    设定文件 
    * @return boolean    返回类型 
    * @throws 
    * @author Anne
     */
    public boolean isValidAddress(String address) {
        return EtherWalletUtil.isValidAddress(address);
    }

}
