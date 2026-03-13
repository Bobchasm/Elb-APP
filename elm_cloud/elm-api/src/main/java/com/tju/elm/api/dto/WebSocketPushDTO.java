package com.tju.elm.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketPushDTO {
    /**
     * 用户ID (不传则群发给所有用户)
     */
    private Long userId;
    
    /**
     * 消息内容 (JSON格式)
     */
    private String message;
}
