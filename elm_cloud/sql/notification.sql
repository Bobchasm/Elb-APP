CREATE DATABASE elm_notification;

USE elm_notification;


SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;


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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户消息通知表' ROW_FORMAT = DYNAMIC;