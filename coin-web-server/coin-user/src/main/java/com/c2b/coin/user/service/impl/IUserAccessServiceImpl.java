package com.c2b.coin.user.service.impl;

import com.c2b.coin.common.enumeration.ErrorMsgEnum;
import com.c2b.coin.user.entity.UserAccess;
import com.c2b.coin.user.mapper.UserAccessMapper;
import com.c2b.coin.user.service.IUserAccessService;
import com.c2b.coin.user.vo.UserAccessVo;
import com.c2b.coin.web.common.RedisUtil;
import com.c2b.coin.web.common.exception.BusinessException;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 关注用户
 *
 * @author tangwei
 */
@Service
public class IUserAccessServiceImpl implements IUserAccessService {

  @Autowired
  protected UserAccessMapper userAccessMapper;

  @Autowired
  RedisUtil redisUtil;

  @Override
  public UserAccess create(long userId, String allowIp, String remark) {
    if (userAccessMapper.findCountByUserId(userId) >= 5) {
      throw new BusinessException(ErrorMsgEnum.USER_ACCESS_CREATE_MAXIMUM_ALLOWED);
    }
    UserAccess userAccess = new UserAccess();
    userAccess.setUserId(userId);
    userAccess.setAccessKeyId(UUID.randomUUID().toString());
    userAccess.setAccessKeySecret(RandomStringUtils.randomAlphanumeric(32));
    userAccess.setAllowIp(allowIp);
    userAccess.setRemark(remark);
    DateTime nowtime = new DateTime();
    DateTime expire = nowtime.plusDays(90).withTime(23, 59, 59, 999);//有效期90天
    userAccess.setExpireDate(expire.getMillis());
    userAccess.setCreateTime(nowtime.getMillis());
    userAccessMapper.insert(userAccess);

    String key = "USER_ACCESS_" + userAccess.getUserId() + "_" + userAccess.getAccessKeyId();
    redisUtil.hset(key, "accessKeySecret", userAccess.getAccessKeySecret());
    redisUtil.hset(key, "allowIp", userAccess.getAllowIp());
    redisUtil.expire(key, userAccess.getExpireDate(), TimeUnit.MILLISECONDS);
    return userAccess;
  }

  @Override
  public List<UserAccessVo> findByUserId(long userId) {
    return userAccessMapper.findByUserId(userId);
  }

  @Override
  public void update(long userId, String accessKeyId, String allowIp, String remark) {
    userAccessMapper.updateByUserId(userId, accessKeyId, allowIp, remark);
    redisUtil.hset("USER_ACCESS_" + userId + "_" + accessKeyId, "allowIp", allowIp);
  }

  @Override
  public void delete(long userId, String accessKeyId) {
    userAccessMapper.deleteByUserId(userId, accessKeyId);
    redisUtil.delKey("USER_ACCESS_" + userId + "_" + accessKeyId);
  }

}
