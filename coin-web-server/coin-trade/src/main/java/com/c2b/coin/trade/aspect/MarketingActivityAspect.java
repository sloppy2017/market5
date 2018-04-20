package com.c2b.coin.trade.aspect;

import com.c2b.coin.account.api.AccountClient;
import com.c2b.coin.account.api.ActivityClient;
import com.c2b.coin.common.MarketingActivityType;
import com.c2b.coin.common.MarketingRandomCoinNumber;
import com.c2b.coin.common.enumeration.ErrorMsgEnum;
import com.c2b.coin.trade.annotation.MarketingActivity;
import com.c2b.coin.trade.entity.ConsignationLog;
import com.c2b.coin.trade.entity.DigitalCoin;
import com.c2b.coin.trade.exceptions.TradeException;
import com.c2b.coin.trade.mapper.MarketingActivityPlanMapper;
import com.c2b.coin.trade.service.ConsignationService;
import com.c2b.coin.trade.service.DigitalCoinService;
import com.c2b.coin.trade.vo.MatchInfoVO;
import com.c2b.coin.web.common.RedisUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Aspect
@Component
public class MarketingActivityAspect {

  private Logger logger = LoggerFactory.getLogger(MarketingActivityAspect.class);


  @Autowired
  private MarketingActivityPlanMapper marketingActivityPlanMapper ;

  @Autowired
  ConsignationService consignationService;

  @Autowired
  DigitalCoinService digitalCoinService;

  @Autowired
  AccountClient accountClient;

  @Autowired
  ActivityClient activityClient;

  @Autowired
  RedisUtil redisUtil;
  @Autowired
  ThreadPoolTaskExecutor taskExecutor;

  @Pointcut("@annotation(com.c2b.coin.trade.annotation.MarketingActivity)")
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
      MarketingActivity marketingActivity = method.getAnnotation(com.c2b.coin.trade.annotation.MarketingActivity.class);
      if(marketingActivity == null || marketingActivity.value().length == 0 ){
        return;
      }

      String[] value = marketingActivity.value();

      Object[] args = joinPoint.getArgs();
      MatchInfoVO matchInfoVO = null;
      try{
        matchInfoVO = (MatchInfoVO)args[0];
      }catch (Exception e){
        return;
      }
      logger.debug(matchInfoVO.toString());
      MatchInfoVO finalMatchInfoVO = matchInfoVO;
      taskExecutor.execute(()->{
        ConsignationLog buyConsignationLog = consignationService.findBySeq(finalMatchInfoVO.getBuySeq());
        ConsignationLog sellConsignationLog = consignationService.findBySeq(finalMatchInfoVO.getSellSeq());
//        if (null == buyConsignationLog || null == sellConsignationLog) {
//          throw new TradeException(ErrorMsgEnum.MATCH_FAIL);
//        }
        DigitalCoin commodityDigitalCoin = digitalCoinService.findByName(finalMatchInfoVO.getCurrency().split("/")[0]);
        DigitalCoin moneyDigitalCoin = digitalCoinService.findByName(finalMatchInfoVO.getCurrency().split("/")[1]);
//        if (commodityDigitalCoin == null || moneyDigitalCoin == null) {
//          throw new TradeException(ErrorMsgEnum.MATCH_FAIL);
//        }

//    if(Arrays.asList(value).contains(MarketingActivityType.REGISTER)){//新用户注册送比特币
//
//
//    }else

        if(Arrays.asList(value).contains(MarketingActivityType.FIREST_RECHARGE_TRADE)){//首次充币并交易送以太坊
          activityFreeETH(buyConsignationLog);
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          activityFreeETH(sellConsignationLog);
          //TODO 首次充币并交易送以太坊通知
        }
        if(Arrays.asList(value).contains(MarketingActivityType.ALL_TRADE)){  //全站交易免手续费
          logger.debug("买方userId："+buyConsignationLog.getUserId()+" 买方userName:"+buyConsignationLog.getUsername()+" 买方手续费："+ finalMatchInfoVO.getBuyMoney());
          logger.debug("卖方userId："+sellConsignationLog.getUserId()+" 卖方userName:"+sellConsignationLog.getUsername()+" 卖方手续费："+ finalMatchInfoVO.getSellMoney());
          accountClient.discountPoundage( buyConsignationLog.getUserId(),
            buyConsignationLog.getUsername(), sellConsignationLog.getUserId(), sellConsignationLog.getUsername(),
            finalMatchInfoVO.getSeq(), moneyDigitalCoin.getId(), commodityDigitalCoin.getId(), finalMatchInfoVO.getSellMoney(),
            finalMatchInfoVO.getBuyMoney(),MarketingActivityType.ALL_TRADE );
        }
      });

    }catch (Exception e){
      e.printStackTrace();
    }
  }

  private synchronized void activityFreeETH(ConsignationLog consignationLog) {
    List<Map<String, Object>> list = null;
    //是否已经进行过活动增币
    list = this.marketingActivityPlanMapper.getFreeCoin(consignationLog.getUserId(), MarketingActivityType.FIREST_RECHARGE_TRADE);
    if (list == null || list.size() == 0) {
      //是否进行过充币
      List<Map<String, Object>> buyerFirstDep = marketingActivityPlanMapper.getFirstDeposit(consignationLog.getUserId());
      if (buyerFirstDep != null && buyerFirstDep.size() > 0  && buyerFirstDep.get(0).get("num").equals(Long.valueOf(1))) {
        //是否进行过交易
        List<Map<String, Object>> tradeList = marketingActivityPlanMapper.getTrade(consignationLog.getUserId());
        if (null != tradeList && tradeList.size() > 0) {
          activityClient.addActivityAsset(consignationLog.getUserId(), 2, consignationLog.getUsername(), MarketingRandomCoinNumber.generateETHNumber(), 4, MarketingActivityType.FIREST_RECHARGE_TRADE);
        }
      }
    }
  }
}
