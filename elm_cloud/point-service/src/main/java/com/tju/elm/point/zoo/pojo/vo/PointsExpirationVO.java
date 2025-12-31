package com.tju.elm.point.zoo.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 积分过期记录VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "积分过期记录VO")
public class PointsExpirationVO {
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "积分明细ID")
    private Long transactionId;

    @Schema(description = "即将过期积分数量")
    private Long pointsAmount;

    @Schema(description = "过期时间（精确到秒）")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

    @Schema(description = "过期日期（用于按日期查询）")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expireDate;

    @Schema(description = "是否已过期 0-未过期 1-已过期")
    private Boolean isExpired;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @Schema(description = "距离过期的天数")
    private Long daysUntilExpiration;
}

