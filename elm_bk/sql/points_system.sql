-- ============================================
-- 积分系统数据库设计
-- 基于现有elm_v2数据库表结构设计
-- ============================================

-- ----------------------------
-- 1. 积分账户表 (points_account)
-- ----------------------------
DROP TABLE IF EXISTS `points_account`;
CREATE TABLE `points_account` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID（关联users表）',
  `total_points` bigint NOT NULL DEFAULT 0 COMMENT '总积分余额',
  `available_points` bigint NOT NULL DEFAULT 0 COMMENT '可用积分余额',
  `frozen_points` bigint NOT NULL DEFAULT 0 COMMENT '冻结积分（用于订单处理中）',
  `member_level` tinyint NOT NULL DEFAULT 0 COMMENT '会员等级 0-普通 1-白银 2-黄金 3-钻石',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `updater` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_id` (`user_id`) USING BTREE COMMENT '用户ID唯一索引',
  INDEX `idx_member_level` (`member_level`) USING BTREE COMMENT '会员等级索引',
  CONSTRAINT `fk_points_account_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='积分账户表';

-- ----------------------------
-- 2. 积分明细表 (points_transaction)
-- ----------------------------
DROP TABLE IF EXISTS `points_transaction`;
CREATE TABLE `points_transaction` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID（关联users表）',
  `account_id` bigint NOT NULL COMMENT '积分账户ID（关联points_account表）',
  `transaction_type` tinyint NOT NULL COMMENT '交易类型 0-获得 1-消费 2-过期 3-冻结 4-解冻',
  `points_source` tinyint NOT NULL COMMENT '积分来源 0-消费积分 1-促销积分 2-等级积分 3-行为积分 4-兑换商品 5-积分+现金消费',
  `points_change` bigint NOT NULL COMMENT '积分变动数量（正数表示增加，负数表示减少）',
  `points_balance` bigint NOT NULL COMMENT '变动后积分余额',
  `expire_time` timestamp NULL DEFAULT NULL COMMENT '积分过期时间',
  `related_order_id` bigint NULL DEFAULT NULL COMMENT '关联订单ID（关联orders表）',
  `related_food_id` bigint NULL DEFAULT NULL COMMENT '关联商品ID（关联food表，兑换商品时使用）',
  `related_rule_id` bigint NULL DEFAULT NULL COMMENT '关联积分规则ID（关联marketing_points_rule表）',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '交易描述',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creator` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `updater` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id` (`user_id`) USING BTREE COMMENT '用户ID索引',
  INDEX `idx_account_id` (`account_id`) USING BTREE COMMENT '账户ID索引',
  INDEX `idx_transaction_type` (`transaction_type`) USING BTREE COMMENT '交易类型索引',
  INDEX `idx_expire_time` (`expire_time`) USING BTREE COMMENT '过期时间索引',
  INDEX `idx_related_order_id` (`related_order_id`) USING BTREE COMMENT '订单ID索引',
  INDEX `idx_create_time` (`create_time`) USING BTREE COMMENT '创建时间索引',
  CONSTRAINT `fk_points_transaction_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_points_transaction_account` FOREIGN KEY (`account_id`) REFERENCES `points_account` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='积分明细表';

-- ----------------------------
-- 3. 积分过期记录表 (points_expiration)
-- ----------------------------
DROP TABLE IF EXISTS `points_expiration`;
CREATE TABLE `points_expiration` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID（关联users表）',
  `transaction_id` bigint NOT NULL COMMENT '积分明细ID（关联points_transaction表）',
  `points_amount` bigint NOT NULL COMMENT '即将过期积分数量',
  `expire_time` timestamp NOT NULL COMMENT '过期时间（精确到秒）',
  `expire_date` date NOT NULL COMMENT '过期日期（用于按日期查询）',
  `is_expired` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已过期 0-未过期 1-已过期',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id` (`user_id`) USING BTREE COMMENT '用户ID索引',
  INDEX `idx_expire_time` (`expire_time`) USING BTREE COMMENT '过期时间索引',
  INDEX `idx_expire_date` (`expire_date`) USING BTREE COMMENT '过期日期索引',
  INDEX `idx_is_expired` (`is_expired`) USING BTREE COMMENT '过期状态索引',
  INDEX `idx_user_expire` (`user_id`, `expire_time`) USING BTREE COMMENT '用户过期时间联合索引',
  CONSTRAINT `fk_points_expiration_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_points_expiration_transaction` FOREIGN KEY (`transaction_id`) REFERENCES `points_transaction` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='积分过期记录表';

-- ----------------------------
-- 4. 营销系统-积分规则表 (marketing_points_rule)
-- ----------------------------
DROP TABLE IF EXISTS `marketing_points_rule`;
CREATE TABLE `marketing_points_rule` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `rule_name` varchar(100) NOT NULL COMMENT '规则名称',
  `rule_type` tinyint NOT NULL COMMENT '规则类型 0-消费积分 1-促销积分 2-等级积分 3-行为积分',
  `rule_status` tinyint NOT NULL DEFAULT 1 COMMENT '规则状态 0-禁用 1-启用',
  `points_ratio` decimal(10, 4) NULL DEFAULT NULL COMMENT '积分比例（如0.1表示消费1元获得0.1积分）',
  `points_multiplier` decimal(10, 4) NULL DEFAULT NULL COMMENT '积分倍数（促销时使用，如2.0表示双倍积分）',
  `member_level` tinyint NULL DEFAULT NULL COMMENT '适用会员等级（NULL表示所有等级）。对于等级积分(rule_type=2)，表示升级到的目标会员等级（1-白银 2-黄金 3-钻石）',
  `min_order_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '最低订单金额（促销积分使用）',
  `max_order_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '最高订单金额（促销积分使用）',
  `food_id` bigint NULL DEFAULT NULL COMMENT '指定商品ID（关联food表，促销积分使用）',
  `holiday_start` date NULL DEFAULT NULL COMMENT '节假日开始日期',
  `holiday_end` date NULL DEFAULT NULL COMMENT '节假日结束日期',
  `behavior_type` varchar(50) NULL DEFAULT NULL COMMENT '行为类型（like-点赞 collect-收藏 repay_loan-还贷款）',
  `points_amount` bigint NULL DEFAULT NULL COMMENT '固定积分数量（行为积分和等级积分使用。对于等级积分(rule_type=2)，表示升级到该会员等级获得的积分数量）',
  `expire_days` int NULL DEFAULT NULL COMMENT '积分有效期（天数，NULL表示永久有效）',
  `start_time` timestamp NULL DEFAULT NULL COMMENT '规则生效开始时间',
  `end_time` timestamp NULL DEFAULT NULL COMMENT '规则生效结束时间',
  `priority` int NOT NULL DEFAULT 0 COMMENT '优先级（数字越大优先级越高）',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `updater` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_rule_type` (`rule_type`) USING BTREE COMMENT '规则类型索引',
  INDEX `idx_rule_status` (`rule_status`) USING BTREE COMMENT '规则状态索引',
  INDEX `idx_start_end_time` (`start_time`, `end_time`) USING BTREE COMMENT '生效时间索引',
  INDEX `idx_member_level` (`member_level`) USING BTREE COMMENT '会员等级索引',
  CONSTRAINT `fk_marketing_points_rule_food` FOREIGN KEY (`food_id`) REFERENCES `food` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='营销系统-积分规则表';

-- ----------------------------
-- 5. 营销系统-积分兑换规则表 (marketing_points_exchange_rule)
-- ----------------------------
DROP TABLE IF EXISTS `marketing_points_exchange_rule`;
CREATE TABLE `marketing_points_exchange_rule` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `rule_name` varchar(100) NOT NULL COMMENT '规则名称',
  `rule_type` tinyint NOT NULL COMMENT '规则类型 0-积分+现金 1-兑换商品',
  `rule_status` tinyint NOT NULL DEFAULT 1 COMMENT '规则状态 0-禁用 1-启用',
  `exchange_ratio` decimal(10, 4) NULL DEFAULT NULL COMMENT '兑换比例（如10表示10积分=1元，仅积分+现金规则需要）',
  `min_points` bigint NULL DEFAULT NULL COMMENT '最小使用积分数量',
  `max_points` bigint NULL DEFAULT NULL COMMENT '最大使用积分数量（NULL表示不限制）',
  `food_id` bigint NULL DEFAULT NULL COMMENT '商品ID（关联food表，兑换商品时使用）',
  `required_points` bigint NULL DEFAULT NULL COMMENT '所需积分（兑换商品时使用）',
  `stock_quantity` int NULL DEFAULT NULL COMMENT '库存数量（兑换商品时使用）',
  `start_time` timestamp NULL DEFAULT NULL COMMENT '规则生效开始时间',
  `end_time` timestamp NULL DEFAULT NULL COMMENT '规则生效结束时间',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `updater` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_rule_type` (`rule_type`) USING BTREE COMMENT '规则类型索引',
  INDEX `idx_rule_status` (`rule_status`) USING BTREE COMMENT '规则状态索引',
  INDEX `idx_food_id` (`food_id`) USING BTREE COMMENT '商品ID索引',
  CONSTRAINT `fk_marketing_exchange_rule_food` FOREIGN KEY (`food_id`) REFERENCES `food` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='营销系统-积分兑换规则表';

-- ----------------------------
-- 6. 积分兑换订单表 (points_exchange_order)
-- ----------------------------
DROP TABLE IF EXISTS `points_exchange_order`;
CREATE TABLE `points_exchange_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID（关联users表）',
  `order_id` bigint NULL DEFAULT NULL COMMENT '关联订单ID（关联orders表）',
  `food_id` bigint NULL DEFAULT NULL COMMENT '兑换商品ID（关联food表）',
  `points_used` bigint NOT NULL COMMENT '使用积分数量',
  `cash_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '现金金额（预留字段，当前纯积分兑换为0）',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态 0-待处理 1-已完成 2-已取消',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id` (`user_id`) USING BTREE COMMENT '用户ID索引',
  INDEX `idx_order_id` (`order_id`) USING BTREE COMMENT '订单ID索引',
  INDEX `idx_status` (`status`) USING BTREE COMMENT '状态索引',
  CONSTRAINT `fk_points_exchange_order_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_points_exchange_order_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_points_exchange_order_food` FOREIGN KEY (`food_id`) REFERENCES `food` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='积分兑换订单表（仅用于纯积分兑换商品，积分+现金支付不使用此表）';

-- ----------------------------
-- 7. 积分到期预警配置表 (points_expiration_alert_config)
-- ----------------------------
DROP TABLE IF EXISTS `points_expiration_alert_config`;
CREATE TABLE `points_expiration_alert_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `alert_days` int NOT NULL COMMENT '提前预警天数（如7表示提前7天预警）',
  `alert_cycle` int NULL DEFAULT NULL COMMENT '预警周期（天数，如每天、每3天）。NULL表示只预警一次',
  `sms_template` varchar(500) NULL DEFAULT NULL COMMENT '短信模板（支持变量：{username}, {points}, {expireDate}）',
  `is_enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用 0-禁用 1-启用',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='积分到期预警配置表';

-- ----------------------------
-- 8. 积分到期预警记录表 (points_expiration_alert_log)
-- ----------------------------
DROP TABLE IF EXISTS `points_expiration_alert_log`;
CREATE TABLE `points_expiration_alert_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID（关联users表）',
  `points_amount` bigint NOT NULL COMMENT '即将过期积分数量',
  `expire_date` date NOT NULL COMMENT '过期日期',
  `alert_time` timestamp NOT NULL COMMENT '预警时间',
  `next_alert_time` timestamp NULL DEFAULT NULL COMMENT '下次预警时间（根据预警周期计算）',
  `is_sent` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已发送 0-未发送 1-已发送',
  `phone` varchar(20) NULL DEFAULT NULL COMMENT '用户手机号（发送短信时使用）',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id` (`user_id`) USING BTREE COMMENT '用户ID索引',
  INDEX `idx_expire_date` (`expire_date`) USING BTREE COMMENT '过期日期索引',
  INDEX `idx_next_alert_time` (`next_alert_time`) USING BTREE COMMENT '下次预警时间索引',
  INDEX `idx_user_expire` (`user_id`, `expire_date`) USING BTREE COMMENT '用户过期日期联合索引',
  CONSTRAINT `fk_points_alert_log_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='积分到期预警记录表';

-- ----------------------------
-- 9. 扩展orders表字段（积分相关）
-- ----------------------------
ALTER TABLE `orders` 
ADD COLUMN `points_used` bigint NULL DEFAULT 0 COMMENT '使用积分数量' AFTER `payment_method`,
ADD COLUMN `points_amount` bigint NULL DEFAULT 0 COMMENT '获得积分数量' AFTER `points_used`,
ADD COLUMN `points_discount_amount` decimal(10, 2) NULL DEFAULT 0 COMMENT '积分抵扣的现金金额（元）' AFTER `points_amount`;

-- ----------------------------
-- 10. 初始化默认积分规则（可选）
-- ----------------------------
-- 基础消费积分规则：消费1元=1积分
INSERT INTO `marketing_points_rule` (`rule_name`, `rule_type`, `rule_status`, `points_ratio`, `priority`, `creator`, `updater`) 
VALUES ('基础消费积分规则', 0, 1, 1.0000, 0, 1, 1);

-- 积分+现金兑换规则：100积分=1元
INSERT INTO `marketing_points_exchange_rule` (`rule_name`, `rule_type`, `rule_status`, `exchange_ratio`, `creator`, `updater`) 
VALUES ('积分+现金兑换规则', 0, 1, 100.0000, 1, 1);

-- 积分兑换商品规则（rule_type = 1）
-- 商品ID为1，需要40积分
INSERT INTO `marketing_points_exchange_rule` (`rule_name`, `rule_type`, `rule_status`, `food_id`, `required_points`, `stock_quantity`, `creator`, `updater`) 
VALUES ('积分兑换商品', 1, 1, 1, 40, 100, 1, 1);

-- 商品ID为2，需要50积分
INSERT INTO `marketing_points_exchange_rule` (`rule_name`, `rule_type`, `rule_status`, `food_id`, `required_points`, `stock_quantity`, `creator`, `updater`) 
VALUES ('积分兑换商品', 1, 1, 2, 50, 100, 1, 1);

-- 商品ID为3，需要60积分
INSERT INTO `marketing_points_exchange_rule` (`rule_name`, `rule_type`, `rule_status`, `food_id`, `required_points`, `stock_quantity`, `creator`, `updater`) 
VALUES ('积分兑换商品', 1, 1, 3, 60, 100, 1, 1);

-- 商品ID为4，需要70积分
INSERT INTO `marketing_points_exchange_rule` (`rule_name`, `rule_type`, `rule_status`, `food_id`, `required_points`, `stock_quantity`, `creator`, `updater`) 
VALUES ('积分兑换商品', 1, 1, 4, 70, 100, 1, 1);

-- 商品ID为5，需要80积分
INSERT INTO `marketing_points_exchange_rule` (`rule_name`, `rule_type`, `rule_status`, `food_id`, `required_points`, `stock_quantity`, `creator`, `updater`) 
VALUES ('积分兑换商品', 1, 1, 5, 80, 100, 1, 1);

-- 等级积分规则：升级到不同会员等级获得的积分
-- 升级到白银会员（1）：获得100积分
INSERT INTO `marketing_points_rule` (`rule_name`, `rule_type`, `rule_status`, `member_level`, `points_amount`, `expire_days`, `priority`, `creator`, `updater`) 
VALUES ('升级到白银会员积分规则', 2, 1, 1, 100, 30, 0, 1, 1);

-- 升级到黄金会员（2）：获得200积分
INSERT INTO `marketing_points_rule` (`rule_name`, `rule_type`, `rule_status`, `member_level`, `points_amount`, `expire_days`, `priority`, `creator`, `updater`) 
VALUES ('升级到黄金会员积分规则', 2, 1, 2, 200, 30, 0, 1, 1);

-- 升级到钻石会员（3）：获得300积分
INSERT INTO `marketing_points_rule` (`rule_name`, `rule_type`, `rule_status`, `member_level`, `points_amount`, `expire_days`, `priority`, `creator`, `updater`) 
VALUES ('升级到钻石会员积分规则', 2, 1, 3, 300, 30, 0, 1, 1);

-- 行为积分规则：不同行为、不同会员等级获得的积分
-- 点赞商家积分规则（按会员等级）
-- 普通用户（0）：获得10积分
INSERT INTO `marketing_points_rule` (`rule_name`, `rule_type`, `rule_status`, `behavior_type`, `member_level`, `points_amount`, `expire_days`, `priority`, `creator`, `updater`) 
VALUES ('点赞商家积分规则-普通用户', 3, 1, 'like', 0, 10, 30, 0, 1, 1);

-- 白银会员（1）：获得15积分
INSERT INTO `marketing_points_rule` (`rule_name`, `rule_type`, `rule_status`, `behavior_type`, `member_level`, `points_amount`, `expire_days`, `priority`, `creator`, `updater`) 
VALUES ('点赞商家积分规则-白银会员', 3, 1, 'like', 1, 15, 30, 0, 1, 1);

-- 黄金会员（2）：获得20积分
INSERT INTO `marketing_points_rule` (`rule_name`, `rule_type`, `rule_status`, `behavior_type`, `member_level`, `points_amount`, `expire_days`, `priority`, `creator`, `updater`) 
VALUES ('点赞商家积分规则-黄金会员', 3, 1, 'like', 2, 20, 30, 0, 1, 1);

-- 钻石会员（3）：获得30积分
INSERT INTO `marketing_points_rule` (`rule_name`, `rule_type`, `rule_status`, `behavior_type`, `member_level`, `points_amount`, `expire_days`, `priority`, `creator`, `updater`) 
VALUES ('点赞商家积分规则-钻石会员', 3, 1, 'like', 3, 30, 30, 0, 1, 1);

-- 收藏商家积分规则（按会员等级）
-- 普通用户（0）：获得20积分
INSERT INTO `marketing_points_rule` (`rule_name`, `rule_type`, `rule_status`, `behavior_type`, `member_level`, `points_amount`, `expire_days`, `priority`, `creator`, `updater`) 
VALUES ('收藏商家积分规则-普通用户', 3, 1, 'collect', 0, 20, 30, 0, 1, 1);

-- 白银会员（1）：获得30积分
INSERT INTO `marketing_points_rule` (`rule_name`, `rule_type`, `rule_status`, `behavior_type`, `member_level`, `points_amount`, `expire_days`, `priority`, `creator`, `updater`) 
VALUES ('收藏商家积分规则-白银会员', 3, 1, 'collect', 1, 30, 30, 0, 1, 1);

-- 黄金会员（2）：获得40积分
INSERT INTO `marketing_points_rule` (`rule_name`, `rule_type`, `rule_status`, `behavior_type`, `member_level`, `points_amount`, `expire_days`, `priority`, `creator`, `updater`) 
VALUES ('收藏商家积分规则-黄金会员', 3, 1, 'collect', 2, 40, 30, 0, 1, 1);

-- 钻石会员（3）：获得60积分
INSERT INTO `marketing_points_rule` (`rule_name`, `rule_type`, `rule_status`, `behavior_type`, `member_level`, `points_amount`, `expire_days`, `priority`, `creator`, `updater`) 
VALUES ('收藏商家积分规则-钻石会员', 3, 1, 'collect', 3, 60, 30, 0, 1, 1);

-- 还贷款积分规则（按会员等级）
-- 普通用户（0）：获得50积分
INSERT INTO `marketing_points_rule` (`rule_name`, `rule_type`, `rule_status`, `behavior_type`, `member_level`, `points_amount`, `expire_days`, `priority`, `creator`, `updater`) 
VALUES ('还贷款积分规则-普通用户', 3, 1, 'repay_loan', 0, 50, 30, 0, 1, 1);

-- 白银会员（1）：获得75积分
INSERT INTO `marketing_points_rule` (`rule_name`, `rule_type`, `rule_status`, `behavior_type`, `member_level`, `points_amount`, `expire_days`, `priority`, `creator`, `updater`) 
VALUES ('还贷款积分规则-白银会员', 3, 1, 'repay_loan', 1, 75, 30, 0, 1, 1);

-- 黄金会员（2）：获得100积分
INSERT INTO `marketing_points_rule` (`rule_name`, `rule_type`, `rule_status`, `behavior_type`, `member_level`, `points_amount`, `expire_days`, `priority`, `creator`, `updater`) 
VALUES ('还贷款积分规则-黄金会员', 3, 1, 'repay_loan', 2, 100, 30, 0, 1, 1);

-- 钻石会员（3）：获得150积分
INSERT INTO `marketing_points_rule` (`rule_name`, `rule_type`, `rule_status`, `behavior_type`, `member_level`, `points_amount`, `expire_days`, `priority`, `creator`, `updater`) 
VALUES ('还贷款积分规则-钻石会员', 3, 1, 'repay_loan', 3, 150, 30, 0, 1, 1);

-- 默认预警配置：提前3天预警，仅预警一次
INSERT INTO `points_expiration_alert_config` (`alert_days`, `alert_cycle`, `sms_template`, `is_enabled`) 
VALUES (3, NULL, '尊敬的{username}先生/女士，您有{points}积分将于{expireDate}过期，请及时使用。', 1);

-- ============================================
-- 促销积分规则（rule_type = 1）
-- ============================================

-- 1. 节假日双倍积分促销（如十一黄金周）
-- 说明：在2025年10月1日至10月7日期间下单，所有订单获得双倍积分（积分倍数为2.0）
INSERT INTO `marketing_points_rule` (`rule_name`, `rule_type`, `rule_status`, `points_multiplier`, 
    `holiday_start`, `holiday_end`, `expire_days`, `priority`, `creator`, `updater`) 
VALUES ('十一黄金周双倍积分', 1, 1, 2.0, '2025-10-01', '2025-10-07', 30, 10, 1, 1);

-- 2. 高额订单双倍积分促销
-- 说明：订单金额达到200元及以上时，获得双倍积分（积分倍数为2.0）
INSERT INTO `marketing_points_rule` (`rule_name`, `rule_type`, `rule_status`, `points_multiplier`, 
    `min_order_amount`, `expire_days`, `priority`, `creator`, `updater`) 
VALUES ('满200元双倍积分', 1, 1, 2.0, 200.00, 30, 8, 1, 1);

-- 3. 指定商品双倍积分促销
-- 说明：购买商品ID为1的商品时，获得双倍积分（积分倍数为2.0）
-- 注意：food_id需要根据实际商品ID调整
INSERT INTO `marketing_points_rule` (`rule_name`, `rule_type`, `rule_status`, `points_multiplier`, 
    `food_id`, `expire_days`, `priority`, `creator`, `updater`) 
VALUES ('指定商品双倍积分', 1, 1, 2.0, 1, 30, 7, 1, 1);

-- 4. 订单金额区间促销（1.5倍积分）
-- 说明：订单金额在100元到300元之间时，获得1.5倍积分（积分倍数为1.5）
INSERT INTO `marketing_points_rule` (`rule_name`, `rule_type`, `rule_status`, `points_multiplier`, 
    `min_order_amount`, `max_order_amount`, `expire_days`, `priority`, `creator`, `updater`) 
VALUES ('100-300元1.5倍积分', 1, 1, 1.5, 100.00, 300.00, 30, 5, 1, 1);

-- 5. 会员等级专属促销（黄金会员三倍积分）
-- 说明：黄金会员（member_level=2）在节假日期间下单，获得三倍积分（积分倍数为3.0）
INSERT INTO `marketing_points_rule` (`rule_name`, `rule_type`, `rule_status`, `points_multiplier`, 
    `member_level`, `holiday_start`, `holiday_end`, `expire_days`, `priority`, `creator`, `updater`) 
VALUES ('黄金会员节假日三倍积分', 1, 1, 3.0, 2, '2024-12-24', '2024-12-26', 30, 9, 1, 1);

-- 6. 综合条件促销（节假日+高额订单+指定商品）
-- 说明：在节假日期间，订单金额达到150元，且购买了指定商品时，获得2.5倍积分
-- 注意：food_id需要根据实际商品ID调整
INSERT INTO `marketing_points_rule` (`rule_name`, `rule_type`, `rule_status`, `points_multiplier`, 
    `min_order_amount`, `food_id`, `holiday_start`, `holiday_end`, `expire_days`, `priority`, `creator`, `updater`) 
VALUES ('节假日高额订单指定商品2.5倍积分', 1, 1, 2.5, 150.00, 1, '2024-12-20', '2024-12-31', 30, 11, 1, 1);

-- ----------------------------
-- 11. 积分抽奖规则表 (points_lottery_rule)
-- ----------------------------
DROP TABLE IF EXISTS `points_lottery_rule`;
CREATE TABLE `points_lottery_rule` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `rule_name` varchar(100) NOT NULL COMMENT '规则名称',
  `member_level` tinyint NOT NULL COMMENT '适用会员等级 1-白银 2-黄金 3-钻石',
  `prize_type` tinyint NOT NULL COMMENT '奖品类型 0-没中奖 1-固定积分 2-积分翻倍',
  `prize_points` bigint NULL DEFAULT NULL COMMENT '固定积分数量（仅prize_type=1时有效）',
  `prize_multiplier` decimal(10, 2) NULL DEFAULT NULL COMMENT '积分翻倍倍数（仅prize_type=2时有效，如2.0表示翻倍）',
  `probability` int NOT NULL COMMENT '中奖概率（百分比，0-100）',
  `prize_order` int NOT NULL DEFAULT 0 COMMENT '奖品排序（数字越小越靠前，用于前端展示）',
  `prize_description` varchar(200) NULL DEFAULT NULL COMMENT '奖品描述（用于前端展示）',
  `rule_status` tinyint NOT NULL DEFAULT 1 COMMENT '规则状态 0-禁用 1-启用',
  `start_time` timestamp NULL DEFAULT NULL COMMENT '规则生效开始时间',
  `end_time` timestamp NULL DEFAULT NULL COMMENT '规则生效结束时间',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `updater` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_member_level` (`member_level`) USING BTREE COMMENT '会员等级索引',
  INDEX `idx_prize_type` (`prize_type`) USING BTREE COMMENT '奖品类型索引',
  INDEX `idx_rule_status` (`rule_status`) USING BTREE COMMENT '规则状态索引',
  INDEX `idx_member_status` (`member_level`, `rule_status`) USING BTREE COMMENT '会员等级和状态联合索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='积分抽奖规则表';

-- ----------------------------
-- 初始化抽奖规则数据
-- ----------------------------

-- 白银会员抽奖规则
INSERT INTO `points_lottery_rule` (`rule_name`, `member_level`, `prize_type`, `prize_points`, `probability`, `prize_order`, `prize_description`, `rule_status`, `creator`, `updater`) 
VALUES 
('白银会员-没中奖', 1, 0, 0, 50, 1, '感谢参与', 1, 1, 1),
('白银会员-20积分', 1, 1, 20, 30, 2, '+ 20 积分', 1, 1, 1),
('白银会员-50积分', 1, 1, 50, 15, 3, '+ 50 积分', 1, 1, 1),
('白银会员-100积分', 1, 1, 100, 5, 4, '+ 100 积分', 1, 1, 1);

-- 黄金会员抽奖规则
INSERT INTO `points_lottery_rule` (`rule_name`, `member_level`, `prize_type`, `prize_points`, `prize_multiplier`, `probability`, `prize_order`, `prize_description`, `rule_status`, `creator`, `updater`) 
VALUES 
('黄金会员-没中奖', 2, 0, 0, NULL, 40, 1, '感谢参与', 1, 1, 1),
('黄金会员-50积分', 2, 1, 50, NULL, 30, 2, '+ 50 积分', 1, 1, 1),
('黄金会员-100积分', 2, 1, 100, NULL, 20, 3, '+ 100 积分', 1, 1, 1),
('黄金会员-积分翻倍', 2, 2, NULL, 2.0, 10, 4, '积分翻倍', 1, 1, 1);

-- 钻石会员抽奖规则
INSERT INTO `points_lottery_rule` (`rule_name`, `member_level`, `prize_type`, `prize_points`, `prize_multiplier`, `probability`, `prize_order`, `prize_description`, `rule_status`, `creator`, `updater`) 
VALUES 
('钻石会员-没中奖', 3, 0, 0, NULL, 30, 1, '感谢参与', 1, 1, 1),
('钻石会员-100积分', 3, 1, 100, NULL, 30, 2, '+ 100 积分', 1, 1, 1),
('钻石会员-200积分', 3, 1, 200, NULL, 25, 3, '+ 200 积分', 1, 1, 1),
('钻石会员-积分翻倍', 3, 2, NULL, 2.0, 15, 4, '积分翻倍', 1, 1, 1);

-- ----------------------------
-- 12. 积分抽奖记录表 (points_lottery_record)
-- ----------------------------
DROP TABLE IF EXISTS `points_lottery_record`;
CREATE TABLE `points_lottery_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID（关联users表）',
  `member_level` tinyint NOT NULL COMMENT '抽奖时的会员等级 0-普通 1-白银 2-黄金 3-钻石',
  `lottery_type` tinyint NOT NULL COMMENT '抽奖类型 0-没中奖 1-固定积分 2-积分翻倍',
  `points_reward` bigint NULL DEFAULT NULL COMMENT '获得的积分数量（没中奖时为0，积分翻倍时为NULL）',
  `points_multiplier` decimal(10, 2) NULL DEFAULT NULL COMMENT '积分翻倍倍数（仅积分翻倍时使用）',
  `original_points` bigint NULL DEFAULT NULL COMMENT '翻倍前的积分数量（仅积分翻倍时使用）',
  `lottery_month` varchar(7) NOT NULL COMMENT '抽奖月份（格式：YYYY-MM，用于统计每月抽奖次数）',
  `transaction_id` bigint NULL DEFAULT NULL COMMENT '关联积分明细ID（关联points_transaction表，中奖时记录）',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `updater` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id` (`user_id`) USING BTREE COMMENT '用户ID索引',
  INDEX `idx_lottery_month` (`lottery_month`) USING BTREE COMMENT '抽奖月份索引',
  INDEX `idx_user_month` (`user_id`, `lottery_month`) USING BTREE COMMENT '用户月份联合索引（用于统计每月抽奖次数）',
  INDEX `idx_create_time` (`create_time`) USING BTREE COMMENT '创建时间索引',
  CONSTRAINT `fk_points_lottery_record_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_points_lottery_record_transaction` FOREIGN KEY (`transaction_id`) REFERENCES `points_transaction` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='积分抽奖记录表';

-- ============================================
-- 数据库设计说明
-- ============================================
-- 1. 所有表遵循现有数据库的命名规范和字段设计风格
-- 2. 使用统一的审计字段：create_time, update_time, creator, updater, is_deleted
-- 3. 外键关联遵循现有表的关联方式
-- 4. 索引设计考虑了查询性能
-- 5. 字段类型与现有表保持一致（bigint, decimal(10,2), timestamp等）
-- ============================================

