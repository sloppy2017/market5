package com.c2b.wallet.service;

import java.math.BigDecimal;
import java.util.Date;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.Wallet.BalanceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.c2b.EcoWalletApplication;
import com.c2b.config.CoreMessageSource;
import com.c2b.constant.Constant;
import com.c2b.exception.RuntimeServiceException;
import com.c2b.util.AES;
import com.c2b.util.OrderGenerater;
import com.c2b.wallet.entity.UserCoin;
import com.c2b.wallet.entity.WithdrawLog;
import com.c2b.wallet.mapper.UserCoinMapper;
import com.c2b.wallet.mapper.WithdrawLogMapper;
import com.c2b.wallet.util.WalletConstant;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

@Service
public class EcoWalletService {
    
    Logger log = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private CoreMessageSource messageSource;
    
    @Autowired
    WithdrawLogMapper withdrawLogMapper;
    
    @Autowired
    UserCoinMapper userCoinMapper;
    

    /**
     * 检查该用户是否已存在钱包地址
     * @param userName
     */
    private void checkExistAddress(String account){
        UserCoin userCoin = userCoinMapper.getUserCoinByAccountAndCurrency(account,Constant.CURRENCY_BTC);
        if (userCoin != null ) {
            throw new RuntimeServiceException(messageSource.getMessage("existAddress", userCoin.getAddress()));
        }
        
    }

    /**
     * 创建钱包地址
     * @param params params // First we configure the network we want to use.
        // The available options are:
        <p> - MainNetParams
        <p> - TestNet3Params
        <p> - RegTestParams
     * @param userName 用户名
     * @param passWord 钱包的支付密码
     * @return 钱包的地址
     */
    public String createWallet(NetworkParameters params, String account){
        
        checkExistAddress(account);
        
        ECKey key = new ECKey();
        
        Address address = key.toAddress(params);
        log.info("address = " + address.toString() + ",key=" + key.getPrivateKeyEncoded(params));
        
        UserCoin userCoin = new UserCoin();
        userCoin.setAccount(account);
        userCoin.setAddress(address.toBase58());
        userCoin.setCurrency(Constant.CURRENCY_BTC);
        userCoin.setCreateTime(new Date());
        userCoin.setPrivateKey(AES.encrypt(key.getPrivateKeyEncoded(params).toString().getBytes()));
        EcoWalletApplication.kit.wallet().importKey(key);
        userCoinMapper.insertSelective(userCoin);
        return address.toBase58();
    }
    
    /**
     * 查看钱包所有信息
     * @return
     */
    public Wallet dumpWallet(){
        return EcoWalletApplication.kit.wallet();
    }
    
    /**
     * 提现
     * @param withdrawLog
     */
    @Transactional
    public synchronized String  sendMoney(NetworkParameters params, WithdrawLog withdrawLog){

        /**
         * 
         * 1、判断用户账号是否存在。
         * 2、提币，记录提币信息
         * 3、正常返回hash，异常返回null
         * 
         */
    	UserCoin userCoin = userCoinMapper.getUserCoinByAccountAndCurrency(withdrawLog.getAccount(),Constant.CURRENCY_BTC);
    	if (userCoin == null){
            throw new RuntimeServiceException("用户信息异常。");//参数错误
        }
        
        //提现申请时，已做过判断，并且扣除了用户的比特币，所以这里不能再做判断   （by add zhangchunming）  
        /*if(record.getMoney().compareTo(userWallet.getBtcAmnt()) > 0){//余额不足
            throw new RuntimeServiceException(messageSource.getMessage("balanceError"));
        }*/
        
        WalletAppKit kit = EcoWalletApplication.kit;

        log.info("Send money to: " + kit.wallet().currentReceiveAddress().toString());

        Address to = Address.fromBase58(params, withdrawLog.getToAddress());

        Coin coin = Coin.parseCoin(withdrawLog.getMoney().toPlainString());
        
        try {
//            SendRequest req = SendRequest.to(to, coin);
//            req.feePerKb = Coin.parseCoin("0.0005");
//            Wallet.SendResult result = kit.wallet().sendCoins(kit.peerGroup(), req);
//            Transaction createdTx = result.tx;
            
            Wallet.SendResult result = kit.wallet().sendCoins(kit.peerGroup(), to, coin);
            log.info("coins sent. transaction hash: " + result.tx.getHashAsString());
            WithdrawLog twithdrawLog = new WithdrawLog();
//            String orderNo =OrderGenerater.generateOrderNo();
            twithdrawLog.setOrderNo(withdrawLog.getOrderNo());
        	twithdrawLog.setAccount(withdrawLog.getAccount());
            twithdrawLog.setToAddress(withdrawLog.getToAddress());
            twithdrawLog.setMoney(withdrawLog.getMoney());
            twithdrawLog.setCurrency(Constant.CURRENCY_BTC);
        	twithdrawLog.setCreateTime(new Date());
            twithdrawLog.setTxHash(result.tx.getHashAsString());
            twithdrawLog.setStatus(WalletConstant.SEND);
            twithdrawLog.setFree(new BigDecimal(result.tx.getFee().toPlainString()));
            withdrawLogMapper.insertSelective(twithdrawLog);
            return result.tx.getHashAsString();
            // you can use a block explorer like https://www.biteasy.com/ to inspect the transaction with the printed transaction hash. 
        } catch (InsufficientMoneyException e) {
            log.info("Not enough coins in your wallet. Missing " + e.missing.getValue() + " satoshis are missing (including fees)");
            log.info("Send money to: " + kit.wallet().currentReceiveAddress().toString());

            // Bitcoinj allows you to define a BalanceFuture to execute a callback once your wallet has a certain balance.
            // Here we wait until the we have enough balance and display a notice.
            // Bitcoinj is using the ListenableFutures of the Guava library. Have a look here for more information: https://github.com/google/guava/wiki/ListenableFutureExplained
            ListenableFuture<Coin> balanceFuture = kit.wallet().getBalanceFuture(coin, BalanceType.AVAILABLE);
            FutureCallback<Coin> callback = new FutureCallback<Coin>() {
                @Override
                public void onSuccess(Coin balance) {
                    log.info("coins arrived and the wallet now has enough balance");
                }

                @Override
                public void onFailure(Throwable t) {
                    log.info("something went wrong");
                }
            };
            Futures.addCallback(balanceFuture, callback);
        }
        return null;
        // shutting down 
//        kit.stopAsync();
//        kit.awaitTerminated();
    
    }
    
    
    
}
