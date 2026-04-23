package com.anti.fraud.modules.feedback.service.impl;

import com.anti.fraud.modules.feedback.entity.Feedback;
import com.anti.fraud.modules.feedback.mapper.FeedbackMapper;
import com.anti.fraud.modules.feedback.service.FeedbackService;
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
import java.util.UUID;

/**
 * 用户评价服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback> implements FeedbackService {

    private final FeedbackMapper feedbackMapper;

    @Override
    @Transactional
    public String createFeedback(Feedback feedback) {
        try {
            // 生成评价ID
            String feedbackId = UUID.randomUUID().toString();
            feedback.setFeedbackId(feedbackId);
            feedback.setStatus(1); // 已提交
            feedback.setDeleted(0);
            feedback.setCreateTime(LocalDateTime.now());
            feedback.setUpdateTime(LocalDateTime.now());

            boolean success = save(feedback);
            if (success) {
                return feedbackId;
            } else {
                throw new RuntimeException("创建评价失败");
            }
        } catch (Exception e) {
            log.error("创建评价失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建评价失败");
        }
    }

    @Override
    @Transactional
    public boolean updateFeedback(Feedback feedback) {
        try {
            feedback.setUpdateTime(LocalDateTime.now());
            return updateById(feedback);
        } catch (Exception e) {
            log.error("更新评价失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteFeedback(String feedbackId) {
        try {
            Feedback feedback = feedbackMapper.selectByFeedbackId(feedbackId);
            if (feedback != null) {
                feedback.setDeleted(1);
                feedback.setUpdateTime(LocalDateTime.now());
                return updateById(feedback);
            }
            return false;
        } catch (Exception e) {
            log.error("删除评价失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Feedback getFeedbackByFeedbackId(String feedbackId) {
        try {
            return feedbackMapper.selectByFeedbackId(feedbackId);
        } catch (Exception e) {
            log.error("获取评价详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Feedback getFeedbackByMessageId(String messageId) {
        try {
            return feedbackMapper.selectByMessageId(messageId);
        } catch (Exception e) {
            log.error("根据消息ID获取评价失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getFeedbackList(Integer type, Integer status, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<Feedback> feedbacks = feedbackMapper.selectByType(type, status, offset, size);
            // 计算总数
            int total = feedbackMapper.countByType(type, status);

            Map<String, Object> result = new HashMap<>();
            result.put("list", feedbacks);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("查询评价列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public Map<String, Object> getFeedbackListByUserId(Long userId, Integer status, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<Feedback> feedbacks = feedbackMapper.selectByUserId(userId, status, offset, size);
            // 计算总数
            int total = feedbackMapper.countByUserId(userId, status);

            Map<String, Object> result = new HashMap<>();
            result.put("list", feedbacks);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("根据用户ID查询评价列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public Map<String, Object> getFeedbackListBySessionId(String sessionId, Integer status, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<Feedback> feedbacks = feedbackMapper.selectBySessionId(sessionId, status, offset, size);
            // 计算总数
            int total = feedbackMapper.countBySessionId(sessionId, status);

            Map<String, Object> result = new HashMap<>();
            result.put("list", feedbacks);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("根据会话ID查询评价列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    @Transactional
    public boolean updateFeedbackStatus(String feedbackId, Integer status) {
        try {
            Feedback feedback = feedbackMapper.selectByFeedbackId(feedbackId);
            if (feedback != null) {
                return feedbackMapper.updateStatus(feedback.getId(), status) > 0;
            }
            return false;
        } catch (Exception e) {
            log.error("更新评价状态失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean handleFeedback(String feedbackId, String handler, String handleResult) {
        try {
            Feedback feedback = feedbackMapper.selectByFeedbackId(feedbackId);
            if (feedback != null) {
                return feedbackMapper.updateHandleInfo(feedback.getId(), handler, LocalDateTime.now(), handleResult) > 0;
            }
            return false;
        } catch (Exception e) {
            log.error("处理评价失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Integer countFeedback(Integer type, Integer status) {
        try {
            return feedbackMapper.countByType(type, status);
        } catch (Exception e) {
            log.error("统计评价数量失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public Integer countFeedbackByUserId(Long userId, Integer status) {
        try {
            return feedbackMapper.countByUserId(userId, status);
        } catch (Exception e) {
            log.error("统计用户评价数量失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public Integer countFeedbackBySessionId(String sessionId, Integer status) {
        try {
            return feedbackMapper.countBySessionId(sessionId, status);
        } catch (Exception e) {
            log.error("统计会话评价数量失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public Double calculateAverageScore(Integer type, Integer status) {
        try {
            return feedbackMapper.calculateAverageScore(type, status);
        } catch (Exception e) {
            log.error("计算平均评分失败: {}", e.getMessage(), e);
            return 0.0;
        }
    }

    @Override
    public List<Feedback> getRecentFeedback(int limit, Integer status) {
        try {
            return feedbackMapper.selectRecentFeedback(limit, status);
        } catch (Exception e) {
            log.error("获取最近的评价失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public int batchCreateFeedback(List<Feedback> feedbacks) {
        try {
            for (Feedback feedback : feedbacks) {
                feedback.setFeedbackId(UUID.randomUUID().toString());
                feedback.setStatus(1); // 已提交
                feedback.setDeleted(0);
                feedback.setCreateTime(LocalDateTime.now());
                feedback.setUpdateTime(LocalDateTime.now());
            }
            return feedbackMapper.batchInsert(feedbacks);
        } catch (Exception e) {
            log.error("批量创建评价失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public Map<String, Object> getFeedbackStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 统计总评价数量
            int total = feedbackMapper.countByType(null, 1);
            statistics.put("total", total);
            
            // 统计各类型评价数量
            int satisfied = feedbackMapper.countByType(1, 1);
            int dissatisfied = feedbackMapper.countByType(2, 1);
            int neutral = feedbackMapper.countByType(3, 1);
            statistics.put("satisfied", satisfied);
            statistics.put("dissatisfied", dissatisfied);
            statistics.put("neutral", neutral);
            
            // 计算平均评分
            Double averageScore = feedbackMapper.calculateAverageScore(null, 1);
            statistics.put("averageScore", averageScore != null ? averageScore : 0.0);
            
            // 统计已处理和未处理的评价数量
            int processed = feedbackMapper.countByType(null, 2);
            int unprocessed = feedbackMapper.countByType(null, 1);
            statistics.put("processed", processed);
            statistics.put("unprocessed", unprocessed);
            
            return statistics;
        } catch (Exception e) {
            log.error("获取评价统计信息失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }
}
