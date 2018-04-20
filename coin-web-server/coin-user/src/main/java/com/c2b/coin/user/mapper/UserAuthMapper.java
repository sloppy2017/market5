package com.c2b.coin.user.mapper;

import com.c2b.coin.user.entity.UserAuth;
import com.coin.config.mybatis.BaseMapper;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public abstract interface UserAuthMapper extends BaseMapper<UserAuth>
{
  @Select({"select * from user_auth where user_id = #{userId} "})
  public abstract List<HashMap> selectByUserId(@Param("userId") Long paramLong);
}