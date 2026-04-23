package com.anti.fraud.modules.exercise.mapper;

import com.anti.fraud.modules.exercise.entity.ExerciseRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 演练记录Mapper
 */
@Mapper
public interface ExerciseRecordMapper extends BaseMapper<ExerciseRecord> {

    /**
     * 根据条件查询演练记录列表
     * @param params 查询参数
     * @param page 页码
     * @param size 每页大小
     * @return 演练记录列表
     */
    List<ExerciseRecord> selectByCondition(@Param("params") Map<String, Object> params, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据条件查询演练记录总数
     * @param params 查询参数
     * @return 演练记录总数
     */
    Integer selectCountByCondition(@Param("params") Map<String, Object> params);

    /**
     * 根据用户ID查询演练记录
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 演练记录列表
     */
    List<ExerciseRecord> selectByUserId(@Param("userId") Long userId, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据用户ID查询演练记录总数
     * @param userId 用户ID
     * @return 演练记录总数
     */
    Integer selectCountByUserId(@Param("userId") Long userId);

    /**
     * 获取用户演练统计信息
     * @param userId 用户ID
     * @return 统计信息
     */
    Map<String, Object> selectUserExerciseStats(@Param("userId") Long userId);

    /**
     * 获取用户最近的演练记录
     * @param userId 用户ID
     * @param limit 数量限制
     * @return 演练记录列表
     */
    List<ExerciseRecord> selectLatestByUserId(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 获取用户最佳成绩
     * @param userId 用户ID
     * @param exerciseType 演练类型
     * @return 最佳成绩
     */
    ExerciseRecord selectBestScoreByUserId(@Param("userId") Long userId, @Param("exerciseType") Integer exerciseType);

    /**
     * 按演练类型统计演练记录
     * @return 类型统计
     */
    List<Map<String, Object>> selectTypeStats();

    /**
     * 按用户统计演练记录
     * @param limit 数量限制
     * @return 用户统计
     */
    List<Map<String, Object>> selectUserStats(@Param("limit") Integer limit);

    /**
     * 获取演练记录趋势
     * @param userId 用户ID
     * @param days 天数
     * @return 趋势数据
     */
    List<Map<String, Object>> selectTrendByUserId(@Param("userId") Long userId, @Param("days") Integer days);
}
