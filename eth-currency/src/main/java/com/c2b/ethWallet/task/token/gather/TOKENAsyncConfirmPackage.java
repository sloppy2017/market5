package com.c2b.ethWallet.task.token.gather;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionTimeoutException;

import com.c2b.ethWallet.client.Web3JClient;
import com.c2b.ethWallet.entity.IcoGatherRecord;
import com.c2b.ethWallet.entity.RechargeLog;
import com.c2b.ethWallet.entity.UserCoin;
import com.c2b.ethWallet.mapper.RechargeLogMapper;
import com.c2b.ethWallet.mapper.UserCoinMapper;
import com.c2b.ethWallet.service.EthTokenWalletService;
import com.c2b.ethWallet.service.IcoGatherRecordService;
import com.c2b.ethWallet.task.token.unit.Transfer;
import com.c2b.ethWallet.util.AES;
import com.c2b.ethWallet.util.Constant;
import com.c2b.ethWallet.util.WalletConstant;


/**  
 * 类说明   
 *  
 * @author Anne  
 * @date 2017年12月23日 
 */
@Component
public class TOKENAsyncConfirmPackage {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private static Web3j web3j = Web3JClient.getClient();

  @Autowired
  private RechargeLogMapper rechargeLogMapper;
  
  @Autowired
  private IcoGatherRecordService gatherRecordService;
  
  @Autowired
  private EthTokenWalletService ethTokenWalletService;
  
  int attempts = 3;
  int sleepDuration = 1000;
  
  BigDecimal amount = new BigDecimal("0.005");

  long maxWaitTime = 1 * 60 * 60 * 1000;
  
  @Autowired
  UserCoinMapper userCoinMapper;
  
  public static enum TaskEvent {
    BALANCE_NOT_ENOUGH(7);

    Integer eventCode;

    private TaskEvent(Integer _code) {
        eventCode = _code;
    }

    public Integer value() {
        return this.eventCode;
    }

}

  final Event event = new Event("Transfer", Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
  }, new TypeReference<Address>() {
  }), Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
  }));
  
  public static BlockingQueue<Map<String, Object>> queue = new LinkedBlockingQueue<Map<String, Object>>();
  
  public void todoGather(IcoGatherRecord gatherRecord){
    doGatherTxhash(gatherRecord);
    while (true) {
      try {
        Thread.sleep(1 * 1000);
      } catch (InterruptedException e2) {
          e2.printStackTrace();
      }
      if (queue.isEmpty()) {
          logger.info("queue is empty, sleep 5 sec");
          continue;
      }
      Map<String, Object> map = queue.poll();
      try {
        String orderId = null;
        RechargeLog record = null;
        try {
          orderId = (String) map.get("orderId");
          Future<TransactionReceipt> receipt = (Future<TransactionReceipt>) map.get("txReceipt");
          record = rechargeLogMapper.getIcoRechargeRecordByOrderId(orderId);
          logger.info("received type:" + gatherRecord.getCurrency() + "\taddress:" + gatherRecord.getToAddress()+"\torderId:"+record.getOrderNo());
          boolean isDone = receipt.isDone();
          TransactionReceipt tr = null;
          if(isDone){
            tr = receipt.get();
            String hash = tr.getTransactionHash();
            BigInteger blkNum = tr.getBlockNumber();
            boolean verifyRet = verifyTransferToken(tr);
            if(verifyRet){
              gatherRecord.setGatherStatus(Constant.GATHER_STATUS_SUCCESS);
              record.setStatus(WalletConstant.CONCENTRATE);
              gatherRecordService.updateGetherRecord(gatherRecord);
              logger.info("orderId:{} txHash:{} address:{} tblockNum:{} gather success!",
                      gatherRecord.getOrderNo(),hash, gatherRecord.getFromAddress(),
                      blkNum);
              return;
            }
            gatherRecord.setGatherStatus(Constant.GATHER_STATUS_FAIL);
            gatherRecordService.updateGetherRecord(gatherRecord);
            logger.info("orderId:{} txHash:{} regather",gatherRecord,hash);
          }else{
            queue.put(map);
            logger.info("Waits for the tokenGather to complete, replay queue for orderId:{}.", orderId);
          }
        } catch (Exception e) {
          record.setIsConcentrate(Constant.IS_UNCONCENTRATE);
          record.setStatus(WalletConstant.FINISH);
          rechargeLogMapper.updateRechargeRecordByTxHash(record);
        }
      } catch (Exception e) {
        e.printStackTrace();
        logger.error(e.getMessage());
      }
    }
  }
  
  public void doGatherTxhash(IcoGatherRecord gatherRecord){
    String orderId = gatherRecord.getOrderNo();
    String currency = gatherRecord.getCurrency();
    if(StringUtils.isEmpty(orderId)){
      gatherRecord.setGatherStatus(Constant.GATHER_STATUS_FAIL);
      gatherRecordService.updateGetherRecord(gatherRecord);
      return;
    }
    String txHash = gatherRecord.getHash();
    RechargeLog record = rechargeLogMapper.getTokenRechargeLogByOrderNoAndCurrency(currency,orderId);
    /*long createTime = gatherRecord.getCreateTime().getTime();
    long deltaTime = DateUtil.getCurrentTimestamp() - createTime;
    if (deltaTime >= maxWaitTime) {
        if(record!=null){
            //充值记录表里面是否归集改为未归集，以便再次进行归集处理。
            record.setIsConcentrate(Constant.IS_UNCONCENTRATE);
            record.setStatus(WalletConstant.FINISH);
            rechargeLogMapper.updateRechargeRecordByTxHash(record);
        }
        gatherRecord.setGatherStatus(Constant.GATHER_STATUS_FAIL);
        gatherRecordService.updateGetherRecord(gatherRecord);
        logger.warn("orderId:{} txHash:{} createTime:{}, it is timeout, retry later",
                record.getOrderNo(), txHash, createTime);
    }
    logger.info("check gatherTask orderId:{} txhash:{}",orderId,txHash);*/
    if(txHash!=null){
      /*EthGetTransactionReceipt ethGetTransactionReceipt = null;
      try {
        ethGetTransactionReceipt = web3j
                .ethGetTransactionReceipt(txHash).sendAsync().get();
      } catch (InterruptedException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      } catch (ExecutionException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
      if(ethGetTransactionReceipt == null || ethGetTransactionReceipt.hasError()){
          logger.error("ethGetTransactionReceipt is null, orderId:{} txHash:{}",
                  record.getOrderNo(), txHash);
          if(record!=null){
              record.setIsConcentrate(Constant.IS_UNCONCENTRATE);
              record.setStatus(WalletConstant.FINISH);
              rechargeLogMapper.updateRechargeRecordByTxHash(record);
          }
          gatherRecord.setGatherStatus(Constant.GATHER_STATUS_FAIL);
          gatherRecordService.updateGetherRecord(gatherRecord);
          return;
      }*/
      TransactionReceipt tx = null;
      try {
          tx = web3j.ethGetTransactionReceipt(txHash).send().getResult();
      } catch (IOException e) {
          e.printStackTrace();
          logger.error(e.getMessage());
      }
      if(tx==null){
          if(record!=null){
              record.setIsConcentrate(Constant.IS_UNCONCENTRATE);
              record.setStatus(WalletConstant.FINISH);
              rechargeLogMapper.updateRechargeRecordByTxHash(record);
          }
          gatherRecord.setGatherStatus(Constant.GATHER_STATUS_FAIL);
          gatherRecordService.updateGetherRecord(gatherRecord);
          return;
      }
      if(tx.getTransactionHash().equalsIgnoreCase(txHash) && tx.getBlockNumber()!=null && tx.getBlockNumber().compareTo(BigInteger.ZERO)==1 && 
          this.verifyTransferToken(tx)){
          BigInteger blockNum = tx.getBlockNumber();
          gatherRecord.setGatherStatus(Constant.GATHER_STATUS_SUCCESS);
          record.setStatus(WalletConstant.CONCENTRATE);
          gatherRecordService.updateGetherRecord(gatherRecord);
          logger.info("orderId:{} txHash:{} address:{} tblockNum:{} gather success!",
                  gatherRecord.getOrderNo(),txHash, gatherRecord.getFromAddress(),
                  blockNum);
          return;
      }else{
        if(currency.equalsIgnoreCase("snt")){
          /*ElecoinInfo elecoinInfo = remoteElecoinService.getElecoinByAddress(_order.getOuterAddress(),ElecoinInfo.CoinType.ETH.value(),
                  ElecoinInfo.Type.DEPOSIT.value(), null, null);
          String seed = EtherWalletUtil.decryptPrivateKey(elecoinInfo.getSalt(), elecoinInfo.getInitVector(), elecoinInfo.getSeed());
          Credentials sendCredentials = EtherWalletUtil.getCredentials(seed);*/
          UserCoin uc = userCoinMapper.getUserCoinByAddressAndCurrency(record.getToAddress(), record.getCurrency());
          if(uc == null){
            logger.warn("gather exception, address:"+record.getToAddress()+"not exist in UserCoin !");
            return;
          }
          String userPrivateKey = uc.getPrivateKey();
          ECKeyPair ecKeyPair = ECKeyPair.create(new BigInteger(AES.decrypt(userPrivateKey)));
          Credentials sendCredentials = Credentials.create(ecKeyPair);
          String txHashVal = tx.getTransactionHash();
          EthSendTransaction  transactionResponse = new EthSendTransaction();
          transactionResponse.setResult(txHashVal);
          try {
            Future<TransactionReceipt> receipt = Transfer.asyncGetTransactionReceipt(web3j, sendCredentials, transactionResponse);
            boolean isDone = receipt.isDone();
            TransactionReceipt tr = null;
            if(isDone){
              tr = receipt.get();
              String hash = tr.getTransactionHash();
              BigInteger blkNum = tr.getBlockNumber();
              boolean verifyRet = verifyTransferToken(tr);
              if(verifyRet){
                gatherRecord.setGatherStatus(Constant.GATHER_STATUS_SUCCESS);
                record.setStatus(WalletConstant.CONCENTRATE);
                gatherRecordService.updateGetherRecord(gatherRecord);
                logger.info("orderId:{} txHash:{} address:{} tblockNum:{} gather success!",
                        gatherRecord.getOrderNo(),hash, gatherRecord.getFromAddress(),
                        blkNum);
                return;
              }
              gatherRecord.setGatherStatus(Constant.GATHER_STATUS_FAIL);
              gatherRecordService.updateGetherRecord(gatherRecord);
              logger.info("orderId:{} txHash:{} regather",gatherRecord,txHash);
            }else{
              gatherRecord.setGatherStatus(Constant.GATHER_STATUS_FAIL);
              gatherRecordService.updateGetherRecord(gatherRecord);
              logger.info("orderId:{} txHash:{} regather",gatherRecord,txHash);
            }
          } catch (InterruptedException | ExecutionException
              | TransactionTimeoutException e) {
            e.printStackTrace();
            gatherRecord.setGatherStatus(Constant.GATHER_STATUS_FAIL);
            gatherRecordService.updateGetherRecord(gatherRecord);
            logger.info("orderId:{} txHash:{} regather",gatherRecord,txHash);
          }
      }else{
          logger.info("orderId:{} txHash:{} ignore it",gatherRecord,txHash);
      }
        return;
      }
    }
  }
  

  public boolean verifyTransferToken(TransactionReceipt receipt) {
      try {
          List<Log> logs = receipt.getLogs();
          if (logs.size() == 1) {
              Log txlog = logs.get(0);
              List<String> topics = txlog.getTopics();
              String encodedEventSignature = EventEncoder.encode(event);
              if (topics.get(0).equalsIgnoreCase(encodedEventSignature)) {
                  return true;
              }
          }
      } catch (Exception e) {
          e.printStackTrace();
      }
      return false;
  }
}
