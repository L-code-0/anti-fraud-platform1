package com.anti.fraud.modules.exam.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.exam.entity.Challenge;
import com.anti.fraud.modules.exam.entity.ChallengeParticipant;
import com.anti.fraud.modules.exam.service.ChallengeService;
import com.anti.fraud.utils.SecurityUtils;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/exam/challenge")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "挑战赛")
public class ChallengeController {

    private final ChallengeService challengeService;

    @Operation(summary = "创建挑战赛事")
    @PostMapping("/create")
    public Result<Void> createChallenge(@RequestBody Challenge challenge) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        challenge.setCreatedBy(SecurityUtils.getCurrentUserName());
        challenge.setUpdatedBy(SecurityUtils.getCurrentUserName());

        try {
            boolean success = challengeService.createChallenge(challenge);
            if (success) {
                return Result.successMsg("创建挑战赛事成功");
            } else {
                return Result.fail("创建挑战赛事失败");
            }
        } catch (Exception e) {
            log.error("创建挑战赛事失败: {}", e.getMessage(), e);
            return Result.fail("创建挑战赛事失败");
        }
    }

    @Operation(summary = "更新挑战赛事")
    @PostMapping("/update")
    public Result<Void> updateChallenge(@RequestBody Challenge challenge) {
        challenge.setUpdatedBy(SecurityUtils.getCurrentUserName());

        try {
            boolean success = challengeService.updateChallenge(challenge);
            if (success) {
                return Result.successMsg("更新挑战赛事成功");
            } else {
                return Result.fail("更新挑战赛事失败");
            }
        } catch (Exception e) {
            log.error("更新挑战赛事失败: {}", e.getMessage(), e);
            return Result.fail("更新挑战赛事失败");
        }
    }

    @Operation(summary = "删除挑战赛事")
    @PostMapping("/delete/{id}")
    public Result<Void> deleteChallenge(@PathVariable Long id) {
        try {
            boolean success = challengeService.deleteChallenge(id);
            if (success) {
                return Result.successMsg("删除挑战赛事成功");
            } else {
                return Result.fail("删除挑战赛事失败");
            }
        } catch (Exception e) {
            log.error("删除挑战赛事失败: {}", e.getMessage(), e);
            return Result.fail("删除挑战赛事失败");
        }
    }

    @Operation(summary = "获取挑战赛事详情")
    @GetMapping("/detail/{id}")
    public Result<Challenge> getChallengeById(@PathVariable Long id) {
        try {
            Challenge challenge = challengeService.getChallengeById(id);
            if (challenge != null) {
                return Result.success("获取挑战赛事详情成功", challenge);
            } else {
                return Result.fail("挑战赛事不存在");
            }
        } catch (Exception e) {
            log.error("获取挑战赛事详情失败: {}", e.getMessage(), e);
            return Result.fail("获取挑战赛事详情失败");
        }
    }

    @Operation(summary = "获取挑战赛事列表")
    @GetMapping("/list")
    public Result<List<Challenge>> getChallengeList(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<Challenge> challenges = challengeService.getChallengeList(status, category, page, size);
            return Result.success("获取挑战赛事列表成功", challenges);
        } catch (Exception e) {
            log.error("获取挑战赛事列表失败: {}", e.getMessage(), e);
            return Result.fail("获取挑战赛事列表失败");
        }
    }

    @Operation(summary = "参与挑战")
    @PostMapping("/participate/{challengeId}")
    public Result<Map<String, Object>> participateChallenge(@PathVariable Long challengeId) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            Map<String, Object> result = challengeService.participateChallenge(challengeId, userId);
            if ((boolean) result.get("success")) {
                return Result.success("参与挑战成功", result);
            } else {
                return Result.fail((String) result.get("message"));
            }
        } catch (Exception e) {
            log.error("参与挑战失败: {}", e.getMessage(), e);
            return Result.fail("参与挑战失败");
        }
    }

    @Operation(summary = "提交挑战答案")
    @PostMapping("/submit/{participantId}")
    public Result<Map<String, Object>> submitChallengeAnswer(
            @PathVariable Long participantId,
            @RequestBody Map<String, Object> answers) {
        try {
            Map<String, Object> result = challengeService.submitChallengeAnswer(participantId, answers);
            if ((boolean) result.get("success")) {
                return Result.success("提交答案成功", result);
            } else {
                return Result.fail((String) result.get("message"));
            }
        } catch (Exception e) {
            log.error("提交挑战答案失败: {}", e.getMessage(), e);
            return Result.fail("提交挑战答案失败");
        }
    }

    @Operation(summary = "获取挑战排行榜")
    @GetMapping("/ranking/{challengeId}")
    public Result<List<ChallengeParticipant>> getChallengeRanking(
            @PathVariable Long challengeId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            List<ChallengeParticipant> ranking = challengeService.getChallengeRanking(challengeId, page, size);
            return Result.success("获取挑战排行榜成功", ranking);
        } catch (Exception e) {
            log.error("获取挑战排行榜失败: {}", e.getMessage(), e);
            return Result.fail("获取挑战排行榜失败");
        }
    }

    @Operation(summary = "获取用户挑战记录")
    @GetMapping("/records")
    public Result<List<ChallengeParticipant>> getUserChallengeRecords(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            List<ChallengeParticipant> records = challengeService.getUserChallengeRecords(userId, page, size);
            return Result.success("获取用户挑战记录成功", records);
        } catch (Exception e) {
            log.error("获取用户挑战记录失败: {}", e.getMessage(), e);
            return Result.fail("获取用户挑战记录失败");
        }
    }

    @Operation(summary = "计算挑战排名")
    @PostMapping("/calculate-ranking/{challengeId}")
    public Result<Void> calculateChallengeRanking(@PathVariable Long challengeId) {
        try {
            boolean success = challengeService.calculateChallengeRanking(challengeId);
            if (success) {
                return Result.successMsg("计算挑战排名成功");
            } else {
                return Result.fail("计算挑战排名失败");
            }
        } catch (Exception e) {
            log.error("计算挑战排名失败: {}", e.getMessage(), e);
            return Result.fail("计算挑战排名失败");
        }
    }

    @Operation(summary = "生成挑战题目")
    @GetMapping("/generate-questions/{challengeId}")
    public Result<List<Map<String, Object>>> generateChallengeQuestions(@PathVariable Long challengeId) {
        try {
            List<Map<String, Object>> questions = challengeService.generateChallengeQuestions(challengeId);
            return Result.success("生成挑战题目成功", questions);
        } catch (Exception e) {
            log.error("生成挑战题目失败: {}", e.getMessage(), e);
            return Result.fail("生成挑战题目失败");
        }
    }

    @Operation(summary = "获取挑战统计信息")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getChallengeStats() {
        try {
            Map<String, Object> stats = challengeService.getChallengeStats();
            return Result.success("获取挑战统计信息成功", stats);
        } catch (Exception e) {
            log.error("获取挑战统计信息失败: {}", e.getMessage(), e);
            return Result.fail("获取挑战统计信息失败");
        }
    }
}
