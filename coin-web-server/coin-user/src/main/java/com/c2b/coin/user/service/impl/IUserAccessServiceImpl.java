package com.c2b.coin.user.service.impl;

import com.c2b.coin.user.entity.UserAccess;
import com.c2b.coin.user.mapper.UserAccessMapper;
import com.c2b.coin.user.service.IUserAccessService;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * 关注用户
 *
 * @author tangwei
 */
@Service
public class IUserAccessServiceImpl implements IUserAccessService {

  @Autowired
  protected UserAccessMapper userAccessMapper;

  @Override
  public UserAccess create(long userId, String allowIp, String remark) {
    UserAccess userAccess = new UserAccess();
    userAccess.setUserId(userId);
    userAccess.setAccessKeyId(UUID.randomUUID().toString());
    userAccess.setAccessKeySecret(RandomStringUtils.randomAlphanumeric(32));
    userAccess.setAllowIp(allowIp);
    userAccess.setRemark(remark);
    DateTime nowtime = new DateTime();
    DateTime expire = nowtime.plusDays(90).withTime(23,59,59,999);//有效期90天
    userAccess.setExpireDate(expire.getMillis());
    userAccess.setCreateTime(nowtime.getMillis());
    userAccessMapper.insert(userAccess);
    return userAccess;
  }

  @Override
  public List<UserAccess> findByUserId(long userId) {
    return userAccessMapper.findByUserId(userId);
  }

  @Override
  public void update(long userId, int id, String allowIp, String remark) {
    userAccessMapper.updateByUserId(userId, id, allowIp, remark);
  }

  @Override
  public void delete(long userId, int id) {
    userAccessMapper.deleteByUserId(userId, id);
  }

}
