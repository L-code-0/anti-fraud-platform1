package com.anti.fraud.modules.risk.service.impl;

import com.anti.fraud.modules.risk.entity.UserRiskProfile;
import com.anti.fraud.modules.risk.mapper.UserRiskProfileMapper;
import com.anti.fraud.modules.risk.service.UserRiskProfileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户风险画像服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserRiskProfileServiceImpl extends ServiceImpl<UserRiskProfileMapper, UserRiskProfile> implements UserRiskProfileService {

    private final UserRiskProfileMapper userRiskProfileMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public boolean createUserRiskProfile(UserRiskProfile userRiskProfile) {
        try {
            userRiskProfile.setAssessmentTime(LocalDateTime.now());
            userRiskProfile.setStatus(1); // 已评估
            userRiskProfile.setDeleted(0);
            userRiskProfile.setCreateTime(LocalDateTime.now());
            userRiskProfile.setUpdateTime(LocalDateTime.now());
            return save(userRiskProfile);
        } catch (Exception e) {
            log.error("创建用户风险画像失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateUserRiskProfile(UserRiskProfile userRiskProfile) {
        try {
            userRiskProfile.setUpdateTime(LocalDateTime.now());
            return updateById(userRiskProfile);
        } catch (Exception e) {
            log.error("更新用户风险画像失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteUserRiskProfile(Long userId) {
        try {
            UserRiskProfile profile = userRiskProfileMapper.selectByUserId(userId);
            if (profile != null) {
                profile.setDeleted(1);
                profile.setUpdateTime(LocalDateTime.now());
                return updateById(profile);
            }
            return false;
        } catch (Exception e) {
            log.error("删除用户风险画像失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public UserRiskProfile getUserRiskProfile(Long userId) {
        try {
            return userRiskProfileMapper.selectByUserId(userId);
        } catch (Exception e) {
            log.error("获取用户风险画像失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getUserRiskProfileList(Long userId, Integer riskLevel, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<UserRiskProfile> profiles = userRiskProfileMapper.selectByUserIdWithPaging(userId, riskLevel, offset, size);
            // 计算总数
            int total = userRiskProfileMapper.selectList(null).size();

            Map<String, Object> result = new HashMap<>();
            result.put("list", profiles);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("查询用户风险画像列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public Map<String, Object> getUserRiskProfileListByRiskLevel(Integer riskLevel, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<UserRiskProfile> profiles = userRiskProfileMapper.selectByRiskLevel(riskLevel, offset, size);
            // 计算总数
            int total = userRiskProfileMapper.countByRiskLevel(riskLevel);

            Map<String, Object> result = new HashMap<>();
            result.put("list", profiles);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("根据风险等级查询用户风险画像列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public List<UserRiskProfile> getUserRiskProfileListByTimeRange(LocalDateTime startTime, LocalDateTime endTime, Long userId, Integer riskLevel) {
        try {
            return userRiskProfileMapper.selectByTimeRange(startTime, endTime, userId, riskLevel);
        } catch (Exception e) {
            log.error("根据时间范围查询用户风险画像列表失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public UserRiskProfile generateUserRiskProfile(Long userId, String username) {
        try {
            // 检查是否已存在用户风险画像
            UserRiskProfile existingProfile = userRiskProfileMapper.selectByUserId(userId);
            if (existingProfile != null) {
                // 更新现有画像
                return updateUserRiskProfileData(existingProfile);
            } else {
                // 创建新画像
                UserRiskProfile profile = new UserRiskProfile();
                profile.setUserId(userId);
                profile.setUsername(username);
                return createUserRiskProfileData(profile);
            }
        } catch (Exception e) {
            log.error("生成用户风险画像失败: {}", e.getMessage(), e);
            throw new RuntimeException("生成用户风险画像失败");
        }
    }

    @Override
    public Map<String, Object> getUserRiskProfileVisualization(Long userId) {
        try {
            UserRiskProfile profile = userRiskProfileMapper.selectByUserId(userId);
            if (profile == null) {
                throw new RuntimeException("用户风险画像不存在");
            }

            Map<String, Object> visualization = new HashMap<>();

            // 雷达图数据
            Map<String, Object> radarData = new HashMap<>();
            radarData.put("behaviorRisk", profile.getBehaviorRiskScore());
            radarData.put("deviceRisk", profile.getDeviceRiskScore());
            radarData.put("socialRisk", profile.getSocialRiskScore());
            visualization.put("radarData", radarData);

            // 风险等级分布
            Map<String, Object> riskLevelDistribution = new HashMap<>();
            riskLevelDistribution.put("lowRisk", userRiskProfileMapper.countByRiskLevel(1));
            riskLevelDistribution.put("mediumRisk", userRiskProfileMapper.countByRiskLevel(2));
            riskLevelDistribution.put("highRisk", userRiskProfileMapper.countByRiskLevel(3));
            riskLevelDistribution.put("veryHighRisk", userRiskProfileMapper.countByRiskLevel(4));
            visualization.put("riskLevelDistribution", riskLevelDistribution);

            // 风险趋势
            List<Map<String, Object>> riskTrend = getUserRiskTrend(userId, 30);
            visualization.put("riskTrend", riskTrend);

            // 风险因素
            Map<String, Object> riskFactors = getUserRiskFactorAnalysis(userId);
            visualization.put("riskFactors", riskFactors);

            // 风险标签
            List<String> riskLabels = getUserRiskLabels(userId);
            visualization.put("riskLabels", riskLabels);

            // 风险建议
            List<String> recommendations = getUserRiskRecommendations(userId);
            visualization.put("recommendations", recommendations);

            return visualization;
        } catch (Exception e) {
            log.error("获取用户风险画像可视化数据失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public List<Map<String, Object>> getUserRiskTrend(Long userId, Integer days) {
        try {
            LocalDateTime endTime = LocalDateTime.now();
            LocalDateTime startTime = endTime.minusDays(days);
            List<UserRiskProfile> profiles = userRiskProfileMapper.selectUserRiskTrend(userId, days);

            return profiles.stream().map(profile -> {
                Map<String, Object> trend = new HashMap<>();
                trend.put("date", profile.getAssessmentTime().toLocalDate());
                trend.put("overallRiskScore", profile.getOverallRiskScore());
                trend.put("behaviorRiskScore", profile.getBehaviorRiskScore());
                trend.put("deviceRiskScore", profile.getDeviceRiskScore());
                trend.put("socialRiskScore", profile.getSocialRiskScore());
                trend.put("riskLevel", profile.getRiskLevel());
                return trend;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取用户风险趋势失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<UserRiskProfile> getHighRiskUsers(Integer limit) {
        try {
            return userRiskProfileMapper.selectHighRiskUsers(limit);
        } catch (Exception e) {
            log.error("获取高风险用户列表失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public Map<String, Object> getUserRiskProfileStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();

            // 统计总用户数
            int totalUsers = userRiskProfileMapper.selectList(null).size();
            statistics.put("totalUsers", totalUsers);

            // 统计各风险等级用户数
            int lowRiskUsers = userRiskProfileMapper.countByRiskLevel(1);
            int mediumRiskUsers = userRiskProfileMapper.countByRiskLevel(2);
            int highRiskUsers = userRiskProfileMapper.countByRiskLevel(3);
            int veryHighRiskUsers = userRiskProfileMapper.countByRiskLevel(4);
            statistics.put("lowRiskUsers", lowRiskUsers);
            statistics.put("mediumRiskUsers", mediumRiskUsers);
            statistics.put("highRiskUsers", highRiskUsers);
            statistics.put("veryHighRiskUsers", veryHighRiskUsers);

            // 统计平均风险分数
            List<UserRiskProfile> allProfiles = userRiskProfileMapper.selectList(null);
            double avgOverallRiskScore = allProfiles.stream().mapToDouble(UserRiskProfile::getOverallRiskScore).average().orElse(0);
            double avgBehaviorRiskScore = allProfiles.stream().mapToDouble(UserRiskProfile::getBehaviorRiskScore).average().orElse(0);
            double avgDeviceRiskScore = allProfiles.stream().mapToDouble(UserRiskProfile::getDeviceRiskScore).average().orElse(0);
            double avgSocialRiskScore = allProfiles.stream().mapToDouble(UserRiskProfile::getSocialRiskScore).average().orElse(0);
            statistics.put("avgOverallRiskScore", avgOverallRiskScore);
            statistics.put("avgBehaviorRiskScore", avgBehaviorRiskScore);
            statistics.put("avgDeviceRiskScore", avgDeviceRiskScore);
            statistics.put("avgSocialRiskScore", avgSocialRiskScore);

            return statistics;
        } catch (Exception e) {
            log.error("获取用户风险画像统计信息失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    @Transactional
    public boolean updateUserRiskProfileStatus(Long userId, Integer status) {
        try {
            return userRiskProfileMapper.updateStatus(userId, status) > 0;
        } catch (Exception e) {
            log.error("更新用户风险画像状态失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public int batchGenerateUserRiskProfiles(List<Long> userIds) {
        try {
            int successCount = 0;
            for (Long userId : userIds) {
                try {
                    generateUserRiskProfile(userId, "user_" + userId);
                    successCount++;
                } catch (Exception e) {
                    log.error("生成用户{}风险画像失败: {}", userId, e.getMessage(), e);
                }
            }
            return successCount;
        } catch (Exception e) {
            log.error("批量生成用户风险画像失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public Map<String, Object> getUserRiskFactorAnalysis(Long userId) {
        try {
            UserRiskProfile profile = userRiskProfileMapper.selectByUserId(userId);
            if (profile == null) {
                throw new RuntimeException("用户风险画像不存在");
            }

            Map<String, Object> riskFactors = new HashMap<>();

            // 分析行为风险因素
            Map<String, Object> behaviorFactors = new HashMap<>();
            behaviorFactors.put("loginAnomalies", Math.random() * 10);
            behaviorFactors.put("transactionAnomalies", Math.random() * 10);
            behaviorFactors.put("activityAnomalies", Math.random() * 10);
            riskFactors.put("behaviorFactors", behaviorFactors);

            // 分析设备风险因素
            Map<String, Object> deviceFactors = new HashMap<>();
            deviceFactors.put("unknownDevices", Math.random() * 5);
            deviceFactors.put("suspiciousLocations", Math.random() * 5);
            deviceFactors.put("deviceFingerprintChanges", Math.random() * 5);
            riskFactors.put("deviceFactors", deviceFactors);

            // 分析社交风险因素
            Map<String, Object> socialFactors = new HashMap<>();
            socialFactors.put("suspiciousConnections", Math.random() * 5);
            socialFactors.put("abnormalInteractions", Math.random() * 5);
            socialFactors.put("riskGroupMembership", Math.random() * 5);
            riskFactors.put("socialFactors", socialFactors);

            return riskFactors;
        } catch (Exception e) {
            log.error("获取用户风险因素分析失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public List<String> getUserRiskRecommendations(Long userId) {
        try {
            UserRiskProfile profile = userRiskProfileMapper.selectByUserId(userId);
            if (profile == null) {
                throw new RuntimeException("用户风险画像不存在");
            }

            List<String> recommendations = new ArrayList<>();

            // 根据风险等级提供建议
            if (profile.getRiskLevel() >= 3) {
                recommendations.add("建议立即进行风险评估和身份验证");
                recommendations.add("建议修改密码并启用双重认证");
                recommendations.add("建议检查最近的账户活动");
            }

            // 根据行为风险提供建议
            if (profile.getBehaviorRiskScore() > 50) {
                recommendations.add("建议注意异常登录行为");
                recommendations.add("建议检查最近的交易记录");
            }

            // 根据设备风险提供建议
            if (profile.getDeviceRiskScore() > 50) {
                recommendations.add("建议检查已登录设备列表");
                recommendations.add("建议移除可疑设备");
            }

            // 根据社交风险提供建议
            if (profile.getSocialRiskScore() > 50) {
                recommendations.add("建议检查好友列表和群组");
                recommendations.add("建议注意可疑的社交互动");
            }

            return recommendations;
        } catch (Exception e) {
            log.error("获取用户风险建议失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<String> getUserRiskLabels(Long userId) {
        try {
            UserRiskProfile profile = userRiskProfileMapper.selectByUserId(userId);
            if (profile == null) {
                throw new RuntimeException("用户风险画像不存在");
            }

            List<String> labels = new ArrayList<>();

            // 根据风险等级添加标签
            switch (profile.getRiskLevel()) {
                case 1:
                    labels.add("低风险用户");
                    break;
                case 2:
                    labels.add("中风险用户");
                    break;
                case 3:
                    labels.add("高风险用户");
                    break;
                case 4:
                    labels.add("极高风险用户");
                    break;
            }

            // 根据风险分数添加标签
            if (profile.getBehaviorRiskScore() > 60) {
                labels.add("行为异常");
            }
            if (profile.getDeviceRiskScore() > 60) {
                labels.add("设备异常");
            }
            if (profile.getSocialRiskScore() > 60) {
                labels.add("社交异常");
            }

            return labels;
        } catch (Exception e) {
            log.error("获取用户风险标签失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * 创建用户风险画像数据
     * @param profile 用户风险画像
     * @return 用户风险画像
     */
    private UserRiskProfile createUserRiskProfileData(UserRiskProfile profile) {
        try {
            // 模拟生成风险分数
            double behaviorRiskScore = Math.random() * 100;
            double deviceRiskScore = Math.random() * 100;
            double socialRiskScore = Math.random() * 100;
            double overallRiskScore = (behaviorRiskScore * 0.4) + (deviceRiskScore * 0.3) + (socialRiskScore * 0.3);

            // 确定风险等级
            int riskLevel = determineRiskLevel(overallRiskScore);

            // 设置风险分数
            profile.setBehaviorRiskScore(behaviorRiskScore);
            profile.setDeviceRiskScore(deviceRiskScore);
            profile.setSocialRiskScore(socialRiskScore);
            profile.setOverallRiskScore(overallRiskScore);
            profile.setRiskLevel(riskLevel);

            // 生成风险标签
            List<String> labels = new ArrayList<>();
            labels.add("新用户");
            profile.setRiskLabels(objectMapper.writeValueAsString(labels));

            // 生成风险趋势
            Map<String, Object> trend = new HashMap<>();
            trend.put("direction", "stable");
            trend.put("change", 0);
            profile.setRiskTrend(objectMapper.writeValueAsString(trend));

            // 生成风险因素
            Map<String, Object> factors = new HashMap<>();
            factors.put("behavior", behaviorRiskScore);
            factors.put("device", deviceRiskScore);
            factors.put("social", socialRiskScore);
            profile.setRiskFactors(objectMapper.writeValueAsString(factors));

            // 生成建议措施
            List<String> recommendations = new ArrayList<>();
            if (riskLevel >= 3) {
                recommendations.add("建议加强账户安全");
            }
            profile.setRecommendations(objectMapper.writeValueAsString(recommendations));

            // 生成画像数据
            Map<String, Object> profileData = new HashMap<>();
            profileData.put("userId", profile.getUserId());
            profileData.put("username", profile.getUsername());
            profileData.put("overallRiskScore", overallRiskScore);
            profileData.put("riskLevel", riskLevel);
            profile.setProfileData(objectMapper.writeValueAsString(profileData));

            // 保存用户风险画像
            createUserRiskProfile(profile);

            return profile;
        } catch (Exception e) {
            log.error("创建用户风险画像数据失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建用户风险画像数据失败");
        }
    }

    /**
     * 更新用户风险画像数据
     * @param profile 用户风险画像
     * @return 用户风险画像
     */
    private UserRiskProfile updateUserRiskProfileData(UserRiskProfile profile) {
        try {
            // 模拟更新风险分数
            double behaviorRiskScore = Math.random() * 100;
            double deviceRiskScore = Math.random() * 100;
            double socialRiskScore = Math.random() * 100;
            double overallRiskScore = (behaviorRiskScore * 0.4) + (deviceRiskScore * 0.3) + (socialRiskScore * 0.3);

            // 确定风险等级
            int riskLevel = determineRiskLevel(overallRiskScore);

            // 更新风险分数
            profile.setBehaviorRiskScore(behaviorRiskScore);
            profile.setDeviceRiskScore(deviceRiskScore);
            profile.setSocialRiskScore(socialRiskScore);
            profile.setOverallRiskScore(overallRiskScore);
            profile.setRiskLevel(riskLevel);
            profile.setAssessmentTime(LocalDateTime.now());

            // 更新画像数据
            Map<String, Object> profileData = new HashMap<>();
            profileData.put("userId", profile.getUserId());
            profileData.put("username", profile.getUsername());
            profileData.put("overallRiskScore", overallRiskScore);
            profileData.put("riskLevel", riskLevel);
            profile.setProfileData(objectMapper.writeValueAsString(profileData));

            // 更新用户风险画像
            updateUserRiskProfile(profile);

            return profile;
        } catch (Exception e) {
            log.error("更新用户风险画像数据失败: {}", e.getMessage(), e);
            throw new RuntimeException("更新用户风险画像数据失败");
        }
    }

    /**
     * 确定风险等级
     * @param overallRiskScore 综合风险分数
     * @return 风险等级
     */
    private int determineRiskLevel(double overallRiskScore) {
        if (overallRiskScore < 30) {
            return 1; // 低风险
        } else if (overallRiskScore < 60) {
            return 2; // 中风险
        } else if (overallRiskScore < 80) {
            return 3; // 高风险
        } else {
            return 4; // 极高风险
        }
    }
}
