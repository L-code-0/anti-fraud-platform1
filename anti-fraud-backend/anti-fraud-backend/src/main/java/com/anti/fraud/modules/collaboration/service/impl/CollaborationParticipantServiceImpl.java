package com.anti.fraud.modules.collaboration.service.impl;

import com.anti.fraud.modules.collaboration.entity.CollaborationParticipant;
import com.anti.fraud.modules.collaboration.mapper.CollaborationParticipantMapper;
import com.anti.fraud.modules.collaboration.service.CollaborationParticipantService;
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
 * 协作演练参与者服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CollaborationParticipantServiceImpl extends ServiceImpl<CollaborationParticipantMapper, CollaborationParticipant> implements CollaborationParticipantService {

    private final CollaborationParticipantMapper collaborationParticipantMapper;

    @Override
    @Transactional
    public boolean addParticipant(CollaborationParticipant participant) {
        try {
            participant.setJoinTime(LocalDateTime.now());
            participant.setStatus(1);
            participant.setCompletionStatus(2);
            participant.setDeleted(0);
            participant.setCreateTime(LocalDateTime.now());
            participant.setUpdateTime(LocalDateTime.now());
            return save(participant);
        } catch (Exception e) {
            log.error("添加参与者失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateParticipant(CollaborationParticipant participant) {
        try {
            participant.setUpdateTime(LocalDateTime.now());
            return updateById(participant);
        } catch (Exception e) {
            log.error("更新参与者信息失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteParticipant(Long id) {
        try {
            CollaborationParticipant participant = getById(id);
            if (participant != null) {
                participant.setDeleted(1);
                participant.setUpdateTime(LocalDateTime.now());
                return updateById(participant);
            }
            return false;
        } catch (Exception e) {
            log.error("删除参与者失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public CollaborationParticipant getParticipantById(Long id) {
        try {
            return getById(id);
        } catch (Exception e) {
            log.error("获取参与者详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getParticipantsBySessionId(Long sessionId, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<CollaborationParticipant> participants = collaborationParticipantMapper.selectBySessionId(sessionId, offset, size);
            int total = collaborationParticipantMapper.selectCountBySessionId(sessionId);

            Map<String, Object> result = new HashMap<>();
            result.put("list", participants);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("根据会话ID查询参与者失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public Map<String, Object> getParticipantsByUserId(Long userId, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<CollaborationParticipant> participants = collaborationParticipantMapper.selectByUserId(userId, offset, size);
            // 计算总数
            int total = participants.size();

            Map<String, Object> result = new HashMap<>();
            result.put("list", participants);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("根据用户ID查询参与的会话失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public CollaborationParticipant getParticipantBySessionIdAndUserId(Long sessionId, Long userId) {
        try {
            return collaborationParticipantMapper.selectBySessionIdAndUserId(sessionId, userId);
        } catch (Exception e) {
            log.error("根据会话ID和用户ID查询参与者失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    @Transactional
    public boolean joinSession(Long sessionId, Long userId, String username, Integer role) {
        try {
            // 检查是否已经加入
            CollaborationParticipant existingParticipant = collaborationParticipantMapper.selectBySessionIdAndUserId(sessionId, userId);
            if (existingParticipant != null) {
                // 更新状态为在线
                existingParticipant.setStatus(1);
                existingParticipant.setJoinTime(LocalDateTime.now());
                existingParticipant.setUpdateTime(LocalDateTime.now());
                return updateById(existingParticipant);
            } else {
                // 新增参与者
                CollaborationParticipant participant = new CollaborationParticipant();
                participant.setSessionId(sessionId);
                participant.setUserId(userId);
                participant.setUsername(username);
                participant.setRole(role);
                participant.setJoinTime(LocalDateTime.now());
                participant.setStatus(1);
                participant.setCompletionStatus(2);
                participant.setDeleted(0);
                participant.setCreateTime(LocalDateTime.now());
                participant.setUpdateTime(LocalDateTime.now());
                return save(participant);
            }
        } catch (Exception e) {
            log.error("参与者加入会话失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean leaveSession(Long sessionId, Long userId) {
        try {
            CollaborationParticipant participant = collaborationParticipantMapper.selectBySessionIdAndUserId(sessionId, userId);
            if (participant != null) {
                participant.setStatus(2);
                participant.setLeaveTime(LocalDateTime.now());
                participant.setUpdateTime(LocalDateTime.now());
                return updateById(participant);
            }
            return false;
        } catch (Exception e) {
            log.error("参与者离开会话失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateParticipantStatus(Long id, Integer status) {
        try {
            CollaborationParticipant participant = getById(id);
            if (participant != null) {
                participant.setStatus(status);
                participant.setUpdateTime(LocalDateTime.now());
                return updateById(participant);
            }
            return false;
        } catch (Exception e) {
            log.error("更新参与者状态失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateParticipantScore(Long id, Integer score) {
        try {
            CollaborationParticipant participant = getById(id);
            if (participant != null) {
                participant.setScore(score);
                participant.setUpdateTime(LocalDateTime.now());
                return updateById(participant);
            }
            return false;
        } catch (Exception e) {
            log.error("更新参与者得分失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateParticipantCompletionStatus(Long id, Integer completionStatus) {
        try {
            CollaborationParticipant participant = getById(id);
            if (participant != null) {
                participant.setCompletionStatus(completionStatus);
                participant.setUpdateTime(LocalDateTime.now());
                return updateById(participant);
            }
            return false;
        } catch (Exception e) {
            log.error("更新参与者完成情况失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Map<String, Object> getRoleStatsBySessionId(Long sessionId) {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("roleStats", collaborationParticipantMapper.selectRoleStatsBySessionId(sessionId));
            return stats;
        } catch (Exception e) {
            log.error("统计会话参与者角色失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Object> getStatusStatsBySessionId(Long sessionId) {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("statusStats", collaborationParticipantMapper.selectStatusStatsBySessionId(sessionId));
            return stats;
        } catch (Exception e) {
            log.error("统计会话参与者状态失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }
}
