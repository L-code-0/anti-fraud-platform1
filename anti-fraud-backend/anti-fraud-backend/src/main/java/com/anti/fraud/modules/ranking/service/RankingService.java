package com.anti.fraud.modules.ranking.service;

import java.util.List;
import java.util.Map;

/**
 * 排行榜服务接口
 */
public interface RankingService {

    /**
     * 更新用户积分排名
     * @param userId 用户ID
     * @param points 积分
     * @param username 用户名
     */
    void updateUserRanking(Long userId, int points, String username);

    /**
     * 获取排行榜前N名
     * @param limit 数量限制
     * @return 排行榜列表，key为用户ID，value为积分
     */
    List<Map<String, Object>> getTopRankings(int limit);

    /**
     * 获取用户排名
     * @param userId 用户ID
     * @return 用户排名
     */
    long getUserRanking(Long userId);

    /**
     * 获取用户积分
     * @param userId 用户ID
     * @return 用户积分
     */
    int getUserPoints(Long userId);

    /**
     * 批量更新用户积分排名
     * @param userPointsMap 用户积分映射，key为用户ID，value为积分
     */
    void batchUpdateRankings(Map<Long, Integer> userPointsMap);

    /**
     * 清空排行榜
     */
    void clearRanking();

    /**
     * 获取指定范围内的排名
     * @param start 起始位置（从0开始）
     * @param end 结束位置
     * @return 排行榜列表
     */
    List<Map<String, Object>> getRankingsInRange(int start, int end);

    /**
     * 获取用户周围的排名
     * @param userId 用户ID
     * @param range 范围
     * @return 排行榜列表
     */
    List<Map<String, Object>> getUserSurroundingRankings(Long userId, int range);

    /**
     * 检查用户是否在排行榜中
     * @param userId 用户ID
     * @return 是否在排行榜中
     */
    boolean isUserInRanking(Long userId);

    /**
     * 获取排行榜总人数
     * @return 总人数
     */
    long getRankingSize();

    /**
     * 刷新排行榜（从数据库同步）
     */
    void refreshRanking();
}
