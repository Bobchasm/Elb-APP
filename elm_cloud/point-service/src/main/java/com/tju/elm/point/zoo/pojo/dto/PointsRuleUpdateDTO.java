package com.tju.elm.point.zoo.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "更新积分规则DTO")
public class PointsRuleUpdateDTO {
    @Schema(description = "规则名称")
    private String ruleName;

    @Schema(description = "规则状态 0-禁用 1-启用")
    private Integer ruleStatus;

    @Schema(description = "积分比例")
    private BigDecimal pointsRatio;

    @Schema(description = "积分倍数")
    private BigDecimal pointsMultiplier;

    @Schema(description = "适用会员等级")
    private Integer memberLevel;

    @Schema(description = "最低订单金额")
    private BigDecimal minOrderAmount;

    @Schema(description = "最高订单金额")
    private BigDecimal maxOrderAmount;

    @Schema(description = "指定商品ID列表（多个商品）")
    private List<Long> foodIds;

    @Schema(description = "节假日开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate holidayStart;

    @Schema(description = "节假日结束日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate holidayEnd;

    @Schema(description = "行为类型")
    private String behaviorType;

    @Schema(description = "固定积分数量")
    private Long pointsAmount;

    @Schema(description = "积分有效期（天数）")
    private Integer expireDays;

    @Schema(description = "规则生效开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "规则生效结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(description = "优先级")
    private Integer priority;
}

