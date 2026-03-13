package com.tju.elm.notification.service;

import com.tju.elm.notification.zoo.pojo.dto.NotificationSendDTO;
import com.tju.elm.notification.zoo.pojo.entity.Notification;

import java.util.List;

public interface NotificationService {
    List<Notification> list(Long userId);

    void readNotification(Long id);

    Long sendNotification(NotificationSendDTO notificationSendDTO);
}
