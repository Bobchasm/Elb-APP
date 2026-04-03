CREATE DATABASE elm_user;

USE elm_user;


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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;


-- ----------------------------
-- Records of delivery_address
-- ----------------------------
INSERT INTO `delivery_address` VALUES (4, '2025-09-25 21:34:42', 5, 0, '2025-09-25 23:00:36', 5, '湖南岳阳汴河街', '小高', 0, '13873021510', 5);
INSERT INTO `delivery_address` VALUES (6, '2025-09-25 21:49:55', 6, 0, '2025-09-25 21:49:55', 6, '湖南岳阳蓝剑大厦', '小刘', 1, '13873021510', 6);
INSERT INTO `delivery_address` VALUES (7, '2025-09-26 10:39:27', 5, 0, '2025-09-26 10:39:27', 5, '湖南岳阳楼', '小李', 1, '16607402898', 5);


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
INSERT INTO `user_authority` VALUES (2, 'BUSINESS');
INSERT INTO `user_authority` VALUES (4, 'BUSINESS');
INSERT INTO `user_authority` VALUES (2, 'USER');
INSERT INTO `user_authority` VALUES (4, 'USER');
INSERT INTO `user_authority` VALUES (5, 'USER');
INSERT INTO `user_authority` VALUES (6, 'USER');

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (2, '2025-09-25 16:17:50', NULL, 0, '2025-09-25 19:06:53', 2, 1, '$2a$10$HL7jh92Duv3mAoklfgS3iOglu1CGblLKqaC.SQL43qtNNKivGfy96', 'sunny');
INSERT INTO `users` VALUES (3, '2025-09-25 16:18:35', NULL, 0, '2025-09-25 16:18:35', NULL, 1, '$2a$10$.0tYR0Ek3Ns36Ggc1PW4YuNNRBTmZQQJ2aw7fBWy7xJVNMoGfyiv.', 'sun');
INSERT INTO `users` VALUES (4, '2025-09-25 16:19:23', NULL, 0, '2025-09-25 16:19:23', NULL, 1, '$2a$10$yr7GoYV3rRGPPwrvqyNaCuBy9VDfoU3qaOYkyYHG.9oqoeFZ70BEC', 'sunshine');
INSERT INTO `users` VALUES (5, '2025-09-25 16:20:16', NULL, 0, '2025-09-25 21:34:23', 5, 1, '$2a$10$7/d4r0bO7OSE0JRY1iKOtuiO1ca/GyFXPq2HnFaXKKrJhN6h4MHZ6', 'rain');
INSERT INTO `users` VALUES (6, '2025-09-25 16:22:47', NULL, 0, '2025-09-25 16:22:47', NULL, 1, '$2a$10$TdoFHty.VsIqCJOxn5hohOrwXcMhtZ0rc8w24LYSqH43q9tBjqUa.', 'rainy');






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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

INSERT INTO `system_config` VALUES (1, 'VIRTUAL_WALLET_RULES', '虚拟钱包规则', 'STRING', NULL, '2026-04-03 21:00:19');
