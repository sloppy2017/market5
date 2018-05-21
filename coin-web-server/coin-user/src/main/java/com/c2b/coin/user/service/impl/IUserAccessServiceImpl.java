package com.c2b.coin.user.service.impl;

import com.c2b.coin.common.Constants;
import com.c2b.coin.common.ParamMessageUtil;
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
  protected RedisUtil redisUtil;

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

    String key = ParamMessageUtil.format(Constants.REDIS_USER_ACCESS_KEY, userAccess.getUserId(), userAccess.getAccessKeyId());
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
    UserAccessVo userAccess = userAccessMapper.getByUserId(userId, accessKeyId);
    if (userAccess == null) {
      throw new BusinessException(ErrorMsgEnum.USER_ACCESS_NOT_EXISTS);
    }
    userAccessMapper.updateByUserId(userAccess.getUserId(), userAccess.getAccessKeyId(), allowIp, remark);

    String key = ParamMessageUtil.format(Constants.REDIS_USER_ACCESS_KEY, userAccess.getUserId(), userAccess.getAccessKeyId());
    redisUtil.hset(ParamMessageUtil.format(Constants.REDIS_USER_ACCESS_KEY, userAccess.getUserId(), userAccess.getAccessKeyId()), "allowIp", allowIp);
    redisUtil.expire(key, userAccess.getExpireDate(), TimeUnit.MILLISECONDS);
  }

  @Override
  public void delete(long userId, String accessKeyId) {
    userAccessMapper.deleteByUserId(userId, accessKeyId);
    redisUtil.delKey(ParamMessageUtil.format(Constants.REDIS_USER_ACCESS_KEY, userId, accessKeyId));
  }

}
