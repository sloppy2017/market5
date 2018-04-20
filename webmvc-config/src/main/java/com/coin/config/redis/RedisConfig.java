package com.coin.config.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCachePrefix;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>redis缓存配置</p>
 */
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {
  private Logger logger = LoggerFactory.getLogger(this.getClass());

  @Bean
  public CacheManager cacheManager(RedisTemplate redisTemplate) {
    logger.info("======================缓存配置===============================");
    RedisCacheManager manager = new RedisCacheManager(redisTemplate);
    manager.setUsePrefix(true);
    RedisCachePrefix cachePrefix = new RedisPrefix("cache");
    manager.setCachePrefix(cachePrefix);
    // 整体缓存过期时间
    manager.setDefaultExpiration(3600L);
    // 设置缓存过期时间。key和缓存过期时间，单位秒
    Map<String, Long> expiresMap = new HashMap<>();
    expiresMap.put("user", 60L);
    manager.setExpires(expiresMap);
    return manager;
  }

  @Bean
  public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    logger.info("======================redis配置===============================");
    RedisTemplate<Object, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    //使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值
    Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(Object.class);

    ObjectMapper mapper = new ObjectMapper();
    mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
    serializer.setObjectMapper(mapper);
    template.setValueSerializer(serializer);
    //使用StringRedisSerializer来序列化和反序列化redis的key值
    template.setKeySerializer(new StringRedisSerializer());
    template.afterPropertiesSet();
    return template;
  }
}
