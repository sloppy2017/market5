package com.c2b.coin.user.service;

import com.c2b.coin.user.entity.UserAccess;
import com.c2b.coin.user.vo.UserAccessVo;

import java.util.List;

/**
 *
 *
 * @auther: tangwei
 * @date: 2018/5/17
 */
public interface IUserAccessService {

  UserAccess create(long userId, String allowIp, String remark);

  List<UserAccessVo> findByUserId(long userId);

  void update(long userId, String accessKeyId, String allowIp, String remark);

  void delete(long userId, String accessKeyId);

}
