/*
 Navicat Premium Dump SQL

 Source Server         : TXCloud1
 Source Server Type    : MySQL
 Source Server Version : 80044 (8.0.44)
 Source Host           : REDACTED_IP:3306
 Source Schema         : elm_v2

 Target Server Type    : MySQL
 Target Server Version : 80044 (8.0.44)
 File Encoding         : 65001

 Date: 13/01/2026 18:35:13
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ai_chat_history
-- ----------------------------
DROP TABLE IF EXISTS `ai_chat_history`;
CREATE TABLE `ai_chat_history`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `creator` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除(0:否,1:是)',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `updater` bigint NULL DEFAULT NULL COMMENT '更新者ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `session_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '会话ID',
  `user_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户消息',
  `ai_response` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'AI回复',
  `chat_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'general' COMMENT '对话类型',
  `processing_time` bigint NULL DEFAULT NULL COMMENT '处理耗时(毫秒)',
  `context_data` json NULL COMMENT '上下文数据',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE COMMENT '用户ID索引',
  INDEX `idx_session_id`(`session_id` ASC) USING BTREE COMMENT '会话ID索引',
  INDEX `idx_chat_type`(`chat_type` ASC) USING BTREE COMMENT '对话类型索引',
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE COMMENT '创建时间索引',
  CONSTRAINT `fk_ai_chat_history_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'AI对话历史表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_chat_history
-- ----------------------------

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
-- Table structure for business
-- ----------------------------
DROP TABLE IF EXISTS `business`;
CREATE TABLE `business`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` timestamp NULL DEFAULT NULL,
  `creator` bigint NULL DEFAULT NULL,
  `is_deleted` tinyint(1) UNSIGNED ZEROFILL NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL,
  `updater` bigint NULL DEFAULT NULL,
  `business_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `business_explain` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `business_img` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `business_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `delivery_price` decimal(10, 2) NULL DEFAULT NULL,
  `order_type_id` int NULL DEFAULT NULL,
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `start_price` decimal(10, 2) NULL DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '0-待审核 1-已上线 2-被拒绝',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `business_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of business
-- ----------------------------
INSERT INTO `business` VALUES (1, '2025-09-25 16:29:40', 2, 0, '2025-09-25 20:24:12', 3, '四川省成都市玉林路46号', '禾川烘焙旗下连锁早餐店', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/d6de4991-e48b-4d6f-8c6b-3c7c0584b100.jpg', '禾川早餐佳', 3.00, 2, NULL, 10.00, 2, 1);
INSERT INTO `business` VALUES (2, '2025-09-25 16:33:26', 2, 0, '2025-09-25 16:33:34', 3, '筷来见面面馆', '面条种类包罗万象，什么都有', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/359bf866-61a6-4b17-a4e9-c8a8953a76ab.jpg', '筷来见面面馆', 2.00, 1, NULL, 10.00, 17, 1);
INSERT INTO `business` VALUES (3, '2025-09-25 16:35:37', 2, 0, '2025-09-25 16:35:45', 3, '重庆市渝中区民族路168号', '禾川烘焙旗下连锁甜品店', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/221f0194-48bf-4f62-a04a-5684ee63d6eb.jpg', '禾川甜品坊', 5.00, 1, NULL, 15.00, 2, 1);
INSERT INTO `business` VALUES (4, '2025-09-25 16:58:47', 4, 0, '2025-09-25 17:06:06', 3, '天津市津南区永旺商城4楼', '朝鲜族非遗，米饭香甜', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/2a722cce-5604-4a07-9081-82981ca232fb.jpg', '米村拌饭', 0.00, 1, NULL, 8.00, 4, 1);
INSERT INTO `business` VALUES (5, '2025-09-25 17:02:51', 4, 0, '2025-09-25 17:06:32', 3, '天津市津南区北洋园校区', '肉串娇嫩多汁，香味四溢', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/29c339f6-eb51-4274-9a35-e40c1340ca48.jpg', '锦州烧烤', 0.00, 1, NULL, 10.00, 17, 1);

-- ----------------------------
-- Table structure for cart
-- ----------------------------
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` timestamp NULL DEFAULT NULL,
  `creator` bigint NULL DEFAULT NULL,
  `is_deleted` tinyint(1) NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL,
  `updater` bigint NULL DEFAULT NULL,
  `quantity` int NULL DEFAULT NULL,
  `business_id` bigint NOT NULL,
  `customer_id` bigint NOT NULL,
  `food_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `business_id`(`business_id` ASC) USING BTREE,
  INDEX `customer_id`(`customer_id` ASC) USING BTREE,
  INDEX `food_id`(`food_id` ASC) USING BTREE,
  CONSTRAINT `cart_ibfk_1` FOREIGN KEY (`business_id`) REFERENCES `business` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `cart_ibfk_2` FOREIGN KEY (`customer_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `cart_ibfk_3` FOREIGN KEY (`food_id`) REFERENCES `food` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 40 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of cart
-- ----------------------------
INSERT INTO `cart` VALUES (6, '2026-01-08 20:59:14', 7, 1, '2026-01-08 20:59:14', 7, 1, 3, 7, 1);
INSERT INTO `cart` VALUES (7, '2026-01-08 21:00:42', 7, 1, '2026-01-08 21:00:42', 7, 1, 2, 7, 3);
INSERT INTO `cart` VALUES (8, '2026-01-08 21:01:51', 7, 1, '2026-01-08 21:01:51', 7, 1, 3, 7, 1);
INSERT INTO `cart` VALUES (9, '2026-01-08 21:04:16', 7, 1, '2026-01-08 21:04:16', 7, 1, 3, 7, 1);
INSERT INTO `cart` VALUES (10, '2026-01-08 21:12:12', 8, 1, '2026-01-08 21:12:12', 8, 1, 3, 8, 1);
INSERT INTO `cart` VALUES (11, '2026-01-08 21:13:22', 8, 1, '2026-01-08 21:13:22', 8, 1, 2, 8, 3);
INSERT INTO `cart` VALUES (12, '2026-01-08 21:14:12', 8, 1, '2026-01-08 21:14:12', 8, 1, 3, 8, 1);
INSERT INTO `cart` VALUES (13, '2026-01-08 21:15:15', 8, 1, '2026-01-08 21:15:15', 8, 1, 3, 8, 1);
INSERT INTO `cart` VALUES (14, '2026-01-09 09:31:45', 9, 1, '2026-01-09 09:31:45', 9, 1, 3, 9, 1);
INSERT INTO `cart` VALUES (15, '2026-01-09 09:33:00', 9, 1, '2026-01-09 09:33:00', 9, 1, 2, 9, 3);
INSERT INTO `cart` VALUES (16, '2026-01-09 09:33:56', 9, 1, '2026-01-09 09:33:56', 9, 1, 3, 9, 1);
INSERT INTO `cart` VALUES (17, '2026-01-09 09:35:10', 9, 1, '2026-01-09 09:35:10', 9, 1, 3, 9, 1);
INSERT INTO `cart` VALUES (18, '2026-01-09 09:45:06', 10, 1, '2026-01-09 09:45:06', 10, 1, 3, 10, 1);
INSERT INTO `cart` VALUES (19, '2026-01-09 09:46:16', 10, 1, '2026-01-09 09:46:16', 10, 1, 2, 10, 3);
INSERT INTO `cart` VALUES (20, '2026-01-09 09:47:06', 10, 1, '2026-01-09 09:47:06', 10, 1, 3, 10, 1);
INSERT INTO `cart` VALUES (21, '2026-01-09 09:48:05', 10, 1, '2026-01-09 09:48:05', 10, 1, 3, 10, 1);
INSERT INTO `cart` VALUES (22, '2026-01-12 09:32:14', 12, 1, '2026-01-12 09:32:14', 12, 1, 3, 12, 1);
INSERT INTO `cart` VALUES (23, '2026-01-12 09:39:40', 12, 1, '2026-01-12 09:39:40', 12, 1, 3, 12, 1);
INSERT INTO `cart` VALUES (24, '2026-01-12 09:43:08', 12, 1, '2026-01-12 09:43:08', 12, 1, 3, 12, 1);
INSERT INTO `cart` VALUES (25, '2026-01-12 09:44:24', 12, 1, '2026-01-12 09:44:24', 12, 1, 2, 12, 3);
INSERT INTO `cart` VALUES (26, '2026-01-12 09:45:19', 12, 1, '2026-01-12 09:45:19', 12, 1, 3, 12, 1);
INSERT INTO `cart` VALUES (27, '2026-01-12 09:49:51', 13, 1, '2026-01-12 09:49:51', 13, 1, 3, 13, 1);
INSERT INTO `cart` VALUES (28, '2026-01-12 09:50:59', 13, 1, '2026-01-12 09:50:59', 13, 1, 2, 13, 3);
INSERT INTO `cart` VALUES (29, '2026-01-12 09:55:16', 14, 1, '2026-01-12 09:55:16', 14, 1, 3, 14, 1);
INSERT INTO `cart` VALUES (30, '2026-01-12 09:56:24', 14, 1, '2026-01-12 09:56:24', 14, 1, 2, 14, 3);
INSERT INTO `cart` VALUES (31, '2026-01-12 09:57:19', 14, 1, '2026-01-12 09:57:19', 14, 1, 3, 14, 1);
INSERT INTO `cart` VALUES (32, '2026-01-12 09:58:29', 14, 1, '2026-01-12 09:58:29', 14, 1, 3, 14, 1);
INSERT INTO `cart` VALUES (33, '2026-01-13 09:30:29', 15, 1, '2026-01-13 09:30:29', 15, 1, 3, 15, 1);
INSERT INTO `cart` VALUES (34, '2026-01-13 09:38:28', 15, 1, '2026-01-13 09:38:28', 15, 1, 3, 15, 1);
INSERT INTO `cart` VALUES (35, '2026-01-13 09:39:46', 15, 1, '2026-01-13 09:39:46', 15, 1, 2, 15, 3);
INSERT INTO `cart` VALUES (36, '2026-01-13 09:40:36', 15, 1, '2026-01-13 09:40:36', 15, 1, 3, 15, 1);
INSERT INTO `cart` VALUES (37, '2026-01-13 09:41:54', 15, 1, '2026-01-13 09:41:54', 15, 1, 3, 15, 1);
INSERT INTO `cart` VALUES (38, '2026-01-13 18:27:56', 16, 1, '2026-01-13 18:27:56', 16, 2, 4, 16, 12);
INSERT INTO `cart` VALUES (39, '2026-01-13 18:28:26', 16, 1, '2026-01-13 18:28:26', 16, 1, 3, 16, 2);

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
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `delivery_address_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of delivery_address
-- ----------------------------
INSERT INTO `delivery_address` VALUES (4, '2025-09-25 21:34:42', 5, 0, '2025-09-25 23:00:36', 5, '湖南岳阳汴河街', '小高', 0, '13873021510', 5);
INSERT INTO `delivery_address` VALUES (6, '2025-09-25 21:49:55', 6, 0, '2025-09-25 21:49:55', 6, '湖南岳阳蓝剑大厦', '小刘', 1, '13873021510', 6);
INSERT INTO `delivery_address` VALUES (7, '2025-09-26 10:39:27', 5, 0, '2025-09-26 10:39:27', 5, '湖南岳阳楼', '小李', 1, '16607402898', 5);
INSERT INTO `delivery_address` VALUES (9, '2026-01-08 20:59:24', 7, 0, '2026-01-08 20:59:24', 7, '湖南岳阳蓝剑大厦', '小萌', 1, '13873021510', 7);
INSERT INTO `delivery_address` VALUES (10, '2026-01-08 21:12:17', 8, 0, '2026-01-08 21:12:17', 8, '湖南岳阳蓝剑大厦', '小萌', 1, '13873021510', 8);
INSERT INTO `delivery_address` VALUES (11, '2026-01-09 09:31:52', 9, 0, '2026-01-09 09:31:52', 9, '湖南岳阳', '十行', 1, '13873021510', 9);
INSERT INTO `delivery_address` VALUES (12, '2026-01-09 09:45:13', 10, 0, '2026-01-09 09:45:13', 10, '湖南岳阳蓝剑大厦', '小萌', 1, '13873021510', 10);
INSERT INTO `delivery_address` VALUES (14, '2026-01-12 09:43:15', 12, 0, '2026-01-12 09:43:15', 12, '湖南岳阳蓝剑大厦', '小萌', 1, '13873021510', 12);
INSERT INTO `delivery_address` VALUES (16, '2026-01-12 09:55:22', 14, 0, '2026-01-12 09:55:22', 14, '湖南岳阳蓝剑大厦', '小萌', 1, '13873021510', 14);
INSERT INTO `delivery_address` VALUES (18, '2026-01-13 09:38:34', 15, 0, '2026-01-13 09:38:34', 15, '湖南岳阳蓝剑大厦', '小萌', 1, '13873021510', 15);
INSERT INTO `delivery_address` VALUES (19, '2026-01-13 18:28:06', 16, 0, '2026-01-13 18:28:06', 16, '天津大学卫津路校区西门', '江哈哈', 1, '16736787654', 16);

-- ----------------------------
-- Table structure for food
-- ----------------------------
DROP TABLE IF EXISTS `food`;
CREATE TABLE `food`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` timestamp NULL DEFAULT NULL,
  `creator` bigint NULL DEFAULT NULL,
  `is_deleted` tinyint(1) NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL,
  `updater` bigint NULL DEFAULT NULL,
  `food_explain` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `food_img` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `food_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `food_price` decimal(10, 2) NOT NULL,
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `business_id` bigint NOT NULL,
  `shelve_status` tinyint NULL DEFAULT 1 COMMENT '0-已下架 1-已上架',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `business_id`(`business_id` ASC) USING BTREE,
  CONSTRAINT `food_ibfk_1` FOREIGN KEY (`business_id`) REFERENCES `business` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of food
-- ----------------------------
INSERT INTO `food` VALUES (1, '2025-09-25 16:38:22', 2, 0, '2025-09-25 16:38:22', 2, '一人份哦~', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/ba2e48e5-d675-43ae-989d-1d0fa98153dd.jpg', '瑞士卷', 28.00, NULL, 3, 1);
INSERT INTO `food` VALUES (2, '2025-09-25 16:39:45', 2, 0, '2025-09-25 16:39:45', 2, '甜蜜诱惑', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/47c56842-a0f3-43f7-b21a-387bb139664c.jpg', '巧克力华莱士', 32.00, NULL, 3, 1);
INSERT INTO `food` VALUES (3, '2025-09-25 16:44:58', 2, 0, '2025-09-25 16:44:58', 2, '飘香葱油', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/d2479b6f-d86d-4b3b-aff1-8af0ed120b15.jpg', '葱油拌面', 14.00, NULL, 2, 1);
INSERT INTO `food` VALUES (4, '2025-09-25 16:45:42', 2, 0, '2025-09-25 16:45:42', 2, '码子任选', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/fac75178-95cf-4472-bbf6-0e3a6b5bdd23.jpg', '刀削面', 15.00, NULL, 2, 1);
INSERT INTO `food` VALUES (5, '2025-09-25 16:46:42', 2, 0, '2025-09-25 16:46:42', 2, '秘制酱料', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/155b9cc9-b931-465f-ade1-65c350ba6a35.jpg', '杂酱面', 8.00, NULL, 2, 1);
INSERT INTO `food` VALUES (6, '2025-09-25 16:50:17', 2, 0, '2025-09-25 16:50:17', 2, '健康美味', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/35130739-f08d-4201-9e21-947627a87e73.jpg', '花卷', 2.00, NULL, 1, 1);
INSERT INTO `food` VALUES (7, '2025-09-25 16:51:42', 2, 0, '2025-09-25 16:51:42', 2, '好吃不贵', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/d0a8fdc8-a9de-41ca-abdc-ef8388cb32eb.jpg', '馒头', 1.00, NULL, 1, 1);
INSERT INTO `food` VALUES (8, '2025-09-25 16:53:11', 2, 0, '2025-09-25 16:53:11', 2, '新鲜现磨', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/e77ec4c4-ef71-4898-90c8-8dd5da679706.jpg', '豆浆', 3.00, NULL, 1, 1);
INSERT INTO `food` VALUES (9, '2025-09-25 17:04:04', 4, 0, '2025-09-25 17:04:04', 4, '蒜香', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/36d12b14-9dd2-4aa5-92ab-ca13fdbd030a.jpg', '烤鸡翅', 10.00, NULL, 5, 1);
INSERT INTO `food` VALUES (10, '2025-09-25 17:04:48', 4, 0, '2025-09-25 17:04:48', 4, '香辣', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/e4865d4e-90ed-4ed6-99e4-a7b0289fb53f.jpg', '烤鱿鱼', 15.00, NULL, 5, 1);
INSERT INTO `food` VALUES (12, '2025-09-25 20:09:25', 4, 0, '2025-09-25 20:09:25', 4, '香辣', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/1eedbc08-91fa-402b-abf3-26c4269e2b85.jpg', '烧土豆炖牛肉', 31.90, NULL, 4, 1);
INSERT INTO `food` VALUES (13, '2025-09-25 20:10:35', 4, 0, '2025-09-25 20:10:35', 4, '软糯解腻', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/89b4b070-841d-4e79-8618-eb895d091d8a.jpg', '石锅营养菜心', 13.60, NULL, 4, 1);

-- ----------------------------
-- Table structure for marketing_points_exchange_rule
-- ----------------------------
DROP TABLE IF EXISTS `marketing_points_exchange_rule`;
CREATE TABLE `marketing_points_exchange_rule`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `rule_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '规则名称',
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
  INDEX `idx_rule_type`(`rule_type` ASC) USING BTREE COMMENT '规则类型索引',
  INDEX `idx_rule_status`(`rule_status` ASC) USING BTREE COMMENT '规则状态索引',
  INDEX `idx_food_id`(`food_id` ASC) USING BTREE COMMENT '商品ID索引',
  CONSTRAINT `fk_marketing_exchange_rule_food` FOREIGN KEY (`food_id`) REFERENCES `food` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '营销系统-积分兑换规则表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of marketing_points_exchange_rule
-- ----------------------------
INSERT INTO `marketing_points_exchange_rule` VALUES (1, '积分+现金兑换规则', 0, 1, 1000.0000, 0, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-08 04:55:30', '2026-01-13 10:00:43', 1, 3, 0);
INSERT INTO `marketing_points_exchange_rule` VALUES (2, '积分兑换商品', 1, 1, NULL, NULL, NULL, 1, 40, 100, NULL, NULL, '2026-01-08 04:55:30', '2026-01-08 04:55:30', 1, 1, 0);
INSERT INTO `marketing_points_exchange_rule` VALUES (3, '积分兑换商品', 1, 1, NULL, NULL, NULL, 2, 50, 100, NULL, NULL, '2026-01-08 04:55:30', '2026-01-08 04:55:30', 1, 1, 0);
INSERT INTO `marketing_points_exchange_rule` VALUES (4, '积分兑换商品', 1, 1, NULL, NULL, NULL, 3, 60, 100, NULL, NULL, '2026-01-08 04:55:30', '2026-01-08 04:55:30', 1, 1, 0);
INSERT INTO `marketing_points_exchange_rule` VALUES (5, '积分兑换商品', 1, 1, NULL, NULL, NULL, 4, 70, 100, NULL, NULL, '2026-01-08 04:55:30', '2026-01-08 04:55:30', 1, 1, 0);
INSERT INTO `marketing_points_exchange_rule` VALUES (6, '积分兑换商品', 1, 1, NULL, NULL, NULL, 5, 80, 89, NULL, NULL, '2026-01-08 04:55:30', '2026-01-13 10:00:56', 1, 3, 0);

-- ----------------------------
-- Table structure for marketing_points_rule
-- ----------------------------
DROP TABLE IF EXISTS `marketing_points_rule`;
CREATE TABLE `marketing_points_rule`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `rule_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '规则名称',
  `rule_type` tinyint NOT NULL COMMENT '规则类型 0-消费积分 1-促销积分 2-等级积分 3-行为积分',
  `rule_status` tinyint NOT NULL DEFAULT 1 COMMENT '规则状态 0-禁用 1-启用',
  `points_ratio` decimal(10, 4) NULL DEFAULT NULL COMMENT '积分比例（如0.1表示消费1元获得0.1积分）',
  `points_multiplier` decimal(10, 4) NULL DEFAULT NULL COMMENT '积分倍数（促销时使用，如2.0表示双倍积分）',
  `member_level` tinyint NULL DEFAULT NULL COMMENT '适用会员等级（NULL表示所有等级）。对于等级积分(rule_type=2)，表示升级到的目标会员等级（1-白银 2-黄金 3-钻石）',
  `min_order_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '最低订单金额（促销积分使用）',
  `max_order_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '最高订单金额（促销积分使用）',
  `holiday_start` date NULL DEFAULT NULL COMMENT '节假日开始日期',
  `holiday_end` date NULL DEFAULT NULL COMMENT '节假日结束日期',
  `behavior_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '行为类型（like-点赞 collect-收藏 repay_loan-还贷款）',
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
  INDEX `idx_rule_type`(`rule_type` ASC) USING BTREE COMMENT '规则类型索引',
  INDEX `idx_rule_status`(`rule_status` ASC) USING BTREE COMMENT '规则状态索引',
  INDEX `idx_start_end_time`(`start_time` ASC, `end_time` ASC) USING BTREE COMMENT '生效时间索引',
  INDEX `idx_member_level`(`member_level` ASC) USING BTREE COMMENT '会员等级索引'
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '营销系统-积分规则表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of marketing_points_rule
-- ----------------------------
INSERT INTO `marketing_points_rule` VALUES (1, '基础消费积分规则', 0, 1, 1.0000, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, '2026-01-08 04:55:30', '2026-01-08 04:55:30', 1, 1, 0);
INSERT INTO `marketing_points_rule` VALUES (2, '升级到白银会员积分规则', 2, 1, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL, 100, 30, NULL, NULL, 0, '2026-01-08 04:55:30', '2026-01-08 04:55:30', 1, 1, 0);
INSERT INTO `marketing_points_rule` VALUES (3, '升级到黄金会员积分规则', 2, 1, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, 200, 30, NULL, NULL, 0, '2026-01-08 04:55:30', '2026-01-08 04:55:30', 1, 1, 0);
INSERT INTO `marketing_points_rule` VALUES (4, '升级到钻石会员积分规则', 2, 1, NULL, NULL, 3, NULL, NULL, NULL, NULL, NULL, 300, 30, NULL, NULL, 0, '2026-01-08 04:55:30', '2026-01-08 04:55:30', 1, 1, 0);
INSERT INTO `marketing_points_rule` VALUES (5, '点赞商家积分规则-普通用户', 3, 1, NULL, NULL, 0, NULL, NULL, NULL, NULL, 'like', 10, 30, NULL, NULL, 0, '2026-01-08 04:55:30', '2026-01-08 04:55:30', 1, 1, 0);
INSERT INTO `marketing_points_rule` VALUES (6, '点赞商家积分规则-白银会员', 3, 1, NULL, NULL, 1, NULL, NULL, NULL, NULL, 'like', 15, 30, NULL, NULL, 0, '2026-01-08 04:55:30', '2026-01-08 04:55:30', 1, 1, 0);
INSERT INTO `marketing_points_rule` VALUES (7, '点赞商家积分规则-黄金会员', 3, 1, NULL, NULL, 2, NULL, NULL, NULL, NULL, 'like', 20, 30, NULL, NULL, 0, '2026-01-08 04:55:30', '2026-01-08 04:55:30', 1, 1, 0);
INSERT INTO `marketing_points_rule` VALUES (8, '点赞商家积分规则-钻石会员', 3, 1, NULL, NULL, 3, NULL, NULL, NULL, NULL, 'like', 30, 30, NULL, NULL, 0, '2026-01-08 04:55:30', '2026-01-08 04:55:30', 1, 1, 0);
INSERT INTO `marketing_points_rule` VALUES (9, '收藏商家积分规则-普通用户', 3, 1, NULL, NULL, 0, NULL, NULL, NULL, NULL, 'collect', 20, 30, NULL, NULL, 0, '2026-01-08 04:55:30', '2026-01-08 04:55:30', 1, 1, 0);
INSERT INTO `marketing_points_rule` VALUES (10, '收藏商家积分规则-白银会员', 3, 1, NULL, NULL, 1, NULL, NULL, NULL, NULL, 'collect', 30, 30, NULL, NULL, 0, '2026-01-08 04:55:30', '2026-01-08 04:55:30', 1, 1, 0);
INSERT INTO `marketing_points_rule` VALUES (11, '收藏商家积分规则-黄金会员', 3, 1, NULL, NULL, 2, NULL, NULL, NULL, NULL, 'collect', 40, 30, NULL, NULL, 0, '2026-01-08 04:55:30', '2026-01-08 04:55:30', 1, 1, 0);
INSERT INTO `marketing_points_rule` VALUES (12, '收藏商家积分规则-钻石会员', 3, 1, NULL, NULL, 3, NULL, NULL, NULL, NULL, 'collect', 60, 30, NULL, NULL, 0, '2026-01-08 04:55:30', '2026-01-08 04:55:30', 1, 1, 0);
INSERT INTO `marketing_points_rule` VALUES (13, '还贷款积分规则-普通用户', 3, 1, NULL, NULL, 0, NULL, NULL, NULL, NULL, 'repay_loan', 50, 30, NULL, NULL, 0, '2026-01-08 04:55:30', '2026-01-08 04:55:30', 1, 1, 0);
INSERT INTO `marketing_points_rule` VALUES (14, '还贷款积分规则-白银会员', 3, 1, NULL, NULL, 1, NULL, NULL, NULL, NULL, 'repay_loan', 75, 30, NULL, NULL, 0, '2026-01-08 04:55:30', '2026-01-08 04:55:30', 1, 1, 0);
INSERT INTO `marketing_points_rule` VALUES (15, '还贷款积分规则-黄金会员', 3, 1, NULL, NULL, 2, NULL, NULL, NULL, NULL, 'repay_loan', 100, 30, NULL, NULL, 0, '2026-01-08 04:55:30', '2026-01-08 04:55:30', 1, 1, 0);
INSERT INTO `marketing_points_rule` VALUES (16, '还贷款积分规则-钻石会员', 3, 1, NULL, NULL, 3, NULL, NULL, NULL, NULL, 'repay_loan', 150, 30, NULL, NULL, 0, '2026-01-08 04:55:30', '2026-01-08 04:55:30', 1, 1, 0);
INSERT INTO `marketing_points_rule` VALUES (17, '十一黄金周双倍积分', 1, 1, NULL, 2.0000, NULL, NULL, NULL, '2025-10-01', '2025-10-07', NULL, NULL, 15, NULL, NULL, 10, '2026-01-08 04:55:30', '2026-01-13 09:57:49', 1, 3, 0);
INSERT INTO `marketing_points_rule` VALUES (18, '满200元双倍积分', 1, 1, NULL, 2.0000, NULL, 200.00, NULL, NULL, NULL, NULL, NULL, 15, NULL, NULL, 8, '2026-01-08 04:55:30', '2026-01-08 12:42:04', 1, 1, 0);
INSERT INTO `marketing_points_rule` VALUES (19, '指定商品双倍积分', 1, 1, NULL, 2.0000, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 15, NULL, NULL, 7, '2026-01-08 04:55:30', '2026-01-08 12:42:07', 1, 1, 0);
INSERT INTO `marketing_points_rule` VALUES (21, '钻石会员双十二三倍积分', 1, 1, NULL, 3.0000, 3, NULL, NULL, '2025-12-01', '2025-12-14', NULL, NULL, 15, NULL, NULL, 9, '2026-01-08 04:55:30', '2026-01-08 12:42:09', 1, 1, 0);
INSERT INTO `marketing_points_rule` VALUES (22, '元旦期间订单满300元2.5倍积分', 1, 1, NULL, 2.5000, NULL, 300.00, NULL, '2026-01-01', '2026-01-04', NULL, NULL, 15, NULL, NULL, 11, '2026-01-08 04:55:30', '2026-01-13 09:57:30', 1, 3, 0);

-- ----------------------------
-- Table structure for marketing_points_rule_food
-- ----------------------------
DROP TABLE IF EXISTS `marketing_points_rule_food`;
CREATE TABLE `marketing_points_rule_food`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `rule_id` bigint NOT NULL COMMENT '积分规则ID（关联marketing_points_rule表）',
  `food_id` bigint NOT NULL COMMENT '商品ID（关联food表）',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_rule_food`(`rule_id` ASC, `food_id` ASC, `is_deleted` ASC) USING BTREE COMMENT '规则商品唯一索引（考虑软删除）',
  INDEX `idx_rule_id`(`rule_id` ASC) USING BTREE COMMENT '规则ID索引',
  INDEX `idx_food_id`(`food_id` ASC) USING BTREE COMMENT '商品ID索引',
  CONSTRAINT `fk_rule_food_food` FOREIGN KEY (`food_id`) REFERENCES `food` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_rule_food_rule` FOREIGN KEY (`rule_id`) REFERENCES `marketing_points_rule` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '促销积分规则与商品关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of marketing_points_rule_food
-- ----------------------------
INSERT INTO `marketing_points_rule_food` VALUES (1, 19, 3, '2026-01-08 11:54:06', '2026-01-08 11:54:06', 0);
INSERT INTO `marketing_points_rule_food` VALUES (2, 19, 4, '2026-01-08 11:54:14', '2026-01-08 11:54:14', 0);
INSERT INTO `marketing_points_rule_food` VALUES (3, 19, 5, '2026-01-08 11:54:22', '2026-01-08 11:54:22', 0);
INSERT INTO `marketing_points_rule_food` VALUES (4, 19, 6, '2026-01-08 11:54:32', '2026-01-08 11:54:32', 0);
INSERT INTO `marketing_points_rule_food` VALUES (5, 19, 7, '2026-01-08 11:54:43', '2026-01-08 11:54:43', 0);

-- ----------------------------
-- Table structure for merchant_interaction
-- ----------------------------
DROP TABLE IF EXISTS `merchant_interaction`;
CREATE TABLE `merchant_interaction`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `merchant_id` bigint NOT NULL COMMENT '商铺ID',
  `liked` tinyint(1) NULL DEFAULT 0 COMMENT '是否点赞(0:否,1:是)',
  `collected` tinyint(1) NULL DEFAULT 0 COMMENT '是否收藏(0:否,1:是)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_merchant`(`user_id` ASC, `merchant_id` ASC) USING BTREE COMMENT '用户商家唯一索引',
  INDEX `idx_merchant_id`(`merchant_id` ASC) USING BTREE COMMENT '商家ID索引',
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE COMMENT '用户ID索引',
  INDEX `idx_liked`(`liked` ASC) USING BTREE COMMENT '点赞状态索引',
  INDEX `idx_collected`(`collected` ASC) USING BTREE COMMENT '收藏状态索引',
  CONSTRAINT `fk_interaction_business` FOREIGN KEY (`merchant_id`) REFERENCES `business` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_interaction_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商家互动表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of merchant_interaction
-- ----------------------------
INSERT INTO `merchant_interaction` VALUES (1, 7, 3, 1, 1, '2026-01-08 12:23:21', '2026-01-08 12:23:22');
INSERT INTO `merchant_interaction` VALUES (2, 8, 3, 1, 1, '2026-01-08 13:15:12', '2026-01-08 13:15:13');
INSERT INTO `merchant_interaction` VALUES (3, 10, 3, 1, 1, '2026-01-09 01:48:00', '2026-01-09 01:48:01');
INSERT INTO `merchant_interaction` VALUES (4, 14, 3, 1, 1, '2026-01-12 01:58:23', '2026-01-12 01:58:26');
INSERT INTO `merchant_interaction` VALUES (5, 15, 3, 1, 1, '2026-01-13 01:41:50', '2026-01-13 01:41:52');
INSERT INTO `merchant_interaction` VALUES (6, 16, 4, 1, 0, '2026-01-13 10:27:59', '2026-01-13 10:27:59');
INSERT INTO `merchant_interaction` VALUES (7, 16, 3, 0, 1, '2026-01-13 10:28:24', '2026-01-13 10:28:24');

-- ----------------------------
-- Table structure for notification
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息唯一ID',
  `user_id` bigint NOT NULL COMMENT '接收用户ID（关联users表id）',
  `notification_type` tinyint NOT NULL COMMENT '消息类型：0=商家申请审核，1=开店申请审核，2=积分过期预警',
  `notification_content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息内容',
  `audit_result` tinyint NULL DEFAULT NULL COMMENT '审核结果：1=通过，2=拒绝（对于积分过期预警notification_type=2时，此字段为NULL）',
  `is_read` tinyint NOT NULL DEFAULT 0 COMMENT '阅读状态：0=未读，1=已读',
  `create_time` datetime NOT NULL COMMENT '消息创建时间',
  `read_time` datetime NULL DEFAULT NULL COMMENT '消息阅读时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_user_is_read`(`user_id` ASC, `is_read` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户消息通知表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of notification
-- ----------------------------
INSERT INTO `notification` VALUES (1, 5, 2, '尊敬的用户，您有100积分将于2026-01-11过期，请及时使用。', NULL, 0, '2026-01-08 12:36:06', NULL, 0);

-- ----------------------------
-- Table structure for orderdetailet
-- ----------------------------
DROP TABLE IF EXISTS `orderdetailet`;
CREATE TABLE `orderdetailet`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` timestamp NULL DEFAULT NULL,
  `creator` bigint NULL DEFAULT NULL,
  `is_deleted` tinyint(1) NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL,
  `updater` bigint NULL DEFAULT NULL,
  `quantity` int NULL DEFAULT NULL,
  `food_id` bigint NOT NULL,
  `order_id` bigint NOT NULL,
  `food_price` decimal(10, 2) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `food_id`(`food_id` ASC) USING BTREE,
  INDEX `order_id`(`order_id` ASC) USING BTREE,
  CONSTRAINT `orderdetailet_ibfk_1` FOREIGN KEY (`food_id`) REFERENCES `food` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `orderdetailet_ibfk_2` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 46 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of orderdetailet
-- ----------------------------
INSERT INTO `orderdetailet` VALUES (39, '2026-01-13 09:38:38', 15, 0, '2026-01-13 09:38:38', 15, 1, 1, 39, 28.00);
INSERT INTO `orderdetailet` VALUES (40, '2026-01-13 09:39:51', 15, 0, '2026-01-13 09:39:51', 15, 1, 3, 40, 14.00);
INSERT INTO `orderdetailet` VALUES (41, '2026-01-13 09:40:43', 15, 0, '2026-01-13 09:40:43', 15, 1, 1, 41, 28.00);
INSERT INTO `orderdetailet` VALUES (42, '2026-01-13 09:42:00', 15, 0, '2026-01-13 09:42:00', 15, 1, 1, 42, 28.00);
INSERT INTO `orderdetailet` VALUES (43, '2026-01-13 09:45:02', 15, 0, '2026-01-13 09:45:02', 15, 1, 5, 43, 8.00);
INSERT INTO `orderdetailet` VALUES (44, '2026-01-13 18:28:10', 16, 0, '2026-01-13 18:28:10', 16, 2, 12, 44, 31.90);
INSERT INTO `orderdetailet` VALUES (45, '2026-01-13 18:28:30', 16, 0, '2026-01-13 18:28:30', 16, 1, 2, 45, 32.00);

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` timestamp NULL DEFAULT NULL,
  `creator` bigint NULL DEFAULT NULL,
  `is_deleted` tinyint(1) NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL,
  `updater` bigint NULL DEFAULT NULL,
  `order_date` timestamp NULL DEFAULT NULL,
  `order_state` int NULL DEFAULT NULL,
  `order_total` decimal(10, 2) NOT NULL,
  `business_id` bigint NOT NULL,
  `customer_id` bigint NOT NULL,
  `address_id` bigint NOT NULL,
  `delivery_price` decimal(10, 2) NOT NULL,
  `payment_method` tinyint NULL DEFAULT NULL COMMENT '支付方式 0-微信支付 1-支付宝支付 2-虚拟钱包支付 3-积分兑换',
  `points_used` bigint NULL DEFAULT 0 COMMENT '使用积分数量',
  `points_amount` bigint NULL DEFAULT 0 COMMENT '获得积分数量',
  `points_discount_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '积分抵扣的现金金额（元）',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `contact_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `contact_sex` int NULL DEFAULT NULL,
  `contact_tel` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `business_id`(`business_id` ASC) USING BTREE,
  INDEX `customer_id`(`customer_id` ASC) USING BTREE,
  INDEX `address_id`(`address_id` ASC) USING BTREE,
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`business_id`) REFERENCES `business` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`customer_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `orders_ibfk_3` FOREIGN KEY (`address_id`) REFERENCES `delivery_address` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 46 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (39, '2026-01-13 09:38:38', 15, 0, '2026-01-13 09:38:38', 15, '2026-01-13 09:38:38', 3, 33.00, 3, 15, 18, 5.00, 2, 0, 33, 0.00, '湖南岳阳蓝剑大厦', '小萌', 1, '13873021510');
INSERT INTO `orders` VALUES (40, '2026-01-13 09:39:51', 15, 0, '2026-01-13 09:39:51', 15, '2026-01-13 09:39:51', 4, 16.00, 2, 15, 18, 2.00, 2, 0, 32, 0.00, '湖南岳阳蓝剑大厦', '小萌', 1, '13873021510');
INSERT INTO `orders` VALUES (41, '2026-01-13 09:40:43', 15, 0, '2026-01-13 09:40:43', 15, '2026-01-13 09:40:43', 1, 33.00, 3, 15, 18, 5.00, 2, 0, 33, 0.00, '湖南岳阳蓝剑大厦', '小萌', 1, '13873021510');
INSERT INTO `orders` VALUES (42, '2026-01-13 09:42:00', 15, 0, '2026-01-13 09:42:00', 15, '2026-01-13 09:42:00', 1, 33.00, 3, 15, 18, 5.00, NULL, 0, 33, 0.00, '湖南岳阳蓝剑大厦', '小萌', 1, '13873021510');
INSERT INTO `orders` VALUES (43, '2026-01-13 09:45:02', 15, 0, '2026-01-13 09:45:02', 15, '2026-01-13 09:45:02', 1, 0.00, 2, 15, 18, 0.00, 3, 0, 0, 0.00, NULL, NULL, NULL, NULL);
INSERT INTO `orders` VALUES (44, '2026-01-13 18:28:10', 16, 0, '2026-01-13 18:28:10', 16, '2026-01-13 18:28:10', 1, 63.80, 4, 16, 19, 0.00, NULL, 10, 63, 0.01, '天津大学卫津路校区西门', '江哈哈', 1, '16736787654');
INSERT INTO `orders` VALUES (45, '2026-01-13 18:28:30', 16, 0, '2026-01-13 18:28:30', 16, '2026-01-13 18:28:30', 1, 37.00, 3, 16, 19, 5.00, NULL, 0, 37, 0.00, '天津大学卫津路校区西门', '江哈哈', 1, '16736787654');

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
  PRIMARY KEY (`id`) USING BTREE,
  CONSTRAINT `person_ibfk_1` FOREIGN KEY (`id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of person
-- ----------------------------
INSERT INTO `person` VALUES ('3347127717@qq.com', '涛', '0', '灿', '19173095993', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/3d038310-4d85-4388-a026-65b6d437b22b.jpg', 2);
INSERT INTO `person` VALUES ('3347127717@qq.com', '李', '0', '灿', '16607302890', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/295a7008-aebd-4389-8a71-a9a278034d3e.jpg', 4);
INSERT INTO `person` VALUES ('3347127717@qq.com', '这', '0', '灿', '19173095993', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/54c4ddcf-ac85-454c-9792-a8e794df3ec4.jpg', 5);
INSERT INTO `person` VALUES ('736213069@qq.com', '宋', '1', '灿', '16607302890', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/2df21e82-c447-4047-810c-1b680ee309b2.jpg', 6);
INSERT INTO `person` VALUES ('736213069@qq.com', '白', '0', '行', '13873021510', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/835e10f5-dde2-4fef-bc92-6cc4dde04cff.png', 7);
INSERT INTO `person` VALUES ('736213069@qq.com', '白', '0', '行', '13873021510', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/d027c75b-fb05-4b24-87e4-b872efc60bc6.png', 8);
INSERT INTO `person` VALUES ('736213069@qq.com', '时', '0', '行', '13873021510', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/c1b1ee21-7aec-436c-99f0-0d422f1fc669.png', 9);
INSERT INTO `person` VALUES ('736213069@qq.com', '白', '0', '行', '13873021510', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/e8889de6-c0d6-49ad-8349-97763216e75b.png', 10);
INSERT INTO `person` VALUES ('12746375@tju.edu.cn', '丽丽', '0', '林', '13021377574', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/f0cbfdd6-6935-4778-894a-86e3376f0a6d.jpg', 11);
INSERT INTO `person` VALUES ('736213069@qq.com', '白', '0', '行', '13873021510', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/2d023cbd-4a8d-4220-86ce-8033eeda5370.png', 12);
INSERT INTO `person` VALUES ('736213069@qq.com', '白', '0', '行', '13873021510', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/3e6c71f5-ccee-4fbe-bc33-c4d629ccd1ad.png', 13);
INSERT INTO `person` VALUES ('736213069@qq.com', '白', '0', '行', '13873021510', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/8a131d22-f429-480f-b094-da131912c316.png', 14);
INSERT INTO `person` VALUES ('736213069@qq.com', '白', '0', '行', '13873021510', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/7ed0cf22-c938-411b-9fc5-03b42f3c20d7.png', 15);
INSERT INTO `person` VALUES ('364726234@tju.edu.cn', '哈哈', '0', '江', '16736787654', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/ec3db0c2-2b9f-49ea-86df-59caddae0f4b.png', 16);
INSERT INTO `person` VALUES ('132482136512@tju.edu.cn', '丽丽', '0', '林', '13021377574', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/3054da5c-f531-4338-a20e-bf4305e3c4f9.png', 17);
INSERT INTO `person` VALUES ('3248123412@tju.edu.cn', 'elm', '0', 'admin', '17463332647', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/cbbc8b8c-ea85-4053-bb6b-9667fbb5b72e.png', 18);

-- ----------------------------
-- Table structure for points_account
-- ----------------------------
DROP TABLE IF EXISTS `points_account`;
CREATE TABLE `points_account`  (
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
  UNIQUE INDEX `uk_user_id`(`user_id` ASC) USING BTREE COMMENT '用户ID唯一索引',
  INDEX `idx_member_level`(`member_level` ASC) USING BTREE COMMENT '会员等级索引',
  CONSTRAINT `fk_points_account_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '积分账户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of points_account
-- ----------------------------
INSERT INTO `points_account` VALUES (2, 7, 239, 173, 66, 2, '2026-01-08 20:59:35', '2026-01-08 21:07:12', NULL, 7, 0);
INSERT INTO `points_account` VALUES (3, 8, 803, 626, 177, 2, '2026-01-08 21:10:44', '2026-01-09 15:48:15', 8, NULL, 0);
INSERT INTO `points_account` VALUES (4, 9, 289, 223, 66, 2, '2026-01-09 09:22:00', '2026-01-09 09:37:48', 9, 9, 0);
INSERT INTO `points_account` VALUES (5, 10, 399, 333, 66, 2, '2026-01-09 09:43:48', '2026-01-09 09:50:32', 10, 10, 0);
INSERT INTO `points_account` VALUES (6, 11, 0, 0, 0, 0, '2026-01-09 18:20:19', '2026-01-09 18:20:19', 11, 11, 0);
INSERT INTO `points_account` VALUES (7, 12, 299, 233, 66, 2, '2026-01-12 09:28:52', '2026-01-12 09:45:33', 12, NULL, 0);
INSERT INTO `points_account` VALUES (8, 13, 33, 33, 0, 0, '2026-01-12 09:47:56', '2026-01-12 09:51:15', 13, 13, 0);
INSERT INTO `points_account` VALUES (9, 14, 479, 413, 66, 2, '2026-01-12 09:54:02', '2026-01-12 09:58:49', 14, NULL, 0);
INSERT INTO `points_account` VALUES (11, 15, 479, 413, 66, 2, '2026-01-13 09:38:47', '2026-01-13 09:45:02', NULL, 15, 0);
INSERT INTO `points_account` VALUES (12, 16, 130, 20, 110, 0, '2026-01-13 18:23:27', '2026-01-13 18:28:35', 16, NULL, 0);
INSERT INTO `points_account` VALUES (13, 17, 0, 0, 0, 0, '2026-01-13 18:24:06', '2026-01-13 18:24:06', 17, 17, 0);
INSERT INTO `points_account` VALUES (14, 18, 0, 0, 0, 0, '2026-01-13 18:25:11', '2026-01-13 18:25:11', 18, 18, 0);

-- ----------------------------
-- Table structure for points_exchange_order
-- ----------------------------
DROP TABLE IF EXISTS `points_exchange_order`;
CREATE TABLE `points_exchange_order`  (
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
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE COMMENT '用户ID索引',
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE COMMENT '订单ID索引',
  INDEX `idx_status`(`status` ASC) USING BTREE COMMENT '状态索引',
  INDEX `fk_points_exchange_order_food`(`food_id` ASC) USING BTREE,
  CONSTRAINT `fk_points_exchange_order_food` FOREIGN KEY (`food_id`) REFERENCES `food` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_points_exchange_order_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_points_exchange_order_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '积分兑换订单表（仅用于纯积分兑换商品，积分+现金支付不使用此表）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of points_exchange_order
-- ----------------------------
INSERT INTO `points_exchange_order` VALUES (6, 15, 43, 5, 80, 0.00, 0, '2026-01-13 09:45:02', '2026-01-13 09:45:02', 0);

-- ----------------------------
-- Table structure for points_expiration
-- ----------------------------
DROP TABLE IF EXISTS `points_expiration`;
CREATE TABLE `points_expiration`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID（关联users表）',
  `transaction_id` bigint NOT NULL COMMENT '积分明细ID（关联points_transaction表）',
  `points_amount` bigint NOT NULL COMMENT '即将过期积分数量',
  `expire_time` timestamp NOT NULL COMMENT '过期时间（精确到秒）',
  `expire_date` date NOT NULL COMMENT '过期日期（用于按日期查询）',
  `is_expired` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已过期 0-未过期 1-已过期',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE COMMENT '用户ID索引',
  INDEX `idx_expire_time`(`expire_time` ASC) USING BTREE COMMENT '过期时间索引',
  INDEX `idx_expire_date`(`expire_date` ASC) USING BTREE COMMENT '过期日期索引',
  INDEX `idx_is_expired`(`is_expired` ASC) USING BTREE COMMENT '过期状态索引',
  INDEX `idx_user_expire`(`user_id` ASC, `expire_time` ASC) USING BTREE COMMENT '用户过期时间联合索引',
  INDEX `fk_points_expiration_transaction`(`transaction_id` ASC) USING BTREE,
  CONSTRAINT `fk_points_expiration_transaction` FOREIGN KEY (`transaction_id`) REFERENCES `points_transaction` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_points_expiration_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 74 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '积分过期记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of points_expiration
-- ----------------------------
INSERT INTO `points_expiration` VALUES (10, 7, 12, 0, '2026-02-07 20:59:28', '2026-02-07', 1, '2026-01-08 20:59:35');
INSERT INTO `points_expiration` VALUES (11, 7, 14, 32, '2026-02-07 21:00:46', '2026-02-07', 1, '2026-01-08 21:00:54');
INSERT INTO `points_expiration` VALUES (12, 7, 15, 73, '2026-02-07 21:01:42', '2026-02-07', 0, '2026-01-08 21:01:42');
INSERT INTO `points_expiration` VALUES (13, 7, 16, 33, '2026-02-07 21:01:56', '2026-02-07', 0, '2026-01-08 21:02:05');
INSERT INTO `points_expiration` VALUES (14, 7, 17, 100, '2026-02-07 21:02:20', '2026-02-07', 0, '2026-01-08 21:02:20');
INSERT INTO `points_expiration` VALUES (15, 7, 18, 33, '2026-02-07 21:04:21', '2026-02-07', 0, '2026-01-08 21:04:34');
INSERT INTO `points_expiration` VALUES (16, 8, 20, 33, '2026-02-07 21:12:20', '2026-02-07', 0, '2026-01-08 21:12:29');
INSERT INTO `points_expiration` VALUES (17, 8, 22, 32, '2026-02-07 21:13:26', '2026-02-07', 1, '2026-01-08 21:13:32');
INSERT INTO `points_expiration` VALUES (18, 8, 23, 200, '2026-02-07 21:14:01', '2026-02-07', 0, '2026-01-08 21:14:01');
INSERT INTO `points_expiration` VALUES (19, 8, 24, 33, '2026-02-07 21:14:18', '2026-02-07', 0, '2026-01-08 21:14:26');
INSERT INTO `points_expiration` VALUES (20, 8, 25, 100, '2026-02-07 21:14:40', '2026-02-07', 0, '2026-01-08 21:14:40');
INSERT INTO `points_expiration` VALUES (21, 8, 26, 20, '2026-02-07 21:15:13', '2026-02-07', 0, '2026-01-08 21:15:13');
INSERT INTO `points_expiration` VALUES (22, 8, 27, 40, '2026-02-07 21:15:14', '2026-02-07', 0, '2026-01-08 21:15:14');
INSERT INTO `points_expiration` VALUES (23, 8, 28, 33, '2026-02-07 21:15:19', '2026-02-07', 0, '2026-01-08 21:15:33');
INSERT INTO `points_expiration` VALUES (24, 8, 29, 233, '2026-01-23 21:16:47', '2026-01-23', 0, '2026-01-08 21:16:48');
INSERT INTO `points_expiration` VALUES (25, 9, 31, 0, '2026-02-08 09:31:55', '2026-02-08', 1, '2026-01-09 09:32:04');
INSERT INTO `points_expiration` VALUES (26, 9, 33, 32, '2026-02-08 09:33:05', '2026-02-08', 1, '2026-01-09 09:33:12');
INSERT INTO `points_expiration` VALUES (27, 9, 34, 123, '2026-02-08 09:33:48', '2026-02-08', 0, '2026-01-09 09:33:48');
INSERT INTO `points_expiration` VALUES (28, 9, 35, 33, '2026-02-08 09:34:00', '2026-02-08', 0, '2026-01-09 09:34:06');
INSERT INTO `points_expiration` VALUES (29, 9, 36, 100, '2026-02-08 09:34:32', '2026-02-08', 0, '2026-01-09 09:34:32');
INSERT INTO `points_expiration` VALUES (30, 9, 37, 33, '2026-02-08 09:35:14', '2026-02-08', 0, '2026-01-09 09:35:28');
INSERT INTO `points_expiration` VALUES (31, 9, 38, 0, '2026-01-24 09:36:52', '2026-01-24', 1, '2026-01-09 09:36:52');
INSERT INTO `points_expiration` VALUES (32, 10, 40, 0, '2026-02-08 09:45:16', '2026-02-08', 1, '2026-01-09 09:45:22');
INSERT INTO `points_expiration` VALUES (33, 10, 42, 32, '2026-02-08 09:46:20', '2026-02-08', 1, '2026-01-09 09:46:27');
INSERT INTO `points_expiration` VALUES (34, 10, 43, 173, '2026-02-08 09:46:55', '2026-02-08', 0, '2026-01-09 09:46:55');
INSERT INTO `points_expiration` VALUES (35, 10, 44, 33, '2026-02-08 09:47:10', '2026-02-08', 0, '2026-01-09 09:47:15');
INSERT INTO `points_expiration` VALUES (36, 10, 45, 100, '2026-02-08 09:47:31', '2026-02-08', 0, '2026-01-09 09:47:31');
INSERT INTO `points_expiration` VALUES (37, 10, 46, 20, '2026-02-08 09:48:03', '2026-02-08', 0, '2026-01-09 09:48:03');
INSERT INTO `points_expiration` VALUES (38, 10, 47, 40, '2026-02-08 09:48:04', '2026-02-08', 0, '2026-01-09 09:48:04');
INSERT INTO `points_expiration` VALUES (39, 10, 48, 33, '2026-02-08 09:48:10', '2026-02-08', 0, '2026-01-09 09:48:23');
INSERT INTO `points_expiration` VALUES (40, 10, 49, 0, '2026-01-24 09:49:38', '2026-01-24', 1, '2026-01-09 09:49:39');
INSERT INTO `points_expiration` VALUES (41, 8, 51, 45, '2026-02-08 15:45:47', '2026-02-08', 0, '2026-01-09 15:46:04');
INSERT INTO `points_expiration` VALUES (42, 8, 52, 33, '2026-02-08 15:47:49', '2026-02-08', 0, '2026-01-09 15:48:09');
INSERT INTO `points_expiration` VALUES (43, 8, 53, 33, '2026-02-08 15:47:49', '2026-02-08', 0, '2026-01-09 15:48:15');
INSERT INTO `points_expiration` VALUES (44, 12, 54, 33, '2026-02-11 09:32:25', '2026-02-11', 0, '2026-01-12 09:32:33');
INSERT INTO `points_expiration` VALUES (45, 12, 55, 33, '2026-02-11 09:43:18', '2026-02-11', 0, '2026-01-12 09:43:27');
INSERT INTO `points_expiration` VALUES (46, 12, 57, 32, '2026-02-11 09:44:29', '2026-02-11', 1, '2026-01-12 09:44:35');
INSERT INTO `points_expiration` VALUES (47, 12, 58, 200, '2026-02-11 09:45:10', '2026-02-11', 0, '2026-01-12 09:45:10');
INSERT INTO `points_expiration` VALUES (48, 12, 59, 33, '2026-02-11 09:45:24', '2026-02-11', 0, '2026-01-12 09:45:33');
INSERT INTO `points_expiration` VALUES (49, 13, 60, 33, '2026-02-11 09:50:00', '2026-02-11', 0, '2026-01-12 09:50:06');
INSERT INTO `points_expiration` VALUES (50, 13, 62, 32, '2026-02-11 09:51:03', '2026-02-11', 1, '2026-01-12 09:51:11');
INSERT INTO `points_expiration` VALUES (51, 14, 63, 33, '2026-02-11 09:55:26', '2026-02-11', 0, '2026-01-12 09:55:34');
INSERT INTO `points_expiration` VALUES (52, 14, 65, 32, '2026-02-11 09:56:29', '2026-02-11', 1, '2026-01-12 09:56:35');
INSERT INTO `points_expiration` VALUES (53, 14, 66, 200, '2026-02-11 09:57:09', '2026-02-11', 0, '2026-01-12 09:57:09');
INSERT INTO `points_expiration` VALUES (54, 14, 67, 33, '2026-02-11 09:57:23', '2026-02-11', 0, '2026-01-12 09:57:30');
INSERT INTO `points_expiration` VALUES (55, 14, 68, 100, '2026-02-11 09:57:46', '2026-02-11', 0, '2026-01-12 09:57:46');
INSERT INTO `points_expiration` VALUES (56, 14, 69, 20, '2026-02-11 09:58:24', '2026-02-11', 0, '2026-01-12 09:58:24');
INSERT INTO `points_expiration` VALUES (57, 14, 70, 40, '2026-02-11 09:58:25', '2026-02-11', 0, '2026-01-12 09:58:25');
INSERT INTO `points_expiration` VALUES (58, 14, 71, 20, '2026-02-11 09:58:27', '2026-02-11', 0, '2026-01-12 09:58:27');
INSERT INTO `points_expiration` VALUES (59, 14, 72, 33, '2026-02-11 09:58:34', '2026-02-11', 0, '2026-01-12 09:58:49');
INSERT INTO `points_expiration` VALUES (61, 15, 74, 33, '2026-02-12 09:38:38', '2026-02-12', 0, '2026-01-13 09:38:47');
INSERT INTO `points_expiration` VALUES (62, 15, 76, 32, '2026-02-12 09:39:51', '2026-02-12', 1, '2026-01-13 09:39:58');
INSERT INTO `points_expiration` VALUES (63, 15, 77, 200, '2026-02-12 09:40:26', '2026-02-12', 0, '2026-01-13 09:40:26');
INSERT INTO `points_expiration` VALUES (64, 15, 78, 33, '2026-02-12 09:40:43', '2026-02-12', 0, '2026-01-13 09:40:52');
INSERT INTO `points_expiration` VALUES (65, 15, 79, 100, '2026-02-12 09:41:10', '2026-02-12', 0, '2026-01-13 09:41:10');
INSERT INTO `points_expiration` VALUES (66, 15, 80, 20, '2026-02-12 09:41:51', '2026-02-12', 0, '2026-01-13 09:41:51');
INSERT INTO `points_expiration` VALUES (67, 15, 81, 40, '2026-02-12 09:41:52', '2026-02-12', 0, '2026-01-13 09:41:52');
INSERT INTO `points_expiration` VALUES (68, 15, 82, 33, '2026-02-12 09:42:00', '2026-02-12', 0, '2026-01-13 09:42:16');
INSERT INTO `points_expiration` VALUES (69, 15, 83, 20, '2026-01-28 09:43:43', '2026-01-28', 0, '2026-01-13 09:43:43');
INSERT INTO `points_expiration` VALUES (70, 16, 85, 10, '2026-02-12 18:27:59', '2026-02-12', 0, '2026-01-13 18:27:59');
INSERT INTO `points_expiration` VALUES (71, 16, 87, 63, '2026-02-12 18:28:10', '2026-02-12', 0, '2026-01-13 18:28:18');
INSERT INTO `points_expiration` VALUES (72, 16, 88, 20, '2026-02-12 18:28:25', '2026-02-12', 0, '2026-01-13 18:28:25');
INSERT INTO `points_expiration` VALUES (73, 16, 89, 37, '2026-02-12 18:28:30', '2026-02-12', 0, '2026-01-13 18:28:36');

-- ----------------------------
-- Table structure for points_expiration_alert_config
-- ----------------------------
DROP TABLE IF EXISTS `points_expiration_alert_config`;
CREATE TABLE `points_expiration_alert_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `alert_days` int NOT NULL COMMENT '提前预警天数（如7表示提前7天预警）',
  `alert_cycle` int NULL DEFAULT NULL COMMENT '预警周期（天数，如每天、每3天）。NULL表示只预警一次',
  `sms_template` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '短信模板（支持变量：{username}, {points}, {expireDate}）',
  `is_enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用 0-禁用 1-启用',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '积分到期预警配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of points_expiration_alert_config
-- ----------------------------
INSERT INTO `points_expiration_alert_config` VALUES (1, 5, 3, '尊敬的{username}先生/女士，您有{points}积分将于{expireDate}过期，请及时使用。', 1, '2026-01-08 04:55:30', '2026-01-13 10:00:31');

-- ----------------------------
-- Table structure for points_expiration_alert_log
-- ----------------------------
DROP TABLE IF EXISTS `points_expiration_alert_log`;
CREATE TABLE `points_expiration_alert_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID（关联users表）',
  `points_amount` bigint NOT NULL COMMENT '即将过期积分数量',
  `expire_date` date NOT NULL COMMENT '过期日期',
  `alert_time` timestamp NOT NULL COMMENT '预警时间',
  `next_alert_time` timestamp NULL DEFAULT NULL COMMENT '下次预警时间（根据预警周期计算）',
  `is_sent` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已发送 0-未发送 1-已发送',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户手机号（发送短信时使用）',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE COMMENT '用户ID索引',
  INDEX `idx_expire_date`(`expire_date` ASC) USING BTREE COMMENT '过期日期索引',
  INDEX `idx_next_alert_time`(`next_alert_time` ASC) USING BTREE COMMENT '下次预警时间索引',
  INDEX `idx_user_expire`(`user_id` ASC, `expire_date` ASC) USING BTREE COMMENT '用户过期日期联合索引',
  CONSTRAINT `fk_points_alert_log_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '积分到期预警记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of points_expiration_alert_log
-- ----------------------------

-- ----------------------------
-- Table structure for points_lottery_record
-- ----------------------------
DROP TABLE IF EXISTS `points_lottery_record`;
CREATE TABLE `points_lottery_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID（关联users表）',
  `member_level` tinyint NOT NULL COMMENT '抽奖时的会员等级 0-普通 1-白银 2-黄金 3-钻石',
  `lottery_type` tinyint NOT NULL COMMENT '抽奖类型 0-没中奖 1-固定积分 2-积分翻倍',
  `points_reward` bigint NULL DEFAULT NULL COMMENT '获得的积分数量（没中奖时为0，积分翻倍时为NULL）',
  `points_multiplier` decimal(10, 2) NULL DEFAULT NULL COMMENT '积分翻倍倍数（仅积分翻倍时使用）',
  `original_points` bigint NULL DEFAULT NULL COMMENT '翻倍前的积分数量（仅积分翻倍时使用）',
  `lottery_month` varchar(7) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '抽奖月份（格式：YYYY-MM，用于统计每月抽奖次数）',
  `transaction_id` bigint NULL DEFAULT NULL COMMENT '关联积分明细ID（关联points_transaction表，中奖时记录）',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `updater` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE COMMENT '用户ID索引',
  INDEX `idx_lottery_month`(`lottery_month` ASC) USING BTREE COMMENT '抽奖月份索引',
  INDEX `idx_user_month`(`user_id` ASC, `lottery_month` ASC) USING BTREE COMMENT '用户月份联合索引（用于统计每月抽奖次数）',
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE COMMENT '创建时间索引',
  INDEX `fk_points_lottery_record_transaction`(`transaction_id` ASC) USING BTREE,
  CONSTRAINT `fk_points_lottery_record_transaction` FOREIGN KEY (`transaction_id`) REFERENCES `points_transaction` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_points_lottery_record_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '积分抽奖记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of points_lottery_record
-- ----------------------------
INSERT INTO `points_lottery_record` VALUES (2, 7, 2, 0, 0, NULL, NULL, '2026-01', NULL, '2026-01-08 21:06:04', '2026-01-08 21:06:04', 7, 7, 0);
INSERT INTO `points_lottery_record` VALUES (3, 8, 2, 2, 393, 2.00, 393, '2026-01', 29, '2026-01-08 21:16:47', '2026-01-08 21:16:48', 8, 8, 0);
INSERT INTO `points_lottery_record` VALUES (4, 9, 2, 1, 50, NULL, NULL, '2026-01', 38, '2026-01-09 09:36:52', '2026-01-09 09:36:52', 9, 9, 0);
INSERT INTO `points_lottery_record` VALUES (5, 10, 2, 1, 100, NULL, NULL, '2026-01', 49, '2026-01-09 09:49:38', '2026-01-09 09:49:39', 10, 10, 0);
INSERT INTO `points_lottery_record` VALUES (6, 15, 2, 1, 100, NULL, NULL, '2026-01', 83, '2026-01-13 09:43:43', '2026-01-13 09:43:43', 15, 15, 0);

-- ----------------------------
-- Table structure for points_lottery_rule
-- ----------------------------
DROP TABLE IF EXISTS `points_lottery_rule`;
CREATE TABLE `points_lottery_rule`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `rule_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '规则名称',
  `member_level` tinyint NOT NULL COMMENT '适用会员等级 1-白银 2-黄金 3-钻石',
  `prize_type` tinyint NOT NULL COMMENT '奖品类型 0-没中奖 1-固定积分 2-积分翻倍',
  `prize_points` bigint NULL DEFAULT NULL COMMENT '固定积分数量（仅prize_type=1时有效）',
  `prize_multiplier` decimal(10, 2) NULL DEFAULT NULL COMMENT '积分翻倍倍数（仅prize_type=2时有效，如2.0表示翻倍）',
  `probability` int NOT NULL COMMENT '中奖概率（百分比，0-100）',
  `prize_order` int NOT NULL DEFAULT 0 COMMENT '奖品排序（数字越小越靠前，用于前端展示）',
  `prize_description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '奖品描述（用于前端展示）',
  `rule_status` tinyint NOT NULL DEFAULT 1 COMMENT '规则状态 0-禁用 1-启用',
  `start_time` timestamp NULL DEFAULT NULL COMMENT '规则生效开始时间',
  `end_time` timestamp NULL DEFAULT NULL COMMENT '规则生效结束时间',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `updater` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_member_level`(`member_level` ASC) USING BTREE COMMENT '会员等级索引',
  INDEX `idx_prize_type`(`prize_type` ASC) USING BTREE COMMENT '奖品类型索引',
  INDEX `idx_rule_status`(`rule_status` ASC) USING BTREE COMMENT '规则状态索引',
  INDEX `idx_member_status`(`member_level` ASC, `rule_status` ASC) USING BTREE COMMENT '会员等级和状态联合索引'
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '积分抽奖规则表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of points_lottery_rule
-- ----------------------------
INSERT INTO `points_lottery_rule` VALUES (1, '白银会员-没中奖', 1, 0, 0, NULL, 50, 1, '感谢参与', 1, NULL, NULL, '2026-01-08 04:55:30', '2026-01-08 04:55:30', 1, 1, 0);
INSERT INTO `points_lottery_rule` VALUES (2, '白银会员-20积分', 1, 1, 20, NULL, 30, 2, '+ 20 积分', 1, NULL, NULL, '2026-01-08 04:55:30', '2026-01-08 04:55:30', 1, 1, 0);
INSERT INTO `points_lottery_rule` VALUES (3, '白银会员-50积分', 1, 1, 50, NULL, 15, 3, '+ 50 积分', 1, NULL, NULL, '2026-01-08 04:55:30', '2026-01-08 04:55:30', 1, 1, 0);
INSERT INTO `points_lottery_rule` VALUES (4, '白银会员-100积分', 1, 1, 100, NULL, 5, 4, '+ 100 积分', 1, NULL, NULL, '2026-01-08 04:55:30', '2026-01-08 04:55:30', 1, 1, 0);
INSERT INTO `points_lottery_rule` VALUES (5, '黄金会员-没中奖', 2, 0, 0, NULL, 40, 1, '感谢参与', 1, NULL, NULL, '2026-01-08 04:55:30', '2026-01-08 04:55:30', 1, 1, 0);
INSERT INTO `points_lottery_rule` VALUES (6, '黄金会员-50积分', 2, 1, 50, NULL, 30, 2, '+ 50 积分', 1, NULL, NULL, '2026-01-08 04:55:30', '2026-01-08 04:55:30', 1, 1, 0);
INSERT INTO `points_lottery_rule` VALUES (7, '黄金会员-100积分', 2, 1, 100, NULL, 20, 3, '+ 100 积分', 1, NULL, NULL, '2026-01-08 04:55:30', '2026-01-08 04:55:30', 1, 1, 0);
INSERT INTO `points_lottery_rule` VALUES (8, '黄金会员-积分翻倍', 2, 2, NULL, 2.00, 10, 4, '积分翻倍', 1, NULL, NULL, '2026-01-08 04:55:30', '2026-01-08 04:55:30', 1, 1, 0);
INSERT INTO `points_lottery_rule` VALUES (9, '钻石会员-没中奖', 3, 0, 0, NULL, 30, 1, '感谢参与', 1, NULL, NULL, '2026-01-08 04:55:30', '2026-01-08 04:55:30', 1, 1, 0);
INSERT INTO `points_lottery_rule` VALUES (10, '钻石会员-100积分', 3, 1, 100, NULL, 30, 2, '+ 100 积分', 1, NULL, NULL, '2026-01-08 04:55:30', '2026-01-08 04:55:30', 1, 1, 0);
INSERT INTO `points_lottery_rule` VALUES (11, '钻石会员-200积分', 3, 1, 200, NULL, 25, 3, '+ 200 积分', 1, NULL, NULL, '2026-01-08 04:55:30', '2026-01-08 04:55:30', 1, 1, 0);
INSERT INTO `points_lottery_rule` VALUES (12, '钻石会员-积分翻倍', 3, 2, NULL, 2.00, 15, 4, '积分翻倍', 1, NULL, NULL, '2026-01-08 04:55:30', '2026-01-08 04:55:30', 1, 1, 0);

-- ----------------------------
-- Table structure for points_transaction
-- ----------------------------
DROP TABLE IF EXISTS `points_transaction`;
CREATE TABLE `points_transaction`  (
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
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE COMMENT '用户ID索引',
  INDEX `idx_account_id`(`account_id` ASC) USING BTREE COMMENT '账户ID索引',
  INDEX `idx_transaction_type`(`transaction_type` ASC) USING BTREE COMMENT '交易类型索引',
  INDEX `idx_expire_time`(`expire_time` ASC) USING BTREE COMMENT '过期时间索引',
  INDEX `idx_related_order_id`(`related_order_id` ASC) USING BTREE COMMENT '订单ID索引',
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE COMMENT '创建时间索引',
  CONSTRAINT `fk_points_transaction_account` FOREIGN KEY (`account_id`) REFERENCES `points_account` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_points_transaction_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 90 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '积分明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of points_transaction
-- ----------------------------
INSERT INTO `points_transaction` VALUES (12, 7, 2, 0, 0, 33, 0, '2026-02-07 20:59:28', 7, NULL, NULL, '订单消费获得积分（冻结中，订单完成后解冻）', '2026-01-08 20:59:35', NULL, '2026-01-08 20:59:35', NULL, 0);
INSERT INTO `points_transaction` VALUES (13, 7, 2, 4, 0, 33, 33, NULL, 7, NULL, NULL, '订单完成解冻奖励积分', '2026-01-08 21:00:13', 7, '2026-01-08 21:00:13', 7, 0);
INSERT INTO `points_transaction` VALUES (14, 7, 2, 0, 0, 32, 33, '2026-02-07 21:00:46', 8, NULL, NULL, '订单消费获得积分（冻结中，订单完成后解冻）', '2026-01-08 21:00:54', NULL, '2026-01-08 21:00:58', 7, 1);
INSERT INTO `points_transaction` VALUES (15, 7, 2, 0, 2, 200, 233, '2026-02-07 21:01:42', NULL, NULL, 3, '升级为黄金会员获得积分', '2026-01-08 21:01:42', 7, '2026-01-08 21:01:42', 7, 0);
INSERT INTO `points_transaction` VALUES (16, 7, 2, 0, 0, 33, 233, '2026-02-07 21:01:56', 9, NULL, NULL, '订单消费获得积分（冻结中，订单完成后解冻）', '2026-01-08 21:02:05', NULL, '2026-01-08 21:02:05', NULL, 0);
INSERT INTO `points_transaction` VALUES (17, 7, 2, 0, 3, 100, 333, '2026-02-07 21:02:20', NULL, NULL, 15, '还贷款获得积分', '2026-01-08 21:02:20', 7, '2026-01-08 21:02:20', 7, 0);
INSERT INTO `points_transaction` VALUES (18, 7, 2, 0, 0, 33, 333, '2026-02-07 21:04:21', 10, NULL, NULL, '订单消费获得积分（冻结中，订单完成后解冻）', '2026-01-08 21:04:34', NULL, '2026-01-08 21:04:34', NULL, 0);
INSERT INTO `points_transaction` VALUES (19, 7, 2, 1, 4, -160, 173, NULL, 11, 5, NULL, '兑换商品：杂酱面 x2', '2026-01-08 21:07:12', 7, '2026-01-08 21:07:12', 7, 0);
INSERT INTO `points_transaction` VALUES (20, 8, 3, 0, 0, 33, 0, '2026-02-07 21:12:20', 12, NULL, NULL, '订单消费获得积分（冻结中，订单完成后解冻）', '2026-01-08 21:12:29', NULL, '2026-01-08 21:12:29', NULL, 0);
INSERT INTO `points_transaction` VALUES (21, 8, 3, 4, 0, 33, 33, NULL, 12, NULL, NULL, '订单完成解冻奖励积分', '2026-01-08 21:13:02', 8, '2026-01-08 21:13:02', 8, 0);
INSERT INTO `points_transaction` VALUES (22, 8, 3, 0, 0, 32, 33, '2026-02-07 21:13:26', 13, NULL, NULL, '订单消费获得积分（冻结中，订单完成后解冻）', '2026-01-08 21:13:32', NULL, '2026-01-08 21:13:37', 8, 1);
INSERT INTO `points_transaction` VALUES (23, 8, 3, 0, 2, 200, 233, '2026-02-07 21:14:01', NULL, NULL, 3, '升级为黄金会员获得积分', '2026-01-08 21:14:01', 8, '2026-01-08 21:14:01', 8, 0);
INSERT INTO `points_transaction` VALUES (24, 8, 3, 0, 0, 33, 233, '2026-02-07 21:14:18', 14, NULL, NULL, '订单消费获得积分（冻结中，订单完成后解冻）', '2026-01-08 21:14:26', NULL, '2026-01-08 21:14:26', NULL, 0);
INSERT INTO `points_transaction` VALUES (25, 8, 3, 0, 3, 100, 333, '2026-02-07 21:14:40', NULL, NULL, 15, '还贷款获得积分', '2026-01-08 21:14:40', 8, '2026-01-08 21:14:40', 8, 0);
INSERT INTO `points_transaction` VALUES (26, 8, 3, 0, 3, 20, 353, '2026-02-07 21:15:13', NULL, NULL, 7, '点赞商家获得积分', '2026-01-08 21:15:13', 8, '2026-01-08 21:15:13', 8, 0);
INSERT INTO `points_transaction` VALUES (27, 8, 3, 0, 3, 40, 393, '2026-02-07 21:15:14', NULL, NULL, 11, '收藏商家获得积分', '2026-01-08 21:15:14', 8, '2026-01-08 21:15:14', 8, 0);
INSERT INTO `points_transaction` VALUES (28, 8, 3, 0, 0, 33, 393, '2026-02-07 21:15:19', 15, NULL, NULL, '订单消费获得积分（冻结中，订单完成后解冻）', '2026-01-08 21:15:33', NULL, '2026-01-08 21:15:33', NULL, 0);
INSERT INTO `points_transaction` VALUES (29, 8, 3, 0, 3, 393, 786, '2026-01-23 21:16:47', NULL, NULL, NULL, '恭喜中奖！积分翻倍，获得 393 积分', '2026-01-08 21:16:48', 8, '2026-01-08 21:16:48', 8, 0);
INSERT INTO `points_transaction` VALUES (30, 8, 3, 1, 4, -160, 626, NULL, 16, 5, NULL, '兑换商品：杂酱面 x2', '2026-01-08 21:18:01', 8, '2026-01-08 21:18:01', 8, 0);
INSERT INTO `points_transaction` VALUES (31, 9, 4, 0, 0, 33, 0, '2026-02-08 09:31:55', 17, NULL, NULL, '订单消费获得积分（冻结中，订单完成后解冻）', '2026-01-09 09:32:04', NULL, '2026-01-09 09:32:04', NULL, 0);
INSERT INTO `points_transaction` VALUES (32, 9, 4, 4, 0, 33, 33, NULL, 17, NULL, NULL, '订单完成解冻奖励积分', '2026-01-09 09:32:37', 9, '2026-01-09 09:32:37', 9, 0);
INSERT INTO `points_transaction` VALUES (33, 9, 4, 0, 0, 32, 33, '2026-02-08 09:33:05', 18, NULL, NULL, '订单消费获得积分（冻结中，订单完成后解冻）', '2026-01-09 09:33:12', NULL, '2026-01-09 09:33:17', 9, 1);
INSERT INTO `points_transaction` VALUES (34, 9, 4, 0, 2, 200, 233, '2026-02-08 09:33:48', NULL, NULL, 3, '升级为黄金会员获得积分', '2026-01-09 09:33:48', 9, '2026-01-09 09:33:48', 9, 0);
INSERT INTO `points_transaction` VALUES (35, 9, 4, 0, 0, 33, 233, '2026-02-08 09:34:00', 19, NULL, NULL, '订单消费获得积分（冻结中，订单完成后解冻）', '2026-01-09 09:34:06', NULL, '2026-01-09 09:34:06', NULL, 0);
INSERT INTO `points_transaction` VALUES (36, 9, 4, 0, 3, 100, 333, '2026-02-08 09:34:32', NULL, NULL, 15, '还贷款获得积分', '2026-01-09 09:34:32', 9, '2026-01-09 09:34:32', 9, 0);
INSERT INTO `points_transaction` VALUES (37, 9, 4, 0, 0, 33, 333, '2026-02-08 09:35:14', 20, NULL, NULL, '订单消费获得积分（冻结中，订单完成后解冻）', '2026-01-09 09:35:28', NULL, '2026-01-09 09:35:28', NULL, 0);
INSERT INTO `points_transaction` VALUES (38, 9, 4, 0, 3, 50, 383, '2026-01-24 09:36:52', NULL, NULL, NULL, '恭喜中奖！获得 50 积分', '2026-01-09 09:36:52', 9, '2026-01-09 09:36:52', 9, 0);
INSERT INTO `points_transaction` VALUES (39, 9, 4, 1, 4, -160, 223, NULL, 21, 5, NULL, '兑换商品：杂酱面 x2', '2026-01-09 09:37:48', 9, '2026-01-09 09:37:48', 9, 0);
INSERT INTO `points_transaction` VALUES (40, 10, 5, 0, 0, 33, 0, '2026-02-08 09:45:16', 22, NULL, NULL, '订单消费获得积分（冻结中，订单完成后解冻）', '2026-01-09 09:45:22', NULL, '2026-01-09 09:45:22', NULL, 0);
INSERT INTO `points_transaction` VALUES (41, 10, 5, 4, 0, 33, 33, NULL, 22, NULL, NULL, '订单完成解冻奖励积分', '2026-01-09 09:45:53', 10, '2026-01-09 09:45:53', 10, 0);
INSERT INTO `points_transaction` VALUES (42, 10, 5, 0, 0, 32, 33, '2026-02-08 09:46:20', 23, NULL, NULL, '订单消费获得积分（冻结中，订单完成后解冻）', '2026-01-09 09:46:27', NULL, '2026-01-09 09:46:31', 10, 1);
INSERT INTO `points_transaction` VALUES (43, 10, 5, 0, 2, 200, 233, '2026-02-08 09:46:55', NULL, NULL, 3, '升级为黄金会员获得积分', '2026-01-09 09:46:55', 10, '2026-01-09 09:46:55', 10, 0);
INSERT INTO `points_transaction` VALUES (44, 10, 5, 0, 0, 33, 233, '2026-02-08 09:47:10', 24, NULL, NULL, '订单消费获得积分（冻结中，订单完成后解冻）', '2026-01-09 09:47:15', NULL, '2026-01-09 09:47:15', NULL, 0);
INSERT INTO `points_transaction` VALUES (45, 10, 5, 0, 3, 100, 333, '2026-02-08 09:47:31', NULL, NULL, 15, '还贷款获得积分', '2026-01-09 09:47:31', 10, '2026-01-09 09:47:31', 10, 0);
INSERT INTO `points_transaction` VALUES (46, 10, 5, 0, 3, 20, 353, '2026-02-08 09:48:03', NULL, NULL, 7, '点赞商家获得积分', '2026-01-09 09:48:03', 10, '2026-01-09 09:48:03', 10, 0);
INSERT INTO `points_transaction` VALUES (47, 10, 5, 0, 3, 40, 393, '2026-02-08 09:48:04', NULL, NULL, 11, '收藏商家获得积分', '2026-01-09 09:48:04', 10, '2026-01-09 09:48:04', 10, 0);
INSERT INTO `points_transaction` VALUES (48, 10, 5, 0, 0, 33, 393, '2026-02-08 09:48:10', 25, NULL, NULL, '订单消费获得积分（冻结中，订单完成后解冻）', '2026-01-09 09:48:23', NULL, '2026-01-09 09:48:23', NULL, 0);
INSERT INTO `points_transaction` VALUES (49, 10, 5, 0, 3, 100, 493, '2026-01-24 09:49:38', NULL, NULL, NULL, '恭喜中奖！获得 100 积分', '2026-01-09 09:49:38', 10, '2026-01-09 09:49:38', 10, 0);
INSERT INTO `points_transaction` VALUES (50, 10, 5, 1, 4, -160, 333, NULL, 26, 5, NULL, '兑换商品：杂酱面 x2', '2026-01-09 09:50:32', 10, '2026-01-09 09:50:32', 10, 0);
INSERT INTO `points_transaction` VALUES (51, 8, 3, 0, 0, 45, 626, '2026-02-08 15:45:47', 21, NULL, NULL, '订单消费获得积分（冻结中，订单完成后解冻）', '2026-01-09 15:46:04', NULL, '2026-01-09 15:46:04', NULL, 0);
INSERT INTO `points_transaction` VALUES (52, 8, 3, 0, 0, 33, 626, '2026-02-08 15:47:49', 22, NULL, NULL, '订单消费获得积分（冻结中，订单完成后解冻）', '2026-01-09 15:48:09', NULL, '2026-01-09 15:48:09', NULL, 0);
INSERT INTO `points_transaction` VALUES (53, 8, 3, 0, 0, 33, 626, '2026-02-08 15:47:49', 22, NULL, NULL, '订单消费获得积分（冻结中，订单完成后解冻）', '2026-01-09 15:48:15', NULL, '2026-01-09 15:48:15', NULL, 0);
INSERT INTO `points_transaction` VALUES (54, 12, 7, 0, 0, 33, 0, '2026-02-11 09:32:25', 27, NULL, NULL, '订单消费获得积分（冻结中，订单完成后解冻）', '2026-01-12 09:32:33', NULL, '2026-01-12 09:32:33', NULL, 0);
INSERT INTO `points_transaction` VALUES (55, 12, 7, 0, 0, 33, 0, '2026-02-11 09:43:18', 29, NULL, NULL, '订单消费获得积分（冻结中，订单完成后解冻）', '2026-01-12 09:43:27', NULL, '2026-01-12 09:43:27', NULL, 0);
INSERT INTO `points_transaction` VALUES (56, 12, 7, 4, 0, 33, 33, NULL, 29, NULL, NULL, '订单完成解冻奖励积分', '2026-01-12 09:43:58', 12, '2026-01-12 09:43:58', 12, 0);
INSERT INTO `points_transaction` VALUES (57, 12, 7, 0, 0, 32, 33, '2026-02-11 09:44:29', 30, NULL, NULL, '订单消费获得积分（冻结中，订单完成后解冻）', '2026-01-12 09:44:35', NULL, '2026-01-12 09:44:41', 12, 1);
INSERT INTO `points_transaction` VALUES (58, 12, 7, 0, 2, 200, 233, '2026-02-11 09:45:10', NULL, NULL, 3, '升级为黄金会员获得积分', '2026-01-12 09:45:10', 12, '2026-01-12 09:45:10', 12, 0);
INSERT INTO `points_transaction` VALUES (59, 12, 7, 0, 0, 33, 233, '2026-02-11 09:45:24', 31, NULL, NULL, '订单消费获得积分（冻结中，订单完成后解冻）', '2026-01-12 09:45:33', NULL, '2026-01-12 09:45:33', NULL, 0);
INSERT INTO `points_transaction` VALUES (60, 13, 8, 0, 0, 33, 0, '2026-02-11 09:50:00', 32, NULL, NULL, '订单消费获得积分（冻结中，订单完成后解冻）', '2026-01-12 09:50:06', NULL, '2026-01-12 09:50:06', NULL, 0);
INSERT INTO `points_transaction` VALUES (61, 13, 8, 4, 0, 33, 33, NULL, 32, NULL, NULL, '订单完成解冻奖励积分', '2026-01-12 09:50:36', 13, '2026-01-12 09:50:36', 13, 0);
INSERT INTO `points_transaction` VALUES (62, 13, 8, 0, 0, 32, 33, '2026-02-11 09:51:03', 33, NULL, NULL, '订单消费获得积分（冻结中，订单完成后解冻）', '2026-01-12 09:51:11', NULL, '2026-01-12 09:51:15', 13, 1);
INSERT INTO `points_transaction` VALUES (63, 14, 9, 0, 0, 33, 0, '2026-02-11 09:55:26', 34, NULL, NULL, '订单消费获得积分（冻结中，订单完成后解冻）', '2026-01-12 09:55:34', NULL, '2026-01-12 09:55:34', NULL, 0);
INSERT INTO `points_transaction` VALUES (64, 14, 9, 4, 0, 33, 33, NULL, 34, NULL, NULL, '订单完成解冻奖励积分', '2026-01-12 09:56:02', 14, '2026-01-12 09:56:02', 14, 0);
INSERT INTO `points_transaction` VALUES (65, 14, 9, 0, 0, 32, 33, '2026-02-11 09:56:29', 35, NULL, NULL, '订单消费获得积分（冻结中，订单完成后解冻）', '2026-01-12 09:56:35', NULL, '2026-01-12 09:56:40', 14, 1);
INSERT INTO `points_transaction` VALUES (66, 14, 9, 0, 2, 200, 233, '2026-02-11 09:57:09', NULL, NULL, 3, '升级为黄金会员获得积分', '2026-01-12 09:57:09', 14, '2026-01-12 09:57:09', 14, 0);
INSERT INTO `points_transaction` VALUES (67, 14, 9, 0, 0, 33, 233, '2026-02-11 09:57:23', 36, NULL, NULL, '订单消费获得积分（冻结中，订单完成后解冻）', '2026-01-12 09:57:30', NULL, '2026-01-12 09:57:30', NULL, 0);
INSERT INTO `points_transaction` VALUES (68, 14, 9, 0, 3, 100, 333, '2026-02-11 09:57:46', NULL, NULL, 15, '还贷款获得积分', '2026-01-12 09:57:46', 14, '2026-01-12 09:57:46', 14, 0);
INSERT INTO `points_transaction` VALUES (69, 14, 9, 0, 3, 20, 353, '2026-02-11 09:58:24', NULL, NULL, 7, '点赞商家获得积分', '2026-01-12 09:58:24', 14, '2026-01-12 09:58:24', 14, 0);
INSERT INTO `points_transaction` VALUES (70, 14, 9, 0, 3, 40, 393, '2026-02-11 09:58:25', NULL, NULL, 11, '收藏商家获得积分', '2026-01-12 09:58:25', 14, '2026-01-12 09:58:25', 14, 0);
INSERT INTO `points_transaction` VALUES (71, 14, 9, 0, 3, 20, 413, '2026-02-11 09:58:27', NULL, NULL, 7, '点赞商家获得积分', '2026-01-12 09:58:27', 14, '2026-01-12 09:58:27', 14, 0);
INSERT INTO `points_transaction` VALUES (72, 14, 9, 0, 0, 33, 413, '2026-02-11 09:58:34', 37, NULL, NULL, '订单消费获得积分（冻结中，订单完成后解冻）', '2026-01-12 09:58:49', NULL, '2026-01-12 09:58:49', NULL, 0);
INSERT INTO `points_transaction` VALUES (74, 15, 11, 0, 0, 33, 0, '2026-02-12 09:38:38', 39, NULL, NULL, '订单消费获得积分（冻结中，订单完成后解冻）', '2026-01-13 09:38:47', NULL, '2026-01-13 09:38:47', NULL, 0);
INSERT INTO `points_transaction` VALUES (75, 15, 11, 4, 0, 33, 33, NULL, 39, NULL, NULL, '订单完成解冻奖励积分', '2026-01-13 09:39:20', 15, '2026-01-13 09:39:20', 15, 0);
INSERT INTO `points_transaction` VALUES (76, 15, 11, 0, 0, 32, 33, '2026-02-12 09:39:51', 40, NULL, NULL, '订单消费获得积分（冻结中，订单完成后解冻）', '2026-01-13 09:39:58', NULL, '2026-01-13 09:40:03', 15, 1);
INSERT INTO `points_transaction` VALUES (77, 15, 11, 0, 2, 200, 233, '2026-02-12 09:40:26', NULL, NULL, 3, '升级为黄金会员获得积分', '2026-01-13 09:40:26', 15, '2026-01-13 09:40:26', 15, 0);
INSERT INTO `points_transaction` VALUES (78, 15, 11, 0, 0, 33, 233, '2026-02-12 09:40:43', 41, NULL, NULL, '订单消费获得积分（冻结中，订单完成后解冻）', '2026-01-13 09:40:52', NULL, '2026-01-13 09:40:52', NULL, 0);
INSERT INTO `points_transaction` VALUES (79, 15, 11, 0, 3, 100, 333, '2026-02-12 09:41:10', NULL, NULL, 15, '还贷款获得积分', '2026-01-13 09:41:10', 15, '2026-01-13 09:41:10', 15, 0);
INSERT INTO `points_transaction` VALUES (80, 15, 11, 0, 3, 20, 353, '2026-02-12 09:41:51', NULL, NULL, 7, '点赞商家获得积分', '2026-01-13 09:41:51', 15, '2026-01-13 09:41:51', 15, 0);
INSERT INTO `points_transaction` VALUES (81, 15, 11, 0, 3, 40, 393, '2026-02-12 09:41:52', NULL, NULL, 11, '收藏商家获得积分', '2026-01-13 09:41:52', 15, '2026-01-13 09:41:52', 15, 0);
INSERT INTO `points_transaction` VALUES (82, 15, 11, 0, 0, 33, 393, '2026-02-12 09:42:00', 42, NULL, NULL, '订单消费获得积分（冻结中，订单完成后解冻）', '2026-01-13 09:42:16', NULL, '2026-01-13 09:42:16', NULL, 0);
INSERT INTO `points_transaction` VALUES (83, 15, 11, 0, 3, 100, 493, '2026-01-28 09:43:43', NULL, NULL, NULL, '恭喜中奖！获得 100 积分', '2026-01-13 09:43:43', 15, '2026-01-13 09:43:43', 15, 0);
INSERT INTO `points_transaction` VALUES (84, 15, 11, 1, 4, -80, 413, NULL, 43, 5, NULL, '兑换商品：杂酱面 x1', '2026-01-13 09:45:02', 15, '2026-01-13 09:45:02', 15, 0);
INSERT INTO `points_transaction` VALUES (85, 16, 12, 0, 3, 10, 10, '2026-02-12 18:27:59', NULL, NULL, 5, '点赞商家获得积分', '2026-01-13 18:27:59', 16, '2026-01-13 18:27:59', 16, 0);
INSERT INTO `points_transaction` VALUES (86, 16, 12, 3, 5, -10, 0, NULL, 44, NULL, NULL, '订单支付冻结积分', '2026-01-13 18:28:18', 16, '2026-01-13 18:28:18', 16, 0);
INSERT INTO `points_transaction` VALUES (87, 16, 12, 0, 0, 63, 0, '2026-02-12 18:28:10', 44, NULL, NULL, '订单消费获得积分（冻结中，订单完成后解冻）', '2026-01-13 18:28:18', NULL, '2026-01-13 18:28:18', NULL, 0);
INSERT INTO `points_transaction` VALUES (88, 16, 12, 0, 3, 20, 20, '2026-02-12 18:28:25', NULL, NULL, 9, '收藏商家获得积分', '2026-01-13 18:28:25', 16, '2026-01-13 18:28:25', 16, 0);
INSERT INTO `points_transaction` VALUES (89, 16, 12, 0, 0, 37, 20, '2026-02-12 18:28:30', 45, NULL, NULL, '订单消费获得积分（冻结中，订单完成后解冻）', '2026-01-13 18:28:36', NULL, '2026-01-13 18:28:36', NULL, 0);

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

-- ----------------------------
-- Records of system_config
-- ----------------------------

-- ----------------------------
-- Table structure for user_authority
-- ----------------------------
DROP TABLE IF EXISTS `user_authority`;
CREATE TABLE `user_authority`  (
  `user_id` bigint NOT NULL,
  `authority_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`user_id`, `authority_name`) USING BTREE,
  INDEX `authority_name`(`authority_name` ASC) USING BTREE,
  CONSTRAINT `user_authority_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `user_authority_ibfk_2` FOREIGN KEY (`authority_name`) REFERENCES `authority` (`name`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_authority
-- ----------------------------
INSERT INTO `user_authority` VALUES (3, 'ADMIN');
INSERT INTO `user_authority` VALUES (18, 'ADMIN');
INSERT INTO `user_authority` VALUES (2, 'BUSINESS');
INSERT INTO `user_authority` VALUES (4, 'BUSINESS');
INSERT INTO `user_authority` VALUES (17, 'BUSINESS');
INSERT INTO `user_authority` VALUES (2, 'USER');
INSERT INTO `user_authority` VALUES (4, 'USER');
INSERT INTO `user_authority` VALUES (5, 'USER');
INSERT INTO `user_authority` VALUES (6, 'USER');
INSERT INTO `user_authority` VALUES (7, 'USER');
INSERT INTO `user_authority` VALUES (8, 'USER');
INSERT INTO `user_authority` VALUES (9, 'USER');
INSERT INTO `user_authority` VALUES (10, 'USER');
INSERT INTO `user_authority` VALUES (11, 'USER');
INSERT INTO `user_authority` VALUES (12, 'USER');
INSERT INTO `user_authority` VALUES (13, 'USER');
INSERT INTO `user_authority` VALUES (14, 'USER');
INSERT INTO `user_authority` VALUES (15, 'USER');
INSERT INTO `user_authority` VALUES (16, 'USER');
INSERT INTO `user_authority` VALUES (17, 'USER');
INSERT INTO `user_authority` VALUES (18, 'USER');

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
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (2, '2025-09-25 16:17:50', NULL, 0, '2025-09-25 19:06:53', 2, 1, '$2a$10$HL7jh92Duv3mAoklfgS3iOglu1CGblLKqaC.SQL43qtNNKivGfy96', 'sunny');
INSERT INTO `users` VALUES (3, '2025-09-25 16:18:35', NULL, 0, '2025-09-25 16:18:35', NULL, 1, '$2a$10$.0tYR0Ek3Ns36Ggc1PW4YuNNRBTmZQQJ2aw7fBWy7xJVNMoGfyiv.', 'sun');
INSERT INTO `users` VALUES (4, '2025-09-25 16:19:23', NULL, 0, '2025-09-25 16:19:23', NULL, 1, '$2a$10$yr7GoYV3rRGPPwrvqyNaCuBy9VDfoU3qaOYkyYHG.9oqoeFZ70BEC', 'sunshine');
INSERT INTO `users` VALUES (5, '2025-09-25 16:20:16', NULL, 0, '2025-09-25 21:34:23', 5, 1, '$2a$10$7/d4r0bO7OSE0JRY1iKOtuiO1ca/GyFXPq2HnFaXKKrJhN6h4MHZ6', 'rain');
INSERT INTO `users` VALUES (6, '2025-09-25 16:22:47', NULL, 0, '2025-09-25 16:22:47', NULL, 1, '$2a$10$TdoFHty.VsIqCJOxn5hohOrwXcMhtZ0rc8w24LYSqH43q9tBjqUa.', 'rainy');
INSERT INTO `users` VALUES (7, '2026-01-08 19:46:13', NULL, 0, '2026-01-08 19:46:13', NULL, 1, '$2a$10$FoIUbw/nQ0/2c51TXXbJsu1Mq58LG5tgrll2guKPi0KP31CdF1X/C', '百行');
INSERT INTO `users` VALUES (8, '2026-01-08 21:10:44', NULL, 0, '2026-01-08 21:10:44', NULL, 1, '$2a$10$KlFPD4CE3Lh4t5LeFiYYF.EHq3PXC1vECNfKd/8cJ.QLGjD72vqKu', '万行');
INSERT INTO `users` VALUES (9, '2026-01-09 09:22:00', NULL, 0, '2026-01-09 09:22:00', NULL, 1, '$2a$10$4F61xt4ZKVaBoU3eUmG9duJEYfLOcz2SMfG7o80WghjS99QXIXOES', '千行');
INSERT INTO `users` VALUES (10, '2026-01-09 09:43:48', NULL, 0, '2026-01-09 09:43:48', NULL, 1, '$2a$10$eTV2fZm.bhSxj07nP6CTc.OvBhSPRz2XW5ezikhqTlofe.WxsbqEm', '亿行');
INSERT INTO `users` VALUES (11, '2026-01-09 18:20:19', NULL, 0, '2026-01-09 18:20:19', NULL, 1, '$2a$10$Jl0GwTbyOCi/9tflgLbmyO/Cb0z7.84LSIutEKe.EH5mnAoX9x/62', 'Bobchasm');
INSERT INTO `users` VALUES (12, '2026-01-12 09:28:52', NULL, 0, '2026-01-12 09:28:52', NULL, 1, '$2a$10$qk2SqKZBeWfZmMTEIQmtseiiOpD./v0W0XgmqscewG7A4abYn3reO', '行行');
INSERT INTO `users` VALUES (13, '2026-01-12 09:47:56', NULL, 0, '2026-01-12 09:47:56', NULL, 1, '$2a$10$Lfwc/iYO8.hlhrEbfiLZzOXBK.21KTMkpqUyX7t/JiOdtuFrPkVmC', '小明');
INSERT INTO `users` VALUES (14, '2026-01-12 09:54:02', NULL, 0, '2026-01-12 09:54:02', NULL, 1, '$2a$10$X1f95BHdZbnzaf6DkmDYG.2aiRLTcp9BYl9NCtBtBDg59YO8S7K1O', '小李');
INSERT INTO `users` VALUES (15, '2026-01-12 10:25:05', NULL, 0, '2026-01-12 10:25:05', NULL, 1, '$2a$10$4tfDuatM94U5KUu1FH6QpuSbxd9TmMvUAsI7NcA5ifg2BpFMLCwxW', '小刘');
INSERT INTO `users` VALUES (16, '2026-01-13 18:23:27', NULL, 0, '2026-01-13 18:23:27', NULL, 1, '$2a$10$6V8.yDf3I3sjNzRiyJcqqe2TBQW92VAyuRLGSaB/zipT8QlZIJODK', 'common_user');
INSERT INTO `users` VALUES (17, '2026-01-13 18:24:06', NULL, 0, '2026-01-13 18:24:06', NULL, 1, '$2a$10$ictC3kkVRSOdsuiZUrlri.Y2spQds1zKKPjHJZn25qZPVQsoz7Q/O', 'business_user');
INSERT INTO `users` VALUES (18, '2026-01-13 18:25:11', NULL, 0, '2026-01-13 18:25:11', NULL, 1, '$2a$10$6LCq5wKmdbKmdFzpbKq7L.C8mcC/vhgyesC5a3wFfr4JcYmqqN/H.', 'admin_user');

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
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of virtual_wallet
-- ----------------------------
INSERT INTO `virtual_wallet` VALUES (3, 7, '2026-01-08 12:58:04', '2026-01-08 13:02:19', 0, 2, 0, 0.00, 10000.00, 0.00);
INSERT INTO `virtual_wallet` VALUES (4, 8, '2026-01-08 13:11:38', '2026-01-08 13:14:39', 0, 2, 0, 0.00, 10000.00, 0.00);
INSERT INTO `virtual_wallet` VALUES (5, 9, '2026-01-09 01:31:10', '2026-01-09 01:34:31', 0, 2, 0, 0.00, 10000.00, 0.00);
INSERT INTO `virtual_wallet` VALUES (6, 10, '2026-01-09 01:44:30', '2026-01-09 01:47:28', 0, 2, 0, 0.00, 10000.00, 0.00);
INSERT INTO `virtual_wallet` VALUES (7, 11, '2026-01-09 10:20:34', '2026-01-09 10:30:26', 0, 0, 0, 101.00, 0.00, 0.00);
INSERT INTO `virtual_wallet` VALUES (16, 14, '2026-01-12 01:54:47', '2026-01-12 01:57:45', 0, 2, 0, 0.00, 10000.00, 0.00);
INSERT INTO `virtual_wallet` VALUES (19, 2, '2026-01-13 01:34:10', '2026-01-13 01:39:19', 0, 0, 0, 33.00, 0.00, 0.00);
INSERT INTO `virtual_wallet` VALUES (21, 15, '2026-01-13 01:37:51', '2026-01-13 01:41:10', 0, 2, 0, 0.00, 10000.00, 0.00);
INSERT INTO `virtual_wallet` VALUES (22, 16, '2026-01-13 10:27:40', '2026-01-13 10:27:48', 0, 0, 0, 124.23, 0.00, 0.00);
INSERT INTO `virtual_wallet` VALUES (23, 17, '2026-01-13 10:31:49', '2026-01-13 10:32:03', 0, 0, 0, 12.12, 0.00, 0.00);

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
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of virtual_wallet_loan
-- ----------------------------
INSERT INTO `virtual_wallet_loan` VALUES (20, 14, 0.00, '2026-01-12 09:50:07', NULL, 0.02);
INSERT INTO `virtual_wallet_loan` VALUES (21, 14, 0.00, '2026-01-12 09:51:11', NULL, 0.02);
INSERT INTO `virtual_wallet_loan` VALUES (22, 16, 0.00, '2026-01-12 09:55:34', NULL, 0.02);
INSERT INTO `virtual_wallet_loan` VALUES (23, 16, 0.00, '2026-01-12 09:56:35', NULL, 0.02);
INSERT INTO `virtual_wallet_loan` VALUES (24, 16, 15.00, '2026-01-12 09:57:31', '2026-01-12 09:57:46', 0.02);
INSERT INTO `virtual_wallet_loan` VALUES (25, 18, 0.00, '2026-01-13 09:30:49', NULL, 0.02);
INSERT INTO `virtual_wallet_loan` VALUES (26, 21, 0.00, '2026-01-13 09:38:47', NULL, 0.02);
INSERT INTO `virtual_wallet_loan` VALUES (27, 21, 0.00, '2026-01-13 09:39:58', NULL, 0.02);
INSERT INTO `virtual_wallet_loan` VALUES (28, 21, 15.00, '2026-01-13 09:40:52', '2026-01-13 09:41:10', 0.02);

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
) ENGINE = InnoDB AUTO_INCREMENT = 68 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of virtual_wallet_transaction
-- ----------------------------
INSERT INTO `virtual_wallet_transaction` VALUES (60, 3, 0, 101.00, 0, 21, 0, 1.00, 0.01, '2026-01-13 09:38:01', '2026-01-13 01:38:01', NULL);
INSERT INTO `virtual_wallet_transaction` VALUES (61, 2, 0, 52.50, 21, 0, 0, 2.50, 0.05, '2026-01-13 09:38:12', '2026-01-13 01:38:12', NULL);
INSERT INTO `virtual_wallet_transaction` VALUES (62, 0, 0, 33.00, 21, 19, 0, 0.00, NULL, '2026-01-13 09:38:47', '2026-01-13 01:39:19', 39);
INSERT INTO `virtual_wallet_transaction` VALUES (63, 0, 3, 16.00, 21, 19, 0, 0.00, NULL, '2026-01-13 09:39:58', '2026-01-13 01:40:03', 40);
INSERT INTO `virtual_wallet_transaction` VALUES (64, 4, 0, 16.00, 19, 21, 0, 0.00, NULL, '2026-01-13 09:40:03', '2026-01-13 01:40:03', 40);
INSERT INTO `virtual_wallet_transaction` VALUES (65, 0, 1, 33.00, 21, 19, 0, 0.00, NULL, '2026-01-13 09:40:52', '2026-01-13 01:40:52', 41);
INSERT INTO `virtual_wallet_transaction` VALUES (66, 3, 0, 124.23, 0, 22, 0, 1.23, 0.01, '2026-01-13 18:27:48', '2026-01-13 10:27:48', NULL);
INSERT INTO `virtual_wallet_transaction` VALUES (67, 3, 0, 12.12, 0, 23, 0, 0.12, 0.01, '2026-01-13 18:32:03', '2026-01-13 10:32:03', NULL);

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
