package com.c2b.coin.user.mapper;

import com.c2b.coin.user.entity.UserNews;
import com.coin.config.mybatis.BaseMapper;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public abstract interface UserNewsMapper extends BaseMapper<UserNews>
{
  @Select({"select * from user_news where user_id = #{userId} and is_del = 0 and status = 0 order by createtime desc"})
  public abstract List<Map<String, Object>> findUserNews(@Param("userId") Long paramLong);

  @Update({"update user_news set is_del = 0 where user_id = #{userId} and is_del = 1 and status = 1"})
  public abstract int clearUserNews(@Param("userId") Long paramLong);

  @Update({"update user_news set is_read = 1 where user_id = #{userId} and is_del = 1 and status = 1"})
  public abstract int readAllUserNews(@Param("userId") Long paramLong);

  @Update({"update user_news set is_read = 1 where user_id = #{userId} and id = #{id} and is_del = 1 and status = 1"})
  public abstract int readUserNews(@Param("userId") Long paramLong1, @Param("id") Long paramLong2);

  @Select({"select count(1) as count from user_news where user_id = #{userId} and is_del = 0 and status = 0 and is_read = 0"})
  public abstract Map<String, Object> countNoRead(@Param("userId") Long paramLong);
}