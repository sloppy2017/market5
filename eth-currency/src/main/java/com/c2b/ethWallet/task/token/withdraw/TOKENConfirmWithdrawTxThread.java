package com.c2b.ethWallet.task.token.withdraw;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.c2b.ethWallet.entity.WithdrawLog;
import com.c2b.ethWallet.mapper.WithdrawLogMapper;
import com.c2b.ethWallet.util.Constant;

/**  
 * 类说明   
 *  
 * @author Anne  
 * @date 2017年12月23日 
 */
@Component("tokenConfirmWithdrawTxThread")
public class TOKENConfirmWithdrawTxThread {
  
  private Logger logger = LoggerFactory.getLogger(getClass());
  
  @Autowired
  private WithdrawLogMapper withdrawLogMapper;

  @Autowired
  private TOKENConfirmWithdrawTx tokenConfirmWithdrawTx;
  
  /*@Scheduled(cron = "15/25 * * * * ?")
  public void runZg() {
    logger.info(Constant.zgTokenName+"提币确认线程开始");
    List<WithdrawLog> withdrawLogList = null;
    try {
      // 获取所有已发送状态的OMG提币记录列表
      withdrawLogList = withdrawLogMapper.listTOKENSend(Constant.zgTokenName);
      if(withdrawLogList != null && withdrawLogList.size()>0){
        tokenConfirmWithdrawTx.doTokenConfirmWork(Constant.zgTokenName, withdrawLogList);
      }
    } catch (Exception e) {
      logger.error(Constant.zgTokenName+"提币确认线程异常："+e.getMessage());
      e.printStackTrace();
    }
    logger.info(Constant.zgTokenName+"提币确认线程结束");
  }*/

  @Scheduled(cron = "10/20 * * * * ?")
  public void runOmg() {
    logger.info(Constant.omgTokenName+"提币确认线程开始");
    List<WithdrawLog> withdrawLogList = null;
    try {
      // 获取所有已发送状态的OMG提币记录列表
      withdrawLogList = withdrawLogMapper.listTOKENSend(Constant.omgTokenName);
      if(withdrawLogList != null && withdrawLogList.size()>0){
        tokenConfirmWithdrawTx.doTokenConfirmWork(Constant.omgTokenName, withdrawLogList);
      }
    } catch (Exception e) {
      logger.error(Constant.omgTokenName+"提币确认线程异常："+e.getMessage());
      e.printStackTrace();
    }
    logger.info(Constant.omgTokenName+"提币确认线程结束");
  }
  
}
