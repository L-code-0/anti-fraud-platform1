package com.anti.fraud.modules.exercise.service.impl;

import com.anti.fraud.modules.exercise.entity.ExerciseEvent;
import com.anti.fraud.modules.exercise.mapper.ExerciseEventMapper;
import com.anti.fraud.modules.exercise.service.ExerciseEventService;
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
 * 演练事件服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ExerciseEventServiceImpl extends ServiceImpl<ExerciseEventMapper, ExerciseEvent> implements ExerciseEventService {

    private final ExerciseEventMapper exerciseEventMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public String createExerciseEvent(ExerciseEvent exerciseEvent) {
        try {
            // 生成事件ID
            String eventId = UUID.randomUUID().toString();
            exerciseEvent.setEventId(eventId);
            exerciseEvent.setEventTime(LocalDateTime.now());
            exerciseEvent.setStatus(2); // 处理中
            exerciseEvent.setDeleted(0);
            exerciseEvent.setCreateTime(LocalDateTime.now());
            exerciseEvent.setUpdateTime(LocalDateTime.now());

            boolean success = save(exerciseEvent);
            if (success) {
                // 异步处理事件
                processExerciseEventAsync(exerciseEvent);
                return eventId;
            } else {
                throw new RuntimeException("创建演练事件失败");
            }
        } catch (Exception e) {
            log.error("创建演练事件失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建演练事件失败");
        }
    }

    @Override
    @Transactional
    public boolean updateExerciseEvent(ExerciseEvent exerciseEvent) {
        try {
            exerciseEvent.setUpdateTime(LocalDateTime.now());
            return updateById(exerciseEvent);
        } catch (Exception e) {
            log.error("更新演练事件失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteExerciseEvent(String eventId) {
        try {
            ExerciseEvent event = exerciseEventMapper.selectByEventId(eventId);
            if (event != null) {
                event.setDeleted(1);
                event.setUpdateTime(LocalDateTime.now());
                return updateById(event);
            }
            return false;
        } catch (Exception e) {
            log.error("删除演练事件失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public ExerciseEvent getExerciseEventByEventId(String eventId) {
        try {
            return exerciseEventMapper.selectByEventId(eventId);
        } catch (Exception e) {
            log.error("获取演练事件详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getExerciseEventList(Integer eventType, Integer status, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<ExerciseEvent> events = exerciseEventMapper.selectByEventType(eventType, status, offset, size);
            // 计算总数
            int total = exerciseEventMapper.countByEventType(eventType, status);

            Map<String, Object> result = new HashMap<>();
            result.put("list", events);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("查询演练事件列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public Map<String, Object> getExerciseEventListByExerciseId(String exerciseId, Integer eventType, Integer status, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<ExerciseEvent> events = exerciseEventMapper.selectByExerciseId(exerciseId, eventType, status, offset, size);
            // 计算总数
            int total = exerciseEventMapper.countByExerciseId(exerciseId, eventType, status);

            Map<String, Object> result = new HashMap<>();
            result.put("list", events);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("根据演练ID查询事件列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public Map<String, Object> getExerciseEventListByUserId(Long userId, Integer eventType, Integer status, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<ExerciseEvent> events = exerciseEventMapper.selectByUserId(userId, eventType, status, offset, size);
            // 计算总数
            int total = exerciseEventMapper.countByUserId(userId, eventType, status);

            Map<String, Object> result = new HashMap<>();
            result.put("list", events);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("根据用户ID查询事件列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public List<ExerciseEvent> getExerciseEventListByTimeRange(LocalDateTime startTime, LocalDateTime endTime, Integer eventType, Integer status) {
        try {
            return exerciseEventMapper.selectByTimeRange(startTime, endTime, eventType, status);
        } catch (Exception e) {
            log.error("根据时间范围查询事件列表失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public boolean updateExerciseEventStatus(String eventId, Integer status) {
        try {
            ExerciseEvent event = exerciseEventMapper.selectByEventId(eventId);
            if (event != null) {
                return exerciseEventMapper.updateStatus(event.getId(), status) > 0;
            }
            return false;
        } catch (Exception e) {
            log.error("更新事件状态失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Integer countExerciseEvent(Integer eventType, Integer status) {
        try {
            return exerciseEventMapper.countByEventType(eventType, status);
        } catch (Exception e) {
            log.error("统计事件数量失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public Integer countExerciseEventByUserId(Long userId, Integer eventType, Integer status) {
        try {
            return exerciseEventMapper.countByUserId(userId, eventType, status);
        } catch (Exception e) {
            log.error("统计用户事件数量失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public Integer countExerciseEventByExerciseId(String exerciseId, Integer eventType, Integer status) {
        try {
            return exerciseEventMapper.countByExerciseId(exerciseId, eventType, status);
        } catch (Exception e) {
            log.error("统计演练事件数量失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public List<ExerciseEvent> getRecentExerciseEvents(int limit, Integer eventType, Integer status) {
        try {
            return exerciseEventMapper.selectRecentEvents(limit, eventType, status);
        } catch (Exception e) {
            log.error("获取最近的事件失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public int batchCreateExerciseEvent(List<ExerciseEvent> events) {
        try {
            for (ExerciseEvent event : events) {
                event.setEventId(UUID.randomUUID().toString());
                event.setEventTime(LocalDateTime.now());
                event.setStatus(2); // 处理中
                event.setDeleted(0);
                event.setCreateTime(LocalDateTime.now());
                event.setUpdateTime(LocalDateTime.now());
            }
            int count = exerciseEventMapper.batchInsert(events);
            // 异步处理事件
            for (ExerciseEvent event : events) {
                processExerciseEventAsync(event);
            }
            return count;
        } catch (Exception e) {
            log.error("批量创建演练事件失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public Map<String, Object> getExerciseEventStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 统计总事件数量
            int total = exerciseEventMapper.countByEventType(null, 1);
            statistics.put("total", total);
            
            // 统计各类型事件数量
            int startExercise = exerciseEventMapper.countByEventType(1, 1);
            int endExercise = exerciseEventMapper.countByEventType(2, 1);
            int selectAnswer = exerciseEventMapper.countByEventType(3, 1);
            int viewExplanation = exerciseEventMapper.countByEventType(4, 1);
            int score = exerciseEventMapper.countByEventType(5, 1);
            int other = exerciseEventMapper.countByEventType(6, 1);
            statistics.put("startExercise", startExercise);
            statistics.put("endExercise", endExercise);
            statistics.put("selectAnswer", selectAnswer);
            statistics.put("viewExplanation", viewExplanation);
            statistics.put("score", score);
            statistics.put("other", other);
            
            // 统计状态分布
            int processed = exerciseEventMapper.countByEventType(null, 1);
            int processing = exerciseEventMapper.countByEventType(null, 2);
            int failed = exerciseEventMapper.countByEventType(null, 3);
            statistics.put("processed", processed);
            statistics.put("processing", processing);
            statistics.put("failed", failed);
            
            return statistics;
        } catch (Exception e) {
            log.error("获取事件统计信息失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public boolean processExerciseEvent(String eventId) {
        try {
            ExerciseEvent event = exerciseEventMapper.selectByEventId(eventId);
            if (event != null) {
                // 这里可以添加具体的事件处理逻辑
                // 例如更新演练状态、计算分数等
                updateExerciseEventStatus(eventId, 1); // 已处理
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("处理演练事件失败: {}", e.getMessage(), e);
            updateExerciseEventStatus(eventId, 3); // 处理失败
            return false;
        }
    }

    @Override
    public String recordStartExerciseEvent(String exerciseId, Long userId, String username) {
        try {
            ExerciseEvent event = new ExerciseEvent();
            event.setExerciseId(exerciseId);
            event.setUserId(userId);
            event.setUsername(username);
            event.setEventType(1); // 开始演练
            event.setContent("{\"action\": \"start_exercise\", \"exerciseId\": \"" + exerciseId + \"\", \"userId\": \"" + userId + \"\"}");
            return createExerciseEvent(event);
        } catch (Exception e) {
            log.error("记录开始演练事件失败: {}", e.getMessage(), e);
            throw new RuntimeException("记录开始演练事件失败");
        }
    }

    @Override
    public String recordEndExerciseEvent(String exerciseId, Long userId, String username, Integer score, Integer duration) {
        try {
            ExerciseEvent event = new ExerciseEvent();
            event.setExerciseId(exerciseId);
            event.setUserId(userId);
            event.setUsername(username);
            event.setEventType(2); // 结束演练
            event.setContent("{\"action\": \"end_exercise\", \"exerciseId\": \"" + exerciseId + \"\", \"userId\": \"" + userId + \"\", \"score\": \"" + score + \"\", \"duration\": \"" + duration + \"\"}");
            return createExerciseEvent(event);
        } catch (Exception e) {
            log.error("记录结束演练事件失败: {}", e.getMessage(), e);
            throw new RuntimeException("记录结束演练事件失败");
        }
    }

    @Override
    public String recordSelectAnswerEvent(String exerciseId, Long userId, String username, String questionId, String selectedAnswer, String correctAnswer, Boolean isCorrect) {
        try {
            ExerciseEvent event = new ExerciseEvent();
            event.setExerciseId(exerciseId);
            event.setUserId(userId);
            event.setUsername(username);
            event.setEventType(3); // 选择答案
            event.setContent("{\"action\": \"select_answer\", \"exerciseId\": \"" + exerciseId + \"\", \"userId\": \"" + userId + \"\", \"questionId\": \"" + questionId + \"\", \"selectedAnswer\": \"" + selectedAnswer + \"\", \"correctAnswer\": \"" + correctAnswer + \"\", \"isCorrect\": \"" + isCorrect + \"\"}");
            return createExerciseEvent(event);
        } catch (Exception e) {
            log.error("记录选择答案事件失败: {}", e.getMessage(), e);
            throw new RuntimeException("记录选择答案事件失败");
        }
    }

    @Override
    public String recordViewExplanationEvent(String exerciseId, Long userId, String username, String questionId) {
        try {
            ExerciseEvent event = new ExerciseEvent();
            event.setExerciseId(exerciseId);
            event.setUserId(userId);
            event.setUsername(username);
            event.setEventType(4); // 查看解析
            event.setContent("{\"action\": \"view_explanation\", \"exerciseId\": \"" + exerciseId + \"\", \"userId\": \"" + userId + \"\", \"questionId\": \"" + questionId + \"\"}");
            return createExerciseEvent(event);
        } catch (Exception e) {
            log.error("记录查看解析事件失败: {}", e.getMessage(), e);
            throw new RuntimeException("记录查看解析事件失败");
        }
    }

    @Override
    public String recordScoreEvent(String exerciseId, Long userId, String username, Integer score, Integer type) {
        try {
            ExerciseEvent event = new ExerciseEvent();
            event.setExerciseId(exerciseId);
            event.setUserId(userId);
            event.setUsername(username);
            event.setEventType(5); // 获得分数
            event.setContent("{\"action\": \"score\", \"exerciseId\": \"" + exerciseId + \"\", \"userId\": \"" + userId + \"\", \"score\": \"" + score + \"\", \"type\": \"" + type + \"\"}");
            return createExerciseEvent(event);
        } catch (Exception e) {
            log.error("记录获得分数事件失败: {}", e.getMessage(), e);
            throw new RuntimeException("记录获得分数事件失败");
        }
    }

    /**
     * 异步处理演练事件
     * @param exerciseEvent 演练事件信息
     */
    private void processExerciseEventAsync(ExerciseEvent exerciseEvent) {
        // 这里简化处理，实际应该使用线程池或消息队列进行异步处理
        new Thread(() -> {
            try {
                processExerciseEvent(exerciseEvent.getEventId());
            } catch (Exception e) {
                log.error("异步处理演练事件失败: {}", e.getMessage(), e);
                updateExerciseEventStatus(exerciseEvent.getEventId(), 3); // 处理失败
            }
        }).start();
    }
}
