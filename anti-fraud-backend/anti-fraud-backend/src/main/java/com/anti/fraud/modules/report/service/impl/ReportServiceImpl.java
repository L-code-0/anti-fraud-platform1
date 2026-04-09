package com.anti.fraud.modules.report.service.impl;

import com.anti.fraud.common.exception.BusinessException;
import com.anti.fraud.common.utils.SecurityUtils;
import com.anti.fraud.modules.report.dto.ReportSubmitDTO;
import com.anti.fraud.modules.report.entity.ReportInfo;
import com.anti.fraud.modules.report.entity.WarningInfo;
import com.anti.fraud.modules.report.mapper.ReportInfoMapper;
import com.anti.fraud.modules.report.mapper.WarningInfoMapper;
import com.anti.fraud.modules.report.service.ReportService;
import com.anti.fraud.modules.report.vo.ReportVO;
import com.anti.fraud.modules.report.vo.WarningVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportInfoMapper reportMapper;
    private final WarningInfoMapper warningMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void submitReport(ReportSubmitDTO submitDTO) {
        Long userId = SecurityUtils.getCurrentUserId();

        ReportInfo report = new ReportInfo();
        report.setUserId(userId);
        report.setReportNo(generateReportNo());
        report.setReportType(submitDTO.getReportType());
        report.setFraudType(submitDTO.getFraudType());
        report.setTitle(submitDTO.getTitle());
        report.setDescription(submitDTO.getDescription());
        report.setPhoneNumber(submitDTO.getPhoneNumber());
        report.setLinkUrl(submitDTO.getLinkUrl());

        if (submitDTO.getImages() != null && !submitDTO.getImages().isEmpty()) {
            try {
                report.setImages(objectMapper.writeValueAsString(submitDTO.getImages()));
            } catch (JsonProcessingException e) {
                // 忽略
            }
        }

        report.setContactName(submitDTO.getContactName());
        report.setContactPhone(submitDTO.getContactPhone());
        report.setIsAnonymous(submitDTO.getIsAnonymous() != null && submitDTO.getIsAnonymous() ? 1 : 0);
        report.setStatus(0);
        report.setRiskLevel(assessRiskLevel(submitDTO));

        reportMapper.insert(report);
    }

    @Override
    public Page<ReportVO> getMyReports(Integer page, Integer size) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("请先登录");
        }

        Page<ReportInfo> reportPage = new Page<>(page, size);
        reportMapper.selectPage(reportPage,
                new LambdaQueryWrapper<ReportInfo>()
                        .eq(ReportInfo::getUserId, userId)
                        .orderByDesc(ReportInfo::getCreateTime)
        );

        // 手动转换避免类型不兼容
        Page<ReportVO> result = new Page<>(reportPage.getCurrent(), reportPage.getSize(), reportPage.getTotal());
        result.setRecords(reportPage.getRecords().stream().map(this::convertToReportVO).collect(Collectors.toList()));
        return result;
    }

    @Override
    public ReportVO getReportDetail(Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        ReportInfo report = reportMapper.selectById(id);

        if (report == null) {
            throw new BusinessException("举报信息不存在");
        }

        if (!report.getUserId().equals(userId)) {
            throw new BusinessException("无权查看此举报");
        }

        return convertToReportVO(report);
    }

    @Override
    public List<WarningVO> getWarningList() {
        List<WarningInfo> warnings = warningMapper.selectList(
                new LambdaQueryWrapper<WarningInfo>()
                        .eq(WarningInfo::getStatus, 1)
                        .orderByDesc(WarningInfo::getWarningLevel)
                        .orderByDesc(WarningInfo::getPublishTime)
                        .last("LIMIT 20")
        );

        return warnings.stream()
                .map(this::convertToWarningVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public WarningVO getWarningDetail(Long id) {
        WarningInfo warning = warningMapper.selectById(id);
        if (warning == null) {
            throw new BusinessException("预警信息不存在");
        }

        warning.setViewCount(warning.getViewCount() + 1);
        warningMapper.updateById(warning);

        return convertToWarningVO(warning);
    }

    @Override
    public List<WarningVO> getLatestWarnings() {
        List<WarningInfo> warnings = warningMapper.selectList(
                new LambdaQueryWrapper<WarningInfo>()
                        .eq(WarningInfo::getStatus, 1)
                        .orderByDesc(WarningInfo::getPublishTime)
                        .last("LIMIT 5")
        );

        return warnings.stream()
                .map(this::convertToWarningVO)
                .collect(Collectors.toList());
    }

    private String generateReportNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "RPT" + timestamp + random;
    }

    private Integer assessRiskLevel(ReportSubmitDTO submitDTO) {
        // 简单风险评估逻辑
        int score = 0;

        if (submitDTO.getPhoneNumber() != null && !submitDTO.getPhoneNumber().isEmpty()) {
            score += 1;
        }
        if (submitDTO.getLinkUrl() != null && !submitDTO.getLinkUrl().isEmpty()) {
            score += 1;
        }
        if (submitDTO.getDescription() != null && submitDTO.getDescription().length() > 100) {
            score += 1;
        }
        if (submitDTO.getFraudType() != null) {
            score += 1;
        }

        if (score >= 3) return 3;
        if (score >= 2) return 2;
        return 1;
    }

    private ReportVO convertToReportVO(ReportInfo report) {
        ReportVO vo = new ReportVO();
        vo.setId(report.getId());
        vo.setReportNo(report.getReportNo());
        vo.setReportType(report.getReportType());
        vo.setFraudType(report.getFraudType());
        vo.setTitle(report.getTitle());
        vo.setDescription(report.getDescription());
        vo.setStatus(report.getStatus());
        vo.setRiskLevel(report.getRiskLevel());
        vo.setHandleResult(report.getHandleResult());
        vo.setRewardPoints(report.getRewardPoints());
        vo.setCreateTime(report.getCreateTime());
        vo.setHandleTime(report.getHandleTime());
        return vo;
    }

    private WarningVO convertToWarningVO(WarningInfo warning) {
        WarningVO vo = new WarningVO();
        vo.setId(warning.getId());
        vo.setTitle(warning.getTitle());
        vo.setWarningLevel(warning.getWarningLevel());
        vo.setFraudType(warning.getFraudType());
        vo.setContent(warning.getContent());
        vo.setPreventionTips(warning.getPreventionTips());
        vo.setViewCount(warning.getViewCount());
        vo.setPublishTime(warning.getPublishTime());
        return vo;
    }
}

