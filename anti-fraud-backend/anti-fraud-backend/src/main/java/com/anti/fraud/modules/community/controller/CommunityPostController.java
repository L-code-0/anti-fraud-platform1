package com.anti.fraud.modules.community.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.community.entity.CommunityPost;
import com.anti.fraud.modules.community.service.CommunityPostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 社区帖子控制器
 */
@RestController
@RequestMapping("/community/post")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "社区帖子管理")
public class CommunityPostController {

    private final CommunityPostService communityPostService;

    @Operation(summary = "新增帖子")
    @PostMapping("/add")
    public Result<Void> addPost(@ApiParam(value = "帖子信息", required = true) @RequestBody CommunityPost post) {
        try {
            boolean success = communityPostService.addPost(post);
            if (success) {
                return Result.successMsg("新增帖子成功");
            } else {
                return Result.fail("新增帖子失败");
            }
        } catch (Exception e) {
            log.error("新增帖子失败: {}", e.getMessage(), e);
            return Result.fail("新增帖子失败");
        }
    }

    @Operation(summary = "更新帖子")
    @PutMapping("/update")
    public Result<Void> updatePost(@ApiParam(value = "帖子信息", required = true) @RequestBody CommunityPost post) {
        try {
            boolean success = communityPostService.updatePost(post);
            if (success) {
                return Result.successMsg("更新帖子成功");
            } else {
                return Result.fail("更新帖子失败");
            }
        } catch (Exception e) {
            log.error("更新帖子失败: {}", e.getMessage(), e);
            return Result.fail("更新帖子失败");
        }
    }

    @Operation(summary = "删除帖子")
    @DeleteMapping("/delete/{id}")
    public Result<Void> deletePost(@ApiParam(value = "帖子ID", required = true) @PathVariable Long id) {
        try {
            boolean success = communityPostService.deletePost(id);
            if (success) {
                return Result.successMsg("删除帖子成功");
            } else {
                return Result.fail("删除帖子失败");
            }
        } catch (Exception e) {
            log.error("删除帖子失败: {}", e.getMessage(), e);
            return Result.fail("删除帖子失败");
        }
    }

    @Operation(summary = "获取帖子详情")
    @GetMapping("/detail/{id}")
    public Result<CommunityPost> getPostById(@ApiParam(value = "帖子ID", required = true) @PathVariable Long id) {
        try {
            CommunityPost post = communityPostService.getPostById(id);
            if (post != null) {
                // 增加浏览量
                communityPostService.increaseViewCount(id);
                return Result.success(post);
            } else {
                return Result.fail("帖子不存在");
            }
        } catch (Exception e) {
            log.error("获取帖子详情失败: {}", e.getMessage(), e);
            return Result.fail("获取帖子详情失败");
        }
    }

    @Operation(summary = "分页查询帖子")
    @PostMapping("/list")
    public Result<Map<String, Object>> getPostList(
            @ApiParam(value = "查询参数", required = false) @RequestBody(required = false) Map<String, Object> params,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            if (params == null) {
                params = new java.util.HashMap<>();
            }
            Map<String, Object> result = communityPostService.getPostList(params, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询帖子列表失败: {}", e.getMessage(), e);
            return Result.fail("查询帖子列表失败");
        }
    }

    @Operation(summary = "根据帖子类型查询帖子")
    @GetMapping("/by-type")
    public Result<Map<String, Object>> getPostsByType(
            @ApiParam(value = "帖子类型: 1-反诈知识, 2-可疑信息, 3-求助问答, 4-经验分享, 5-其他", required = true) @RequestParam Integer postType,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = communityPostService.getPostsByType(postType, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据帖子类型查询帖子失败: {}", e.getMessage(), e);
            return Result.fail("根据帖子类型查询帖子失败");
        }
    }

    @Operation(summary = "增加点赞数")
    @PostMapping("/like/{id}")
    public Result<Void> increaseLikeCount(@ApiParam(value = "帖子ID", required = true) @PathVariable Long id) {
        try {
            boolean success = communityPostService.increaseLikeCount(id);
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

    @Operation(summary = "增加分享数")
    @PostMapping("/share/{id}")
    public Result<Void> increaseShareCount(@ApiParam(value = "帖子ID", required = true) @PathVariable Long id) {
        try {
            boolean success = communityPostService.increaseShareCount(id);
            if (success) {
                return Result.successMsg("增加分享数成功");
            } else {
                return Result.fail("增加分享数失败");
            }
        } catch (Exception e) {
            log.error("增加分享数失败: {}", e.getMessage(), e);
            return Result.fail("增加分享数失败");
        }
    }

    @Operation(summary = "获取热门帖子")
    @GetMapping("/hot")
    public Result<List<CommunityPost>> getHotPosts(
            @ApiParam(value = "数量限制", required = true) @RequestParam Integer limit) {
        try {
            List<CommunityPost> posts = communityPostService.getHotPosts(limit);
            return Result.success(posts);
        } catch (Exception e) {
            log.error("获取热门帖子失败: {}", e.getMessage(), e);
            return Result.fail("获取热门帖子失败");
        }
    }

    @Operation(summary = "获取最新帖子")
    @GetMapping("/latest")
    public Result<List<CommunityPost>> getLatestPosts(
            @ApiParam(value = "数量限制", required = true) @RequestParam Integer limit) {
        try {
            List<CommunityPost> posts = communityPostService.getLatestPosts(limit);
            return Result.success(posts);
        } catch (Exception e) {
            log.error("获取最新帖子失败: {}", e.getMessage(), e);
            return Result.fail("获取最新帖子失败");
        }
    }

    @Operation(summary = "统计帖子信息")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getPostStats() {
        try {
            Map<String, Object> stats = communityPostService.getPostStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("统计帖子信息失败: {}", e.getMessage(), e);
            return Result.fail("统计帖子信息失败");
        }
    }

    @Operation(summary = "搜索帖子")
    @GetMapping("/search")
    public Result<Map<String, Object>> searchPosts(
            @ApiParam(value = "关键词", required = true) @RequestParam String keyword,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = communityPostService.searchPosts(keyword, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("搜索帖子失败: {}", e.getMessage(), e);
            return Result.fail("搜索帖子失败");
        }
    }
}
