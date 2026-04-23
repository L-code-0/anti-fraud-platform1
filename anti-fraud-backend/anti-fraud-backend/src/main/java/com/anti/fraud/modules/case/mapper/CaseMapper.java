package com.anti.fraud.modules.case.mapper;

import com.anti.fraud.modules.case.entity.Case;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 案例Mapper
 */
@Mapper
public interface CaseMapper extends BaseMapper<Case> {

    /**
     * 根据条件查询案例列表
     * @param params 查询参数
     * @param page 页码
     * @param size 每页大小
     * @return 案例列表
     */
    List<Case> selectByCondition(@Param("params") Map<String, Object> params, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据条件查询案例总数
     * @param params 查询参数
     * @return 案例总数
     */
    Integer selectCountByCondition(@Param("params") Map<String, Object> params);

    /**
     * 根据案例类型查询案例
     * @param type 案例类型
     * @param page 页码
     * @param size 每页大小
     * @return 案例列表
     */
    List<Case> selectByType(@Param("type") Integer type, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 增加浏览量
     * @param id 案例ID
     */
    void increaseViewCount(@Param("id") Long id);

    /**
     * 增加点赞数
     * @param id 案例ID
     */
    void increaseLikeCount(@Param("id") Long id);

    /**
     * 增加评论数
     * @param id 案例ID
     */
    void increaseCommentCount(@Param("id") Long id);

    /**
     * 增加分享数
     * @param id 案例ID
     */
    void increaseShareCount(@Param("id") Long id);

    /**
     * 获取热门案例
     * @param limit 数量限制
     * @return 热门案例列表
     */
    List<Case> selectHotCases(@Param("limit") Integer limit);

    /**
     * 获取最新案例
     * @param limit 数量限制
     * @return 最新案例列表
     */
    List<Case> selectLatestCases(@Param("limit") Integer limit);

    /**
     * 按案例类型统计案例数量
     * @return 类型统计
     */
    List<Map<String, Object>> selectTypeStats();

    /**
     * 搜索案例
     * @param keyword 关键词
     * @param page 页码
     * @param size 每页大小
     * @return 搜索结果
     */
    List<Case> selectByKeyword(@Param("keyword") String keyword, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 搜索案例总数
     * @param keyword 关键词
     * @return 搜索结果总数
     */
    Integer selectCountByKeyword(@Param("keyword") String keyword);
}
