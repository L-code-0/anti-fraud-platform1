package com.anti.fraud.modules.points.service.impl;

import com.anti.fraud.common.exception.BusinessException;
import com.anti.fraud.common.utils.SecurityUtils;
import com.anti.fraud.modules.notification.service.NotificationService;
import com.anti.fraud.modules.points.entity.BadgeInfo;
import com.anti.fraud.modules.points.entity.PointsRecord;
import com.anti.fraud.modules.points.entity.UserBadge;
import com.anti.fraud.modules.points.mapper.BadgeInfoMapper;
import com.anti.fraud.modules.points.mapper.PointsRecordMapper;
import com.anti.fraud.modules.points.mapper.UserBadgeMapper;
import com.anti.fraud.modules.points.service.PointsService;
import com.anti.fraud.modules.points.vo.BadgeVO;
import com.anti.fraud.modules.points.vo.PointsRecordVO;
import com.anti.fraud.modules.points.vo.PointsStatsVO;
import com.anti.fraud.modules.user.entity.User;
import com.anti.fraud.modules.user.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointsServiceImpl implements PointsService {

    private final PointsRecordMapper pointsRecordMapper;
    private final BadgeInfoMapper badgeInfoMapper;
    private final UserBadgeMapper userBadgeMapper;
    private final UserMapper userMapper;
    private final NotificationService notificationService;

    @Override
    public PointsStatsVO getPointsStats() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("请先登录");
        }

        User user = userMapper.selectById(userId);

        PointsStatsVO stats = new PointsStatsVO();
        stats.setCurrentPoints(user.getPoints());
        stats.setCurrentLevel(user.getLevel());
        stats.setNextLevelPoints(calculateNextLevelPoints(user.getLevel()));
        stats.setRanking(pointsRecordMapper.getUserRanking(userId));
        stats.setTotalPoints(pointsRecordMapper.sumEarnedPoints(userId));
        stats.setWeeklyPoints(0);
        stats.setMonthlyPoints(0);

        return stats;
    }

    @Override
    public Page<PointsRecordVO> getPointsRecords(Integer page, Integer size) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("请先登录");
        }

        Page<PointsRecord> recordPage = new Page<>(page, size);
        pointsRecordMapper.selectPage(recordPage,
                new LambdaQueryWrapper<PointsRecord>()
                        .eq(PointsRecord::getUserId, userId)
                        .orderByDesc(PointsRecord::getCreateTime)
        );

        // 手动转换避免类型不兼容
        Page<PointsRecordVO> result = new Page<>(recordPage.getCurrent(), recordPage.getSize(), recordPage.getTotal());
        result.setRecords(recordPage.getRecords().stream().map(this::convertToPointsRecordVO).collect(Collectors.toList()));
        return result;
    }

    @Override
    public List<BadgeVO> getAllBadges() {
        Long userId = SecurityUtils.getCurrentUserId();

        List<BadgeInfo> badges = badgeInfoMapper.selectList(
                new LambdaQueryWrapper<BadgeInfo>()
                        .eq(BadgeInfo::getStatus, 1)
        );

        // 缓存用户已获得的勋章ID
        Set<Long> acquiredBadgeIds = new HashSet<>();
        if (userId != null) {
            List<UserBadge> userBadges = userBadgeMapper.selectByUserId(userId);
            for (UserBadge userBadge : userBadges) {
                acquiredBadgeIds.add(userBadge.getBadgeId());
            }
        }

        return badges.stream()
                .map(badge -> {
                    BadgeVO vo = convertToBadgeVO(badge);
                    if (userId != null) {
                        vo.setIsAcquired(acquiredBadgeIds.contains(badge.getId()));
                    }
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<BadgeVO> getMyBadges() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("请先登录");
        }

        // 从user_badge表查询用户已获得的勋章
        List<UserBadge> userBadges = userBadgeMapper.selectByUserId(userId);
        List<BadgeVO> result = new ArrayList<>();
        for (UserBadge ub : userBadges) {
            BadgeInfo badge = badgeInfoMapper.selectById(ub.getBadgeId());
            if (badge != null) {
                BadgeVO vo = convertToBadgeVO(badge);
                vo.setIsAcquired(true);
                vo.setAcquireTime(ub.getAcquireTime());
                vo.setAcquireReason(ub.getAcquireReason());
                result.add(vo);
            }
        }
        return result;
    }

    @Override
    @Transactional
    public void addPoints(Long userId, Integer points, String source, Long sourceId, String description) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return;
        }

        user.setPoints(user.getPoints() + points);
        user.setLevel(calculateLevel(user.getPoints()));
        userMapper.updateById(user);

        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setPoints(points);
        record.setType(1);
        record.setSource(source);
        record.setSourceId(sourceId);
        record.setDescription(description);
        record.setBalanceAfter(user.getPoints());

        pointsRecordMapper.insert(record);

        checkAndAwardBadges(userId);
    }

    @Override
    public void checkAndAwardBadges(Long userId) {
        // 检查并授予勋章
        // 根据用户积分、等级、完成任务数等条件自动授予勋章
        User user = userMapper.selectById(userId);
        if (user == null) {
            return;
        }

        // 查询用户已获得的勋章
        List<UserBadge> existingBadges = userBadgeMapper.selectByUserId(userId);
        Set<Long> acquiredBadgeIds = existingBadges.stream()
                .map(UserBadge::getBadgeId).collect(Collectors.toSet());

        // 查询所有可获得的勋章
        List<BadgeInfo> allBadges = badgeInfoMapper.selectList(
                new LambdaQueryWrapper<BadgeInfo>().eq(BadgeInfo::getStatus, 1)
        );

        for (BadgeInfo badge : allBadges) {
            // 跳过已获得的勋章
            if (acquiredBadgeIds.contains(badge.getId())) {
                continue;
            }

            // 检查是否满足获得条件
            boolean shouldAward = false;
            String awardReason = "";

            // 根据勋章类型检查条件
            switch (badge.getBadgeType()) {
                case 1:  // points_level - 积分等级勋章
                    // 积分等级勋章：达到指定积分
                    if (user.getPoints() >= badge.getPointsReward()) {
                        shouldAward = true;
                        awardReason = "达到" + badge.getPointsReward() + "积分";
                    }
                    break;
                case 2:  // learning - 学习勋章
                    // 学习勋章：完成一定学习时长
                    int studyMinutes = getUserStudyMinutes(userId);
                    if (studyMinutes >= badge.getPointsReward()) {
                        shouldAward = true;
                        awardReason = "学习时长达到" + studyMinutes + "分钟";
                    }
                    break;
                case 3:  // test - 测验勋章
                    // 测验勋章：完成一定数量测验
                    int testCount = getUserTestCount(userId);
                    if (testCount >= badge.getPointsReward()) {
                        shouldAward = true;
                        awardReason = "完成" + testCount + "次测验";
                    }
                    break;
                case 4:  // simulation - 演练勋章
                    // 演练勋章：完成一定数量演练
                    int simulationCount = getUserSimulationCount(userId);
                    if (simulationCount >= badge.getPointsReward()) {
                        shouldAward = true;
                        awardReason = "完成" + simulationCount + "次演练";
                    }
                    break;
                case 5:  // checkin - 签到勋章
                    // 签到勋章：连续签到天数
                    int checkinDays = getUserCheckinDays(userId);
                    if (checkinDays >= badge.getPointsReward()) {
                        shouldAward = true;
                        awardReason = "连续签到" + checkinDays + "天";
                    }
                    break;
                case 6:  // community - 社区勋章
                    // 社区勋章：发布一定数量帖子
                    int postCount = getUserPostCount(userId);
                    if (postCount >= badge.getPointsReward()) {
                        shouldAward = true;
                        awardReason = "发布" + postCount + "个帖子";
                    }
                    break;
            }

            if (shouldAward) {
                // 授予勋章
                UserBadge newBadge = new UserBadge();
                newBadge.setUserId(userId);
                newBadge.setBadgeId(badge.getId());
                newBadge.setAcquireTime(LocalDateTime.now());
                newBadge.setAcquireReason(awardReason);
                newBadge.setCreateTime(LocalDateTime.now());
                newBadge.setUpdateTime(LocalDateTime.now());
                userBadgeMapper.insert(newBadge);

                // 发送通知
                try {
                    notificationService.sendSystemNotification(
                            "获得新勋章",
                            "恭喜您获得了「" + badge.getBadgeName() + "」勋章！",
                            userId
                    );
                } catch (Exception e) {
                    log.error("发送勋章通知失败: {}", e.getMessage(), e);
                }

                log.info("用户 {} 获得勋章: {}，原因: {}", userId, badge.getBadgeName(), awardReason);
            }
        }
    }

    /**
     * 获取用户学习时长（分钟）
     * @param userId 用户ID
     * @return 学习时长
     */
    private int getUserStudyMinutes(Long userId) {
        // 实际实现需要查询学习时长表
        // 暂时返回0
        return 0;
    }

    /**
     * 获取用户测验次数
     * @param userId 用户ID
     * @return 测验次数
     */
    private int getUserTestCount(Long userId) {
        // 实际实现需要查询测验记录表
        // 暂时返回0
        return 0;
    }

    /**
     * 获取用户演练次数
     * @param userId 用户ID
     * @return 演练次数
     */
    private int getUserSimulationCount(Long userId) {
        // 实际实现需要查询演练记录表
        // 暂时返回0
        return 0;
    }

    /**
     * 获取用户连续签到天数
     * @param userId 用户ID
     * @return 连续签到天数
     */
    private int getUserCheckinDays(Long userId) {
        // 实际实现需要查询签到记录表
        // 暂时返回0
        return 0;
    }

    /**
     * 获取用户发布帖子数量
     * @param userId 用户ID
     * @return 帖子数量
     */
    private int getUserPostCount(Long userId) {
        // 实际实现需要查询社区帖子表
        // 暂时返回0
        return 0;
    }

    private Integer calculateLevel(Integer points) {
        if (points < 100) return 1;
        if (points < 300) return 2;
        if (points < 600) return 3;
        if (points < 1000) return 4;
        if (points < 2000) return 5;
        if (points < 4000) return 6;
        if (points < 8000) return 7;
        if (points < 15000) return 8;
        if (points < 30000) return 9;
        return 10;
    }

    private Integer calculateNextLevelPoints(Integer currentLevel) {
        int[] levelPoints = {100, 300, 600, 1000, 2000, 4000, 8000, 15000, 30000, 50000};
        if (currentLevel >= levelPoints.length) {
            return levelPoints[levelPoints.length - 1];
        }
        return levelPoints[currentLevel];
    }

    private PointsRecordVO convertToPointsRecordVO(PointsRecord record) {
        PointsRecordVO vo = new PointsRecordVO();
        vo.setId(record.getId());
        vo.setPoints(record.getPoints());
        vo.setType(record.getType());
        vo.setSource(record.getSource());
        vo.setDescription(record.getDescription());
        vo.setBalanceAfter(record.getBalanceAfter());
        vo.setCreateTime(record.getCreateTime());
        return vo;
    }

    private BadgeVO convertToBadgeVO(BadgeInfo badge) {
        BadgeVO vo = new BadgeVO();
        vo.setId(badge.getId());
        vo.setBadgeName(badge.getBadgeName());
        vo.setBadgeType(badge.getBadgeType());
        vo.setBadgeIcon(badge.getBadgeIcon());
        vo.setDescription(badge.getDescription());
        vo.setPointsReward(badge.getPointsReward());
        return vo;
    }
}
