CREATE DATABASE elm_business;

USE elm_business;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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
                             INDEX `user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of business
-- ----------------------------
INSERT INTO `business` VALUES (1, '2025-09-25 16:29:40', 2, 0, '2025-09-25 20:24:12', 3, '四川省成都市玉林路46号', '禾川烘焙旗下连锁早餐店', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/d6de4991-e48b-4d6f-8c6b-3c7c0584b100.jpg', '禾川早餐佳', 3.00, 2, NULL, 10.00, 2, 1);
INSERT INTO `business` VALUES (2, '2025-09-25 16:33:26', 2, 0, '2025-09-25 16:33:34', 3, '筷来见面面馆', '面条种类包罗万象，什么都有', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/359bf866-61a6-4b17-a4e9-c8a8953a76ab.jpg', '筷来见面面馆', 2.00, 1, NULL, 10.00, 2, 1);
INSERT INTO `business` VALUES (3, '2025-09-25 16:35:37', 2, 0, '2025-09-25 16:35:45', 3, '重庆市渝中区民族路168号', '禾川烘焙旗下连锁甜品店', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/221f0194-48bf-4f62-a04a-5684ee63d6eb.jpg', '禾川甜品坊', 5.00, 1, NULL, 15.00, 2, 1);
INSERT INTO `business` VALUES (4, '2025-09-25 16:58:47', 4, 0, '2025-09-25 17:06:06', 3, '天津市津南区永旺商城4楼', '朝鲜族非遗，米饭香甜', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/2a722cce-5604-4a07-9081-82981ca232fb.jpg', '米村拌饭', 0.00, 1, NULL, 8.00, 4, 1);
INSERT INTO `business` VALUES (5, '2025-09-25 17:02:51', 4, 0, '2025-09-25 17:06:32', 3, '天津市津南区北洋园校区', '肉串娇嫩多汁，香味四溢', 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/29c339f6-eb51-4274-9a35-e40c1340ca48.jpg', '锦州烧烤', 0.00, 1, NULL, 10.00, 4, 1);


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
                                         INDEX `idx_collected`(`collected` ASC) USING BTREE COMMENT '收藏状态索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商家互动表' ROW_FORMAT = DYNAMIC;