-- 多媒体资源表
DROP TABLE IF EXISTS `multimedia_resource`;
CREATE TABLE `multimedia_resource` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title` VARCHAR(200) NOT NULL COMMENT '资源标题',
    `type` TINYINT NOT NULL COMMENT '资源类型：1-视频，2-音频，3-图片，4-文档',
    `url` VARCHAR(500) NOT NULL COMMENT '资源URL',
    `cover` VARCHAR(500) COMMENT '资源封面',
    `description` TEXT COMMENT '资源描述',
    `duration` INT COMMENT '资源时长（秒）',
    `size` BIGINT COMMENT '资源大小（字节）',
    `format` VARCHAR(50) COMMENT '资源格式',
    `knowledge_point` VARCHAR(100) COMMENT '知识点',
    `tags` VARCHAR(200) COMMENT '标签',
    `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览量',
    `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
    `share_count` INT NOT NULL DEFAULT 0 COMMENT '分享数',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-启用，2-禁用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_type` (`type`),
    KEY `idx_knowledge_point` (`knowledge_point`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='多媒体资源表';
