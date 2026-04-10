package com.anti.fraud.modules.analysis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.anti.fraud.modules.analysis.entity.LearningWeakness;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface LearningWeaknessMapper extends BaseMapper<LearningWeakness> {
    
    /**
     * 查询用户的薄弱点列表
     * @param userId 用户ID
     * @param threshold 正确率阈值（低于此值视为薄弱）
     * @return 薄弱点列表
     */
    @Select("SELECT * FROM learning_weakness WHERE user_id = #{userId} AND correct_rate < #{threshold} ORDER BY correct_rate ASC")
    List<LearningWeakness> selectWeaknessesByUserId(@Param("userId") Long userId, @Param("threshold") Double threshold);
    
    /**
     * 获取用户的分类掌握情况
     * @param userId 用户ID
     * @return 分类掌握情况列表
     */
    @Select("SELECT category, category_name as categoryName, total_questions as totalQuestions, " +
            "wrong_questions as wrongQuestions, correct_rate as correctRate, " +
            "CASE WHEN correct_rate >= 0.9 THEN '精通' WHEN correct_rate >= 0.7 THEN '熟练' " +
            "WHEN correct_rate >= 0.5 THEN '一般' ELSE '薄弱' END as masteryLevel " +
            "FROM learning_weakness WHERE user_id = #{userId} ORDER BY correct_rate DESC")
    List<Map<String, Object>> selectCategoryMastery(@Param("userId") Long userId);
    
}
