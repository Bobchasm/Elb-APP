package com.tju.elm_bk.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "积分到期预警配置实体")
public class PointsExpirationAlertConfig {
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "提前预警天数（如7表示提前7天预警）")
    private Integer alertDays;

    @Schema(description = "预警周期（天数，如每天、每3天）。NULL表示只预警一次")
    private Integer alertCycle;

    @Schema(description = "短信模板（支持变量：{username}, {points}, {expireDate}）")
    private String smsTemplate;

    @Schema(description = "是否启用 0-禁用 1-启用")
    private Boolean isEnabled;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

