/*
 Navicat Premium Dump SQL

 Source Server         : TXCloud1
 Source Server Type    : MySQL
 Source Server Version : 80044 (8.0.44)
 Source Host           : REDACTED_IP:3306
 Source Schema         : elm_virtual_wallet

 Target Server Type    : MySQL
 Target Server Version : 80044 (8.0.44)
 File Encoding         : 65001

 Date: 03/04/2026 22:39:17
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for virtual_wallet
-- ----------------------------
DROP TABLE IF EXISTS `virtual_wallet`;
CREATE TABLE `virtual_wallet`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'è™šæ‹Ÿé’±åŒ…id',
  `user_id` bigint NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `status` tinyint NULL DEFAULT 0 COMMENT 'é’±åŒ…çŠ¶æ€ 0-æ­£å¸¸ 1-å†»ç»“',
  `vip_level` tinyint NULL DEFAULT 0 COMMENT 'vipçº§åˆ« 0-éžvip',
  `is_deleted` tinyint NULL DEFAULT 0,
  `balance` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT 'ä½™é¢',
  `overdraft_amount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT 'å¯é€æ”¯é‡‘é¢',
  `overdrawn_amount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT 'å·²é€æ”¯é‡‘é¢',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of virtual_wallet
-- ----------------------------
INSERT INTO `virtual_wallet` VALUES (1, 7, '2026-04-03 10:27:16', '2026-04-03 10:27:23', 0, 0, 0, 123.22, 0.00, 0.00);

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
-- Records of virtual_wallet_loan
-- ----------------------------

-- ----------------------------
-- Table structure for virtual_wallet_transaction
-- ----------------------------
DROP TABLE IF EXISTS `virtual_wallet_transaction`;
CREATE TABLE `virtual_wallet_transaction`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `type` tinyint NULL DEFAULT NULL COMMENT 'äº¤æ˜“ç±»åž‹ 0-æ”¯ä»˜ 1-æ”¶æ¬¾ 2-æçŽ° 3-å……å€¼ 4-é€€æ¬¾',
  `status` tinyint NULL DEFAULT 0 COMMENT 'æ“ä½œé‡‘é¢æ˜¯å¦ä¸ºå†»ç»“ 0-å¦ 1-æ˜¯ 2-å–æ¶ˆæ ‡è®°',
  `amount` decimal(10, 2) NOT NULL COMMENT 'æ“ä½œé‡‘é¢',
  `from_account` bigint NOT NULL COMMENT 'è½¬å‡ºé’±åŒ… äº¤æ˜“ç±»åž‹ä¸ºå……å€¼æ—¶å€¼ä¸º0',
  `to_account` bigint NOT NULL COMMENT 'è½¬å…¥é’±åŒ… äº¤æ˜“ç±»åž‹ä¸ºæçŽ°æ—¶å€¼ä¸º0',
  `is_deleted` tinyint NULL DEFAULT 0,
  `fee` decimal(10, 2) NULL DEFAULT NULL COMMENT 'æ‰‹ç»­è´¹æˆ–å¥–åŠ±',
  `fee_rate` decimal(10, 2) NULL DEFAULT NULL COMMENT 'æ‰‹ç»­è´¹çŽ‡æˆ–å¥–åŠ±çŽ‡',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'äº¤æ˜“æ—¶é—´',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `order_id` bigint NULL DEFAULT NULL COMMENT 'è‹¥æ˜¯è®¢å•æ”¯ä»˜çš„æµæ°´ï¼Œå…³è”è®¢å•idï¼Œç”¨äºŽå†»ç»“æ“ä½œ',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of virtual_wallet_transaction
-- ----------------------------
INSERT INTO `virtual_wallet_transaction` VALUES (1, 3, 0, 123.22, 0, 1, 0, 1.22, 0.01, '2026-04-03 18:27:24', '2026-04-03 10:27:23', NULL);

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
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of virtual_wallet_vip_rule
-- ----------------------------
INSERT INTO `virtual_wallet_vip_rule` VALUES (1, '白银会员', '轻松解锁基础额度，应急周转不再难', 0, 1000.00, 20.00);
INSERT INTO `virtual_wallet_vip_rule` VALUES (2, '黄金会员', '额度翻倍，权限升级，满足您更多消费规划', 0, 10000.00, 100.00);
INSERT INTO `virtual_wallet_vip_rule` VALUES (3, '钻石会员', '尊享最高借贷额度，让您资金调度游刃有余', 0, 1000000.00, 1000.00);

SET FOREIGN_KEY_CHECKS = 1;
