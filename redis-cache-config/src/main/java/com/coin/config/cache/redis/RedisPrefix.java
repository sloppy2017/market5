package com.coin.config.cache.redis;

import org.springframework.data.redis.cache.RedisCachePrefix;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

public class RedisPrefix implements RedisCachePrefix {
  private final RedisSerializer serializer;
  private final String delimiter;

  public RedisPrefix() {
    this(":");
  }

  public RedisPrefix(String delimiter) {
    this.serializer = new StringRedisSerializer();
    this.delimiter = delimiter;
  }

  @Override
  public byte[] prefix(String cacheName) {
    return this.serializer.serialize(this.delimiter != null ? this.delimiter.concat(":").concat(cacheName).concat(":") : cacheName.concat(":"));
  }
}
