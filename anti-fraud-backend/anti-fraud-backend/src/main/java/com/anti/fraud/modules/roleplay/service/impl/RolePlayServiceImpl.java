package com.anti.fraud.modules.roleplay.service.impl;

import com.anti.fraud.modules.roleplay.entity.RolePlaySession;
import com.anti.fraud.modules.roleplay.entity.RolePlayScore;
import com.anti.fraud.modules.roleplay.mapper.RolePlaySessionMapper;
import com.anti.fraud.modules.roleplay.mapper.RolePlayScoreMapper;
import com.anti.fraud.modules.roleplay.service.RolePlayService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class RolePlayServiceImpl implements RolePlayService {

    private final RolePlaySessionMapper rolePlaySessionMapper;
    private final RolePlayScoreMapper rolePlayScoreMapper;

    @Override
    public boolean createSession(RolePlaySession session) {
        try {
            session.setStatus(1); // 1: 进行中
            session.setStartTime(LocalDateTime.now());
            session.setTotalScore(0);
            rolePlaySessionMapper.insert(session);
            log.info("创建角色扮演会话成功: id={}, scenarioName={}", session.getId(), session.getScenarioName());
            return true;
        } catch (Exception e) {
            log.error("创建角色扮演会话失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean updateSession(RolePlaySession session) {
        try {
            rolePlaySessionMapper.updateById(session);
            log.info("更新角色扮演会话成功: id={}", session.getId());
            return true;
        } catch (Exception e) {
            log.error("更新角色扮演会话失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean endSession(Long sessionId, String sessionData) {
        try {
            RolePlaySession session = rolePlaySessionMapper.selectById(sessionId);
            if (session != null) {
                session.setStatus(2); // 2: 已完成
                session.setEndTime(LocalDateTime.now());
                session.setDuration((int) Duration.between(session.getStartTime(), session.getEndTime()).toMinutes());
                session.setSessionData(sessionData);
                
                // 计算总评分
                Integer totalScore = calculateTotalScore(sessionId);
                session.setTotalScore(totalScore);
                
                // 生成反馈
                String feedback = generateFeedback(sessionId);
                session.setFeedback(feedback);
                
                rolePlaySessionMapper.updateById(session);
                log.info("结束角色扮演会话成功: id={}, totalScore={}", sessionId, totalScore);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("结束角色扮演会话失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public RolePlaySession getSessionById(Long sessionId) {
        try {
            return rolePlaySessionMapper.selectById(sessionId);
        } catch (Exception e) {
            log.error("获取会话详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<RolePlaySession> getUserSessions(Long userId, Integer status, int page, int size) {
        try {
            LambdaQueryWrapper<RolePlaySession> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(RolePlaySession::getUserId, userId);
            if (status != null) {
                queryWrapper.eq(RolePlaySession::getStatus, status);
            }
            queryWrapper.orderByDesc(RolePlaySession::getCreateTime);

            IPage<RolePlaySession> pageInfo = new Page<>(page, size);
            rolePlaySessionMapper.selectPage(pageInfo, queryWrapper);

            return pageInfo.getRecords();
        } catch (Exception e) {
            log.error("获取用户会话列表失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<RolePlaySession> getAllSessions(Integer status, int page, int size) {
        try {
            LambdaQueryWrapper<RolePlaySession> queryWrapper = new LambdaQueryWrapper<>();
            if (status != null) {
                queryWrapper.eq(RolePlaySession::getStatus, status);
            }
            queryWrapper.orderByDesc(RolePlaySession::getCreateTime);

            IPage<RolePlaySession> pageInfo = new Page<>(page, size);
            rolePlaySessionMapper.selectPage(pageInfo, queryWrapper);

            return pageInfo.getRecords();
        } catch (Exception e) {
            log.error("获取所有会话列表失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public boolean scoreRolePlay(RolePlayScore score) {
        try {
            rolePlayScoreMapper.insert(score);
            log.info("评分角色扮演成功: sessionId={}, criterion={}, score={}", score.getSessionId(), score.getCriterion(), score.getScore());
            return true;
        } catch (Exception e) {
            log.error("评分角色扮演失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<RolePlayScore> getSessionScores(Long sessionId) {
        try {
            LambdaQueryWrapper<RolePlayScore> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(RolePlayScore::getSessionId, sessionId);
            return rolePlayScoreMapper.selectList(queryWrapper);
        } catch (Exception e) {
            log.error("获取会话评分失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<RolePlayScore> getUserScoreHistory(Long userId, int page, int size) {
        try {
            LambdaQueryWrapper<RolePlayScore> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(RolePlayScore::getUserId, userId)
                    .orderByDesc(RolePlayScore::getCreateTime);

            IPage<RolePlayScore> pageInfo = new Page<>(page, size);
            rolePlayScoreMapper.selectPage(pageInfo, queryWrapper);

            return pageInfo.getRecords();
        } catch (Exception e) {
            log.error("获取用户评分历史失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public Integer calculateTotalScore(Long sessionId) {
        try {
            List<RolePlayScore> scores = getSessionScores(sessionId);
            if (scores.isEmpty()) {
                return 0;
            }

            int totalScore = 0;
            for (RolePlayScore score : scores) {
                totalScore += score.getScore();
            }

            return totalScore / scores.size();
        } catch (Exception e) {
            log.error("计算会话总评分失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public String generateFeedback(Long sessionId) {
        try {
            List<RolePlayScore> scores = getSessionScores(sessionId);
            if (scores.isEmpty()) {
                return "暂无评分反馈";
            }

            StringBuilder feedback = new StringBuilder();
            feedback.append("角色扮演反馈：\n");

            int totalScore = 0;
            for (RolePlayScore score : scores) {
                feedback.append(score.getCriterion()).append(": ").append(score.getScore()).append("分");
                if (score.getComments() != null && !score.getComments().isEmpty()) {
                    feedback.append(" (").append(score.getComments()).append(")");
                }
                feedback.append("\n");
                totalScore += score.getScore();
            }

            int averageScore = totalScore / scores.size();
            feedback.append("\n总评分：").append(averageScore).append("分\n");

            if (averageScore >= 90) {
                feedback.append("表现优秀！您在角色扮演中展示了出色的反诈意识和应对能力。");
            } else if (averageScore >= 70) {
                feedback.append("表现良好！您在角色扮演中展示了基本的反诈意识和应对能力，但仍有提升空间。");
            } else {
                feedback.append("需要加强！您在角色扮演中展示的反诈意识和应对能力有待提高，建议多学习反诈知识。");
            }

            return feedback.toString();
        } catch (Exception e) {
            log.error("生成反馈失败: {}", e.getMessage(), e);
            return "生成反馈失败";
        }
    }

    @Override
    public Map<String, Object> getRolePlayStats(Long userId) {
        try {
            // 统计用户会话总数
            LambdaQueryWrapper<RolePlaySession> sessionQuery = new LambdaQueryWrapper<>();
            sessionQuery.eq(RolePlaySession::getUserId, userId);
            long totalSessions = rolePlaySessionMapper.selectCount(sessionQuery);

            // 统计已完成会话数
            LambdaQueryWrapper<RolePlaySession> completedQuery = new LambdaQueryWrapper<>();
            completedQuery.eq(RolePlaySession::getUserId, userId)
                    .eq(RolePlaySession::getStatus, 2);
            long completedSessions = rolePlaySessionMapper.selectCount(completedQuery);

            // 计算平均评分
            List<RolePlaySession> sessions = rolePlaySessionMapper.selectList(completedQuery);
            int totalScore = 0;
            for (RolePlaySession session : sessions) {
                totalScore += session.getTotalScore();
            }
            double averageScore = sessions.isEmpty() ? 0 : (double) totalScore / sessions.size();

            // 按场景统计
            Map<String, Long> scenarioStats = new HashMap<>();
            for (RolePlaySession session : sessions) {
                String scenarioName = session.getScenarioName();
                scenarioStats.put(scenarioName, scenarioStats.getOrDefault(scenarioName, 0L) + 1);
            }

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalSessions", totalSessions);
            stats.put("completedSessions", completedSessions);
            stats.put("averageScore", averageScore);
            stats.put("scenarioStats", scenarioStats);

            log.info("获取角色扮演统计信息成功: userId={}, totalSessions={}", userId, totalSessions);
            return stats;
        } catch (Exception e) {
            log.error("获取角色扮演统计信息失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public List<Map<String, Object>> getRecommendedScenarios(Long userId, int count) {
        try {
            // 简单的推荐逻辑：随机选择场景
            List<Map<String, Object>> scenarios = new ArrayList<>();
            
            // 模拟场景数据
            scenarios.add(Map.of(
                    "id", "1",
                    "name", "电信诈骗场景",
                    "description", "模拟电信诈骗场景，训练用户识别和应对能力",
                    "difficulty", "中等",
                    "duration", 30
            ));
            scenarios.add(Map.of(
                    "id", "2",
                    "name", "网络诈骗场景",
                    "description", "模拟网络诈骗场景，训练用户识别和应对能力",
                    "difficulty", "困难",
                    "duration", 45
            ));
            scenarios.add(Map.of(
                    "id", "3",
                    "name", "金融诈骗场景",
                    "description", "模拟金融诈骗场景，训练用户识别和应对能力",
                    "difficulty", "简单",
                    "duration", 20
            ));
            scenarios.add(Map.of(
                    "id", "4",
                    "name", "冒充公检法场景",
                    "description", "模拟冒充公检法诈骗场景，训练用户识别和应对能力",
                    "difficulty", "中等",
                    "duration", 35
            ));
            scenarios.add(Map.of(
                    "id", "5",
                    "name", "网络兼职刷单场景",
                    "description", "模拟网络兼职刷单诈骗场景，训练用户识别和应对能力",
                    "difficulty", "简单",
                    "duration", 25
            ));

            // 随机选择指定数量的场景
            Collections.shuffle(scenarios);
            return scenarios.subList(0, Math.min(count, scenarios.size()));
        } catch (Exception e) {
            log.error("获取推荐场景失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
