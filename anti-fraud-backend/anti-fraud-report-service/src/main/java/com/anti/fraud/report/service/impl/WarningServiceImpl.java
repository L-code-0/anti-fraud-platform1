package com.anti.fraud.report.service.impl;

import com.anti.fraud.report.entity.Warning;
import com.anti.fraud.report.mapper.WarningMapper;
import com.anti.fraud.report.service.WarningService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 预警服务实现类
 */
@Service
public class WarningServiceImpl extends ServiceImpl<WarningMapper, Warning> implements WarningService {

    @Override
    public Long publishWarning(Warning warning) {
        warning.setPublishTime(LocalDateTime.now());
        warning.setStartTime(LocalDateTime.now());
        warning.setStatus("active");
        warning.setViewCount(0);
        warning.setShareCount(0);
        warning.setCreateTime(LocalDateTime.now());
        warning.setUpdateTime(LocalDateTime.now());
        warning.setDeleted(0);
        save(warning);
        return warning.getId();
    }

    @Override
    public List<Warning> getLatestWarnings(int page, int size) {
        IPage<Warning> warningPage = new Page<>(page, size);
        LambdaQueryWrapper<Warning> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Warning::getStatus, "active")
                .eq(Warning::getDeleted, 0)
                .orderByDesc(Warning::getPublishTime);
        page(warningPage, queryWrapper);
        return warningPage.getRecords();
    }

    @Override
    public List<Warning> getWarningsByType(String type, int page, int size) {
        IPage<Warning> warningPage = new Page<>(page, size);
        LambdaQueryWrapper<Warning> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Warning::getType, type)
                .eq(Warning::getStatus, "active")
                .eq(Warning::getDeleted, 0)
                .orderByDesc(Warning::getPublishTime);
        page(warningPage, queryWrapper);
        return warningPage.getRecords();
    }

    @Override
    public List<Warning> getWarningsByLevel(String level, int page, int size) {
        IPage<Warning> warningPage = new Page<>(page, size);
        LambdaQueryWrapper<Warning> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Warning::getLevel, level)
                .eq(Warning::getStatus, "active")
                .eq(Warning::getDeleted, 0)
                .orderByDesc(Warning::getPublishTime);
        page(warningPage, queryWrapper);
        return warningPage.getRecords();
    }

    @Override
    public Warning getWarningDetail(Long warningId) {
        Warning warning = getById(warningId);
        if (warning != null) {
            // 增加查看次数
            warning.setViewCount(warning.getViewCount() + 1);
            updateById(warning);
        }
        return warning;
    }

    @Override
    public boolean updateWarningStatus(Long warningId, String status) {
        Warning warning = getById(warningId);
        if (warning == null) {
            return false;
        }
        warning.setStatus(status);
        warning.setUpdateTime(LocalDateTime.now());
        return updateById(warning);
    }

    @Override
    public WarningStatistics getWarningStatistics() {
        WarningStatistics statistics = new WarningStatistics();
        LocalDateTime now = LocalDateTime.now();

        // 今日预警数
        LambdaQueryWrapper<Warning> todayQuery = new LambdaQueryWrapper<>();
        todayQuery.ge(Warning::getPublishTime, now.truncatedTo(ChronoUnit.DAYS))
                .eq(Warning::getDeleted, 0);
        statistics.setTodayWarnings((int) count(todayQuery));

        // 本周预警数
        LocalDateTime weekStart = now.minusDays(now.getDayOfWeek().getValue() - 1).truncatedTo(ChronoUnit.DAYS);
        LambdaQueryWrapper<Warning> weekQuery = new LambdaQueryWrapper<>();
        weekQuery.ge(Warning::getPublishTime, weekStart)
                .eq(Warning::getDeleted, 0);
        statistics.setWeekWarnings((int) count(weekQuery));

        // 本月预警数
        LocalDateTime monthStart = now.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
        LambdaQueryWrapper<Warning> monthQuery = new LambdaQueryWrapper<>();
        monthQuery.ge(Warning::getPublishTime, monthStart)
                .eq(Warning::getDeleted, 0);
        statistics.setMonthWarnings((int) count(monthQuery));

        // 预警准确率（模拟数据）
        statistics.setAccuracyRate(98.5);

        return statistics;
    }
}
