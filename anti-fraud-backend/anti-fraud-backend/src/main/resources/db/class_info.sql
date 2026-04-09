-- 班级信息表
CREATE TABLE IF NOT EXISTS `class_info` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `class_name` VARCHAR(100) NOT NULL COMMENT '班级名称',
    `class_code` VARCHAR(20) NOT NULL COMMENT '班级码（唯一）',
    `teacher_id` BIGINT COMMENT '班主任ID',
    `teacher_name` VARCHAR(50) COMMENT '班主任姓名',
    `description` VARCHAR(500) COMMENT '班级描述',
    `student_count` INT NOT NULL DEFAULT 0 COMMENT '学生人数',
    `learning_progress` INT NOT NULL DEFAULT 0 COMMENT '学习进度（平均）',
    `mastery_rate` INT NOT NULL DEFAULT 0 COMMENT '知识掌握率',
    `average_score` INT NOT NULL DEFAULT 0 COMMENT '平均分',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-已关闭 1-正常',
    `creator_id` BIGINT COMMENT '创建者ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_class_code` (`class_code`),
    INDEX `idx_teacher_id` (`teacher_id`),
    INDEX `idx_creator_id` (`creator_id`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班级信息表';

-- 班级学生关联表
CREATE TABLE IF NOT EXISTS `class_student` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `class_id` BIGINT NOT NULL COMMENT '班级ID',
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `student_no` VARCHAR(50) COMMENT '学号',
    `student_name` VARCHAR(50) COMMENT '学生姓名',
    `learning_progress` INT NOT NULL DEFAULT 0 COMMENT '学习进度',
    `average_score` INT NOT NULL DEFAULT 0 COMMENT '平均分',
    `last_login_time` DATETIME COMMENT '最后登录时间',
    `join_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_class_student` (`class_id`, `student_id`),
    INDEX `idx_student_id` (`student_id`),
    INDEX `idx_class_id` (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班级学生关联表';

-- 插入测试班级数据
INSERT INTO `class_info` (`class_name`, `class_code`, `teacher_name`, `description`, `student_count`, `learning_progress`, `mastery_rate`, `average_score`, `creator_id`, `create_time`) VALUES
('反诈安全 1 班', 'CL0001', '张老师', '反诈安全知识学习班级，面向大一新生', 45, 68, 72, 85, 1, NOW()),
('反诈安全 2 班', 'CL0002', '李老师', '反诈安全知识学习班级，面向大二学生', 38, 75, 78, 88, 1, NOW()),
('反诈安全 3 班', 'CL0003', '王老师', '反诈安全知识学习班级，面向大三学生', 42, 82, 85, 90, 1, NOW()),
('网络安全 1 班', 'CL0004', '赵老师', '网络安全与反诈知识学习班级', 35, 60, 65, 78, 1, NOW()),
('金融安全 1 班', 'CL0005', '钱老师', '金融诈骗防范学习班级', 40, 70, 75, 82, 1, NOW());

-- 插入测试学生数据
INSERT INTO `class_student` (`class_id`, `student_id`, `student_no`, `student_name`, `learning_progress`, `average_score`, `join_time`) VALUES
-- 班级1的学生
(1, 101, 'STU001001', '张三', 85, 92, DATE_SUB(NOW(), INTERVAL 30 DAY)),
(1, 102, 'STU001002', '李四', 72, 78, DATE_SUB(NOW(), INTERVAL 28 DAY)),
(1, 103, 'STU001003', '王五', 68, 75, DATE_SUB(NOW(), INTERVAL 25 DAY)),
(1, 104, 'STU001004', '赵六', 90, 95, DATE_SUB(NOW(), INTERVAL 20 DAY)),
(1, 105, 'STU001005', '钱七', 55, 62, DATE_SUB(NOW(), INTERVAL 15 DAY)),
-- 班级2的学生
(2, 201, 'STU002001', '孙八', 78, 85, DATE_SUB(NOW(), INTERVAL 22 DAY)),
(2, 202, 'STU002002', '周九', 82, 88, DATE_SUB(NOW(), INTERVAL 18 DAY)),
(2, 203, 'STU002003', '吴十', 65, 70, DATE_SUB(NOW(), INTERVAL 12 DAY)),
-- 班级3的学生
(3, 301, 'STU003001', '郑一', 95, 98, DATE_SUB(NOW(), INTERVAL 10 DAY)),
(3, 302, 'STU003002', '陈二', 88, 92, DATE_SUB(NOW(), INTERVAL 8 DAY));
