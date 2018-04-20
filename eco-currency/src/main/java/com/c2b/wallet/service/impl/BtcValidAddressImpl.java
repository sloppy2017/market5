package com.c2b.wallet.service.impl;

import com.c2b.EcoWalletApplication;
import com.c2b.wallet.service.VerificationAddress;

import org.bitcoinj.core.Address;
import org.springframework.stereotype.Service;

@Service
public class BtcValidAddressImpl implements VerificationAddress{

    @Override
    public void isValidAddress(String address) {
        
        Address.fromBase58(EcoWalletApplication.params, address);
        
    }

}
