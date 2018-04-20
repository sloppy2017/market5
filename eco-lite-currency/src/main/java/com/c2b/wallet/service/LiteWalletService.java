package com.c2b.wallet.service;

import com.c2b.constant.Constant;
import com.c2b.constant.WalletConstant;
import com.c2b.exception.RuntimeServiceException;
import com.c2b.util.AES;
import com.c2b.util.OrderGenerater;
import com.c2b.wallet.entity.UserCoin;
import com.c2b.wallet.entity.WithdrawLog;
import com.c2b.wallet.mapper.UserCoinMapper;
import com.c2b.wallet.mapper.WithdrawLogMapper;
import com.c2b.wallet.util.LiteWalletAsync;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.litecoin.core.*;
import com.google.litecoin.kits.WalletAppKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import static com.google.common.base.Preconditions.checkNotNull;

@Component("liteWalletService")
public class LiteWalletService {

  Logger log = LoggerFactory.getLogger(getClass());

  @Autowired
  private UserCoinMapper userCoinMapper;
  @Autowired
  private WithdrawLogMapper withdrawLogMapper;

  /**
   * 检查该用户是否已存在钱包地址
   *
   * @param userName
   */
  private void checkExistAddress(String account) {
    UserCoin userCoin = userCoinMapper.getUserCoinByAccountAndCurrency(account, Constant.CURRENCY_LTC);
    if (userCoin != null) {
      throw new RuntimeServiceException("地址已存在：" + userCoin.getAddress());
    }
  }

  /**
   * 创建钱包地址
   *
   * @param params  params // First we configure the network we want to use.
   *                // The available options are:
   *                <p> - MainNetParams
   *                <p> - TestNet3Params
   *                <p> - RegTestParams
   * @param account 用户名
   * @return 钱包的地址
   */
  public String createWallet(NetworkParameters params, String account) throws IOException {

//        checkExistAddress(account);
    Wallet wallet = LiteWalletAsync.kit.wallet();
    ECKey key = new ECKey();
    wallet.addKey(key);
    Address address = key.toAddress(params);
    UserCoin userCoin = new UserCoin();
    userCoin.setAccount(account);
    userCoin.setAddress(address.toString());
    userCoin.setCurrency(Constant.CURRENCY_LTC);
    userCoin.setPrivateKey(AES.encrypt(key.getPrivateKeyEncoded(params).toString().getBytes("UTF-8")));
    userCoin.setCreateTime(new Date());
    userCoinMapper.insertSelective(userCoin);
    return address.toString();
  }


  /**
   * 提现
   * 在params网络里，用户 userName 发送coin钱给receiverAddress
   *
   * @param params
   * @param userId          用户ID
   * @param id              记录ID
   * @param coin
   * @param receiverAddress
   */
  @Transactional
  public synchronized String sendMoney(NetworkParameters params, WithdrawLog withdrawLog) throws Exception {
    Address forwardingAddress = new Address(params, withdrawLog.getToAddress());
    BigInteger value = Utils.toNanoCoins(withdrawLog.getMoney().toString());
    System.out.println("Sending " + Utils.bitcoinValueToFriendlyString(value) + " LTC");
    // Now send the coins back! Send with a small fee attached to ensure rapid confirmation.
//        WalletAppKit kit = new WalletAppKit(LtcWalletApplication.params, new File(wallertRootPath + File.separatorChar + userName), userName );

    UserCoin userCoin = userCoinMapper.getUserCoinByAccountAndCurrency(withdrawLog.getAccount(),Constant.CURRENCY_LTC);
    if (userCoin == null){
      throw new RuntimeServiceException("用户信息异常。");//参数错误
    }
        /*String userId = userCoinMapper.getUserIdByAddress(withdrawRecord.getAddress());
        //如果是平台内部转账。
        if (StringUtils.isNotBlank(userId)){
        	withdrawRecord.setTxHash("ico hash");
        	withdrawRecord.setStatus(WalletConstant.FINISH);
        	withdrawRecord.setFree(BigDecimal.ZERO);
            //提现记录
            boolean updateTxHashById = withdrawRecordService.updateTxHashById(withdrawRecord);
            if(updateTxHashById){
            	//给另一个用户加钱
                UserWallet userWallet = new UserWallet();
                userWallet.setUserId(Integer.valueOf(userId));
                userWallet.setLtcAmnt(withdrawRecord.getMoney());
                userWalletService.updateAdd(userWallet);
            }
            return;
        }*/

    WalletAppKit kit = LiteWalletAsync.kit;
    final Wallet.SendResult sendResult = kit.wallet().sendCoins(kit.peerGroup(), forwardingAddress, value);
    checkNotNull(sendResult);  // We should never try to send more coins than we have!
    System.out.println("Sending ...");
    WithdrawLog twithdrawLog = new WithdrawLog();
//        String orderNo =OrderGenerater.generateOrderNo();
    twithdrawLog.setOrderNo(withdrawLog.getOrderNo());
    twithdrawLog.setAccount(withdrawLog.getAccount());
    twithdrawLog.setToAddress(withdrawLog.getToAddress());
    twithdrawLog.setMoney(withdrawLog.getMoney());
    twithdrawLog.setCurrency(Constant.CURRENCY_LTC);
    twithdrawLog.setTxHash(sendResult.tx.getHashAsString());
    twithdrawLog.setStatus(WalletConstant.SEND);
    twithdrawLog.setFree(new BigDecimal(Utils.bitcoinValueToFriendlyString(sendResult.tx.REFERENCE_DEFAULT_MIN_TX_FEE)));
    twithdrawLog.setCreateTime(new Date());

    withdrawLogMapper.insertSelective(twithdrawLog);


//        record.setFree(new BigDecimal(sendResult.tx.REFERENCE_DEFAULT_MIN_TX_FEE));
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
    return sendResult.tx.getHashAsString();
  }


}
