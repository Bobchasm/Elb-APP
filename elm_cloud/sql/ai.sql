CREATE DATABASE elm_ai_record;

USE elm_ai_record;

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
                                    INDEX `idx_create_time`(`create_time` ASC) USING BTREE COMMENT '创建时间索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'AI对话历史表' ROW_FORMAT = DYNAMIC;