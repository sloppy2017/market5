package com.c2b.coin.user.mapper;

import com.c2b.coin.user.entity.UserAccess;
import com.coin.config.mybatis.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface UserAccessMapper extends BaseMapper<UserAccess> {

  @Select({"select id, user_id, access_key_id, allow_ip, remark, expire_date, create_time from user_access where user_id = #{userId} order by id desc"})
  List<UserAccess> findByUserId(@Param("userId") long userId);

  @Update({"update user_access as a set a.allowIp = #{allowIp}, a.remark = #{remark} where a.userId = #{userId} and a.id = #{id} limit 1"})
  void updateByUserId(@Param("userId") long userId, @Param("id") int id, @Param("allowIp") String allowIp, @Param("remark") String remark);

  @Delete({"delete user_access as a where a.userId = #{userId} and a.id = #{id} limit 1"})
  void deleteByUserId(@Param("userId") long userId, @Param("id") int id);

}
