-- 数据库索引优化脚本

USE anti_fraud;

-- 知识内容表索引优化
ALTER TABLE knowledge_content ADD INDEX idx_category_id (category_id);
ALTER TABLE knowledge_content ADD INDEX idx_status (status);
ALTER TABLE knowledge_content ADD INDEX idx_audit_status (audit_status);
ALTER TABLE knowledge_content ADD INDEX idx_view_count (view_count);
ALTER TABLE knowledge_content ADD INDEX idx_publish_time (publish_time);
ALTER TABLE knowledge_content ADD INDEX idx_is_top (is_top);

-- 测试记录表索引优化
ALTER TABLE test_record ADD INDEX idx_user_id (user_id);
ALTER TABLE test_record ADD INDEX idx_paper_id (paper_id);
ALTER TABLE test_record ADD INDEX idx_submit_time (submit_time);

-- 演练记录表索引优化
ALTER TABLE simulation_record ADD INDEX idx_user_id (user_id);
ALTER TABLE simulation_record ADD INDEX idx_scene_id (scene_id);
ALTER TABLE simulation_record ADD INDEX idx_end_time (end_time);

-- 举报表索引优化
ALTER TABLE report_info ADD INDEX idx_status (status);
ALTER TABLE report_info ADD INDEX idx_risk_level (risk_level);
ALTER TABLE report_info ADD INDEX idx_create_time (create_time);

-- 用户表索引优化
ALTER TABLE sys_user ADD INDEX idx_role_id (role_id);
ALTER TABLE sys_user ADD INDEX idx_status (status);
ALTER TABLE sys_user ADD INDEX idx_points (points);

-- 活动表索引优化
ALTER TABLE activity_info ADD INDEX idx_status (status);
ALTER TABLE activity_info ADD INDEX idx_start_time (start_time);
ALTER TABLE activity_info ADD INDEX idx_end_time (end_time);

-- 预警信息表索引优化
ALTER TABLE warning_info ADD INDEX idx_warning_level (warning_level);
ALTER TABLE warning_info ADD INDEX idx_status (status);
ALTER TABLE warning_info ADD INDEX idx_publish_time (publish_time);

-- 学习记录表索引优化
ALTER TABLE study_record ADD INDEX idx_user_id (user_id);
ALTER TABLE study_record ADD INDEX idx_knowledge_id (knowledge_id);
ALTER TABLE study_record ADD INDEX idx_last_study_time (last_study_time);

-- 积分记录表索引优化
ALTER TABLE points_record ADD INDEX idx_user_id (user_id);
ALTER TABLE points_record ADD INDEX idx_type (type);
ALTER TABLE points_record ADD INDEX idx_create_time (create_time);

-- 活动报名表索引优化
ALTER TABLE activity_registration ADD INDEX idx_activity_id (activity_id);
ALTER TABLE activity_registration ADD INDEX idx_user_id (user_id);
ALTER TABLE activity_registration ADD INDEX idx_status (status);

-- 学习薄弱点表索引优化
ALTER TABLE learning_weakness ADD INDEX idx_user_id (user_id);
ALTER TABLE learning_weakness ADD INDEX idx_category (category);
ALTER TABLE learning_weakness ADD INDEX idx_correct_rate (correct_rate);

-- 导出任务表索引优化
ALTER TABLE export_task ADD INDEX idx_user_id (user_id);
ALTER TABLE export_task ADD INDEX idx_status (status);
ALTER TABLE export_task ADD INDEX idx_create_time (create_time);