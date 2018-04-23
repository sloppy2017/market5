package com.c2b.coin.web.common;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author MMM
 * @desc 描述
 * @date 2017-02-20 10:29
 **/
@Component
public class RedisUtil {
  @Autowired
  private RedisTemplate<Object, Object> redisTemplate;
  @Resource(name = "redisTemplate")
  private ValueOperations<Object, Object> valOps;

  @Autowired
  StringRedisTemplate stringRedisTemplate;

  @Resource(name = "stringRedisTemplate")
  ValueOperations<String, String> valOpsStr;

  public <T> T get(String key, Class<T> clazz) {
    return TypeUtils.castToJavaBean(valOps.get("key"), clazz);
  }

  public void set(final String key,final Object value) {
	  valOps.set(key, value);
  }
  
  public void set(final String key, final String value, final int expiresTimes) {
    valOpsStr.set(key, value, expiresTimes, TimeUnit.SECONDS);
  }

  public void set(final String key, final String value) {
    valOpsStr.set(key, value);
  }

  public String get(final String key) {
    return valOpsStr.get(key);
  }

  public Long incr(final String key) {
    return valOpsStr.increment(key, 1);
  }

  public Long ttl(final String key){
    return redisTemplate.execute(new RedisCallback<Long>() {
      @Override
      public Long doInRedis(RedisConnection redisConnection) throws DataAccessException {
        return redisConnection.ttl(key.getBytes());
      }
    });
  }

  public boolean expire(final String key, long expire) {
    return redisTemplate.expire(key, expire, TimeUnit.SECONDS);
  }

  public boolean hset(final String key, final String field, Object obj) {
    final String value = JSONObject.toJSONString(obj);
    return redisTemplate.execute(new RedisCallback<Boolean>() {
      @Override
      public Boolean doInRedis(RedisConnection redisConnection) throws DataAccessException {
        RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
        return redisConnection.hSet(key.getBytes(), field.getBytes(), value.getBytes());
      }
    });
  }
  public String hget(final String key, final String field){
    try {
      String result = redisTemplate.execute(new RedisCallback<String>() {
        @Override
        public String doInRedis(RedisConnection redisConnection) throws DataAccessException {
          return new String(redisConnection.hGet(key.getBytes(),field.getBytes()));
        }
      });
      return result;
    }catch (Exception e){
      return "";
    }

  }
  public <T> List<T> getList(String key, Class<T> clz) {
    String json = get(key);
    if (json != null) {
      List<T> list = JSONObject.parseArray(json, clz);
      return list;
    }
    return null;
  }
  public Boolean isMember(final String key,final String value){
    return redisTemplate.execute(new RedisCallback<Boolean>() {
      @Override
      public Boolean doInRedis(RedisConnection redisConnection) throws DataAccessException {
        return redisConnection.sIsMember(key.getBytes(), value.getBytes());
      }
    });
  }

  public Long zset(final String key, final String value){
    return redisTemplate.execute(new RedisCallback<Long>() {
      @Override
      public Long doInRedis(RedisConnection connection) throws DataAccessException {
        return connection.sAdd(key.getBytes(), value.getBytes());
      }
    });
  }
  public long lpush(final String key, Object obj) {
    final String value = JSONObject.toJSONString(obj);
    long result = redisTemplate.execute(new RedisCallback<Long>() {
      @Override
      public Long doInRedis(RedisConnection connection) throws DataAccessException {
        RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
        long count = connection.lPush(serializer.serialize(key), serializer.serialize(value));
        return count;
      }
    });
    return result;
  }

  public long time() {
    return redisTemplate.execute(new RedisCallback<Long>() {

      @Override
      public Long doInRedis(RedisConnection connection) throws DataAccessException {
        return connection.time();
      }
    });
  }

  public long rpush(final String key, Object obj) {
    final String value = JSONObject.toJSONString(obj);
    long result = redisTemplate.execute(new RedisCallback<Long>() {
      @Override
      public Long doInRedis(RedisConnection connection) throws DataAccessException {
        RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
        long count = connection.rPush(serializer.serialize(key), serializer.serialize(value));
        return count;
      }
    });
    return result;
  }

  public String lpop(final String key) {
    String result = redisTemplate.execute(new RedisCallback<String>() {
      @Override
      public String doInRedis(RedisConnection connection) throws DataAccessException {
        RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
        byte[] res = connection.lPop(serializer.serialize(key));
        return serializer.deserialize(res);
      }
    });
    return result;
  }

  public Long sadd(final String key, final String value){
    return redisTemplate.execute(new RedisCallback<Long>() {
      @Override
      public Long doInRedis(RedisConnection redisConnection) throws DataAccessException {
        return redisConnection.sAdd(key.getBytes(), value.getBytes());
      }
    });
  }

  public Set<byte[]> smember(final String key){
    return redisTemplate.execute(new RedisCallback<Set<byte[]>>() {
      @Override
      public Set<byte[]> doInRedis(RedisConnection redisConnection) throws DataAccessException {
        return redisConnection.sMembers(key.getBytes());
      }
    });
  }

  public Long delKey(final String key) {
    return redisTemplate.execute(new RedisCallback<Long>() {
      @Override
      public Long doInRedis(RedisConnection connection) throws DataAccessException {
        RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
        return connection.del(key.getBytes());
      }
    });
  }

  public Boolean setnx(final String key, final String value, long expire){
    boolean flag =  redisTemplate.execute(new RedisCallback<Boolean>() {
      @Override
      public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
        return connection.setNX(key.getBytes(), value.getBytes());
      }
    });
    if (flag){
      expire(key,expire); // 设置过期时间
    }
    return flag;
  }

}
