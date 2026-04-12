package com.anti.fraud.modules.team.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.team.entity.Team;
import com.anti.fraud.modules.team.entity.TeamMember;
import com.anti.fraud.modules.team.service.TeamService;
import com.anti.fraud.utils.SecurityUtils;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/team")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "团队管理")
public class TeamController {

    private final TeamService teamService;

    @Operation(summary = "创建团队")
    @PostMapping("/create")
    public Result<Void> createTeam(@RequestBody Team team) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        team.setCreatorId(userId);
        team.setCreatorName(SecurityUtils.getCurrentUserName());
        team.setCreatedBy(SecurityUtils.getCurrentUserName());
        team.setUpdatedBy(SecurityUtils.getCurrentUserName());

        try {
            boolean success = teamService.createTeam(team);
            if (success) {
                return Result.successMsg("创建团队成功");
            } else {
                return Result.fail("创建团队失败");
            }
        } catch (Exception e) {
            log.error("创建团队失败: {}", e.getMessage(), e);
            return Result.fail("创建团队失败");
        }
    }

    @Operation(summary = "更新团队")
    @PostMapping("/update")
    public Result<Void> updateTeam(@RequestBody Team team) {
        team.setUpdatedBy(SecurityUtils.getCurrentUserName());

        try {
            boolean success = teamService.updateTeam(team);
            if (success) {
                return Result.successMsg("更新团队成功");
            } else {
                return Result.fail("更新团队失败");
            }
        } catch (Exception e) {
            log.error("更新团队失败: {}", e.getMessage(), e);
            return Result.fail("更新团队失败");
        }
    }

    @Operation(summary = "删除团队")
    @PostMapping("/delete/{id}")
    public Result<Void> deleteTeam(@PathVariable Long id) {
        try {
            boolean success = teamService.deleteTeam(id);
            if (success) {
                return Result.successMsg("删除团队成功");
            } else {
                return Result.fail("删除团队失败");
            }
        } catch (Exception e) {
            log.error("删除团队失败: {}", e.getMessage(), e);
            return Result.fail("删除团队失败");
        }
    }

    @Operation(summary = "获取团队详情")
    @GetMapping("/detail/{id}")
    public Result<Team> getTeamById(@PathVariable Long id) {
        try {
            Team team = teamService.getTeamById(id);
            if (team != null) {
                return Result.success("获取团队详情成功", team);
            } else {
                return Result.fail("团队不存在");
            }
        } catch (Exception e) {
            log.error("获取团队详情失败: {}", e.getMessage(), e);
            return Result.fail("获取团队详情失败");
        }
    }

    @Operation(summary = "获取团队列表")
    @GetMapping("/list")
    public Result<List<Team>> getTeamList(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<Team> teams = teamService.getTeamList(status, page, size);
            return Result.success("获取团队列表成功", teams);
        } catch (Exception e) {
            log.error("获取团队列表失败: {}", e.getMessage(), e);
            return Result.fail("获取团队列表失败");
        }
    }

    @Operation(summary = "获取用户创建的团队")
    @GetMapping("/created")
    public Result<List<Team>> getUserCreatedTeams(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            List<Team> teams = teamService.getUserCreatedTeams(userId, page, size);
            return Result.success("获取用户创建的团队成功", teams);
        } catch (Exception e) {
            log.error("获取用户创建的团队失败: {}", e.getMessage(), e);
            return Result.fail("获取用户创建的团队失败");
        }
    }

    @Operation(summary = "获取用户加入的团队")
    @GetMapping("/joined")
    public Result<List<Team>> getUserJoinedTeams(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            List<Team> teams = teamService.getUserJoinedTeams(userId, page, size);
            return Result.success("获取用户加入的团队成功", teams);
        } catch (Exception e) {
            log.error("获取用户加入的团队失败: {}", e.getMessage(), e);
            return Result.fail("获取用户加入的团队失败");
        }
    }

    @Operation(summary = "加入团队")
    @PostMapping("/join/{teamId}")
    public Result<Void> joinTeam(@PathVariable Long teamId) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            boolean success = teamService.joinTeam(teamId, userId, SecurityUtils.getCurrentUserName());
            if (success) {
                return Result.successMsg("加入团队成功");
            } else {
                return Result.fail("加入团队失败");
            }
        } catch (Exception e) {
            log.error("加入团队失败: {}", e.getMessage(), e);
            return Result.fail("加入团队失败");
        }
    }

    @Operation(summary = "退出团队")
    @PostMapping("/leave/{teamId}")
    public Result<Void> leaveTeam(@PathVariable Long teamId) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            boolean success = teamService.leaveTeam(teamId, userId);
            if (success) {
                return Result.successMsg("退出团队成功");
            } else {
                return Result.fail("退出团队失败");
            }
        } catch (Exception e) {
            log.error("退出团队失败: {}", e.getMessage(), e);
            return Result.fail("退出团队失败");
        }
    }

    @Operation(summary = "获取团队成员列表")
    @GetMapping("/members/{teamId}")
    public Result<List<TeamMember>> getTeamMembers(
            @PathVariable Long teamId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<TeamMember> members = teamService.getTeamMembers(teamId, page, size);
            return Result.success("获取团队成员列表成功", members);
        } catch (Exception e) {
            log.error("获取团队成员列表失败: {}", e.getMessage(), e);
            return Result.fail("获取团队成员列表失败");
        }
    }

    @Operation(summary = "踢出团队成员")
    @PostMapping("/remove/{teamId}/{userId}")
    public Result<Void> removeTeamMember(@PathVariable Long teamId, @PathVariable Long userId) {
        try {
            boolean success = teamService.removeTeamMember(teamId, userId);
            if (success) {
                return Result.successMsg("踢出团队成员成功");
            } else {
                return Result.fail("踢出团队成员失败");
            }
        } catch (Exception e) {
            log.error("踢出团队成员失败: {}", e.getMessage(), e);
            return Result.fail("踢出团队成员失败");
        }
    }

    @Operation(summary = "转让团队所有权")
    @PostMapping("/transfer/{teamId}/{newOwnerId}")
    public Result<Void> transferTeamOwnership(@PathVariable Long teamId, @PathVariable Long newOwnerId) {
        try {
            boolean success = teamService.transferTeamOwnership(teamId, newOwnerId);
            if (success) {
                return Result.successMsg("转让团队所有权成功");
            } else {
                return Result.fail("转让团队所有权失败");
            }
        } catch (Exception e) {
            log.error("转让团队所有权失败: {}", e.getMessage(), e);
            return Result.fail("转让团队所有权失败");
        }
    }

    @Operation(summary = "开始团队协作演练")
    @PostMapping("/drill/start")
    public Result<Map<String, Object>> startTeamDrill(@RequestBody Map<String, Object> data) {
        Long teamId = Long.valueOf(data.get("teamId").toString());
        String drillType = (String) data.get("drillType");
        String drillName = (String) data.get("drillName");

        try {
            Map<String, Object> result = teamService.startTeamDrill(teamId, drillType, drillName);
            if ((boolean) result.get("success")) {
                return Result.success("开始团队协作演练成功", result);
            } else {
                return Result.fail((String) result.get("message"));
            }
        } catch (Exception e) {
            log.error("开始团队协作演练失败: {}", e.getMessage(), e);
            return Result.fail("开始团队协作演练失败");
        }
    }

    @Operation(summary = "提交团队演练结果")
    @PostMapping("/drill/submit")
    public Result<Map<String, Object>> submitTeamDrillResult(@RequestBody Map<String, Object> data) {
        Long teamId = Long.valueOf(data.get("teamId").toString());
        Long drillId = Long.valueOf(data.get("drillId").toString());
        Map<String, Object> results = (Map<String, Object>) data.get("results");

        try {
            Map<String, Object> result = teamService.submitTeamDrillResult(teamId, drillId, results);
            if ((boolean) result.get("success")) {
                return Result.success("提交团队演练结果成功", result);
            } else {
                return Result.fail((String) result.get("message"));
            }
        } catch (Exception e) {
            log.error("提交团队演练结果失败: {}", e.getMessage(), e);
            return Result.fail("提交团队演练结果失败");
        }
    }

    @Operation(summary = "获取团队演练历史")
    @GetMapping("/drill/history/{teamId}")
    public Result<List<Map<String, Object>>> getTeamDrillHistory(
            @PathVariable Long teamId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<Map<String, Object>> history = teamService.getTeamDrillHistory(teamId, page, size);
            return Result.success("获取团队演练历史成功", history);
        } catch (Exception e) {
            log.error("获取团队演练历史失败: {}", e.getMessage(), e);
            return Result.fail("获取团队演练历史失败");
        }
    }

    @Operation(summary = "获取团队统计信息")
    @GetMapping("/stats/{teamId}")
    public Result<Map<String, Object>> getTeamStats(@PathVariable Long teamId) {
        try {
            Map<String, Object> stats = teamService.getTeamStats(teamId);
            return Result.success("获取团队统计信息成功", stats);
        } catch (Exception e) {
            log.error("获取团队统计信息失败: {}", e.getMessage(), e);
            return Result.fail("获取团队统计信息失败");
        }
    }

    @Operation(summary = "搜索团队")
    @GetMapping("/search")
    public Result<List<Team>> searchTeams(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<Team> teams = teamService.searchTeams(keyword, page, size);
            return Result.success("搜索团队成功", teams);
        } catch (Exception e) {
            log.error("搜索团队失败: {}", e.getMessage(), e);
            return Result.fail("搜索团队失败");
        }
    }
}
