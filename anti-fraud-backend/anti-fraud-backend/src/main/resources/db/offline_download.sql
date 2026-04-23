-- 离线下载表
DROP TABLE IF EXISTS `offline_download`;
CREATE TABLE `offline_download` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `resource_id` BIGINT NOT NULL COMMENT '资源ID',
    `resource_name` VARCHAR(100) NOT NULL COMMENT '资源名称',
    `resource_type` TINYINT NOT NULL DEFAULT 1 COMMENT '资源类型：1-知识文章，2-视频，3-音频，4-文档，5-其他',
    `resource_path` VARCHAR(200) NOT NULL COMMENT '资源路径',
    `storage_path` VARCHAR(200) COMMENT '存储路径',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '下载状态：1-待下载，2-下载中，3-已完成，4-下载失败',
    `progress` INT NOT NULL DEFAULT 0 COMMENT '下载进度（0-100）',
    `file_size` BIGINT COMMENT '文件大小（字节）',
    `downloaded_size` BIGINT NOT NULL DEFAULT 0 COMMENT '已下载大小（字节）',
    `start_time` DATETIME COMMENT '下载开始时间',
    `end_time` DATETIME COMMENT '下载完成时间',
    `failure_reason` VARCHAR(200) COMMENT '失败原因',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_resource_id` (`resource_id`),
    KEY `idx_status` (`status`),
    KEY `idx_resource_type` (`resource_type`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='离线下载表';
