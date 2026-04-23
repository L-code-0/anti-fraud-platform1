package com.anti.fraud.modules.community.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.community.entity.CommunityComment;
import com.anti.fraud.modules.community.service.CommunityCommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 社区评论控制器
 */
@RestController
@RequestMapping("/community/comment")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "社区评论管理")
public class CommunityCommentController {

    private final CommunityCommentService communityCommentService;

    @Operation(summary = "新增评论")
    @PostMapping("/add")
    public Result<Void> addComment(@ApiParam(value = "评论信息", required = true) @RequestBody CommunityComment comment) {
        try {
            boolean success = communityCommentService.addComment(comment);
            if (success) {
                return Result.successMsg("新增评论成功");
            } else {
                return Result.fail("新增评论失败");
            }
        } catch (Exception e) {
            log.error("新增评论失败: {}", e.getMessage(), e);
            return Result.fail("新增评论失败");
        }
    }

    @Operation(summary = "更新评论")
    @PutMapping("/update")
    public Result<Void> updateComment(@ApiParam(value = "评论信息", required = true) @RequestBody CommunityComment comment) {
        try {
            boolean success = communityCommentService.updateComment(comment);
            if (success) {
                return Result.successMsg("更新评论成功");
            } else {
                return Result.fail("更新评论失败");
            }
        } catch (Exception e) {
            log.error("更新评论失败: {}", e.getMessage(), e);
            return Result.fail("更新评论失败");
        }
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteComment(@ApiParam(value = "评论ID", required = true) @PathVariable Long id) {
        try {
            boolean success = communityCommentService.deleteComment(id);
            if (success) {
                return Result.successMsg("删除评论成功");
            } else {
                return Result.fail("删除评论失败");
            }
        } catch (Exception e) {
            log.error("删除评论失败: {}", e.getMessage(), e);
            return Result.fail("删除评论失败");
        }
    }

    @Operation(summary = "获取评论详情")
    @GetMapping("/detail/{id}")
    public Result<CommunityComment> getCommentById(@ApiParam(value = "评论ID", required = true) @PathVariable Long id) {
        try {
            CommunityComment comment = communityCommentService.getCommentById(id);
            if (comment != null) {
                return Result.success(comment);
            } else {
                return Result.fail("评论不存在");
            }
        } catch (Exception e) {
            log.error("获取评论详情失败: {}", e.getMessage(), e);
            return Result.fail("获取评论详情失败");
        }
    }

    @Operation(summary = "根据帖子ID查询评论")
    @GetMapping("/by-post")
    public Result<Map<String, Object>> getCommentsByPostId(
            @ApiParam(value = "帖子ID", required = true) @RequestParam Long postId,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = communityCommentService.getCommentsByPostId(postId, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据帖子ID查询评论失败: {}", e.getMessage(), e);
            return Result.fail("根据帖子ID查询评论失败");
        }
    }

    @Operation(summary = "根据父评论ID查询子评论")
    @GetMapping("/by-parent")
    public Result<Map<String, Object>> getCommentsByParentId(
            @ApiParam(value = "父评论ID", required = true) @RequestParam Long parentId,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = communityCommentService.getCommentsByParentId(parentId, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据父评论ID查询子评论失败: {}", e.getMessage(), e);
            return Result.fail("根据父评论ID查询子评论失败");
        }
    }

    @Operation(summary = "增加点赞数")
    @PostMapping("/like/{id}")
    public Result<Void> increaseLikeCount(@ApiParam(value = "评论ID", required = true) @PathVariable Long id) {
        try {
            boolean success = communityCommentService.increaseLikeCount(id);
            if (success) {
                return Result.successMsg("增加点赞数成功");
            } else {
                return Result.fail("增加点赞数失败");
            }
        } catch (Exception e) {
            log.error("增加点赞数失败: {}", e.getMessage(), e);
            return Result.fail("增加点赞数失败");
        }
    }

    @Operation(summary = "统计帖子评论数")
    @GetMapping("/count/{postId}")
    public Result<Integer> countCommentsByPostId(@ApiParam(value = "帖子ID", required = true) @PathVariable Long postId) {
        try {
            Integer count = communityCommentService.countCommentsByPostId(postId);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计帖子评论数失败: {}", e.getMessage(), e);
            return Result.fail("统计帖子评论数失败");
        }
    }
}
