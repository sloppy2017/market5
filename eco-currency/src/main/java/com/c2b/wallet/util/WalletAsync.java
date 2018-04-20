package com.c2b.wallet.util;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.core.listeners.TransactionConfidenceEventListener;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.KeyChainEventListener;
import org.bitcoinj.wallet.listeners.ScriptsChangeEventListener;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;
import org.bitcoinj.wallet.listeners.WalletCoinsSentEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONObject;
import com.c2b.EcoWalletApplication;
import com.c2b.constant.Constant;
import com.c2b.exception.RuntimeServiceException;
import com.c2b.util.AES;
import com.c2b.util.OrderGenerater;
import com.c2b.wallet.entity.DigitalCoin;
import com.c2b.wallet.entity.RechargeLog;
import com.c2b.wallet.entity.UserCoin;
import com.c2b.wallet.entity.WithdrawLog;
import com.c2b.wallet.mapper.DigitalCoinMapper;
import com.c2b.wallet.mapper.RechargeLogMapper;
import com.c2b.wallet.mapper.UserCoinMapper;
import com.c2b.wallet.mapper.WithdrawLogMapper;
import com.c2b.wallet.service.DepositService;
import com.c2b.wallet.service.EcoWalletService;
import com.c2b.wallet.service.WithDrawService;

@Component
public class WalletAsync implements Runnable{
    
    static Logger log = LoggerFactory.getLogger(WalletAsync.class);
    
    @Value("${wallet.privateKey}")
    String privateKey;
    
    @Autowired
    private EcoWalletService ecoWalletService;
    
    @Resource
    private UserCoinMapper userCoinMapper;
    @Resource
    private RechargeLogMapper rechargeLogMapper;
    
    @Resource
    private WithdrawLogMapper withdrawLogMapper;
    
    @Autowired
    private WithDrawService withDrawService;
    
    @Autowired
    private DepositService depositService;
    
    @Autowired
    private DigitalCoinMapper digitalCoinMapper;
    
    
    
    public WalletAsync() {
    }
    
    @Override
    public void run() {
        // Now we initialize a new WalletAppKit. The kit handles all the boilerplate for us and is the easiest way to get everything up and running.
        // Have a look at the WalletAppKit documentation and its source to understand what's happening behind the scenes: https://github.com/bitcoinj/bitcoinj/blob/master/core/src/main/java/org/bitcoinj/kits/WalletAppKit.java
        

        // In case you want to connect with your local bitcoind tell the kit to connect to localhost.
        // You must do that in reg test mode.
        //kit.connectToLocalHost();

        // Now we start the kit and sync the blockchain.
        // bitcoinj is working a lot with the Google Guava libraries. The WalletAppKit extends the AbstractIdleService. Have a look at the introduction to Guava services: https://github.com/google/guava/wiki/ServiceExplained
        //获取key
        List<ECKey> ecKeys = EcoWalletApplication.kit.wallet().getImportedKeys();
        
        if(ecKeys == null || ecKeys.size() == 0){
            log.info("--------------init key---------");
            //通过私钥获取key并导入钱包wallet
            ECKey key;
            DumpedPrivateKey dumpedPrivateKey = DumpedPrivateKey.fromBase58(EcoWalletApplication.params, 
                    AES.decrypt(privateKey) );
            key = dumpedPrivateKey.getKey();
            EcoWalletApplication.kit.wallet().importKey(key);
        }
        log.info(" keys == " + EcoWalletApplication.kit.wallet().getImportedKeys().size());
        //收币监听器
        EcoWalletApplication.kit.wallet().addCoinsReceivedEventListener(new WalletCoinsReceivedEventListener() {
            @Override
            public void onCoinsReceived(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
                log.info("addCoinsReceivedEventListener getValueSentToMe=="+  tx.getValueSentToMe(wallet) +",free="+ tx.getFee());
                log.info("addCoinsReceivedEventListener getValueSentFromMe=="+ tx.getValueSentFromMe(wallet));
                log.info("--------> coins resceived: " + tx.getHashAsString() + ", value = " + tx.getValue(wallet));
                    
                if (tx.getValue(wallet).compareTo(Coin.valueOf(10000)) == -1) {
                    log.info("receive coin balance is less than 10000sato, do not accept.");
                    log.info("tx:" + tx.getHashAsString());
                    return;
                }
                
                for (TransactionOutput txop : tx.getOutputs()){
                    Coin value = txop.getValue();
                    if (value.compareTo(Coin.valueOf(10000)) == -1) {
                        log.info("receive txo is less than 10000sato, do not accept.");
                        log.info("tx:" + tx.getHashAsString() + ", tx_output:" + txop.getIndex());
                        return;
                    }
                    String address = txop.getScriptPubKey().getToAddress(EcoWalletApplication.params).toString();
                    log.info("Received address= " + address);
                    if (txop.isMine(wallet)){
                    	UserCoin userCoin = userCoinMapper.getUserCoinByAddress(address);
                        
                        if (userCoin==null){
                            throw new RuntimeServiceException("用户信息不存在");
                        }
                        
                        /*RechargeRecord record = new RechargeRecord();
                        String orderNo =OrderGenerater.generateOrderNo();
                        record.setByUserId(userId);
                        record.setOrderNo(orderNo);
                        Coin sendToMe = tx.getValueSentToMe(wallet);
                        Coin fee = tx.getFee();
                        if (fee == null){
                            fee = Coin.valueOf(0);
                        }
                        record.setUserId(userId);
                        record.setMoney(new BigDecimal(sendToMe.toPlainString()));
                        record.setFree(new BigDecimal (fee.toPlainString()));
                        record.setStatus(WalletConstant.SEND);
                        record.setCurrency("BTC");
                        record.setCreateTime(new Date());
                        record.setTxHash(tx.getHashAsString());
                        record.setAddress(address);
                        //增加充值记录
                        rechargeRecordService.insertRechargeRecord(record);*/
                        RechargeLog rechargeLog = new RechargeLog();
                        String orderNo =OrderGenerater.generateOrderNo();
                        rechargeLog.setOrderNo(orderNo);
                        rechargeLog.setToAccount(userCoin.getAccount());
                        rechargeLog.setToAddress(address);
                        rechargeLog.setFromAddress(null);
                        rechargeLog.setCurrency(Constant.CURRENCY_BTC);
                        Coin sendToMe = tx.getValueSentToMe(wallet);
                        Coin fee = tx.getFee();
                        if (fee == null){
                            fee = Coin.valueOf(0);
                        }
                        rechargeLog.setMoney(new BigDecimal(sendToMe.toPlainString()));
                        rechargeLog.setFree(new BigDecimal (fee.toPlainString()));
                        rechargeLog.setStatus(WalletConstant.SEND);
                        rechargeLog.setCreateTime(new Date());
                        rechargeLog.setTxHash(tx.getHashAsString());
                        rechargeLogMapper.insertSelective(rechargeLog);
                        log.info(address + ",addCoinsReceive money===" + txop.getValue().toFriendlyString());
                        //回调
                        try {
							DigitalCoin digitalCoin = digitalCoinMapper.selectDigitalCoinByCoinName(Constant.CURRENCY_BTC);
							depositService.broadcastCallback(rechargeLog.getToAddress(), digitalCoin.getId(), rechargeLog.getToAccount(), tx.getHashAsString(), rechargeLog.getMoney());
						} catch (Exception e) {
							log.error("-------BTC充币广播回调发生异常，address="+rechargeLog.getToAddress()+",money="+rechargeLog.getMoney()+",account="+rechargeLog.getToAccount());
							e.printStackTrace();
						}
                    }else{
                        log.info("=========not me======");
                    }
                }
            }
                
        });
        //发币监听器
        EcoWalletApplication.kit.wallet().addCoinsSentEventListener(new WalletCoinsSentEventListener() {
            @Override
            public void onCoinsSent(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
                log.info("addCoinsSentEventListener getValueSentToMe=="+  tx.getValueSentToMe(wallet));
                log.info("addCoinsSentEventListener getValueSentFromMe=="+ tx.getValueSentFromMe(wallet) + ",free= " + tx.getFee());
                log.info("--------> coins sent: " + tx.getHashAsString());
                log.info("coins sent");
                for (TransactionInput txip : tx.getInputs()){
                    log.info(" TransactionInput：{} " , txip);
                }
                
            }
        });

        EcoWalletApplication.kit.wallet().addKeyChainEventListener(new KeyChainEventListener() {
            @Override
            public void onKeysAdded(List<ECKey> keys) {
                log.info("new key added");
            }
        });

        EcoWalletApplication.kit.wallet().addScriptsChangeEventListener(new ScriptsChangeEventListener() {
            @Override
            public void onScriptsChanged(Wallet wallet, List<Script> scripts, boolean isAddingScripts) {
                log.info("new script added");
            }
        });
        //交易确认监听器
        EcoWalletApplication.kit.wallet().addTransactionConfidenceEventListener(new TransactionConfidenceEventListener() {
            @Override
            public void onTransactionConfidenceChanged(Wallet wallet, Transaction tx) {
                log.info("Depth =: " + tx.getConfidence().getDepthInBlocks());
                log.info("Peers =: " + tx.getConfidence().numBroadcastPeers());
                
                int depth = tx.getConfidence().getDepthInBlocks();
                
                
                //在真实BTC网络里,1600个区块
                log.info("--------depth--{}",depth);
                if (depth == 0 || depth > 1600){
                    //do nothing if confirmed time is zero
                    //100次确认之内的交易都有重新处理的机会
                    log.info("==========depth return");
                    return;
                }
                
                log.info("confidence changed: " + tx.getHashAsString() + ", new block depth: " + depth);
                
//                //交易确认   金额<1是3个，<10是4个，<100是5个。剩余6个
//                int peers = tx.getConfidence().numBroadcastPeers();
//                if (peers < 3){
//                    log.info("==========peers return");
//                    return;
//                }
                
                //TODO 通过hash 取充值提现记录，更新状态
                for (TransactionOutput txop : tx.getOutputs()){
                    
                    if (txop.isMine(wallet)){
                        String address = txop.getScriptPubKey().getToAddress(EcoWalletApplication.params).toString();
                        log.info("record ----address:{} ,tx :{},value:{} " , address, tx.getHashAsString(), tx.getValue(wallet));
                        log.info("record ----fee:{} " , tx.getFee());
                        
//                        Coin receiveCoin = tx.getValue(wallet);
                        
                        //交易确认   金额<1是3个，<10是4个，<100是5个。剩余6个
//                        boolean updateFlag = checkValue(receiveCoin, peers);
//                        log.info("updateFlag: {}",updateFlag);
//                        
//                        if (updateFlag){
                            
                            //hash需要判断是充值记录还是提现记录都要判断，
                        	DigitalCoin digitalCoin = digitalCoinMapper.selectDigitalCoinByCoinName(Constant.CURRENCY_BTC);
                        	WithdrawLog withdrawLog = withdrawLogMapper.findWithdrawLogByTxHash(tx.getHashAsString());
                        	//是我们发起的交易
                            if (withdrawLog != null && WalletConstant.SEND.equals(withdrawLog.getStatus())){
                                log.info("提现记录  withdrawLog " );
                                withdrawLog.setStatus(WalletConstant.FINISH);
                                withdrawLog.setUpdateTime(new Date());
//                                withdrawLog.setFree(new BigDecimal (tx.getFee().toPlainString()));
                                withdrawLogMapper.updateWithdrawLogByTxHash(withdrawLog);
                                
                                //回调
                                try {
                                	UserCoin userCoin = userCoinMapper.getUserCoinByAccountAndCurrency(withdrawLog.getAccount(), Constant.CURRENCY_LTC);
									log.info("BTC提币账号信息：account="+userCoin.getAccount()+",提币账号自己的地址address="+userCoin.getAddress());
									String withdrawCallback = withDrawService.confirmCallback(userCoin.getAddress(), digitalCoin.getId(), withdrawLog.getAccount(), tx.getHashAsString(), withdrawLog.getMoney(),withdrawLog.getOrderNo());
									log.info("----------比特币提币确认回调结果withdrawCallback="+withdrawCallback);
									JSONObject withdrawObject = JSONObject.parseObject(withdrawCallback);
									if(withdrawObject.getBooleanValue("success")){
										withdrawLogMapper.updateIsSend(tx.getHashAsString(), "1");//回调已发送
									}else{
										withdrawLogMapper.updateIsSend(tx.getHashAsString(), "0");//回调异常
									}
                                } catch (Exception e) {
									log.error("-------BTC提币确认回调发生异常，address="+withdrawLog.getToAddress()+",money="+withdrawLog.getMoney()+",account="+withdrawLog.getAccount());
									withdrawLogMapper.updateIsSend(tx.getHashAsString(), "0");//回调异常
									e.printStackTrace();
								}
                                
                            }
                            
                            RechargeLog rechargeLog = rechargeLogMapper.findRechargeLogByTxHash(tx.getHashAsString());
                            if (rechargeLog != null  && WalletConstant.SEND.equals(rechargeLog.getStatus())) {
                                log.info("TransactionConfidence:"+address + " , " + tx.getHashAsString() + ", "+tx.getFee());
                                log.info("TransactionConfidence===" + txop.getValue().toFriendlyString());
                                log.info("充值记录  rechargeLog " );
                                rechargeLog.setStatus(WalletConstant.FINISH);
                                rechargeLog.setUpdateTime(new Date());
                                
                                rechargeLogMapper.updateRechargeLogByTxHash(rechargeLog);
                                
                                //回调
                                try {
									String rechargeCallback = depositService.confirmCallback(rechargeLog.getToAddress(), digitalCoin.getId(), rechargeLog.getToAccount(), tx.getHashAsString(), rechargeLog.getMoney());
									log.info("比特币充币确认回调rechargeCallback="+rechargeCallback);
									JSONObject rechargeObject = JSONObject.parseObject(rechargeCallback);
									if(rechargeObject.getBooleanValue("success")){
										rechargeLogMapper.updateIsSend(tx.getHashAsString(), "1");//回调已发送
									}else{
										rechargeLogMapper.updateIsSend(tx.getHashAsString(), "0");//回调异常
									}
                                } catch (Exception e) {
									log.error("-------BTC充币确认回调发生异常，address="+rechargeLog.getToAddress()+",money="+rechargeLog.getMoney()+",account="+rechargeLog.getToAccount());
									rechargeLogMapper.updateIsSend(tx.getHashAsString(), "0");//回调异常
									e.printStackTrace();
								}
                                
                            }
//                        }
                        
                    }
                }
            }
        });
        
        // Ready to run. The kit syncs the blockchain and our wallet event listener gets notified when something happens.
        // To test everything we create and print a fresh receiving address. Send some coins to that address and see if everything works.
        log.info("send money to: " + EcoWalletApplication.kit.wallet().freshReceiveAddress().toString());
        
        //导入数据库里所有key
        List<UserCoin> userCoinList = userCoinMapper.getAllUserCoin(Constant.CURRENCY_BTC);
        if (userCoinList != null ){
            
            for (UserCoin userCoin : userCoinList) {
                
                try {
                    ECKey key;
                    DumpedPrivateKey dumpedPrivateKey = DumpedPrivateKey.fromBase58(EcoWalletApplication.params, 
                            AES.decrypt(userCoin.getPrivateKey()) );
                    key = dumpedPrivateKey.getKey();
                    boolean containsFlag = EcoWalletApplication.kit.wallet().getImportedKeys().contains(key);
                    boolean addressFlag = key.toAddress(EcoWalletApplication.params).toBase58().equals(userCoin.getAddress());
                    
                    log.info("contains key :{}, address : {}, data key {}", key , 
                            key.toAddress(EcoWalletApplication.params).toBase58() , userCoin.getAddress() );
                    log.info("addressFlag , {},containsFlag :{}", addressFlag, containsFlag);
                    if (!containsFlag && addressFlag){
                        EcoWalletApplication.kit.wallet().importKey(key);
                    }
                } catch (AddressFormatException e) {
                    log.info(" 数据格式异常 ",e);
                }
                
            }
        }
        
        for (ECKey ecKey : ecKeys) {
            log.info("address= " + ecKey.toAddress(EcoWalletApplication.params).toBase58());
        }
    
        log.info(EcoWalletApplication.kit.wallet().getBalance().toFriendlyString());
    }
    
    /**
     * 交易确认   金额<1是3个，<10是4个，<100是5个。剩余6个
     * @param receiveCoin
     * @param peers
     * @return
     */
    public boolean checkValue(Coin receiveCoin, int peers){
        
        log.debug("receiveCoin : {}, peers : {}", receiveCoin.longValue() , peers);
        
        if (peers < 3){
            return false;
        }
        //100000000L 为1个BTC
        if (100000000L >= receiveCoin.longValue() && peers >= 3){
            return true;
        }
        //10个
        if (1000000000L >= receiveCoin.longValue() && peers >= 4){
            return true;
        }
        //100个
        if (10000000000L >= receiveCoin.longValue() && peers >= 5){
            return true;
        }
        //大于100个
        if (10000000000L < receiveCoin.longValue() && peers >= 6){
            return true;
        }
        
        return false;
    }
}
