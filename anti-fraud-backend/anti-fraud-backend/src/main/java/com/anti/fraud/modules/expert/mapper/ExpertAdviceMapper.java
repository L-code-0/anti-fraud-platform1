package com.anti.fraud.modules.expert.mapper;

import com.anti.fraud.modules.expert.entity.ExpertAdvice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * 专家建议 Mapper
 */
@Mapper
public interface ExpertAdviceMapper extends BaseMapper<ExpertAdvice> {

    /**
     * 分页查询专家建议
     */
    IPage<Map<String, Object>> selectAdvicePage(
            Page<Map<String, Object>> page,
            @Param("expertId") Long expertId,
            @Param("category") String category
    );

    /**
     * 统计专家发布的建议数
     */
    @Select("SELECT COUNT(*) FROM expert_advice WHERE expert_id = #{expertId} AND deleted = 0")
    int countByExpertId(@Param("expertId") Long expertId);

    /**
     * 统计专家建议的总阅读量
     */
    @Select("SELECT IFNULL(SUM(view_count), 0) FROM expert_advice WHERE expert_id = #{expertId} AND deleted = 0")
    long sumViewCountByExpertId(@Param("expertId") Long expertId);

    /**
     * 统计专家建议的总点赞数
     */
    @Select("SELECT IFNULL(SUM(thumb_count), 0) FROM expert_advice WHERE expert_id = #{expertId} AND deleted = 0")
    long sumThumbCountByExpertId(@Param("expertId") Long expertId);
}
