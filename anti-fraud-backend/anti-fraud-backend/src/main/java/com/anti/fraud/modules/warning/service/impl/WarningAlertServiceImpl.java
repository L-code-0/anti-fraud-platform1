package com.anti.fraud.modules.warning.service.impl;

import com.anti.fraud.common.exception.BusinessException;
import com.anti.fraud.modules.warning.dto.WarningProcessDTO;
import com.anti.fraud.modules.warning.dto.WarningQueryDTO;
import com.anti.fraud.modules.warning.entity.WarningAlert;
import com.anti.fraud.modules.warning.mapper.WarningAlertMapper;
import com.anti.fraud.modules.warning.service.WarningAlertService;
import com.anti.fraud.modules.warning.vo.WarningAlertVO;
import com.anti.fraud.modules.warning.vo.WarningStatsVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 预警警报服务实现
 */
@Service
@RequiredArgsConstructor
public class WarningAlertServiceImpl implements WarningAlertService {

    private final WarningAlertMapper warningAlertMapper;

    @Override
    public WarningStatsVO getStats() {
        WarningStatsVO stats = new WarningStatsVO();
        stats.setHighRiskCount(0);
        stats.setMediumRiskCount(0);
        stats.setLowRiskCount(0);

        // 统计各风险等级数量
        List<Map<String, Object>> riskCounts = warningAlertMapper.countByRiskLevel();
        for (Map<String, Object> item : riskCounts) {
            Integer riskLevel = (Integer) item.get("risk_level");
            Long count = (Long) item.get("count");
            switch (riskLevel) {
                case 1 -> stats.setHighRiskCount(count.intValue());
                case 2 -> stats.setMediumRiskCount(count.intValue());
                case 3 -> stats.setLowRiskCount(count.intValue());
            }
        }

        // 统计待处理总数
        LambdaQueryWrapper<WarningAlert> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(WarningAlert::getStatus, 0).eq(WarningAlert::getDeleted, 0);
        stats.setPendingCount(Math.toIntExact(warningAlertMapper.selectCount(pendingWrapper)));

        // 统计今日新增
        LambdaQueryWrapper<WarningAlert> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.ge(WarningAlert::getCreateTime, LocalDate.now().atStartOfDay())
                .eq(WarningAlert::getDeleted, 0);
        stats.setTodayCount(Math.toIntExact(warningAlertMapper.selectCount(todayWrapper)));

        return stats;
    }

    @Override
    public Page<WarningAlertVO> getWarningPage(WarningQueryDTO queryDTO) {
        Page<Map<String, Object>> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());

        IPage<Map<String, Object>> resultPage = warningAlertMapper.selectWarningPage(
                page,
                queryDTO.getKeyword(),
                queryDTO.getRiskLevel(),
                queryDTO.getWarningType(),
                queryDTO.getStatus()
        );

        // 转换为 VO
        Page<WarningAlertVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<WarningAlertVO> voList = resultPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public WarningAlertVO getWarningDetail(Long id) {
        Map<String, Object> detail = warningAlertMapper.selectWarningDetail(id);
        if (detail == null || detail.isEmpty()) {
            throw new BusinessException("预警信息不存在");
        }
        return convertToDetailVO(detail);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processWarning(WarningProcessDTO processDTO) {
        WarningAlert alert = warningAlertMapper.selectById(processDTO.getId());
        if (alert == null) {
            throw new BusinessException("预警信息不存在");
        }

        if (alert.getStatus() != 0) {
            throw new BusinessException("该预警已被处理");
        }

        LambdaUpdateWrapper<WarningAlert> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(WarningAlert::getId, processDTO.getId())
                .set(WarningAlert::getStatus, processDTO.getStatus())
                .set(WarningAlert::getProcessResult, processDTO.getResult())
                .set(WarningAlert::getProcessedAt, LocalDateTime.now())
                .set(WarningAlert::getUpdateTime, LocalDateTime.now());

        int rows = warningAlertMapper.update(null, updateWrapper);
        if (rows == 0) {
            throw new BusinessException("处理失败，请重试");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchProcess(List<Long> ids, Integer status, String result) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要处理的预警");
        }

        LambdaUpdateWrapper<WarningAlert> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(WarningAlert::getId, ids)
                .eq(WarningAlert::getStatus, 0)
                .set(WarningAlert::getStatus, status)
                .set(WarningAlert::getProcessResult, result)
                .set(WarningAlert::getProcessedAt, LocalDateTime.now())
                .set(WarningAlert::getUpdateTime, LocalDateTime.now());

        warningAlertMapper.update(null, updateWrapper);
    }

    /**
     * 转换为列表 VO
     */
    private WarningAlertVO convertToVO(Map<String, Object> map) {
        WarningAlertVO vo = new WarningAlertVO();
        vo.setId(((Number) map.get("id")).longValue());
        vo.setRiskLevel((Integer) map.get("riskLevel"));
        vo.setRiskLevelName(getRiskLevelName((Integer) map.get("riskLevel")));
        vo.setWarningType((Integer) map.get("warningType"));
        vo.setWarningTypeName(getWarningTypeName((Integer) map.get("warningType")));
        vo.setContent((String) map.get("content"));
        vo.setRiskScore((Integer) map.get("riskScore"));
        vo.setStatus((Integer) map.get("status"));
        vo.setProcessResult((String) map.get("processResult"));
        vo.setRelatedUser((String) map.get("relatedUserName"));
        vo.setCreateTime((LocalDateTime) map.get("createTime"));
        vo.setProcessedAt((LocalDateTime) map.get("processedAt"));
        return vo;
    }

    /**
     * 转换为详情 VO
     */
    private WarningAlertVO convertToDetailVO(Map<String, Object> map) {
        WarningAlertVO vo = convertToVO(map);
        vo.setAnalysis((String) map.get("analysis"));
        vo.setSuggestion((String) map.get("suggestion"));
        vo.setProcessorName((String) map.get("processorName"));

        String relatedKnowledge = (String) map.get("related_knowledge");
        if (StringUtils.hasText(relatedKnowledge)) {
            vo.setRelatedKnowledge(Arrays.asList(relatedKnowledge.split(",")));
        }

        return vo;
    }

    /**
     * 获取风险等级名称
     */
    private String getRiskLevelName(Integer level) {
        if (level == null) return "未知";
        return switch (level) {
            case 1 -> "高风险";
            case 2 -> "中风险";
            case 3 -> "低风险";
            default -> "未知";
        };
    }

    /**
     * 获取预警类型名称
     */
    private String getWarningTypeName(Integer type) {
        if (type == null) return "未知";
        return switch (type) {
            case 1 -> "电信诈骗";
            case 2 -> "网络诈骗";
            case 3 -> "校园贷";
            case 4 -> "兼职诈骗";
            default -> "其他";
        };
    }
}
