package com.anti.fraud.modules.exam.service.impl;

import com.anti.fraud.modules.exam.entity.Challenge;
import com.anti.fraud.modules.exam.entity.ChallengeParticipant;
import com.anti.fraud.modules.exam.mapper.ChallengeMapper;
import com.anti.fraud.modules.exam.mapper.ChallengeParticipantMapper;
import com.anti.fraud.modules.exam.service.ChallengeService;
import com.anti.fraud.modules.exam.service.QuestionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChallengeServiceImpl implements ChallengeService {

    private final ChallengeMapper challengeMapper;
    private final ChallengeParticipantMapper challengeParticipantMapper;
    private final QuestionService questionService;

    @Override
    public boolean createChallenge(Challenge challenge) {
        try {
            challenge.setParticipantCount(0);
            challenge.setStatus(1); // 1: 待开始
            challengeMapper.insert(challenge);
            log.info("创建挑战赛事成功: id={}, title={}", challenge.getId(), challenge.getTitle());
            return true;
        } catch (Exception e) {
            log.error("创建挑战赛事失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean updateChallenge(Challenge challenge) {
        try {
            challengeMapper.updateById(challenge);
            log.info("更新挑战赛事成功: id={}", challenge.getId());
            return true;
        } catch (Exception e) {
            log.error("更新挑战赛事失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean deleteChallenge(Long id) {
        try {
            challengeMapper.deleteById(id);
            log.info("删除挑战赛事成功: id={}", id);
            return true;
        } catch (Exception e) {
            log.error("删除挑战赛事失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Challenge getChallengeById(Long id) {
        try {
            return challengeMapper.selectById(id);
        } catch (Exception e) {
            log.error("获取挑战赛事详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<Challenge> getChallengeList(Integer status, String category, int page, int size) {
        try {
            LambdaQueryWrapper<Challenge> queryWrapper = new LambdaQueryWrapper<>();
            if (status != null) {
                queryWrapper.eq(Challenge::getStatus, status);
            }
            if (category != null && !category.isEmpty()) {
                queryWrapper.eq(Challenge::getCategory, category);
            }
            queryWrapper.orderByDesc(Challenge::getCreateTime);

            IPage<Challenge> pageInfo = new Page<>(page, size);
            challengeMapper.selectPage(pageInfo, queryWrapper);

            return pageInfo.getRecords();
        } catch (Exception e) {
            log.error("获取挑战赛事列表失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public Map<String, Object> participateChallenge(Long challengeId, Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Challenge challenge = challengeMapper.selectById(challengeId);
            if (challenge == null) {
                result.put("success", false);
                result.put("message", "挑战赛事不存在");
                return result;
            }

            // 检查是否已经参与
            LambdaQueryWrapper<ChallengeParticipant> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ChallengeParticipant::getChallengeId, challengeId)
                    .eq(ChallengeParticipant::getUserId, userId);
            ChallengeParticipant existingParticipant = challengeParticipantMapper.selectOne(queryWrapper);
            if (existingParticipant != null) {
                result.put("success", false);
                result.put("message", "您已经参与过此挑战");
                return result;
            }

            // 检查挑战状态
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(challenge.getStartTime())) {
                result.put("success", false);
                result.put("message", "挑战尚未开始");
                return result;
            }
            if (now.isAfter(challenge.getEndTime())) {
                result.put("success", false);
                result.put("message", "挑战已结束");
                return result;
            }

            // 创建参与记录
            ChallengeParticipant participant = new ChallengeParticipant();
            participant.setChallengeId(challengeId);
            participant.setUserId(userId);
            participant.setStatus(1); // 1: 进行中
            participant.setParticipateTime(now);
            challengeParticipantMapper.insert(participant);

            // 更新参与人数
            challenge.setParticipantCount(challenge.getParticipantCount() + 1);
            challengeMapper.updateById(challenge);

            // 生成挑战题目
            List<Map<String, Object>> questions = generateChallengeQuestions(challengeId);

            result.put("success", true);
            result.put("message", "参与挑战成功");
            result.put("participantId", participant.getId());
            result.put("questions", questions);
            
            log.info("用户参与挑战成功: userId={}, challengeId={}", userId, challengeId);
            return result;
        } catch (Exception e) {
            log.error("参与挑战失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "参与挑战失败");
            return result;
        }
    }

    @Override
    public Map<String, Object> submitChallengeAnswer(Long participantId, Map<String, Object> answers) {
        Map<String, Object> result = new HashMap<>();
        try {
            ChallengeParticipant participant = challengeParticipantMapper.selectById(participantId);
            if (participant == null) {
                result.put("success", false);
                result.put("message", "参与记录不存在");
                return result;
            }

            if (participant.getStatus() != 1) {
                result.put("success", false);
                result.put("message", "挑战已结束或已提交");
                return result;
            }

            // 解析答案
            List<Map<String, Object>> answerList = (List<Map<String, Object>>) answers.get("answers");
            int correctCount = 0;
            int totalCount = answerList.size();
            
            for (Map<String, Object> answer : answerList) {
                boolean isCorrect = (boolean) answer.getOrDefault("isCorrect", false);
                if (isCorrect) {
                    correctCount++;
                }
            }

            int score = correctCount * 100 / totalCount;
            
            // 更新参与记录
            participant.setScore(score);
            participant.setStatus(2); // 2: 已完成
            participant.setFinishTime(LocalDateTime.now());
            participant.setAnswers(answers.toString());
            challengeParticipantMapper.updateById(participant);

            // 计算排名
            calculateChallengeRanking(participant.getChallengeId());

            result.put("success", true);
            result.put("message", "提交答案成功");
            result.put("score", score);
            result.put("correctCount", correctCount);
            result.put("totalCount", totalCount);
            
            log.info("用户提交挑战答案成功: participantId={}, score={}", participantId, score);
            return result;
        } catch (Exception e) {
            log.error("提交挑战答案失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "提交答案失败");
            return result;
        }
    }

    @Override
    public List<ChallengeParticipant> getChallengeRanking(Long challengeId, int page, int size) {
        try {
            LambdaQueryWrapper<ChallengeParticipant> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ChallengeParticipant::getChallengeId, challengeId)
                    .eq(ChallengeParticipant::getStatus, 2) // 已完成
                    .orderByDesc(ChallengeParticipant::getScore)
                    .orderByAsc(ChallengeParticipant::getFinishTime);

            IPage<ChallengeParticipant> pageInfo = new Page<>(page, size);
            challengeParticipantMapper.selectPage(pageInfo, queryWrapper);

            return pageInfo.getRecords();
        } catch (Exception e) {
            log.error("获取挑战排行榜失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<ChallengeParticipant> getUserChallengeRecords(Long userId, int page, int size) {
        try {
            LambdaQueryWrapper<ChallengeParticipant> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ChallengeParticipant::getUserId, userId)
                    .orderByDesc(ChallengeParticipant::getParticipateTime);

            IPage<ChallengeParticipant> pageInfo = new Page<>(page, size);
            challengeParticipantMapper.selectPage(pageInfo, queryWrapper);

            return pageInfo.getRecords();
        } catch (Exception e) {
            log.error("获取用户挑战记录失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public boolean calculateChallengeRanking(Long challengeId) {
        try {
            LambdaQueryWrapper<ChallengeParticipant> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ChallengeParticipant::getChallengeId, challengeId)
                    .eq(ChallengeParticipant::getStatus, 2) // 已完成
                    .orderByDesc(ChallengeParticipant::getScore)
                    .orderByAsc(ChallengeParticipant::getFinishTime);

            List<ChallengeParticipant> participants = challengeParticipantMapper.selectList(queryWrapper);
            
            for (int i = 0; i < participants.size(); i++) {
                ChallengeParticipant participant = participants.get(i);
                participant.setRank(i + 1);
                challengeParticipantMapper.updateById(participant);
            }
            
            log.info("计算挑战排名成功: challengeId={}, participantCount={}", challengeId, participants.size());
            return true;
        } catch (Exception e) {
            log.error("计算挑战排名失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<Map<String, Object>> generateChallengeQuestions(Long challengeId) {
        try {
            Challenge challenge = challengeMapper.selectById(challengeId);
            if (challenge == null) {
                return Collections.emptyList();
            }

            List<Map<String, Object>> questions = new ArrayList<>();
            int questionCount = challenge.getQuestionCount();
            String category = challenge.getCategory();
            String difficulty = challenge.getDifficulty();

            // 生成题目
            for (int i = 0; i < questionCount; i++) {
                Map<String, Object> question = new HashMap<>();
                question.put("id", new Random().nextLong());
                question.put("questionText", category + "挑战题目 " + (i + 1) + "（" + difficulty + "）");
                question.put("optionA", "选项A");
                question.put("optionB", "选项B");
                question.put("optionC", "选项C");
                question.put("optionD", "选项D");
                question.put("correctAnswer", "A");
                question.put("explanation", "题目解析");
                question.put("category", category);
                question.put("difficulty", difficulty);
                question.put("score", 100 / questionCount);
                questions.add(question);
            }

            return questions;
        } catch (Exception e) {
            log.error("生成挑战题目失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public Map<String, Object> getChallengeStats() {
        try {
            // 统计挑战赛事总数
            long totalChallenges = challengeMapper.selectCount(null);

            // 统计参与人数
            long totalParticipants = challengeParticipantMapper.selectCount(null);

            // 统计已完成挑战数
            LambdaQueryWrapper<ChallengeParticipant> completedQuery = new LambdaQueryWrapper<>();
            completedQuery.eq(ChallengeParticipant::getStatus, 2);
            long completedChallenges = challengeParticipantMapper.selectCount(completedQuery);

            // 按分类统计
            Map<String, Long> categoryStats = new HashMap<>();
            List<Challenge> challenges = challengeMapper.selectList(null);
            for (Challenge challenge : challenges) {
                String category = challenge.getCategory();
                categoryStats.put(category, categoryStats.getOrDefault(category, 0L) + 1);
            }

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalChallenges", totalChallenges);
            stats.put("totalParticipants", totalParticipants);
            stats.put("completedChallenges", completedChallenges);
            stats.put("categoryStats", categoryStats);

            log.info("获取挑战统计信息成功: totalChallenges={}, totalParticipants={}", totalChallenges, totalParticipants);
            return stats;
        } catch (Exception e) {
            log.error("获取挑战统计信息失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }
}
