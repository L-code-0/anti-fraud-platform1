package com.anti.fraud.modules.export.service.impl;

import com.anti.fraud.modules.export.service.ExportService;
import com.anti.fraud.modules.export.vo.ReportExportVO;
import com.anti.fraud.modules.export.vo.TestScoreExportVO;
import com.anti.fraud.modules.export.vo.UserStatisticsExportVO;
import com.anti.fraud.modules.report.entity.ReportInfo;
import com.anti.fraud.modules.report.mapper.ReportInfoMapper;
import com.anti.fraud.modules.test.entity.TestRecord;
import com.anti.fraud.modules.test.mapper.TestRecordMapper;
import com.anti.fraud.modules.user.entity.User;
import com.anti.fraud.modules.user.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据导出服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {
    
    private final UserMapper userMapper;
    private final TestRecordMapper testRecordMapper;
    private final ReportInfoMapper reportInfoMapper;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    @Override
    public List<UserStatisticsExportVO> exportUserStatistics(Map<String, Object> params) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        if (params.get("department") != null) {
            wrapper.eq(User::getDepartment, params.get("department"));
        }
        if (params.get("keyword") != null) {
            wrapper.and(w -> w.like(User::getUsername, params.get("keyword"))
                    .or().like(User::getRealName, params.get("keyword")));
        }
        
        List<User> users = userMapper.selectList(wrapper);
        
        return users.stream().map(user -> {
            UserStatisticsExportVO vo = new UserStatisticsExportVO();
            vo.setUsername(user.getUsername());
            vo.setRealName(user.getRealName() != null ? user.getRealName() : "-");
            vo.setDepartment(user.getDepartment() != null ? user.getDepartment() : "-");
            vo.setPoints(user.getPoints());
            vo.setLevel(user.getLevel());
            vo.setStudyDuration(0); // 可从学习记录表查询
            vo.setTestCount(testRecordMapper.selectCount(
                    new LambdaQueryWrapper<TestRecord>().eq(TestRecord::getUserId, user.getId())));
            vo.setSimulationCount(0); // 可从演练记录表查询
            vo.setCreateTime(user.getCreateTime() != null ? 
                    user.getCreateTime().format(FORMATTER) : "-");
            return vo;
        }).collect(Collectors.toList());
    }
    
    @Override
    public List<TestScoreExportVO> exportTestScores(Map<String, Object> params) {
        LambdaQueryWrapper<TestRecord> wrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        if (params.get("paperId") != null) {
            wrapper.eq(TestRecord::getPaperId, params.get("paperId"));
        }
        if (params.get("startTime") != null) {
            wrapper.ge(TestRecord::getSubmitTime, params.get("startTime"));
        }
        if (params.get("endTime") != null) {
            wrapper.le(TestRecord::getSubmitTime, params.get("endTime"));
        }
        
        wrapper.orderByDesc(TestRecord::getSubmitTime);
        
        List<TestRecord> records = testRecordMapper.selectList(wrapper);
        Map<Long, User> userMap = getUserMap(records.stream()
                .map(TestRecord::getUserId).collect(Collectors.toSet()));
        
        return records.stream().map(record -> {
            TestScoreExportVO vo = new TestScoreExportVO();
            User user = userMap.get(record.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
                vo.setRealName(user.getRealName() != null ? user.getRealName() : "-");
            }
            vo.setPaperName("试卷" + record.getPaperId()); // 可关联查询
            vo.setScore(record.getScore());
            vo.setTotalScore(record.getTotalScore());
            vo.setCorrectRate(String.format("%.1f%%", record.getScore() * 100.0 / record.getTotalScore()));
            vo.setDuration(record.getDuration());
            vo.setSubmitTime(record.getSubmitTime() != null ? 
                    record.getSubmitTime().format(FORMATTER) : "-");
            vo.setResult(record.getIsPassed() == 1 ? "通过" : "未通过");
            return vo;
        }).collect(Collectors.toList());
    }
    
    @Override
    public List<ReportExportVO> exportReports(Map<String, Object> params) {
        LambdaQueryWrapper<ReportInfo> wrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        if (params.get("status") != null) {
            wrapper.eq(ReportInfo::getStatus, params.get("status"));
        }
        if (params.get("fraudType") != null) {
            wrapper.eq(ReportInfo::getFraudType, params.get("fraudType"));
        }
        if (params.get("startTime") != null) {
            wrapper.ge(ReportInfo::getCreateTime, params.get("startTime"));
        }
        if (params.get("endTime") != null) {
            wrapper.le(ReportInfo::getCreateTime, params.get("endTime"));
        }
        
        wrapper.orderByDesc(ReportInfo::getCreateTime);
        
        List<ReportInfo> reports = reportInfoMapper.selectList(wrapper);
        Map<Long, User> userMap = getUserMap(reports.stream()
                .map(ReportInfo::getReporterId).collect(Collectors.toSet()));
        
        return reports.stream().map(report -> {
            ReportExportVO vo = new ReportExportVO();
            vo.setReportType(getReportTypeName(report.getReportType()));
            vo.setFraudType(report.getFraudType() != null ? report.getFraudType() : "-");
            User reporter = userMap.get(report.getReporterId());
            if (reporter != null) {
                vo.setReporterName(reporter.getRealName() != null ? reporter.getRealName() : reporter.getUsername());
            }
            vo.setContent(report.getContent() != null ? 
                    report.getContent().substring(0, Math.min(50, report.getContent().length())) : "-");
            vo.setCreateTime(report.getCreateTime() != null ? 
                    report.getCreateTime().format(FORMATTER) : "-");
            vo.setStatus(getStatusName(report.getStatus()));
            
            User handler = userMap.get(report.getHandlerId());
            if (handler != null) {
                vo.setHandlerName(handler.getRealName() != null ? handler.getRealName() : handler.getUsername());
            }
            vo.setHandleTime(report.getHandleTime() != null ? 
                    report.getHandleTime().format(FORMATTER) : "-");
            vo.setHandleResult(report.getHandleResult() != null ? report.getHandleResult() : "-");
            return vo;
        }).collect(Collectors.toList());
    }
    
    @Override
    public List<Map<String, Object>> exportKnowledge(Map<String, Object> params) {
        // 知识库导出逻辑
        return new ArrayList<>();
    }
    
    @Override
    public List<Map<String, Object>> exportActivities(Map<String, Object> params) {
        // 活动参与记录导出逻辑
        return new ArrayList<>();
    }
    
    @Override
    public List<Map<String, Object>> exportPointsRecords(Map<String, Object> params) {
        // 积分记录导出逻辑
        return new ArrayList<>();
    }
    
    @Override
    public String generateFileName(String type) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
        return type + "_" + date + "_" + time + ".xlsx";
    }
    
    /**
     * 获取用户Map
     */
    private Map<Long, User> getUserMap(Set<Long> userIds) {
        if (userIds.isEmpty()) {
            return new HashMap<>();
        }
        List<User> users = userMapper.selectBatchIds(userIds);
        return users.stream().collect(Collectors.toMap(User::getId, u -> u));
    }
    
    /**
     * 获取举报类型名称
     */
    private String getReportTypeName(Integer type) {
        if (type == null) return "-";
        switch (type) {
            case 1: return "电信诈骗";
            case 2: return "网络诈骗";
            case 3: return "线下诈骗";
            case 4: return "其他";
            default: return "未知";
        }
    }
    
    /**
     * 获取状态名称
     */
    private String getStatusName(Integer status) {
        if (status == null) return "-";
        switch (status) {
            case 0: return "待处理";
            case 1: return "处理中";
            case 2: return "已处理";
            case 3: return "已驳回";
            default: return "未知";
        }
    }
}
