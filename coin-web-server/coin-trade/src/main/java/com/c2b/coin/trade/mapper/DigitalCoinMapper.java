package com.c2b.coin.trade.mapper;

import com.c2b.coin.trade.entity.DigitalCoin;
import com.coin.config.mybatis.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface DigitalCoinMapper extends BaseMapper<DigitalCoin> {

	List<DigitalCoin> listDigitalCoin();

	@Select("select id, coin_name AS coinName, coin_full_name AS coinFullName, is_enabled AS isEnabled , create_time AS createTime, update_time AS updateTime, remark from digital_coin WHERE is_enabled=1 AND coin_name = #{coinName}")
	DigitalCoin getDigitalCoinByCoinName(@Param("coinName") String coinName);
}
