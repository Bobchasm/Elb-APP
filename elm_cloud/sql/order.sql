CREATE DATABASE elm_order;

USE elm_order;

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
                           `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                           `contact_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                           `contact_sex` int NULL DEFAULT NULL,
                           `contact_tel` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                           PRIMARY KEY (`id`) USING BTREE,
                           INDEX `business_id`(`business_id` ASC) USING BTREE,
                           INDEX `customer_id`(`customer_id` ASC) USING BTREE,
                           INDEX `address_id`(`address_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;



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
                                  INDEX `order_id`(`order_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;