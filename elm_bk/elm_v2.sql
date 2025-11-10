/*
 Navicat Premium Dump SQL

 Source Server         : tx_cloud1
 Source Server Type    : MySQL
 Source Server Version : 90400 (9.4.0)
 Source Host           : REDACTED_IP:3306
 Source Schema         : elm_v2

 Target Server Type    : MySQL
 Target Server Version : 90400 (9.4.0)
 File Encoding         : 65001

 Date: 10/11/2025 23:25:51
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
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'AI对话历史表' ROW_FORMAT = DYNAMIC;

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
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of business
-- ----------------------------
INSERT INTO `business` VALUES (1, '2025-09-25 16:29:40', 2, 0, '2025-09-25 20:24:12', 3, '四川省成都市玉林路46号', '禾川烘焙旗下连锁早餐店', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/d6de4991-e48b-4d6f-8c6b-3c7c0584b100.jpg', '禾川早餐佳', 3.00, 2, NULL, 10.00, 2, 1);
INSERT INTO `business` VALUES (2, '2025-09-25 16:33:26', 2, 0, '2025-09-25 16:33:34', 3, '筷来见面面馆', '面条种类包罗万象，什么都有', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/359bf866-61a6-4b17-a4e9-c8a8953a76ab.jpg', '筷来见面面馆', 2.00, 1, NULL, 10.00, 2, 1);
INSERT INTO `business` VALUES (3, '2025-09-25 16:35:37', 2, 0, '2025-09-25 16:35:45', 3, '重庆市渝中区民族路168号', '禾川烘焙旗下连锁甜品店', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/221f0194-48bf-4f62-a04a-5684ee63d6eb.jpg', '禾川甜品坊', 5.00, 1, NULL, 15.00, 2, 1);
INSERT INTO `business` VALUES (4, '2025-09-25 16:58:47', 4, 0, '2025-09-25 17:06:06', 3, '天津市津南区永旺商城4楼', '朝鲜族非遗，米饭香甜', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/2a722cce-5604-4a07-9081-82981ca232fb.jpg', '米村拌饭', 0.00, 1, NULL, 8.00, 4, 1);
INSERT INTO `business` VALUES (5, '2025-09-25 17:02:51', 4, 0, '2025-09-25 17:06:32', 3, '天津市津南区北洋园校区', '肉串娇嫩多汁，香味四溢', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/29c339f6-eb51-4274-9a35-e40c1340ca48.jpg', '锦州烧烤', 0.00, 1, NULL, 10.00, 4, 1);

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
) ENGINE = InnoDB AUTO_INCREMENT = 41 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of cart
-- ----------------------------
INSERT INTO `cart` VALUES (24, '2025-11-10 14:16:17', 8, 1, '2025-11-10 14:16:17', 8, 4, 3, 8, 1);
INSERT INTO `cart` VALUES (25, '2025-11-10 14:21:55', 8, 1, '2025-11-10 14:21:55', 8, 1, 3, 8, 1);
INSERT INTO `cart` VALUES (26, '2025-11-10 14:28:42', 8, 1, '2025-11-10 14:28:42', 8, 1, 3, 8, 1);
INSERT INTO `cart` VALUES (27, '2025-11-10 14:36:03', 8, 1, '2025-11-10 14:36:03', 8, 1, 3, 8, 1);
INSERT INTO `cart` VALUES (28, '2025-11-10 14:37:07', 8, 1, '2025-11-10 14:37:07', 8, 7, 1, 8, 6);
INSERT INTO `cart` VALUES (29, '2025-11-10 14:41:15', 8, 1, '2025-11-10 14:41:15', 8, 2, 5, 8, 9);
INSERT INTO `cart` VALUES (30, '2025-11-10 14:41:44', 8, 1, '2025-11-10 14:41:44', 8, 4, 5, 8, 9);
INSERT INTO `cart` VALUES (31, '2025-11-10 14:45:01', 9, 1, '2025-11-10 14:45:01', 9, 2, 3, 9, 1);
INSERT INTO `cart` VALUES (32, '2025-11-10 14:49:44', 10, 1, '2025-11-10 14:49:44', 10, 4, 5, 10, 9);
INSERT INTO `cart` VALUES (33, '2025-11-10 14:53:42', 9, 1, '2025-11-10 14:53:42', 9, 2, 3, 9, 1);
INSERT INTO `cart` VALUES (34, '2025-11-10 14:56:35', 10, 1, '2025-11-10 14:56:35', 10, 1, 3, 10, 1);
INSERT INTO `cart` VALUES (35, '2025-11-10 14:59:16', 10, 1, '2025-11-10 14:59:16', 10, 3, 5, 10, 10);
INSERT INTO `cart` VALUES (36, '2025-11-10 15:25:44', 9, 1, '2025-11-10 15:25:44', 9, 1, 3, 9, 1);
INSERT INTO `cart` VALUES (37, '2025-11-10 15:39:01', 9, 1, '2025-11-10 15:39:01', 9, 2, 5, 9, 10);
INSERT INTO `cart` VALUES (38, '2025-11-10 21:08:54', 10, 1, '2025-11-10 21:08:54', 10, 3, 5, 10, 10);
INSERT INTO `cart` VALUES (39, '2025-11-10 21:09:20', 10, 1, '2025-11-10 21:09:20', 10, 1, 3, 10, 2);
INSERT INTO `cart` VALUES (40, '2025-11-10 21:09:21', 10, 1, '2025-11-10 21:09:21', 10, 1, 3, 10, 1);

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
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of delivery_address
-- ----------------------------
INSERT INTO `delivery_address` VALUES (4, '2025-09-25 21:34:42', 5, 0, '2025-09-25 23:00:36', 5, '湖南岳阳汴河街', '小高', 0, '13873021510', 5);
INSERT INTO `delivery_address` VALUES (6, '2025-09-25 21:49:55', 6, 0, '2025-09-25 21:49:55', 6, '湖南岳阳蓝剑大厦', '小刘', 1, '13873021510', 6);
INSERT INTO `delivery_address` VALUES (7, '2025-09-26 10:39:27', 5, 0, '2025-09-26 10:39:27', 5, '湖南岳阳楼', '小李', 1, '16607402898', 5);
INSERT INTO `delivery_address` VALUES (8, '2025-11-10 14:16:24', 8, 0, '2025-11-10 14:16:24', 8, '大蓝', 'hwz', 1, '18841154656', 8);
INSERT INTO `delivery_address` VALUES (9, '2025-11-10 14:45:07', 9, 0, '2025-11-10 14:45:07', 9, '大蓝', 'hwz', 1, '18841154656', 9);
INSERT INTO `delivery_address` VALUES (10, '2025-11-10 14:49:49', 10, 0, '2025-11-10 14:49:49', 10, '大蓝', 'hwz', 1, '18841154656', 10);

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
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

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
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商家互动表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of merchant_interaction
-- ----------------------------

-- ----------------------------
-- Table structure for notification
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息唯一ID',
  `user_id` bigint NOT NULL COMMENT '接收用户ID（关联users表id）',
  `notification_type` tinyint NOT NULL COMMENT '消息类型：0=商家申请审核，1=开店申请审核',
  `notification_content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息内容',
  `audit_result` tinyint NOT NULL COMMENT '审核结果：1=通过，2=拒绝',
  `is_read` tinyint NOT NULL DEFAULT 0 COMMENT '阅读状态：0=未读，1=已读',
  `create_time` datetime NOT NULL COMMENT '消息创建时间',
  `read_time` datetime NULL DEFAULT NULL COMMENT '消息阅读时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_user_is_read`(`user_id` ASC, `is_read` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 39 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户消息通知表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of notification
-- ----------------------------

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
) ENGINE = InnoDB AUTO_INCREMENT = 40 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of orderdetailet
-- ----------------------------
INSERT INTO `orderdetailet` VALUES (8, '2025-09-25 21:50:00', 6, 0, '2025-09-25 21:50:00', 6, 1, 1, 7, 28.00);
INSERT INTO `orderdetailet` VALUES (23, '2025-11-10 14:16:27', 8, 0, '2025-11-10 14:16:27', 8, 4, 1, 22, 28.00);
INSERT INTO `orderdetailet` VALUES (24, '2025-11-10 14:21:58', 8, 0, '2025-11-10 14:21:58', 8, 1, 1, 23, 28.00);
INSERT INTO `orderdetailet` VALUES (25, '2025-11-10 14:28:46', 8, 0, '2025-11-10 14:28:46', 8, 1, 1, 24, 28.00);
INSERT INTO `orderdetailet` VALUES (26, '2025-11-10 14:36:07', 8, 0, '2025-11-10 14:36:07', 8, 1, 1, 25, 28.00);
INSERT INTO `orderdetailet` VALUES (27, '2025-11-10 14:37:11', 8, 0, '2025-11-10 14:37:11', 8, 7, 6, 26, 2.00);
INSERT INTO `orderdetailet` VALUES (28, '2025-11-10 14:41:18', 8, 0, '2025-11-10 14:41:18', 8, 2, 9, 27, 10.00);
INSERT INTO `orderdetailet` VALUES (29, '2025-11-10 14:41:47', 8, 0, '2025-11-10 14:41:47', 8, 4, 9, 28, 10.00);
INSERT INTO `orderdetailet` VALUES (30, '2025-11-10 14:45:10', 9, 0, '2025-11-10 14:45:10', 9, 2, 1, 29, 28.00);
INSERT INTO `orderdetailet` VALUES (31, '2025-11-10 14:49:52', 10, 0, '2025-11-10 14:49:52', 10, 4, 9, 30, 10.00);
INSERT INTO `orderdetailet` VALUES (32, '2025-11-10 14:53:45', 9, 0, '2025-11-10 14:53:45', 9, 2, 1, 31, 28.00);
INSERT INTO `orderdetailet` VALUES (33, '2025-11-10 14:56:38', 10, 0, '2025-11-10 14:56:38', 10, 1, 1, 32, 28.00);
INSERT INTO `orderdetailet` VALUES (34, '2025-11-10 14:59:19', 10, 0, '2025-11-10 14:59:19', 10, 3, 10, 33, 15.00);
INSERT INTO `orderdetailet` VALUES (35, '2025-11-10 15:25:47', 9, 0, '2025-11-10 15:25:47', 9, 1, 1, 34, 28.00);
INSERT INTO `orderdetailet` VALUES (36, '2025-11-10 15:39:04', 9, 0, '2025-11-10 15:39:04', 9, 2, 10, 35, 15.00);
INSERT INTO `orderdetailet` VALUES (37, '2025-11-10 21:09:00', 10, 0, '2025-11-10 21:09:00', 10, 3, 10, 36, 15.00);
INSERT INTO `orderdetailet` VALUES (38, '2025-11-10 21:09:26', 10, 0, '2025-11-10 21:09:26', 10, 1, 2, 37, 32.00);
INSERT INTO `orderdetailet` VALUES (39, '2025-11-10 21:09:26', 10, 0, '2025-11-10 21:09:26', 10, 1, 1, 37, 28.00);

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
  `payment_method` tinyint NULL DEFAULT 0 COMMENT '支付方式 0-微信支付 1-支付宝支付 2-虚拟钱包支付',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `business_id`(`business_id` ASC) USING BTREE,
  INDEX `customer_id`(`customer_id` ASC) USING BTREE,
  INDEX `address_id`(`address_id` ASC) USING BTREE,
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`business_id`) REFERENCES `business` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`customer_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `orders_ibfk_3` FOREIGN KEY (`address_id`) REFERENCES `delivery_address` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 38 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (7, '2025-09-25 21:50:00', 6, 0, '2025-09-25 21:50:00', 6, '2025-09-25 21:50:00', 3, 33.00, 3, 6, 6, 5.00, 0);
INSERT INTO `orders` VALUES (22, '2025-11-10 14:16:27', 8, 0, '2025-11-10 14:16:27', 8, '2025-11-10 14:16:27', 0, 117.00, 3, 8, 8, 5.00, 0);
INSERT INTO `orders` VALUES (23, '2025-11-10 14:21:58', 8, 0, '2025-11-10 14:21:58', 8, '2025-11-10 14:21:58', 0, 33.00, 3, 8, 8, 5.00, 0);
INSERT INTO `orders` VALUES (24, '2025-11-10 14:28:46', 8, 0, '2025-11-10 14:28:46', 8, '2025-11-10 14:28:46', 0, 33.00, 3, 8, 8, 5.00, 0);
INSERT INTO `orders` VALUES (25, '2025-11-10 14:36:07', 8, 0, '2025-11-10 14:36:07', 8, '2025-11-10 14:36:07', 0, 33.00, 3, 8, 8, 5.00, 0);
INSERT INTO `orders` VALUES (26, '2025-11-10 14:37:11', 8, 0, '2025-11-10 14:37:11', 8, '2025-11-10 14:37:11', 0, 17.00, 1, 8, 8, 3.00, 0);
INSERT INTO `orders` VALUES (27, '2025-11-10 14:41:18', 8, 0, '2025-11-10 14:41:18', 8, '2025-11-10 14:41:18', 0, 20.00, 5, 8, 8, 0.00, 0);
INSERT INTO `orders` VALUES (28, '2025-11-10 14:41:47', 8, 0, '2025-11-10 14:41:47', 8, '2025-11-10 14:41:47', 0, 40.00, 5, 8, 8, 0.00, 0);
INSERT INTO `orders` VALUES (29, '2025-11-10 14:45:10', 9, 0, '2025-11-10 14:45:10', 9, '2025-11-10 14:45:10', 0, 61.00, 3, 9, 9, 5.00, 0);
INSERT INTO `orders` VALUES (30, '2025-11-10 14:49:52', 10, 0, '2025-11-10 14:49:52', 10, '2025-11-10 14:49:52', 0, 40.00, 5, 10, 10, 0.00, 0);
INSERT INTO `orders` VALUES (31, '2025-11-10 14:53:45', 9, 0, '2025-11-10 14:53:45', 9, '2025-11-10 14:53:45', 0, 61.00, 3, 9, 9, 5.00, 0);
INSERT INTO `orders` VALUES (32, '2025-11-10 14:56:38', 10, 0, '2025-11-10 14:56:38', 10, '2025-11-10 14:56:38', 0, 33.00, 3, 10, 10, 5.00, 0);
INSERT INTO `orders` VALUES (33, '2025-11-10 14:59:19', 10, 0, '2025-11-10 14:59:19', 10, '2025-11-10 14:59:19', 0, 45.00, 5, 10, 10, 0.00, 0);
INSERT INTO `orders` VALUES (34, '2025-11-10 15:25:47', 9, 0, '2025-11-10 15:25:47', 9, '2025-11-10 15:25:47', 0, 33.00, 3, 9, 9, 5.00, 0);
INSERT INTO `orders` VALUES (35, '2025-11-10 15:39:04', 9, 0, '2025-11-10 15:39:04', 9, '2025-11-10 15:39:04', 0, 30.00, 5, 9, 9, 0.00, 0);
INSERT INTO `orders` VALUES (36, '2025-11-10 21:09:00', 10, 0, '2025-11-10 21:09:00', 10, '2025-11-10 21:09:00', 1, 45.00, 5, 10, 10, 0.00, 0);
INSERT INTO `orders` VALUES (37, '2025-11-10 21:09:26', 10, 0, '2025-11-10 21:09:26', 10, '2025-11-10 21:09:26', 1, 65.00, 3, 10, 10, 5.00, 0);

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
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

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
INSERT INTO `person` VALUES ('3023244157@tju.edu.cn', 'chasm', '0', 'cpp', '17850362981', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/e8ad390d-9f9c-4433-b0a6-4dbf3f002e4c.jpg', 7);
INSERT INTO `person` VALUES ('2323376769@qq.com', 'j', '0', 'sy', '18841154656', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/19a21dbd-9505-4e34-bdda-dba8b1721575.png', 8);
INSERT INTO `person` VALUES ('2549407920@qq.com', '1', '0', '1', '18612345678', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/00fdb78e-fad6-4b98-ba19-732dbf0fe049.png', 9);
INSERT INTO `person` VALUES ('2549407920@qq.com', '1', '0', '1', '18612345678', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/084d5432-398c-4282-bf1d-15fc2cd37459.png', 10);

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

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
INSERT INTO `user_authority` VALUES (2, 'BUSINESS');
INSERT INTO `user_authority` VALUES (4, 'BUSINESS');
INSERT INTO `user_authority` VALUES (2, 'USER');
INSERT INTO `user_authority` VALUES (4, 'USER');
INSERT INTO `user_authority` VALUES (5, 'USER');
INSERT INTO `user_authority` VALUES (6, 'USER');
INSERT INTO `user_authority` VALUES (7, 'USER');
INSERT INTO `user_authority` VALUES (8, 'USER');
INSERT INTO `user_authority` VALUES (9, 'USER');
INSERT INTO `user_authority` VALUES (10, 'USER');

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
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (2, '2025-09-25 16:17:50', NULL, 0, '2025-09-25 19:06:53', 2, 1, '$2a$10$HL7jh92Duv3mAoklfgS3iOglu1CGblLKqaC.SQL43qtNNKivGfy96', 'sunny');
INSERT INTO `users` VALUES (3, '2025-09-25 16:18:35', NULL, 0, '2025-09-25 16:18:35', NULL, 1, '$2a$10$.0tYR0Ek3Ns36Ggc1PW4YuNNRBTmZQQJ2aw7fBWy7xJVNMoGfyiv.', 'sun');
INSERT INTO `users` VALUES (4, '2025-09-25 16:19:23', NULL, 0, '2025-09-25 16:19:23', NULL, 1, '$2a$10$yr7GoYV3rRGPPwrvqyNaCuBy9VDfoU3qaOYkyYHG.9oqoeFZ70BEC', 'sunshine');
INSERT INTO `users` VALUES (5, '2025-09-25 16:20:16', NULL, 0, '2025-09-25 21:34:23', 5, 1, '$2a$10$7/d4r0bO7OSE0JRY1iKOtuiO1ca/GyFXPq2HnFaXKKrJhN6h4MHZ6', 'rain');
INSERT INTO `users` VALUES (6, '2025-09-25 16:22:47', NULL, 0, '2025-09-25 16:22:47', NULL, 1, '$2a$10$TdoFHty.VsIqCJOxn5hohOrwXcMhtZ0rc8w24LYSqH43q9tBjqUa.', 'rainy');
INSERT INTO `users` VALUES (7, '2025-11-10 12:26:02', NULL, 0, '2025-11-10 12:26:02', NULL, 1, '$2a$10$T67UImADF0f0ir8/dW9K.eSethxPg54ZjEq0gTuPlOFGibP9p3eva', 'chasm_cpp');
INSERT INTO `users` VALUES (8, '2025-11-10 14:15:55', NULL, 0, '2025-11-10 14:15:55', NULL, 1, '$2a$10$RxIYMUl8vjB8ILuwe8Y/3uRn5qhBgCsuuKN.XAyIH1Mx/k0fWZgmm', 'test');
INSERT INTO `users` VALUES (9, '2025-11-10 14:44:03', NULL, 0, '2025-11-10 14:44:03', NULL, 1, '$2a$10$JZAPv/xDNcXZpXcQq6BR.O.vQU53FqTWH2XIWZGr59rI/N8Dyx6yi', '1');
INSERT INTO `users` VALUES (10, '2025-11-10 14:49:28', NULL, 0, '2025-11-10 14:49:28', NULL, 1, '$2a$10$T1ijojSBztP1RZpKp89AIuwLu4AoQahGNuE5.sg/PCheOSYqy5KkO', '2');

-- ----------------------------
-- Table structure for virtual_wallet
-- ----------------------------
DROP TABLE IF EXISTS `virtual_wallet`;
CREATE TABLE `virtual_wallet`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '虚拟钱包id',
  `user_id` bigint NOT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `status` tinyint NULL DEFAULT 0 COMMENT '钱包状态 0-正常 1-冻结',
  `vip_level` tinyint NULL DEFAULT 0 COMMENT 'vip级别 0-非vip',
  `is_deleted` tinyint NULL DEFAULT 0,
  `balance` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '余额',
  `overdraft_amount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '可透支金额',
  `overdrawn_amount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '已透支金额',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of virtual_wallet
-- ----------------------------

-- ----------------------------
-- Table structure for virtual_wallet_transaction
-- ----------------------------
DROP TABLE IF EXISTS `virtual_wallet_transaction`;
CREATE TABLE `virtual_wallet_transaction`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `type` tinyint NULL DEFAULT NULL COMMENT '交易类型 0-支付 1-收款 2-提现 3-充值',
  `status` tinyint NULL DEFAULT 0 COMMENT '操作金额是否为冻结 0-否 1-是',
  `amount` decimal(10, 2) NOT NULL COMMENT '操作金额',
  `from_account` bigint NOT NULL COMMENT '转出钱包 交易类型为充值时值为0',
  `to_account` bigint NOT NULL COMMENT '转入钱包 交易类型为提现时值为0',
  `is_deleted` tinyint NULL DEFAULT 0,
  `fee` decimal(10, 2) NULL DEFAULT NULL COMMENT '手续费或奖励',
  `fee_rate` decimal(10, 2) NULL DEFAULT NULL COMMENT '手续费率或奖励率',
  `create_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '交易时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of virtual_wallet_transaction
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
