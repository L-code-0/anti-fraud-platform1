package com.anti.fraud.common.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
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

    /**
     * 获取键的剩余过期时间
     *
     * @param key 缓存键
     * @return 剩余过期时间（秒），如果键不存在或没有过期时间返回-1
     */
    public Long getExpire(String key) {
        log.debug("获取Redis缓存过期时间: key={}", key);
        return redisTemplate.getExpire(key);
    }

    /**
     * 设置Hash字段值
     *
     * @param key 缓存键
     * @param hashKey Hash的field
     * @param value Hash的value
     */
    public void hset(String key, String hashKey, Object value) {
        log.debug("设置Redis Hash: key={}, hashKey={}", key, hashKey);
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * 设置Hash（存储整个Map）
     *
     * @param key 缓存键
     * @param map Hash的键值对
     */
    public void hset(String key, Map<String, Object> map) {
        log.debug("设置Redis Hash: key={}", key);
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * 获取Hash中指定字段的值
     *
     * @param key 缓存键
     * @param hashKey Hash的field
     * @return Hash的value
     */
    public Object hget(String key, String hashKey) {
        log.debug("获取Redis Hash: key={}, hashKey={}", key, hashKey);
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 获取Hash中所有数据
     *
     * @param key 缓存键
     * @return Hash的所有键值对
     */
    public Map<Object, Object> hgetAll(String key) {
        log.debug("获取Redis Hash所有数据: key={}", key);
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 删除Hash中指定字段
     *
     * @param key 缓存键
     * @param hashKeys Hash的field集合
     * @return 删除的字段数量
     */
    public Long hdel(String key, Object... hashKeys) {
        log.debug("删除Redis Hash: key={}, hashKeys={}", key, hashKeys);
        return redisTemplate.opsForHash().delete(key, hashKeys);
    }

    /**
     * 判断Hash中指定字段是否存在
     *
     * @param key 缓存键
     * @param hashKey Hash的field
     * @return 是否存在
     */
    public Boolean hHasKey(String key, String hashKey) {
        log.debug("检查Redis Hash字段是否存在: key={}, hashKey={}", key, hashKey);
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    /**
     * 获取Hash中所有field
     *
     * @param key 缓存键
     * @return 所有field的Set
     */
    public Set<Object> hkeys(String key) {
        log.debug("获取Redis Hash所有field: key={}", key);
        return redisTemplate.opsForHash().keys(key);
    }

    /**
     * 获取Hash中字段数量
     *
     * @param key 缓存键
     * @return 字段数量
     */
    public Long hsize(String key) {
        log.debug("获取Redis Hash大小: key={}", key);
        return redisTemplate.opsForHash().size(key);
    }
}
