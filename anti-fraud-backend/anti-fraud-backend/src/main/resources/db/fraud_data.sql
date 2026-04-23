-- 真实诈骗案例表
DROP TABLE IF EXISTS `fraud_case`;
CREATE TABLE `fraud_case` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `case_title` VARCHAR(200) NOT NULL COMMENT '案例标题',
    `case_type` VARCHAR(50) NOT NULL COMMENT '诈骗类型: TELEPHONE-电话诈骗, NETWORK-网络诈骗, SMS-短信诈骗, INVESTMENT-投资诈骗, RELATIONSHIP-情感诈骗, FINANCE-金融诈骗, OTHER-其他',
    `occur_time` DATETIME COMMENT '发生时间',
    `victim_profile` TEXT COMMENT '受害者画像(脱敏)',
    `fraud_process` TEXT NOT NULL COMMENT '诈骗过程描述(脱敏)',
    `loss_amount` DECIMAL(12,2) DEFAULT 0.00 COMMENT '损失金额(元)',
    `recovery_amount` DECIMAL(12,2) DEFAULT 0.00 COMMENT '追回金额(元)',
    `case_status` VARCHAR(50) DEFAULT '已报案' COMMENT '案件状态: 已报案, 调查中, 已破案, 已结案',
    `police_info` VARCHAR(500) COMMENT '警方信息(脱敏)',
    `warning_level` TINYINT NOT NULL DEFAULT 1 COMMENT '预警等级: 1-低, 2-中, 3-高',
    `prevention_advice` TEXT COMMENT '防范建议',
    `source` VARCHAR(200) NOT NULL COMMENT '信息来源',
    `is_anonymous` TINYINT NOT NULL DEFAULT 1 COMMENT '是否匿名: 0-否, 1-是',
    `is_verified` TINYINT NOT NULL DEFAULT 0 COMMENT '是否验证: 0-未验证, 1-已验证',
    `view_count` INT NOT NULL DEFAULT 0 COMMENT '查看次数',
    `share_count` INT NOT NULL DEFAULT 0 COMMENT '分享次数',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_case_type` (`case_type`),
    KEY `idx_occur_time` (`occur_time`),
    KEY `idx_warning_level` (`warning_level`),
    KEY `idx_status` (`status`),
    KEY `idx_is_verified` (`is_verified`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='真实诈骗案例表';

-- 诈骗话术文本表
DROP TABLE IF EXISTS `fraud_script`;
CREATE TABLE `fraud_script` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `script_title` VARCHAR(200) NOT NULL COMMENT '话术标题',
    `fraud_type` VARCHAR(50) NOT NULL COMMENT '诈骗类型: TELEPHONE-电话诈骗, NETWORK-网络诈骗, SMS-短信诈骗, INVESTMENT-投资诈骗, RELATIONSHIP-情感诈骗, FINANCE-金融诈骗, OTHER-其他',
    `script_content` TEXT NOT NULL COMMENT '话术内容(脱敏)',
    `target_group` VARCHAR(200) COMMENT '目标群体',
    `common_responses` TEXT COMMENT '常见回应',
    `warning_signs` TEXT COMMENT '预警特征',
    `prevention_tips` TEXT COMMENT '防范提示',
    `source` VARCHAR(200) NOT NULL COMMENT '信息来源',
    `version` VARCHAR(50) DEFAULT '1.0' COMMENT '版本号',
    `is_verified` TINYINT NOT NULL DEFAULT 0 COMMENT '是否验证: 0-未验证, 1-已验证',
    `usage_count` INT NOT NULL DEFAULT 0 COMMENT '使用次数',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_fraud_type` (`fraud_type`),
    KEY `idx_target_group` (`target_group`),
    KEY `idx_is_verified` (`is_verified`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='诈骗话术文本表';

-- 诈骗手法时间线表
DROP TABLE IF EXISTS `fraud_timeline`;
CREATE TABLE `fraud_timeline` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `event_date` DATE NOT NULL COMMENT '事件日期',
    `event_title` VARCHAR(200) NOT NULL COMMENT '事件标题',
    `fraud_type` VARCHAR(50) NOT NULL COMMENT '诈骗类型',
    `event_description` TEXT NOT NULL COMMENT '事件描述',
    `impact_scope` VARCHAR(200) COMMENT '影响范围',
    `source` VARCHAR(200) NOT NULL COMMENT '信息来源',
    `reference_url` VARCHAR(500) COMMENT '参考链接',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_event_date` (`event_date`),
    KEY `idx_fraud_type` (`fraud_type`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='诈骗手法时间线表';

-- 受害者画像数据表
DROP TABLE IF EXISTS `victim_profile`;
CREATE TABLE `victim_profile` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `profile_type` VARCHAR(50) NOT NULL COMMENT '画像类型: AGE-年龄, OCCUPATION-职业, EDUCATION-学历, REGION-地区, BEHAVIOR-行为特征, OTHER-其他',
    `profile_name` VARCHAR(100) NOT NULL COMMENT '画像名称',
    `description` TEXT COMMENT '画像描述',
    `risk_level` TINYINT NOT NULL DEFAULT 1 COMMENT '风险等级: 1-低, 2-中, 3-高',
    `statistical_data` TEXT COMMENT '统计数据(JSON格式)',
    `source` VARCHAR(200) NOT NULL COMMENT '信息来源',
    `is_anonymous` TINYINT NOT NULL DEFAULT 1 COMMENT '是否匿名: 0-否, 1-是',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_profile_type` (`profile_type`),
    KEY `idx_risk_level` (`risk_level`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='受害者画像数据表';

-- 防范建议专家库表
DROP TABLE IF EXISTS `expert_advice`;
CREATE TABLE `expert_advice` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `advice_title` VARCHAR(200) NOT NULL COMMENT '建议标题',
    `fraud_type` VARCHAR(50) NOT NULL COMMENT '针对的诈骗类型',
    `advice_content` TEXT NOT NULL COMMENT '建议内容',
    `expert_name` VARCHAR(100) COMMENT '专家姓名(脱敏)',
    `expert_title` VARCHAR(100) COMMENT '专家头衔',
    `source` VARCHAR(200) NOT NULL COMMENT '信息来源',
    `publish_date` DATE COMMENT '发布日期',
    `priority` TINYINT NOT NULL DEFAULT 1 COMMENT '优先级: 1-低, 2-中, 3-高',
    `view_count` INT NOT NULL DEFAULT 0 COMMENT '查看次数',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_fraud_type` (`fraud_type`),
    KEY `idx_priority` (`priority`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='防范建议专家库表';

-- 测试数据：真实诈骗案例
INSERT INTO `fraud_case` (`case_title`, `case_type`, `occur_time`, `victim_profile`, `fraud_process`, `loss_amount`, `recovery_amount`, `case_status`, `police_info`, `warning_level`, `prevention_advice`, `source`, `is_anonymous`, `is_verified`) VALUES
('冒充公检法诈骗案例', 'TELEPHONE', '2026-03-15 14:30:00', '35岁女性，公司职员，本科学历，首次遭遇诈骗', '受害者接到自称"公安局"的电话，声称其身份信息被盗用参与洗钱活动，要求将资金转入"安全账户"进行"资金审查"。受害者在对方的威胁下，分3次转账共计15万元。', 150000.00, 80000.00, '已破案', '市公安局反诈中心', 3, '接到自称公检法的电话时，应挂断后通过官方渠道核实身份，公检法机关不会要求转账到所谓的安全账户。', '市公安局反诈中心', 1, 1),
('网络刷单诈骗案例', 'NETWORK', '2026-03-20 09:15:00', '22岁大学生，无固定收入，网络兼职需求强烈', '受害者在社交平台看到"刷单兼职"广告，添加对方QQ后，按照对方要求先完成小额刷单并获得返利。随后对方以"任务未完成"为由，要求受害者继续刷单，受害者先后转账共计8万元。', 80000.00, 0.00, '调查中', '区公安局', 3, '任何要求先垫付资金的兼职都是骗局，不要相信"刷单返利"等天上掉馅饼的好事。', '区公安局', 1, 1),
('虚假投资理财诈骗案例', 'INVESTMENT', '2026-03-10 16:45:00', '45岁男性，企业高管，有投资需求', '受害者在微信群被推荐一个"高收益"投资平台，初期小额投资获得高额返利，随后投入50万元后平台无法提现，最终平台关闭。', 500000.00, 0.00, '已报案', '市公安局经侦支队', 3, '高收益必然伴随高风险，投资前要核实平台资质，选择正规金融机构。', '市公安局经侦支队', 1, 1);

-- 测试数据：诈骗话术文本
INSERT INTO `fraud_script` (`script_title`, `fraud_type`, `script_content`, `target_group`, `common_responses`, `warning_signs`, `prevention_tips`, `source`, `is_verified`) VALUES
('冒充公检法话术模板', 'TELEPHONE', '您好，这里是XX市公安局刑警大队，我们接到银行监管部门通报，您的银行账户涉嫌参与一起重大洗钱案件，涉案金额高达500万元。您的账户将在两小时内被冻结，需要您将资金转入我们的安全账户进行核查。如果不配合，后果自负！', '普通市民', '什么？我没参与任何洗钱活动，你们搞错了吧？
请问有什么证据证明我涉嫌洗钱？
好的，我配合调查，请说需要怎么做。', '1. 自称公检法工作人员
2. 声称账户涉嫌违法
3. 要求转账到安全账户
4. 语气强硬，威胁恐吓', '1. 公检法机关不会通过电话办案
2. 不会要求转账到安全账户
3. 可通过110核实身份', '反诈中心', 1),
('刷单返利话术模板', 'NETWORK', '亲，欢迎加入我们的兼职团队！我们是正规电商推广平台，只需要动动手指刷刷单，就能日赚200-500元，无需任何费用。你先试着刷一单，只需充值100元，完成后立即返还120元，立赚20元。现在有个限时活动，充值满10000元送2000元，错过就没有了！', '学生、待业人员、宝妈', '真的能赚这么多钱吗？怎么操作？
天下没有免费的午餐，这不会是骗局吧？
好的，我加入，请告诉我怎么做。', '1. 承诺高收益
2. 要求先垫付资金
3. 用返利诱导加大投入
4. 后期无法提现', '1. 任何要求先交钱的兼职都是骗局
2. 刷单本身就是违法行为
3. 不要相信天上掉馅饼的好事', '反诈中心', 1),
('虚假贷款话术模板', 'NETWORK', '您好，这里是闪电贷平台。看到您申请了贷款，最高可贷20万元，当天放款，月息仅0.3%，无需抵押担保。您只需要下载我们的APP，填写资料即可。不过您需要先缴纳1000元保证金，证明您有还款能力。', '急需资金的人群', '太好了，我正需要钱，怎么申请？
利息这么低？正规贷款平台年化利率都不可能这么低。
你们是正规持牌机构吗？', '1. 承诺低息快速放款
2. 要求提前缴纳费用
3. 无资质要求
4. 贷款额度异常高', '1. 正规贷款不需要提前缴费
2. 检查平台是否有金融牌照
3. 对比正规金融机构利率', '反诈中心', 1);

-- 测试数据：诈骗手法时间线
INSERT INTO `fraud_timeline` (`event_date`, `event_title`, `fraud_type`, `event_description`, `impact_scope`, `source`, `reference_url`) VALUES
('2026-03-01', '新型AI语音诈骗出现', 'TELEPHONE', '诈骗分子利用AI技术模拟受害者亲友的声音进行诈骗，成功率大幅提高。', '全国范围', '公安部反诈中心', 'https://www.mps.gov.cn/n2254536/n6408607/c7854783/content.html'),
('2026-03-10', '虚假投资平台集中爆发', 'INVESTMENT', '多个虚假投资平台同时出现，以"元宇宙"、"数字藏品"等概念为噱头进行诈骗。', '全国范围', '证监会', 'https://www.csrc.gov.cn/csrc/c100028/c100042/202603/t20260310_412345.html'),
('2026-03-20', '校园贷诈骗卷土重来', 'FINANCE', '针对高校学生的校园贷诈骗再次出现，以"低息无抵押"为诱饵。', '高校学生', '教育部', 'https://www.moe.gov.cn/jyb_xwfb/s5147/202603/t20260320_1056789.html');

-- 测试数据：受害者画像数据
INSERT INTO `victim_profile` (`profile_type`, `profile_name`, `description`, `risk_level`, `statistical_data`, `source`, `is_anonymous`) VALUES
('AGE', '18-25岁青年', '该年龄段人群社会经验不足，容易被高收益诱惑，网络接触频繁，风险意识薄弱', 3, '{"victim_count": 1250, "average_loss": 35000, "main_fraud_types": ["刷单返利", "网络贷款", "游戏诈骗"]}', '公安部统计数据', 1),
('OCCUPATION', '企业财务人员', '掌握公司资金，容易成为冒充老板诈骗的目标，涉及金额通常较大', 3, '{"victim_count": 320, "average_loss": 120000, "main_fraud_types": ["冒充老板", "虚假交易", "钓鱼邮件"]}', '企业反诈骗联盟', 1),
('BEHAVIOR', '网络购物高频用户', '经常在网上购物，容易遭遇虚假客服、退款诈骗', 2, '{"victim_count": 850, "average_loss": 12000, "main_fraud_types": ["虚假客服", "退款诈骗", "虚假物流"]}', '电商平台安全中心', 1);

-- 测试数据：防范建议专家库
INSERT INTO `expert_advice` (`advice_title`, `fraud_type`, `advice_content`, `expert_name`, `expert_title`, `source`, `publish_date`, `priority`) VALUES
('如何识别冒充公检法诈骗', 'TELEPHONE', '1. 公检法机关不会通过电话办案
2. 不会要求转账到安全账户
3. 不会通过QQ、微信等社交工具发送法律文书
4. 可通过110核实对方身份
5. 遇到类似情况保持冷静，不要被对方恐吓', '张警官', '反诈专家', '市公安局反诈中心', '2026-03-01', 3),
('网络兼职防骗指南', 'NETWORK', '1. 任何要求先垫付资金的兼职都是骗局
2. 刷单、打字员、手工活等低门槛高收入的兼职基本都是骗局
3. 不要轻易透露个人信息和银行卡信息
4. 遇到可疑情况及时报警', '李教授', '网络安全专家', '高校网络安全中心', '2026-03-15', 3),
('投资理财防骗技巧', 'INVESTMENT', '1. 高收益必然伴随高风险，年化收益率超过10%的投资要谨慎
2. 核实平台资质，选择正规金融机构
3. 了解投资产品的具体内容和风险
4. 不要相信"稳赚不赔"的承诺
5. 定期关注投资平台的动态', '王经理', '金融分析师', '银保监会', '2026-03-10', 3);