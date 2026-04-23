package com.anti.fraud.modules.community.mapper;

import com.anti.fraud.modules.community.entity.CommunityPost;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 社区帖子Mapper
 */
@Mapper
public interface CommunityPostMapper extends BaseMapper<CommunityPost> {

    /**
     * 根据条件查询帖子列表
     * @param params 查询参数
     * @param page 页码
     * @param size 每页大小
     * @return 帖子列表
     */
    List<CommunityPost> selectByCondition(@Param("params") Map<String, Object> params, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据条件查询帖子总数
     * @param params 查询参数
     * @return 帖子总数
     */
    Integer selectCountByCondition(@Param("params") Map<String, Object> params);

    /**
     * 根据帖子类型查询帖子
     * @param postType 帖子类型
     * @param page 页码
     * @param size 每页大小
     * @return 帖子列表
     */
    List<CommunityPost> selectByPostType(@Param("postType") Integer postType, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 增加浏览量
     * @param id 帖子ID
     */
    void increaseViewCount(@Param("id") Long id);

    /**
     * 增加点赞数
     * @param id 帖子ID
     */
    void increaseLikeCount(@Param("id") Long id);

    /**
     * 增加评论数
     * @param id 帖子ID
     */
    void increaseCommentCount(@Param("id") Long id);

    /**
     * 增加分享数
     * @param id 帖子ID
     */
    void increaseShareCount(@Param("id") Long id);

    /**
     * 获取热门帖子
     * @param limit 数量限制
     * @return 热门帖子列表
     */
    List<CommunityPost> selectHotPosts(@Param("limit") Integer limit);

    /**
     * 获取最新帖子
     * @param limit 数量限制
     * @return 最新帖子列表
     */
    List<CommunityPost> selectLatestPosts(@Param("limit") Integer limit);

    /**
     * 按帖子类型统计帖子数量
     * @return 类型统计
     */
    List<Map<String, Object>> selectPostTypeStats();

    /**
     * 搜索帖子
     * @param keyword 关键词
     * @param page 页码
     * @param size 每页大小
     * @return 搜索结果
     */
    List<CommunityPost> selectByKeyword(@Param("keyword") String keyword, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 搜索帖子总数
     * @param keyword 关键词
     * @return 搜索结果总数
     */
    Integer selectCountByKeyword(@Param("keyword") String keyword);
}
