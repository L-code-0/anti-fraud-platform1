package com.anti.fraud.common.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 * <p>
 * 提供常用的Redis操作封装，简化Redis的使用。
 * 所有操作均使用Spring Data Redis的模板方法。
 * </p>
 *
 * @author Anti-Fraud Platform Team
 * @version 1.0
 * @since 2024-01-01
 * @see RedisTemplate
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RedisUtils {

    /**
     * Redis操作模板
     */
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 设置键值对
     * <p>
     * 默认使用Redis配置的过期时间。
     * </p>
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public void set(String key, Object value) {
        log.debug("设置Redis缓存: key={}", key);
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置键值对并指定过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param timeout 过期时间数值
     * @param unit    过期时间单位
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        log.debug("设置Redis缓存（带过期时间）: key={}, timeout={} {}", key, timeout, unit);
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 获取缓存值
     *
     * @param key 缓存键
     * @return 缓存值，不存在返回null
     */
    public Object get(String key) {
        log.debug("获取Redis缓存: key={}", key);
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除单个缓存键
     *
     * @param key 缓存键
     * @return 是否删除成功
     */
    public Boolean delete(String key) {
        log.debug("删除Redis缓存: key={}", key);
        return redisTemplate.delete(key);
    }

    /**
     * 批量删除缓存键
     *
     * @param keys 缓存键集合
     * @return 删除的键数量
     */
    public Long delete(Collection<String> keys) {
        log.debug("批量删除Redis缓存: keys={}", keys);
        return redisTemplate.delete(keys);
    }

    /**
     * 判断缓存键是否存在
     *
     * @param key 缓存键
     * @return 是否存在
     */
    public Boolean hasKey(String key) {
        log.debug("检查Redis缓存是否存在: key={}", key);
        return redisTemplate.hasKey(key);
    }

    /**
     * 设置键的过期时间
     *
     * @param key     缓存键
     * @param timeout 过期时间数值
     * @param unit    过期时间单位
     * @return 是否设置成功
     */
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        log.debug("设置Redis缓存过期时间: key={}, timeout={} {}", key, timeout, unit);
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 自增操作（原子递增）
     *
     * @param key 缓存键
     * @return 递增后的值
     */
    public Long increment(String key) {
        log.debug("Redis自增操作: key={}", key);
        return redisTemplate.opsForValue().increment(key);
    }

    /**
     * 自增指定步长
     *
     * @param key   缓存键
     * @param delta 递增量
     * @return 递增后的值
     */
    public Long increment(String key, long delta) {
        log.debug("Redis自增操作: key={}, delta={}", key, delta);
        return redisTemplate.opsForValue().increment(key, delta);
    }
}
