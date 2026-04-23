package com.anti.fraud.modules.collaboration.service.impl;

import com.anti.fraud.modules.collaboration.entity.CollaborationSession;
import com.anti.fraud.modules.collaboration.mapper.CollaborationSessionMapper;
import com.anti.fraud.modules.collaboration.service.CollaborationSessionService;
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
 * 协作演练会话服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CollaborationSessionServiceImpl extends ServiceImpl<CollaborationSessionMapper, CollaborationSession> implements CollaborationSessionService {

    private final CollaborationSessionMapper collaborationSessionMapper;

    @Override
    @Transactional
    public boolean createSession(CollaborationSession session) {
        try {
            session.setCurrentParticipants(0);
            session.setStatus(1);
            session.setDeleted(0);
            session.setCreateTime(LocalDateTime.now());
            session.setUpdateTime(LocalDateTime.now());
            return save(session);
        } catch (Exception e) {
            log.error("创建会话失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateSession(CollaborationSession session) {
        try {
            session.setUpdateTime(LocalDateTime.now());
            return updateById(session);
        } catch (Exception e) {
            log.error("更新会话失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteSession(Long id) {
        try {
            CollaborationSession session = getById(id);
            if (session != null) {
                session.setDeleted(1);
                session.setUpdateTime(LocalDateTime.now());
                return updateById(session);
            }
            return false;
        } catch (Exception e) {
            log.error("删除会话失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public CollaborationSession getSessionById(Long id) {
        try {
            return getById(id);
        } catch (Exception e) {
            log.error("获取会话详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getSessionList(Map<String, Object> params, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<CollaborationSession> sessions = collaborationSessionMapper.selectByCondition(params, offset, size);
            int total = collaborationSessionMapper.selectCountByCondition(params);

            Map<String, Object> result = new HashMap<>();
            result.put("list", sessions);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("查询会话列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public Map<String, Object> getSessionsByCreatorId(Long creatorId, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<CollaborationSession> sessions = collaborationSessionMapper.selectByCreatorId(creatorId, offset, size);
            // 计算总数
            Map<String, Object> params = new HashMap<>();
            params.put("creatorId", creatorId);
            int total = collaborationSessionMapper.selectCountByCondition(params);

            Map<String, Object> result = new HashMap<>();
            result.put("list", sessions);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("根据创建者ID查询会话失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public Map<String, Object> getSessionsByScenarioId(Long scenarioId, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<CollaborationSession> sessions = collaborationSessionMapper.selectByScenarioId(scenarioId, offset, size);
            // 计算总数
            Map<String, Object> params = new HashMap<>();
            params.put("scenarioId", scenarioId);
            int total = collaborationSessionMapper.selectCountByCondition(params);

            Map<String, Object> result = new HashMap<>();
            result.put("list", sessions);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("根据场景ID查询会话失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    @Transactional
    public boolean startSession(Long id) {
        try {
            CollaborationSession session = getById(id);
            if (session != null && session.getStatus() == 1) {
                session.setStatus(2);
                session.setStartTime(LocalDateTime.now());
                session.setUpdateTime(LocalDateTime.now());
                return updateById(session);
            }
            return false;
        } catch (Exception e) {
            log.error("开始会话失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean endSession(Long id) {
        try {
            CollaborationSession session = getById(id);
            if (session != null && session.getStatus() == 2) {
                session.setStatus(3);
                session.setEndTime(LocalDateTime.now());
                session.setUpdateTime(LocalDateTime.now());
                return updateById(session);
            }
            return false;
        } catch (Exception e) {
            log.error("结束会话失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean cancelSession(Long id) {
        try {
            CollaborationSession session = getById(id);
            if (session != null && (session.getStatus() == 1 || session.getStatus() == 2)) {
                session.setStatus(4);
                session.setEndTime(LocalDateTime.now());
                session.setUpdateTime(LocalDateTime.now());
                return updateById(session);
            }
            return false;
        } catch (Exception e) {
            log.error("取消会话失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean increaseCurrentParticipants(Long id) {
        try {
            collaborationSessionMapper.increaseCurrentParticipants(id);
            return true;
        } catch (Exception e) {
            log.error("增加当前参与人数失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean decreaseCurrentParticipants(Long id) {
        try {
            collaborationSessionMapper.decreaseCurrentParticipants(id);
            return true;
        } catch (Exception e) {
            log.error("减少当前参与人数失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<CollaborationSession> getActiveSessions(int limit) {
        try {
            return collaborationSessionMapper.selectActiveSessions(limit);
        } catch (Exception e) {
            log.error("获取进行中的会话失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public Map<String, Object> getSessionStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("statusStats", collaborationSessionMapper.selectStatusStats());
            return stats;
        } catch (Exception e) {
            log.error("统计会话信息失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }
}
