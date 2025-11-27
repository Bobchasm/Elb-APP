package com.tju.elm_bk.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "积分到期预警记录实体")
public class PointsExpirationAlertLog {
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用户ID（关联users表）")
    private Long userId;

    @Schema(description = "即将过期积分数量")
    private Long pointsAmount;

    @Schema(description = "过期日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expireDate;

    @Schema(description = "预警时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime alertTime;

    @Schema(description = "下次预警时间（根据预警周期计算）")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime nextAlertTime;

    @Schema(description = "是否已发送 0-未发送 1-已发送")
    private Boolean isSent;

    @Schema(description = "用户手机号（发送短信时使用）")
    private String phone;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}

