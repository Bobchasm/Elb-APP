package com.tju.elm.notification.zoo.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.swing.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationSendDTO {
    @Schema(description = "接收者id")
    private Long receiverId;
    @Schema(description = "消息类型")
    private Integer type;
    @Schema(description = "消息内容")
    private String text;
    @Schema(description = "审批结果")
    private String auditResult;
}
