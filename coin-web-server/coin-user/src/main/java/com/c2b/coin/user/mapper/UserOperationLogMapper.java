package com.c2b.coin.user.mapper;

import com.c2b.coin.user.entity.UserOperationLog;
import com.coin.config.mybatis.BaseMapper;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public abstract interface UserOperationLogMapper extends BaseMapper<UserOperationLog>
{
  @Select({"SELECT * FROM user_operation_log WHERE operation = 'LOGIN' AND username = #{username}  LIMIT 1,1"})
  public abstract Map<String, Object> findLastLoginLog(@Param("username") String paramString);
}