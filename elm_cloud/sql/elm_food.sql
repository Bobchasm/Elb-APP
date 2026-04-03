/*
 Navicat Premium Dump SQL

 Source Server         : TXCloud1
 Source Server Type    : MySQL
 Source Server Version : 80044 (8.0.44)
 Source Host           : REDACTED_IP:3306
 Source Schema         : elm_food

 Target Server Type    : MySQL
 Target Server Version : 80044 (8.0.44)
 File Encoding         : 65001

 Date: 03/04/2026 22:38:39
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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
  INDEX `food_id`(`food_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of cart
-- ----------------------------
INSERT INTO `cart` VALUES (1, '2026-04-03 20:17:26', 7, 1, '2026-04-03 20:17:26', 7, 1, 5, 7, 9);
INSERT INTO `cart` VALUES (2, '2026-04-03 20:17:27', 7, 1, '2026-04-03 20:17:27', 7, 1, 5, 7, 10);

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
  INDEX `business_id`(`business_id` ASC) USING BTREE
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

SET FOREIGN_KEY_CHECKS = 1;
