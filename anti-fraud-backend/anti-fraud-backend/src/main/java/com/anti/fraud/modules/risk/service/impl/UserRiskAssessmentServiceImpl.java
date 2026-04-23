package com.anti.fraud.modules.risk.service.impl;

import com.anti.fraud.modules.exam.entity.ExamMonitor;
import com.anti.fraud.modules.exam.mapper.ExamMonitorMapper;
import com.anti.fraud.modules.risk.entity.UserRiskAssessment;
import com.anti.fraud.modules.risk.mapper.UserRiskAssessmentMapper;
import com.anti.fraud.modules.risk.service.UserRiskAssessmentService;
import com.anti.fraud.modules.risk.vo.RiskLevelStatsVO;
import com.anti.fraud.modules.user.entity.LoginDevice;
import com.anti.fraud.modules.user.mapper.LoginDeviceMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户风险评估服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserRiskAssessmentServiceImpl implements UserRiskAssessmentService {

    private final UserRiskAssessmentMapper userRiskAssessmentMapper;
    private final LoginDeviceMapper loginDeviceMapper;
    private final ExamMonitorMapper examMonitorMapper;

    @Override
    @Transactional
    public UserRiskAssessment assessUserRisk(Long userId) {
        try {
            // 计算风险评分
            int riskScore = calculateRiskScore(userId);
            
            // 确定风险等级
            String riskLevel = determineRiskLevel(riskScore);
            
            // 收集风险因素
            List<String> riskFactors = collectRiskFactors(userId);
            
            // 生成评估结果和建议
            String assessmentResult = generateAssessmentResult(riskScore, riskLevel, riskFactors);
            String recommendations = generateRecommendations(riskLevel, riskFactors);
            
            // 创建风险评估记录
            UserRiskAssessment assessment = new UserRiskAssessment();
            assessment.setUserId(userId);
            assessment.setUsername("用户" + userId); // 实际项目中应该从用户服务获取
            assessment.setRiskScore(riskScore);
            assessment.setRiskLevel(riskLevel);
            assessment.setRiskFactors(String.join(", ", riskFactors));
            assessment.setAssessmentResult(assessmentResult);
            assessment.setRecommendations(recommendations);
            assessment.setStatus(riskScore >= 70 ? 2 : 1); // 风险评分>=70为异常
            assessment.setCreateTime(LocalDateTime.now());
            assessment.setUpdateTime(LocalDateTime.now());
            
            // 保存评估记录
            userRiskAssessmentMapper.insert(assessment);
            
            log.info("用户风险评估完成: userId={}, riskScore={}, riskLevel={}", userId, riskScore, riskLevel);
            return assessment;
        } catch (Exception e) {
            log.error("评估用户风险失败: {}", e.getMessage(), e);
            throw new RuntimeException("评估用户风险失败", e);
        }
    }

    @Override
    public List<UserRiskAssessment> getUserRiskAssessments(Long userId, int page, int size) {
        try {
            Page<UserRiskAssessment> pageParam = new Page<>(page, size);
            LambdaQueryWrapper<UserRiskAssessment> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserRiskAssessment::getUserId, userId)
                    .orderByDesc(UserRiskAssessment::getCreateTime);
            return userRiskAssessmentMapper.selectPage(pageParam, queryWrapper).getRecords();
        } catch (Exception e) {
            log.error("获取用户风险评估记录失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public UserRiskAssessment getLatestUserRiskAssessment(Long userId) {
        try {
            return userRiskAssessmentMapper.selectLatestByUserId(userId);
        } catch (Exception e) {
            log.error("获取用户最新风险评估记录失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<RiskLevelStatsVO> getRiskLevelStats() {
        try {
            List<RiskLevelStatsVO> stats = userRiskAssessmentMapper.countByRiskLevel();
            // 计算百分比
            int total = stats.stream().mapToInt(RiskLevelStatsVO::getCount).sum();
            if (total > 0) {
                stats.forEach(stat -> {
                    stat.setPercentage((double) stat.getCount() / total * 100);
                });
            }
            return stats;
        } catch (Exception e) {
            log.error("获取风险等级统计失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<UserRiskAssessment> getHighRiskUsers(int limit) {
        try {
            return userRiskAssessmentMapper.selectByRiskLevel("高", limit);
        } catch (Exception e) {
            log.error("获取高风险用户列表失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public Map<Long, UserRiskAssessment> batchAssessUserRisk(List<Long> userIds) {
        Map<Long, UserRiskAssessment> results = new HashMap<>();
        for (Long userId : userIds) {
            try {
                UserRiskAssessment assessment = assessUserRisk(userId);
                results.put(userId, assessment);
            } catch (Exception e) {
                log.error("批量评估用户风险失败: userId={}, error={}", userId, e.getMessage());
            }
        }
        return results;
    }

    @Override
    @Transactional
    public boolean updateUserRiskAssessment(UserRiskAssessment assessment) {
        try {
            assessment.setUpdateTime(LocalDateTime.now());
            int result = userRiskAssessmentMapper.updateById(assessment);
            log.info("更新用户风险评估: id={}, userId={}", assessment.getId(), assessment.getUserId());
            return result > 0;
        } catch (Exception e) {
            log.error("更新用户风险评估失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteUserRiskAssessment(Long id) {
        try {
            int result = userRiskAssessmentMapper.deleteById(id);
            log.info("删除用户风险评估: id={}", id);
            return result > 0;
        } catch (Exception e) {
            log.error("删除用户风险评估失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 计算用户风险评分
     * @param userId 用户ID
     * @return 风险评分 0-100
     */
    private int calculateRiskScore(Long userId) {
        int riskScore = 0;
        
        // 1. 登录设备风险
        riskScore += calculateDeviceRisk(userId);
        
        // 2. 考试作弊风险
        riskScore += calculateExamRisk(userId);
        
        // 3. 其他风险因素（可根据实际需求添加）
        
        // 限制评分范围在0-100之间
        return Math.min(100, Math.max(0, riskScore));
    }

    /**
     * 计算登录设备风险
     * @param userId 用户ID
     * @return 风险评分 0-50
     */
    private int calculateDeviceRisk(Long userId) {
        int deviceRisk = 0;
        
        try {
            List<LoginDevice> devices = loginDeviceMapper.selectByUserId(userId);
            
            // 设备数量风险
            if (devices.size() > 3) {
                deviceRisk += 15;
            } else if (devices.size() > 1) {
                deviceRisk += 5;
            }
            
            // 设备活跃状态风险
            long inactiveDevices = devices.stream().filter(device -> !device.getIsActive()).count();
            if (inactiveDevices > 0) {
                deviceRisk += 10;
            }
            
            // 不可信设备风险
            long untrustedDevices = devices.stream().filter(device -> !device.getIsTrusted()).count();
            if (untrustedDevices > 0) {
                deviceRisk += 15;
            }
            
        } catch (Exception e) {
            log.error("计算设备风险失败: {}", e.getMessage());
        }
        
        return deviceRisk;
    }

    /**
     * 计算考试作弊风险
     * @param userId 用户ID
     * @return 风险评分 0-50
     */
    private int calculateExamRisk(Long userId) {
        int examRisk = 0;
        
        try {
            LambdaQueryWrapper<ExamMonitor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ExamMonitor::getUserId, userId)
                    .eq(ExamMonitor::getMonitorType, "cheating_detection")
                    .orderByDesc(ExamMonitor::getCreateTime)
                    .last("LIMIT 5"); // 最近5次考试监控
            
            List<ExamMonitor> monitors = examMonitorMapper.selectList(queryWrapper);
            
            for (ExamMonitor monitor : monitors) {
                if (monitor.getRiskLevel() != null) {
                    examRisk += monitor.getRiskLevel() * 2;
                }
            }
            
        } catch (Exception e) {
            log.error("计算考试风险失败: {}", e.getMessage());
        }
        
        return Math.min(50, examRisk);
    }

    /**
     * 确定风险等级
     * @param riskScore 风险评分
     * @return 风险等级
     */
    private String determineRiskLevel(int riskScore) {
        if (riskScore >= 80) {
            return "极高";
        } else if (riskScore >= 60) {
            return "高";
        } else if (riskScore >= 40) {
            return "中";
        } else {
            return "低";
        }
    }

    /**
     * 收集风险因素
     * @param userId 用户ID
     * @return 风险因素列表
     */
    private List<String> collectRiskFactors(Long userId) {
        List<String> riskFactors = new ArrayList<>();
        
        // 设备风险因素
        try {
            List<LoginDevice> devices = loginDeviceMapper.selectByUserId(userId);
            if (devices.size() > 3) {
                riskFactors.add("登录设备过多");
            }
            long untrustedDevices = devices.stream().filter(device -> !device.getIsTrusted()).count();
            if (untrustedDevices > 0) {
                riskFactors.add("存在不可信设备");
            }
        } catch (Exception e) {
            log.error("收集设备风险因素失败: {}", e.getMessage());
        }
        
        // 考试风险因素
        try {
            LambdaQueryWrapper<ExamMonitor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ExamMonitor::getUserId, userId)
                    .eq(ExamMonitor::getMonitorType, "cheating_detection")
                    .orderByDesc(ExamMonitor::getCreateTime)
                    .last("LIMIT 1"); // 最近一次考试监控
            
            ExamMonitor monitor = examMonitorMapper.selectOne(queryWrapper);
            if (monitor != null && monitor.getRiskDescription() != null) {
                String[] risks = monitor.getRiskDescription().split(", ");
                for (String risk : risks) {
                    riskFactors.add("考试: " + risk);
                }
            }
        } catch (Exception e) {
            log.error("收集考试风险因素失败: {}", e.getMessage());
        }
        
        return riskFactors;
    }

    /**
     * 生成评估结果
     * @param riskScore 风险评分
     * @param riskLevel 风险等级
     * @param riskFactors 风险因素
     * @return 评估结果
     */
    private String generateAssessmentResult(int riskScore, String riskLevel, List<String> riskFactors) {
        StringBuilder result = new StringBuilder();
        result.append("用户风险评分为").append(riskScore).append("，风险等级为").append(riskLevel).append("。");
        if (!riskFactors.isEmpty()) {
            result.append("主要风险因素包括：").append(String.join("、", riskFactors)).append("。");
        }
        return result.toString();
    }

    /**
     * 生成建议措施
     * @param riskLevel 风险等级
     * @param riskFactors 风险因素
     * @return 建议措施
     */
    private String generateRecommendations(String riskLevel, List<String> riskFactors) {
        StringBuilder recommendations = new StringBuilder();
        
        if ("极高".equals(riskLevel)) {
            recommendations.append("1. 立即审核用户账号活动\n");
            recommendations.append("2. 暂时限制用户权限\n");
            recommendations.append("3. 联系用户进行身份验证\n");
        } else if ("高".equals(riskLevel)) {
            recommendations.append("1. 加强用户行为监控\n");
            recommendations.append("2. 要求用户重新验证身份\n");
            recommendations.append("3. 检查异常登录设备\n");
        } else if ("中".equals(riskLevel)) {
            recommendations.append("1. 提醒用户注意账号安全\n");
            recommendations.append("2. 建议用户更新密码\n");
            recommendations.append("3. 监控用户后续行为\n");
        } else {
            recommendations.append("1. 保持常规安全措施\n");
            recommendations.append("2. 定期提醒用户更新密码\n");
        }
        
        // 根据具体风险因素添加建议
        if (riskFactors.stream().anyMatch(factor -> factor.contains("设备"))) {
            recommendations.append("4. 建议用户只在可信设备上登录\n");
        }
        if (riskFactors.stream().anyMatch(factor -> factor.contains("考试"))) {
            recommendations.append("4. 加强考试监控措施\n");
        }
        
        return recommendations.toString();
    }
}
