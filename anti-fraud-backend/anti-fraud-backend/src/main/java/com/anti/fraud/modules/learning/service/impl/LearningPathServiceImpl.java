package com.anti.fraud.modules.learning.service.impl;

import com.anti.fraud.modules.learning.entity.LearningPath;
import com.anti.fraud.modules.learning.entity.LearningPathNode;
import com.anti.fraud.modules.learning.mapper.LearningPathMapper;
import com.anti.fraud.modules.learning.mapper.LearningPathNodeMapper;
import com.anti.fraud.modules.learning.service.LearningPathService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学习路径服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LearningPathServiceImpl extends ServiceImpl<LearningPathMapper, LearningPath> implements LearningPathService {

    private final LearningPathMapper learningPathMapper;
    private final LearningPathNodeMapper learningPathNodeMapper;

    @Override
    @Transactional
    public boolean createLearningPath(LearningPath learningPath) {
        try {
            learningPath.setStatus(1);
            learningPath.setViewCount(0);
            learningPath.setCompletionCount(0);
            learningPath.setAverageScore(0.0);
            learningPath.setDeleted(0);
            learningPath.setCreateTime(LocalDateTime.now());
            learningPath.setUpdateTime(LocalDateTime.now());
            return save(learningPath);
        } catch (Exception e) {
            log.error("创建学习路径失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateLearningPath(LearningPath learningPath) {
        try {
            learningPath.setUpdateTime(LocalDateTime.now());
            return updateById(learningPath);
        } catch (Exception e) {
            log.error("更新学习路径失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteLearningPath(Long id) {
        try {
            LearningPath learningPath = getById(id);
            if (learningPath != null) {
                learningPath.setDeleted(1);
                learningPath.setUpdateTime(LocalDateTime.now());
                boolean success = updateById(learningPath);
                if (success) {
                    // 删除相关的节点
                    List<LearningPathNode> nodes = learningPathNodeMapper.selectByPathId(id);
                    for (LearningPathNode node : nodes) {
                        node.setDeleted(1);
                        learningPathNodeMapper.updateById(node);
                    }
                }
                return success;
            }
            return false;
        } catch (Exception e) {
            log.error("删除学习路径失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public LearningPath getLearningPathById(Long id) {
        try {
            return getById(id);
        } catch (Exception e) {
            log.error("获取学习路径详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getLearningPathList(Map<String, Object> params, int page, int size) {
        try {
            // 这里可以根据params构建查询条件
            // 暂时使用BaseMapper的selectList方法
            List<LearningPath> learningPaths = list();
            int total = learningPaths.size();
            int offset = (page - 1) * size;
            int end = Math.min(offset + size, total);
            List<LearningPath> pageList = learningPaths.subList(offset, end);

            Map<String, Object> result = new HashMap<>();
            result.put("list", pageList);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("查询学习路径列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public Map<String, Object> getLearningPathsByTargetUserType(Integer targetUserType, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<LearningPath> learningPaths = learningPathMapper.selectByTargetUserType(targetUserType, offset, size);
            // 计算总数
            int total = learningPaths.size();

            Map<String, Object> result = new HashMap<>();
            result.put("list", learningPaths);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("根据目标用户类型查询学习路径失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public Map<String, Object> getLearningPathsByDifficulty(Integer difficulty, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<LearningPath> learningPaths = learningPathMapper.selectByDifficulty(difficulty, offset, size);
            // 计算总数
            int total = learningPaths.size();

            Map<String, Object> result = new HashMap<>();
            result.put("list", learningPaths);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("根据难度查询学习路径失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public Map<String, Object> getLearningPathStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("targetUserTypeStats", learningPathMapper.selectTargetUserTypeStats());
            stats.put("difficultyStats", learningPathMapper.selectDifficultyStats());
            return stats;
        } catch (Exception e) {
            log.error("统计学习路径信息失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    @Transactional
    public boolean incrementViewCount(Long id) {
        try {
            learningPathMapper.incrementViewCount(id);
            return true;
        } catch (Exception e) {
            log.error("增加浏览量失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean incrementCompletionCount(Long id) {
        try {
            learningPathMapper.incrementCompletionCount(id);
            return true;
        } catch (Exception e) {
            log.error("增加完成人数失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateAverageScore(Long id, Double score) {
        try {
            learningPathMapper.updateAverageScore(id, score);
            return true;
        } catch (Exception e) {
            log.error("更新平均评分失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<LearningPath> getHotLearningPaths(int limit) {
        try {
            return learningPathMapper.selectHotLearningPaths(limit);
        } catch (Exception e) {
            log.error("获取热门学习路径失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<LearningPath> getRecommendedLearningPaths(Long userId, int limit) {
        try {
            return learningPathMapper.selectRecommendedLearningPaths(userId, limit);
        } catch (Exception e) {
            log.error("获取推荐学习路径失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public boolean addLearningPathNode(LearningPathNode node) {
        try {
            node.setDeleted(0);
            node.setCreateTime(LocalDateTime.now());
            node.setUpdateTime(LocalDateTime.now());
            boolean success = learningPathNodeMapper.insert(node) > 0;
            if (success) {
                // 重新排序节点
                reorderNodes(node.getPathId());
                // 更新学习路径的预估时长
                updatePathDuration(node.getPathId());
            }
            return success;
        } catch (Exception e) {
            log.error("添加学习路径节点失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateLearningPathNode(LearningPathNode node) {
        try {
            node.setUpdateTime(LocalDateTime.now());
            boolean success = learningPathNodeMapper.updateById(node) > 0;
            if (success) {
                // 更新学习路径的预估时长
                updatePathDuration(node.getPathId());
            }
            return success;
        } catch (Exception e) {
            log.error("更新学习路径节点失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteLearningPathNode(Long id) {
        try {
            LearningPathNode node = learningPathNodeMapper.selectById(id);
            if (node != null) {
                Long pathId = node.getPathId();
                node.setDeleted(1);
                node.setUpdateTime(LocalDateTime.now());
                boolean success = learningPathNodeMapper.updateById(node) > 0;
                if (success) {
                    // 重新排序节点
                    reorderNodes(pathId);
                    // 更新学习路径的预估时长
                    updatePathDuration(pathId);
                }
                return success;
            }
            return false;
        } catch (Exception e) {
            log.error("删除学习路径节点失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public LearningPathNode getLearningPathNodeById(Long id) {
        try {
            return learningPathNodeMapper.selectById(id);
        } catch (Exception e) {
            log.error("获取学习路径节点详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<LearningPathNode> getLearningPathNodesByPathId(Long pathId) {
        try {
            return learningPathNodeMapper.selectByPathId(pathId);
        } catch (Exception e) {
            log.error("根据学习路径ID获取节点列表失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public Integer calculateTotalDuration(Long pathId) {
        try {
            return learningPathNodeMapper.calculateTotalDuration(pathId);
        } catch (Exception e) {
            log.error("计算学习路径的总时长失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public LearningPathNode getNextNode(Long pathId, Integer currentNodeOrder) {
        try {
            return learningPathNodeMapper.selectNextNode(pathId, currentNodeOrder);
        } catch (Exception e) {
            log.error("获取学习路径的下一个节点失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public LearningPathNode getPreviousNode(Long pathId, Integer currentNodeOrder) {
        try {
            return learningPathNodeMapper.selectPreviousNode(pathId, currentNodeOrder);
        } catch (Exception e) {
            log.error("获取学习路径的前一个节点失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    @Transactional
    public boolean reorderNodes(Long pathId) {
        try {
            learningPathNodeMapper.reorderNodes(pathId);
            return true;
        } catch (Exception e) {
            log.error("重新排序学习路径节点失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public LearningPath generatePersonalizedLearningPath(Long userId, Integer targetUserType, Integer difficulty) {
        try {
            // 这里简化处理，实际应该根据用户的学习历史、能力水平等生成个性化的学习路径
            LearningPath learningPath = new LearningPath();
            learningPath.setName("个性化学习路径");
            learningPath.setDescription("根据您的学习情况生成的个性化反诈学习路径");
            learningPath.setTargetUserType(targetUserType);
            learningPath.setDifficulty(difficulty);
            learningPath.setEstimatedDuration(10); // 预估10小时
            learningPath.setStatus(1);
            learningPath.setViewCount(0);
            learningPath.setCompletionCount(0);
            learningPath.setAverageScore(0.0);
            learningPath.setCreatorId(userId);
            learningPath.setCreatorName("系统");
            learningPath.setDeleted(0);
            learningPath.setCreateTime(LocalDateTime.now());
            learningPath.setUpdateTime(LocalDateTime.now());
            save(learningPath);

            // 添加默认节点
            addDefaultNodes(learningPath.getId());

            return learningPath;
        } catch (Exception e) {
            log.error("生成个性化学习路径失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<LearningPath> recommendLearningPaths(Long userId, int limit) {
        try {
            // 这里简化处理，实际应该根据用户的学习历史、能力水平等推荐学习路径
            return learningPathMapper.selectRecommendedLearningPaths(userId, limit);
        } catch (Exception e) {
            log.error("推荐学习路径失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    // 更新学习路径的预估时长
    private void updatePathDuration(Long pathId) {
        try {
            Integer totalDuration = learningPathNodeMapper.calculateTotalDuration(pathId);
            if (totalDuration != null) {
                LearningPath learningPath = getById(pathId);
                if (learningPath != null) {
                    learningPath.setEstimatedDuration(totalDuration / 60); // 转换为小时
                    learningPath.setUpdateTime(LocalDateTime.now());
                    updateById(learningPath);
                }
            }
        } catch (Exception e) {
            log.error("更新学习路径的预估时长失败: {}", e.getMessage(), e);
        }
    }

    // 添加默认节点
    private void addDefaultNodes(Long pathId) {
        try {
            // 这里简化处理，实际应该根据学习路径的目标用户类型和难度添加相应的节点
            List<LearningPathNode> nodes = new ArrayList<>();

            // 添加知识学习节点
            LearningPathNode node1 = new LearningPathNode();
            node1.setPathId(pathId);
            node1.setName("反诈基础知识");
            node1.setNodeType(1); // 知识学习
            node1.setResourceId(1L);
            node1.setResourceName("反诈基础知识");
            node1.setNodeOrder(1);
            node1.setEstimatedDuration(60); // 60分钟
            node1.setDescription("了解常见的诈骗类型和防范方法");
            node1.setDeleted(0);
            node1.setCreateTime(LocalDateTime.now());
            node1.setUpdateTime(LocalDateTime.now());
            nodes.add(node1);

            // 添加视频学习节点
            LearningPathNode node2 = new LearningPathNode();
            node2.setPathId(pathId);
            node2.setName("反诈案例分析");
            node2.setNodeType(2); // 视频学习
            node2.setResourceId(1L);
            node2.setResourceName("反诈案例分析视频");
            node2.setNodeOrder(2);
            node2.setEstimatedDuration(90); // 90分钟
            node2.setDescription("通过真实案例学习如何识别和防范诈骗");
            node2.setDeleted(0);
            node2.setCreateTime(LocalDateTime.now());
            node2.setUpdateTime(LocalDateTime.now());
            nodes.add(node2);

            // 添加练习测试节点
            LearningPathNode node3 = new LearningPathNode();
            node3.setPathId(pathId);
            node3.setName("反诈知识测试");
            node3.setNodeType(3); // 练习测试
            node3.setResourceId(1L);
            node3.setResourceName("反诈知识测试");
            node3.setNodeOrder(3);
            node3.setEstimatedDuration(60); // 60分钟
            node3.setDescription("测试您的反诈知识掌握程度");
            node3.setDeleted(0);
            node3.setCreateTime(LocalDateTime.now());
            node3.setUpdateTime(LocalDateTime.now());
            nodes.add(node3);

            // 添加实战演练节点
            LearningPathNode node4 = new LearningPathNode();
            node4.setPathId(pathId);
            node4.setName("反诈实战演练");
            node4.setNodeType(4); // 实战演练
            node4.setResourceId(1L);
            node4.setResourceName("反诈实战演练");
            node4.setNodeOrder(4);
            node4.setEstimatedDuration(120); // 120分钟
            node4.setDescription("在模拟场景中练习反诈技能");
            node4.setDeleted(0);
            node4.setCreateTime(LocalDateTime.now());
            node4.setUpdateTime(LocalDateTime.now());
            nodes.add(node4);

            // 批量插入节点
            for (LearningPathNode node : nodes) {
                learningPathNodeMapper.insert(node);
            }
        } catch (Exception e) {
            log.error("添加默认节点失败: {}", e.getMessage(), e);
        }
    }
}
