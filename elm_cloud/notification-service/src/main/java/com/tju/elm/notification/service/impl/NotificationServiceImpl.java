package com.tju.elm.notification.service.impl;

import com.tju.elm.notification.mapper.NotificationMapper;
import com.tju.elm.notification.service.NotificationService;
import com.tju.elm.notification.zoo.pojo.dto.NotificationSendDTO;
import com.tju.elm.notification.zoo.pojo.entity.Notification;
import com.tju.elm.notification.zoo.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private WebSocketServer webSocketServer;

    @Override
    public List<Notification> list(Long userId) {
        log.info("获取用户{}的通知列表", userId);
        return notificationMapper.list(userId);
    }

    @Override
    public void readNotification(Long id) {
        notificationMapper.updateRead(id, LocalDateTime.now());
    }

    @Override
    public Long sendNotification(NotificationSendDTO notificationSendDTO) {
        Notification notification = new Notification();
        notification.setUserId(notificationSendDTO.getReceiverId());
        notification.setNotificationType(notificationSendDTO.getType());
        notification.setNotificationContent(notificationSendDTO.getText());
        notification.setAuditResult(notification.getAuditResult());
        notification.setIsRead(0);
        notification.setIsDeleted(0);
        notification.setCreateTime(LocalDateTime.now());
        notificationMapper.insert(notification);
        return notification.getId();
    }
}
