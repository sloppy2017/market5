//package com.c2b.ethWallet.task.token.gather;
//
//import java.util.List;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.c2b.ethWallet.entity.RechargeLog;
//import com.c2b.ethWallet.mapper.RechargeLogMapper;
//import com.c2b.ethWallet.util.Constant;
//
///**  
// * 类说明   
// *  
// * @author Anne  
// * @date 2017年12月23日 
// */
//@Component("tokenGatherThread")
//public class TOKENGatherThread{
//
//  Logger logger = LoggerFactory.getLogger(getClass());
//  
//  @Autowired
//  private RechargeLogMapper rechargeLogMapper;
//  
//  @Autowired
//  private TOKENGather tokenGather;
//  
//  /*@Scheduled(cron = "25/35 * * * * ?")
//  @Transactional
//  public void runZg() {
//    logger.info(Constant.zgTokenName+"归集线程开始");
//    try{
//      //查询所有待归集的充值记录
//      List<RechargeLog> unGatherList = rechargeLogMapper.listTOKENRechargeByGather(Constant.zgTokenName);
//      if(unGatherList!=null && unGatherList.size()>0){
//        for(RechargeLog rechargeLog:unGatherList){
//          tokenGather.doTokenGather(Constant.zgTokenName, rechargeLog);
//        }
//      }
//    }catch(Exception e){
//      logger.error(Constant.zgTokenName+"归集线程异常："+e.getMessage());
//      e.printStackTrace();
//    }
//    logger.info(Constant.zgTokenName+"归集线程结束");
//  }*/
//  
//  @Scheduled(cron = "20/30 * * * * ?")
//  @Transactional
//  public void runOmg() {
//    logger.info(Constant.omgTokenName+"归集线程开始");
//    try{
//      //查询所有待归集的充值记录
//      List<RechargeLog> unGatherList = rechargeLogMapper.listTOKENRechargeByGather(Constant.omgTokenName);
//      if(unGatherList!=null && unGatherList.size()>0){
//        for(RechargeLog rechargeLog:unGatherList){
//          tokenGather.doTokenGather(Constant.omgTokenName, rechargeLog);
//        }
//      }
//    }catch(Exception e){
//      logger.error(Constant.omgTokenName+"归集线程异常："+e.getMessage());
//      e.printStackTrace();
//    }
//    logger.info(Constant.omgTokenName+"归集线程结束");
//  }
//
//}
