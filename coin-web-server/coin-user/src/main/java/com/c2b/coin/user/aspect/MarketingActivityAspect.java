package com.c2b.coin.user.aspect;

import com.c2b.coin.common.MarketingActivityType;
import com.c2b.coin.common.MarketingRandomCoinNumber;
import com.c2b.coin.user.feign.ActivityFeign;
import com.c2b.coin.user.mapper.MarketingActivityPlanMapper;
import com.c2b.coin.user.annotation.MarketingActivity;
import com.c2b.coin.user.entity.UserInfo;
import com.coin.config.cache.redis.RedisUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Aspect
@Component
//@DependsOn("activityClient")
public class MarketingActivityAspect {

  private Logger logger = LoggerFactory.getLogger(MarketingActivityAspect.class);

  @Autowired
  ActivityFeign activityFeign;

  @Autowired
  MarketingActivityPlanMapper marketingActivityPlanMapper ;

  @Autowired
  RedisUtil redisUtil;

  @Pointcut("@annotation(com.c2b.coin.user.annotation.MarketingActivity)")
  public void activityPointCut(){}

  @AfterReturning(pointcut="activityPointCut()",returning="rvt")
  public void afterReturning(JoinPoint joinPoint, Object rvt) throws Throwable {
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

    if(rvt==null){
      return;
    }
    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    UserInfo userInfo = (UserInfo) rvt;
    List<Map<String,Object>>  list = this.marketingActivityPlanMapper.getFreeCoin(userInfo.getId(),MarketingActivityType.REGISTER);
    if(list != null && list.size() > 0){
      return;
    }
    String key = "FREE_COIN_NUM_BTC";
    String coinNumStr =  redisUtil.hget(key, request.getSession().getId());
    if(StringUtils.isEmpty(coinNumStr)) coinNumStr = String.valueOf(MarketingRandomCoinNumber.generateBTCNumber());
    BigDecimal coinNum = new BigDecimal(coinNumStr.substring(1,coinNumStr.length()-1));

    if(Arrays.asList(value).contains(MarketingActivityType.REGISTER)){//新用户注册送比特币
      activityFeign.addActivityAsset(userInfo.getId(),1,userInfo.getUsername(),coinNum,3, MarketingActivityType.REGISTER);
    }
    //TODO 新用户注册送比特币通知

  }
}
