/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80040 (8.0.40)
 Source Host           : localhost:3306
 Source Schema         : elm

 Target Server Type    : MySQL
 Target Server Version : 80040 (8.0.40)
 File Encoding         : 65001

 Date: 08/09/2025 10:58:03
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for business
-- ----------------------------
DROP TABLE IF EXISTS `business`;
CREATE TABLE `business`  (
  `businessId` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '商家编号',
  `businessName` varchar(40) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '商家名称',
  `businessAddress` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '商家地址',
  `businessExplain` varchar(40) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '商家介绍',
  `businessImg` mediumtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '商家图片（base64）',
  `orderTypeId` int NOT NULL COMMENT '点餐分类',
  `starPrice` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '起送费',
  `deliveryPrice` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '配送费',
  `remarks` varchar(40) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`businessId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10024 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for business_user
-- ----------------------------
DROP TABLE IF EXISTS `business_user`;
CREATE TABLE `business_user`  (
  `businessId` int NOT NULL AUTO_INCREMENT COMMENT '商家编号',
  `password` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '商家密码',
  `businessName` varchar(40) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '商家名称',
  `businessAddress` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '商家地址',
  `businessExplain` varchar(40) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '商家介绍',
  `starPrice` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '起送费',
  `deliveryPrice` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '配送费',
  `phoneNumber` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`businessId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10024 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for cart
-- ----------------------------
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart`  (
  `cartId` int NOT NULL AUTO_INCREMENT COMMENT '无意义编号',
  `foodId` int NOT NULL COMMENT '食品编号',
  `businessId` int NOT NULL COMMENT '所属商家编号',
  `userId` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '所属用户编号',
  `quantity` int NOT NULL COMMENT '同一类型食品的购买数量',
  PRIMARY KEY (`cartId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 107 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for deliveryaddress
-- ----------------------------
DROP TABLE IF EXISTS `deliveryaddress`;
CREATE TABLE `deliveryaddress`  (
  `daId` int NOT NULL AUTO_INCREMENT COMMENT '送货地址编号',
  `contactName` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '联系人姓名',
  `contactSex` int NOT NULL COMMENT '联系人性别',
  `contactTel` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '联系人电话',
  `address` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '送货地址',
  `userId` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '所属用户编号',
  PRIMARY KEY (`daId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for food
-- ----------------------------
DROP TABLE IF EXISTS `food`;
CREATE TABLE `food`  (
  `foodId` int NOT NULL AUTO_INCREMENT COMMENT '食品编号',
  `foodName` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '食品名称',
  `foodExplain` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '食品介绍',
  `foodImg` mediumtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '食品图片',
  `foodPrice` decimal(5, 2) NOT NULL COMMENT '食品价格',
  `businessId` int NOT NULL COMMENT '所属商家编号',
  `remarks` varchar(40) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`foodId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for orderdetailet
-- ----------------------------
DROP TABLE IF EXISTS `orderdetailet`;
CREATE TABLE `orderdetailet`  (
  `odId` int NOT NULL AUTO_INCREMENT COMMENT '订单明细编号',
  `orderId` int NOT NULL COMMENT '所属订单编号',
  `foodId` int NOT NULL COMMENT '食品编号',
  `quantity` int NOT NULL COMMENT '数量',
  `foodName` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '食物下单时名字',
  `foodPrice` decimal(5, 2) NULL DEFAULT NULL COMMENT '食物下单时价格',
  PRIMARY KEY (`odId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 85 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `orderId` int NOT NULL AUTO_INCREMENT COMMENT '订单编号',
  `userId` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '用户编号',
  `businessId` int NOT NULL COMMENT '商家编号',
  `orderDate` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '订购日期',
  `orderTotal` decimal(7, 2) NOT NULL DEFAULT 0.00 COMMENT '订单总价',
  `daId` int NOT NULL COMMENT '送货地址编号',
  `orderState` int NOT NULL DEFAULT 0 COMMENT '订单状态（0：未支付； 1：已支付）',
  `deliveryPrice` decimal(5, 2) NULL DEFAULT NULL,
  PRIMARY KEY (`orderId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 59 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `userId` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '用户编号',
  `password` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '密码',
  `userName` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '用户名称',
  `userSex` int NOT NULL DEFAULT 1 COMMENT '用户性别（1：男； 0：女）',
  `userImg` mediumtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL COMMENT '用户头像',
  `delTag` int NOT NULL DEFAULT 1 COMMENT '删除标记（1：正常； 0：删除）',
  PRIMARY KEY (`userId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
