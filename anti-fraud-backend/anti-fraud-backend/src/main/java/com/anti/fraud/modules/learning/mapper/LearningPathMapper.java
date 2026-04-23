package com.anti.fraud.modules.learning.mapper;

import com.anti.fraud.modules.learning.entity.LearningPath;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 学习路径Mapper
 */
@Mapper
public interface LearningPathMapper extends BaseMapper<LearningPath> {

    /**
     * 根据目标用户类型查询学习路径
     * @param targetUserType 目标用户类型
     * @param page 页码
     * @param size 每页大小
     * @return 学习路径列表
     */
    List<LearningPath> selectByTargetUserType(@Param("targetUserType") Integer targetUserType, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据难度查询学习路径
     * @param difficulty 难度等级
     * @param page 页码
     * @param size 每页大小
     * @return 学习路径列表
     */
    List<LearningPath> selectByDifficulty(@Param("difficulty") Integer difficulty, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 按目标用户类型统计学习路径数量
     * @return 类型统计
     */
    List<Map<String, Object>> selectTargetUserTypeStats();

    /**
     * 按难度统计学习路径数量
     * @return 难度统计
     */
    List<Map<String, Object>> selectDifficultyStats();

    /**
     * 增加浏览量
     * @param id 学习路径ID
     */
    void incrementViewCount(@Param("id") Long id);

    /**
     * 增加完成人数
     * @param id 学习路径ID
     */
    void incrementCompletionCount(@Param("id") Long id);

    /**
     * 更新平均评分
     * @param id 学习路径ID
     * @param score 新评分
     */
    void updateAverageScore(@Param("id") Long id, @Param("score") Double score);

    /**
     * 获取热门学习路径
     * @param limit 数量限制
     * @return 学习路径列表
     */
    List<LearningPath> selectHotLearningPaths(@Param("limit") Integer limit);

    /**
     * 获取推荐学习路径
     * @param userId 用户ID
     * @param limit 数量限制
     * @return 学习路径列表
     */
    List<LearningPath> selectRecommendedLearningPaths(@Param("userId") Long userId, @Param("limit") Integer limit);
}
