package com.c2b.coin.trade.mapper;

import com.c2b.coin.trade.entity.OrderLog;
import com.coin.config.mybatis.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface OrderLogMapper extends BaseMapper<OrderLog> {


  List<Map<String, Object>> listOrderLog(String bizType);

  @Select("<script> SELECT made_time AS create_time,biz_type,trade_type,consignation_price,consignation_count, made_count, made_price AS made_average_price,4 AS consignation_status, after_consignation_count from order_log where 1=1  <when test='userId!=null'>and user_id=#{userId} </when> and made_time BETWEEN #{startTimeStamp} and #{endTimeStamp}  ORDER BY made_time DESC </script>")
  List<Map<String, Object>> listOrderLogForTwentyFour(@Param("userId") Integer userId, @Param("startTimeStamp") long startTimeStamp, @Param("endTimeStamp") long endTimeStamp);

  int updateOrderLog(OrderLog orderLog);

  @Select("<script> SELECT made_time AS create_time,biz_type,trade_type,consignation_price,consignation_count, made_count, made_price AS made_average_price," +
    "4 AS consignation_status, after_consignation_count AS unTradeCount, case trade_type when 3 then consignation_count when 4 then consignation_count else consignation_count * consignation_price " +
    "end as consignationTotalMoney " +
//    "FROM order_log WHERE user_id = #{userId} and made_count=consignation_count <when test='bizType!=null'>and biz_type=#{bizType} " +
    "FROM order_log WHERE user_id = #{userId} <when test='bizType!=null'>and biz_type=#{bizType} " +
    "</when>  order by made_time desc </script>")
  List<Map<String, Object>> listOrder(@Param("userId") Long userId, @Param(value = "bizType") String bizType);

  @Select("select * from order_log where consignation_no = #{consignationNo}")
  List<OrderLog> listOrders(String consignationNo);
}
