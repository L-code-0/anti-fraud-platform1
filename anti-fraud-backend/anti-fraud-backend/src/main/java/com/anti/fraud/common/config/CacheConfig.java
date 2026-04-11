package com.anti.fraud.common.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Redis缓存配置类
 * <p>
 * 配置Redis缓存管理器，设置序列化方式和缓存过期时间。
 * 支持将Java对象序列化为JSON存储到Redis中。
 * </p>
 *
 * @author Anti-Fraud Platform Team
 * @version 1.0
 * @since 2024-01-01
 * @see EnableCaching
 * @see RedisCacheManager
 */
@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {

    /**
     * 配置RedisTemplate
     * <p>
     * 使用JSON序列化方式存储数据，支持Java对象直接缓存。
     * </p>
     *
     * @param connectionFactory Redis连接工厂
     * @return RedisTemplate实例
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        log.info("初始化RedisTemplate");

        // 创建RedisTemplate
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 使用JSON序列化器
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();

        // 设置Java8时间模块支持
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 设置序列化可见性
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        // 启用类型信息
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        // 配置序列化器的对象映射器
        jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

        // 创建字符串序列化器
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);

        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);

        // value序列化方式采用JSON序列化
        template.setValueSerializer(jackson2JsonRedisSerializer);

        // hash的value序列化方式采用JSON序列化
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        template.afterPropertiesSet();

        log.info("RedisTemplate初始化完成");
        return template;
    }

    /**
     * 配置缓存管理器
     * <p>
     * 设置默认的缓存过期时间和序列化方式。
     * </p>
     *
     * @param connectionFactory Redis连接工厂
     * @return CacheManager实例
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        log.info("初始化RedisCacheManager");

        // 配置序列化
        ObjectMapper cacheObjectMapper = new ObjectMapper();

        // 设置Java8时间模块支持
        cacheObjectMapper.registerModule(new JavaTimeModule());
        cacheObjectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 设置序列化可见性
        cacheObjectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        // 启用类型信息
        cacheObjectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        Jackson2JsonRedisSerializer<Object> cacheSerializer = new Jackson2JsonRedisSerializer<>(cacheObjectMapper, Object.class);

        // 创建Redis缓存配置
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                // 设置默认过期时间：30分钟
                .entryTtl(Duration.ofMinutes(30))
                // 设置Key的序列化方式
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                // 设置Value的序列化方式
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(cacheSerializer))
                // 禁用缓存空值
                .disableCachingNullValues();

        // 创建缓存管理器
        RedisCacheManager cacheManager = RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                // 设置缓存过期时间
                .withCacheConfiguration("warnings", RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofHours(1))) // 预警缓存1小时
                .withCacheConfiguration("activities", RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(15))) // 活动缓存15分钟
                .withCacheConfiguration("dashboard", RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(5))) // 仪表盘统计缓存5分钟
                .withCacheConfiguration("knowledge", RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofHours(24))) // 知识库缓存24小时
                .build();

        log.info("RedisCacheManager初始化完成");
        return cacheManager;
    }
}
