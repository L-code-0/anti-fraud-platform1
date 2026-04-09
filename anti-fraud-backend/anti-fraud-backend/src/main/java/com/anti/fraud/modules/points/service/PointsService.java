package com.anti.fraud.modules.points.service;

import com.anti.fraud.modules.points.vo.BadgeVO;
import com.anti.fraud.modules.points.vo.PointsRecordVO;
import com.anti.fraud.modules.points.vo.PointsStatsVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface PointsService {

    PointsStatsVO getPointsStats();

    Page<PointsRecordVO> getPointsRecords(Integer page, Integer size);

    List<BadgeVO> getAllBadges();

    List<BadgeVO> getMyBadges();

    void addPoints(Long userId, Integer points, String source, Long sourceId, String description);

    void checkAndAwardBadges(Long userId);
}