package com.anti.fraud.modules.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 登录设备实体
 */
@Data
@TableName("login_device")
public class LoginDevice {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private String deviceId;
    private String deviceInfo;
    private String ipAddress;
    private String location;
    private String lastLoginTime;
    private Boolean isTrusted;
    private Boolean isActive;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
