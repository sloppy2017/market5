package com.c2b.ethWallet.task;


import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.c2b.ethWallet.entity.WithdrawLog;
import com.c2b.ethWallet.mapper.WithdrawLogMapper;
import com.c2b.ethWallet.service.EthWalletService;

/**
 * 
* @ClassName: MonitorWithDrawThread 
* @Description: TODO(以太坊提币监控线程类) 
* @author 焦博韬  
* @date 2017年10月12日 上午10:47:46 
*注：因为现在提币直接就是send状态，没有审核状态，所以此线程无需启用！！！！！！
 */
@Component("monitorWithDrawThread")
public class MonitorWithDrawThread implements Runnable {
    private Logger logger = org.apache.log4j.Logger.getLogger(MonitorWithDrawThread.class);

    @Autowired
    private EthWalletService ethWalletService;

    @Autowired
    private WithdrawLogMapper withdrawLogMapper;

//    @Scheduled(cron = "0 1/3 * * * ? ")
    @Override
    public void run() {

        logger.info("以太坊提币线程-----开始------------------" + new Date());
        List<WithdrawLog> records = withdrawLogMapper.listETHApprove();

        for (WithdrawLog withdrawRecord : records) {
            try {
                ethWalletService.sendMoney(withdrawRecord);
            } catch (Exception e) {
                e.printStackTrace();
            }
            logger.info(" WithdrawRecord: " + withdrawRecord + " ");
        }
        logger.info("以太坊提币线程-----结束---------------------");

    }
}