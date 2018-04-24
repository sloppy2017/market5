//package com.c2b.ethWallet.task.token.deposit;
//
//import java.util.List;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import com.c2b.ethWallet.entity.RechargeLog;
//import com.c2b.ethWallet.mapper.RechargeLogMapper;
//import com.c2b.ethWallet.util.Constant;
//
///**  
// * 类说明   
// *  token充币确认线程
// * @author Anne  
// * @date 2017年12月23日 
// */
//@Component("tokenConfirmDepositTxThread")
//public class TOKENConfirmDepositTxThread{
//
//  private Logger logger = LoggerFactory.getLogger(getClass());
//  
//  @Autowired
//  private RechargeLogMapper rechargeLogMapper;
//  
//  @Autowired
//  private TOKENConfirmDepositTx tokenConfirmDepositTx;
//  
// /* @Scheduled(cron = "0/20 * * * * ?")
//  public void runZg() {
//    logger.info(Constant.zgTokenName+"充币确认线程开始");
//    List<RechargeLog> rechargeLogList = null;
//    try {
//      // 查询所有SEND状态的充币记录列表
//      rechargeLogList = rechargeLogMapper.listTOKENSend(Constant.zgTokenName);
//      if(rechargeLogList != null && rechargeLogList.size()>0){
//        tokenConfirmDepositTx.doConfirmDepositWork(Constant.zgTokenName, rechargeLogList);
//      }
//    } catch (Exception e) {
//      logger.error(Constant.zgTokenName+"充币确认线程异常："+e.getMessage());
//      e.printStackTrace();
//    }
//    logger.info(Constant.zgTokenName+"充币确认线程结束");
//  }*/
//  
//  @Scheduled(cron = "0/15 * * * * ?")
//  public void runOmg() {
//    logger.info(Constant.omgTokenName+"充币确认线程开始");
//    List<RechargeLog> rechargeLogList = null;
//    try {
//      // 查询所有SEND状态的充币记录列表
//      rechargeLogList = rechargeLogMapper.listTOKENSend(Constant.omgTokenName);
//      if(rechargeLogList != null && rechargeLogList.size()>0){
//        tokenConfirmDepositTx.doConfirmDepositWork(Constant.omgTokenName, rechargeLogList);
//      }
//    } catch (Exception e) {
//      logger.error(Constant.omgTokenName+"充币确认线程异常："+e.getMessage());
//      e.printStackTrace();
//    }
//    logger.info(Constant.omgTokenName+"充币确认线程结束");
//  }
//
//  /*@Scheduled(cron = "0 1/3 * * * ?")
//  public void runSnt() {
//    logger.info(Constant.sntTokenName+"充币确认线程开始");
//    List<RechargeLog> rechargeLogList = null;
//    try {
//      // 查询所有SEND状态的充币记录列表
//      rechargeLogList = rechargeLogMapper.listTOKENSend(Constant.sntTokenName);
//      if(rechargeLogList != null && rechargeLogList.size()>0){
//        tokenConfirmDepositTx.doConfirmDepositWork(Constant.sntTokenName, rechargeLogList);
//      }
//    } catch (Exception e) {
//      logger.error(Constant.sntTokenName+"充币确认线程异常："+e.getMessage());
//      e.printStackTrace();
//    }
//    logger.info(Constant.sntTokenName+"充币确认线程结束");
//  }
//  
//  @Scheduled(cron = "0 1/3 * * * ?")
//  public void runGnt() {
//    logger.info(Constant.gntTokenName+"充币确认线程开始");
//    List<RechargeLog> rechargeLogList = null;
//    try {
//      // 查询所有SEND状态的充币记录列表
//      rechargeLogList = rechargeLogMapper.listTOKENSend(Constant.gntTokenName);
//      if(rechargeLogList != null && rechargeLogList.size()>0){
//        tokenConfirmDepositTx.doConfirmDepositWork(Constant.gntTokenName, rechargeLogList);
//      }
//    } catch (Exception e) {
//      logger.error(Constant.gntTokenName+"充币确认线程异常："+e.getMessage());
//      e.printStackTrace();
//    }
//    logger.info(Constant.gntTokenName+"充币确认线程结束");
//  }
//  
//  @Scheduled(cron = "0 1/3 * * * ?")
//  public void runPowr() {
//    logger.info(Constant.powrTokenName+"充币确认线程开始");
//    List<RechargeLog> rechargeLogList = null;
//    try {
//      // 查询所有SEND状态的充币记录列表
//      rechargeLogList = rechargeLogMapper.listTOKENSend(Constant.powrTokenName);
//      if(rechargeLogList != null && rechargeLogList.size()>0){
//        tokenConfirmDepositTx.doConfirmDepositWork(Constant.powrTokenName, rechargeLogList);
//      }
//    } catch (Exception e) {
//      logger.error(Constant.powrTokenName+"充币确认线程异常："+e.getMessage());
//      e.printStackTrace();
//    }
//    logger.info(Constant.powrTokenName+"充币确认线程结束");
//  }
//  
//  @Scheduled(cron = "0 1/3 * * * ?")
//  public void runPtk() {
//    logger.info(Constant.ptkTokenName+"充币确认线程开始");
//    List<RechargeLog> rechargeLogList = null;
//    try {
//      // 查询所有SEND状态的充币记录列表
//      rechargeLogList = rechargeLogMapper.listTOKENSend(Constant.ptkTokenName);
//      if(rechargeLogList != null && rechargeLogList.size()>0){
//        tokenConfirmDepositTx.doConfirmDepositWork(Constant.ptkTokenName, rechargeLogList);
//      }
//    } catch (Exception e) {
//      logger.error(Constant.ptkTokenName+"充币确认线程异常："+e.getMessage());
//      e.printStackTrace();
//    }
//    logger.info(Constant.ptkTokenName+"充币确认线程结束");
//  }*/
//}
