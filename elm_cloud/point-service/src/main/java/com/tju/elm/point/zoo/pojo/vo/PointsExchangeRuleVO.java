package com.tju.elm.point.zoo.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "积分兑换规则VO")
public class PointsExchangeRuleVO {
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "规则名称")
    private String ruleName;

    @Schema(description = "规则类型 0-积分+现金 1-兑换商品")
    private Integer ruleType;

    @Schema(description = "规则类型名称")
    private String ruleTypeName;

    @Schema(description = "规则状态 0-禁用 1-启用")
    private Integer ruleStatus;

    @Schema(description = "兑换比例（如10表示10积分=1元）")
    private BigDecimal exchangeRatio;

    @Schema(description = "最小使用积分数量")
    private Long minPoints;

    @Schema(description = "最大使用积分数量")
    private Long maxPoints;

    @Schema(description = "商品ID")
    private Long foodId;

    @Schema(description = "所需积分")
    private Long requiredPoints;

    @Schema(description = "库存数量")
    private Integer stockQuantity;

    @Schema(description = "规则生效开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "规则生效结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

