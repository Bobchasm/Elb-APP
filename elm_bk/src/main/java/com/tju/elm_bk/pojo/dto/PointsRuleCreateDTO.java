package com.tju.elm_bk.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "创建积分规则DTO")
public class PointsRuleCreateDTO {
    @Schema(description = "规则名称", required = true)
    private String ruleName;

    @Schema(description = "规则类型 0-消费积分 1-促销积分 2-等级积分 3-行为积分", required = true)
    private Integer ruleType;

    @Schema(description = "规则状态 0-禁用 1-启用")
    private Integer ruleStatus;

    @Schema(description = "积分比例（如0.1表示消费1元获得0.1积分）")
    private BigDecimal pointsRatio;

    @Schema(description = "积分倍数（促销时使用，如2.0表示双倍积分）")
    private BigDecimal pointsMultiplier;

    @Schema(description = "适用会员等级（NULL表示所有等级）")
    private Integer memberLevel;

    @Schema(description = "最低订单金额")
    private BigDecimal minOrderAmount;

    @Schema(description = "最高订单金额")
    private BigDecimal maxOrderAmount;

    @Schema(description = "指定商品ID")
    private Long foodId;

    @Schema(description = "节假日开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate holidayStart;

    @Schema(description = "节假日结束日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate holidayEnd;

    @Schema(description = "行为类型（like-点赞 collect-收藏 repay_loan-还贷款）")
    private String behaviorType;

    @Schema(description = "固定积分数量（行为积分使用）")
    private Long pointsAmount;

    @Schema(description = "积分有效期（天数，NULL表示永久有效）")
    private Integer expireDays;

    @Schema(description = "规则生效开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "规则生效结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(description = "优先级（数字越大优先级越高）")
    private Integer priority;
}

