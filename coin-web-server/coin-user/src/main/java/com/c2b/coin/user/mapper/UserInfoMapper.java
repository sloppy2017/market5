package com.c2b.coin.user.mapper;

import com.c2b.coin.user.entity.UserInfo;
import com.coin.config.mybatis.BaseMapper;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public abstract interface UserInfoMapper extends BaseMapper<UserInfo>
{
  @Update({"update user_info set is_auth = #{isAuth} where id = #{userId}"})
  public abstract void updateIsAuthByUserId(@Param("isAuth") Integer paramInteger, @Param("userId") Long paramLong);

  @Select({"select * from user_info where id = #{userId}"})
  public abstract Map<String, Object> selectByUserId(@Param("userId") long paramLong);
}