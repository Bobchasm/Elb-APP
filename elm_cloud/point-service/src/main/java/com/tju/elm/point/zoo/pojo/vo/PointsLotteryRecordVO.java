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
@Schema(description = "积分抽奖记录VO")
public class PointsLotteryRecordVO {
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "抽奖时的会员等级 0-普通 1-白银 2-黄金 3-钻石")
    private Integer memberLevel;

    @Schema(description = "会员等级名称")
    private String memberLevelName;

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

    @Schema(description = "抽奖月份")
    private String lotteryMonth;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}

