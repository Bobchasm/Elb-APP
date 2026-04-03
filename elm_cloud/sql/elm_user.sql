/*
 Navicat Premium Dump SQL

 Source Server         : TXCloud1
 Source Server Type    : MySQL
 Source Server Version : 80044 (8.0.44)
 Source Host           : REDACTED_IP:3306
 Source Schema         : elm_user

 Target Server Type    : MySQL
 Target Server Version : 80044 (8.0.44)
 File Encoding         : 65001

 Date: 03/04/2026 22:39:10
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for authority
-- ----------------------------
DROP TABLE IF EXISTS `authority`;
CREATE TABLE `authority`  (
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of authority
-- ----------------------------
INSERT INTO `authority` VALUES ('ADMIN');
INSERT INTO `authority` VALUES ('BUSINESS');
INSERT INTO `authority` VALUES ('USER');

-- ----------------------------
-- Table structure for delivery_address
-- ----------------------------
DROP TABLE IF EXISTS `delivery_address`;
CREATE TABLE `delivery_address`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` timestamp NULL DEFAULT NULL,
  `creator` bigint NULL DEFAULT NULL,
  `is_deleted` tinyint(1) NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL,
  `updater` bigint NULL DEFAULT NULL,
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `contact_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `contact_sex` int NULL DEFAULT NULL,
  `contact_tel` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of delivery_address
-- ----------------------------
INSERT INTO `delivery_address` VALUES (4, '2025-09-25 21:34:42', 5, 0, '2025-09-25 23:00:36', 5, '湖南岳阳汴河街', '小高', 0, '13873021510', 5);
INSERT INTO `delivery_address` VALUES (6, '2025-09-25 21:49:55', 6, 0, '2025-09-25 21:49:55', 6, '湖南岳阳蓝剑大厦', '小刘', 1, '13873021510', 6);
INSERT INTO `delivery_address` VALUES (7, '2025-09-26 10:39:27', 5, 0, '2025-09-26 10:39:27', 5, '湖南岳阳楼', '小李', 1, '16607402898', 5);
INSERT INTO `delivery_address` VALUES (8, '2026-04-03 18:27:06', 7, 0, '2026-04-03 18:27:06', 7, '201', '将哈哈', 1, '14374885748', 7);

-- ----------------------------
-- Table structure for permission_application
-- ----------------------------
DROP TABLE IF EXISTS `permission_application`;
CREATE TABLE `permission_application`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `userId` bigint NOT NULL,
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '审核状态 0-未审核 1-同意 2-拒绝',
  `is_deleted` tinyint NULL DEFAULT 0,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of permission_application
-- ----------------------------

-- ----------------------------
-- Table structure for person
-- ----------------------------
DROP TABLE IF EXISTS `person`;
CREATE TABLE `person`  (
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `first_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `gender` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `last_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `photo` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of person
-- ----------------------------
INSERT INTO `person` VALUES ('3347127717@qq.com', '涛', '0', '灿', '19173095993', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/3d038310-4d85-4388-a026-65b6d437b22b.jpg', 2);
INSERT INTO `person` VALUES ('3347127717@qq.com', '李', '0', '灿', '16607302890', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/295a7008-aebd-4389-8a71-a9a278034d3e.jpg', 4);
INSERT INTO `person` VALUES ('3347127717@qq.com', '这', '0', '灿', '19173095993', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/54c4ddcf-ac85-454c-9792-a8e794df3ec4.jpg', 5);
INSERT INTO `person` VALUES ('736213069@qq.com', '宋', '1', '灿', '16607302890', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/2df21e82-c447-4047-810c-1b680ee309b2.jpg', 6);
INSERT INTO `person` VALUES ('347593855432@tju.edu.cn', '哈哈', '1', '江', '16736787654', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/42b8f591-87d3-4e14-a354-d555738d0ddb.jpg', 7);
INSERT INTO `person` VALUES ('2341235@qq.com', '丽丽', '0', '林', '13021377574', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/1e276cbc-da97-4092-8e0b-7123abc1fe9e.jpg', 8);
INSERT INTO `person` VALUES ('adnib214@tju.edu.cn', 'vx6_wsl', '0', '123', '13424280592', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/10c8f320-6838-4d8b-b6f6-67e6663f77af.jpg', 9);
INSERT INTO `person` VALUES ('adnib214@tju.edu.cn', 'vx6_wsl', '0', '123', '13424280592', 'https://txcloud-cos1-1375850795.cos.accelerate.myqcloud.com/fd7a3922-be77-4785-a428-0b3521e251aa.jpg', 10);
INSERT INTO `person` VALUES ('36473944@tju.edu.cn', '丽丽', '0', '林', '13021377574', 'https://txcloud-cos1-1375850795.cos.accelerate.myqcloud.com/7bf3d1c0-349d-41eb-a242-b34b749def90.JPG', 11);

-- ----------------------------
-- Table structure for system_config
-- ----------------------------
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `config_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `config_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `value_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `update_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of system_config
-- ----------------------------
INSERT INTO `system_config` VALUES (1, 'VIRTUAL_WALLET_RULES', '虚拟钱包规则', 'STRING', NULL, '2026-04-03 21:00:19');

-- ----------------------------
-- Table structure for user_authority
-- ----------------------------
DROP TABLE IF EXISTS `user_authority`;
CREATE TABLE `user_authority`  (
  `user_id` bigint NOT NULL,
  `authority_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`user_id`, `authority_name`) USING BTREE,
  INDEX `authority_name`(`authority_name` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_authority
-- ----------------------------
INSERT INTO `user_authority` VALUES (3, 'ADMIN');
INSERT INTO `user_authority` VALUES (9, 'ADMIN');
INSERT INTO `user_authority` VALUES (2, 'BUSINESS');
INSERT INTO `user_authority` VALUES (4, 'BUSINESS');
INSERT INTO `user_authority` VALUES (8, 'BUSINESS');
INSERT INTO `user_authority` VALUES (2, 'USER');
INSERT INTO `user_authority` VALUES (4, 'USER');
INSERT INTO `user_authority` VALUES (5, 'USER');
INSERT INTO `user_authority` VALUES (6, 'USER');
INSERT INTO `user_authority` VALUES (7, 'USER');
INSERT INTO `user_authority` VALUES (8, 'USER');
INSERT INTO `user_authority` VALUES (9, 'USER');
INSERT INTO `user_authority` VALUES (10, 'USER');
INSERT INTO `user_authority` VALUES (11, 'USER');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` timestamp NULL DEFAULT NULL,
  `creator` bigint NULL DEFAULT NULL,
  `is_deleted` tinyint(1) NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL,
  `updater` bigint NULL DEFAULT NULL,
  `activated` tinyint(1) NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (2, '2025-09-25 16:17:50', NULL, 0, '2025-09-25 19:06:53', 2, 1, '$2a$10$HL7jh92Duv3mAoklfgS3iOglu1CGblLKqaC.SQL43qtNNKivGfy96', 'sunny');
INSERT INTO `users` VALUES (3, '2025-09-25 16:18:35', NULL, 0, '2025-09-25 16:18:35', NULL, 1, '$2a$10$.0tYR0Ek3Ns36Ggc1PW4YuNNRBTmZQQJ2aw7fBWy7xJVNMoGfyiv.', 'sun');
INSERT INTO `users` VALUES (4, '2025-09-25 16:19:23', NULL, 0, '2025-09-25 16:19:23', NULL, 1, '$2a$10$yr7GoYV3rRGPPwrvqyNaCuBy9VDfoU3qaOYkyYHG.9oqoeFZ70BEC', 'sunshine');
INSERT INTO `users` VALUES (5, '2025-09-25 16:20:16', NULL, 0, '2025-09-25 21:34:23', 5, 1, '$2a$10$7/d4r0bO7OSE0JRY1iKOtuiO1ca/GyFXPq2HnFaXKKrJhN6h4MHZ6', 'rain');
INSERT INTO `users` VALUES (6, '2025-09-25 16:22:47', NULL, 0, '2025-09-25 16:22:47', NULL, 1, '$2a$10$TdoFHty.VsIqCJOxn5hohOrwXcMhtZ0rc8w24LYSqH43q9tBjqUa.', 'rainy');
INSERT INTO `users` VALUES (7, '2026-04-02 08:45:35', NULL, 0, '2026-04-02 08:45:35', NULL, 1, '$2a$10$bgGQf3a/KvTL7jNJyNLPJ./s0DBnO2o6y0b53kc4TNrMTSy8Vvjfe', 'common_user');
INSERT INTO `users` VALUES (8, '2026-04-02 08:51:26', NULL, 0, '2026-04-02 08:51:26', NULL, 1, '$2a$10$3hF/TNnfbaELZI7CXm4eZOh5P6Rm3UBWEDkZOGL8TwUEQieHvZAki', 'business_user');
INSERT INTO `users` VALUES (9, '2026-04-02 08:52:08', NULL, 0, '2026-04-02 08:52:08', NULL, 1, '$2a$10$o3zmtqweVEUKBErKelFFyu1X4PUMTy5hvrescHxsUqsDikEAbYD9O', 'admin_user');
INSERT INTO `users` VALUES (10, '2026-04-03 20:23:16', NULL, 0, '2026-04-03 20:23:16', NULL, 1, '$2a$10$7RH1OkTBOSKIPJ4x639/JOF.9LloGPWgQgvT/M5i6LckQog4dvTjS', '嘻嘻哈哈');
INSERT INTO `users` VALUES (11, '2026-04-03 20:26:18', NULL, 0, '2026-04-03 20:26:18', NULL, 1, '$2a$10$9NuDHSW/L/mN1sIVc0WXiuc5AWV6r8dFRBPrDhXiPj2f7S6J6Nd2u', 'test');

SET FOREIGN_KEY_CHECKS = 1;
