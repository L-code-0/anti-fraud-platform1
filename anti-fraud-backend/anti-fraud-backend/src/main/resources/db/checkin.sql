-- 打卡记录表
DROP TABLE IF EXISTS `checkin_record`;
CREATE TABLE `checkin_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `checkin_type` TINYINT NOT NULL COMMENT '打卡类型：1-每日打卡，2-学习打卡',
    `checkin_date` DATETIME NOT NULL COMMENT '打卡日期',
    `checkin_time` DATETIME NOT NULL COMMENT '打卡时间',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '打卡状态：1-成功，2-失败',
    `location` VARCHAR(200) COMMENT '打卡地点（可选）',
    `ip_address` VARCHAR(50) COMMENT '打卡IP',
    `consecutive_days` INT NOT NULL DEFAULT 0 COMMENT '连续打卡天数',
    `points` INT NOT NULL DEFAULT 0 COMMENT '获得积分',
    `remark` VARCHAR(500) COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_checkin_date` (`checkin_date`),
    KEY `idx_checkin_type` (`checkin_type`),
    KEY `idx_user_checkin_date` (`user_id`, `checkin_date`),
    UNIQUE KEY `uk_user_checkin_type_date` (`user_id`, `checkin_type`, DATE(`checkin_date`))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='打卡记录表';
