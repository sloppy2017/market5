package com.c2b.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.c2b.wallet.service.LiteWalletService;

@Service
public class LiteTask {

    Logger log = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private LiteWalletService liteWalletService;
    
    /*@Scheduled(cron = "0 0/1 * * * ? ")
    public void payResultQuery() {
        log.info("莱特币提币定时器---------start----------" + new Date());
        
        try {
			List<WithdrawRecord> records = withdrawRecordService.listAllApprove("LTC");
			for (WithdrawRecord withdrawRecord : records) {
			    log.info(" WithdrawRecord: {} ", JSON.toJSONString(withdrawRecord));
			    if("LTC".equals(withdrawRecord.getCurrency())){
//			        UserLogin userLogin = userLoginService.getUserLoginByUserId(withdrawRecord.getUserId());
			        WithdrawRecord record = new WithdrawRecord();
			        record.setId(withdrawRecord.getId());
//			        record.setUserName(userLogin.getUserName());
//			        record.setPassword(userLogin.getPassword());
			        record.setMoney(withdrawRecord.getMoney());
			        record.setAddress(withdrawRecord.getAddress());
			    	liteWalletService.sendMoney(LiteWalletAsync.params, withdrawRecord);
			    }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        log.info("莱特币提币定时器----------end------------"+ new Date());
    }*/
}
