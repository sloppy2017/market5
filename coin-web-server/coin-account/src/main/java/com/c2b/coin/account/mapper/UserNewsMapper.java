package com.c2b.coin.account.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.c2b.coin.account.entity.UserNews;
import com.coin.config.mybatis.BaseMapper;

public interface UserNewsMapper extends BaseMapper<UserNews> {

  @Select("select * from user_news where user_id = #{userId} and is_del = 1 and status = 1 order by createtime desc")
  List<Map<String,Object>> findUserNews(@Param("userId") Long userId);

  @Update("update user_news set is_del = 0 where user_id = #{userId} and is_del = 1 and status = 1")
  int clearUserNews(@Param("userId") Long userId);

  @Update("update user_news set is_read = 1 where user_id = #{userId} and is_del = 1 and status = 1")
  int readAllUserNews(@Param("userId") Long userId);

  @Update("update user_news set is_read = 1 where user_id = #{userId} and id = #{id} and is_del = 1 and status = 1")
  int readUserNews(@Param("userId") Long userId,@Param("id") Long id);

  @Select("select count(1) as count from user_news where user_id = #{userId} and is_del = 1 and status = 1 and is_read = 0")
  Map<String,Object> countNoRead(@Param("userId") Long userId);
}
