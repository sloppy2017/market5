package com.c2b.coin.user.service;

import com.c2b.coin.user.entity.UserAccess;

import java.util.List;

/**
 *
 *
 * @auther: tangwei
 * @date: 2018/5/17
 */
public interface IUserAccessService {

  UserAccess create(long userId, String allowIp, String remark);

  List<UserAccess> findByUserId(long userId);

  void update(long userId, int id, String allowIp, String remark);

  void delete(long userId, int id);

}
