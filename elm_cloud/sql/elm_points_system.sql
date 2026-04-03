/*
 Navicat Premium Dump SQL

 Source Server         : TXCloud1
 Source Server Type    : MySQL
 Source Server Version : 80044 (8.0.44)
 Source Host           : REDACTED_IP:3306
 Source Schema         : elm_points_system

 Target Server Type    : MySQL
 Target Server Version : 80044 (8.0.44)
 File Encoding         : 65001

 Date: 03/04/2026 22:39:01
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for marketing_points_exchange_rule
-- ----------------------------
DROP TABLE IF EXISTS `marketing_points_exchange_rule`;
CREATE TABLE `marketing_points_exchange_rule`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ä¸»é”®ID',
  `rule_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'è§„åˆ™åç§°',
  `rule_type` tinyint NOT NULL COMMENT 'è§„åˆ™ç±»åž‹ 0-ç§¯åˆ†+çŽ°é‡‘ 1-å…‘æ¢å•†å“',
  `rule_status` tinyint NOT NULL DEFAULT 1 COMMENT 'è§„åˆ™çŠ¶æ€ 0-ç¦ç”¨ 1-å¯ç”¨',
  `exchange_ratio` decimal(10, 4) NULL DEFAULT NULL COMMENT 'å…‘æ¢æ¯”ä¾‹ï¼ˆå¦‚10è¡¨ç¤º10ç§¯åˆ†=1å…ƒï¼Œä»…ç§¯åˆ†+çŽ°é‡‘è§„åˆ™éœ€è¦ï¼‰',
  `min_points` bigint NULL DEFAULT NULL COMMENT 'æœ€å°ä½¿ç”¨ç§¯åˆ†æ•°é‡',
  `max_points` bigint NULL DEFAULT NULL COMMENT 'æœ€å¤§ä½¿ç”¨ç§¯åˆ†æ•°é‡ï¼ˆNULLè¡¨ç¤ºä¸é™åˆ¶ï¼‰',
  `food_id` bigint NULL DEFAULT NULL COMMENT 'å•†å“IDï¼ˆå…³è”foodè¡¨ï¼Œå…‘æ¢å•†å“æ—¶ä½¿ç”¨ï¼‰',
  `required_points` bigint NULL DEFAULT NULL COMMENT 'æ‰€éœ€ç§¯åˆ†ï¼ˆå…‘æ¢å•†å“æ—¶ä½¿ç”¨ï¼‰',
  `stock_quantity` int NULL DEFAULT NULL COMMENT 'åº“å­˜æ•°é‡ï¼ˆå…‘æ¢å•†å“æ—¶ä½¿ç”¨ï¼‰',
  `start_time` timestamp NULL DEFAULT NULL COMMENT 'è§„åˆ™ç”Ÿæ•ˆå¼€å§‹æ—¶é—´',
  `end_time` timestamp NULL DEFAULT NULL COMMENT 'è§„åˆ™ç”Ÿæ•ˆç»“æŸæ—¶é—´',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'åˆ›å»ºæ—¶é—´',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'æ›´æ–°æ—¶é—´',
  `creator` bigint NULL DEFAULT NULL COMMENT 'åˆ›å»ºäººID',
  `updater` bigint NULL DEFAULT NULL COMMENT 'æ›´æ–°äººID',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'æ˜¯å¦åˆ é™¤',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_rule_type`(`rule_type` ASC) USING BTREE COMMENT 'è§„åˆ™ç±»åž‹ç´¢å¼•',
  INDEX `idx_rule_status`(`rule_status` ASC) USING BTREE COMMENT 'è§„åˆ™çŠ¶æ€ç´¢å¼•',
  INDEX `idx_food_id`(`food_id` ASC) USING BTREE COMMENT 'å•†å“IDç´¢å¼•'
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'è¥é”€ç³»ç»Ÿ-ç§¯åˆ†å…‘æ¢è§„åˆ™è¡¨' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of marketing_points_exchange_rule
-- ----------------------------
INSERT INTO `marketing_points_exchange_rule` VALUES (1, '积分+现金兑换规则', 0, 1, 100.0000, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-04-02 00:34:12', '2026-04-02 00:34:12', 1, 1, 0);
INSERT INTO `marketing_points_exchange_rule` VALUES (2, '积分兑换商品', 1, 1, NULL, NULL, NULL, 1, 40, 100, NULL, NULL, '2026-04-02 00:34:12', '2026-04-02 00:34:12', 1, 1, 0);
INSERT INTO `marketing_points_exchange_rule` VALUES (3, '积分兑换商品', 1, 1, NULL, NULL, NULL, 2, 50, 100, NULL, NULL, '2026-04-02 00:34:12', '2026-04-02 00:34:12', 1, 1, 0);
INSERT INTO `marketing_points_exchange_rule` VALUES (4, '积分兑换商品', 1, 1, NULL, NULL, NULL, 3, 60, 100, NULL, NULL, '2026-04-02 00:34:12', '2026-04-02 00:34:12', 1, 1, 0);
INSERT INTO `marketing_points_exchange_rule` VALUES (5, '积分兑换商品', 1, 1, NULL, NULL, NULL, 4, 70, 100, NULL, NULL, '2026-04-02 00:34:12', '2026-04-02 00:34:12', 1, 1, 0);
INSERT INTO `marketing_points_exchange_rule` VALUES (6, '积分兑换商品', 1, 1, NULL, NULL, NULL, 5, 80, 100, NULL, NULL, '2026-04-02 00:34:12', '2026-04-02 00:34:12', 1, 1, 0);

-- ----------------------------
-- Table structure for marketing_points_rule
-- ----------------------------
DROP TABLE IF EXISTS `marketing_points_rule`;
CREATE TABLE `marketing_points_rule`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ä¸»é”®ID',
  `rule_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'è§„åˆ™åç§°',
  `rule_type` tinyint NOT NULL COMMENT 'è§„åˆ™ç±»åž‹ 0-æ¶ˆè´¹ç§¯åˆ† 1-ä¿ƒé”€ç§¯åˆ† 2-ç­‰çº§ç§¯åˆ† 3-è¡Œä¸ºç§¯åˆ†',
  `rule_status` tinyint NOT NULL DEFAULT 1 COMMENT 'è§„åˆ™çŠ¶æ€ 0-ç¦ç”¨ 1-å¯ç”¨',
  `points_ratio` decimal(10, 4) NULL DEFAULT NULL COMMENT 'ç§¯åˆ†æ¯”ä¾‹ï¼ˆå¦‚0.1è¡¨ç¤ºæ¶ˆè´¹1å…ƒèŽ·å¾—0.1ç§¯åˆ†ï¼‰',
  `points_multiplier` decimal(10, 4) NULL DEFAULT NULL COMMENT 'ç§¯åˆ†å€æ•°ï¼ˆä¿ƒé”€æ—¶ä½¿ç”¨ï¼Œå¦‚2.0è¡¨ç¤ºåŒå€ç§¯åˆ†ï¼‰',
  `member_level` tinyint NULL DEFAULT NULL COMMENT 'é€‚ç”¨ä¼šå‘˜ç­‰çº§ï¼ˆNULLè¡¨ç¤ºæ‰€æœ‰ç­‰çº§ï¼‰ã€‚å¯¹äºŽç­‰çº§ç§¯åˆ†(rule_type=2)ï¼Œè¡¨ç¤ºå‡çº§åˆ°çš„ç›®æ ‡ä¼šå‘˜ç­‰çº§ï¼ˆ1-ç™½é“¶ 2-é»„é‡‘ 3-é’»çŸ³ï¼‰',
  `min_order_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT 'æœ€ä½Žè®¢å•é‡‘é¢ï¼ˆä¿ƒé”€ç§¯åˆ†ä½¿ç”¨ï¼‰',
  `max_order_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT 'æœ€é«˜è®¢å•é‡‘é¢ï¼ˆä¿ƒé”€ç§¯åˆ†ä½¿ç”¨ï¼‰',
  `holiday_start` date NULL DEFAULT NULL COMMENT 'èŠ‚å‡æ—¥å¼€å§‹æ—¥æœŸ',
  `holiday_end` date NULL DEFAULT NULL COMMENT 'èŠ‚å‡æ—¥ç»“æŸæ—¥æœŸ',
  `behavior_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'è¡Œä¸ºç±»åž‹ï¼ˆlike-ç‚¹èµž collect-æ”¶è— repay_loan-è¿˜è´·æ¬¾ï¼‰',
  `points_amount` bigint NULL DEFAULT NULL COMMENT 'å›ºå®šç§¯åˆ†æ•°é‡ï¼ˆè¡Œä¸ºç§¯åˆ†å’Œç­‰çº§ç§¯åˆ†ä½¿ç”¨ã€‚å¯¹äºŽç­‰çº§ç§¯åˆ†(rule_type=2)ï¼Œè¡¨ç¤ºå‡çº§åˆ°è¯¥ä¼šå‘˜ç­‰çº§èŽ·å¾—çš„ç§¯åˆ†æ•°é‡ï¼‰',
  `expire_days` int NULL DEFAULT NULL COMMENT 'ç§¯åˆ†æœ‰æ•ˆæœŸï¼ˆå¤©æ•°ï¼ŒNULLè¡¨ç¤ºæ°¸ä¹…æœ‰æ•ˆï¼‰',
  `start_time` timestamp NULL DEFAULT NULL COMMENT 'è§„åˆ™ç”Ÿæ•ˆå¼€å§‹æ—¶é—´',
  `end_time` timestamp NULL DEFAULT NULL COMMENT 'è§„åˆ™ç”Ÿæ•ˆç»“æŸæ—¶é—´',
  `priority` int NOT NULL DEFAULT 0 COMMENT 'ä¼˜å…ˆçº§ï¼ˆæ•°å­—è¶Šå¤§ä¼˜å…ˆçº§è¶Šé«˜ï¼‰',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'åˆ›å»ºæ—¶é—´',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'æ›´æ–°æ—¶é—´',
  `creator` bigint NULL DEFAULT NULL COMMENT 'åˆ›å»ºäººID',
  `updater` bigint NULL DEFAULT NULL COMMENT 'æ›´æ–°äººID',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'æ˜¯å¦åˆ é™¤',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_rule_type`(`rule_type` ASC) USING BTREE COMMENT 'è§„åˆ™ç±»åž‹ç´¢å¼•',
  INDEX `idx_rule_status`(`rule_status` ASC) USING BTREE COMMENT 'è§„åˆ™çŠ¶æ€ç´¢å¼•',
  INDEX `idx_start_end_time`(`start_time` ASC, `end_time` ASC) USING BTREE COMMENT 'ç”Ÿæ•ˆæ—¶é—´ç´¢å¼•',
  INDEX `idx_member_level`(`member_level` ASC) USING BTREE COMMENT 'ä¼šå‘˜ç­‰çº§ç´¢å¼•'
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'è¥é”€ç³»ç»Ÿ-ç§¯åˆ†è§„åˆ™è¡¨' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of marketing_points_rule
-- ----------------------------
INSERT INTO `marketing_points_rule` VALUES (1, '基础消费积分规则', 0, 1, 1.0000, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, '2026-04-02 00:34:12', '2026-04-02 00:34:12', 1, 1, 0);
INSERT INTO `marketing_points_rule` VALUES (2, '升级到白银会员积分规则', 2, 1, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL, 100, 30, NULL, NULL, 0, '2026-04-02 00:34:12', '2026-04-02 00:34:12', 1, 1, 0);
INSERT INTO `marketing_points_rule` VALUES (3, '升级到黄金会员积分规则', 2, 1, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, 200, 30, NULL, NULL, 0, '2026-04-02 00:34:12', '2026-04-02 00:34:12', 1, 1, 0);
INSERT INTO `marketing_points_rule` VALUES (4, '升级到钻石会员积分规则', 2, 1, NULL, NULL, 3, NULL, NULL, NULL, NULL, NULL, 300, 30, NULL, NULL, 0, '2026-04-02 00:34:12', '2026-04-02 00:34:12', 1, 1, 0);
INSERT INTO `marketing_points_rule` VALUES (5, '点赞商家积分规则-普通用户', 3, 1, NULL, NULL, 0, NULL, NULL, NULL, NULL, 'like', 10, 30, NULL, NULL, 0, '2026-04-02 00:34:12', '2026-04-02 00:34:12', 1, 1, 0);
INSERT INTO `marketing_points_rule` VALUES (6, '点赞商家积分规则-白银会员', 3, 1, NULL, NULL, 1, NULL, NULL, NULL, NULL, 'like', 15, 30, NULL, NULL, 0, '2026-04-02 00:34:12', '2026-04-02 00:34:12', 1, 1, 0);
INSERT INTO `marketing_points_rule` VALUES (7, '点赞商家积分规则-黄金会员', 3, 1, NULL, NULL, 2, NULL, NULL, NULL, NULL, 'like', 20, 30, NULL, NULL, 0, '2026-04-02 00:34:12', '2026-04-02 00:34:12', 1, 1, 0);
INSERT INTO `marketing_points_rule` VALUES (8, '点赞商家积分规则-钻石会员', 3, 1, NULL, NULL, 3, NULL, NULL, NULL, NULL, 'like', 30, 30, NULL, NULL, 0, '2026-04-02 00:34:12', '2026-04-02 00:34:12', 1, 1, 0);
INSERT INTO `marketing_points_rule` VALUES (9, '收藏商家积分规则-普通用户', 3, 1, NULL, NULL, 0, NULL, NULL, NULL, NULL, 'collect', 20, 30, NULL, NULL, 0, '2026-04-02 00:34:12', '2026-04-02 00:34:12', 1, 1, 0);
INSERT INTO `marketing_points_rule` VALUES (10, '收藏商家积分规则-白银会员', 3, 1, NULL, NULL, 1, NULL, NULL, NULL, NULL, 'collect', 30, 30, NULL, NULL, 0, '2026-04-02 00:34:12', '2026-04-02 00:34:12', 1, 1, 0);
INSERT INTO `marketing_points_rule` VALUES (11, '收藏商家积分规则-黄金会员', 3, 1, NULL, NULL, 2, NULL, NULL, NULL, NULL, 'collect', 40, 30, NULL, NULL, 0, '2026-04-02 00:34:12', '2026-04-02 00:34:12', 1, 1, 0);
INSERT INTO `marketing_points_rule` VALUES (12, '收藏商家积分规则-钻石会员', 3, 1, NULL, NULL, 3, NULL, NULL, NULL, NULL, 'collect', 60, 30, NULL, NULL, 0, '2026-04-02 00:34:12', '2026-04-02 00:34:12', 1, 1, 0);
INSERT INTO `marketing_points_rule` VALUES (13, '还贷款积分规则-普通用户', 3, 1, NULL, NULL, 0, NULL, NULL, NULL, NULL, 'repay_loan', 50, 30, NULL, NULL, 0, '2026-04-02 00:34:12', '2026-04-02 00:34:12', 1, 1, 0);
INSERT INTO `marketing_points_rule` VALUES (14, '还贷款积分规则-白银会员', 3, 1, NULL, NULL, 1, NULL, NULL, NULL, NULL, 'repay_loan', 75, 30, NULL, NULL, 0, '2026-04-02 00:34:12', '2026-04-02 00:34:12', 1, 1, 0);
INSERT INTO `marketing_points_rule` VALUES (15, '还贷款积分规则-黄金会员', 3, 1, NULL, NULL, 2, NULL, NULL, NULL, NULL, 'repay_loan', 100, 30, NULL, NULL, 0, '2026-04-02 00:34:12', '2026-04-02 00:34:12', 1, 1, 0);
INSERT INTO `marketing_points_rule` VALUES (16, '还贷款积分规则-钻石会员', 3, 1, NULL, NULL, 3, NULL, NULL, NULL, NULL, 'repay_loan', 150, 30, NULL, NULL, 0, '2026-04-02 00:34:12', '2026-04-02 00:34:12', 1, 1, 0);
INSERT INTO `marketing_points_rule` VALUES (17, '十一黄金周双倍积分', 1, 1, NULL, 2.0000, NULL, NULL, NULL, '2025-10-01', '2025-10-07', NULL, NULL, 30, NULL, NULL, 10, '2026-04-02 00:34:12', '2026-04-02 00:34:12', 1, 1, 0);
INSERT INTO `marketing_points_rule` VALUES (18, '满200元双倍积分', 1, 1, NULL, 2.0000, NULL, 200.00, NULL, NULL, NULL, NULL, NULL, 30, NULL, NULL, 8, '2026-04-02 00:34:12', '2026-04-02 00:34:12', 1, 1, 0);
INSERT INTO `marketing_points_rule` VALUES (19, '指定商品双倍积分', 1, 1, NULL, 2.0000, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 30, NULL, NULL, 7, '2026-04-02 00:34:12', '2026-04-02 00:34:12', 1, 1, 0);
INSERT INTO `marketing_points_rule` VALUES (20, '100-300元1.5倍积分', 1, 1, NULL, 1.5000, NULL, 100.00, 300.00, NULL, NULL, NULL, NULL, 30, NULL, NULL, 5, '2026-04-02 00:34:12', '2026-04-02 00:34:12', 1, 1, 0);
INSERT INTO `marketing_points_rule` VALUES (21, '黄金会员节假日三倍积分', 1, 1, NULL, 3.0000, 2, NULL, NULL, '2024-12-24', '2024-12-26', NULL, NULL, 30, NULL, NULL, 9, '2026-04-02 00:34:12', '2026-04-02 00:34:12', 1, 1, 0);
INSERT INTO `marketing_points_rule` VALUES (22, '节假日高额订单指定商品2.5倍积分', 1, 1, NULL, 2.5000, NULL, 150.00, NULL, '2024-12-20', '2024-12-31', NULL, NULL, 30, NULL, NULL, 11, '2026-04-02 00:34:12', '2026-04-02 00:34:12', 1, 1, 0);

-- ----------------------------
-- Table structure for marketing_points_rule_food
-- ----------------------------
DROP TABLE IF EXISTS `marketing_points_rule_food`;
CREATE TABLE `marketing_points_rule_food`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ä¸»é”®ID',
  `rule_id` bigint NOT NULL COMMENT 'ç§¯åˆ†è§„åˆ™IDï¼ˆå…³è”marketing_points_ruleè¡¨ï¼‰',
  `food_id` bigint NOT NULL COMMENT 'å•†å“IDï¼ˆå…³è”foodè¡¨ï¼‰',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'åˆ›å»ºæ—¶é—´',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'æ›´æ–°æ—¶é—´',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'æ˜¯å¦åˆ é™¤',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_rule_food`(`rule_id` ASC, `food_id` ASC, `is_deleted` ASC) USING BTREE COMMENT 'è§„åˆ™å•†å“å”¯ä¸€ç´¢å¼•ï¼ˆè€ƒè™‘è½¯åˆ é™¤ï¼‰',
  INDEX `idx_rule_id`(`rule_id` ASC) USING BTREE COMMENT 'è§„åˆ™IDç´¢å¼•',
  INDEX `idx_food_id`(`food_id` ASC) USING BTREE COMMENT 'å•†å“IDç´¢å¼•'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'ä¿ƒé”€ç§¯åˆ†è§„åˆ™ä¸Žå•†å“å…³è”è¡¨' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of marketing_points_rule_food
-- ----------------------------

-- ----------------------------
-- Table structure for points_account
-- ----------------------------
DROP TABLE IF EXISTS `points_account`;
CREATE TABLE `points_account`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ä¸»é”®ID',
  `user_id` bigint NOT NULL COMMENT 'ç”¨æˆ·IDï¼ˆå…³è”usersè¡¨ï¼‰',
  `total_points` bigint NOT NULL DEFAULT 0 COMMENT 'æ€»ç§¯åˆ†ä½™é¢',
  `available_points` bigint NOT NULL DEFAULT 0 COMMENT 'å¯ç”¨ç§¯åˆ†ä½™é¢',
  `frozen_points` bigint NOT NULL DEFAULT 0 COMMENT 'å†»ç»“ç§¯åˆ†ï¼ˆç”¨äºŽè®¢å•å¤„ç†ä¸­ï¼‰',
  `member_level` tinyint NOT NULL DEFAULT 0 COMMENT 'ä¼šå‘˜ç­‰çº§ 0-æ™®é€š 1-ç™½é“¶ 2-é»„é‡‘ 3-é’»çŸ³',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'åˆ›å»ºæ—¶é—´',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'æ›´æ–°æ—¶é—´',
  `creator` bigint NULL DEFAULT NULL COMMENT 'åˆ›å»ºäººID',
  `updater` bigint NULL DEFAULT NULL COMMENT 'æ›´æ–°äººID',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'æ˜¯å¦åˆ é™¤',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id` ASC) USING BTREE COMMENT 'ç”¨æˆ·IDå”¯ä¸€ç´¢å¼•',
  INDEX `idx_member_level`(`member_level` ASC) USING BTREE COMMENT 'ä¼šå‘˜ç­‰çº§ç´¢å¼•'
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'ç§¯åˆ†è´¦æˆ·è¡¨' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of points_account
-- ----------------------------
INSERT INTO `points_account` VALUES (1, 7, 392, 28, 364, 0, '2026-04-02 08:45:35', '2026-04-03 20:35:17', 7, 7, 0);
INSERT INTO `points_account` VALUES (2, 8, 0, 0, 0, 0, '2026-04-02 08:51:26', '2026-04-02 08:51:26', 8, 8, 0);
INSERT INTO `points_account` VALUES (3, 9, 0, 0, 0, 0, '2026-04-02 08:52:08', '2026-04-02 08:52:08', 9, 9, 0);
INSERT INTO `points_account` VALUES (4, 10, 0, 0, 0, 0, '2026-04-03 20:23:16', '2026-04-03 20:23:16', 10, 10, 0);
INSERT INTO `points_account` VALUES (5, 11, 0, 0, 0, 0, '2026-04-03 20:26:18', '2026-04-03 20:26:18', 11, 11, 0);

-- ----------------------------
-- Table structure for points_exchange_order
-- ----------------------------
DROP TABLE IF EXISTS `points_exchange_order`;
CREATE TABLE `points_exchange_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ä¸»é”®ID',
  `user_id` bigint NOT NULL COMMENT 'ç”¨æˆ·IDï¼ˆå…³è”usersè¡¨ï¼‰',
  `order_id` bigint NULL DEFAULT NULL COMMENT 'å…³è”è®¢å•IDï¼ˆå…³è”ordersè¡¨ï¼‰',
  `food_id` bigint NULL DEFAULT NULL COMMENT 'å…‘æ¢å•†å“IDï¼ˆå…³è”foodè¡¨ï¼‰',
  `points_used` bigint NOT NULL COMMENT 'ä½¿ç”¨ç§¯åˆ†æ•°é‡',
  `cash_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT 'çŽ°é‡‘é‡‘é¢ï¼ˆé¢„ç•™å­—æ®µï¼Œå½“å‰çº¯ç§¯åˆ†å…‘æ¢ä¸º0ï¼‰',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT 'çŠ¶æ€ 0-å¾…å¤„ç† 1-å·²å®Œæˆ 2-å·²å–æ¶ˆ',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'åˆ›å»ºæ—¶é—´',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'æ›´æ–°æ—¶é—´',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'æ˜¯å¦åˆ é™¤',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE COMMENT 'ç”¨æˆ·IDç´¢å¼•',
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE COMMENT 'è®¢å•IDç´¢å¼•',
  INDEX `idx_status`(`status` ASC) USING BTREE COMMENT 'çŠ¶æ€ç´¢å¼•'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'ç§¯åˆ†å…‘æ¢è®¢å•è¡¨ï¼ˆä»…ç”¨äºŽçº¯ç§¯åˆ†å…‘æ¢å•†å“ï¼Œç§¯åˆ†+çŽ°é‡‘æ”¯ä»˜ä¸ä½¿ç”¨æ­¤è¡¨ï¼‰' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of points_exchange_order
-- ----------------------------

-- ----------------------------
-- Table structure for points_expiration
-- ----------------------------
DROP TABLE IF EXISTS `points_expiration`;
CREATE TABLE `points_expiration`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ä¸»é”®ID',
  `user_id` bigint NOT NULL COMMENT 'ç”¨æˆ·IDï¼ˆå…³è”usersè¡¨ï¼‰',
  `transaction_id` bigint NOT NULL COMMENT 'ç§¯åˆ†æ˜Žç»†IDï¼ˆå…³è”points_transactionè¡¨ï¼‰',
  `points_amount` bigint NOT NULL COMMENT 'å³å°†è¿‡æœŸç§¯åˆ†æ•°é‡',
  `expire_time` timestamp NOT NULL COMMENT 'è¿‡æœŸæ—¶é—´ï¼ˆç²¾ç¡®åˆ°ç§’ï¼‰',
  `expire_date` date NOT NULL COMMENT 'è¿‡æœŸæ—¥æœŸï¼ˆç”¨äºŽæŒ‰æ—¥æœŸæŸ¥è¯¢ï¼‰',
  `is_expired` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'æ˜¯å¦å·²è¿‡æœŸ 0-æœªè¿‡æœŸ 1-å·²è¿‡æœŸ',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'åˆ›å»ºæ—¶é—´',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE COMMENT 'ç”¨æˆ·IDç´¢å¼•',
  INDEX `idx_expire_time`(`expire_time` ASC) USING BTREE COMMENT 'è¿‡æœŸæ—¶é—´ç´¢å¼•',
  INDEX `idx_expire_date`(`expire_date` ASC) USING BTREE COMMENT 'è¿‡æœŸæ—¥æœŸç´¢å¼•',
  INDEX `idx_is_expired`(`is_expired` ASC) USING BTREE COMMENT 'è¿‡æœŸçŠ¶æ€ç´¢å¼•',
  INDEX `idx_user_expire`(`user_id` ASC, `expire_time` ASC) USING BTREE COMMENT 'ç”¨æˆ·è¿‡æœŸæ—¶é—´è”åˆç´¢å¼•'
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'ç§¯åˆ†è¿‡æœŸè®°å½•è¡¨' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of points_expiration
-- ----------------------------
INSERT INTO `points_expiration` VALUES (1, 7, 1, 50, '2026-05-03 20:17:31', '2026-05-03', 0, '2026-04-03 20:35:17');

-- ----------------------------
-- Table structure for points_expiration_alert_config
-- ----------------------------
DROP TABLE IF EXISTS `points_expiration_alert_config`;
CREATE TABLE `points_expiration_alert_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ä¸»é”®ID',
  `alert_days` int NOT NULL COMMENT 'æå‰é¢„è­¦å¤©æ•°ï¼ˆå¦‚7è¡¨ç¤ºæå‰7å¤©é¢„è­¦ï¼‰',
  `alert_cycle` int NULL DEFAULT NULL COMMENT 'é¢„è­¦å‘¨æœŸï¼ˆå¤©æ•°ï¼Œå¦‚æ¯å¤©ã€æ¯3å¤©ï¼‰ã€‚NULLè¡¨ç¤ºåªé¢„è­¦ä¸€æ¬¡',
  `sms_template` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'çŸ­ä¿¡æ¨¡æ¿ï¼ˆæ”¯æŒå˜é‡ï¼š{username}, {points}, {expireDate}ï¼‰',
  `is_enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT 'æ˜¯å¦å¯ç”¨ 0-ç¦ç”¨ 1-å¯ç”¨',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'åˆ›å»ºæ—¶é—´',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'æ›´æ–°æ—¶é—´',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'ç§¯åˆ†åˆ°æœŸé¢„è­¦é…ç½®è¡¨' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of points_expiration_alert_config
-- ----------------------------
INSERT INTO `points_expiration_alert_config` VALUES (1, 3, NULL, '尊敬的{username}先生/女士，您有{points}积分将于{expireDate}过期，请及时使用。', 1, '2026-04-02 00:34:12', '2026-04-02 00:34:12');

-- ----------------------------
-- Table structure for points_expiration_alert_log
-- ----------------------------
DROP TABLE IF EXISTS `points_expiration_alert_log`;
CREATE TABLE `points_expiration_alert_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ä¸»é”®ID',
  `user_id` bigint NOT NULL COMMENT 'ç”¨æˆ·IDï¼ˆå…³è”usersè¡¨ï¼‰',
  `points_amount` bigint NOT NULL COMMENT 'å³å°†è¿‡æœŸç§¯åˆ†æ•°é‡',
  `expire_date` date NOT NULL COMMENT 'è¿‡æœŸæ—¥æœŸ',
  `alert_time` timestamp NOT NULL COMMENT 'é¢„è­¦æ—¶é—´',
  `next_alert_time` timestamp NULL DEFAULT NULL COMMENT 'ä¸‹æ¬¡é¢„è­¦æ—¶é—´ï¼ˆæ ¹æ®é¢„è­¦å‘¨æœŸè®¡ç®—ï¼‰',
  `is_sent` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'æ˜¯å¦å·²å‘é€ 0-æœªå‘é€ 1-å·²å‘é€',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ç”¨æˆ·æ‰‹æœºå·ï¼ˆå‘é€çŸ­ä¿¡æ—¶ä½¿ç”¨ï¼‰',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'åˆ›å»ºæ—¶é—´',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE COMMENT 'ç”¨æˆ·IDç´¢å¼•',
  INDEX `idx_expire_date`(`expire_date` ASC) USING BTREE COMMENT 'è¿‡æœŸæ—¥æœŸç´¢å¼•',
  INDEX `idx_next_alert_time`(`next_alert_time` ASC) USING BTREE COMMENT 'ä¸‹æ¬¡é¢„è­¦æ—¶é—´ç´¢å¼•',
  INDEX `idx_user_expire`(`user_id` ASC, `expire_date` ASC) USING BTREE COMMENT 'ç”¨æˆ·è¿‡æœŸæ—¥æœŸè”åˆç´¢å¼•'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'ç§¯åˆ†åˆ°æœŸé¢„è­¦è®°å½•è¡¨' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of points_expiration_alert_log
-- ----------------------------

-- ----------------------------
-- Table structure for points_lottery_record
-- ----------------------------
DROP TABLE IF EXISTS `points_lottery_record`;
CREATE TABLE `points_lottery_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ä¸»é”®ID',
  `user_id` bigint NOT NULL COMMENT 'ç”¨æˆ·IDï¼ˆå…³è”usersè¡¨ï¼‰',
  `member_level` tinyint NOT NULL COMMENT 'æŠ½å¥–æ—¶çš„ä¼šå‘˜ç­‰çº§ 0-æ™®é€š 1-ç™½é“¶ 2-é»„é‡‘ 3-é’»çŸ³',
  `lottery_type` tinyint NOT NULL COMMENT 'æŠ½å¥–ç±»åž‹ 0-æ²¡ä¸­å¥– 1-å›ºå®šç§¯åˆ† 2-ç§¯åˆ†ç¿»å€',
  `points_reward` bigint NULL DEFAULT NULL COMMENT 'èŽ·å¾—çš„ç§¯åˆ†æ•°é‡ï¼ˆæ²¡ä¸­å¥–æ—¶ä¸º0ï¼Œç§¯åˆ†ç¿»å€æ—¶ä¸ºNULLï¼‰',
  `points_multiplier` decimal(10, 2) NULL DEFAULT NULL COMMENT 'ç§¯åˆ†ç¿»å€å€æ•°ï¼ˆä»…ç§¯åˆ†ç¿»å€æ—¶ä½¿ç”¨ï¼‰',
  `original_points` bigint NULL DEFAULT NULL COMMENT 'ç¿»å€å‰çš„ç§¯åˆ†æ•°é‡ï¼ˆä»…ç§¯åˆ†ç¿»å€æ—¶ä½¿ç”¨ï¼‰',
  `lottery_month` varchar(7) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'æŠ½å¥–æœˆä»½ï¼ˆæ ¼å¼ï¼šYYYY-MMï¼Œç”¨äºŽç»Ÿè®¡æ¯æœˆæŠ½å¥–æ¬¡æ•°ï¼‰',
  `transaction_id` bigint NULL DEFAULT NULL COMMENT 'å…³è”ç§¯åˆ†æ˜Žç»†IDï¼ˆå…³è”points_transactionè¡¨ï¼Œä¸­å¥–æ—¶è®°å½•ï¼‰',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'åˆ›å»ºæ—¶é—´',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'æ›´æ–°æ—¶é—´',
  `creator` bigint NULL DEFAULT NULL COMMENT 'åˆ›å»ºäººID',
  `updater` bigint NULL DEFAULT NULL COMMENT 'æ›´æ–°äººID',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'æ˜¯å¦åˆ é™¤',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE COMMENT 'ç”¨æˆ·IDç´¢å¼•',
  INDEX `idx_lottery_month`(`lottery_month` ASC) USING BTREE COMMENT 'æŠ½å¥–æœˆä»½ç´¢å¼•',
  INDEX `idx_user_month`(`user_id` ASC, `lottery_month` ASC) USING BTREE COMMENT 'ç”¨æˆ·æœˆä»½è”åˆç´¢å¼•ï¼ˆç”¨äºŽç»Ÿè®¡æ¯æœˆæŠ½å¥–æ¬¡æ•°ï¼‰',
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE COMMENT 'åˆ›å»ºæ—¶é—´ç´¢å¼•'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'ç§¯åˆ†æŠ½å¥–è®°å½•è¡¨' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of points_lottery_record
-- ----------------------------

-- ----------------------------
-- Table structure for points_lottery_rule
-- ----------------------------
DROP TABLE IF EXISTS `points_lottery_rule`;
CREATE TABLE `points_lottery_rule`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ä¸»é”®ID',
  `rule_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'è§„åˆ™åç§°',
  `member_level` tinyint NOT NULL COMMENT 'é€‚ç”¨ä¼šå‘˜ç­‰çº§ 1-ç™½é“¶ 2-é»„é‡‘ 3-é’»çŸ³',
  `prize_type` tinyint NOT NULL COMMENT 'å¥–å“ç±»åž‹ 0-æ²¡ä¸­å¥– 1-å›ºå®šç§¯åˆ† 2-ç§¯åˆ†ç¿»å€',
  `prize_points` bigint NULL DEFAULT NULL COMMENT 'å›ºå®šç§¯åˆ†æ•°é‡ï¼ˆä»…prize_type=1æ—¶æœ‰æ•ˆï¼‰',
  `prize_multiplier` decimal(10, 2) NULL DEFAULT NULL COMMENT 'ç§¯åˆ†ç¿»å€å€æ•°ï¼ˆä»…prize_type=2æ—¶æœ‰æ•ˆï¼Œå¦‚2.0è¡¨ç¤ºç¿»å€ï¼‰',
  `probability` int NOT NULL COMMENT 'ä¸­å¥–æ¦‚çŽ‡ï¼ˆç™¾åˆ†æ¯”ï¼Œ0-100ï¼‰',
  `prize_order` int NOT NULL DEFAULT 0 COMMENT 'å¥–å“æŽ’åºï¼ˆæ•°å­—è¶Šå°è¶Šé å‰ï¼Œç”¨äºŽå‰ç«¯å±•ç¤ºï¼‰',
  `prize_description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'å¥–å“æè¿°ï¼ˆç”¨äºŽå‰ç«¯å±•ç¤ºï¼‰',
  `rule_status` tinyint NOT NULL DEFAULT 1 COMMENT 'è§„åˆ™çŠ¶æ€ 0-ç¦ç”¨ 1-å¯ç”¨',
  `start_time` timestamp NULL DEFAULT NULL COMMENT 'è§„åˆ™ç”Ÿæ•ˆå¼€å§‹æ—¶é—´',
  `end_time` timestamp NULL DEFAULT NULL COMMENT 'è§„åˆ™ç”Ÿæ•ˆç»“æŸæ—¶é—´',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'åˆ›å»ºæ—¶é—´',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'æ›´æ–°æ—¶é—´',
  `creator` bigint NULL DEFAULT NULL COMMENT 'åˆ›å»ºäººID',
  `updater` bigint NULL DEFAULT NULL COMMENT 'æ›´æ–°äººID',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'æ˜¯å¦åˆ é™¤',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_member_level`(`member_level` ASC) USING BTREE COMMENT 'ä¼šå‘˜ç­‰çº§ç´¢å¼•',
  INDEX `idx_prize_type`(`prize_type` ASC) USING BTREE COMMENT 'å¥–å“ç±»åž‹ç´¢å¼•',
  INDEX `idx_rule_status`(`rule_status` ASC) USING BTREE COMMENT 'è§„åˆ™çŠ¶æ€ç´¢å¼•',
  INDEX `idx_member_status`(`member_level` ASC, `rule_status` ASC) USING BTREE COMMENT 'ä¼šå‘˜ç­‰çº§å’ŒçŠ¶æ€è”åˆç´¢å¼•'
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'ç§¯åˆ†æŠ½å¥–è§„åˆ™è¡¨' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of points_lottery_rule
-- ----------------------------
INSERT INTO `points_lottery_rule` VALUES (1, '白银会员-没中奖', 1, 0, 0, NULL, 50, 1, '感谢参与', 1, NULL, NULL, '2026-04-02 00:34:12', '2026-04-02 00:34:12', 1, 1, 0);
INSERT INTO `points_lottery_rule` VALUES (2, '白银会员-20积分', 1, 1, 20, NULL, 30, 2, '+ 20 积分', 1, NULL, NULL, '2026-04-02 00:34:12', '2026-04-02 00:34:12', 1, 1, 0);
INSERT INTO `points_lottery_rule` VALUES (3, '白银会员-50积分', 1, 1, 50, NULL, 15, 3, '+ 50 积分', 1, NULL, NULL, '2026-04-02 00:34:12', '2026-04-02 00:34:12', 1, 1, 0);
INSERT INTO `points_lottery_rule` VALUES (4, '白银会员-100积分', 1, 1, 100, NULL, 5, 4, '+ 100 积分', 1, NULL, NULL, '2026-04-02 00:34:12', '2026-04-02 00:34:12', 1, 1, 0);
INSERT INTO `points_lottery_rule` VALUES (5, '黄金会员-没中奖', 2, 0, 0, NULL, 40, 1, '感谢参与', 1, NULL, NULL, '2026-04-02 00:34:12', '2026-04-02 00:34:12', 1, 1, 0);
INSERT INTO `points_lottery_rule` VALUES (6, '黄金会员-50积分', 2, 1, 50, NULL, 30, 2, '+ 50 积分', 1, NULL, NULL, '2026-04-02 00:34:12', '2026-04-02 00:34:12', 1, 1, 0);
INSERT INTO `points_lottery_rule` VALUES (7, '黄金会员-100积分', 2, 1, 100, NULL, 20, 3, '+ 100 积分', 1, NULL, NULL, '2026-04-02 00:34:12', '2026-04-02 00:34:12', 1, 1, 0);
INSERT INTO `points_lottery_rule` VALUES (8, '黄金会员-积分翻倍', 2, 2, NULL, 2.00, 10, 4, '积分翻倍', 1, NULL, NULL, '2026-04-02 00:34:12', '2026-04-02 00:34:12', 1, 1, 0);
INSERT INTO `points_lottery_rule` VALUES (9, '钻石会员-没中奖', 3, 0, 0, NULL, 30, 1, '感谢参与', 1, NULL, NULL, '2026-04-02 00:34:12', '2026-04-02 00:34:12', 1, 1, 0);
INSERT INTO `points_lottery_rule` VALUES (10, '钻石会员-100积分', 3, 1, 100, NULL, 30, 2, '+ 100 积分', 1, NULL, NULL, '2026-04-02 00:34:12', '2026-04-02 00:34:12', 1, 1, 0);
INSERT INTO `points_lottery_rule` VALUES (11, '钻石会员-200积分', 3, 1, 200, NULL, 25, 3, '+ 200 积分', 1, NULL, NULL, '2026-04-02 00:34:12', '2026-04-02 00:34:12', 1, 1, 0);
INSERT INTO `points_lottery_rule` VALUES (12, '钻石会员-积分翻倍', 3, 2, NULL, 2.00, 15, 4, '积分翻倍', 1, NULL, NULL, '2026-04-02 00:34:12', '2026-04-02 00:34:12', 1, 1, 0);

-- ----------------------------
-- Table structure for points_transaction
-- ----------------------------
DROP TABLE IF EXISTS `points_transaction`;
CREATE TABLE `points_transaction`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ä¸»é”®ID',
  `user_id` bigint NOT NULL COMMENT 'ç”¨æˆ·IDï¼ˆå…³è”usersè¡¨ï¼‰',
  `account_id` bigint NOT NULL COMMENT 'ç§¯åˆ†è´¦æˆ·IDï¼ˆå…³è”points_accountè¡¨ï¼‰',
  `transaction_type` tinyint NOT NULL COMMENT 'äº¤æ˜“ç±»åž‹ 0-èŽ·å¾— 1-æ¶ˆè´¹ 2-è¿‡æœŸ 3-å†»ç»“ 4-è§£å†»',
  `points_source` tinyint NOT NULL COMMENT 'ç§¯åˆ†æ¥æº 0-æ¶ˆè´¹ç§¯åˆ† 1-ä¿ƒé”€ç§¯åˆ† 2-ç­‰çº§ç§¯åˆ† 3-è¡Œä¸ºç§¯åˆ† 4-å…‘æ¢å•†å“ 5-ç§¯åˆ†+çŽ°é‡‘æ¶ˆè´¹',
  `points_change` bigint NOT NULL COMMENT 'ç§¯åˆ†å˜åŠ¨æ•°é‡ï¼ˆæ­£æ•°è¡¨ç¤ºå¢žåŠ ï¼Œè´Ÿæ•°è¡¨ç¤ºå‡å°‘ï¼‰',
  `points_balance` bigint NOT NULL COMMENT 'å˜åŠ¨åŽç§¯åˆ†ä½™é¢',
  `expire_time` timestamp NULL DEFAULT NULL COMMENT 'ç§¯åˆ†è¿‡æœŸæ—¶é—´',
  `related_order_id` bigint NULL DEFAULT NULL COMMENT 'å…³è”è®¢å•IDï¼ˆå…³è”ordersè¡¨ï¼‰',
  `related_food_id` bigint NULL DEFAULT NULL COMMENT 'å…³è”å•†å“IDï¼ˆå…³è”foodè¡¨ï¼Œå…‘æ¢å•†å“æ—¶ä½¿ç”¨ï¼‰',
  `related_rule_id` bigint NULL DEFAULT NULL COMMENT 'å…³è”ç§¯åˆ†è§„åˆ™IDï¼ˆå…³è”marketing_points_ruleè¡¨ï¼‰',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'äº¤æ˜“æè¿°',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'åˆ›å»ºæ—¶é—´',
  `creator` bigint NULL DEFAULT NULL COMMENT 'åˆ›å»ºäººID',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'æ›´æ–°æ—¶é—´',
  `updater` bigint NULL DEFAULT NULL COMMENT 'æ›´æ–°äººID',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'æ˜¯å¦åˆ é™¤',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE COMMENT 'ç”¨æˆ·IDç´¢å¼•',
  INDEX `idx_account_id`(`account_id` ASC) USING BTREE COMMENT 'è´¦æˆ·IDç´¢å¼•',
  INDEX `idx_transaction_type`(`transaction_type` ASC) USING BTREE COMMENT 'äº¤æ˜“ç±»åž‹ç´¢å¼•',
  INDEX `idx_expire_time`(`expire_time` ASC) USING BTREE COMMENT 'è¿‡æœŸæ—¶é—´ç´¢å¼•',
  INDEX `idx_related_order_id`(`related_order_id` ASC) USING BTREE COMMENT 'è®¢å•IDç´¢å¼•',
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE COMMENT 'åˆ›å»ºæ—¶é—´ç´¢å¼•'
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'ç§¯åˆ†æ˜Žç»†è¡¨' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of points_transaction
-- ----------------------------
INSERT INTO `points_transaction` VALUES (1, 7, 1, 0, 0, 50, 28, '2026-05-03 20:17:31', 1, NULL, NULL, '订单消费获得积分（冻结中，订单完成后解冻）', '2026-04-03 20:35:17', 7, '2026-04-03 20:35:17', 7, 0);

SET FOREIGN_KEY_CHECKS = 1;
