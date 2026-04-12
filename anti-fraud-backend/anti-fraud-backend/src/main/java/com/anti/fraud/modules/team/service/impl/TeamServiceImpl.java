package com.anti.fraud.modules.team.service.impl;

import com.anti.fraud.modules.team.entity.Team;
import com.anti.fraud.modules.team.entity.TeamMember;
import com.anti.fraud.modules.team.mapper.TeamMapper;
import com.anti.fraud.modules.team.mapper.TeamMemberMapper;
import com.anti.fraud.modules.team.service.TeamService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamMapper teamMapper;
    private final TeamMemberMapper teamMemberMapper;

    @Override
    public boolean createTeam(Team team) {
        try {
            team.setMemberCount(0);
            team.setStatus(1); // 1: 活跃
            teamMapper.insert(team);
            
            // 创建者自动加入团队
            TeamMember member = new TeamMember();
            member.setTeamId(team.getId());
            member.setUserId(team.getCreatorId());
            member.setUserName(team.getCreatorName());
            member.setRole("owner"); // 所有者
            member.setStatus(1); // 1: 活跃
            member.setJoinTime(LocalDateTime.now());
            teamMemberMapper.insert(member);
            
            // 更新团队成员数
            team.setMemberCount(1);
            teamMapper.updateById(team);
            
            log.info("创建团队成功: id={}, name={}, creatorId={}", team.getId(), team.getName(), team.getCreatorId());
            return true;
        } catch (Exception e) {
            log.error("创建团队失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean updateTeam(Team team) {
        try {
            teamMapper.updateById(team);
            log.info("更新团队成功: id={}", team.getId());
            return true;
        } catch (Exception e) {
            log.error("更新团队失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean deleteTeam(Long id) {
        try {
            // 删除团队成员
            LambdaQueryWrapper<TeamMember> memberQuery = new LambdaQueryWrapper<>();
            memberQuery.eq(TeamMember::getTeamId, id);
            teamMemberMapper.delete(memberQuery);
            
            // 删除团队
            teamMapper.deleteById(id);
            log.info("删除团队成功: id={}", id);
            return true;
        } catch (Exception e) {
            log.error("删除团队失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Team getTeamById(Long id) {
        try {
            return teamMapper.selectById(id);
        } catch (Exception e) {
            log.error("获取团队详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<Team> getTeamList(Integer status, int page, int size) {
        try {
            LambdaQueryWrapper<Team> queryWrapper = new LambdaQueryWrapper<>();
            if (status != null) {
                queryWrapper.eq(Team::getStatus, status);
            }
            queryWrapper.orderByDesc(Team::getCreateTime);

            IPage<Team> pageInfo = new Page<>(page, size);
            teamMapper.selectPage(pageInfo, queryWrapper);

            return pageInfo.getRecords();
        } catch (Exception e) {
            log.error("获取团队列表失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Team> getUserCreatedTeams(Long userId, int page, int size) {
        try {
            LambdaQueryWrapper<Team> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Team::getCreatorId, userId)
                    .orderByDesc(Team::getCreateTime);

            IPage<Team> pageInfo = new Page<>(page, size);
            teamMapper.selectPage(pageInfo, queryWrapper);

            return pageInfo.getRecords();
        } catch (Exception e) {
            log.error("获取用户创建的团队失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Team> getUserJoinedTeams(Long userId, int page, int size) {
        try {
            // 先获取用户加入的团队ID
            LambdaQueryWrapper<TeamMember> memberQuery = new LambdaQueryWrapper<>();
            memberQuery.eq(TeamMember::getUserId, userId);
            List<TeamMember> members = teamMemberMapper.selectList(memberQuery);
            
            if (members.isEmpty()) {
                return Collections.emptyList();
            }
            
            List<Long> teamIds = members.stream().map(TeamMember::getTeamId).collect(java.util.stream.Collectors.toList());
            
            // 根据团队ID获取团队信息
            LambdaQueryWrapper<Team> teamQuery = new LambdaQueryWrapper<>();
            teamQuery.in(Team::getId, teamIds)
                    .orderByDesc(Team::getCreateTime);

            IPage<Team> pageInfo = new Page<>(page, size);
            teamMapper.selectPage(pageInfo, teamQuery);

            return pageInfo.getRecords();
        } catch (Exception e) {
            log.error("获取用户加入的团队失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public boolean joinTeam(Long teamId, Long userId, String userName) {
        try {
            // 检查团队是否存在
            Team team = teamMapper.selectById(teamId);
            if (team == null) {
                return false;
            }
            
            // 检查用户是否已经在团队中
            LambdaQueryWrapper<TeamMember> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TeamMember::getTeamId, teamId)
                    .eq(TeamMember::getUserId, userId);
            TeamMember existingMember = teamMemberMapper.selectOne(queryWrapper);
            if (existingMember != null) {
                return false;
            }
            
            // 加入团队
            TeamMember member = new TeamMember();
            member.setTeamId(teamId);
            member.setUserId(userId);
            member.setUserName(userName);
            member.setRole("member"); // 普通成员
            member.setStatus(1); // 1: 活跃
            member.setJoinTime(LocalDateTime.now());
            teamMemberMapper.insert(member);
            
            // 更新团队成员数
            team.setMemberCount(team.getMemberCount() + 1);
            teamMapper.updateById(team);
            
            log.info("加入团队成功: teamId={}, userId={}", teamId, userId);
            return true;
        } catch (Exception e) {
            log.error("加入团队失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean leaveTeam(Long teamId, Long userId) {
        try {
            // 检查用户是否是团队成员
            LambdaQueryWrapper<TeamMember> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TeamMember::getTeamId, teamId)
                    .eq(TeamMember::getUserId, userId);
            TeamMember member = teamMemberMapper.selectOne(queryWrapper);
            if (member == null) {
                return false;
            }
            
            // 检查用户是否是所有者
            Team team = teamMapper.selectById(teamId);
            if (team.getCreatorId().equals(userId)) {
                return false; // 所有者不能退出团队，只能转让或删除
            }
            
            // 退出团队
            teamMemberMapper.delete(queryWrapper);
            
            // 更新团队成员数
            team.setMemberCount(team.getMemberCount() - 1);
            teamMapper.updateById(team);
            
            log.info("退出团队成功: teamId={}, userId={}", teamId, userId);
            return true;
        } catch (Exception e) {
            log.error("退出团队失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<TeamMember> getTeamMembers(Long teamId, int page, int size) {
        try {
            LambdaQueryWrapper<TeamMember> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TeamMember::getTeamId, teamId)
                    .orderByDesc(TeamMember::getJoinTime);

            IPage<TeamMember> pageInfo = new Page<>(page, size);
            teamMemberMapper.selectPage(pageInfo, queryWrapper);

            return pageInfo.getRecords();
        } catch (Exception e) {
            log.error("获取团队成员列表失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public boolean removeTeamMember(Long teamId, Long userId) {
        try {
            // 检查用户是否是团队成员
            LambdaQueryWrapper<TeamMember> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TeamMember::getTeamId, teamId)
                    .eq(TeamMember::getUserId, userId);
            TeamMember member = teamMemberMapper.selectOne(queryWrapper);
            if (member == null) {
                return false;
            }
            
            // 检查用户是否是所有者
            Team team = teamMapper.selectById(teamId);
            if (team.getCreatorId().equals(userId)) {
                return false; // 不能踢出所有者
            }
            
            // 踢出成员
            teamMemberMapper.delete(queryWrapper);
            
            // 更新团队成员数
            team.setMemberCount(team.getMemberCount() - 1);
            teamMapper.updateById(team);
            
            log.info("踢出团队成员成功: teamId={}, userId={}", teamId, userId);
            return true;
        } catch (Exception e) {
            log.error("踢出团队成员失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean transferTeamOwnership(Long teamId, Long newOwnerId) {
        try {
            Team team = teamMapper.selectById(teamId);
            if (team == null) {
                return false;
            }
            
            // 检查新所有者是否是团队成员
            LambdaQueryWrapper<TeamMember> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TeamMember::getTeamId, teamId)
                    .eq(TeamMember::getUserId, newOwnerId);
            TeamMember member = teamMemberMapper.selectOne(queryWrapper);
            if (member == null) {
                return false;
            }
            
            // 更新团队所有者
            team.setCreatorId(newOwnerId);
            team.setCreatorName(member.getUserName());
            teamMapper.updateById(team);
            
            // 更新成员角色
            member.setRole("owner");
            teamMemberMapper.updateById(member);
            
            // 更新原所有者的角色
            LambdaQueryWrapper<TeamMember> oldOwnerQuery = new LambdaQueryWrapper<>();
            oldOwnerQuery.eq(TeamMember::getTeamId, teamId)
                    .eq(TeamMember::getUserId, team.getCreatorId());
            TeamMember oldOwner = teamMemberMapper.selectOne(oldOwnerQuery);
            if (oldOwner != null) {
                oldOwner.setRole("member");
                teamMemberMapper.updateById(oldOwner);
            }
            
            log.info("转让团队所有权成功: teamId={}, newOwnerId={}", teamId, newOwnerId);
            return true;
        } catch (Exception e) {
            log.error("转让团队所有权失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Map<String, Object> startTeamDrill(Long teamId, String drillType, String drillName) {
        try {
            // 检查团队是否存在
            Team team = teamMapper.selectById(teamId);
            if (team == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "团队不存在");
                return result;
            }
            
            // 检查团队成员
            List<TeamMember> members = getTeamMembers(teamId, 1, 100);
            if (members.isEmpty()) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "团队无成员");
                return result;
            }
            
            // 模拟开始演练
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "开始团队协作演练成功");
            result.put("drillId", new Random().nextLong());
            result.put("teamId", teamId);
            result.put("teamName", team.getName());
            result.put("drillType", drillType);
            result.put("drillName", drillName);
            result.put("memberCount", members.size());
            result.put("members", members);
            result.put("startTime", LocalDateTime.now());
            
            log.info("开始团队协作演练成功: teamId={}, drillType={}, drillName={}", teamId, drillType, drillName);
            return result;
        } catch (Exception e) {
            log.error("开始团队协作演练失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "开始团队协作演练失败");
            return result;
        }
    }

    @Override
    public Map<String, Object> submitTeamDrillResult(Long teamId, Long drillId, Map<String, Object> results) {
        try {
            // 模拟提交结果
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "提交团队演练结果成功");
            result.put("teamId", teamId);
            result.put("drillId", drillId);
            result.put("score", 85);
            result.put("rank", 3);
            result.put("feedback", "团队表现良好，继续努力！");
            result.put("submitTime", LocalDateTime.now());
            
            log.info("提交团队演练结果成功: teamId={}, drillId={}", teamId, drillId);
            return result;
        } catch (Exception e) {
            log.error("提交团队演练结果失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "提交团队演练结果失败");
            return result;
        }
    }

    @Override
    public List<Map<String, Object>> getTeamDrillHistory(Long teamId, int page, int size) {
        try {
            // 模拟演练历史数据
            List<Map<String, Object>> history = new ArrayList<>();
            history.add(Map.of(
                    "drillId", 1L,
                    "drillName", "电信诈骗团队演练",
                    "drillType", "roleplay",
                    "score", 85,
                    "rank", 3,
                    "participants", 5,
                    "date", LocalDateTime.now().minusDays(1)
            ));
            history.add(Map.of(
                    "drillId", 2L,
                    "drillName", "网络诈骗团队演练",
                    "drillType", "challenge",
                    "score", 90,
                    "rank", 1,
                    "participants", 5,
                    "date", LocalDateTime.now().minusDays(7)
            ));
            history.add(Map.of(
                    "drillId", 3L,
                    "drillName", "金融诈骗团队演练",
                    "drillType", "adaptive",
                    "score", 75,
                    "rank", 5,
                    "participants", 4,
                    "date", LocalDateTime.now().minusDays(14)
            ));
            
            return history;
        } catch (Exception e) {
            log.error("获取团队演练历史失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public Map<String, Object> getTeamStats(Long teamId) {
        try {
            Team team = teamMapper.selectById(teamId);
            if (team == null) {
                return new HashMap<>();
            }
            
            // 模拟统计数据
            Map<String, Object> stats = new HashMap<>();
            stats.put("teamName", team.getName());
            stats.put("memberCount", team.getMemberCount());
            stats.put("totalDrills", 10);
            stats.put("averageScore", 82.5);
            stats.put("highestScore", 95);
            stats.put("bestRank", 1);
            stats.put("drillTypes", Map.of(
                    "roleplay", 4,
                    "challenge", 3,
                    "adaptive", 3
            ));
            
            log.info("获取团队统计信息成功: teamId={}", teamId);
            return stats;
        } catch (Exception e) {
            log.error("获取团队统计信息失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public List<Team> searchTeams(String keyword, int page, int size) {
        try {
            LambdaQueryWrapper<Team> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.like(Team::getName, keyword)
                    .or()
                    .like(Team::getDescription, keyword)
                    .orderByDesc(Team::getCreateTime);

            IPage<Team> pageInfo = new Page<>(page, size);
            teamMapper.selectPage(pageInfo, queryWrapper);

            return pageInfo.getRecords();
        } catch (Exception e) {
            log.error("搜索团队失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
