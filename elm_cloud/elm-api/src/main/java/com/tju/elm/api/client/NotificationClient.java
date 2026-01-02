package com.tju.elm.api.client;

import com.tju.elm.api.dto.NotificationSendDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import result.HttpResult;

@FeignClient("notification-service")
public interface NotificationClient {
    @PostMapping("/api/notifications/send")
    HttpResult<Long> sendNotification(@RequestBody NotificationSendDTO notificationSendDTO);

}
