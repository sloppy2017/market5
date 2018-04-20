package com.c2b.wallet.util;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.c2b.constant.Constant;
import com.c2b.constant.WalletConstant;
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
import com.c2b.wallet.service.LiteWalletService;
import com.c2b.wallet.service.WithDrawService;
import com.google.litecoin.core.AbstractWalletEventListener;
import com.google.litecoin.core.Address;
import com.google.litecoin.core.AddressFormatException;
import com.google.litecoin.core.DumpedPrivateKey;
import com.google.litecoin.core.ECKey;
import com.google.litecoin.core.NetworkParameters;
import com.google.litecoin.core.Transaction;
import com.google.litecoin.core.TransactionInput;
import com.google.litecoin.core.TransactionOutput;
import com.google.litecoin.core.Utils;
import com.google.litecoin.core.Wallet;
import com.google.litecoin.kits.WalletAppKit;
import com.google.litecoin.params.MainNetParams;
import com.google.litecoin.script.Script;

@Service
public class LiteWalletAsync implements Runnable{
    
    static Logger log = LoggerFactory.getLogger(LiteWalletAsync.class);
    
    @Autowired
    private LiteWalletService liteWalletService;
    
    @Autowired
    private UserCoinMapper userCoinMapper;
    
    @Autowired
    private RechargeLogMapper rechargeLogMapper;
    
    @Autowired
    private WithdrawLogMapper withdrawLogMapper;
    
    @Autowired
    private DepositService depositService;
    
    @Autowired
    private DigitalCoinMapper digitalCoinMapper;
    
    @Autowired
    private WithDrawService withDrawService;
    
    
    private static  String wallertRootPath = "C:\\ecocurrency";
    
    public static NetworkParameters params = MainNetParams.get();
    
    private static String userName ="eco-lite-currency";
    
    @Value("${litewallet.privateKey:T6KL4Tp8mwQ9wBarR5JKYVDJCL2FQrod5xAYUmFhRwCxtqqWg5aL}")
    String privateKey;
    
    public static WalletAppKit kit = new WalletAppKit(params, 
            new File(wallertRootPath + File.separatorChar + userName), userName );
    
    public LiteWalletAsync() {
        
    }

    @Override
    public void run() {
        
        log.info("------------------run---------------" );
        kit.startAndWait();
        Wallet wallet = kit.wallet();
        System.out.println("======kit.wallet()====="+kit.wallet().toString(true, true, true, null));
        List<ECKey> ecKeys = kit.wallet().getKeys();
        if(ecKeys == null || ecKeys.size() == 0){
            log.info("--------------init key---------");
            ECKey key = null;
            try {
            	privateKey = AES.decrypt(privateKey);
				/*if (privateKey.length() == 51) {
				    DumpedPrivateKey dumpedPrivateKey = new DumpedPrivateKey(params, privateKey);
				    key = dumpedPrivateKey.getKey();
				} else {
				    BigInteger privKey = Base58.decodeToBigInteger(privateKey);
				    key = new ECKey(privKey);
				}*/
            	DumpedPrivateKey dumpedPrivateKey = new DumpedPrivateKey(params, privateKey);
			    key = dumpedPrivateKey.getKey();
				kit.wallet().addKey(key);
			} catch (AddressFormatException e) {
				e.printStackTrace();
			}
        }
        log.info("--------ltc批量导入前---ecKeys.size()="+ecKeys.size());
        Address adminAddress = kit.wallet().getKeys().get(0).toAddress(params);
        // We want to know when we receive money.
        kit.wallet().addEventListener(new AbstractWalletEventListener() {
            @Override
            public void onCoinsReceived(Wallet w, Transaction tx, BigInteger prevBalance, BigInteger newBalance) {
                log.info("addCoinsReceivedEventListener getValueSentToMe=="+  tx.getValueSentToMe(wallet) );
                log.info("addCoinsReceivedEventListener getValueSentFromMe=="+ tx.getValueSentFromMe(wallet));
                log.info("--------> coins resceived: " + tx.getHashAsString() + ", value = " + tx.getValue(wallet));
                
                for (TransactionOutput txop : tx.getOutputs()){
                    
                    String address = txop.getScriptPubKey().getToAddress(params).toString();
                    log.info("Received address= " + address);
                    
                    if(txop.isMineOrWatched(wallet)){
                    	UserCoin userCoin = userCoinMapper.getUserCoinByAddress(address);
                        
                        if (userCoin==null){
                            throw new RuntimeServiceException("用户信息不存在");
                        }
                        
//                        BigInteger sendToMe = txop.getValue().subtract(Transaction.REFERENCE_DEFAULT_MIN_TX_FEE);
                        log.info("------------txop.getValue()="+Utils.bitcoinValueToFriendlyString(txop.getValue()));
                        log.info("------------tx.getValueSentToMe(wallet)="+Utils.bitcoinValueToFriendlyString(tx.getValueSentToMe(wallet)));
                        try {
							log.info("-------------txop.getHashAsString()="+txop.getHash());
						} catch (Exception e) {
							e.printStackTrace();
						}
                        log.info("-------------tx.getHashAsString()="+tx.getHashAsString());
                        //增加充值记录
                        RechargeLog rechargeLog = new RechargeLog();
                        String orderNo =OrderGenerater.generateOrderNo();
                        rechargeLog.setOrderNo(orderNo);
                        rechargeLog.setToAccount(userCoin.getAccount());
                        rechargeLog.setToAddress(address);
                        rechargeLog.setFromAddress(null);
                        rechargeLog.setCurrency(Constant.CURRENCY_LTC);
                        
                        rechargeLog.setMoney(new BigDecimal(Utils.bitcoinValueToFriendlyString(txop.getValue())));
//                      rechargeLog.setMoney(new BigDecimal(Utils.bitcoinValueToFriendlyString(tx.getValueSentToMe(wallet))));
                        rechargeLog.setFree(new BigDecimal (Utils.bitcoinValueToFriendlyString(Transaction.REFERENCE_DEFAULT_MIN_TX_FEE)));
                        rechargeLog.setStatus(WalletConstant.SEND);
                        rechargeLog.setCreateTime(new Date());
                        rechargeLog.setTxHash(tx.getHashAsString());
                        rechargeLogMapper.insertSelective(rechargeLog);
                        log.info("=====address:"+address + ",addCoinsReceive money===" + txop.getValue().subtract(Transaction.REFERENCE_DEFAULT_MIN_TX_FEE));
                        //回调
                        try {
							DigitalCoin digitalCoin = digitalCoinMapper.selectDigitalCoinByCoinName(Constant.CURRENCY_LTC);
							depositService.broadcastCallback(rechargeLog.getToAddress(), digitalCoin.getId(), rechargeLog.getToAccount(), tx.getHashAsString(), rechargeLog.getMoney());
						} catch (Exception e) {
							log.error("-------LTC充币广播回调发生异常，address="+rechargeLog.getToAddress()+",money="+rechargeLog.getMoney()+",account="+rechargeLog.getToAccount());
							e.printStackTrace();
						}
                    }else{
                        log.info("=========not me======");
                    }
                }

                // Wait until it's made it into the block chain (may run immediately if it's already there).
                //
                // For this dummy app of course, we could just forward the unconfirmed transaction. If it were
                // to be double spent, no harm done. Wallet.allowSpendingUnconfirmedTransactions() would have to
                // be called in onSetupCompleted() above. But we don't do that here to demonstrate the more common
                // case of waiting for a block.
                /*Futures.addCallback(tx.getConfidence().getDepthFuture(1), new FutureCallback<Transaction>() {
                    @Override
                    public void onSuccess(Transaction result) {
                        // "result" here is the same as "tx" above, but we use it anyway for clarity.
                        try {
                            BigInteger value = result.getValueSentToMe(kit.wallet());
                            System.out.println("Forwarding " + Utils.bitcoinValueToFriendlyString(value) + " LTC");
                            // Now send the coins back! Send with a small fee attached to ensure rapid confirmation.
                            final BigInteger amountToSend = value.subtract(Transaction.REFERENCE_DEFAULT_MIN_TX_FEE);
                            
                            final Wallet.SendResult sendResult = kit.wallet().sendCoins(kit.peerGroup(), adminAddress, amountToSend);
                            checkNotNull(sendResult);  // We should never try to send more coins than we have!
                            System.out.println("Sending ...");
                            // Register a callback that is invoked when the transaction has propagated across the network.
                            // This shows a second style of registering ListenableFuture callbacks, it works when you don't
                            // need access to the object the future returns.
                            sendResult.broadcastComplete.addListener(new Runnable() {
                                @Override
                                public void run() {
                                    // The wallet has changed now, it'll get auto saved shortly or when the app shuts down.
                                    System.out.println("Sent coins onwards! Transaction hash is " + sendResult.tx.getHashAsString());
                                }
                            }, MoreExecutors.sameThreadExecutor());
                        } catch (KeyCrypterException e) {
                            // We don't use encrypted wallets in this example - can never happen.
                            throw new RuntimeException(e);
                        } catch (InsufficientMoneyException e) {
                            // This should never happen - we're only trying to forward what we received!
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        // This kind of future can't fail, just rethrow in case something weird happens.
                        throw new RuntimeException(t);
                    }
                });*/
            }
            
            @Override
            public void onTransactionConfidenceChanged(Wallet wallet, Transaction tx){
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
                        String address = txop.getScriptPubKey().getToAddress(params).toString();
                        log.info("record ----address:{} ,tx :{},value:{} " , address, tx.getHashAsString(), tx.getValue(wallet));
//                        log.info("record ----fee:{} " , tx.getFee());
                        
//                        Coin receiveCoin = tx.getValue(wallet);
                        
                        //交易确认   金额<1是3个，<10是4个，<100是5个。剩余6个
//                        boolean updateFlag = checkValue(receiveCoin, peers);
//                        log.info("updateFlag: {}",updateFlag);
//                        
//                        if (updateFlag){
                        
                            DigitalCoin digitalCoin = digitalCoinMapper.selectDigitalCoinByCoinName(Constant.CURRENCY_LTC);
                            //hash需要判断是充值记录还是提现记录都要判断，
                        	WithdrawLog withdrawLog = withdrawLogMapper.findWithdrawLogByTxHash(tx.getHashAsString());
                            //是我们发起的交易
                            if (withdrawLog != null && WalletConstant.SEND.equals(withdrawLog.getStatus())){
                                log.info("提现记录  WithdrawRecord " );
                                withdrawLog.setStatus(WalletConstant.FINISH);
                                withdrawLog.setUpdateTime(new Date());
//                                withdrawLog.setFree(new BigDecimal(Utils.bitcoinValueToFriendlyString(tx.REFERENCE_DEFAULT_MIN_TX_FEE)));
                                withdrawLogMapper.updateWithdrawLogByTxHash(withdrawLog);
                              //回调
                                try {
                                	UserCoin userCoin = userCoinMapper.getUserCoinByAccountAndCurrency(withdrawLog.getAccount(), Constant.CURRENCY_LTC);
									log.info("LTC提币账号信息：account="+userCoin.getAccount()+",提币账号自己的地址address="+userCoin.getAddress());
                                	String withdrawCallback = withDrawService.confirmCallback(userCoin.getAddress(), digitalCoin.getId(), withdrawLog.getAccount(), tx.getHashAsString(), withdrawLog.getMoney(),withdrawLog.getOrderNo());
									log.info("----------莱特币提币确认回调结果withdrawCallback="+withdrawCallback);
									JSONObject withdrawObject = JSONObject.parseObject(withdrawCallback);
									if(withdrawObject.getBooleanValue("success")){
										withdrawLogMapper.updateIsSend(tx.getHashAsString(), "1");//回调已发送
									}else{
										withdrawLogMapper.updateIsSend(tx.getHashAsString(), "0");//回调异常
									}
									
                                } catch (Exception e) {
									log.error("-------LTC提币确认回调发生异常，address="+withdrawLog.getToAddress()+",money="+withdrawLog.getMoney()+",account="+withdrawLog.getAccount());
									withdrawLogMapper.updateIsSend(tx.getHashAsString(), "0");//回调异常
									e.printStackTrace();
								}
                            } 
                            RechargeLog rechargeLog = rechargeLogMapper.findRechargeLogByTxHash(tx.getHashAsString());
                            
                            if (rechargeLog != null  && WalletConstant.SEND.equals(rechargeLog.getStatus())) {
                                log.info("TransactionConfidence:"+address + " , " + tx.getHashAsString());
                                log.info("TransactionConfidence===" + Utils.bitcoinValueToFriendlyString(txop.getValue()));
                                log.info("充值记录  WithdrawRecord " );
                                rechargeLog.setStatus(WalletConstant.FINISH);
                                rechargeLog.setUpdateTime(new Date());
                                rechargeLogMapper.updateRechargeLogByTxHash(rechargeLog);
                                //回调
                                try {
									String rechargeCallback = depositService.confirmCallback(rechargeLog.getToAddress(), digitalCoin.getId(), rechargeLog.getToAccount(), tx.getHashAsString(), rechargeLog.getMoney());
									log.info("莱特币充币确认回调rechargeCallback="+rechargeCallback);
									JSONObject rechargeObject = JSONObject.parseObject(rechargeCallback);
									if(rechargeObject.getBooleanValue("success")){
										rechargeLogMapper.updateIsSend(tx.getHashAsString(), "1");//回调已发送
									}else{
										rechargeLogMapper.updateIsSend(tx.getHashAsString(), "0");//回调异常
									}
                                } catch (Exception e) {
									log.error("-------LTC充币确认回调发生异常，address="+rechargeLog.getToAddress()+",money="+rechargeLog.getMoney()+",account="+rechargeLog.getToAccount());
									rechargeLogMapper.updateIsSend(tx.getHashAsString(), "0");//回调异常
									e.printStackTrace();
								}
                            }
//                        }
                        
                    }
                }
            }

            @Override
            public void onCoinsSent(Wallet wallet, Transaction tx,
                    BigInteger prevBalance, BigInteger newBalance) {
                super.onCoinsSent(wallet, tx, prevBalance, newBalance);
                log.info("addCoinsSentEventListener getValueSentToMe=="+  tx.getValueSentToMe(wallet));
                log.info("addCoinsSentEventListener getValueSentFromMe=="+ tx.getValueSentFromMe(wallet));
                log.info("--------> tx hash of coins sent: " + tx.getHashAsString());
                log.info("coins sent");
                for (TransactionInput txip : tx.getInputs()){
                    log.info(" TransactionInput：{} " , txip);
                }
            }

            @Override
            public void onReorganize(Wallet wallet) {
                super.onReorganize(wallet);
            }

            @Override
            public void onKeysAdded(Wallet wallet, List<ECKey> keys) {
                super.onKeysAdded(wallet, keys);
                log.info("new key added");
            }

            @Override
            public void onScriptsAdded(Wallet wallet, List<Script> scripts) {
                super.onScriptsAdded(wallet, scripts);
                log.info("new script added");
            }

            @Override
            public void onWalletChanged(Wallet wallet) {
                // TODO Auto-generated method stub
                super.onWalletChanged(wallet);
            }

            @Override
            public void onChange() {
                // TODO Auto-generated method stub
                super.onChange();
            }
            
            
        });

        System.out.println("Send coins to: " + adminAddress);
        
        //导入数据库里所有key
        log.info("---------ltc----导入数据库中的所有key------------");
        List<UserCoin> userCoinList = userCoinMapper.getAllUserCoin(Constant.CURRENCY_LTC);
        if (userCoinList != null ){
            
            for (UserCoin userCoin : userCoinList) {
                if(userCoin.getPrivateKey()!=null&&userCoin.getAccount()!=null){
                	try {
                        ECKey key;
                        
                        String privateKey = AES.decrypt(userCoin.getPrivateKey());
                        log.info("-----decrypt="+privateKey);
                        if(privateKey == null){
                        	continue;
                        }
        				/*if (privateKey.length() == 51) {
        				    DumpedPrivateKey dumpedPrivateKey = new DumpedPrivateKey(params, privateKey);
        				    key = dumpedPrivateKey.getKey();
        				} else {
        				    BigInteger privKey = Base58.decodeToBigInteger(privateKey);
        				    key = new ECKey(privKey);
        				}*/
                        DumpedPrivateKey dumpedPrivateKey = new DumpedPrivateKey(params, privateKey);
    				    key = dumpedPrivateKey.getKey();
        				boolean containsFlag = kit.wallet().getKeys().contains(key);
        				boolean addressFlag = key.toAddress(params).toString().equals(userCoin.getAddress());
        				
                        log.info("contains key :{}, address : {}, data key {}", key , 
                                key.toAddress(params).toString() , userCoin.getAddress());
                        log.info("addressFlag , {},containsFlag :{}", addressFlag, containsFlag);
                        if (!containsFlag && addressFlag){
                        	kit.wallet().addKey(key);
                        }
                    } catch (AddressFormatException e) {
                        log.info("数据格式异常 ",e);
                    } catch (Exception e) {
                        log.info(e.getMessage(),e);
                    } 
                }
            }
        }
        log.info("----------LTC批量导入后ecKeys.size()="+ecKeys.size());
        for (ECKey ecKey : ecKeys) {
            log.info("LTC address= " + ecKey.toAddress(params).toString());
        }
    
        log.info("wallet.getBalance()==="+kit.wallet().getBalance().toString());

    }
    public static void main(String[] args) {
		System.out.println("T6KL4Tp8mwQ9wBarR5JKYVDJCL2FQrod5xAYUmFhRwCxtqqWg5aL".length());
	}
}
