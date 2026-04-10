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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 举报预警服务实现类
 * <p>
 * 提供举报管理和预警信息相关的核心业务逻辑。
 * 举报功能支持用户提交诈骗举报、自动风险评估、举报处理跟踪等。
 * 预警功能包括预警信息发布、浏览统计等。
 * </p>
 *
 * @author Anti-Fraud Platform Team
 * @version 1.0
 * @since 2024-01-01
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final ReportInfoMapper reportMapper;
    private final WarningInfoMapper warningMapper;
    private final ObjectMapper objectMapper;

    /**
     * 提交举报信息
     * <p>
     * 用户提交诈骗举报，系统自动完成以下处理：
     * 1. 生成唯一的举报编号（格式：RPT+时间戳+随机字符）
     * 2. 根据举报内容评估风险等级
     * 3. 保存举报信息到数据库
     * </p>
     *
     * @param submitDTO 举报提交信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitReport(ReportSubmitDTO submitDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("用户提交举报: userId={}, reportType={}, fraudType={}", 
                userId, submitDTO.getReportType(), submitDTO.getFraudType());

        // 构建举报信息实体
        ReportInfo report = new ReportInfo();
        report.setUserId(userId);
        report.setReportNo(generateReportNo());
        report.setReportType(submitDTO.getReportType());
        report.setFraudType(submitDTO.getFraudType());
        report.setTitle(submitDTO.getTitle());
        report.setDescription(submitDTO.getDescription());
        report.setPhoneNumber(submitDTO.getPhoneNumber());
        report.setLinkUrl(submitDTO.getLinkUrl());

        // 处理图片列表，序列化为JSON字符串
        if (submitDTO.getImages() != null && !submitDTO.getImages().isEmpty()) {
            try {
                report.setImages(objectMapper.writeValueAsString(submitDTO.getImages()));
                log.debug("举报图片数量: {}", submitDTO.getImages().size());
            } catch (JsonProcessingException e) {
                log.error("图片JSON序列化失败: {}", e.getMessage());
                // 忽略，继续保存其他信息
            }
        }

        // 设置联系方式
        report.setContactName(submitDTO.getContactName());
        report.setContactPhone(submitDTO.getContactPhone());
        
        // 处理匿名标记
        report.setIsAnonymous(submitDTO.getIsAnonymous() != null && submitDTO.getIsAnonymous() ? 1 : 0);
        
        // 初始状态为待处理
        report.setStatus(0);
        
        // 自动评估风险等级
        report.setRiskLevel(assessRiskLevel(submitDTO));
        log.debug("举报风险等级评估: level={}", report.getRiskLevel());

        // 保存举报信息
        reportMapper.insert(report);
        log.info("举报提交成功: reportNo={}", report.getReportNo());
    }

    /**
     * 获取我的举报记录
     * <p>
     * 分页查询当前用户提交的所有举报记录，按创建时间倒序排列。
     * </p>
     *
     * @param page 页码
     * @param size 每页数量
     * @return 举报分页列表
     * @throws BusinessException 当用户未登录时抛出
     */
    @Override
    public Page<ReportVO> getMyReports(Integer page, Integer size) {
        log.debug("查询用户举报记录: page={}, size={}", page, size);
        
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            log.warn("查询举报记录失败：用户未登录");
            throw new BusinessException("请先登录");
        }

        // 构建分页查询
        Page<ReportInfo> reportPage = new Page<>(page, size);
        reportMapper.selectPage(reportPage,
                new LambdaQueryWrapper<ReportInfo>()
                        .eq(ReportInfo::getUserId, userId)
                        .orderByDesc(ReportInfo::getCreateTime)
        );

        // 转换为视图对象
        Page<ReportVO> result = new Page<>(reportPage.getCurrent(), reportPage.getSize(), reportPage.getTotal());
        result.setRecords(reportPage.getRecords().stream().map(this::convertToReportVO).collect(Collectors.toList()));
        
        log.debug("查询到{}条举报记录", reportPage.getTotal());
        return result;
    }

    /**
     * 获取举报详情
     * <p>
     * 根据举报ID获取详细信息。用户只能查看自己提交的举报。
     * </p>
     *
     * @param id 举报ID
     * @return 举报详情
     * @throws BusinessException 当举报不存在或无权查看时抛出
     */
    @Override
    public ReportVO getReportDetail(Long id) {
        log.debug("查询举报详情: id={}", id);
        
        Long userId = SecurityUtils.getCurrentUserId();
        ReportInfo report = reportMapper.selectById(id);

        if (report == null) {
            log.warn("查询举报失败：举报不存在, id={}", id);
            throw new BusinessException("举报信息不存在");
        }

        // 检查权限：只能查看自己的举报
        if (!report.getUserId().equals(userId)) {
            log.warn("查询举报失败：无权查看, id={}, userId={}", id, userId);
            throw new BusinessException("无权查看此举报");
        }

        return convertToReportVO(report);
    }

    /**
     * 获取预警列表
     * <p>
     * 查询所有生效的预警信息，按预警等级和发布时间排序，最多返回20条。
     * </p>
     *
     * @return 预警列表
     */
    @Override
    public List<WarningVO> getWarningList() {
        log.debug("查询预警列表");
        
        List<WarningInfo> warnings = warningMapper.selectList(
                new LambdaQueryWrapper<WarningInfo>()
                        .eq(WarningInfo::getStatus, 1) // 只查询生效的预警
                        .orderByDesc(WarningInfo::getWarningLevel) // 按预警等级倒序
                        .orderByDesc(WarningInfo::getPublishTime) // 按发布时间倒序
                        .last("LIMIT 20")
        );

        List<WarningVO> result = warnings.stream()
                .map(this::convertToWarningVO)
                .collect(Collectors.toList());
                
        log.debug("查询到{}条预警信息", result.size());
        return result;
    }

    /**
     * 获取预警详情
     * <p>
     * 根据预警ID获取详细信息，同时更新预警的浏览次数。
     * </p>
     *
     * @param id 预警ID
     * @return 预警详情
     * @throws BusinessException 当预警不存在时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public WarningVO getWarningDetail(Long id) {
        log.debug("查询预警详情: id={}", id);
        
        WarningInfo warning = warningMapper.selectById(id);
        if (warning == null) {
            log.warn("查询预警失败：预警不存在, id={}", id);
            throw new BusinessException("预警信息不存在");
        }

        // 更新浏览次数
        warning.setViewCount(warning.getViewCount() + 1);
        warningMapper.updateById(warning);
        log.debug("预警浏览次数更新: id={}, viewCount={}", id, warning.getViewCount());

        return convertToWarningVO(warning);
    }

    /**
     * 获取最新预警
     * <p>
     * 获取最近发布的5条预警信息，按发布时间倒序。
     * </p>
     *
     * @return 最新预警列表
     */
    @Override
    public List<WarningVO> getLatestWarnings() {
        log.debug("查询最新预警");
        
        List<WarningInfo> warnings = warningMapper.selectList(
                new LambdaQueryWrapper<WarningInfo>()
                        .eq(WarningInfo::getStatus, 1) // 只查询生效的预警
                        .orderByDesc(WarningInfo::getPublishTime) // 按发布时间倒序
                        .last("LIMIT 5")
        );

        return warnings.stream()
                .map(this::convertToWarningVO)
                .collect(Collectors.toList());
    }

    /**
     * 生成举报编号
     * <p>
     * 格式：RPT + yyyyMMddHHmmss + 4位随机字符
     * 示例：RPT20240101123045A1B2
     * </p>
     *
     * @return 唯一的举报编号
     */
    private String generateReportNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        String reportNo = "RPT" + timestamp + random;
        log.debug("生成举报编号: {}", reportNo);
        return reportNo;
    }

    /**
     * 评估举报风险等级
     * <p>
     * 根据举报信息的内容完整性进行风险评估：
     * - 提供手机号码 +1分
     * - 提供链接地址 +1分
     * - 描述内容超过100字符 +1分
     * - 选择诈骗类型 +1分
     * <p>
     * 风险等级：
     * - 3分及以上：高风险(3)
     * - 2分：中风险(2)
     * - 0-1分：低风险(1)
     * </p>
     *
     * @param submitDTO 举报提交信息
     * @return 风险等级（1-低，2-中，3-高）
     */
    private Integer assessRiskLevel(ReportSubmitDTO submitDTO) {
        log.debug("开始评估举报风险等级");
        
        int score = 0;

        // 评估手机号码
        if (submitDTO.getPhoneNumber() != null && !submitDTO.getPhoneNumber().isEmpty()) {
            score += 1;
            log.debug("风险评估：提供手机号码 +1分");
        }
        
        // 评估链接地址
        if (submitDTO.getLinkUrl() != null && !submitDTO.getLinkUrl().isEmpty()) {
            score += 1;
            log.debug("风险评估：提供链接地址 +1分");
        }
        
        // 评估描述内容长度
        if (submitDTO.getDescription() != null && submitDTO.getDescription().length() > 100) {
            score += 1;
            log.debug("风险评估：详细描述 +1分");
        }
        
        // 评估诈骗类型选择
        if (submitDTO.getFraudType() != null) {
            score += 1;
            log.debug("风险评估：选择诈骗类型 +1分");
        }

        // 计算最终风险等级
        Integer riskLevel;
        if (score >= 3) {
            riskLevel = 3; // 高风险
        } else if (score >= 2) {
            riskLevel = 2; // 中风险
        } else {
            riskLevel = 1; // 低风险
        }
        
        log.debug("风险评估完成: score={}, riskLevel={}", score, riskLevel);
        return riskLevel;
    }

    /**
     * 将举报实体转换为视图对象
     *
     * @param report 举报实体
     * @return 举报视图对象
     */
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

    /**
     * 将预警实体转换为视图对象
     *
     * @param warning 预警实体
     * @return 预警视图对象
     */
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
