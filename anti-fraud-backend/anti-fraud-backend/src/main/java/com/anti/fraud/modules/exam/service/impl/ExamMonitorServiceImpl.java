package com.anti.fraud.modules.exam.service.impl;

import com.anti.fraud.modules.exam.entity.ExamMonitor;
import com.anti.fraud.modules.exam.mapper.ExamMonitorMapper;
import com.anti.fraud.modules.exam.service.ExamMonitorService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExamMonitorServiceImpl implements ExamMonitorService {

    private final ExamMonitorMapper examMonitorMapper;

    @Override
    public boolean recordMonitorData(ExamMonitor monitor) {
        try {
            examMonitorMapper.insert(monitor);
            log.info("记录考试监控数据成功: userId={}, examId={}, monitorType={}", 
                    monitor.getUserId(), monitor.getExamId(), monitor.getMonitorType());
            return true;
        } catch (Exception e) {
            log.error("记录考试监控数据失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Map<String, Object> detectCheating(Long userId, Long examId, String monitorData) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 解析监控数据
            Map<String, Object> data = parseMonitorData(monitorData);
            
            // 检测作弊行为
            int riskLevel = 0;
            List<String> riskDescriptions = new ArrayList<>();
            
            // 检测切屏行为
            if (data.containsKey("screenChanges")) {
                int screenChanges = (int) data.get("screenChanges");
                if (screenChanges > 5) {
                    riskLevel += 2;
                    riskDescriptions.add("频繁切屏");
                } else if (screenChanges > 10) {
                    riskLevel += 3;
                    riskDescriptions.add("严重切屏");
                }
            }
            
            // 检测鼠标移动异常
            if (data.containsKey("mouseMovements")) {
                int mouseMovements = (int) data.get("mouseMovements");
                if (mouseMovements < 10) {
                    riskLevel += 1;
                    riskDescriptions.add("鼠标移动异常");
                } else if (mouseMovements < 5) {
                    riskLevel += 2;
                    riskDescriptions.add("鼠标几乎未移动");
                }
            }
            
            // 检测答题速度异常
            if (data.containsKey("answerSpeed")) {
                double answerSpeed = (double) data.get("answerSpeed");
                if (answerSpeed < 2) {
                    riskLevel += 2;
                    riskDescriptions.add("答题速度过快");
                } else if (answerSpeed < 1) {
                    riskLevel += 3;
                    riskDescriptions.add("答题速度异常快");
                } else if (answerSpeed > 60) {
                    riskLevel += 1;
                    riskDescriptions.add("答题速度过慢");
                }
            }
            
            // 检测多设备登录
            if (data.containsKey("deviceCount")) {
                int deviceCount = (int) data.get("deviceCount");
                if (deviceCount > 1) {
                    riskLevel += 3;
                    riskDescriptions.add("多设备登录");
                }
            }
            
            // 检测浏览器标签页切换
            if (data.containsKey("tabSwitches")) {
                int tabSwitches = (int) data.get("tabSwitches");
                if (tabSwitches > 10) {
                    riskLevel += 2;
                    riskDescriptions.add("频繁切换标签页");
                } else if (tabSwitches > 20) {
                    riskLevel += 3;
                    riskDescriptions.add("严重切换标签页");
                }
            }
            
            // 检测复制粘贴行为
            if (data.containsKey("copyPasteCount")) {
                int copyPasteCount = (int) data.get("copyPasteCount");
                if (copyPasteCount > 5) {
                    riskLevel += 2;
                    riskDescriptions.add("频繁复制粘贴");
                }
            }
            
            // 检测窗口大小变化
            if (data.containsKey("windowResizeCount")) {
                int windowResizeCount = (int) data.get("windowResizeCount");
                if (windowResizeCount > 10) {
                    riskLevel += 1;
                    riskDescriptions.add("频繁调整窗口大小");
                }
            }
            
            // 检测键盘输入异常
            if (data.containsKey("keyboardInput")) {
                Map<String, Object> keyboardData = (Map<String, Object>) data.get("keyboardInput");
                if (keyboardData != null) {
                    if (keyboardData.containsKey("typingSpeed") && keyboardData.get("typingSpeed") instanceof Double) {
                        double typingSpeed = (double) keyboardData.get("typingSpeed");
                        if (typingSpeed > 200) {
                            riskLevel += 2;
                            riskDescriptions.add("打字速度异常");
                        }
                    }
                }
            }
            
            // 生成检测结果
            result.put("riskLevel", riskLevel);
            result.put("riskDescriptions", riskDescriptions);
            result.put("isCheating", riskLevel >= 3);
            result.put("suggestion", riskLevel >= 3 ? "疑似作弊，建议人工审核" : "正常答题");
            
            // 记录检测结果
            ExamMonitor monitor = new ExamMonitor();
            monitor.setUserId(userId);
            monitor.setExamId(examId);
            monitor.setMonitorType("cheating_detection");
            monitor.setMonitorData(monitorData);
            monitor.setRiskLevel(riskLevel);
            monitor.setRiskDescription(String.join(", ", riskDescriptions));
            monitor.setStatus(riskLevel >= 3 ? 2 : 1); // 1: 正常, 2: 异常
            examMonitorMapper.insert(monitor);
            
            log.info("检测作弊行为: userId={}, examId={}, riskLevel={}", userId, examId, riskLevel);
            return result;
        } catch (Exception e) {
            log.error("检测作弊行为失败: {}", e.getMessage(), e);
            result.put("riskLevel", 0);
            result.put("riskDescriptions", Collections.emptyList());
            result.put("isCheating", false);
            result.put("suggestion", "检测失败，默认正常");
            return result;
        }
    }

    @Override
    public List<ExamMonitor> getMonitorRecords(Long userId, Long examId) {
        try {
            LambdaQueryWrapper<ExamMonitor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ExamMonitor::getUserId, userId)
                    .eq(ExamMonitor::getExamId, examId)
                    .orderByDesc(ExamMonitor::getCreateTime);
            return examMonitorMapper.selectList(queryWrapper);
        } catch (Exception e) {
            log.error("获取用户考试监控记录失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public Map<String, Object> getCheatingRiskReport(Long examId) {
        try {
            LambdaQueryWrapper<ExamMonitor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ExamMonitor::getExamId, examId)
                    .eq(ExamMonitor::getMonitorType, "cheating_detection");
            List<ExamMonitor> monitors = examMonitorMapper.selectList(queryWrapper);
            
            // 统计风险等级
            int totalUsers = monitors.size();
            int highRiskCount = 0;
            int mediumRiskCount = 0;
            int lowRiskCount = 0;
            
            for (ExamMonitor monitor : monitors) {
                int riskLevel = monitor.getRiskLevel();
                if (riskLevel >= 3) {
                    highRiskCount++;
                } else if (riskLevel >= 1) {
                    mediumRiskCount++;
                } else {
                    lowRiskCount++;
                }
            }
            
            Map<String, Object> report = new HashMap<>();
            report.put("totalUsers", totalUsers);
            report.put("highRiskCount", highRiskCount);
            report.put("mediumRiskCount", mediumRiskCount);
            report.put("lowRiskCount", lowRiskCount);
            report.put("highRiskRate", totalUsers > 0 ? (double) highRiskCount / totalUsers * 100 : 0);
            report.put("mediumRiskRate", totalUsers > 0 ? (double) mediumRiskCount / totalUsers * 100 : 0);
            report.put("lowRiskRate", totalUsers > 0 ? (double) lowRiskCount / totalUsers * 100 : 0);
            
            // 风险类型分布
            Map<String, Integer> riskTypeDistribution = new HashMap<>();
            for (ExamMonitor monitor : monitors) {
                if (monitor.getRiskDescription() != null) {
                    String[] risks = monitor.getRiskDescription().split(", ");
                    for (String risk : risks) {
                        riskTypeDistribution.put(risk, riskTypeDistribution.getOrDefault(risk, 0) + 1);
                    }
                }
            }
            report.put("riskTypeDistribution", riskTypeDistribution);
            
            log.info("生成考试作弊风险报告: examId={}, totalUsers={}, highRiskCount={}", 
                    examId, totalUsers, highRiskCount);
            return report;
        } catch (Exception e) {
            log.error("获取考试作弊风险报告失败: {}", e.getMessage(), e);
            return Collections.emptyMap();
        }
    }

    @Override
    public boolean handleCheating(Long monitorId, String action) {
        try {
            ExamMonitor monitor = examMonitorMapper.selectById(monitorId);
            if (monitor != null) {
                // 更新处理状态
                if ("block".equals(action)) {
                    monitor.setStatus(3); // 已封禁
                } else if ("warning".equals(action)) {
                    monitor.setStatus(4); // 已警告
                } else if ("ignore".equals(action)) {
                    monitor.setStatus(5); // 忽略
                }
                examMonitorMapper.updateById(monitor);
                log.info("处理作弊行为: monitorId={}, action={}", monitorId, action);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("处理作弊行为失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Map<String, Object> generateAntiCheatConfig(Long examId) {
        Map<String, Object> config = new HashMap<>();
        config.put("screenMonitoring", true);
        config.put("mouseTracking", true);
        config.put("tabSwitchDetection", true);
        config.put("deviceFingerprinting", true);
        config.put("answerSpeedMonitoring", true);
        config.put("maxScreenChanges", 5);
        config.put("minMouseMovements", 10);
        config.put("minAnswerTime", 2); // 秒/题
        config.put("allowMultipleDevices", false);
        return config;
    }

    /**
     * 解析监控数据
     */
    private Map<String, Object> parseMonitorData(String monitorData) {
        try {
            // 简单的JSON解析（实际项目中应该使用JSON库）
            Map<String, Object> data = new HashMap<>();
            // 模拟解析结果
            data.put("screenChanges", 3);
            data.put("mouseMovements", 50);
            data.put("answerSpeed", 3.5);
            data.put("deviceCount", 1);
            return data;
        } catch (Exception e) {
            log.error("解析监控数据失败: {}", e.getMessage(), e);
            return Collections.emptyMap();
        }
    }
}
