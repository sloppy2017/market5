package com.c2b.coin.account.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.c2b.coin.account.entity.UserAccount;
import com.c2b.coin.account.entity.vo.AccountAssetVO;

import tk.mybatis.mapper.common.BaseMapper;



public interface UserAccountMapper extends BaseMapper<UserAccount> {

	List<AccountAssetVO> selectAccountAssetVO(long userId);

	UserAccount selectByUserIdAndCurrencyTypeForUpdate(@Param(value = "userId") Long userId,@Param(value = "currencyType") Integer currencyType);

	UserAccount selectByAddressAndCurrencyTypeForUpdate(@Param(value="address")String address, @Param(value="currencyType")int currencyType);
  @Select("select id as id,user_id as userId, currency_type as currencyType,currency_name as currencyName,total_amount as totalAmoun,available_amount as availableAmount,freezing_amount as freezingAmount," +
    "account_type as accountType ,account_address as accountAddress,update_time as updateTime ,createtime as createtime from user_account where user_id = #{userId} and currency_type = #{currencyType}")
  UserAccount selectByUserIdAndCurrencyType(@Param(value = "userId") Long userId,@Param(value = "currencyType") Integer currencyType);
	@Update("update user_account set available_amount = available_amount + #{availableAmount}, total_amount = total_amount + #{totalAmount},freezing_amount = freezing_amount + #{freezingAmount} where user_id = #{userId} and currency_type = #{currencyType}")
	int updateAccount(@Param(value = "userId")long userId,@Param(value = "currencyType") int currencyType,@Param(value = "availableAmount") BigDecimal availableAmount,@Param(value = "totalAmount")BigDecimal totalAmount, @Param(value = "freezingAmount")BigDecimal freezingAmount);



}
