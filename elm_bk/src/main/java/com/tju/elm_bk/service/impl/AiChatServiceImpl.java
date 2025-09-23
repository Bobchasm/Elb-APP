package com.tju.elm_bk.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tju.elm_bk.dto.AiChatRequestDTO;
import com.tju.elm_bk.dto.DeepSeekRequestDTO;
import com.tju.elm_bk.dto.DeepSeekResponseDTO;
import com.tju.elm_bk.entity.AiChatHistory;
import com.tju.elm_bk.entity.Business;
import com.tju.elm_bk.entity.Food;
import com.tju.elm_bk.entity.Order;
import com.tju.elm_bk.entity.User;
import com.tju.elm_bk.exception.APIException;
import com.tju.elm_bk.mapper.AiChatHistoryMapper;
import com.tju.elm_bk.result.ResultCodeEnum;
import com.tju.elm_bk.service.AiChatService;
import com.tju.elm_bk.utils.AiKnowledgeBaseUtil;
import com.tju.elm_bk.utils.DeepSeekApiClient;
import com.tju.elm_bk.mapper.UserMapper;
import com.tju.elm_bk.utils.SecurityUtils;
import com.tju.elm_bk.vo.AiChatHistoryVO;
import com.tju.elm_bk.vo.AiChatResponseVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatServiceImpl implements AiChatService {
    
    private final DeepSeekApiClient deepSeekApiClient;
    private final AiKnowledgeBaseUtil knowledgeBaseUtil;
    private final AiChatHistoryMapper chatHistoryMapper;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;
    
    /**
     * 获取当前用户ID的辅助方法
     */
    private Long getCurrentUserId() {
        try {
            String username = SecurityUtils.getCurrentUsername()
                    .orElse(null);
            if (username == null) return null;
            
            User currentUser = userMapper.findByUsername(username);
            return currentUser != null ? currentUser.getId() : null;
        } catch (Exception e) {
            log.warn("获取当前用户ID失败: {}", e.getMessage());
            return null;
        }
    }
    
    @Override
    @Transactional
    public AiChatResponseVO chat(AiChatRequestDTO request) {
        long startTime = System.currentTimeMillis();
        
        try {
            // 生成或使用现有的会话ID
            String sessionId = request.getSessionId();
            if (sessionId == null || sessionId.trim().isEmpty()) {
                sessionId = UUID.randomUUID().toString();
            }
            
            // 构建AI请求
            DeepSeekRequestDTO deepSeekRequest = buildDeepSeekRequest(request);
            
            // 调用DeepSeek API
            DeepSeekResponseDTO deepSeekResponse = deepSeekApiClient.chatCompletionSync(deepSeekRequest);
            
            // 解析AI响应
            String aiMessage = extractAiMessage(deepSeekResponse);
            
            // 构建响应
            AiChatResponseVO response = new AiChatResponseVO();
            response.setMessage(aiMessage);
            response.setSessionId(sessionId);
            response.setResponseType("text");
            response.setResponseTime(LocalDateTime.now());
            
            long processingTime = System.currentTimeMillis() - startTime;
            response.setProcessingTime(processingTime);
            
            // 保存对话历史
            saveChatHistory(request, response, processingTime);
            
            return response;
            
        } catch (Exception e) {
            log.error("AI聊天处理失败", e);
            
            // 返回错误响应
            AiChatResponseVO errorResponse = new AiChatResponseVO();
            errorResponse.setMessage("抱歉，我现在遇到了一些技术问题，请稍后再试或联系人工客服。");
            errorResponse.setSessionId(request.getSessionId());
            errorResponse.setResponseType("error");
            errorResponse.setResponseTime(LocalDateTime.now());
            errorResponse.setProcessingTime(System.currentTimeMillis() - startTime);
            
            return errorResponse;
        }
    }
    
    /**
     * 构建DeepSeek API请求
     */
    private DeepSeekRequestDTO buildDeepSeekRequest(AiChatRequestDTO request) {
        List<DeepSeekRequestDTO.MessageDTO> messages = new ArrayList<>();
        
        // 添加系统提示词
        String systemPrompt = buildSystemPrompt(request);
        messages.add(new DeepSeekRequestDTO.MessageDTO("system", systemPrompt));
        
        // 添加历史对话（最近3轮）
        if (request.getSessionId() != null && !request.getSessionId().trim().isEmpty()) {
            List<AiChatHistory> recentHistory = chatHistoryMapper.selectBySessionId(request.getSessionId());
            if (recentHistory.size() > 6) { // 保留最近3轮对话
                recentHistory = recentHistory.subList(recentHistory.size() - 6, recentHistory.size());
            }
            
            for (AiChatHistory history : recentHistory) {
                messages.add(new DeepSeekRequestDTO.MessageDTO("user", history.getUserMessage()));
                messages.add(new DeepSeekRequestDTO.MessageDTO("assistant", history.getAiResponse()));
            }
        }
        
        // 添加当前用户消息
        String enhancedUserMessage = enhanceUserMessage(request);
        messages.add(new DeepSeekRequestDTO.MessageDTO("user", enhancedUserMessage));
        
        // 构建请求
        DeepSeekRequestDTO deepSeekRequest = new DeepSeekRequestDTO();
        deepSeekRequest.setMessages(messages);
        deepSeekRequest.setMaxTokens(1024);
        deepSeekRequest.setTemperature(0.7);
        
        return deepSeekRequest;
    }
    
    /**
     * 构建系统提示词
     */
    private String buildSystemPrompt(AiChatRequestDTO request) {
        StringBuilder prompt = new StringBuilder(knowledgeBaseUtil.buildSystemPrompt());
        
        // 添加用户上下文信息
        if (request.getUserId() != null) {
            Map<String, Object> userContext = knowledgeBaseUtil.getUserContext(request.getUserId());
            if (!userContext.isEmpty()) {
                prompt.append("\n\n用户信息：\n");
                userContext.forEach((key, value) -> 
                    prompt.append("- ").append(key).append(": ").append(value).append("\n"));
            }
        }
        
        return prompt.toString();
    }
    
    /**
     * 增强用户消息（添加相关数据上下文）
     */
    private String enhanceUserMessage(AiChatRequestDTO request) {
        String originalMessage = request.getMessage();
        StringBuilder enhancedMessage = new StringBuilder(originalMessage);
        
        // 检测消息中是否包含商家、菜品、订单相关关键词
        String message = originalMessage.toLowerCase();
        
        // 商家相关
        if (containsBusinessKeywords(message)) {
            List<String> businessKeywords = extractBusinessKeywords(originalMessage);
            for (String keyword : businessKeywords) {
                List<Business> businesses = knowledgeBaseUtil.searchBusinesses(keyword, 3);
                if (!businesses.isEmpty()) {
                    enhancedMessage.append("\n\n相关商家信息：\n");
                    for (Business business : businesses) {
                        enhancedMessage.append("- ").append(knowledgeBaseUtil.formatBusinessInfo(business)).append("\n");
                    }
                }
            }
        }
        
        // 菜品相关
        if (containsFoodKeywords(message)) {
            List<String> foodKeywords = extractFoodKeywords(originalMessage);
            for (String keyword : foodKeywords) {
                List<Food> foods = knowledgeBaseUtil.searchFoods(keyword, 3);
                if (!foods.isEmpty()) {
                    enhancedMessage.append("\n\n相关菜品信息：\n");
                    for (Food food : foods) {
                        enhancedMessage.append("- ").append(knowledgeBaseUtil.formatFoodInfo(food)).append("\n");
                    }
                }
            }
        }
        
        // 订单相关
        if (containsOrderKeywords(message) && request.getUserId() != null) {
            Map<String, Object> userContext = knowledgeBaseUtil.getUserContext(request.getUserId());
            if (userContext.containsKey("lastOrderId")) {
                Long orderId = (Long) userContext.get("lastOrderId");
                Order order = knowledgeBaseUtil.getOrderById(orderId);
                if (order != null) {
                    enhancedMessage.append("\n\n您的最近订单信息：\n");
                    enhancedMessage.append("- ").append(knowledgeBaseUtil.formatOrderInfo(order)).append("\n");
                }
            }
        }
        
        return enhancedMessage.toString();
    }
    
    /**
     * 检测是否包含商家相关关键词
     */
    private boolean containsBusinessKeywords(String message) {
        String[] keywords = {"商家", "店铺", "餐厅", "外卖店", "商户", "饭店"};
        for (String keyword : keywords) {
            if (message.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 检测是否包含菜品相关关键词
     */
    private boolean containsFoodKeywords(String message) {
        String[] keywords = {"菜", "菜品", "食物", "美食", "餐", "吃", "点餐", "菜单"};
        for (String keyword : keywords) {
            if (message.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 检测是否包含订单相关关键词
     */
    private boolean containsOrderKeywords(String message) {
        String[] keywords = {"订单", "下单", "支付", "配送", "外卖", "催单", "退款"};
        for (String keyword : keywords) {
            if (message.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 提取商家相关关键词
     */
    private List<String> extractBusinessKeywords(String message) {
        List<String> keywords = new ArrayList<>();
        // 简单的关键词提取逻辑，可以根据需要完善
        Pattern pattern = Pattern.compile("[\u4e00-\u9fa5a-zA-Z0-9]{2,10}[店铺商家餐厅]|[店铺商家餐厅][\u4e00-\u9fa5a-zA-Z0-9]{2,10}");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            keywords.add(matcher.group());
        }
        return keywords;
    }
    
    /**
     * 提取菜品相关关键词
     */
    private List<String> extractFoodKeywords(String message) {
        List<String> keywords = new ArrayList<>();
        // 简单的关键词提取逻辑
        Pattern pattern = Pattern.compile("[\u4e00-\u9fa5a-zA-Z0-9]{2,10}[菜品饭面]|[菜品饭面][\u4e00-\u9fa5a-zA-Z0-9]{2,10}");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            keywords.add(matcher.group());
        }
        return keywords;
    }
    
    /**
     * 提取AI响应消息
     */
    private String extractAiMessage(DeepSeekResponseDTO response) {
        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            return "抱歉，我现在无法处理您的请求，请稍后再试。";
        }
        
        DeepSeekResponseDTO.ChoiceDTO choice = response.getChoices().get(0);
        if (choice.getMessage() == null || choice.getMessage().getContent() == null) {
            return "抱歉，我现在无法处理您的请求，请稍后再试。";
        }
        
        return choice.getMessage().getContent().trim();
    }
    
    /**
     * 保存对话历史
     */
    private void saveChatHistory(AiChatRequestDTO request, AiChatResponseVO response, long processingTime) {
        try {
            AiChatHistory history = new AiChatHistory();
            history.setUserId(request.getUserId());
            history.setSessionId(response.getSessionId());
            history.setUserMessage(request.getMessage());
            history.setAiResponse(response.getMessage());
            history.setChatType(request.getChatType());
            history.setProcessingTime(processingTime);
            history.setCreateTime(LocalDateTime.now());
            history.setCreator(request.getUserId());
            history.setIsDeleted(false);
            
            // 构建上下文数据
            Map<String, Object> contextData = Map.of(
                "responseType", response.getResponseType(),
                "needConfirmation", response.getNeedConfirmation(),
                "userAgent", "web" // 可以根据需要添加更多上下文信息
            );
            
            try {
                history.setContextData(objectMapper.writeValueAsString(contextData));
            } catch (JsonProcessingException e) {
                log.warn("序列化上下文数据失败", e);
                history.setContextData("{}");
            }
            
            chatHistoryMapper.insert(history);
            
        } catch (Exception e) {
            log.error("保存对话历史失败", e);
            // 不影响主流程，只记录错误
        }
    }
    
    @Override
    public List<AiChatHistoryVO> getChatHistory(Long userId, Integer page, Integer size) {
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1 || size > 50) size = 20;
        
        int offset = (page - 1) * size;
        List<AiChatHistory> histories = chatHistoryMapper.selectByUserId(userId, size, offset);
        
        return histories.stream()
                .map(this::convertToVO)
                .toList();
    }
    
    @Override
    public List<AiChatHistoryVO> getChatHistoryBySession(String sessionId) {
        List<AiChatHistory> histories = chatHistoryMapper.selectBySessionId(sessionId);
        return histories.stream()
                .map(this::convertToVO)
                .toList();
    }
    
    @Override
    @Transactional
    public Boolean deleteChatHistory(Long historyId, Long userId) {
        try {
            Long currentUserId = getCurrentUserId();
            if (currentUserId == null) {
                currentUserId = userId;
            }
            
            int result = chatHistoryMapper.deleteById(historyId, currentUserId);
            return result > 0;
        } catch (Exception e) {
            log.error("删除对话历史失败", e);
            return false;
        }
    }
    
    @Override
    @Transactional
    public Boolean cleanOldChatHistory(Long userId, Integer keepCount) {
        try {
            if (keepCount == null || keepCount < 10) {
                keepCount = 50; // 默认保留50条
            }
            
            int result = chatHistoryMapper.cleanOldRecords(userId, keepCount);
            return result >= 0;
        } catch (Exception e) {
            log.error("清理旧对话历史失败", e);
            return false;
        }
    }
    
    /**
     * 转换为VO对象
     */
    private AiChatHistoryVO convertToVO(AiChatHistory history) {
        AiChatHistoryVO vo = new AiChatHistoryVO();
        BeanUtils.copyProperties(history, vo);
        return vo;
    }
}
