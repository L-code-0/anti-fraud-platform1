-- 专家案例分析表
CREATE TABLE IF NOT EXISTS `expert_analysis` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title` VARCHAR(200) NOT NULL COMMENT '案例标题',
    `type` TINYINT NOT NULL DEFAULT 1 COMMENT '案例类型：1-典型案例 2-新型诈骗',
    `summary` VARCHAR(500) NOT NULL COMMENT '案例摘要',
    `content` TEXT COMMENT '详细内容',
    `expert_id` BIGINT NOT NULL COMMENT '发布专家ID',
    `expert_name` VARCHAR(50) COMMENT '发布专家姓名',
    `view_count` INT NOT NULL DEFAULT 0 COMMENT '阅读量',
    `thumb_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-草稿 1-已发布',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    INDEX `idx_expert_id` (`expert_id`),
    INDEX `idx_type` (`type`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='专家案例分析表';

-- 专家建议表
CREATE TABLE IF NOT EXISTS `expert_advice` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title` VARCHAR(200) NOT NULL COMMENT '建议标题',
    `category` VARCHAR(50) NOT NULL COMMENT '建议分类',
    `content` TEXT NOT NULL COMMENT '建议内容',
    `expert_id` BIGINT NOT NULL COMMENT '发布专家ID',
    `expert_name` VARCHAR(50) COMMENT '发布专家姓名',
    `view_count` INT NOT NULL DEFAULT 0 COMMENT '阅读量',
    `thumb_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-草稿 1-已发布',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    INDEX `idx_expert_id` (`expert_id`),
    INDEX `idx_category` (`category`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='专家建议表';

-- 插入测试案例分析数据
INSERT INTO `expert_analysis` (`title`, `type`, `summary`, `content`, `expert_id`, `expert_name`, `view_count`, `thumb_count`, `create_time`) VALUES
('冒充客服诈骗：警惕"退款"陷阱', 1, '受害人接到自称电商客服的电话，称其订单异常需要退款，按照对方指示操作后被骗。', '## 案例背景\n\n2024年3月，张女士接到一个自称某电商平台客服的电话，对方称其购买的化妆品存在质量问题，需要进行退款处理。\n\n## 诈骗过程\n\n1. 骗子通过非法渠道获取购物信息，精准说出张女士购买的商品详情\n2. 以"退款需要走流程"为由，诱导张女士下载指定APP\n3. 通过APP屏幕共享功能，获取张女士银行卡信息和验证码\n4. 最终转走张女士账户内5万元\n\n## 防范要点\n\n- 正规电商退款会在APP内直接完成，不会要求下载其他APP\n- 不要开启屏幕共享给陌生人\n- 验证码是资金安全的最后一道防线，绝不能告诉他人', 1, '专家李明', 1234, 89, NOW()),

('AI换脸诈骗新手段曝光', 2, '近期出现利用AI换脸技术冒充熟人诈骗的案件，骗子通过视频通话取得信任后实施诈骗。', '## 新型诈骗手段分析\n\nAI换脸诈骗是近期出现的新型诈骗方式，骗子利用深度伪造技术，在视频通话中冒充受害人的亲友，从而骗取信任实施诈骗。\n\n## 技术原理\n\n骗子通过社交媒体收集目标人物的照片和视频，使用AI技术生成逼真的换脸视频，在视频通话中冒充他人。\n\n## 识别方法\n\n1. 要求对方做一些特定动作，如遮挡脸部特定部位\n2. 观察视频是否有不自然的闪烁或模糊\n3. 通过其他渠道（电话、微信）核实身份\n4. 询问只有真正认识的人才知道的细节', 2, '专家王芳', 2345, 156, NOW()),

('校园贷诈骗案例分析', 1, '不法分子以"低息贷款"为诱饵，诱导大学生办理贷款，实际收取高额利息和手续费。', '## 案例概述\n\n某高校学生小李因急需用钱，在网上看到"无抵押、低息、快速放款"的广告，联系后对方要求先缴纳"保证金"、"手续费"等费用，最终被骗8000元。\n\n## 常见套路\n\n1. 以"低息"为诱饵吸引学生\n2. 要求先缴纳各种费用\n3. 虚假合同、隐藏条款\n4. 暴力催收、威胁恐吓\n\n## 防范建议\n\n- 有资金需求应通过正规金融机构办理\n- 不要轻信网络贷款广告\n- 保护好个人身份证件和银行卡信息', 1, '专家张强', 1890, 112, NOW()),

('投资理财诈骗深度剖析', 1, '骗子通过社交媒体添加好友，推荐"稳赚不赔"的投资项目，前期小额返利，后期大额诈骗。', '## 诈骗特征\n\n投资理财诈骗是目前最高发的诈骗类型之一，骗子通常通过以下步骤实施诈骗：\n\n1. **引流阶段**：通过社交媒体、交友软件添加目标\n2. **建立信任**：伪装成功人士，展示虚假收益\n3. **小利诱导**：让受害者先小额投资并返利\n4. **大额诈骗**：诱导追加投资后失联\n\n## 识别要点\n\n- 承诺"稳赚不赔"的都是诈骗\n- 正规投资不会有"内部渠道"\n- 凡是要求转账到个人账户的都是诈骗', 3, '专家刘洋', 3456, 234, NOW());

-- 插入测试专家建议数据
INSERT INTO `expert_advice` (`title`, `category`, `content`, `expert_id`, `expert_name`, `view_count`, `thumb_count`, `create_time`) VALUES
('如何识别冒充公检法诈骗', '防骗技巧', '公检法机关不会通过电话、微信、QQ等方式办案，也不会设立"安全账户"。如接到类似电话，请立即挂断并拨打110核实。记住三点：不轻信、不转账、不泄露个人信息。', 1, '专家李明', 3456, 234, NOW()),

('警惕网络购物退款陷阱', '安全提醒', '正规电商平台的退款流程会在APP内完成，不会要求你点击陌生链接或提供验证码。遇到"客服"主动联系时，请通过官方渠道核实。记住：退款不需要你操作任何转账！', 2, '专家王芳', 2345, 156, NOW()),

('刷单返利都是诈骗', '案例警示', '刷单本身就是违法行为，且绝大多数刷单返利都是诈骗。前几笔小额返利只是为了让你放松警惕，后续大额刷单将血本无归。记住：天上不会掉馅饼，所有刷单都是诈骗！', 1, '专家张强', 4567, 345, NOW()),

('反诈法实施后你的权益更有保障', '政策解读', '《中华人民共和国反电信网络诈骗法》于2022年12月1日起施行，对电信网络诈骗行为进行了更严厉的打击和惩处。公民应了解自己的权利和义务，发现诈骗行为及时举报，共同维护网络安全环境。', 3, '专家刘洋', 1890, 98, NOW()),

('银行卡被盗刷怎么办', '安全提醒', '1. 立即致电银行客服冻结账户\n2. 保存好交易记录等证据\n3. 拨打110报警\n4. 不要点击任何陌生链接或下载不明软件\n5. 定期修改银行卡密码，开启短信提醒', 2, '专家王芳', 2890, 178, NOW());
