package com.c2b.coin.account.aspect;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.c2b.coin.account.annotation.MarketingActivity;
import com.c2b.coin.account.mapper.MarketingActivityPlanMapper;
import com.c2b.coin.account.service.impl.ActivityAssetService;
import com.c2b.coin.common.MarketingActivityType;
import com.c2b.coin.common.MarketingRandomCoinNumber;

@Aspect
@Component
public class MarketingActivityAspect {

  private Logger logger = LoggerFactory.getLogger(MarketingActivityAspect.class);


  @Autowired
  private MarketingActivityPlanMapper marketingActivityPlanMapper ;

  @Autowired
  private ActivityAssetService activityAssetService;

  @Pointcut("@annotation(com.c2b.coin.account.annotation.MarketingActivity)")
  public void activityPointCut(){}

  @AfterReturning("activityPointCut()")
  public void afterReturning(JoinPoint joinPoint) {

    try{
      List<Map<String,Object>> effectivePlanList = this.marketingActivityPlanMapper.findEffectivePlanInDate();
      if(effectivePlanList == null || effectivePlanList.size() == 0){
        return;
      }

      MethodSignature signature = (MethodSignature) joinPoint.getSignature();
      Method method = signature.getMethod();
      MarketingActivity marketingActivity = method.getAnnotation(MarketingActivity.class);
      if(marketingActivity == null || marketingActivity.value().length == 0 ){
        return;
      }

      String[] value = marketingActivity.value();

      Object[] args = joinPoint.getArgs();
      String userName ;
      try{
        userName = (String)args[2];
      }catch (Exception e){
        return;
      }
      logger.debug("userName = "+userName);

      List<Map<String,Object>> list = marketingActivityPlanMapper.getUserInfoByUserName(userName);
      if (null == list || list.size() == 0 ) {
        return;
      }
      Long userId = (Long)list.get(0).get("id");
      if(Arrays.asList(value).contains(MarketingActivityType.FIREST_RECHARGE_TRADE)){//首次充币并交易送以太坊
        activityFreeETH(userId,userName);
        //TODO 首次充币并交易送以太坊通知
      }

    }catch (Exception e){
      e.printStackTrace();
    }
  }

  private synchronized void activityFreeETH(Long userId, String userName) {
    List<Map<String, Object>> list = null;
    //是否已经进行过活动增币
    list = this.marketingActivityPlanMapper.getFreeCoin(userId, MarketingActivityType.FIREST_RECHARGE_TRADE);
    if (list == null || list.size() == 0) {
      //是否进行过充币
      List<Map<String, Object>> buyerFirstDep = marketingActivityPlanMapper.getFirstDeposit(userId);
      if (buyerFirstDep != null && buyerFirstDep.size() > 0  && buyerFirstDep.get(0).get("num").equals(Long.valueOf(1))) {
        //是否进行过交易
        List<Map<String, Object>> tradeList = marketingActivityPlanMapper.getTrade(userId);
        if (null != tradeList && tradeList.size() > 0) {
          activityAssetService.addActivityAsset(userId,  userName,2, MarketingRandomCoinNumber.generateETHNumber(), 4, MarketingActivityType.FIREST_RECHARGE_TRADE);
        }
      }
    }
  }
}
