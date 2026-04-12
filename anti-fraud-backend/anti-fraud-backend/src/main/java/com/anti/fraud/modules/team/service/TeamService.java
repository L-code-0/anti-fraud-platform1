package com.anti.fraud.modules.team.service;

import com.anti.fraud.modules.team.entity.Team;
import com.anti.fraud.modules.team.entity.TeamMember;
import java.util.List;
import java.util.Map;

public interface TeamService {

    /**
     * 创建团队
     * @param team 团队信息
     * @return 是否成功
     */
    boolean createTeam(Team team);

    /**
     * 更新团队
     * @param team 团队信息
     * @return 是否成功
     */
    boolean updateTeam(Team team);

    /**
     * 删除团队
     * @param id 团队ID
     * @return 是否成功
     */
    boolean deleteTeam(Long id);

    /**
     * 获取团队详情
     * @param id 团队ID
     * @return 团队详情
     */
    Team getTeamById(Long id);

    /**
     * 获取团队列表
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 团队列表
     */
    List<Team> getTeamList(Integer status, int page, int size);

    /**
     * 获取用户创建的团队
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 团队列表
     */
    List<Team> getUserCreatedTeams(Long userId, int page, int size);

    /**
     * 获取用户加入的团队
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 团队列表
     */
    List<Team> getUserJoinedTeams(Long userId, int page, int size);

    /**
     * 加入团队
     * @param teamId 团队ID
     * @param userId 用户ID
     * @param userName 用户名
     * @return 是否成功
     */
    boolean joinTeam(Long teamId, Long userId, String userName);

    /**
     * 退出团队
     * @param teamId 团队ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean leaveTeam(Long teamId, Long userId);

    /**
     * 获取团队成员列表
     * @param teamId 团队ID
     * @param page 页码
     * @param size 每页大小
     * @return 成员列表
     */
    List<TeamMember> getTeamMembers(Long teamId, int page, int size);

    /**
     * 踢出团队成员
     * @param teamId 团队ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean removeTeamMember(Long teamId, Long userId);

    /**
     * 转让团队所有权
     * @param teamId 团队ID
     * @param newOwnerId 新所有者ID
     * @return 是否成功
     */
    boolean transferTeamOwnership(Long teamId, Long newOwnerId);

    /**
     * 开始团队协作演练
     * @param teamId 团队ID
     * @param drillType 演练类型
     * @param drillName 演练名称
     * @return 演练信息
     */
    Map<String, Object> startTeamDrill(Long teamId, String drillType, String drillName);

    /**
     * 提交团队演练结果
     * @param teamId 团队ID
     * @param drillId 演练ID
     * @param results 演练结果
     * @return 提交结果
     */
    Map<String, Object> submitTeamDrillResult(Long teamId, Long drillId, Map<String, Object> results);

    /**
     * 获取团队演练历史
     * @param teamId 团队ID
     * @param page 页码
     * @param size 每页大小
     * @return 演练历史
     */
    List<Map<String, Object>> getTeamDrillHistory(Long teamId, int page, int size);

    /**
     * 获取团队统计信息
     * @param teamId 团队ID
     * @return 统计信息
     */
    Map<String, Object> getTeamStats(Long teamId);

    /**
     * 搜索团队
     * @param keyword 关键词
     * @param page 页码
     * @param size 每页大小
     * @return 团队列表
     */
    List<Team> searchTeams(String keyword, int page, int size);
}
