package com.anti.fraud.modules.points.mapper;

import com.anti.fraud.modules.points.entity.PointsRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PointsRecordMapper extends BaseMapper<PointsRecord> {

    @Select("SELECT SUM(points) FROM points_record WHERE user_id = #{userId} AND type = 1")
    Integer sumEarnedPoints(@Param("userId") Long userId);

    @Select("SELECT COUNT(*) + 1 FROM sys_user WHERE points > (SELECT points FROM sys_user WHERE id = #{userId})")
    Integer getUserRanking(@Param("userId") Long userId);
}
