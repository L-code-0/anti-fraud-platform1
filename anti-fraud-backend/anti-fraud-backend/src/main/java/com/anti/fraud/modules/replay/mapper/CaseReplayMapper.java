package com.anti.fraud.modules.replay.mapper;

import com.anti.fraud.modules.replay.entity.CaseReplay;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 案例回放Mapper
 */
@Mapper
public interface CaseReplayMapper extends BaseMapper<CaseReplay> {

    /**
     * 根据条件查询案例回放列表
     * @param params 查询参数
     * @param page 页码
     * @param size 每页大小
     * @return 案例回放列表
     */
    List<CaseReplay> selectByCondition(@Param("params") Map<String, Object> params, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据条件查询案例回放总数
     * @param params 查询参数
     * @return 案例回放总数
     */
    Integer selectCountByCondition(@Param("params") Map<String, Object> params);

    /**
     * 根据案例ID查询案例回放
     * @param caseId 案例ID
     * @return 案例回放列表
     */
    List<CaseReplay> selectByCaseId(@Param("caseId") Long caseId);

    /**
     * 增加浏览量
     * @param id 案例回放ID
     */
    void increaseViewCount(@Param("id") Long id);

    /**
     * 增加点赞数
     * @param id 案例回放ID
     */
    void increaseLikeCount(@Param("id") Long id);

    /**
     * 增加分享数
     * @param id 案例回放ID
     */
    void increaseShareCount(@Param("id") Long id);

    /**
     * 获取热门案例回放
     * @param limit 数量限制
     * @return 热门案例回放列表
     */
    List<CaseReplay> selectHotReplays(@Param("limit") Integer limit);

    /**
     * 获取最新案例回放
     * @param limit 数量限制
     * @return 最新案例回放列表
     */
    List<CaseReplay> selectLatestReplays(@Param("limit") Integer limit);

    /**
     * 按类型统计案例回放数量
     * @return 类型统计
     */
    List<Map<String, Object>> selectTypeStats();
}
