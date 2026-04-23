package com.anti.fraud.modules.exercise.service;

import com.anti.fraud.modules.exercise.entity.ExerciseEvent;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 演练事件服务接口
 */
public interface ExerciseEventService extends IService<ExerciseEvent> {

    /**
     * 创建演练事件
     * @param exerciseEvent 演练事件信息
     * @return 事件ID
     */
    String createExerciseEvent(ExerciseEvent exerciseEvent);

    /**
     * 更新演练事件
     * @param exerciseEvent 演练事件信息
     * @return 是否成功
     */
    boolean updateExerciseEvent(ExerciseEvent exerciseEvent);

    /**
     * 删除演练事件
     * @param eventId 事件ID
     * @return 是否成功
     */
    boolean deleteExerciseEvent(String eventId);

    /**
     * 获取演练事件详情
     * @param eventId 事件ID
     * @return 演练事件详情
     */
    ExerciseEvent getExerciseEventByEventId(String eventId);

    /**
     * 分页查询演练事件列表
     * @param eventType 事件类型
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 演练事件列表和总数
     */
    Map<String, Object> getExerciseEventList(Integer eventType, Integer status, int page, int size);

    /**
     * 根据演练ID查询事件列表
     * @param exerciseId 演练ID
     * @param eventType 事件类型
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 事件列表和总数
     */
    Map<String, Object> getExerciseEventListByExerciseId(String exerciseId, Integer eventType, Integer status, int page, int size);

    /**
     * 根据用户ID查询事件列表
     * @param userId 用户ID
     * @param eventType 事件类型
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 事件列表和总数
     */
    Map<String, Object> getExerciseEventListByUserId(Long userId, Integer eventType, Integer status, int page, int size);

    /**
     * 根据时间范围查询事件列表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param eventType 事件类型
     * @param status 状态
     * @return 事件列表
     */
    List<ExerciseEvent> getExerciseEventListByTimeRange(LocalDateTime startTime, LocalDateTime endTime, Integer eventType, Integer status);

    /**
     * 更新事件状态
     * @param eventId 事件ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateExerciseEventStatus(String eventId, Integer status);

    /**
     * 统计事件数量
     * @param eventType 事件类型
     * @param status 状态
     * @return 事件数量
     */
    Integer countExerciseEvent(Integer eventType, Integer status);

    /**
     * 统计用户事件数量
     * @param userId 用户ID
     * @param eventType 事件类型
     * @param status 状态
     * @return 事件数量
     */
    Integer countExerciseEventByUserId(Long userId, Integer eventType, Integer status);

    /**
     * 统计演练事件数量
     * @param exerciseId 演练ID
     * @param eventType 事件类型
     * @param status 状态
     * @return 事件数量
     */
    Integer countExerciseEventByExerciseId(String exerciseId, Integer eventType, Integer status);

    /**
     * 获取最近的事件
     * @param limit 数量限制
     * @param eventType 事件类型
     * @param status 状态
     * @return 事件列表
     */
    List<ExerciseEvent> getRecentExerciseEvents(int limit, Integer eventType, Integer status);

    /**
     * 批量创建演练事件
     * @param events 事件列表
     * @return 成功创建的数量
     */
    int batchCreateExerciseEvent(List<ExerciseEvent> events);

    /**
     * 获取事件统计信息
     * @return 统计信息
     */
    Map<String, Object> getExerciseEventStatistics();

    /**
     * 处理演练事件
     * @param eventId 事件ID
     * @return 是否成功
     */
    boolean processExerciseEvent(String eventId);

    /**
     * 记录开始演练事件
     * @param exerciseId 演练ID
     * @param userId 用户ID
     * @param username 用户名
     * @return 事件ID
     */
    String recordStartExerciseEvent(String exerciseId, Long userId, String username);

    /**
     * 记录结束演练事件
     * @param exerciseId 演练ID
     * @param userId 用户ID
     * @param username 用户名
     * @param score 分数
     * @param duration 持续时间（秒）
     * @return 事件ID
     */
    String recordEndExerciseEvent(String exerciseId, Long userId, String username, Integer score, Integer duration);

    /**
     * 记录选择答案事件
     * @param exerciseId 演练ID
     * @param userId 用户ID
     * @param username 用户名
     * @param questionId 题目ID
     * @param selectedAnswer 选择的答案
     * @param correctAnswer 正确答案
     * @param isCorrect 是否正确
     * @return 事件ID
     */
    String recordSelectAnswerEvent(String exerciseId, Long userId, String username, String questionId, String selectedAnswer, String correctAnswer, Boolean isCorrect);

    /**
     * 记录查看解析事件
     * @param exerciseId 演练ID
     * @param userId 用户ID
     * @param username 用户名
     * @param questionId 题目ID
     * @return 事件ID
     */
    String recordViewExplanationEvent(String exerciseId, Long userId, String username, String questionId);

    /**
     * 记录获得分数事件
     * @param exerciseId 演练ID
     * @param userId 用户ID
     * @param username 用户名
     * @param score 分数
     * @param type 分数类型：1-题目得分，2-演练得分
     * @return 事件ID
     */
    String recordScoreEvent(String exerciseId, Long userId, String username, Integer score, Integer type);
}
