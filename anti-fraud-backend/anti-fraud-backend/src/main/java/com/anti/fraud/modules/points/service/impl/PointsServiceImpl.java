package com.anti.fraud.modules.points.service.impl;

import com.anti.fraud.common.exception.BusinessException;
import com.anti.fraud.common.utils.SecurityUtils;
import com.anti.fraud.modules.points.entity.BadgeInfo;
import com.anti.fraud.modules.points.entity.PointsRecord;
import com.anti.fraud.modules.points.mapper.BadgeInfoMapper;
import com.anti.fraud.modules.points.mapper.PointsRecordMapper;
import com.anti.fraud.modules.points.service.PointsService;
import com.anti.fraud.modules.points.vo.BadgeVO;
import com.anti.fraud.modules.points.vo.PointsRecordVO;
import com.anti.fraud.modules.points.vo.PointsStatsVO;
import com.anti.fraud.modules.user.entity.User;
import com.anti.fraud.modules.user.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PointsServiceImpl implements PointsService {

    private final PointsRecordMapper pointsRecordMapper;
    private final BadgeInfoMapper badgeInfoMapper;
    private final UserMapper userMapper;

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

        return badges.stream()
                .map(badge -> {
                    BadgeVO vo = convertToBadgeVO(badge);
                    if (userId != null) {
                        // 检查用户是否已获得该勋章
                        // TODO: 查询user_badge表
                        vo.setIsAcquired(false);
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

        // TODO: 从user_badge表查询用户已获得的勋章
        return getAllBadges().stream()
                .filter(BadgeVO::getIsAcquired)
                .collect(Collectors.toList());
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
        // TODO: 检查并授予勋章
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
