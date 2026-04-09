package com.anti.fraud.modules.expert.mapper;

import com.anti.fraud.modules.expert.entity.ExpertAnalysis;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * 专家案例分析 Mapper
 */
@Mapper
public interface ExpertAnalysisMapper extends BaseMapper<ExpertAnalysis> {

    /**
     * 分页查询案例分析
     */
    IPage<Map<String, Object>> selectAnalysisPage(
            Page<Map<String, Object>> page,
            @Param("expertId") Long expertId,
            @Param("type") Integer type
    );

    /**
     * 统计专家发布的案例分析数
     */
    @Select("SELECT COUNT(*) FROM expert_analysis WHERE expert_id = #{expertId} AND deleted = 0")
    int countByExpertId(@Param("expertId") Long expertId);

    /**
     * 统计专家案例分析的总阅读量
     */
    @Select("SELECT IFNULL(SUM(view_count), 0) FROM expert_analysis WHERE expert_id = #{expertId} AND deleted = 0")
    long sumViewCountByExpertId(@Param("expertId") Long expertId);

    /**
     * 统计专家案例分析的总点赞数
     */
    @Select("SELECT IFNULL(SUM(thumb_count), 0) FROM expert_analysis WHERE expert_id = #{expertId} AND deleted = 0")
    long sumThumbCountByExpertId(@Param("expertId") Long expertId);
}
