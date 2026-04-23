package com.anti.fraud.modules.emotion.service.impl;

import com.anti.fraud.modules.emotion.entity.EmotionAnalysis;
import com.anti.fraud.modules.emotion.mapper.EmotionAnalysisMapper;
import com.anti.fraud.modules.emotion.service.EmotionService;
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
 * 情感分析服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EmotionServiceImpl extends ServiceImpl<EmotionAnalysisMapper, EmotionAnalysis> implements EmotionService {

    private final EmotionAnalysisMapper emotionAnalysisMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public String createEmotionAnalysis(EmotionAnalysis emotionAnalysis) {
        try {
            // 生成分析ID
            String analysisId = UUID.randomUUID().toString();
            emotionAnalysis.setAnalysisId(analysisId);
            emotionAnalysis.setStatus(2); // 分析中
            emotionAnalysis.setDeleted(0);
            emotionAnalysis.setCreateTime(LocalDateTime.now());
            emotionAnalysis.setUpdateTime(LocalDateTime.now());

            boolean success = save(emotionAnalysis);
            if (success) {
                // 异步分析情感
                analyzeTextEmotionAsync(emotionAnalysis);
                return analysisId;
            } else {
                throw new RuntimeException("创建情感分析失败");
            }
        } catch (Exception e) {
            log.error("创建情感分析失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建情感分析失败");
        }
    }

    @Override
    @Transactional
    public boolean updateEmotionAnalysis(EmotionAnalysis emotionAnalysis) {
        try {
            emotionAnalysis.setUpdateTime(LocalDateTime.now());
            return updateById(emotionAnalysis);
        } catch (Exception e) {
            log.error("更新情感分析失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteEmotionAnalysis(String analysisId) {
        try {
            EmotionAnalysis analysis = emotionAnalysisMapper.selectByAnalysisId(analysisId);
            if (analysis != null) {
                analysis.setDeleted(1);
                analysis.setUpdateTime(LocalDateTime.now());
                return updateById(analysis);
            }
            return false;
        } catch (Exception e) {
            log.error("删除情感分析失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public EmotionAnalysis getEmotionAnalysisByAnalysisId(String analysisId) {
        try {
            return emotionAnalysisMapper.selectByAnalysisId(analysisId);
        } catch (Exception e) {
            log.error("获取情感分析详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public EmotionAnalysis getEmotionAnalysisByMessageId(String messageId) {
        try {
            return emotionAnalysisMapper.selectByMessageId(messageId);
        } catch (Exception e) {
            log.error("根据消息ID获取情感分析失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getEmotionAnalysisList(Integer emotionType, Integer status, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<EmotionAnalysis> analyses = emotionAnalysisMapper.selectByEmotionType(emotionType, status, offset, size);
            // 计算总数
            int total = emotionAnalysisMapper.countByEmotionType(emotionType, status);

            Map<String, Object> result = new HashMap<>();
            result.put("list", analyses);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("查询情感分析列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public Map<String, Object> getEmotionAnalysisListByUserId(Long userId, Integer status, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<EmotionAnalysis> analyses = emotionAnalysisMapper.selectByUserId(userId, status, offset, size);
            // 计算总数
            int total = emotionAnalysisMapper.countByUserId(userId, status);

            Map<String, Object> result = new HashMap<>();
            result.put("list", analyses);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("根据用户ID查询情感分析列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public Map<String, Object> getEmotionAnalysisListBySessionId(String sessionId, Integer status, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<EmotionAnalysis> analyses = emotionAnalysisMapper.selectBySessionId(sessionId, status, offset, size);
            // 计算总数
            int total = emotionAnalysisMapper.countBySessionId(sessionId, status);

            Map<String, Object> result = new HashMap<>();
            result.put("list", analyses);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("根据会话ID查询情感分析列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    @Transactional
    public boolean updateEmotionAnalysisStatus(String analysisId, Integer status) {
        try {
            EmotionAnalysis analysis = emotionAnalysisMapper.selectByAnalysisId(analysisId);
            if (analysis != null) {
                return emotionAnalysisMapper.updateStatus(analysis.getId(), status) > 0;
            }
            return false;
        } catch (Exception e) {
            log.error("更新情感分析状态失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateEmotionAnalysisResult(String analysisId, Integer emotionType, Double emotionScore, String emotionTags, String analysisResult) {
        try {
            EmotionAnalysis analysis = emotionAnalysisMapper.selectByAnalysisId(analysisId);
            if (analysis != null) {
                return emotionAnalysisMapper.updateAnalysisResult(analysis.getId(), emotionType, emotionScore, emotionTags, analysisResult, LocalDateTime.now()) > 0;
            }
            return false;
        } catch (Exception e) {
            log.error("更新情感分析结果失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Integer countEmotionAnalysis(Integer emotionType, Integer status) {
        try {
            return emotionAnalysisMapper.countByEmotionType(emotionType, status);
        } catch (Exception e) {
            log.error("统计情感分析数量失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public Integer countEmotionAnalysisByUserId(Long userId, Integer status) {
        try {
            return emotionAnalysisMapper.countByUserId(userId, status);
        } catch (Exception e) {
            log.error("统计用户情感分析数量失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public Integer countEmotionAnalysisBySessionId(String sessionId, Integer status) {
        try {
            return emotionAnalysisMapper.countBySessionId(sessionId, status);
        } catch (Exception e) {
            log.error("统计会话情感分析数量失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public Double calculateAverageEmotionScore(Integer emotionType, Integer status) {
        try {
            return emotionAnalysisMapper.calculateAverageEmotionScore(emotionType, status);
        } catch (Exception e) {
            log.error("计算平均情感得分失败: {}", e.getMessage(), e);
            return 0.0;
        }
    }

    @Override
    public List<EmotionAnalysis> getRecentEmotionAnalysis(int limit, Integer status) {
        try {
            return emotionAnalysisMapper.selectRecentAnalysis(limit, status);
        } catch (Exception e) {
            log.error("获取最近的情感分析失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public int batchCreateEmotionAnalysis(List<EmotionAnalysis> analyses) {
        try {
            for (EmotionAnalysis analysis : analyses) {
                analysis.setAnalysisId(UUID.randomUUID().toString());
                analysis.setStatus(2); // 分析中
                analysis.setDeleted(0);
                analysis.setCreateTime(LocalDateTime.now());
                analysis.setUpdateTime(LocalDateTime.now());
            }
            int count = emotionAnalysisMapper.batchInsert(analyses);
            // 异步分析情感
            for (EmotionAnalysis analysis : analyses) {
                analyzeTextEmotionAsync(analysis);
            }
            return count;
        } catch (Exception e) {
            log.error("批量创建情感分析失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public Map<String, Object> getEmotionAnalysisStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 统计总分析数量
            int total = emotionAnalysisMapper.countByEmotionType(null, 1);
            statistics.put("total", total);
            
            // 统计各情感类型分析数量
            int positive = emotionAnalysisMapper.countByEmotionType(1, 1);
            int negative = emotionAnalysisMapper.countByEmotionType(2, 1);
            int neutral = emotionAnalysisMapper.countByEmotionType(3, 1);
            statistics.put("positive", positive);
            statistics.put("negative", negative);
            statistics.put("neutral", neutral);
            
            // 计算平均情感得分
            Double averageScore = emotionAnalysisMapper.calculateAverageEmotionScore(null, 1);
            statistics.put("averageScore", averageScore != null ? averageScore : 0.0);
            
            // 统计已分析和分析中的数量
            int analyzed = emotionAnalysisMapper.countByEmotionType(null, 1);
            int analyzing = emotionAnalysisMapper.countByEmotionType(null, 2);
            int failed = emotionAnalysisMapper.countByEmotionType(null, 3);
            statistics.put("analyzed", analyzed);
            statistics.put("analyzing", analyzing);
            statistics.put("failed", failed);
            
            return statistics;
        } catch (Exception e) {
            log.error("获取情感分析统计信息失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public EmotionAnalysis analyzeTextEmotion(String text) {
        try {
            // 这里简化处理，实际应该调用真实的情感分析API
            // 例如使用文心一言或ChatGLM的情感分析能力
            EmotionAnalysis analysis = new EmotionAnalysis();
            analysis.setAnalysisId(UUID.randomUUID().toString());
            analysis.setText(text);
            analysis.setEmotionType(3); // 默认中性
            analysis.setEmotionScore(0.0);
            analysis.setEmotionTags("{\"tags\": [\"中性\"]}");
            analysis.setAnalysisResult("{\"result\": \"中性\", \"confidence\": 0.5}");
            analysis.setAnalysisTime(LocalDateTime.now());
            analysis.setStatus(1); // 已分析
            analysis.setCreateTime(LocalDateTime.now());
            analysis.setUpdateTime(LocalDateTime.now());
            analysis.setDeleted(0);
            
            // 简单的情感分析逻辑
            if (text.contains("高兴") || text.contains("开心") || text.contains("快乐") || text.contains("满意")) {
                analysis.setEmotionType(1); // 积极
                analysis.setEmotionScore(0.8);
                analysis.setEmotionTags("{\"tags\": [\"积极\", \"开心\"]}");
                analysis.setAnalysisResult("{\"result\": \"积极\", \"confidence\": 0.9}");
            } else if (text.contains("生气") || text.contains("愤怒") || text.contains("伤心") || text.contains("失望")) {
                analysis.setEmotionType(2); // 消极
                analysis.setEmotionScore(-0.8);
                analysis.setEmotionTags("{\"tags\": [\"消极\", \"生气\"]}");
                analysis.setAnalysisResult("{\"result\": \"消极\", \"confidence\": 0.9}");
            }
            
            return analysis;
        } catch (Exception e) {
            log.error("分析文本情感失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> adjustResponseStrategy(EmotionAnalysis emotionAnalysis) {
        try {
            Map<String, Object> strategy = new HashMap<>();
            
            if (emotionAnalysis == null) {
                // 默认策略
                strategy.put("type", "neutral");
                strategy.put("tone", "neutral");
                strategy.put("content", "感谢您的咨询，我们会为您提供专业的反诈知识。");
                strategy.put("recommendations", new ArrayList<>());
                return strategy;
            }
            
            Integer emotionType = emotionAnalysis.getEmotionType();
            Double emotionScore = emotionAnalysis.getEmotionScore();
            
            if (emotionType == 1) { // 积极
                strategy.put("type", "positive");
                strategy.put("tone", "friendly");
                strategy.put("content", "很高兴看到您对我们的服务感到满意！如果您有任何其他问题，随时可以向我咨询。");
                strategy.put("recommendations", List.of("继续保持积极的心态", "分享您的反诈经验帮助他人"));
            } else if (emotionType == 2) { // 消极
                strategy.put("type", "negative");
                strategy.put("tone", "empathetic");
                strategy.put("content", "我理解您的感受，遇到这样的情况确实让人感到困扰。请放心，我们会尽力帮助您解决问题，为您提供最有效的反诈建议。");
                strategy.put("recommendations", List.of("保持冷静，不要慌张", "及时联系相关部门", "学习更多反诈知识"));
            } else { // 中性
                strategy.put("type", "neutral");
                strategy.put("tone", "professional");
                strategy.put("content", "感谢您的咨询，我们会为您提供专业的反诈知识和建议。");
                strategy.put("recommendations", List.of("了解常见诈骗手法", "提高警惕，保护个人信息", "定期更新反诈知识"));
            }
            
            return strategy;
        } catch (Exception e) {
            log.error("调整回答策略失败: {}", e.getMessage(), e);
            Map<String, Object> strategy = new HashMap<>();
            strategy.put("type", "neutral");
            strategy.put("tone", "neutral");
            strategy.put("content", "感谢您的咨询，我们会为您提供专业的反诈知识。");
            strategy.put("recommendations", new ArrayList<>());
            return strategy;
        }
    }

    /**
     * 异步分析文本情感
     * @param emotionAnalysis 情感分析信息
     */
    private void analyzeTextEmotionAsync(EmotionAnalysis emotionAnalysis) {
        // 这里简化处理，实际应该使用线程池或消息队列进行异步处理
        new Thread(() -> {
            try {
                EmotionAnalysis analysis = analyzeTextEmotion(emotionAnalysis.getText());
                if (analysis != null) {
                    updateEmotionAnalysisResult(
                            emotionAnalysis.getAnalysisId(),
                            analysis.getEmotionType(),
                            analysis.getEmotionScore(),
                            analysis.getEmotionTags(),
                            analysis.getAnalysisResult()
                    );
                    updateEmotionAnalysisStatus(emotionAnalysis.getAnalysisId(), 1); // 已分析
                } else {
                    updateEmotionAnalysisStatus(emotionAnalysis.getAnalysisId(), 3); // 分析失败
                }
            } catch (Exception e) {
                log.error("异步分析文本情感失败: {}", e.getMessage(), e);
                updateEmotionAnalysisStatus(emotionAnalysis.getAnalysisId(), 3); // 分析失败
            }
        }).start();
    }
}
