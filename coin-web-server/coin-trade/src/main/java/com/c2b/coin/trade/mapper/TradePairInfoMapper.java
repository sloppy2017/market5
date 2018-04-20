package com.c2b.coin.trade.mapper;

import com.c2b.coin.trade.entity.TradePairInfo;
import com.coin.config.mybatis.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Anne
 * @date 2017.10.19
 */
public interface TradePairInfoMapper extends BaseMapper<TradePairInfo>{

  @Select("select * from digital_coin where is_enabled=1")
  List<Map<String, Object>> listCurrency();

  @Select("select * from digital_coin where is_enabled=1 and id=#{id}")
  Map<String, Object> getDigitalCoinById(@Param(value = "id") Long id);

  List<TradePairInfo> listTradePairInfo();

  @Select("select commodity_coin as commodityCoin ,money_coin as moneyCoin from trade_pair_info WHERE data_status=0 AND id=#{id}")
  TradePairInfo getTradePairInfoByPK(@Param(value = "id") Long id);

  TradePairInfo getTradePairInfoValueByPK(Long id);

  @Select("select id,commodity_coin,commodity_coin_name,money_coin,money_coin_name FROM trade_pair_info WHERE data_status=0 AND money_coin=#{moneyCoin}")
  List<Map<String, Object>> listTradePairByMoneyCoin(@Param(value = "moneyCoin") Long moneyCoin);

  @Select("select commodity_coin as commodityCoin,commodity_coin_name as commodityCoinName,money_coin as moneyCoin,money_coin_name as moneyCoinName FROM trade_pair_info WHERE data_status=0")
  List<Map<String, Object>> listTradePair();
}
