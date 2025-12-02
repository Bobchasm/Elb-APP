package com.tju.elm_bk.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "积分抽奖记录实体")
public class PointsLotteryRecord {
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用户ID（关联users表）")
    private Long userId;

    @Schema(description = "抽奖时的会员等级 0-普通 1-白银 2-黄金 3-钻石")
    private Integer memberLevel;

    @Schema(description = "抽奖类型 0-没中奖 1-固定积分 2-积分翻倍")
    private Integer lotteryType;

    @Schema(description = "获得的积分数量（没中奖时为0，积分翻倍时为NULL）")
    private Long pointsReward;

    @Schema(description = "积分翻倍倍数（仅积分翻倍时使用）")
    private BigDecimal pointsMultiplier;

    @Schema(description = "翻倍前的积分数量（仅积分翻倍时使用）")
    private Long originalPoints;

    @Schema(description = "抽奖月份（格式：YYYY-MM，用于统计每月抽奖次数）")
    private String lotteryMonth;

    @Schema(description = "关联积分明细ID（关联points_transaction表，中奖时记录）")
    private Long transactionId;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "创建人ID")
    private Long creator;

    @Schema(description = "是否删除")
    @JsonProperty("deleted")
    private Boolean isDeleted;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "更新人ID")
    private Long updater;
}

