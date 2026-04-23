package com.anti.fraud.modules.learning.service;

import com.anti.fraud.modules.learning.entity.LearningPath;
import com.anti.fraud.modules.learning.entity.LearningPathNode;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 学习路径服务接口
 */
public interface LearningPathService extends IService<LearningPath> {

    /**
     * 创建学习路径
     * @param learningPath 学习路径信息
     * @return 是否成功
     */
    boolean createLearningPath(LearningPath learningPath);

    /**
     * 更新学习路径
     * @param learningPath 学习路径信息
     * @return 是否成功
     */
    boolean updateLearningPath(LearningPath learningPath);

    /**
     * 删除学习路径
     * @param id 学习路径ID
     * @return 是否成功
     */
    boolean deleteLearningPath(Long id);

    /**
     * 获取学习路径详情
     * @param id 学习路径ID
     * @return 学习路径详情
     */
    LearningPath getLearningPathById(Long id);

    /**
     * 分页查询学习路径
     * @param params 查询参数
     * @param page 页码
     * @param size 每页大小
     * @return 学习路径列表和总数
     */
    Map<String, Object> getLearningPathList(Map<String, Object> params, int page, int size);

    /**
     * 根据目标用户类型查询学习路径
     * @param targetUserType 目标用户类型
     * @param page 页码
     * @param size 每页大小
     * @return 学习路径列表和总数
     */
    Map<String, Object> getLearningPathsByTargetUserType(Integer targetUserType, int page, int size);

    /**
     * 根据难度查询学习路径
     * @param difficulty 难度等级
     * @param page 页码
     * @param size 每页大小
     * @return 学习路径列表和总数
     */
    Map<String, Object> getLearningPathsByDifficulty(Integer difficulty, int page, int size);

    /**
     * 统计学习路径信息
     * @return 统计信息
     */
    Map<String, Object> getLearningPathStats();

    /**
     * 增加浏览量
     * @param id 学习路径ID
     * @return 是否成功
     */
    boolean incrementViewCount(Long id);

    /**
     * 增加完成人数
     * @param id 学习路径ID
     * @return 是否成功
     */
    boolean incrementCompletionCount(Long id);

    /**
     * 更新平均评分
     * @param id 学习路径ID
     * @param score 新评分
     * @return 是否成功
     */
    boolean updateAverageScore(Long id, Double score);

    /**
     * 获取热门学习路径
     * @param limit 数量限制
     * @return 学习路径列表
     */
    List<LearningPath> getHotLearningPaths(int limit);

    /**
     * 获取推荐学习路径
     * @param userId 用户ID
     * @param limit 数量限制
     * @return 学习路径列表
     */
    List<LearningPath> getRecommendedLearningPaths(Long userId, int limit);

    /**
     * 添加学习路径节点
     * @param node 学习路径节点信息
     * @return 是否成功
     */
    boolean addLearningPathNode(LearningPathNode node);

    /**
     * 更新学习路径节点
     * @param node 学习路径节点信息
     * @return 是否成功
     */
    boolean updateLearningPathNode(LearningPathNode node);

    /**
     * 删除学习路径节点
     * @param id 节点ID
     * @return 是否成功
     */
    boolean deleteLearningPathNode(Long id);

    /**
     * 获取学习路径节点详情
     * @param id 节点ID
     * @return 节点详情
     */
    LearningPathNode getLearningPathNodeById(Long id);

    /**
     * 根据学习路径ID获取节点列表
     * @param pathId 学习路径ID
     * @return 节点列表
     */
    List<LearningPathNode> getLearningPathNodesByPathId(Long pathId);

    /**
     * 计算学习路径的总时长
     * @param pathId 学习路径ID
     * @return 总时长（分钟）
     */
    Integer calculateTotalDuration(Long pathId);

    /**
     * 获取学习路径的下一个节点
     * @param pathId 学习路径ID
     * @param currentNodeOrder 当前节点顺序
     * @return 下一个节点
     */
    LearningPathNode getNextNode(Long pathId, Integer currentNodeOrder);

    /**
     * 获取学习路径的前一个节点
     * @param pathId 学习路径ID
     * @param currentNodeOrder 当前节点顺序
     * @return 前一个节点
     */
    LearningPathNode getPreviousNode(Long pathId, Integer currentNodeOrder);

    /**
     * 重新排序学习路径节点
     * @param pathId 学习路径ID
     * @return 是否成功
     */
    boolean reorderNodes(Long pathId);

    /**
     * 生成个性化学习路径
     * @param userId 用户ID
     * @param targetUserType 目标用户类型
     * @param difficulty 难度等级
     * @return 学习路径
     */
    LearningPath generatePersonalizedLearningPath(Long userId, Integer targetUserType, Integer difficulty);

    /**
     * 推荐学习路径
     * @param userId 用户ID
     * @param limit 数量限制
     * @return 学习路径列表
     */
    List<LearningPath> recommendLearningPaths(Long userId, int limit);
}
