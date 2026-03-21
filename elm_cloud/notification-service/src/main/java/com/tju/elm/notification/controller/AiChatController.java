package com.tju.elm.notification.controller;

import com.tju.elm.api.client.UserClient;
import com.tju.elm.notification.service.AiChatService;
import com.tju.elm.notification.zoo.pojo.dto.AiChatRequestDTO;
import com.tju.elm.notification.zoo.pojo.vo.AiChatHistoryVO;
import com.tju.elm.notification.zoo.pojo.vo.AiChatResponseVO;
import com.tju.elm.notification.zoo.utils.AiKnowledgeBaseUtil;
import exception.APIException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import result.HttpResult;
import result.ResultCodeEnum;

@Slf4j
@RestController
@RequestMapping("/api/ai/chat")
@RequiredArgsConstructor
@Validated
@Tag(name = "AI智能客服", description = "提供AI智能客服对话相关的接口")
public class AiChatController {
    
    private final AiChatService aiChatService;
    private final AiKnowledgeBaseUtil knowledgeBaseUtil;

    private UserClient userClient;

    
    /**
     * AI聊天接口
     */
    @PostMapping
    @Operation(summary = "发送消息给AI客服", description = "用户发送消息给AI客服，获取智能回复")
    public HttpResult<AiChatResponseVO> chat(@Valid @RequestBody AiChatRequestDTO request) {
        try {
            // 如果请求中没有用户ID，尝试从安全上下文获取
            if (request.getUserId() == null) {
                Long currentUserId = knowledgeBaseUtil.getCurrentUser().getId();
                if (currentUserId != null) {
                    request.setUserId(currentUserId);
                }
            }

            log.info("AI聊天请求: userId={}, message={}, chatType={}",
                    request.getUserId(), request.getMessage(), request.getChatType());

            AiChatResponseVO response = aiChatService.chat(request);

            log.info("AI聊天响应: sessionId={}, processingTime={}ms",
                    response.getSessionId(), response.getProcessingTime());

            return HttpResult.success(response);

        } catch (Exception e) {
            log.error("AI聊天处理失败", e);
            throw new APIException(ResultCodeEnum.SERVER_ERROR);
        }
    }
    
    /**
     * 获取用户对话历史
     */
    @GetMapping("/history")
    @Operation(summary = "获取用户对话历史", description = "分页获取用户的AI对话历史记录")
    public HttpResult<List<AiChatHistoryVO>> getChatHistory(
            @Parameter(description = "用户ID，不传则使用当前登录用户")
            @RequestParam(required = false) Long userId,
            @Parameter(description = "页码，从1开始")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小，最大50")
            @RequestParam(defaultValue = "20") Integer size) {

        try {
            // 如果没有传入用户ID，使用当前登录用户
            if (userId == null) {
                userId = knowledgeBaseUtil.getCurrentUser().getId();
                if (userId == null) {
                    throw new APIException(ResultCodeEnum.UNAUTHORIZED);
                }
            }

            // 权限检查：只能查看自己的对话历史（除非是管理员）
            Long currentUserId = knowledgeBaseUtil.getCurrentUser().getId();
            if (currentUserId != null && !currentUserId.equals(userId)) {
                // 这里可以添加管理员权限检查
                throw new APIException(ResultCodeEnum.NOT_ENOUGH_PERMISSION);
            }

            List<AiChatHistoryVO> history = aiChatService.getChatHistory(userId, page, size);
            return HttpResult.success(history);

        } catch (APIException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取对话历史失败", e);
            throw new APIException(ResultCodeEnum.SERVER_ERROR);
        }
    }
    
    /**
     * 根据会话ID获取对话历史
     */
    @GetMapping("/history/session/{sessionId}")
    @Operation(summary = "根据会话ID获取对话历史", description = "获取指定会话的完整对话历史")
    public HttpResult<List<AiChatHistoryVO>> getChatHistoryBySession(
            @Parameter(description = "会话ID") 
            @PathVariable String sessionId) {
        
        try {
            if (sessionId == null || sessionId.trim().isEmpty()) {
                throw new APIException(ResultCodeEnum.PARAM_NOT_MATCHED);
            }
            
            List<AiChatHistoryVO> history = aiChatService.getChatHistoryBySession(sessionId);
            return HttpResult.success(history);
            
        } catch (APIException e) {
            throw e;
        } catch (Exception e) {
            log.error("根据会话ID获取对话历史失败", e);
            throw new APIException(ResultCodeEnum.SERVER_ERROR);
        }
    }
    
    /**
     * 删除对话历史
     */
    @DeleteMapping("/history/{historyId}")
    @Operation(summary = "删除对话历史", description = "删除指定的对话历史记录")
    public HttpResult<Boolean> deleteChatHistory(
            @Parameter(description = "对话历史ID")
            @PathVariable Long historyId) {

        try {
            if (historyId == null || historyId <= 0) {
                throw new APIException(ResultCodeEnum.PARAM_NOT_MATCHED);
            }

            Long currentUserId = knowledgeBaseUtil.getCurrentUser().getId();
            if (currentUserId == null) {
                throw new APIException(ResultCodeEnum.UNAUTHORIZED);
            }

            Boolean result = aiChatService.deleteChatHistory(historyId, currentUserId);
            return HttpResult.success(result);

        } catch (APIException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除对话历史失败", e);
            throw new APIException(ResultCodeEnum.SERVER_ERROR);
        }
    }
    
    /**
     * 清理用户的旧对话记录
     */
    @PostMapping("/history/clean")
    @Operation(summary = "清理旧对话记录", description = "清理用户的旧对话记录，保留最近的N条")
    public HttpResult<Boolean> cleanOldChatHistory(
            @Parameter(description = "保留的记录数量，默认50条")
            @RequestParam(defaultValue = "50") Integer keepCount) {

        try {
            Long currentUserId = knowledgeBaseUtil.getCurrentUser().getId();
            if (currentUserId == null) {
                throw new APIException(ResultCodeEnum.UNAUTHORIZED);
            }

            Boolean result = aiChatService.cleanOldChatHistory(currentUserId, keepCount);
            return HttpResult.success(result);

        } catch (APIException e) {
            throw e;
        } catch (Exception e) {
            log.error("清理旧对话记录失败", e);
            throw new APIException(ResultCodeEnum.SERVER_ERROR);
        }
    }
    
    /**
     * AI客服健康检查
     */
    @GetMapping("/health")
    @Operation(summary = "AI客服健康检查", description = "检查AI客服系统状态")
    public HttpResult<String> healthCheck() {
        try {
            // 这里可以添加一些简单的健康检查逻辑
            return HttpResult.success("AI客服系统运行正常 🤖");
        } catch (Exception e) {
            log.error("AI客服健康检查失败", e);
            throw new APIException(ResultCodeEnum.SERVER_ERROR);
        }
    }


}
