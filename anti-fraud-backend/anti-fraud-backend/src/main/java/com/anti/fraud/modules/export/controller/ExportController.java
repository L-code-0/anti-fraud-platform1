package com.anti.fraud.modules.export.controller;

import com.alibaba.excel.EasyExcel;
import com.anti.fraud.common.exception.BusinessException;
import com.anti.fraud.modules.export.entity.ExportTask;
import com.anti.fraud.modules.export.service.ExportService;
import com.anti.fraud.modules.export.vo.ReportExportVO;
import com.anti.fraud.modules.export.vo.TestScoreExportVO;
import com.anti.fraud.modules.export.vo.UserStatisticsExportVO;
import com.anti.fraud.security.service.RateLimitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据导出控制器
 */
@Slf4j
@Tag(name = "数据导出", description = "Excel数据导出接口")
@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;
    private final RateLimitService rateLimitService;

    @Operation(summary = "导出用户统计数据")
    @PostMapping("/users")
    public void exportUserStatistics(
            @RequestBody(required = false) Map<String, Object> params,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        // 检查导出频率限制
        RateLimitService.RateLimitResult rateLimitResult = rateLimitService.checkExportLimit(request);
        if (!rateLimitResult.isAllowed()) {
            throw new BusinessException(rateLimitResult.getMessage());
        }
        
        if (params == null) {
            params = new HashMap<>();
        }

        List<UserStatisticsExportVO> data = exportService.exportUserStatistics(params);
        String fileName = exportService.generateFileName("用户统计");

        writeExcel(response, fileName, UserStatisticsExportVO.class, data);
    }

    @Operation(summary = "导出测验成绩")
    @PostMapping("/test-scores")
    public void exportTestScores(
            @RequestBody(required = false) Map<String, Object> params,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        // 检查导出频率限制
        RateLimitService.RateLimitResult rateLimitResult = rateLimitService.checkExportLimit(request);
        if (!rateLimitResult.isAllowed()) {
            throw new BusinessException(rateLimitResult.getMessage());
        }
        
        if (params == null) {
            params = new HashMap<>();
        }

        List<TestScoreExportVO> data = exportService.exportTestScores(params);
        String fileName = exportService.generateFileName("测验成绩");

        writeExcel(response, fileName, TestScoreExportVO.class, data);
    }

    @Operation(summary = "导出举报记录")
    @PostMapping("/reports")
    public void exportReports(
            @RequestBody(required = false) Map<String, Object> params,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        // 检查导出频率限制
        RateLimitService.RateLimitResult rateLimitResult = rateLimitService.checkExportLimit(request);
        if (!rateLimitResult.isAllowed()) {
            throw new BusinessException(rateLimitResult.getMessage());
        }
        
        if (params == null) {
            params = new HashMap<>();
        }

        List<ReportExportVO> data = exportService.exportReports(params);
        String fileName = exportService.generateFileName("举报记录");

        writeExcel(response, fileName, ReportExportVO.class, data);
    }

    @Operation(summary = "导出知识库内容")
    @PostMapping("/knowledge")
    public void exportKnowledge(
            @RequestBody(required = false) Map<String, Object> params,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        // 检查导出频率限制
        RateLimitService.RateLimitResult rateLimitResult = rateLimitService.checkExportLimit(request);
        if (!rateLimitResult.isAllowed()) {
            throw new BusinessException(rateLimitResult.getMessage());
        }
        
        if (params == null) {
            params = new HashMap<>();
        }

        List<Map<String, Object>> data = exportService.exportKnowledge(params);
        String fileName = exportService.generateFileName("知识库");

        // 动态表头导出
        writeDynamicExcel(response, fileName, data);
    }

    @Operation(summary = "导出活动参与记录")
    @PostMapping("/activities")
    public void exportActivities(
            @RequestBody(required = false) Map<String, Object> params,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        // 检查导出频率限制
        RateLimitService.RateLimitResult rateLimitResult = rateLimitService.checkExportLimit(request);
        if (!rateLimitResult.isAllowed()) {
            throw new BusinessException(rateLimitResult.getMessage());
        }
        
        if (params == null) {
            params = new HashMap<>();
        }

        List<Map<String, Object>> data = exportService.exportActivities(params);
        String fileName = exportService.generateFileName("活动参与");

        writeDynamicExcel(response, fileName, data);
    }

    @Operation(summary = "导出积分记录")
    @PostMapping("/points")
    public void exportPointsRecords(
            @RequestBody(required = false) Map<String, Object> params,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        // 检查导出频率限制
        RateLimitService.RateLimitResult rateLimitResult = rateLimitService.checkExportLimit(request);
        if (!rateLimitResult.isAllowed()) {
            throw new BusinessException(rateLimitResult.getMessage());
        }
        
        if (params == null) {
            params = new HashMap<>();
        }

        List<Map<String, Object>> data = exportService.exportPointsRecords(params);
        String fileName = exportService.generateFileName("积分记录");

        writeDynamicExcel(response, fileName, data);
    }

    /**
     * 写入Excel（固定类型）
     */
    private <T> void writeExcel(HttpServletResponse response, String fileName,
                                Class<T> clazz, List<T> data) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition",
                "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));

        EasyExcel.write(response.getOutputStream(), clazz)
                .sheet("数据")
                .doWrite(data);

        log.info("导出Excel成功: {}", fileName);
    }

    /**
     * 写入Excel（动态类型）
     */
    private void writeDynamicExcel(HttpServletResponse response, String fileName, 
                                   List<Map<String, Object>> data) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", 
                "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));

        if (data.isEmpty()) {
            EasyExcel.write(response.getOutputStream())
                    .sheet("数据")
                    .doWrite(data);
        } else {
            // 使用动态表头，EasyExcel head() 需要 List<List<String>>
            List<List<String>> headList = new ArrayList<>();
            data.get(0).keySet().forEach(key -> headList.add(Arrays.asList(key)));
            EasyExcel.write(response.getOutputStream())
                    .head(headList)
                    .sheet("数据")
                    .doWrite(data);
        }

        log.info("导出Excel成功: {}", fileName);
    }
    
    // ==================== 异步导出接口 ====================
    
    @Operation(summary = "创建异步导出任务")
    @PostMapping("/async/{type}")
    public Map<String, Object> createAsyncExportTask(
            @PathVariable String type,
            @RequestBody(required = false) Map<String, Object> params,
            @RequestHeader("X-User-Id") Long userId,
            HttpServletRequest request
    ) {
        // 检查导出频率限制
        RateLimitService.RateLimitResult rateLimitResult = rateLimitService.checkExportLimit(request);
        if (!rateLimitResult.isAllowed()) {
            throw new BusinessException(rateLimitResult.getMessage());
        }
        
        if (params == null) {
            params = new HashMap<>();
        }
        
        Long taskId = exportService.createExportTask(userId, type, params);
        
        Map<String, Object> result = new HashMap<>();
        result.put("taskId", taskId);
        result.put("message", "导出任务已创建，正在处理中");
        return result;
    }
    
    @Operation(summary = "获取导出任务状态")
    @GetMapping("/task/{taskId}")
    public ExportTask getExportTaskStatus(@PathVariable Long taskId) {
        return exportService.getExportTask(taskId);
    }
    
    @Operation(summary = "获取用户导出任务列表")
    @GetMapping("/tasks")
    public List<ExportTask> getUserExportTasks(@RequestHeader("X-User-Id") Long userId) {
        return exportService.getUserExportTasks(userId);
    }
    
    @Operation(summary = "下载导出文件")
    @GetMapping("/download/{taskId}")
    public void downloadExportFile(
            @PathVariable Long taskId,
            HttpServletResponse response
    ) throws IOException {
        ExportTask task = exportService.getExportTask(taskId);
        if (task == null || task.getStatus() != 2) {
            response.setStatus(404);
            return;
        }
        
        byte[] fileData = exportService.downloadExportFile(task.getFilePath());
        
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", 
                "attachment;filename=" + URLEncoder.encode(task.getFileName(), StandardCharsets.UTF_8));
        response.setContentLength(fileData.length);
        
        try (OutputStream os = response.getOutputStream()) {
            os.write(fileData);
            os.flush();
        }
        
        log.info("下载导出文件成功: {}", task.getFileName());
    }
}





