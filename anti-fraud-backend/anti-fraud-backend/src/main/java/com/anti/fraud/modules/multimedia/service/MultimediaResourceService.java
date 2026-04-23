package com.anti.fraud.modules.multimedia.service;

import com.anti.fraud.modules.multimedia.entity.MultimediaResource;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 多媒体资源服务接口
 */
public interface MultimediaResourceService extends IService<MultimediaResource> {

    /**
     * 新增多媒体资源
     * @param multimediaResource 多媒体资源信息
     * @return 是否成功
     */
    boolean addMultimediaResource(MultimediaResource multimediaResource);

    /**
     * 更新多媒体资源
     * @param multimediaResource 多媒体资源信息
     * @return 是否成功
     */
    boolean updateMultimediaResource(MultimediaResource multimediaResource);

    /**
     * 删除多媒体资源
     * @param id 多媒体资源ID
     * @return 是否成功
     */
    boolean deleteMultimediaResource(Long id);

    /**
     * 获取多媒体资源详情
     * @param id 多媒体资源ID
     * @return 多媒体资源详情
     */
    MultimediaResource getMultimediaResourceById(Long id);

    /**
     * 分页查询多媒体资源
     * @param params 查询参数
     * @param page 页码
     * @param size 每页大小
     * @return 多媒体资源列表和总数
     */
    Map<String, Object> getMultimediaResourceList(Map<String, Object> params, int page, int size);

    /**
     * 根据资源类型查询多媒体资源
     * @param type 资源类型
     * @param page 页码
     * @param size 每页大小
     * @return 多媒体资源列表和总数
     */
    Map<String, Object> getMultimediaResourcesByType(Integer type, int page, int size);

    /**
     * 根据知识点查询多媒体资源
     * @param knowledgePoint 知识点
     * @param page 页码
     * @param size 每页大小
     * @return 多媒体资源列表和总数
     */
    Map<String, Object> getMultimediaResourcesByKnowledgePoint(String knowledgePoint, int page, int size);

    /**
     * 增加浏览量
     * @param id 资源ID
     * @return 是否成功
     */
    boolean increaseViewCount(Long id);

    /**
     * 增加点赞数
     * @param id 资源ID
     * @return 是否成功
     */
    boolean increaseLikeCount(Long id);

    /**
     * 增加分享数
     * @param id 资源ID
     * @return 是否成功
     */
    boolean increaseShareCount(Long id);

    /**
     * 获取热门多媒体资源
     * @param limit 数量限制
     * @return 热门多媒体资源列表
     */
    List<MultimediaResource> getHotResources(int limit);

    /**
     * 获取最新多媒体资源
     * @param limit 数量限制
     * @return 最新多媒体资源列表
     */
    List<MultimediaResource> getLatestResources(int limit);

    /**
     * 统计多媒体资源信息
     * @return 统计信息
     */
    Map<String, Object> getMultimediaResourceStats();

    /**
     * 搜索多媒体资源
     * @param keyword 关键词
     * @param page 页码
     * @param size 每页大小
     * @return 搜索结果
     */
    Map<String, Object> searchMultimediaResources(String keyword, int page, int size);

    /**
     * 批量上传多媒体资源
     * @param resources 多媒体资源列表
     * @return 上传结果
     */
    List<MultimediaResource> batchUploadMultimediaResources(List<MultimediaResource> resources);
}
