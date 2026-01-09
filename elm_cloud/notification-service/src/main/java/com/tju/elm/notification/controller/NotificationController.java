package com.tju.elm.notification.controller;

import com.tju.elm.notification.service.NotificationService;
import com.tju.elm.notification.zoo.pojo.dto.NotificationSendDTO;
import com.tju.elm.notification.zoo.pojo.dto.WebSocketPushDTO;
import com.tju.elm.notification.zoo.pojo.entity.Notification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import result.HttpResult;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name="消息管理", description = "获得消息列表与读取消息")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private com.tju.elm.notification.zoo.websocket.WebSocketServer webSocketServer;
    @GetMapping("/notifications")
    @Operation(summary = "获取消息列表")
    public HttpResult<List<Notification>> getNotifications(Long userId){
        return HttpResult.success(notificationService.list(userId));
    }

    @PutMapping("/notifications/{id}/read")
    @Operation(summary = "读取消息")
    public HttpResult readNotification(@PathVariable Long id){
        notificationService.readNotification(id);
        return HttpResult.success();
    }

    @PostMapping("/notifications/send")
    @Operation(summary = "发送消息")
    public HttpResult<Long> sendNotification(@RequestBody NotificationSendDTO notificationSendDTO) {
        return HttpResult.success(notificationService.sendNotification(notificationSendDTO));
    }

    @PostMapping("/notifications/push")
    @Operation(summary = "WebSocket推送消息", description = "通过WebSocket实时推送消息给指定用户或所有用户")
    public HttpResult<Void> pushMessage(@RequestBody WebSocketPushDTO pushDTO) {
        if (pushDTO.getUserId() != null) {
            // 推送给指定用户
            webSocketServer.sendToClient(pushDTO.getUserId().toString(), pushDTO.getMessage());
        } else {
            // 群发给所有用户
            webSocketServer.sendToAllClient(pushDTO.getMessage());
        }
        return HttpResult.success();
    }

}
