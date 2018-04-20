//package com.c2b.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.AutoConfigureAfter;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
//import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.GenericToStringSerializer;
//import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//
//@Configuration
//@AutoConfigureAfter(RedisAutoConfiguration.class)
////@ConditionalOnBean(RedisConnectionFactory.class)
//public class RedisTemplateConfig {
//
//    @Bean
//    @Autowired
//    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
//
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(factory);
//        template.setHashValueSerializer(new JdkSerializationRedisSerializer());
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setValueSerializer(new JdkSerializationRedisSerializer());
//        template.setHashKeySerializer(new StringRedisSerializer());
//        template.afterPropertiesSet();
//        return template;
//    }
//
//    @Bean
//    @Autowired
//    public RedisTemplate<String, Long> createTemplate(RedisConnectionFactory factory) {
//        final RedisTemplate<String, Long> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(factory);
//        redisTemplate.setHashValueSerializer(new GenericToStringSerializer<>(Long.class));
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new GenericToStringSerializer<>(Long.class));
//        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
//        redisTemplate.setEnableDefaultSerializer(true);
//        redisTemplate.afterPropertiesSet();
//        return redisTemplate;
//    }
//}