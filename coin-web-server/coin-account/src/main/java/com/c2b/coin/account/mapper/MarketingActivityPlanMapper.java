package com.c2b.coin.account.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.c2b.coin.account.entity.MarketingActivityPlan;
import com.coin.config.mybatis.BaseMapper;

public interface MarketingActivityPlanMapper extends BaseMapper<MarketingActivityPlan> {

  @Select("select * from marketing_activity_plan where is_effective = 1")
  List<Map<String,Object>> findEffectivePlan();

  @Select("select * from marketing_activity_plan where is_effective = 1 and now() between start_time and end_time")
  List<Map<String,Object>> findEffectivePlanInDate();


  @Select("select count(1) num from asset_log where user_id = #{userId} and status = 3 and operation_type= 1 " +
    " and FROM_UNIXTIME(createtime/1000) between " +
    "(select start_time from marketing_activity_plan where is_effective = 1) and " +
    "(select end_time from marketing_activity_plan where is_effective = 1)")
  List<Map<String,Object>> getFirstDeposit(@Param("userId") Long userId);

  @Select("select * from order_log where user_id= #{userId}")
  List<Map<String,Object>> getTrade(@Param("userId") Long userId);


  @Select("select  * from  asset_log where user_id= #{userId} and remark = #{remark} ")
  List<Map<String,Object>> getFreeCoin(@Param("userId") Long userId, @Param("remark") String remark);

  @Select("select u.id id,u.mobile mobile,a.actual_asset asset from user_info u ,asset_log a " +
    " where u.id=a.user_id and u.mobile =#{phone} and a.remark = #{remark} ")
  List<Map<String,Object>> getGivenCoinByPhone(@Param("phone") String phone, @Param("remark") String remark);

  @Select("select * from user_info where username = #{userName}")
  List<Map<String,Object>> getUserInfoByUserName(@Param("userName") String userName);
}
