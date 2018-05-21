package com.c2b.coin.user.mapper;

import com.c2b.coin.user.entity.UserAccess;
import com.c2b.coin.user.vo.UserAccessVo;
import com.coin.config.mybatis.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface UserAccessMapper extends BaseMapper<UserAccess> {

  @Select({"select id, user_id, access_key_id, allow_ip, remark, expire_date, create_time from user_access where user_id = #{userId} and access_key_id = #{accessKeyId} limit 1"})
  UserAccessVo getByUserId(@Param("userId") long userId, @Param("accessKeyId") String accessKeyId);

  @Select({"select id, user_id, access_key_id, allow_ip, remark, expire_date, create_time from user_access where user_id = #{userId} order by id desc"})
  List<UserAccessVo> findByUserId(@Param("userId") long userId);

  @Select({"select count(1) from user_access where user_id = #{userId}"})
  int findCountByUserId(@Param("userId") long userId);

  @Update({"update user_access as a set a.allow_ip = #{allowIp}, a.remark = #{remark} where a.user_id = #{userId} and a.access_key_id = #{accessKeyId} limit 1"})
  void updateByUserId(@Param("userId") long userId, @Param("accessKeyId") String accessKeyId, @Param("allowIp") String allowIp, @Param("remark") String remark);

  @Delete({"delete from user_access where user_id = #{userId} and access_key_id = #{accessKeyId} limit 1"})
  void deleteByUserId(@Param("userId") long userId, @Param("accessKeyId") String accessKeyId);

}
