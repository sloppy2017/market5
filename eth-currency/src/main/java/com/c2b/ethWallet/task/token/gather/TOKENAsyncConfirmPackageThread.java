//package com.c2b.ethWallet.task.token.gather;
//
//import java.util.List;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import com.c2b.ethWallet.entity.IcoGatherRecord;
//import com.c2b.ethWallet.service.IcoGatherRecordService;
//import com.c2b.ethWallet.util.Constant;
//
///**  
// * 类说明   
// *  
// * @author Anne  
// * @date 2017年12月23日 
// */
//@Component("tokenAsyncConfirmPackageThread")
//public class TOKENAsyncConfirmPackageThread{
//
//  private Logger logger = LoggerFactory.getLogger(getClass());
//  
//  @Autowired
//  private IcoGatherRecordService gatherRecordService;
//  
//  @Autowired
//  private TOKENAsyncConfirmPackage tokenAsyncConfirmPackage;
//  
//  /*@Scheduled(cron = "25/40 * * * * ?")
//  public void runZg() {
//    logger.info(Constant.zgTokenName+"确认打包线程开始");
//    try{
//      List<IcoGatherRecord> gatherRecordList = gatherRecordService.listTOKENGatherRecord(Constant.zgTokenName);
//      if(gatherRecordList!=null && gatherRecordList.size()>0){
//        for(IcoGatherRecord gatherRecord:gatherRecordList){
//          tokenAsyncConfirmPackage.todoGather(gatherRecord);
//        }
//      }
//    }catch(Exception e){
//      logger.info(Constant.zgTokenName+"确认打包线程异常："+e.getMessage());
//      e.printStackTrace();
//    }
//    logger.info(Constant.zgTokenName+"确认打包线程结束");
//  }*/
//  
//  @Scheduled(cron = "20/35 * * * * ?")
//  public void runOmg() {
//    logger.info(Constant.omgTokenName+"确认打包线程开始");
//    try{
//      List<IcoGatherRecord> gatherRecordList = gatherRecordService.listTOKENGatherRecord(Constant.omgTokenName);
//      if(gatherRecordList!=null && gatherRecordList.size()>0){
//        for(IcoGatherRecord gatherRecord:gatherRecordList){
//          tokenAsyncConfirmPackage.todoGather(gatherRecord);
//        }
//      }
//    }catch(Exception e){
//      logger.info(Constant.omgTokenName+"确认打包线程异常："+e.getMessage());
//      e.printStackTrace();
//    }
//    logger.info(Constant.omgTokenName+"确认打包线程结束");
//  }
//
//}
