package com.anti.fraud.modules.classroom.mapper;

import com.anti.fraud.modules.classroom.entity.ClassInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Map;

/**
 * 班级 Mapper
 */
@Mapper
public interface ClassInfoMapper extends BaseMapper<ClassInfo> {

    /**
     * 分页查询班级列表
     */
    IPage<Map<String, Object>> selectClassPage(
            Page<Map<String, Object>> page,
            @Param("keyword") String keyword,
            @Param("teacherId") Long teacherId
    );

    /**
     * 获取班级详情
     */
    Map<String, Object> selectClassDetail(@Param("id") Long id);

    /**
     * 更新班级统计
     */
    @Update("UPDATE class_info SET student_count = (SELECT COUNT(*) FROM class_student WHERE class_id = #{classId} AND deleted = 0), update_time = NOW() WHERE id = #{classId}")
    int updateStudentCount(@Param("classId") Long classId);

    /**
     * 检查班级码是否存在
     */
    @Select("SELECT COUNT(*) FROM class_info WHERE class_code = #{classCode} AND deleted = 0")
    int checkClassCodeExists(@Param("classCode") String classCode);

    /**
     * 统计班级总数
     */
    @Select("SELECT COUNT(*) FROM class_info WHERE deleted = 0")
    int countTotal();

    /**
     * 统计学生总数
     */
    @Select("SELECT COUNT(DISTINCT student_id) FROM class_student WHERE deleted = 0")
    int countTotalStudents();

    /**
     * 计算平均学习进度
     */
    @Select("SELECT IFNULL(AVG(learning_progress), 0) FROM class_info WHERE deleted = 0")
    Double avgProgress();
}
