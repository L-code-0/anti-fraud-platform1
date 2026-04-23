package com.anti.fraud.modules.season.service.impl;

import com.anti.fraud.common.exception.BusinessException;
import com.anti.fraud.modules.notification.service.NotificationService;
import com.anti.fraud.modules.season.entity.SeasonInfo;
import com.anti.fraud.modules.season.entity.UserSeasonData;
import com.anti.fraud.modules.season.mapper.SeasonInfoMapper;
import com.anti.fraud.modules.season.mapper.UserSeasonDataMapper;
import com.anti.fraud.modules.season.service.SeasonService;
import com.anti.fraud.modules.season.vo.SeasonVO;
import com.anti.fraud.modules.user.entity.User;
import com.anti.fraud.modules.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 赛季服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SeasonServiceImpl implements SeasonService {

    private final SeasonInfoMapper seasonInfoMapper;
    private final UserSeasonDataMapper userSeasonDataMapper;
    private final UserMapper userMapper;
    private final NotificationService notificationService;

    @Override
    public SeasonInfo getCurrentSeason() {
        try {
            LocalDateTime currentTime = LocalDateTime.now();
            SeasonInfo season = seasonInfoMapper.selectCurrentSeason(currentTime);
            if (season == null) {
                throw new BusinessException("当前无活跃赛季");
            }
            return season;
        } catch (Exception e) {
            log.error("获取当前赛季失败: {}", e.getMessage(), e);
            throw new BusinessException("获取当前赛季失败");
        }
    }

    @Override
    public List<SeasonInfo> getHistorySeasons(int limit) {
        try {
            LocalDateTime currentTime = LocalDateTime.now();
            return seasonInfoMapper.selectHistorySeasons(currentTime, limit);
        } catch (Exception e) {
            log.error("获取历史赛季失败: {}", e.getMessage(), e);
            throw new BusinessException("获取历史赛季失败");
        }
    }

    @Override
    public UserSeasonData getUserCurrentSeasonData(Long userId) {
        try {
            SeasonInfo currentSeason = getCurrentSeason();
            UserSeasonData userSeasonData = userSeasonDataMapper.selectByUserIdAndSeasonId(userId, currentSeason.getId());
            if (userSeasonData == null) {
                // 创建用户赛季数据
                userSeasonData = new UserSeasonData();
                userSeasonData.setUserId(userId);
                userSeasonData.setSeasonId(currentSeason.getId());
                userSeasonData.setSeasonPoints(0);
                userSeasonData.setSeasonRank(0);
                userSeasonData.setCompletedTasks(0);
                userSeasonData.setAcquiredBadges(0);
                userSeasonData.setStatus(1); // 进行中
                userSeasonData.setCreateTime(LocalDateTime.now());
                userSeasonData.setUpdateTime(LocalDateTime.now());
                userSeasonDataMapper.insert(userSeasonData);
            }
            return userSeasonData;
        } catch (Exception e) {
            log.error("获取用户当前赛季数据失败: {}", e.getMessage(), e);
            throw new BusinessException("获取用户当前赛季数据失败");
        }
    }

    @Override
    public List<UserSeasonData> getUserHistorySeasonData(Long userId, int limit) {
        try {
            return userSeasonDataMapper.selectByUserId(userId, 2); // 2-已结算
        } catch (Exception e) {
            log.error("获取用户历史赛季数据失败: {}", e.getMessage(), e);
            throw new BusinessException("获取用户历史赛季数据失败");
        }
    }

    @Override
    @Transactional
    public boolean updateUserSeasonPoints(Long userId, int points) {
        try {
            UserSeasonData userSeasonData = getUserCurrentSeasonData(userId);
            int newPoints = userSeasonData.getSeasonPoints() + points;
            userSeasonData.setSeasonPoints(newPoints);
            userSeasonData.setUpdateTime(LocalDateTime.now());
            userSeasonDataMapper.updateById(userSeasonData);
            return true;
        } catch (Exception e) {
            log.error("更新用户赛季积分失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean settleSeason(Long seasonId) {
        try {
            SeasonInfo season = seasonInfoMapper.selectById(seasonId);
            if (season == null) {
                throw new BusinessException("赛季不存在");
            }

            // 更新赛季状态为已结束
            season.setStatus(3);
            season.setUpdateTime(LocalDateTime.now());
            seasonInfoMapper.updateById(season);

            // 结算用户赛季数据
            List<UserSeasonData> userSeasonDataList = userSeasonDataMapper.selectBySeasonId(seasonId, 1000);
            for (UserSeasonData data : userSeasonDataList) {
                data.setStatus(2); // 已结算
                data.setSettleTime(LocalDateTime.now());
                data.setUpdateTime(LocalDateTime.now());
                userSeasonDataMapper.updateById(data);

                // 发送赛季结算通知
                try {
                    notificationService.sendSystemNotification(
                            "赛季结算",
                            "「" + season.getSeasonName() + "」赛季已结算，您的赛季积分为：" + data.getSeasonPoints(),
                            data.getUserId()
                    );
                } catch (Exception e) {
                    log.error("发送赛季结算通知失败: {}", e.getMessage(), e);
                }
            }

            // 重置排行榜
            resetSeasonRanking(seasonId);

            log.info("赛季 {} 结算完成", seasonId);
            return true;
        } catch (Exception e) {
            log.error("结算赛季失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public SeasonInfo createSeason(SeasonInfo seasonInfo) {
        try {
            seasonInfo.setStatus(1); // 未开始
            seasonInfo.setCreateTime(LocalDateTime.now());
            seasonInfo.setUpdateTime(LocalDateTime.now());
            seasonInfoMapper.insert(seasonInfo);
            log.info("创建新赛季: {}", seasonInfo.getSeasonName());
            return seasonInfo;
        } catch (Exception e) {
            log.error("创建赛季失败: {}", e.getMessage(), e);
            throw new BusinessException("创建赛季失败");
        }
    }

    @Override
    public boolean updateSeason(SeasonInfo seasonInfo) {
        try {
            seasonInfo.setUpdateTime(LocalDateTime.now());
            seasonInfoMapper.updateById(seasonInfo);
            return true;
        } catch (Exception e) {
            log.error("更新赛季失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean deleteSeason(Long seasonId) {
        try {
            seasonInfoMapper.deleteById(seasonId);
            return true;
        } catch (Exception e) {
            log.error("删除赛季失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public SeasonInfo getSeasonById(Long seasonId) {
        try {
            return seasonInfoMapper.selectById(seasonId);
        } catch (Exception e) {
            log.error("获取赛季详情失败: {}", e.getMessage(), e);
            throw new BusinessException("获取赛季详情失败");
        }
    }

    @Override
    public List<Map<String, Object>> getSeasonRanking(Long seasonId, int limit) {
        try {
            List<UserSeasonData> rankingData = userSeasonDataMapper.selectSeasonRanking(seasonId, limit);
            List<Map<String, Object>> ranking = new ArrayList<>();

            for (int i = 0; i < rankingData.size(); i++) {
                UserSeasonData data = rankingData.get(i);
                User user = userMapper.selectById(data.getUserId());
                if (user != null) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("rank", i + 1);
                    item.put("userId", data.getUserId());
                    item.put("username", user.getUsername());
                    item.put("seasonPoints", data.getSeasonPoints());
                    item.put("completedTasks", data.getCompletedTasks());
                    item.put("acquiredBadges", data.getAcquiredBadges());
                    ranking.add(item);
                }
            }

            return ranking;
        } catch (Exception e) {
            log.error("获取赛季排行榜失败: {}", e.getMessage(), e);
            throw new BusinessException("获取赛季排行榜失败");
        }
    }

    @Override
    public boolean resetSeasonRanking(Long seasonId) {
        try {
            // 实际实现需要重新计算排名
            log.info("重置赛季 {} 排行榜", seasonId);
            return true;
        } catch (Exception e) {
            log.error("重置赛季排行榜失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean checkSeasonStatus() {
        try {
            LocalDateTime currentTime = LocalDateTime.now();
            // 检查是否有需要开始的赛季
            List<SeasonInfo> upcomingSeasons = seasonInfoMapper.selectByStatus(1);
            for (SeasonInfo season : upcomingSeasons) {
                if (season.getStartTime().isBefore(currentTime)) {
                    season.setStatus(2); // 进行中
                    season.setUpdateTime(currentTime);
                    seasonInfoMapper.updateById(season);
                    log.info("赛季 {} 开始", season.getSeasonName());
                }
            }

            // 检查是否有需要结束的赛季
            List<SeasonInfo> ongoingSeasons = seasonInfoMapper.selectByStatus(2);
            for (SeasonInfo season : ongoingSeasons) {
                if (season.getEndTime().isBefore(currentTime)) {
                    settleSeason(season.getId());
                    log.info("赛季 {} 结束", season.getSeasonName());
                }
            }

            return true;
        } catch (Exception e) {
            log.error("检查赛季状态失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public int batchUpdateUserSeasonData(Long seasonId, Map<Long, Integer> userPointsMap) {
        try {
            int count = 0;
            for (Map.Entry<Long, Integer> entry : userPointsMap.entrySet()) {
                Long userId = entry.getKey();
                Integer points = entry.getValue();
                UserSeasonData userSeasonData = userSeasonDataMapper.selectByUserIdAndSeasonId(userId, seasonId);
                if (userSeasonData != null) {
                    userSeasonData.setSeasonPoints(points);
                    userSeasonData.setUpdateTime(LocalDateTime.now());
                    userSeasonDataMapper.updateById(userSeasonData);
                    count++;
                }
            }
            return count;
        } catch (Exception e) {
            log.error("批量更新用户赛季数据失败: {}", e.getMessage(), e);
            return 0;
        }
    }
}
