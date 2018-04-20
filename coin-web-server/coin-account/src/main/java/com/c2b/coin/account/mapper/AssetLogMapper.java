package com.c2b.coin.account.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.c2b.coin.account.entity.AssetLog;
import com.coin.config.mybatis.BaseMapper;

public interface AssetLogMapper extends BaseMapper<AssetLog> {

	List<AssetLog> selectAssetLogOrderByCreateTime(@Param("type")Integer type, @Param("userId")long userId);

	List<AssetLog> findByCurrency(@Param("type")Integer type, @Param("userId")long userId, @Param("currencyType") Integer currencyType, @Param("startDate") Long startDate, @Param("endDate") Long endDate);

	@Select("SELECT currency_name as currency_name, sum(actual_asset) as actual_asset FROM asset_log WHERE user_id = #{userId} AND remark = #{remark} group by currency_name")
  List<Map<String,Object>> selectActivityCoin(@Param("userId") long userId,@Param("remark") String activityName);
}
