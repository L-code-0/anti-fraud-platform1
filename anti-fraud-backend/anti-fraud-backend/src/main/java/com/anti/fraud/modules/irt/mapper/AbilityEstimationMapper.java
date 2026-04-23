package com.anti.fraud.modules.irt.mapper;

import com.anti.fraud.modules.irt.entity.AbilityEstimation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户能力估计Mapper
 */
@Mapper
public interface AbilityEstimationMapper extends BaseMapper<AbilityEstimation> {

    /**
     * 根据估计ID查询能力估计
     * @param estimationId 估计ID
     * @return 能力估计
     */
    AbilityEstimation selectByEstimationId(@Param("estimationId") String estimationId);

    /**
     * 根据用户ID查询能力估计列表
     * @param userId 用户ID
     * @param testStatus 测试状态
     * @param page 页码
     * @param size 每页大小
     * @return 能力估计列表
     */
    List<AbilityEstimation> selectByUserId(@Param("userId") Long userId, @Param("testStatus") Integer testStatus, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据测试ID查询能力估计
     * @param testId 测试ID
     * @return 能力估计
     */
    AbilityEstimation selectByTestId(@Param("testId") String testId);

    /**
     * 根据模型ID查询能力估计列表
     * @param modelId 模型ID
     * @param testStatus 测试状态
     * @param page 页码
     * @param size 每页大小
     * @return 能力估计列表
     */
    List<AbilityEstimation> selectByModelId(@Param("modelId") String modelId, @Param("testStatus") Integer testStatus, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 更新能力估计状态
     * @param id 能力估计ID
     * @param testStatus 测试状态
     * @return 影响行数
     */
    int updateTestStatus(@Param("id") Long id, @Param("testStatus") Integer testStatus);

    /**
     * 更新能力估计值
     * @param id 能力估计ID
     * @param ability 能力值
     * @param abilityStd 能力估计标准差
     * @param answeredCount 已答题数量
     * @param correctCount 正确答题数量
     * @param estimationTime 估计时间
     * @return 影响行数
     */
    int updateAbility(@Param("id") Long id, @Param("ability") Double ability, @Param("abilityStd") Double abilityStd, @Param("answeredCount") Integer answeredCount, @Param("correctCount") Integer correctCount, @Param("estimationTime") LocalDateTime estimationTime);

    /**
     * 统计能力估计数量
     * @param userId 用户ID
     * @param testStatus 测试状态
     * @return 能力估计数量
     */
    Integer countByUserId(@Param("userId") Long userId, @Param("testStatus") Integer testStatus);

    /**
     * 获取用户最新的能力估计
     * @param userId 用户ID
     * @param modelId 模型ID
     * @return 能力估计
     */
    AbilityEstimation selectLatestByUserId(@Param("userId") Long userId, @Param("modelId") String modelId);

    /**
     * 获取最新的能力估计列表
     * @param limit 数量限制
     * @param modelId 模型ID
     * @param testStatus 测试状态
     * @return 能力估计列表
     */
    List<AbilityEstimation> selectRecentAbilityEstimations(@Param("limit") Integer limit, @Param("modelId") String modelId, @Param("testStatus") Integer testStatus);

    /**
     * 批量插入能力估计
     * @param abilityEstimations 能力估计列表
     * @return 插入成功数量
     */
    int batchInsert(@Param("abilityEstimations") List<AbilityEstimation> abilityEstimations);
}
