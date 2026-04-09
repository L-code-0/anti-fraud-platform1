package com.anti.fraud.modules.report.service.impl;

import com.anti.fraud.common.exception.BusinessException;
import com.anti.fraud.common.utils.SecurityUtils;
import com.anti.fraud.modules.points.service.PointsService;
import com.anti.fraud.modules.report.dto.ReportHandleDTO;
import com.anti.fraud.modules.report.dto.WarningCreateDTO;
import com.anti.fraud.modules.report.entity.ReportInfo;
import com.anti.fraud.modules.report.entity.WarningInfo;
import com.anti.fraud.modules.report.mapper.ReportInfoMapper;
import com.anti.fraud.modules.report.mapper.WarningInfoMapper;
import com.anti.fraud.modules.report.service.ReportManageService;
import com.anti.fraud.modules.report.vo.ReportVO;
import com.anti.fraud.modules.report.vo.WarningVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportManageServiceImpl implements ReportManageService {

    private final ReportInfoMapper reportMapper;
    private final WarningInfoMapper warningMapper;
    private final PointsService pointsService;

    @Override
    public Page<ReportVO> getReportPage(Integer page, Integer size, Integer status, Integer riskLevel) {
        Page<ReportInfo> reportPage = new Page<>(page, size);

        LambdaQueryWrapper<ReportInfo> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(ReportInfo::getStatus, status);
        }

        if (riskLevel != null) {
            wrapper.eq(ReportInfo::getRiskLevel, riskLevel);
        }

        wrapper.orderByDesc(ReportInfo::getCreateTime);

        reportMapper.selectPage(reportPage, wrapper);

        // 手动转换避免类型不兼容
        Page<ReportVO> result = new Page<>(reportPage.getCurrent(), reportPage.getSize(), reportPage.getTotal());
        result.setRecords(reportPage.getRecords().stream().map(this::convertToReportVO).collect(Collectors.toList()));
        return result;
    }

    @Override
    public ReportVO getReportDetail(Long id) {
        ReportInfo report = reportMapper.selectById(id);
        if (report == null) {
            throw new BusinessException("举报信息不存在");
        }
        return convertToReportVO(report);
    }

    @Override
    @Transactional
    public void handleReport(Long id, ReportHandleDTO handleDTO) {
        ReportInfo report = reportMapper.selectById(id);
        if (report == null) {
            throw new BusinessException("举报信息不存在");
        }

        if (report.getStatus() == 2) {
            throw new BusinessException("该举报已处理");
        }

        Long userId = SecurityUtils.getCurrentUserId();

        report.setStatus(2);
        report.setRiskLevel(handleDTO.getRiskLevel());
        report.setHandleResult(handleDTO.getHandleResult());
        report.setRewardPoints(handleDTO.getRewardPoints());
        report.setHandlerId(userId);
        report.setHandleTime(LocalDateTime.now());

        reportMapper.updateById(report);

        // 发放积分奖励
        if (handleDTO.getRewardPoints() != null && handleDTO.getRewardPoints() > 0 && report.getUserId() != null) {
            pointsService.addPoints(
                    report.getUserId(),
                    handleDTO.getRewardPoints(),
                    "举报奖励",
                    report.getId(),
                    "举报核实通过奖励"
            );
        }
    }

    @Override
    public Page<WarningVO> getWarningPage(Integer page, Integer size, Integer warningLevel, Integer status) {
        Page<WarningInfo> warningPage = new Page<>(page, size);

        LambdaQueryWrapper<WarningInfo> wrapper = new LambdaQueryWrapper<>();

        if (warningLevel != null) {
            wrapper.eq(WarningInfo::getWarningLevel, warningLevel);
        }

        if (status != null) {
            wrapper.eq(WarningInfo::getStatus, status);
        }

        wrapper.orderByDesc(WarningInfo::getCreateTime);

        warningMapper.selectPage(warningPage, wrapper);

        // 手动转换避免类型不兼容
        Page<WarningVO> result = new Page<>(warningPage.getCurrent(), warningPage.getSize(), warningPage.getTotal());
        result.setRecords(warningPage.getRecords().stream().map(this::convertToWarningVO).collect(Collectors.toList()));
        return result;
    }

    @Override
    @Transactional
    public void createWarning(WarningCreateDTO createDTO) {
        WarningInfo warning = new WarningInfo();
        warning.setTitle(createDTO.getTitle());
        warning.setWarningLevel(createDTO.getWarningLevel());
        warning.setFraudType(createDTO.getFraudType());
        warning.setContent(createDTO.getContent());
        warning.setPreventionTips(createDTO.getPreventionTips());
        warning.setViewCount(0);
        warning.setStatus(0);
        warning.setPublisherId(SecurityUtils.getCurrentUserId());

        warningMapper.insert(warning);
    }

    @Override
    @Transactional
    public void updateWarning(Long id, WarningCreateDTO createDTO) {
        WarningInfo warning = warningMapper.selectById(id);
        if (warning == null) {
            throw new BusinessException("预警信息不存在");
        }

        warning.setTitle(createDTO.getTitle());
        warning.setWarningLevel(createDTO.getWarningLevel());
        warning.setFraudType(createDTO.getFraudType());
        warning.setContent(createDTO.getContent());
        warning.setPreventionTips(createDTO.getPreventionTips());

        warningMapper.updateById(warning);
    }

    @Override
    @Transactional
    public void deleteWarning(Long id) {
        warningMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void publishWarning(Long id) {
        WarningInfo warning = warningMapper.selectById(id);
        if (warning == null) {
            throw new BusinessException("预警信息不存在");
        }

        warning.setStatus(1);
        warning.setPublishTime(LocalDateTime.now());
        warning.setPublisherId(SecurityUtils.getCurrentUserId());

        warningMapper.updateById(warning);
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
