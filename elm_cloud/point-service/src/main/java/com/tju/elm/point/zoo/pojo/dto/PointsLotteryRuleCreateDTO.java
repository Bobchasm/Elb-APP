package com.tju.elm.point.zoo.pojo.dto;

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
@Schema(description = "创建积分抽奖规则DTO")
public class PointsLotteryRuleCreateDTO {
    @Schema(description = "规则名称", required = true)
    private String ruleName;

    @Schema(description = "适用会员等级 1-白银 2-黄金 3-钻石", required = true)
    private Integer memberLevel;

    @Schema(description = "奖品类型 0-没中奖 1-固定积分 2-积分翻倍", required = true)
    private Integer prizeType;

    @Schema(description = "固定积分数量（仅prize_type=1时有效）")
    private Long prizePoints;

    @Schema(description = "积分翻倍倍数（仅prize_type=2时有效，如2.0表示翻倍）")
    private BigDecimal prizeMultiplier;

    @Schema(description = "中奖概率（百分比，0-100）", required = true)
    private Integer probability;

    @Schema(description = "奖品排序（数字越小越靠前）")
    private Integer prizeOrder;

    @Schema(description = "奖品描述")
    private String prizeDescription;

    @Schema(description = "规则状态 0-禁用 1-启用")
    private Integer ruleStatus;

    @Schema(description = "规则生效开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "规则生效结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}

