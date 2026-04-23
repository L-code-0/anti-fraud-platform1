package com.anti.fraud.modules.exercise.service;

import com.anti.fraud.modules.exercise.entity.ExerciseRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 演练记录服务接口
 */
public interface ExerciseRecordService extends IService<ExerciseRecord> {

    /**
     * 新增演练记录
     * @param exerciseRecord 演练记录信息
     * @return 是否成功
     */
    boolean addExerciseRecord(ExerciseRecord exerciseRecord);

    /**
     * 更新演练记录
     * @param exerciseRecord 演练记录信息
     * @return 是否成功
     */
    boolean updateExerciseRecord(ExerciseRecord exerciseRecord);

    /**
     * 删除演练记录
     * @param id 演练记录ID
     * @return 是否成功
     */
    boolean deleteExerciseRecord(Long id);

    /**
     * 获取演练记录详情
     * @param id 演练记录ID
     * @return 演练记录详情
     */
    ExerciseRecord getExerciseRecordById(Long id);

    /**
     * 分页查询演练记录
     * @param params 查询参数
     * @param page 页码
     * @param size 每页大小
     * @return 演练记录列表和总数
     */
    Map<String, Object> getExerciseRecordList(Map<String, Object> params, int page, int size);

    /**
     * 根据用户ID查询演练记录
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 演练记录列表和总数
     */
    Map<String, Object> getExerciseRecordsByUserId(Long userId, int page, int size);

    /**
     * 获取用户演练统计信息
     * @param userId 用户ID
     * @return 统计信息
     */
    Map<String, Object> getUserExerciseStats(Long userId);

    /**
     * 获取用户最近的演练记录
     * @param userId 用户ID
     * @param limit 数量限制
     * @return 演练记录列表
     */
    List<ExerciseRecord> getLatestExerciseRecordsByUserId(Long userId, int limit);

    /**
     * 获取用户最佳成绩
     * @param userId 用户ID
     * @param exerciseType 演练类型
     * @return 最佳成绩
     */
    ExerciseRecord getBestScoreByUserId(Long userId, Integer exerciseType);

    /**
     * 统计演练记录信息
     * @return 统计信息
     */
    Map<String, Object> getExerciseRecordStats();

    /**
     * 获取演练记录趋势
     * @param userId 用户ID
     * @param days 天数
     * @return 趋势数据
     */
    List<Map<String, Object>> getExerciseRecordTrend(Long userId, int days);

    /**
     * 对比两次演练记录
     * @param recordId1 第一次演练记录ID
     * @param recordId2 第二次演练记录ID
     * @return 对比结果
     */
    Map<String, Object> compareExerciseRecords(Long recordId1, Long recordId2);

    /**
     * 分析用户演练表现
     * @param userId 用户ID
     * @param days 天数
     * @return 分析结果
     */
    Map<String, Object> analyzeUserExercisePerformance(Long userId, int days);
}
