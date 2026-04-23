-- 操作审计日志表
DROP TABLE IF EXISTS `audit_log`;
CREATE TABLE `audit_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT COMMENT '操作人ID',
    `username` VARCHAR(50) COMMENT '操作人名称',
    `operation_type` TINYINT NOT NULL COMMENT '操作类型：1-登录，2-登出，3-新增，4-修改，5-删除，6-查询，7-其他',
    `module` VARCHAR(100) NOT NULL COMMENT '操作模块',
    `operation_content` TEXT NOT NULL COMMENT '操作内容',
    `operation_result` TINYINT NOT NULL COMMENT '操作结果：1-成功，2-失败',
    `error_message` TEXT COMMENT '错误信息',
    `ip_address` VARCHAR(50) COMMENT '操作IP',
    `operation_time` DATETIME NOT NULL COMMENT '操作时间',
    `browser_info` VARCHAR(200) COMMENT '浏览器信息',
    `device_info` VARCHAR(200) COMMENT '设备信息',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-正常，2-异常',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_operation_type` (`operation_type`),
    KEY `idx_module` (`module`),
    KEY `idx_operation_result` (`operation_result`),
    KEY `idx_operation_time` (`operation_time`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作审计日志表';
