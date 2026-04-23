package com.anti.fraud.modules.season.service;

import com.anti.fraud.modules.season.entity.SeasonInfo;
import com.anti.fraud.modules.season.entity.UserSeasonData;
import com.anti.fraud.modules.season.vo.SeasonVO;

import java.util.List;
import java.util.Map;

/**
 * 赛季服务接口
 */
public interface SeasonService {

    /**
     * 获取当前赛季信息
     * @return 当前赛季信息
     */
    SeasonInfo getCurrentSeason();

    /**
     * 获取历史赛季列表
     * @param limit 数量限制
     * @return 历史赛季列表
     */
    List<SeasonInfo> getHistorySeasons(int limit);

    /**
     * 获取用户当前赛季数据
     * @param userId 用户ID
     * @return 用户赛季数据
     */
    UserSeasonData getUserCurrentSeasonData(Long userId);

    /**
     * 获取用户历史赛季数据
     * @param userId 用户ID
     * @param limit 数量限制
     * @return 用户历史赛季数据列表
     */
    List<UserSeasonData> getUserHistorySeasonData(Long userId, int limit);

    /**
     * 更新用户赛季积分
     * @param userId 用户ID
     * @param points 积分增量
     * @return 是否成功
     */
    boolean updateUserSeasonPoints(Long userId, int points);

    /**
     * 结算赛季
     * @param seasonId 赛季ID
     * @return 是否成功
     */
    boolean settleSeason(Long seasonId);

    /**
     * 生成新赛季
     * @param seasonInfo 赛季信息
     * @return 新赛季信息
     */
    SeasonInfo createSeason(SeasonInfo seasonInfo);

    /**
     * 更新赛季信息
     * @param seasonInfo 赛季信息
     * @return 是否成功
     */
    boolean updateSeason(SeasonInfo seasonInfo);

    /**
     * 删除赛季
     * @param seasonId 赛季ID
     * @return 是否成功
     */
    boolean deleteSeason(Long seasonId);

    /**
     * 获取赛季详情
     * @param seasonId 赛季ID
     * @return 赛季详情
     */
    SeasonInfo getSeasonById(Long seasonId);

    /**
     * 获取赛季排行榜
     * @param seasonId 赛季ID
     * @param limit 数量限制
     * @return 赛季排行榜
     */
    List<Map<String, Object>> getSeasonRanking(Long seasonId, int limit);

    /**
     * 重置赛季排行榜
     * @param seasonId 赛季ID
     * @return 是否成功
     */
    boolean resetSeasonRanking(Long seasonId);

    /**
     * 检查赛季状态
     * @return 检查结果
     */
    boolean checkSeasonStatus();

    /**
     * 批量更新用户赛季数据
     * @param seasonId 赛季ID
     * @param userPointsMap 用户积分映射
     * @return 成功更新的数量
     */
    int batchUpdateUserSeasonData(Long seasonId, Map<Long, Integer> userPointsMap);
}
