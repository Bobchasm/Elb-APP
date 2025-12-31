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
@Schema(description = "积分抽奖结果VO")
public class PointsLotteryResultVO {
    @Schema(description = "抽奖记录ID")
    private Long recordId;

    @Schema(description = "抽奖类型 0-没中奖 1-固定积分 2-积分翻倍")
    private Integer lotteryType;

    @Schema(description = "抽奖类型名称")
    private String lotteryTypeName;

    @Schema(description = "获得的积分数量")
    private Long pointsReward;

    @Schema(description = "积分翻倍倍数")
    private BigDecimal pointsMultiplier;

    @Schema(description = "翻倍前的积分数量")
    private Long originalPoints;

    @Schema(description = "描述抽奖结果")
    private String description;

    @Schema(description = "抽奖时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "剩余抽奖次数")
    private Integer remainingChances;
}

