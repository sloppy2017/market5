package com.c2b.coin.trade.mapper;

import com.c2b.coin.trade.entity.ConsignationLog;
import com.coin.config.mybatis.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Anne
 *
 */
@Service
public interface ConsignationLogMapper extends BaseMapper<ConsignationLog> {

  @Select("  <script> SELECT username,consignation_no,biz_type,trade_type,concat('',consignation_price) as consignation_price,concat('',consignation_count) as consignation_count,"
      + "concat('',made_count) as made_count,concat('',made_price) as made_price,consignation_status,data_status,create_time,made_time,update_time,remark,concat('',made_average_price) as  made_average_price"
      + " FROM consignation_log WHERE data_status=0 AND user_id=#{userId} and "
      + " consignation_status=#{consignationStatus} <when test='bizType!=0'>and biz_type=#{bizType} </when>  "
      + "  order by create_time desc  </script>")
  List<Map<String, Object>> listConsignationOrderByUserIdAndStatus(
    @Param(value = "userId") String userId,
    @Param(value = "consignationStatus") int consignationStatus,
    @Param(value = "bizType") int bizType);

  @Select(" <script> SELECT username,consignation_no,biz_type,trade_type,concat('',consignation_price) as consignation_price,concat('',consignation_count) as consignation_count,"
      + "concat('',made_count) as made_count,concat('',made_price) as made_price,consignation_status,data_status,create_time,made_time,update_time,remark,concat('',made_average_price) as  made_average_price"
      + " FROM consignation_log WHERE consignation_status !=1 and data_status=0 <when test='userId!=null'> AND user_id=#{userId} </when> <when test='bizType!=0'>and biz_type=#{bizType} </when> "
      + " order by create_time desc </script>")
  List<Map<String, Object>> listConsignationOrderByUserId(
    @Param(value = "userId") String userId,
    @Param(value = "bizType") int bizType);

  int updateConsignation(ConsignationLog consignationLog);

  int matchAfterUpdateConsignation(ConsignationLog consignationLog);

  @Update("update consignation_log set consignation_status = #{consignationStatus} where consignation_no=#{consignationNo}")
  int updateConsignationStatus(ConsignationLog consignationLog);

  /**
   * @modify guoxp
   * 仅显示限价交易委托单
   * @param userId
   * @return
   */
  @Select("SELECT username,consignation_no,biz_type,trade_type,concat('',consignation_price) as consignation_price,concat('',consignation_count) as consignation_count,"
      + " concat('',made_count) as made_count,concat('',made_price) as made_price,consignation_status,data_status,create_time,made_time,update_time,"
      + " remark,concat('',made_average_price) as made_average_price FROM consignation_log WHERE user_id = #{userId} and consignation_status not in (4,5) and data_status=0 and made_count<consignation_count "
      + " and trade_type in (1,2) order by create_time desc ")
  List<Map<String, String>> listUnTradeOrder(@Param("userId") Long userId);

  @Update("update consignation_log set consignation_status = 5 where user_id = #{userId} and consignation_status not in (4,5)")
  int revokeAllOrder(@Param("userId") Long userId);

  @Update("UPDATE consignation_log SET consignation_status = #{status} where consignation_no = #{seq}")
  int updateStatus(@Param("seq") String seq, @Param("status") Integer status);

  @Select("select * from consignation_log where consignation_no = #{seq}")
  Map<String,Object> findConsingationBySeq(@Param("seq") String seq);

  int updateConsignationWithEnd(ConsignationLog consignationLog);

  int updateMarketConsignationWithEnd(ConsignationLog consignationLog);

  @Select("  <script> SELECT c.username,c.consignation_no,c.biz_type,c.trade_type,concat('',c.consignation_price) as consignation_price,concat('',c.consignation_count) as consignation_count,"
      + "concat('',c.made_count) as made_count,concat('',c.made_price) as made_price,c.consignation_status,c.data_status,c.create_time,c.made_time,c.update_time,c.remark,concat('',c.made_average_price) as  made_average_price"
      + " FROM consignation_log  c LEFT JOIN trade_pair_info t ON c.biz_type = t.id WHERE c.data_status=0 AND c.user_id=#{userId} and "
      + " c.consignation_status=#{consignationStatus} <when test='moneyCoinID!=0'>and t.money_coin=#{moneyCoinID} AND t.data_status=0 </when>  "
      + "  order by c.create_time desc  </script>")
  List<Map<String, Object>> listConsignationOrderByUserIdAndStatusAndMoneyCoin(
    @Param(value = "userId") String userId,
    @Param(value = "consignationStatus") int consignationStatus,
    @Param(value = "moneyCoinID") int moneyCoinID);

  @Select(" <script> SELECT c.username,c.consignation_no,c.biz_type,c.trade_type,concat('',c.consignation_price) as consignation_price,concat('',c.consignation_count) as consignation_count,"
      + "concat('',c.made_count) as made_count,concat('',c.made_price) as made_price,c.consignation_status,c.data_status,c.create_time,c.made_time,c.update_time,concat('',c.made_average_price) as  made_average_price"
      + " FROM consignation_log c LEFT JOIN trade_pair_info t ON c.biz_type = t.id WHERE c.data_status=0 <when test='userId!=null'> AND c.user_id=#{userId} </when> <when test='moneyCoinID!=0'>and t.money_coin=#{moneyCoinID} AND t.data_status=0 </when> "
      + " order by c.create_time desc </script>")
  List<Map<String, Object>> listConsignationOrderByUserIdAndMoneyCoin(
    @Param(value = "userId") String userId,
    @Param(value = "moneyCoinID") int moneyCoinID);

  @Select(" <script> SELECT username,consignation_no,biz_type,trade_type,concat('',consignation_price) as consignation_price,concat('',consignation_count) as consignation_count,"
      + "concat('',made_count) as made_count,concat('',made_price) as made_price,consignation_status,data_status,create_time,made_time,update_time,remark,concat('',made_average_price) as  made_average_price"
      + " FROM consignation_log WHERE data_status=0 and consignation_status in(1,2,3) and trade_type in (1,2) <when test='userId!=null'> AND user_id=#{userId} </when>"
      + " order by create_time desc </script>")
  List<Map<String, Object>> listConsignationOrderByUserIdForRevokeOrder(@Param(value = "userId") String userId);
}
