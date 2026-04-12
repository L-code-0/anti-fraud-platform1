package com.anti.fraud.modules.note.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.note.entity.Note;
import com.anti.fraud.modules.note.service.NoteService;
import com.anti.fraud.utils.SecurityUtils;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/note")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "学习笔记")
public class NoteController {

    private final NoteService noteService;

    @Operation(summary = "创建笔记")
    @PostMapping("/create")
    public Result<Void> createNote(@RequestBody Note note) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        if (note.getContent() == null || note.getContent().isEmpty()) {
            return Result.fail("笔记内容不能为空");
        }

        note.setUserId(userId);
        note.setUserName(SecurityUtils.getCurrentUserName());

        try {
            boolean success = noteService.createNote(note);
            if (success) {
                return Result.successMsg("创建笔记成功");
            } else {
                return Result.fail("创建笔记失败");
            }
        } catch (Exception e) {
            log.error("创建笔记失败: {}", e.getMessage(), e);
            return Result.fail("创建笔记失败");
        }
    }

    @Operation(summary = "更新笔记")
    @PostMapping("/update")
    public Result<Void> updateNote(@RequestBody Note note) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        if (note.getId() == null) {
            return Result.fail("笔记ID不能为空");
        }

        if (note.getContent() == null || note.getContent().isEmpty()) {
            return Result.fail("笔记内容不能为空");
        }

        try {
            Note existingNote = noteService.getNoteById(note.getId());
            if (existingNote == null || !existingNote.getUserId().equals(userId)) {
                return Result.fail("无权限修改此笔记");
            }

            note.setUserId(userId);
            boolean success = noteService.updateNote(note);
            if (success) {
                return Result.successMsg("更新笔记成功");
            } else {
                return Result.fail("更新笔记失败");
            }
        } catch (Exception e) {
            log.error("更新笔记失败: {}", e.getMessage(), e);
            return Result.fail("更新笔记失败");
        }
    }

    @Operation(summary = "删除笔记")
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteNote(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            boolean success = noteService.deleteNote(id, userId);
            if (success) {
                return Result.successMsg("删除笔记成功");
            } else {
                return Result.fail("删除笔记失败");
            }
        } catch (Exception e) {
            log.error("删除笔记失败: {}", e.getMessage(), e);
            return Result.fail("删除笔记失败");
        }
    }

    @Operation(summary = "获取笔记详情")
    @GetMapping("/detail/{id}")
    public Result<Note> getNoteDetail(@PathVariable Long id) {
        try {
            Note note = noteService.getNoteById(id);
            if (note == null) {
                return Result.fail("笔记不存在");
            }

            // 检查权限
            Long userId = SecurityUtils.getCurrentUserId();
            if (note.getIsPublic() == 0 && (userId == null || !note.getUserId().equals(userId))) {
                return Result.fail("无权限查看此笔记");
            }

            return Result.success("获取笔记详情成功", note);
        } catch (Exception e) {
            log.error("获取笔记详情失败: {}", e.getMessage(), e);
            return Result.fail("获取笔记详情失败");
        }
    }

    @Operation(summary = "获取用户笔记列表")
    @GetMapping("/user/list")
    public Result<List<Note>> getUserNotes(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            List<Note> notes = noteService.getUserNotes(userId, page, size);
            return Result.success("获取用户笔记列表成功", notes);
        } catch (Exception e) {
            log.error("获取用户笔记列表失败: {}", e.getMessage(), e);
            return Result.fail("获取用户笔记列表失败");
        }
    }

    @Operation(summary = "获取公开笔记列表")
    @GetMapping("/public/list")
    public Result<List<Note>> getPublicNotes(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            List<Note> notes = noteService.getPublicNotes(page, size);
            return Result.success("获取公开笔记列表成功", notes);
        } catch (Exception e) {
            log.error("获取公开笔记列表失败: {}", e.getMessage(), e);
            return Result.fail("获取公开笔记列表失败");
        }
    }

    @Operation(summary = "点赞笔记")
    @PostMapping("/like/{id}")
    public Result<Void> likeNote(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            boolean success = noteService.likeNote(id, userId);
            if (success) {
                return Result.successMsg("点赞成功");
            } else {
                return Result.fail("点赞失败");
            }
        } catch (Exception e) {
            log.error("点赞笔记失败: {}", e.getMessage(), e);
            return Result.fail("点赞失败");
        }
    }

    @Operation(summary = "取消点赞")
    @PostMapping("/unlike/{id}")
    public Result<Void> unlikeNote(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            boolean success = noteService.unlikeNote(id, userId);
            if (success) {
                return Result.successMsg("取消点赞成功");
            } else {
                return Result.fail("取消点赞失败");
            }
        } catch (Exception e) {
            log.error("取消点赞失败: {}", e.getMessage(), e);
            return Result.fail("取消点赞失败");
        }
    }

    @Operation(summary = "标记笔记为公开")
    @PostMapping("/public/{id}")
    public Result<Void> markAsPublic(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            boolean success = noteService.markAsPublic(id, userId);
            if (success) {
                return Result.successMsg("标记为公开成功");
            } else {
                return Result.fail("标记为公开失败");
            }
        } catch (Exception e) {
            log.error("标记笔记为公开失败: {}", e.getMessage(), e);
            return Result.fail("标记为公开失败");
        }
    }

    @Operation(summary = "标记笔记为私有")
    @PostMapping("/private/{id}")
    public Result<Void> markAsPrivate(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            boolean success = noteService.markAsPrivate(id, userId);
            if (success) {
                return Result.successMsg("标记为私有成功");
            } else {
                return Result.fail("标记为私有失败");
            }
        } catch (Exception e) {
            log.error("标记笔记为私有失败: {}", e.getMessage(), e);
            return Result.fail("标记为私有失败");
        }
    }
}
