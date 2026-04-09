package com.anti.fraud.modules.classroom.service.impl;

import com.anti.fraud.common.exception.BusinessException;
import com.anti.fraud.modules.classroom.dto.ClassCreateDTO;
import com.anti.fraud.modules.classroom.dto.ClassQueryDTO;
import com.anti.fraud.modules.classroom.dto.ClassUpdateDTO;
import com.anti.fraud.modules.classroom.dto.JoinClassDTO;
import com.anti.fraud.modules.classroom.entity.ClassInfo;
import com.anti.fraud.modules.classroom.entity.ClassStudent;
import com.anti.fraud.modules.classroom.mapper.ClassInfoMapper;
import com.anti.fraud.modules.classroom.mapper.ClassStudentMapper;
import com.anti.fraud.modules.classroom.service.ClassService;
import com.anti.fraud.modules.classroom.vo.ClassDetailVO;
import com.anti.fraud.modules.classroom.vo.ClassStatsVO;
import com.anti.fraud.modules.classroom.vo.ClassStudentVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 班级服务实现
 */
@Service
@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {

    private final ClassInfoMapper classInfoMapper;
    private final ClassStudentMapper classStudentMapper;

    @Override
    public ClassStatsVO getStats() {
        ClassStatsVO stats = new ClassStatsVO();
        stats.setTotalClasses(classInfoMapper.countTotal());
        stats.setTotalStudents(classInfoMapper.countTotalStudents());

        Double avgProgress = classInfoMapper.avgProgress();
        stats.setAvgProgress(avgProgress != null ? avgProgress.intValue() : 0);

        return stats;
    }

    @Override
    public Page<ClassVO> getClassPage(ClassQueryDTO queryDTO) {
        Page<Map<String, Object>> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());

        Page<Map<String, Object>> resultPage = classInfoMapper.selectClassPage(
                page,
                queryDTO.getKeyword(),
                null
        );

        Page<ClassVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        voPage.setRecords(resultPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));

        return voPage;
    }

    @Override
    public ClassDetailVO getClassDetail(Long id) {
        Map<String, Object> detail = classInfoMapper.selectClassDetail(id);
        if (detail == null || detail.isEmpty()) {
            throw new BusinessException("班级不存在");
        }
        return convertToDetailVO(detail);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createClass(ClassCreateDTO createDTO, Long userId) {
        // 生成唯一班级码
        String classCode = generateClassCode();
        while (classInfoMapper.checkClassCodeExists(classCode) > 0) {
            classCode = generateClassCode();
        }

        ClassInfo classInfo = new ClassInfo();
        classInfo.setClassName(createDTO.getClassName());
        classInfo.setClassCode(classCode);
        classInfo.setTeacherName(createDTO.getTeacherName());
        classInfo.setDescription(createDTO.getDescription());
        classInfo.setTeacherId(userId);
        classInfo.setCreatorId(userId);
        classInfo.setStudentCount(0);
        classInfo.setLearningProgress(0);
        classInfo.setMasteryRate(0);
        classInfo.setAverageScore(0);
        classInfo.setStatus(1);
        classInfo.setCreateTime(LocalDateTime.now());

        classInfoMapper.insert(classInfo);
        return classInfo.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateClass(ClassUpdateDTO updateDTO) {
        ClassInfo classInfo = classInfoMapper.selectById(updateDTO.getId());
        if (classInfo == null) {
            throw new BusinessException("班级不存在");
        }

        classInfo.setClassName(updateDTO.getClassName());
        classInfo.setTeacherName(updateDTO.getTeacherName());
        classInfo.setDescription(updateDTO.getDescription());
        classInfo.setUpdateTime(LocalDateTime.now());

        classInfoMapper.updateById(classInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteClass(Long id) {
        ClassInfo classInfo = classInfoMapper.selectById(id);
        if (classInfo == null) {
            throw new BusinessException("班级不存在");
        }

        classInfoMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void joinClass(JoinClassDTO joinDTO, Long studentId, String studentName) {
        // 查找班级
        LambdaQueryWrapper<ClassInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassInfo::getClassCode, joinDTO.getClassCode())
                .eq(ClassInfo::getDeleted, 0);
        ClassInfo classInfo = classInfoMapper.selectOne(wrapper);

        if (classInfo == null) {
            throw new BusinessException("班级码无效");
        }

        // 检查是否已加入
        if (classStudentMapper.checkStudentInClass(classInfo.getId(), studentId) > 0) {
            throw new BusinessException("您已在该班级中");
        }

        // 添加学生
        ClassStudent classStudent = new ClassStudent();
        classStudent.setClassId(classInfo.getId());
        classStudent.setStudentId(studentId);
        classStudent.setStudentName(studentName);
        classStudent.setStudentNo("STU" + String.format("%06d", studentId));
        classStudent.setLearningProgress(0);
        classStudent.setAverageScore(0);
        classStudent.setJoinTime(LocalDateTime.now());
        classStudent.setCreateTime(LocalDateTime.now());

        classStudentMapper.insert(classStudent);

        // 更新班级学生数
        classInfoMapper.updateStudentCount(classInfo.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void leaveClass(Long classId, Long studentId) {
        LambdaQueryWrapper<ClassStudent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassStudent::getClassId, classId)
                .eq(ClassStudent::getStudentId, studentId);

        classStudentMapper.delete(wrapper);
        classInfoMapper.updateStudentCount(classId);
    }

    @Override
    public Page<ClassStudentVO> getStudentPage(Long classId, Integer page, Integer size, String keyword) {
        Page<Map<String, Object>> pageParam = new Page<>(page, size);

        Page<Map<String, Object>> resultPage = classStudentMapper.selectStudentPage(
                pageParam,
                classId,
                keyword
        );

        Page<ClassStudentVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        voPage.setRecords(resultPage.getRecords().stream()
                .map(this::convertToStudentVO)
                .collect(Collectors.toList()));

        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeStudent(Long classId, Long studentId) {
        LambdaQueryWrapper<ClassStudent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassStudent::getClassId, classId)
                .eq(ClassStudent::getStudentId, studentId);

        classStudentMapper.delete(wrapper);
        classInfoMapper.updateStudentCount(classId);
    }

    @Override
    public Page<ClassVO> getMyClasses(Long studentId, Integer page, Integer size) {
        // 查询学生所在班级
        Page<ClassVO> voPage = new Page<>(page, size);

        // 这里简化处理，实际应该联表查询
        return voPage;
    }

    /**
     * 生成班级码
     */
    private String generateClassCode() {
        return "CL" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    /**
     * 转换为 VO
     */
    private ClassVO convertToVO(Map<String, Object> map) {
        ClassVO vo = new ClassVO();
        vo.setId(((Number) map.get("id")).longValue());
        vo.setClassName((String) map.get("className"));
        vo.setClassCode((String) map.get("classCode"));
        vo.setTeacherName((String) map.get("teacherName"));
        vo.setDescription((String) map.get("description"));
        vo.setStudentCount((Integer) map.get("studentCount"));
        vo.setLearningProgress((Integer) map.get("learningProgress"));
        vo.setAverageScore((Integer) map.get("averageScore"));
        vo.setStatus((Integer) map.get("status"));
        vo.setCreateTime((LocalDateTime) map.get("createTime"));
        return vo;
    }

    /**
     * 转换为详情 VO
     */
    private ClassDetailVO convertToDetailVO(Map<String, Object> map) {
        ClassDetailVO vo = new ClassDetailVO();
        vo.setId(((Number) map.get("id")).longValue());
        vo.setClassName((String) map.get("class_name"));
        vo.setClassCode((String) map.get("class_code"));
        vo.setTeacherId(map.get("teacher_id") != null ? ((Number) map.get("teacher_id")).longValue() : null);
        vo.setTeacherName((String) map.get("teacher_name"));
        vo.setDescription((String) map.get("description"));
        vo.setStudentCount((Integer) map.get("student_count"));
        vo.setLearningProgress((Integer) map.get("learning_progress"));
        vo.setMasteryRate((Integer) map.get("mastery_rate"));
        vo.setAverageScore((Integer) map.get("average_score"));
        vo.setStatus((Integer) map.get("status"));
        vo.setCreateTime((LocalDateTime) map.get("create_time"));

        // 生成邀请链接
        vo.setInviteLink("/join/" + map.get("class_code"));

        return vo;
    }

    /**
     * 转换为学生 VO
     */
    private ClassStudentVO convertToStudentVO(Map<String, Object> map) {
        ClassStudentVO vo = new ClassStudentVO();
        vo.setId(((Number) map.get("id")).longValue());
        vo.setStudentId(((Number) map.get("studentId")).longValue());
        vo.setStudentNo((String) map.get("studentId"));
        vo.setName((String) map.get("name"));
        vo.setLearningProgress((Integer) map.get("learningProgress"));
        vo.setScore((Integer) map.get("score"));
        vo.setLastLogin((LocalDateTime) map.get("lastLogin"));
        vo.setJoinTime((LocalDateTime) map.get("joinTime"));
        return vo;
    }
}
