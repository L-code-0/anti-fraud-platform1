package com.anti.fraud.modules.user.mapper;

import com.anti.fraud.modules.user.entity.LoginDevice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 登录设备Mapper
 */
public interface LoginDeviceMapper extends BaseMapper<LoginDevice> {
    
    /**
     * 根据用户ID查询登录设备
     * @param userId 用户ID
     * @return 登录设备列表
     */
    List<LoginDevice> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 根据设备ID查询登录设备
     * @param deviceId 设备ID
     * @return 登录设备
     */
    LoginDevice selectByDeviceId(@Param("deviceId") String deviceId);
    
    /**
     * 更新设备状态
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @param isTrusted 是否可信
     * @param isActive 是否活跃
     * @return 影响行数
     */
    int updateDeviceStatus(@Param("userId") Long userId, @Param("deviceId") String deviceId, 
                         @Param("isTrusted") Boolean isTrusted, @Param("isActive") Boolean isActive);
    
    /**
     * 批量更新设备为非活跃
     * @param userId 用户ID
     * @param excludeDeviceId 排除的设备ID
     * @return 影响行数
     */
    int batchUpdateToInactive(@Param("userId") Long userId, @Param("excludeDeviceId") String excludeDeviceId);
}
