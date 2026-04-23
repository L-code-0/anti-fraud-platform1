package com.anti.fraud.modules.ranking.service.impl;

import com.anti.fraud.common.utils.RedisUtils;
import com.anti.fraud.modules.ranking.service.RankingService;
import com.anti.fraud.modules.user.entity.User;
import com.anti.fraud.modules.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 排行榜服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RankingServiceImpl implements RankingService {

    private final RedisUtils redisUtils;
    private final UserMapper userMapper;

    // 排行榜键名
    private static final String RANKING_KEY = "anti_fraud:ranking:points";
    // 用户信息键名前缀
    private static final String USER_INFO_KEY_PREFIX = "anti_fraud:user:info:";

    @Override
    public void updateUserRanking(Long userId, int points, String username) {
        try {
            // 更新排行榜
            redisUtils.zadd(RANKING_KEY, points, userId.toString());
            // 缓存用户信息
            redisUtils.hset(USER_INFO_KEY_PREFIX + userId, "username", username);
            redisUtils.hset(USER_INFO_KEY_PREFIX + userId, "points", String.valueOf(points));
            log.debug("更新用户 {} 排名，积分: {}", userId, points);
        } catch (Exception e) {
            log.error("更新用户排名失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public List<Map<String, Object>> getTopRankings(int limit) {
        try {
            // 获取排行榜前N名
            List<String> userIds = redisUtils.zrevrange(RANKING_KEY, 0, limit - 1);
            List<Map<String, Object>> rankings = new ArrayList<>();

            for (int i = 0; i < userIds.size(); i++) {
                String userIdStr = userIds.get(i);
                Long userId = Long.valueOf(userIdStr);
                // 获取用户积分
                double points = redisUtils.zscore(RANKING_KEY, userIdStr);
                // 获取用户信息
                String username = redisUtils.hget(USER_INFO_KEY_PREFIX + userId, "username");
                if (username == null) {
                    // 从数据库查询用户信息
                    User user = userMapper.selectById(userId);
                    if (user != null) {
                        username = user.getUsername();
                        // 缓存用户信息
                        redisUtils.hset(USER_INFO_KEY_PREFIX + userId, "username", username);
                    }
                }

                Map<String, Object> ranking = new HashMap<>();
                ranking.put("rank", i + 1);
                ranking.put("userId", userId);
                ranking.put("username", username);
                ranking.put("points", (int) points);
                rankings.add(ranking);
            }

            return rankings;
        } catch (Exception e) {
            log.error("获取排行榜失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public long getUserRanking(Long userId) {
        try {
            // 获取用户排名（Redis中排名从0开始，需要+1）
            Long rank = redisUtils.zrevrank(RANKING_KEY, userId.toString());
            return rank != null ? rank + 1 : 0;
        } catch (Exception e) {
            log.error("获取用户排名失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public int getUserPoints(Long userId) {
        try {
            // 获取用户积分
            Double points = redisUtils.zscore(RANKING_KEY, userId.toString());
            return points != null ? points.intValue() : 0;
        } catch (Exception e) {
            log.error("获取用户积分失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public void batchUpdateRankings(Map<Long, Integer> userPointsMap) {
        try {
            for (Map.Entry<Long, Integer> entry : userPointsMap.entrySet()) {
                Long userId = entry.getKey();
                Integer points = entry.getValue();
                // 获取用户名
                User user = userMapper.selectById(userId);
                if (user != null) {
                    updateUserRanking(userId, points, user.getUsername());
                }
            }
            log.debug("批量更新排行榜，更新 {} 个用户", userPointsMap.size());
        } catch (Exception e) {
            log.error("批量更新排行榜失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public void clearRanking() {
        try {
            redisUtils.delete(RANKING_KEY);
            log.info("清空排行榜");
        } catch (Exception e) {
            log.error("清空排行榜失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public List<Map<String, Object>> getRankingsInRange(int start, int end) {
        try {
            // 获取指定范围内的排名
            List<String> userIds = redisUtils.zrevrange(RANKING_KEY, start, end);
            List<Map<String, Object>> rankings = new ArrayList<>();

            for (int i = 0; i < userIds.size(); i++) {
                String userIdStr = userIds.get(i);
                Long userId = Long.valueOf(userIdStr);
                // 获取用户积分
                double points = redisUtils.zscore(RANKING_KEY, userIdStr);
                // 获取用户信息
                String username = redisUtils.hget(USER_INFO_KEY_PREFIX + userId, "username");
                if (username == null) {
                    // 从数据库查询用户信息
                    User user = userMapper.selectById(userId);
                    if (user != null) {
                        username = user.getUsername();
                        // 缓存用户信息
                        redisUtils.hset(USER_INFO_KEY_PREFIX + userId, "username", username);
                    }
                }

                Map<String, Object> ranking = new HashMap<>();
                ranking.put("rank", start + i + 1);
                ranking.put("userId", userId);
                ranking.put("username", username);
                ranking.put("points", (int) points);
                rankings.add(ranking);
            }

            return rankings;
        } catch (Exception e) {
            log.error("获取指定范围排行榜失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Map<String, Object>> getUserSurroundingRankings(Long userId, int range) {
        try {
            // 获取用户排名
            Long rank = redisUtils.zrevrank(RANKING_KEY, userId.toString());
            if (rank == null) {
                return new ArrayList<>();
            }

            // 计算范围
            int start = Math.max(0, rank.intValue() - range);
            int end = rank.intValue() + range;

            return getRankingsInRange(start, end);
        } catch (Exception e) {
            log.error("获取用户周围排名失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public boolean isUserInRanking(Long userId) {
        try {
            // 检查用户是否在排行榜中
            return redisUtils.zscore(RANKING_KEY, userId.toString()) != null;
        } catch (Exception e) {
            log.error("检查用户是否在排行榜中失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public long getRankingSize() {
        try {
            // 获取排行榜总人数
            return redisUtils.zcard(RANKING_KEY);
        } catch (Exception e) {
            log.error("获取排行榜总人数失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public void refreshRanking() {
        try {
            // 清空现有排行榜
            clearRanking();

            // 从数据库获取所有用户的积分
            List<User> users = userMapper.selectList(null);
            Map<Long, Integer> userPointsMap = new HashMap<>();
            for (User user : users) {
                userPointsMap.put(user.getId(), user.getPoints());
            }

            // 批量更新排行榜
            batchUpdateRankings(userPointsMap);
            log.info("刷新排行榜，共 {} 个用户", users.size());
        } catch (Exception e) {
            log.error("刷新排行榜失败: {}", e.getMessage(), e);
        }
    }
}
