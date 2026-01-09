package com.tju.elm.notification.zoo.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "WebSocket推送消息DTO")
public class WebSocketPushDTO {
    
    @Schema(description = "用户ID(不传则群发给所有用户)")
    private Long userId;
    
    @Schema(description = "消息内容(JSON格式)", required = true)
    private String message;
}
