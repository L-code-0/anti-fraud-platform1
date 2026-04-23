package com.anti.fraud.modules.risk.service.impl;

import com.anti.fraud.modules.risk.entity.RiskAssessment;
import com.anti.fraud.modules.risk.entity.BehaviorRisk;
import com.anti.fraud.modules.risk.entity.DeviceRisk;
import com.anti.fraud.modules.risk.entity.SocialRisk;
import com.anti.fraud.modules.risk.mapper.RiskAssessmentMapper;
import com.anti.fraud.modules.risk.mapper.BehaviorRiskMapper;
import com.anti.fraud.modules.risk.mapper.DeviceRiskMapper;
import com.anti.fraud.modules.risk.mapper.SocialRiskMapper;
import com.anti.fraud.modules.risk.service.RiskAssessmentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 风险评估服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RiskAssessmentServiceImpl extends ServiceImpl<RiskAssessmentMapper, RiskAssessment> implements RiskAssessmentService {

    private final RiskAssessmentMapper riskAssessmentMapper;
    private final BehaviorRiskMapper behaviorRiskMapper;
    private final DeviceRiskMapper deviceRiskMapper;
    private final SocialRiskMapper socialRiskMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public String createRiskAssessment(RiskAssessment riskAssessment) {
        try {
            // 生成评估ID
            String assessmentId = UUID.randomUUID().toString();
            riskAssessment.setAssessmentId(assessmentId);
            riskAssessment.setAssessmentTime(LocalDateTime.now());
            riskAssessment.setStatus(2); // 评估中
            riskAssessment.setDeleted(0);
            riskAssessment.setCreateTime(LocalDateTime.now());
            riskAssessment.setUpdateTime(LocalDateTime.now());

            boolean success = save(riskAssessment);
            if (success) {
                // 异步进行多维度评估
                performMultiDimensionalAssessmentAsync(riskAssessment);
                return assessmentId;
            } else {
                throw new RuntimeException("创建风险评估失败");
            }
        } catch (Exception e) {
            log.error("创建风险评估失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建风险评估失败");
        }
    }

    @Override
    @Transactional
    public boolean updateRiskAssessment(RiskAssessment riskAssessment) {
        try {
            riskAssessment.setUpdateTime(LocalDateTime.now());
            return updateById(riskAssessment);
        } catch (Exception e) {
            log.error("更新风险评估失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteRiskAssessment(String assessmentId) {
        try {
            RiskAssessment assessment = riskAssessmentMapper.selectByAssessmentId(assessmentId);
            if (assessment != null) {
                assessment.setDeleted(1);
                assessment.setUpdateTime(LocalDateTime.now());
                return updateById(assessment);
            }
            return false;
        } catch (Exception e) {
            log.error("删除风险评估失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public RiskAssessment getRiskAssessmentByAssessmentId(String assessmentId) {
        try {
            return riskAssessmentMapper.selectByAssessmentId(assessmentId);
        } catch (Exception e) {
            log.error("获取风险评估详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getRiskAssessmentList(Long userId, Integer riskLevel, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<RiskAssessment> assessments = riskAssessmentMapper.selectByUserId(userId, riskLevel, offset, size);
            // 计算总数
            int total = riskAssessmentMapper.countByUserId(userId, riskLevel);

            Map<String, Object> result = new HashMap<>();
            result.put("list", assessments);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("查询风险评估列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public List<RiskAssessment> getRiskAssessmentListByTimeRange(LocalDateTime startTime, LocalDateTime endTime, Integer riskLevel) {
        try {
            return riskAssessmentMapper.selectByTimeRange(startTime, endTime, riskLevel);
        } catch (Exception e) {
            log.error("根据时间范围查询风险评估列表失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public Map<String, Object> multiDimensionalRiskAssessment(Long userId, String username) {
        try {
            // 计算行为风险分数
            double behaviorRiskScore = calculateBehaviorRiskScore(userId);
            
            // 计算设备风险分数
            double deviceRiskScore = calculateDeviceRiskScore(userId);
            
            // 计算社交风险分数
            double socialRiskScore = calculateSocialRiskScore(userId);
            
            // 计算综合风险分数
            double overallRiskScore = (behaviorRiskScore * 0.4) + (deviceRiskScore * 0.3) + (socialRiskScore * 0.3);
            
            // 确定风险等级
            int riskLevel = determineRiskLevel(overallRiskScore);
            
            // 创建或更新风险评估
            RiskAssessment assessment = riskAssessmentMapper.selectLatestByUserId(userId);
            if (assessment == null) {
                assessment = new RiskAssessment();
                assessment.setAssessmentId(UUID.randomUUID().toString());
                assessment.setUserId(userId);
                assessment.setUsername(username);
                assessment.setDeleted(0);
                assessment.setCreateTime(LocalDateTime.now());
            }
            
            assessment.setBehaviorRiskScore(behaviorRiskScore);
            assessment.setDeviceRiskScore(deviceRiskScore);
            assessment.setSocialRiskScore(socialRiskScore);
            assessment.setOverallRiskScore(overallRiskScore);
            assessment.setRiskLevel(riskLevel);
            assessment.setAssessmentTime(LocalDateTime.now());
            assessment.setStatus(1); // 已评估
            assessment.setUpdateTime(LocalDateTime.now());
            
            if (assessment.getId() == null) {
                riskAssessmentMapper.insert(assessment);
            } else {
                riskAssessmentMapper.updateById(assessment);
            }
            
            // 返回评估结果
            Map<String, Object> result = new HashMap<>();
            result.put("assessmentId", assessment.getAssessmentId());
            result.put("behaviorRiskScore", behaviorRiskScore);
            result.put("deviceRiskScore", deviceRiskScore);
            result.put("socialRiskScore", socialRiskScore);
            result.put("overallRiskScore", overallRiskScore);
            result.put("riskLevel", riskLevel);
            result.put("assessmentTime", assessment.getAssessmentTime());
            
            return result;
        } catch (Exception e) {
            log.error("多维度风险评估失败: {}", e.getMessage(), e);
            throw new RuntimeException("多维度风险评估失败");
        }
    }

    @Override
    @Transactional
    public String createBehaviorRisk(BehaviorRisk behaviorRisk) {
        try {
            // 生成风险ID
            String riskId = UUID.randomUUID().toString();
            behaviorRisk.setRiskId(riskId);
            behaviorRisk.setStatus(2); // 分析中
            behaviorRisk.setDeleted(0);
            behaviorRisk.setCreateTime(LocalDateTime.now());
            behaviorRisk.setUpdateTime(LocalDateTime.now());

            boolean success = behaviorRiskMapper.insert(behaviorRisk) > 0;
            if (success) {
                // 异步分析行为风险
                analyzeBehaviorRiskAsync(behaviorRisk);
                return riskId;
            } else {
                throw new RuntimeException("创建行为风险失败");
            }
        } catch (Exception e) {
            log.error("创建行为风险失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建行为风险失败");
        }
    }

    @Override
    @Transactional
    public boolean updateBehaviorRisk(BehaviorRisk behaviorRisk) {
        try {
            behaviorRisk.setUpdateTime(LocalDateTime.now());
            return behaviorRiskMapper.updateById(behaviorRisk) > 0;
        } catch (Exception e) {
            log.error("更新行为风险失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteBehaviorRisk(String riskId) {
        try {
            BehaviorRisk risk = behaviorRiskMapper.selectByRiskId(riskId);
            if (risk != null) {
                risk.setDeleted(1);
                risk.setUpdateTime(LocalDateTime.now());
                return behaviorRiskMapper.updateById(risk) > 0;
            }
            return false;
        } catch (Exception e) {
            log.error("删除行为风险失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public BehaviorRisk getBehaviorRiskByRiskId(String riskId) {
        try {
            return behaviorRiskMapper.selectByRiskId(riskId);
        } catch (Exception e) {
            log.error("获取行为风险详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getBehaviorRiskList(Long userId, Integer behaviorType, Integer riskLevel, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<BehaviorRisk> risks = behaviorRiskMapper.selectByUserId(userId, behaviorType, riskLevel, offset, size);
            // 计算总数
            int total = behaviorRiskMapper.countByBehaviorType(userId, behaviorType, riskLevel);

            Map<String, Object> result = new HashMap<>();
            result.put("list", risks);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("查询行为风险列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    @Transactional
    public Map<String, Object> analyzeBehaviorRisk(Long userId, String username, Integer behaviorType, String behaviorContent) {
        try {
            // 这里简化处理，实际应该使用更复杂的算法进行行为风险分析
            // 例如使用Isolation Forest异常检测等
            double riskScore = Math.random() * 100;
            int riskLevel = determineRiskLevel(riskScore);
            
            // 计算时间衰减因子
            double timeDecayFactor = calculateTimeDecayFactor(LocalDateTime.now(), 30);
            
            // 创建行为风险
            BehaviorRisk risk = new BehaviorRisk();
            risk.setRiskId(UUID.randomUUID().toString());
            risk.setUserId(userId);
            risk.setUsername(username);
            risk.setBehaviorType(behaviorType);
            risk.setBehaviorContent(behaviorContent);
            risk.setBehaviorTime(LocalDateTime.now());
            risk.setRiskScore(riskScore);
            risk.setRiskLevel(riskLevel);
            risk.setTimeDecayFactor(timeDecayFactor);
            risk.setStatus(1); // 已分析
            risk.setDeleted(0);
            risk.setCreateTime(LocalDateTime.now());
            risk.setUpdateTime(LocalDateTime.now());
            
            behaviorRiskMapper.insert(risk);
            
            // 返回分析结果
            Map<String, Object> result = new HashMap<>();
            result.put("riskId", risk.getRiskId());
            result.put("riskScore", riskScore);
            result.put("riskLevel", riskLevel);
            result.put("timeDecayFactor", timeDecayFactor);
            
            return result;
        } catch (Exception e) {
            log.error("分析行为风险失败: {}", e.getMessage(), e);
            throw new RuntimeException("分析行为风险失败");
        }
    }

    @Override
    @Transactional
    public String createDeviceRisk(DeviceRisk deviceRisk) {
        try {
            // 生成风险ID
            String riskId = UUID.randomUUID().toString();
            deviceRisk.setRiskId(riskId);
            deviceRisk.setStatus(2); // 分析中
            deviceRisk.setDeleted(0);
            deviceRisk.setCreateTime(LocalDateTime.now());
            deviceRisk.setUpdateTime(LocalDateTime.now());

            boolean success = deviceRiskMapper.insert(deviceRisk) > 0;
            if (success) {
                // 异步分析设备风险
                analyzeDeviceRiskAsync(deviceRisk);
                return riskId;
            } else {
                throw new RuntimeException("创建设备风险失败");
            }
        } catch (Exception e) {
            log.error("创建设备风险失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建设备风险失败");
        }
    }

    @Override
    @Transactional
    public boolean updateDeviceRisk(DeviceRisk deviceRisk) {
        try {
            deviceRisk.setUpdateTime(LocalDateTime.now());
            return deviceRiskMapper.updateById(deviceRisk) > 0;
        } catch (Exception e) {
            log.error("更新设备风险失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteDeviceRisk(String riskId) {
        try {
            DeviceRisk risk = deviceRiskMapper.selectByRiskId(riskId);
            if (risk != null) {
                risk.setDeleted(1);
                risk.setUpdateTime(LocalDateTime.now());
                return deviceRiskMapper.updateById(risk) > 0;
            }
            return false;
        } catch (Exception e) {
            log.error("删除设备风险失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public DeviceRisk getDeviceRiskByRiskId(String riskId) {
        try {
            return deviceRiskMapper.selectByRiskId(riskId);
        } catch (Exception e) {
            log.error("获取设备风险详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getDeviceRiskList(Long userId, Integer deviceType, Integer riskLevel, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<DeviceRisk> risks = deviceRiskMapper.selectByUserId(userId, deviceType, riskLevel, offset, size);
            // 计算总数
            int total = deviceRiskMapper.countByDeviceType(userId, deviceType, riskLevel);

            Map<String, Object> result = new HashMap<>();
            result.put("list", risks);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("查询设备风险列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    @Transactional
    public Map<String, Object> analyzeDeviceRisk(Long userId, String username, String deviceId, String deviceInfo, String ipAddress, String location) {
        try {
            // 这里简化处理，实际应该使用更复杂的算法进行设备风险分析
            double riskScore = Math.random() * 100;
            int riskLevel = determineRiskLevel(riskScore);
            
            // 创建设备风险
            DeviceRisk risk = new DeviceRisk();
            risk.setRiskId(UUID.randomUUID().toString());
            risk.setUserId(userId);
            risk.setUsername(username);
            risk.setDeviceId(deviceId);
            risk.setDeviceInfo(deviceInfo);
            risk.setIpAddress(ipAddress);
            risk.setLocation(location);
            risk.setRiskScore(riskScore);
            risk.setRiskLevel(riskLevel);
            risk.setLastUsedTime(LocalDateTime.now());
            risk.setStatus(1); // 已分析
            risk.setDeleted(0);
            risk.setCreateTime(LocalDateTime.now());
            risk.setUpdateTime(LocalDateTime.now());
            
            deviceRiskMapper.insert(risk);
            
            // 返回分析结果
            Map<String, Object> result = new HashMap<>();
            result.put("riskId", risk.getRiskId());
            result.put("riskScore", riskScore);
            result.put("riskLevel", riskLevel);
            
            return result;
        } catch (Exception e) {
            log.error("分析设备风险失败: {}", e.getMessage(), e);
            throw new RuntimeException("分析设备风险失败");
        }
    }

    @Override
    @Transactional
    public String createSocialRisk(SocialRisk socialRisk) {
        try {
            // 生成风险ID
            String riskId = UUID.randomUUID().toString();
            socialRisk.setRiskId(riskId);
            socialRisk.setStatus(2); // 分析中
            socialRisk.setDeleted(0);
            socialRisk.setCreateTime(LocalDateTime.now());
            socialRisk.setUpdateTime(LocalDateTime.now());

            boolean success = socialRiskMapper.insert(socialRisk) > 0;
            if (success) {
                // 异步分析社交风险
                analyzeSocialRiskAsync(socialRisk);
                return riskId;
            } else {
                throw new RuntimeException("创建社交风险失败");
            }
        } catch (Exception e) {
            log.error("创建社交风险失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建社交风险失败");
        }
    }

    @Override
    @Transactional
    public boolean updateSocialRisk(SocialRisk socialRisk) {
        try {
            socialRisk.setUpdateTime(LocalDateTime.now());
            return socialRiskMapper.updateById(socialRisk) > 0;
        } catch (Exception e) {
            log.error("更新社交风险失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteSocialRisk(String riskId) {
        try {
            SocialRisk risk = socialRiskMapper.selectByRiskId(riskId);
            if (risk != null) {
                risk.setDeleted(1);
                risk.setUpdateTime(LocalDateTime.now());
                return socialRiskMapper.updateById(risk) > 0;
            }
            return false;
        } catch (Exception e) {
            log.error("删除社交风险失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public SocialRisk getSocialRiskByRiskId(String riskId) {
        try {
            return socialRiskMapper.selectByRiskId(riskId);
        } catch (Exception e) {
            log.error("获取社交风险详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getSocialRiskList(Long userId, Integer socialType, Integer riskLevel, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<SocialRisk> risks = socialRiskMapper.selectByUserId(userId, socialType, riskLevel, offset, size);
            // 计算总数
            int total = socialRiskMapper.countBySocialType(userId, socialType, riskLevel);

            Map<String, Object> result = new HashMap<>();
            result.put("list", risks);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("查询社交风险列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    @Transactional
    public Map<String, Object> analyzeSocialRisk(Long userId, String username, String targetUserId, String targetUsername, Integer socialType, String socialContent) {
        try {
            // 这里简化处理，实际应该使用更复杂的算法进行社交风险分析
            double riskScore = Math.random() * 100;
            int riskLevel = determineRiskLevel(riskScore);
            
            // 创建社交风险
            SocialRisk risk = new SocialRisk();
            risk.setRiskId(UUID.randomUUID().toString());
            risk.setUserId(userId);
            risk.setUsername(username);
            risk.setTargetUserId(targetUserId);
            risk.setTargetUsername(targetUsername);
            risk.setSocialType(socialType);
            risk.setSocialContent(socialContent);
            risk.setSocialTime(LocalDateTime.now());
            risk.setRiskScore(riskScore);
            risk.setRiskLevel(riskLevel);
            risk.setStatus(1); // 已分析
            risk.setDeleted(0);
            risk.setCreateTime(LocalDateTime.now());
            risk.setUpdateTime(LocalDateTime.now());
            
            socialRiskMapper.insert(risk);
            
            // 返回分析结果
            Map<String, Object> result = new HashMap<>();
            result.put("riskId", risk.getRiskId());
            result.put("riskScore", riskScore);
            result.put("riskLevel", riskLevel);
            
            return result;
        } catch (Exception e) {
            log.error("分析社交风险失败: {}", e.getMessage(), e);
            throw new RuntimeException("分析社交风险失败");
        }
    }

    @Override
    @Transactional
    public int updateTimeDecayFactor(Long userId, int days) {
        try {
            // 获取用户的所有行为风险
            List<BehaviorRisk> behaviorRisks = behaviorRiskMapper.selectByUserId(userId, null, null, 0, 1000);
            
            int updatedCount = 0;
            for (BehaviorRisk risk : behaviorRisks) {
                double timeDecayFactor = calculateTimeDecayFactor(risk.getBehaviorTime(), days);
                behaviorRiskMapper.updateRiskScore(risk.getId(), risk.getRiskScore() * timeDecayFactor, risk.getRiskLevel(), timeDecayFactor);
                updatedCount++;
            }
            
            return updatedCount;
        } catch (Exception e) {
            log.error("更新时间衰减因子失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public Map<String, Object> getRiskAssessmentStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 统计总风险评估数量
            int total = riskAssessmentMapper.selectList(null).size();
            statistics.put("total", total);
            
            // 统计各风险等级数量
            int lowRisk = riskAssessmentMapper.countByRiskLevel(1);
            int mediumRisk = riskAssessmentMapper.countByRiskLevel(2);
            int highRisk = riskAssessmentMapper.countByRiskLevel(3);
            int veryHighRisk = riskAssessmentMapper.countByRiskLevel(4);
            statistics.put("lowRisk", lowRisk);
            statistics.put("mediumRisk", mediumRisk);
            statistics.put("highRisk", highRisk);
            statistics.put("veryHighRisk", veryHighRisk);
            
            // 统计行为风险数量
            int totalBehaviorRisks = behaviorRiskMapper.selectList(null).size();
            statistics.put("totalBehaviorRisks", totalBehaviorRisks);
            
            // 统计设备风险数量
            int totalDeviceRisks = deviceRiskMapper.selectList(null).size();
            statistics.put("totalDeviceRisks", totalDeviceRisks);
            
            // 统计社交风险数量
            int totalSocialRisks = socialRiskMapper.selectList(null).size();
            statistics.put("totalSocialRisks", totalSocialRisks);
            
            return statistics;
        } catch (Exception e) {
            log.error("获取风险评估统计信息失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    @Transactional
    public int batchCreateBehaviorRisks(List<BehaviorRisk> behaviorRisks) {
        try {
            for (BehaviorRisk risk : behaviorRisks) {
                risk.setRiskId(UUID.randomUUID().toString());
                risk.setStatus(2); // 分析中
                risk.setDeleted(0);
                risk.setCreateTime(LocalDateTime.now());
                risk.setUpdateTime(LocalDateTime.now());
            }
            int count = behaviorRiskMapper.batchInsert(behaviorRisks);
            // 异步分析行为风险
            for (BehaviorRisk risk : behaviorRisks) {
                analyzeBehaviorRiskAsync(risk);
            }
            return count;
        } catch (Exception e) {
            log.error("批量创建行为风险失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    @Transactional
    public int batchCreateDeviceRisks(List<DeviceRisk> deviceRisks) {
        try {
            for (DeviceRisk risk : deviceRisks) {
                risk.setRiskId(UUID.randomUUID().toString());
                risk.setStatus(2); // 分析中
                risk.setDeleted(0);
                risk.setCreateTime(LocalDateTime.now());
                risk.setUpdateTime(LocalDateTime.now());
            }
            int count = deviceRiskMapper.batchInsert(deviceRisks);
            // 异步分析设备风险
            for (DeviceRisk risk : deviceRisks) {
                analyzeDeviceRiskAsync(risk);
            }
            return count;
        } catch (Exception e) {
            log.error("批量创建设备风险失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    @Transactional
    public int batchCreateSocialRisks(List<SocialRisk> socialRisks) {
        try {
            for (SocialRisk risk : socialRisks) {
                risk.setRiskId(UUID.randomUUID().toString());
                risk.setStatus(2); // 分析中
                risk.setDeleted(0);
                risk.setCreateTime(LocalDateTime.now());
                risk.setUpdateTime(LocalDateTime.now());
            }
            int count = socialRiskMapper.batchInsert(socialRisks);
            // 异步分析社交风险
            for (SocialRisk risk : socialRisks) {
                analyzeSocialRiskAsync(risk);
            }
            return count;
        } catch (Exception e) {
            log.error("批量创建社交风险失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 异步进行多维度评估
     * @param riskAssessment 风险评估信息
     */
    private void performMultiDimensionalAssessmentAsync(RiskAssessment riskAssessment) {
        // 这里简化处理，实际应该使用线程池或消息队列进行异步处理
        new Thread(() -> {
            try {
                multiDimensionalRiskAssessment(riskAssessment.getUserId(), riskAssessment.getUsername());
            } catch (Exception e) {
                log.error("异步多维度评估失败: {}", e.getMessage(), e);
                riskAssessmentMapper.updateStatus(riskAssessment.getId(), 3); // 评估失败
            }
        }).start();
    }

    /**
     * 异步分析行为风险
     * @param behaviorRisk 行为风险信息
     */
    private void analyzeBehaviorRiskAsync(BehaviorRisk behaviorRisk) {
        // 这里简化处理，实际应该使用线程池或消息队列进行异步处理
        new Thread(() -> {
            try {
                analyzeBehaviorRisk(behaviorRisk.getUserId(), behaviorRisk.getUsername(), behaviorRisk.getBehaviorType(), behaviorRisk.getBehaviorContent());
            } catch (Exception e) {
                log.error("异步分析行为风险失败: {}", e.getMessage(), e);
                behaviorRiskMapper.updateStatus(behaviorRisk.getId(), 3); // 分析失败
            }
        }).start();
    }

    /**
     * 异步分析设备风险
     * @param deviceRisk 设备风险信息
     */
    private void analyzeDeviceRiskAsync(DeviceRisk deviceRisk) {
        // 这里简化处理，实际应该使用线程池或消息队列进行异步处理
        new Thread(() -> {
            try {
                analyzeDeviceRisk(deviceRisk.getUserId(), deviceRisk.getUsername(), deviceRisk.getDeviceId(), deviceRisk.getDeviceInfo(), deviceRisk.getIpAddress(), deviceRisk.getLocation());
            } catch (Exception e) {
                log.error("异步分析设备风险失败: {}", e.getMessage(), e);
                deviceRiskMapper.updateStatus(deviceRisk.getId(), 3); // 分析失败
            }
        }).start();
    }

    /**
     * 异步分析社交风险
     * @param socialRisk 社交风险信息
     */
    private void analyzeSocialRiskAsync(SocialRisk socialRisk) {
        // 这里简化处理，实际应该使用线程池或消息队列进行异步处理
        new Thread(() -> {
            try {
                analyzeSocialRisk(socialRisk.getUserId(), socialRisk.getUsername(), socialRisk.getTargetUserId(), socialRisk.getTargetUsername(), socialRisk.getSocialType(), socialRisk.getSocialContent());
            } catch (Exception e) {
                log.error("异步分析社交风险失败: {}", e.getMessage(), e);
                socialRiskMapper.updateStatus(socialRisk.getId(), 3); // 分析失败
            }
        }).start();
    }

    /**
     * 计算行为风险分数
     * @param userId 用户ID
     * @return 行为风险分数
     */
    private double calculateBehaviorRiskScore(Long userId) {
        // 这里简化处理，实际应该使用更复杂的算法
        List<BehaviorRisk> behaviorRisks = behaviorRiskMapper.selectByUserId(userId, null, null, 0, 100);
        
        if (behaviorRisks.isEmpty()) {
            return 0.0;
        }
        
        double totalRiskScore = 0.0;
        for (BehaviorRisk risk : behaviorRisks) {
            totalRiskScore += risk.getRiskScore() * risk.getTimeDecayFactor();
        }
        
        return totalRiskScore / behaviorRisks.size();
    }

    /**
     * 计算设备风险分数
     * @param userId 用户ID
     * @return 设备风险分数
     */
    private double calculateDeviceRiskScore(Long userId) {
        // 这里简化处理，实际应该使用更复杂的算法
        List<DeviceRisk> deviceRisks = deviceRiskMapper.selectByUserId(userId, null, null, 0, 100);
        
        if (deviceRisks.isEmpty()) {
            return 0.0;
        }
        
        double totalRiskScore = 0.0;
        for (DeviceRisk risk : deviceRisks) {
            totalRiskScore += risk.getRiskScore();
        }
        
        return totalRiskScore / deviceRisks.size();
    }

    /**
     * 计算社交风险分数
     * @param userId 用户ID
     * @return 社交风险分数
     */
    private double calculateSocialRiskScore(Long userId) {
        // 这里简化处理，实际应该使用更复杂的算法
        List<SocialRisk> socialRisks = socialRiskMapper.selectByUserId(userId, null, null, 0, 100);
        
        if (socialRisks.isEmpty()) {
            return 0.0;
        }
        
        double totalRiskScore = 0.0;
        for (SocialRisk risk : socialRisks) {
            totalRiskScore += risk.getRiskScore();
        }
        
        return totalRiskScore / socialRisks.size();
    }

    /**
     * 确定风险等级
     * @param riskScore 风险分数
     * @return 风险等级
     */
    private int determineRiskLevel(double riskScore) {
        if (riskScore < 25) {
            return 1; // 低风险
        } else if (riskScore < 50) {
            return 2; // 中风险
        } else if (riskScore < 75) {
            return 3; // 高风险
        } else {
            return 4; // 极高风险
        }
    }

    /**
     * 计算时间衰减因子
     * @param behaviorTime 行为时间
     * @param days 天数
     * @return 时间衰减因子
     */
    private double calculateTimeDecayFactor(LocalDateTime behaviorTime, int days) {
        LocalDateTime now = LocalDateTime.now();
        long daysBetween = ChronoUnit.DAYS.between(behaviorTime, now);
        
        if (daysBetween >= days) {
            return 0.0;
        }
        
        // 线性衰减
        return 1.0 - ((double) daysBetween / days);
    }
}
