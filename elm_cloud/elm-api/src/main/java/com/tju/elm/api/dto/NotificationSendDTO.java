package com.tju.elm.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationSendDTO {
    private Long receiverId;
    private Integer type;
    private String text;
    private String auditResult;
}
