package com.c2b.coin.market.mapper;

import com.c2b.coin.market.entity.MatchMoney;
import com.coin.config.mybatis.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface MatchMoneyMapper extends BaseMapper<MatchMoney> {

  @Select("select * from order_log where biz_type = #{currency} and made_time < #{end} and made_time >= #{begin} order by made_time ")
//@Select("select * from match_money where currency = #{currency} and time < #{end} and time >= #{begin}")
  public List<Map<String,Object>> getDataByTime(@Param("currency") String currency,
                                                @Param("begin") Long begin, @Param("end") Long end);

  @Select(" SELECT IFNULL(MAX(made_price),0) AS highest, IFNULL(MIN(made_price),0) AS lowest,IFNULL(SUM(made_count),0) AS volum, " +
    " IFNULL((SELECT made_price FROM order_log WHERE biz_type = #{currency} " +
    " AND made_time < #{end}  AND made_time >= #{begin} ORDER BY made_time ASC LIMIT 1),0) AS 'open'," +
    " IFNULL((SELECT made_price FROM order_log WHERE biz_type = #{currency} " +
    " AND made_time < #{end}  AND made_time >= #{begin} ORDER BY made_time DESC LIMIT 1),0) AS 'close' " +
    " FROM order_log WHERE biz_type = #{currency} " +
    " AND made_time < #{end}  AND made_time >= #{begin} ORDER BY made_time ")
  public Map<String,Object> getKDate(@Param("currency") String currency,
                                     @Param("begin") Long begin, @Param("end") Long end);

//  @Select("select * from order_log where currency= #{currency} order by time desc limit 1, #{n}")
  @Select("select * from order_log where biz_type = #{currency} order by made_time desc limit 1, #{n}")
  public List<Map<String,Object>> getTopNRealTimePriceBycurrency(@Param("currency") String currency, @Param("n") Integer n);

//  @Select("select * from match_money where time = (select max(time) from match_money where currency = #{currency} " +
//    "and time < current_timestamp() and time >= date_add(current_timestamp(), interval -1 day) )  " +
//    "and currency = #{currency}")
  @Select("select * from order_log where biz_type = #{currency} and made_time >= UNIX_TIMESTAMP(date_add(now(), interval -1 day))*1000 order by made_time desc limit 1")
  public Map<String,Object> getRealTimePriceBycurrency(@Param("currency") String currency);

//  @Select("select * from match_money where time = (select min(time) from match_money where currency = #{currency} " +
//    "and time < current_timestamp() and time >= date_add(current_timestamp(), interval -1 day) )  " +
//    "and currency = #{currency}")
  @Select("select * from order_log where biz_type = #{currency} and made_time >= UNIX_TIMESTAMP(date_add(now(), interval -1 day))*1000 order by made_time limit 1")
  public Map<String,Object> get24HOldPriceBycurrencyTime(@Param("currency") String currency);

//  @Select("select max(money) from match_money where currency = #{currency} and time < current_timestamp() and " +
//    " time >= date_add(current_timestamp(), interval -1 day) ")
  @Select("select max(made_price) from order_log where biz_type = #{currency} and made_time >= UNIX_TIMESTAMP(date_add(now(), interval -1 day))*1000")
  public Map<String,Object> get24HMaxPriceBycurrency(@Param("currency") String currency);

//  @Select("select min(money) from match_money where currency = #{currency} and time < current_timestamp() and " +
//    " time >= date_add(current_timestamp(), interval -1 day) ")
  @Select("select min(made_price) from order_log where biz_type = #{currency} and  made_time >= UNIX_TIMESTAMP(date_add(now(), interval -1 day))*1000")
  public Map<String,Object> get24HMinPriceBycurrency(@Param("currency") String currency);

  @Select("select sum(value) from match_sum where currency = #{currency} and `key` = #{type} " +
    "and time < current_timestamp() and time >= date_add(current_timestamp(), interval -1 day) ")
  public Map<String,Object> getRealTimeCountBycurrency(@Param("currency") String currency, @Param("time") String time, @Param("type") String type);

  @Select("select sum(value) volume from match_sum where currency = #{currency} and `key` = #{type} " +
    "and UNIX_TIMESTAMP(date) < #{end} and UNIX_TIMESTAMP(date) >= #{begin} ")
  public Map<String,Object> getCountBycurrency(@Param("currency") String currency, @Param("begin") Long begin, @Param("end") Long end, @Param("type") String type);

  @Select("select max(made_price) max ,min(made_price) min ,sum(made_count)/2 sum from order_log where  biz_type = #{currency}" +
    " and made_time >= UNIX_TIMESTAMP(date_add(now(), interval -1 day))*1000 ")
  public Map<String,Object> get24HMaxMinSumPriceBycurrency(@Param("currency") String currency);

  @Select("select * from digital_coin where is_enabled = 1")
  public List<Map<String,Object>> getCoinEnable();

  @Select("select * from trade_pair_info where data_status = 0 order by money_coin_name")
  public List<Map<String,Object>> getTradePairs();

  @Select("select distinct money_coin_name from trade_pair_info where data_status = 0")
  public List<Map<String,Object>> getTradeBaseCoin();

  @Select("select commodity_coin_name from trade_pair_info where data_status = 0 " +
    "and money_coin_name = #{moneyCoin}")
  public List<Map<String,Object>> getTradeTradeCoinByBase(String moneyCoin);


}
