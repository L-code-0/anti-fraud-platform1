package com.anti.fraud.modules.behavior.service.impl;

import com.anti.fraud.modules.behavior.entity.BehaviorAnalysis;
import com.anti.fraud.modules.behavior.mapper.BehaviorAnalysisMapper;
import com.anti.fraud.modules.behavior.service.BehaviorAnalysisService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 行为分析服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class BehaviorAnalysisServiceImpl extends ServiceImpl<BehaviorAnalysisMapper, BehaviorAnalysis> implements BehaviorAnalysisService {

    private final BehaviorAnalysisMapper behaviorAnalysisMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public String createBehaviorAnalysis(BehaviorAnalysis behaviorAnalysis) {
        try {
            // 生成分析ID
            String analysisId = UUID.randomUUID().toString();
            behaviorAnalysis.setAnalysisId(analysisId);
            behaviorAnalysis.setBehaviorTime(LocalDateTime.now());
            behaviorAnalysis.setStatus(2); // 分析中
            behaviorAnalysis.setDeleted(0);
            behaviorAnalysis.setCreateTime(LocalDateTime.now());
            behaviorAnalysis.setUpdateTime(LocalDateTime.now());

            boolean success = save(behaviorAnalysis);
            if (success) {
                // 异步分析行为
                analyzeUserBehaviorAsync(behaviorAnalysis);
                return analysisId;
            } else {
                throw new RuntimeException("创建行为分析失败");
            }
        } catch (Exception e) {
            log.error("创建行为分析失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建行为分析失败");
        }
    }

    @Override
    @Transactional
    public boolean updateBehaviorAnalysis(BehaviorAnalysis behaviorAnalysis) {
        try {
            behaviorAnalysis.setUpdateTime(LocalDateTime.now());
            return updateById(behaviorAnalysis);
        } catch (Exception e) {
            log.error("更新行为分析失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteBehaviorAnalysis(String analysisId) {
        try {
            BehaviorAnalysis analysis = behaviorAnalysisMapper.selectByAnalysisId(analysisId);
            if (analysis != null) {
                analysis.setDeleted(1);
                analysis.setUpdateTime(LocalDateTime.now());
                return updateById(analysis);
            }
            return false;
        } catch (Exception e) {
            log.error("删除行为分析失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public BehaviorAnalysis getBehaviorAnalysisByAnalysisId(String analysisId) {
        try {
            return behaviorAnalysisMapper.selectByAnalysisId(analysisId);
        } catch (Exception e) {
            log.error("获取行为分析详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getBehaviorAnalysisList(Integer behaviorType, Integer status, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<BehaviorAnalysis> analyses = behaviorAnalysisMapper.selectByBehaviorType(behaviorType, status, offset, size);
            // 计算总数
            int total = behaviorAnalysisMapper.countByBehaviorType(behaviorType, status);

            Map<String, Object> result = new HashMap<>();
            result.put("list", analyses);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("查询行为分析列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public Map<String, Object> getBehaviorAnalysisListByUserId(Long userId, Integer behaviorType, Integer status, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<BehaviorAnalysis> analyses = behaviorAnalysisMapper.selectByUserId(userId, behaviorType, status, offset, size);
            // 计算总数
            int total = behaviorAnalysisMapper.countByUserId(userId, behaviorType, status);

            Map<String, Object> result = new HashMap<>();
            result.put("list", analyses);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("根据用户ID查询行为分析列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public List<BehaviorAnalysis> getBehaviorAnalysisListByTimeRange(LocalDateTime startTime, LocalDateTime endTime, Integer behaviorType, Integer status) {
        try {
            return behaviorAnalysisMapper.selectByTimeRange(startTime, endTime, behaviorType, status);
        } catch (Exception e) {
            log.error("根据时间范围查询行为分析列表失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public boolean updateBehaviorAnalysisStatus(String analysisId, Integer status) {
        try {
            BehaviorAnalysis analysis = behaviorAnalysisMapper.selectByAnalysisId(analysisId);
            if (analysis != null) {
                return behaviorAnalysisMapper.updateStatus(analysis.getId(), status) > 0;
            }
            return false;
        } catch (Exception e) {
            log.error("更新分析状态失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateBehaviorAnalysisResult(String analysisId, String analysisResult, String feedbackContent) {
        try {
            BehaviorAnalysis analysis = behaviorAnalysisMapper.selectByAnalysisId(analysisId);
            if (analysis != null) {
                return behaviorAnalysisMapper.updateAnalysisResult(analysis.getId(), analysisResult, feedbackContent) > 0;
            }
            return false;
        } catch (Exception e) {
            log.error("更新分析结果失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Integer countBehaviorAnalysis(Integer behaviorType, Integer status) {
        try {
            return behaviorAnalysisMapper.countByBehaviorType(behaviorType, status);
        } catch (Exception e) {
            log.error("统计行为分析数量失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public Integer countBehaviorAnalysisByUserId(Long userId, Integer behaviorType, Integer status) {
        try {
            return behaviorAnalysisMapper.countByUserId(userId, behaviorType, status);
        } catch (Exception e) {
            log.error("统计用户行为分析数量失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public List<BehaviorAnalysis> getRecentBehaviorAnalysis(int limit, Integer behaviorType, Integer status) {
        try {
            return behaviorAnalysisMapper.selectRecentAnalysis(limit, behaviorType, status);
        } catch (Exception e) {
            log.error("获取最近的行为分析失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public int batchCreateBehaviorAnalysis(List<BehaviorAnalysis> analyses) {
        try {
            for (BehaviorAnalysis analysis : analyses) {
                analysis.setAnalysisId(UUID.randomUUID().toString());
                analysis.setBehaviorTime(LocalDateTime.now());
                analysis.setStatus(2); // 分析中
                analysis.setDeleted(0);
                analysis.setCreateTime(LocalDateTime.now());
                analysis.setUpdateTime(LocalDateTime.now());
            }
            int count = behaviorAnalysisMapper.batchInsert(analyses);
            // 异步分析行为
            for (BehaviorAnalysis analysis : analyses) {
                analyzeUserBehaviorAsync(analysis);
            }
            return count;
        } catch (Exception e) {
            log.error("批量创建行为分析失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public Map<String, Object> getBehaviorAnalysisStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 统计总行为分析数量
            int total = behaviorAnalysisMapper.countByBehaviorType(null, 1);
            statistics.put("total", total);
            
            // 统计各类型行为分析数量
            int browse = behaviorAnalysisMapper.countByBehaviorType(1, 1);
            int click = behaviorAnalysisMapper.countByBehaviorType(2, 1);
            int stay = behaviorAnalysisMapper.countByBehaviorType(3, 1);
            int search = behaviorAnalysisMapper.countByBehaviorType(4, 1);
            int answer = behaviorAnalysisMapper.countByBehaviorType(5, 1);
            int exercise = behaviorAnalysisMapper.countByBehaviorType(6, 1);
            int other = behaviorAnalysisMapper.countByBehaviorType(7, 1);
            statistics.put("browse", browse);
            statistics.put("click", click);
            statistics.put("stay", stay);
            statistics.put("search", search);
            statistics.put("answer", answer);
            statistics.put("exercise", exercise);
            statistics.put("other", other);
            
            // 统计状态分布
            int analyzed = behaviorAnalysisMapper.countByBehaviorType(null, 1);
            int analyzing = behaviorAnalysisMapper.countByBehaviorType(null, 2);
            int failed = behaviorAnalysisMapper.countByBehaviorType(null, 3);
            statistics.put("analyzed", analyzed);
            statistics.put("analyzing", analyzing);
            statistics.put("failed", failed);
            
            return statistics;
        } catch (Exception e) {
            log.error("获取行为分析统计信息失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public boolean analyzeUserBehavior(String analysisId) {
        try {
            BehaviorAnalysis analysis = behaviorAnalysisMapper.selectByAnalysisId(analysisId);
            if (analysis != null) {
                // 这里简化处理，实际应该使用AI模型进行行为分析
                // 例如使用文心一言或ChatGLM的API进行行为分析
                String analysisResult = "{\"result\": \"分析完成\", \"confidence\": 0.9}";
                String feedbackContent = "{\"feedback\": \"建议继续学习反诈知识\", \"suggestions\": [\"多参加模拟演练\", \"关注最新诈骗手法\"]}";
                
                updateBehaviorAnalysisResult(analysisId, analysisResult, feedbackContent);
                updateBehaviorAnalysisStatus(analysisId, 1); // 已分析
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("分析用户行为失败: {}", e.getMessage(), e);
            updateBehaviorAnalysisStatus(analysisId, 3); // 分析失败
            return false;
        }
    }

    @Override
    public String recordBrowseBehavior(Long userId, String username, String content, Integer duration) {
        try {
            BehaviorAnalysis analysis = new BehaviorAnalysis();
            analysis.setUserId(userId);
            analysis.setUsername(username);
            analysis.setBehaviorType(1); // 浏览
            analysis.setBehaviorContent("{\"content\": \"" + content + "\", \"duration\": \"" + duration + "\"}");
            return createBehaviorAnalysis(analysis);
        } catch (Exception e) {
            log.error("记录用户浏览行为失败: {}", e.getMessage(), e);
            throw new RuntimeException("记录用户浏览行为失败");
        }
    }

    @Override
    public String recordClickBehavior(Long userId, String username, String content, String position) {
        try {
            BehaviorAnalysis analysis = new BehaviorAnalysis();
            analysis.setUserId(userId);
            analysis.setUsername(username);
            analysis.setBehaviorType(2); // 点击
            analysis.setBehaviorContent("{\"content\": \"" + content + "\", \"position\": \"" + position + "\"}");
            return createBehaviorAnalysis(analysis);
        } catch (Exception e) {
            log.error("记录用户点击行为失败: {}", e.getMessage(), e);
            throw new RuntimeException("记录用户点击行为失败");
        }
    }

    @Override
    public String recordStayBehavior(Long userId, String username, String content, Integer duration) {
        try {
            BehaviorAnalysis analysis = new BehaviorAnalysis();
            analysis.setUserId(userId);
            analysis.setUsername(username);
            analysis.setBehaviorType(3); // 停留
            analysis.setBehaviorContent("{\"content\": \"" + content + "\", \"duration\": \"" + duration + "\"}");
            return createBehaviorAnalysis(analysis);
        } catch (Exception e) {
            log.error("记录用户停留行为失败: {}", e.getMessage(), e);
            throw new RuntimeException("记录用户停留行为失败");
        }
    }

    @Override
    public String recordSearchBehavior(Long userId, String username, String keyword, Integer resultCount) {
        try {
            BehaviorAnalysis analysis = new BehaviorAnalysis();
            analysis.setUserId(userId);
            analysis.setUsername(username);
            analysis.setBehaviorType(4); // 搜索
            analysis.setBehaviorContent("{\"keyword\": \"" + keyword + "\", \"resultCount\": \"" + resultCount + "\"}");
            return createBehaviorAnalysis(analysis);
        } catch (Exception e) {
            log.error("记录用户搜索行为失败: {}", e.getMessage(), e);
            throw new RuntimeException("记录用户搜索行为失败");
        }
    }

    @Override
    public String recordAnswerBehavior(Long userId, String username, String questionId, String answer, String correctAnswer, Boolean isCorrect, Integer timeUsed) {
        try {
            BehaviorAnalysis analysis = new BehaviorAnalysis();
            analysis.setUserId(userId);
            analysis.setUsername(username);
            analysis.setBehaviorType(5); // 答题
            analysis.setBehaviorContent("{\"questionId\": \"" + questionId + "\", \"answer\": \"" + answer + "\", \"correctAnswer\": \"" + correctAnswer + "\", \"isCorrect\": \"" + isCorrect + "\", \"timeUsed\": \"" + timeUsed + "\"}");
            return createBehaviorAnalysis(analysis);
        } catch (Exception e) {
            log.error("记录用户答题行为失败: {}", e.getMessage(), e);
            throw new RuntimeException("记录用户答题行为失败");
        }
    }

    @Override
    public String recordExerciseBehavior(Long userId, String username, String exerciseId, String action, String result) {
        try {
            BehaviorAnalysis analysis = new BehaviorAnalysis();
            analysis.setUserId(userId);
            analysis.setUsername(username);
            analysis.setBehaviorType(6); // 演练
            analysis.setBehaviorContent("{\"exerciseId\": \"" + exerciseId + "\", \"action\": \"" + action + "\", \"result\": \"" + result + "\"}");
            return createBehaviorAnalysis(analysis);
        } catch (Exception e) {
            log.error("记录用户演练行为失败: {}", e.getMessage(), e);
            throw new RuntimeException("记录用户演练行为失败");
        }
    }

    /**
     * 异步分析用户行为
     * @param behaviorAnalysis 行为分析信息
     */
    private void analyzeUserBehaviorAsync(BehaviorAnalysis behaviorAnalysis) {
        // 这里简化处理，实际应该使用线程池或消息队列进行异步处理
        new Thread(() -> {
            try {
                analyzeUserBehavior(behaviorAnalysis.getAnalysisId());
            } catch (Exception e) {
                log.error("异步分析用户行为失败: {}", e.getMessage(), e);
                updateBehaviorAnalysisStatus(behaviorAnalysis.getAnalysisId(), 3); // 分析失败
            }
        }).start();
    }
}
