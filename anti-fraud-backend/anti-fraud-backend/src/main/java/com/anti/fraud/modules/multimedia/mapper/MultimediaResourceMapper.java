package com.anti.fraud.modules.multimedia.mapper;

import com.anti.fraud.modules.multimedia.entity.MultimediaResource;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 多媒体资源Mapper
 */
@Mapper
public interface MultimediaResourceMapper extends BaseMapper<MultimediaResource> {

    /**
     * 根据条件查询多媒体资源列表
     * @param params 查询参数
     * @param page 页码
     * @param size 每页大小
     * @return 多媒体资源列表
     */
    List<MultimediaResource> selectByCondition(@Param("params") Map<String, Object> params, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据条件查询多媒体资源总数
     * @param params 查询参数
     * @return 多媒体资源总数
     */
    Integer selectCountByCondition(@Param("params") Map<String, Object> params);

    /**
     * 根据资源类型查询多媒体资源
     * @param type 资源类型
     * @param page 页码
     * @param size 每页大小
     * @return 多媒体资源列表
     */
    List<MultimediaResource> selectByType(@Param("type") Integer type, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据知识点查询多媒体资源
     * @param knowledgePoint 知识点
     * @param page 页码
     * @param size 每页大小
     * @return 多媒体资源列表
     */
    List<MultimediaResource> selectByKnowledgePoint(@Param("knowledgePoint") String knowledgePoint, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 增加浏览量
     * @param id 资源ID
     */
    void increaseViewCount(@Param("id") Long id);

    /**
     * 增加点赞数
     * @param id 资源ID
     */
    void increaseLikeCount(@Param("id") Long id);

    /**
     * 增加分享数
     * @param id 资源ID
     */
    void increaseShareCount(@Param("id") Long id);

    /**
     * 获取热门多媒体资源
     * @param limit 数量限制
     * @return 热门多媒体资源列表
     */
    List<MultimediaResource> selectHotResources(@Param("limit") Integer limit);

    /**
     * 获取最新多媒体资源
     * @param limit 数量限制
     * @return 最新多媒体资源列表
     */
    List<MultimediaResource> selectLatestResources(@Param("limit") Integer limit);

    /**
     * 按资源类型统计多媒体资源数量
     * @return 类型统计
     */
    List<Map<String, Object>> selectTypeStats();

    /**
     * 按知识点统计多媒体资源数量
     * @return 知识点统计
     */
    List<Map<String, Object>> selectKnowledgePointStats();
}
