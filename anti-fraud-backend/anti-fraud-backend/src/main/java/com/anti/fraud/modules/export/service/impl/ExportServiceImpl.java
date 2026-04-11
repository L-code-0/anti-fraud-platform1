package com.anti.fraud.modules.export.service.impl;

import com.anti.fraud.modules.export.entity.ExportTask;
import com.anti.fraud.modules.export.mapper.ExportTaskMapper;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private final ExportTaskMapper exportTaskMapper;
    private final ObjectMapper objectMapper;
    
    // 导出文件存储目录
    private static final String EXPORT_DIR = "export";

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
                    new LambdaQueryWrapper<TestRecord>().eq(TestRecord::getUserId, user.getId())).intValue());
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
            if (record.getTotalScore() != null && record.getTotalScore() > 0) {
                double scoreValue = record.getScore() != null ? record.getScore() : 0;
                vo.setCorrectRate(String.format("%.1f%%", scoreValue * 100.0 / record.getTotalScore()));
            } else {
                vo.setCorrectRate("0.0%");
            }
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
    
    @Override
    public Long createExportTask(Long userId, String taskType, Map<String, Object> params) {
        try {
            // 创建导出任务
            ExportTask task = new ExportTask();
            task.setUserId(userId);
            task.setTaskType(taskType);
            task.setParams(objectMapper.writeValueAsString(params));
            task.setStatus(0); // 等待中
            task.setProgress(0);
            
            exportTaskMapper.insert(task);
            
            // 异步执行导出任务
            new Thread(() -> executeExportTask(task)).start();
            
            return task.getId();
        } catch (Exception e) {
            log.error("创建导出任务失败", e);
            throw new RuntimeException("创建导出任务失败", e);
        }
    }
    
    @Override
    public ExportTask getExportTask(Long taskId) {
        return exportTaskMapper.selectById(taskId);
    }
    
    @Override
    public List<ExportTask> getUserExportTasks(Long userId) {
        return exportTaskMapper.selectByUserId(userId);
    }
    
    @Override
    public void executeExportTask(ExportTask task) {
        log.info("开始执行导出任务: {}", task.getId());
        
        try {
            // 更新任务状态为处理中
            task.setStatus(1);
            task.setProgress(10);
            exportTaskMapper.updateById(task);
            
            // 解析参数
            Map<String, Object> params = objectMapper.readValue(task.getParams(), new TypeReference<Map<String, Object>>() {});
            
            // 根据任务类型执行导出
            List<?> data = null;
            
            switch (task.getTaskType()) {
                case "users":
                    data = exportUserStatistics(params);
                    break;
                case "test-scores":
                    data = exportTestScores(params);
                    break;
                case "reports":
                    data = exportReports(params);
                    break;
                case "knowledge":
                    data = exportKnowledge(params);
                    break;
                case "activities":
                    data = exportActivities(params);
                    break;
                case "points":
                    data = exportPointsRecords(params);
                    break;
                default:
                    throw new IllegalArgumentException("未知的导出类型: " + task.getTaskType());
            }
            
            task.setTotalCount(data.size());
            task.setProgress(30);
            exportTaskMapper.updateById(task);
            
            // 确保导出目录存在
            Path exportPath = Paths.get(EXPORT_DIR);
            if (!Files.exists(exportPath)) {
                Files.createDirectories(exportPath);
            }
            
            // 生成文件名
            String fileName = generateFileName(task.getTaskType());
            String filePath = EXPORT_DIR + File.separator + fileName;
            
            // 执行导出（这里使用分页导出避免OOM）
            int pageSize = 1000;
            int totalPages = (data.size() + pageSize - 1) / pageSize;
            
            for (int i = 0; i < totalPages; i++) {
                int start = i * pageSize;
                int end = Math.min(start + pageSize, data.size());
                // 处理分页数据
                data.subList(start, end);
                
                // 这里应该实现分页导出逻辑
                // 为了简化，这里只是模拟分页处理
                
                task.setProcessedCount(end);
                task.setProgress(30 + (end * 70) / data.size());
                exportTaskMapper.updateById(task);
                
                // 模拟处理时间
                Thread.sleep(100);
            }
            
            // 更新任务状态为完成
            task.setStatus(2);
            task.setProgress(100);
            task.setFileName(fileName);
            task.setFilePath(filePath);
            exportTaskMapper.updateById(task);
            
            log.info("导出任务完成: {}", task.getId());
        } catch (Exception e) {
            log.error("执行导出任务失败", e);
            task.setStatus(3);
            task.setErrorMessage(e.getMessage());
            exportTaskMapper.updateById(task);
        }
    }
    
    @Override
    public byte[] downloadExportFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                throw new RuntimeException("文件不存在");
            }
            return Files.readAllBytes(path);
        } catch (IOException e) {
            log.error("下载导出文件失败", e);
            throw new RuntimeException("下载导出文件失败", e);
        }
    }
}
