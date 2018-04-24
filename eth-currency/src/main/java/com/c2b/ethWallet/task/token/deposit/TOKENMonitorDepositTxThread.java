//package com.c2b.ethWallet.task.token.deposit;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import com.c2b.ethWallet.util.Constant;
//
///**
// * 类说明
// *  token充币监控（广播）线程
// * @author Anne
// * @date 2017年12月23日
// */
//@Component("tokenMonitorDepositTxThread")
//public class TOKENMonitorDepositTxThread{
//  private Logger logger = LoggerFactory.getLogger(getClass());
//  
//  @Autowired
//  private TOKENMonitorDepositTx tokenMonitorDepositTx;
//  
// /* @Scheduled(cron = "0/15 * * * * ?")
//  public void runzg() {
//    logger.info(Constant.zgTokenName+"充币广播线程开始");
//    tokenMonitorDepositTx.doTokenScanDeposit(Constant.zgTokenName);
//    logger.info(Constant.zgTokenName+"充币广播线程结束");
//  }*/
//  
//  @Scheduled(cron = "0/10 * * * * ?")
//  public void runOmg() {
//    logger.info(Constant.omgTokenName+"充币广播线程开始");
//    tokenMonitorDepositTx.doTokenScanDeposit(Constant.omgTokenName);
//    logger.info(Constant.omgTokenName+"充币广播线程结束");
//  }
//  
//  /*@Scheduled(cron = "0 1/3 * * * ?")
//  public void runSnt() {
//    logger.info(Constant.sntTokenName+"充币广播线程开始");
//    tokenMonitorDepositTx.doTokenScanDeposit(Constant.sntTokenName);
//    logger.info(Constant.sntTokenName+"充币广播线程结束");
//  }
//  
//  @Scheduled(cron = "0 1/3 * * * ?")
//  public void runGnt() {
//    logger.info(Constant.gntTokenName+"充币广播线程开始");
//    tokenMonitorDepositTx.doTokenScanDeposit(Constant.gntTokenName);
//    logger.info(Constant.gntTokenName+"充币广播线程结束");
//  }
//  
//  @Scheduled(cron = "0 1/3 * * * ?")
//  public void runPowr() {
//    logger.info(Constant.powrTokenName+"充币广播线程开始");
//    tokenMonitorDepositTx.doTokenScanDeposit(Constant.powrTokenName);
//    logger.info(Constant.powrTokenName+"充币广播线程结束");
//  }
//  
//  @Scheduled(cron = "0 1/3 * * * ?")
//  public void runPtk() {
//    logger.info(Constant.ptkTokenName+"充币广播线程开始");
//    tokenMonitorDepositTx.doTokenScanDeposit(Constant.ptkTokenName);
//    logger.info(Constant.ptkTokenName+"充币广播线程结束");
//  }*/
//}
