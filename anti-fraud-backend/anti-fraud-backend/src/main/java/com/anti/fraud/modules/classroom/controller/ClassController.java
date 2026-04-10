package com.anti.fraud.modules.classroom.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.classroom.dto.ClassCreateDTO;
import com.anti.fraud.modules.classroom.dto.ClassQueryDTO;
import com.anti.fraud.modules.classroom.dto.ClassUpdateDTO;
import com.anti.fraud.modules.classroom.dto.JoinClassDTO;
import com.anti.fraud.modules.classroom.service.ClassService;
import com.anti.fraud.modules.classroom.vo.ClassDetailVO;
import com.anti.fraud.modules.classroom.vo.ClassStatsVO;
import com.anti.fraud.modules.classroom.vo.ClassStudentVO;
import com.anti.fraud.modules.classroom.vo.ClassVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 班级管理控制器
 */
@Tag(name = "班级管理", description = "班级创建、查询、学生管理")
@RestController
@RequestMapping("/class")
@RequiredArgsConstructor
public class ClassController {

    private final ClassService classService;

    @Operation(summary = "获取班级统计", description = "获取班级总数、学生总数、平均进度")
    @GetMapping("/stats")
    public Result<ClassStatsVO> getStats() {
        return Result.success(classService.getStats());
    }

    @Operation(summary = "获取班级列表", description = "分页查询班级列表，支持搜索")
    @GetMapping("/list")
    public Result<Page<ClassVO>> getClassList(ClassQueryDTO queryDTO) {
        return Result.success(classService.getClassPage(queryDTO));
    }

    @Operation(summary = "获取班级详情", description = "根据ID获取班级详细信息")
    @GetMapping("/{id}")
    public Result<ClassDetailVO> getClassDetail(
            @Parameter(description = "班级ID") @PathVariable Long id) {
        return Result.success(classService.getClassDetail(id));
    }

    @Operation(summary = "创建班级", description = "管理员或教师创建班级")
    @PostMapping("/create")
    public Result<Long> createClass(
            @RequestBody ClassCreateDTO createDTO,
            @Parameter(hidden = true) @RequestAttribute("userId") Long userId) {
        Long classId = classService.createClass(createDTO, userId);
        return Result.success("创建成功", classId);
    }

    @Operation(summary = "更新班级", description = "管理员或教师更新班级信息")
    @PostMapping("/update")
    public Result<String> updateClass(@RequestBody ClassUpdateDTO updateDTO) {
        classService.updateClass(updateDTO);
        return Result.success("更新成功");
    }

    @Operation(summary = "删除班级", description = "管理员删除班级")
    @DeleteMapping("/{id}")
    public Result<String> deleteClass(
            @Parameter(description = "班级ID") @PathVariable Long id) {
        classService.deleteClass(id);
        return Result.success("删除成功");
    }

    @Operation(summary = "加入班级", description = "学生通过班级码加入班级")
    @PostMapping("/join")
    public Result<String> joinClass(
            @RequestBody JoinClassDTO joinDTO,
            @Parameter(hidden = true) @RequestAttribute("userId") Long userId,
            @Parameter(hidden = true) @RequestAttribute("userName") String userName) {
        classService.joinClass(joinDTO, userId, userName);
        return Result.success("加入成功");
    }

    @Operation(summary = "退出班级", description = "学生退出班级")
    @PostMapping("/{classId}/leave")
    public Result<String> leaveClass(
            @Parameter(description = "班级ID") @PathVariable Long classId,
            @Parameter(hidden = true) @RequestAttribute("userId") Long userId) {
        classService.leaveClass(classId, userId);
        return Result.success("退出成功");
    }

    @Operation(summary = "获取班级学生列表", description = "分页查询班级学生")
    @GetMapping("/{classId}/students")
    public Result<Page<ClassStudentVO>> getStudents(
            @Parameter(description = "班级ID") @PathVariable Long classId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword) {
        return Result.success(classService.getStudentPage(classId, page, size, keyword));
    }

    @Operation(summary = "移除学生", description = "管理员或教师从班级移除学生")
    @DeleteMapping("/{classId}/student/{studentId}")
    public Result<String> removeStudent(
            @Parameter(description = "班级ID") @PathVariable Long classId,
            @Parameter(description = "学生ID") @PathVariable Long studentId) {
        classService.removeStudent(classId, studentId);
        return Result.success("移除成功");
    }

    @Operation(summary = "获取我的班级", description = "学生查看自己加入的班级")
    @GetMapping("/my")
    public Result<Page<ClassVO>> getMyClasses(
            @Parameter(hidden = true) @RequestAttribute("userId") Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "12") Integer size) {
        return Result.success(classService.getMyClasses(userId, page, size));
    }
}
