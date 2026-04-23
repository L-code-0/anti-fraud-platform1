package com.anti.fraud.modules.risk.service.impl;

import com.anti.fraud.modules.risk.entity.RiskProfile;
import com.anti.fraud.modules.risk.mapper.RiskProfileMapper;
import com.anti.fraud.modules.risk.service.RiskProfileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
 * 风险画像服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RiskProfileServiceImpl extends ServiceImpl<RiskProfileMapper, RiskProfile> implements RiskProfileService {

    private final RiskProfileMapper riskProfileMapper;

    @Override
    @Transactional
    public boolean createRiskProfile(RiskProfile riskProfile) {
        try {
            riskProfile.setStatus(1);
            riskProfile.setDeleted(0);
            riskProfile.setCreateTime(LocalDateTime.now());
            riskProfile.setUpdateTime(LocalDateTime.now());
            riskProfile.setLastUpdateTime(LocalDateTime.now());
            return save(riskProfile);
        } catch (Exception e) {
            log.error("创建风险画像失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateRiskProfile(RiskProfile riskProfile) {
        try {
            riskProfile.setUpdateTime(LocalDateTime.now());
            riskProfile.setLastUpdateTime(LocalDateTime.now());
            return updateById(riskProfile);
        } catch (Exception e) {
            log.error("更新风险画像失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteRiskProfile(Long id) {
        try {
            RiskProfile riskProfile = getById(id);
            if (riskProfile != null) {
                riskProfile.setDeleted(1);
                riskProfile.setUpdateTime(LocalDateTime.now());
                return updateById(riskProfile);
            }
            return false;
        } catch (Exception e) {
            log.error("删除风险画像失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public RiskProfile getRiskProfileById(Long id) {
        try {
            return getById(id);
        } catch (Exception e) {
            log.error("获取风险画像详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public RiskProfile getRiskProfileByUserId(Long userId) {
        try {
            return riskProfileMapper.selectByUserId(userId);
        } catch (Exception e) {
            log.error("根据用户ID获取风险画像失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getRiskProfileList(Map<String, Object> params, int page, int size) {
        try {
            // 这里可以根据params构建查询条件
            // 暂时使用BaseMapper的selectList方法
            List<RiskProfile> riskProfiles = list();
            int total = riskProfiles.size();
            int offset = (page - 1) * size;
            int end = Math.min(offset + size, total);
            List<RiskProfile> pageList = riskProfiles.subList(offset, end);

            Map<String, Object> result = new HashMap<>();
            result.put("list", pageList);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("查询风险画像列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public Map<String, Object> getRiskProfilesByLevel(Integer riskLevel, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<RiskProfile> riskProfiles = riskProfileMapper.selectByRiskLevel(riskLevel, offset, size);
            // 计算总数
            int total = riskProfiles.size();

            Map<String, Object> result = new HashMap<>();
            result.put("list", riskProfiles);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("根据风险等级查询风险画像失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public Map<String, Object> getRiskProfilesByScoreRange(Integer minScore, Integer maxScore, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<RiskProfile> riskProfiles = riskProfileMapper.selectByScoreRange(minScore, maxScore, offset, size);
            // 计算总数
            int total = riskProfiles.size();

            Map<String, Object> result = new HashMap<>();
            result.put("list", riskProfiles);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("根据风险评分范围查询风险画像失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    @Transactional
    public boolean updateRiskScoreAndLevel(Long id, Integer riskScore, Integer riskLevel) {
        try {
            riskProfileMapper.updateRiskScoreAndLevel(id, riskScore, riskLevel);
            return true;
        } catch (Exception e) {
            log.error("更新风险评分和等级失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public RiskProfile generateRiskProfile(Long userId, String username) {
        try {
            // 检查是否已存在风险画像
            RiskProfile existingProfile = riskProfileMapper.selectByUserId(userId);
            if (existingProfile != null) {
                // 更新现有风险画像
                existingProfile.setRiskScore(calculateRiskScore(userId));
                existingProfile.setRiskLevel(calculateRiskLevel(existingProfile.getRiskScore()));
                existingProfile.setRiskTags(generateRiskTags(userId));
                existingProfile.setBehaviorFeatures(generateBehaviorFeatures(userId));
                existingProfile.setRiskAnalysis(generateRiskAnalysis(existingProfile));
                existingProfile.setSuggestions(generateSuggestions(existingProfile));
                existingProfile.setLastUpdateTime(LocalDateTime.now());
                existingProfile.setUpdateTime(LocalDateTime.now());
                updateById(existingProfile);
                return existingProfile;
            } else {
                // 创建新风险画像
                RiskProfile riskProfile = new RiskProfile();
                riskProfile.setUserId(userId);
                riskProfile.setUsername(username);
                riskProfile.setRiskScore(calculateRiskScore(userId));
                riskProfile.setRiskLevel(calculateRiskLevel(riskProfile.getRiskScore()));
                riskProfile.setRiskTags(generateRiskTags(userId));
                riskProfile.setBehaviorFeatures(generateBehaviorFeatures(userId));
                riskProfile.setRiskAnalysis(generateRiskAnalysis(riskProfile));
                riskProfile.setSuggestions(generateSuggestions(riskProfile));
                riskProfile.setStatus(1);
                riskProfile.setDeleted(0);
                riskProfile.setLastUpdateTime(LocalDateTime.now());
                riskProfile.setCreateTime(LocalDateTime.now());
                riskProfile.setUpdateTime(LocalDateTime.now());
                save(riskProfile);
                return riskProfile;
            }
        } catch (Exception e) {
            log.error("生成风险画像失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    @Transactional
    public List<RiskProfile> batchGenerateRiskProfiles(List<Long> userIds) {
        try {
            List<RiskProfile> generatedProfiles = new ArrayList<>();
            for (Long userId : userIds) {
                // 这里简化处理，实际应该从用户服务获取用户名
                String username = "User_" + userId;
                RiskProfile profile = generateRiskProfile(userId, username);
                if (profile != null) {
                    generatedProfiles.add(profile);
                }
            }
            return generatedProfiles;
        } catch (Exception e) {
            log.error("批量生成风险画像失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public Map<String, Object> getRiskProfileStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("riskLevelStats", riskProfileMapper.selectRiskLevelStats());
            stats.put("scoreDistribution", riskProfileMapper.selectScoreDistribution());
            return stats;
        } catch (Exception e) {
            log.error("统计风险画像信息失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public List<RiskProfile> getTopRiskUsers(int limit) {
        try {
            return riskProfileMapper.selectTopRiskUsers(limit);
        } catch (Exception e) {
            log.error("获取风险评分最高的用户失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public Map<String, Object> getScoreDistribution() {
        try {
            Map<String, Object> distribution = new HashMap<>();
            distribution.put("scoreDistribution", riskProfileMapper.selectScoreDistribution());
            return distribution;
        } catch (Exception e) {
            log.error("获取风险评分分布失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    // 计算风险评分
    private Integer calculateRiskScore(Long userId) {
        // 这里简化处理，实际应该根据用户行为数据计算风险评分
        // 例如：登录行为、浏览行为、演练行为等
        return (int) (Math.random() * 100);
    }

    // 计算风险等级
    private Integer calculateRiskLevel(Integer riskScore) {
        if (riskScore < 30) {
            return 1; // 低风险
        } else if (riskScore < 70) {
            return 2; // 中风险
        } else {
            return 3; // 高风险
        }
    }

    // 生成风险标签
    private String generateRiskTags(Long userId) {
        // 这里简化处理，实际应该根据用户行为数据生成风险标签
        return "{\"tags\": [\"低风险用户\", \"正常行为\"]}";
    }

    // 生成行为特征
    private String generateBehaviorFeatures(Long userId) {
        // 这里简化处理，实际应该根据用户行为数据生成行为特征
        return "{\"features\": [\"正常登录行为\", \"定期学习\", \"积极参与演练\"]}";
    }

    // 生成风险分析结果
    private String generateRiskAnalysis(RiskProfile riskProfile) {
        // 这里简化处理，实际应该根据风险评分和行为特征生成风险分析结果
        return "{\"analysis\": \"用户风险评分较低，行为特征正常，属于低风险用户。\"}";
    }

    // 生成建议措施
    private String generateSuggestions(RiskProfile riskProfile) {
        // 这里简化处理，实际应该根据风险分析结果生成建议措施
        return "{\"suggestions\": [\"继续保持良好的安全习惯\", \"定期参与反诈学习\"]}";
    }
}
