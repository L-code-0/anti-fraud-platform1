package com.anti.fraud.modules.classroom.mapper;

import com.anti.fraud.modules.classroom.entity.ClassStudent;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * 班级学生 Mapper
 */
@Mapper
public interface ClassStudentMapper extends BaseMapper<ClassStudent> {

    /**
     * 分页查询班级学生
     */
    IPage<Map<String, Object>> selectStudentPage(
            Page<Map<String, Object>> page,
            @Param("classId") Long classId,
            @Param("keyword") String keyword
    );

    /**
     * 检查学生是否已在班级中
     */
    @Select("SELECT COUNT(*) FROM class_student WHERE class_id = #{classId} AND student_id = #{studentId} AND deleted = 0")
    int checkStudentInClass(@Param("classId") Long classId, @Param("studentId") Long studentId);

    /**
     * 统计班级学生数
     */
    @Select("SELECT COUNT(*) FROM class_student WHERE class_id = #{classId} AND deleted = 0")
    int countByClassId(@Param("classId") Long classId);

    /**
     * 获取班级学生学习进度统计
     */
    @Select("SELECT IFNULL(AVG(learning_progress), 0) as avg_progress, IFNULL(AVG(average_score), 0) as avg_score FROM class_student WHERE class_id = #{classId} AND deleted = 0")
    Map<String, Object> getClassStats(@Param("classId") Long classId);
}
