-- AI对话历史表
CREATE TABLE `ai_chat_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `creator` bigint(20) NULL DEFAULT NULL COMMENT '创建者ID',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除(0:否,1:是)',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `updater` bigint(20) NULL DEFAULT NULL COMMENT '更新者ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `session_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '会话ID',
  `user_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户消息',
  `ai_response` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'AI回复',
  `chat_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'general' COMMENT '对话类型',
  `processing_time` bigint(20) NULL DEFAULT NULL COMMENT '处理耗时(毫秒)',
  `context_data` json NULL COMMENT '上下文数据',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id` (`user_id`) USING BTREE COMMENT '用户ID索引',
  INDEX `idx_session_id` (`session_id`) USING BTREE COMMENT '会话ID索引',
  INDEX `idx_chat_type` (`chat_type`) USING BTREE COMMENT '对话类型索引',
  INDEX `idx_create_time` (`create_time`) USING BTREE COMMENT '创建时间索引',
  CONSTRAINT `fk_ai_chat_history_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'AI对话历史表' ROW_FORMAT = Dynamic;
