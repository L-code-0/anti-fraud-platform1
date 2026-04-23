package com.anti.fraud.modules.community.service;

import com.anti.fraud.modules.community.entity.CommunityPost;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 社区帖子服务接口
 */
public interface CommunityPostService extends IService<CommunityPost> {

    /**
     * 新增帖子
     * @param post 帖子信息
     * @return 是否成功
     */
    boolean addPost(CommunityPost post);

    /**
     * 更新帖子
     * @param post 帖子信息
     * @return 是否成功
     */
    boolean updatePost(CommunityPost post);

    /**
     * 删除帖子
     * @param id 帖子ID
     * @return 是否成功
     */
    boolean deletePost(Long id);

    /**
     * 获取帖子详情
     * @param id 帖子ID
     * @return 帖子详情
     */
    CommunityPost getPostById(Long id);

    /**
     * 分页查询帖子
     * @param params 查询参数
     * @param page 页码
     * @param size 每页大小
     * @return 帖子列表和总数
     */
    Map<String, Object> getPostList(Map<String, Object> params, int page, int size);

    /**
     * 根据帖子类型查询帖子
     * @param postType 帖子类型
     * @param page 页码
     * @param size 每页大小
     * @return 帖子列表和总数
     */
    Map<String, Object> getPostsByType(Integer postType, int page, int size);

    /**
     * 增加浏览量
     * @param id 帖子ID
     * @return 是否成功
     */
    boolean increaseViewCount(Long id);

    /**
     * 增加点赞数
     * @param id 帖子ID
     * @return 是否成功
     */
    boolean increaseLikeCount(Long id);

    /**
     * 增加评论数
     * @param id 帖子ID
     * @return 是否成功
     */
    boolean increaseCommentCount(Long id);

    /**
     * 增加分享数
     * @param id 帖子ID
     * @return 是否成功
     */
    boolean increaseShareCount(Long id);

    /**
     * 获取热门帖子
     * @param limit 数量限制
     * @return 热门帖子列表
     */
    List<CommunityPost> getHotPosts(int limit);

    /**
     * 获取最新帖子
     * @param limit 数量限制
     * @return 最新帖子列表
     */
    List<CommunityPost> getLatestPosts(int limit);

    /**
     * 统计帖子信息
     * @return 统计信息
     */
    Map<String, Object> getPostStats();

    /**
     * 搜索帖子
     * @param keyword 关键词
     * @param page 页码
     * @param size 每页大小
     * @return 搜索结果
     */
    Map<String, Object> searchPosts(String keyword, int page, int size);
}
