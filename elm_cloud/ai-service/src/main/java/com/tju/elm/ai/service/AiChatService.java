package com.tju.elm.ai.service;


import com.tju.elm.ai.zoo.pojo.AiChatHistoryVO;
import com.tju.elm.ai.zoo.pojo.AiChatRequestDTO;
import com.tju.elm.ai.zoo.pojo.AiChatResponseVO;

import java.util.List;

public interface AiChatService {
    
    /**
     * 处理AI聊天请求
     */
    AiChatResponseVO chat(AiChatRequestDTO request);

    /**
     * 获取用户对话历史
     */
    List<AiChatHistoryVO> getChatHistory(Long userId, Integer page, Integer size);
    
    /**
     * 根据会话ID获取对话历史
     */
    List<AiChatHistoryVO> getChatHistoryBySession(String sessionId);
    
    /**
     * 删除对话历史
     */
    Boolean deleteChatHistory(Long historyId, Long userId);

    /**
     * 清理用户的旧对话记录（保留最近的N条）
     */
    Boolean cleanOldChatHistory(Long userId, Integer keepCount);
}
