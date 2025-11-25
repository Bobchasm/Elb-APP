/*
 Navicat Premium Dump SQL

 Source Server         : tx_cloud1
 Source Server Type    : MySQL
 Source Server Version : 80044 (8.0.44)
 Source Host           : REDACTED_IP:3306
 Source Schema         : elm_v2

 Target Server Type    : MySQL
 Target Server Version : 80044 (8.0.44)
 File Encoding         : 65001

 Date: 25/11/2025 11:26:14
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
