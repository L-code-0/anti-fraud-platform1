package com.anti.fraud.modules.classroom.service;

import com.anti.fraud.modules.classroom.dto.ClassCreateDTO;
import com.anti.fraud.modules.classroom.dto.ClassQueryDTO;
import com.anti.fraud.modules.classroom.dto.ClassUpdateDTO;
import com.anti.fraud.modules.classroom.dto.JoinClassDTO;
import com.anti.fraud.modules.classroom.vo.ClassDetailVO;
import com.anti.fraud.modules.classroom.vo.ClassStatsVO;
import com.anti.fraud.modules.classroom.vo.ClassStudentVO;
import com.anti.fraud.modules.classroom.vo.ClassVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 班级服务接口
 */
public interface ClassService {

    /**
     * 获取班级统计
     */
    ClassStatsVO getStats();

    /**
     * 分页查询班级列表
     */
    Page<ClassVO> getClassPage(ClassQueryDTO queryDTO);

    /**
     * 获取班级详情
     */
    ClassDetailVO getClassDetail(Long id);

    /**
     * 创建班级
     */
    Long createClass(ClassCreateDTO createDTO, Long userId);

    /**
     * 更新班级
     */
    void updateClass(ClassUpdateDTO updateDTO);

    /**
     * 删除班级
     */
    void deleteClass(Long id);

    /**
     * 加入班级
     */
    void joinClass(JoinClassDTO joinDTO, Long studentId, String studentName);

    /**
     * 退出班级
     */
    void leaveClass(Long classId, Long studentId);

    /**
     * 分页查询班级学生
     */
    Page<ClassStudentVO> getStudentPage(Long classId, Integer page, Integer size, String keyword);

    /**
     * 移除学生
     */
    void removeStudent(Long classId, Long studentId);

    /**
     * 获取我的班级
     */
    Page<ClassVO> getMyClasses(Long studentId, Integer page, Integer size);
}
