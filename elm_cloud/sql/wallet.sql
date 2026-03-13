CREATE DATABASE elm_virtual_wallet;

USE elm_virtual_wallet;


-- ----------------------------
-- Table structure for virtual_wallet
-- ----------------------------
DROP TABLE IF EXISTS `virtual_wallet`;
CREATE TABLE `virtual_wallet`  (
                                   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '虚拟钱包id',
                                   `user_id` bigint NOT NULL,
                                   `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                   `status` tinyint NULL DEFAULT 0 COMMENT '钱包状态 0-正常 1-冻结',
                                   `vip_level` tinyint NULL DEFAULT 0 COMMENT 'vip级别 0-非vip',
                                   `is_deleted` tinyint NULL DEFAULT 0,
                                   `balance` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '余额',
                                   `overdraft_amount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '可透支金额',
                                   `overdrawn_amount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '已透支金额',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for virtual_wallet_loan
-- ----------------------------
DROP TABLE IF EXISTS `virtual_wallet_loan`;
CREATE TABLE `virtual_wallet_loan`  (
                                        `id` bigint NOT NULL AUTO_INCREMENT,
                                        `wallet_id` bigint NOT NULL,
                                        `loan_amount` decimal(10, 2) NOT NULL,
                                        `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                        `repay_time` timestamp NULL DEFAULT NULL,
                                        `rate` decimal(10, 2) NULL DEFAULT NULL,
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for virtual_wallet_transaction
-- ----------------------------
DROP TABLE IF EXISTS `virtual_wallet_transaction`;
CREATE TABLE `virtual_wallet_transaction`  (
                                               `id` bigint NOT NULL AUTO_INCREMENT,
                                               `type` tinyint NULL DEFAULT NULL COMMENT '交易类型 0-支付 1-收款 2-提现 3-充值 4-退款',
                                               `status` tinyint NULL DEFAULT 0 COMMENT '操作金额是否为冻结 0-否 1-是 2-取消标记',
                                               `amount` decimal(10, 2) NOT NULL COMMENT '操作金额',
                                               `from_account` bigint NOT NULL COMMENT '转出钱包 交易类型为充值时值为0',
                                               `to_account` bigint NOT NULL COMMENT '转入钱包 交易类型为提现时值为0',
                                               `is_deleted` tinyint NULL DEFAULT 0,
                                               `fee` decimal(10, 2) NULL DEFAULT NULL COMMENT '手续费或奖励',
                                               `fee_rate` decimal(10, 2) NULL DEFAULT NULL COMMENT '手续费率或奖励率',
                                               `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '交易时间',
                                               `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                               `order_id` bigint NULL DEFAULT NULL COMMENT '若是订单支付的流水，关联订单id，用于冻结操作',
                                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for virtual_wallet_vip_rule
-- ----------------------------
DROP TABLE IF EXISTS `virtual_wallet_vip_rule`;
CREATE TABLE `virtual_wallet_vip_rule`  (
                                            `id` int NOT NULL AUTO_INCREMENT,
                                            `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                            `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
                                            `is_deleted` tinyint NULL DEFAULT 0,
                                            `overdraft_limit` decimal(10, 2) NOT NULL DEFAULT 0.00,
                                            `cost` decimal(10, 2) NOT NULL,
                                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of virtual_wallet_vip_rule
-- ----------------------------
INSERT INTO `virtual_wallet_vip_rule` VALUES (1, '白银会员', '轻松解锁基础额度，应急周转不再难', 0, 1000.00, 20.00);
INSERT INTO `virtual_wallet_vip_rule` VALUES (2, '黄金会员', '额度翻倍，权限升级，满足您更多消费规划', 0, 10000.00, 100.00);
INSERT INTO `virtual_wallet_vip_rule` VALUES (3, '钻石会员', '尊享最高借贷额度，让您资金调度游刃有余', 0, 1000000.00, 1000.00);

SET FOREIGN_KEY_CHECKS = 1;