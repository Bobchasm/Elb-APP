package com.tju.elm_bk.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "积分明细实体")
public class PointsTransaction {
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用户ID（关联users表）")
    private Long userId;

    @Schema(description = "积分账户ID（关联points_account表）")
    private Long accountId;

    @Schema(description = "交易类型 0-获得 1-消费 2-过期 3-冻结 4-解冻")
    private Integer transactionType;

    @Schema(description = "积分来源 0-消费积分 1-促销积分 2-等级积分 3-行为积分 4-兑换商品 5-积分+现金消费")
    private Integer pointsSource;

    @Schema(description = "积分变动数量（正数表示增加，负数表示减少）")
    private Long pointsChange;

    @Schema(description = "变动后积分余额")
    private Long pointsBalance;

    @Schema(description = "积分过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

    @Schema(description = "关联订单ID（关联orders表）")
    private Long relatedOrderId;

    @Schema(description = "关联商品ID（关联food表，兑换商品时使用）")
    private Long relatedFoodId;

    @Schema(description = "关联积分规则ID（关联marketing_points_rule表）")
    private Long relatedRuleId;

    @Schema(description = "交易描述")
    private String description;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "创建人ID")
    private Long creator;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "更新人ID")
    private Long updater;

    @Schema(description = "是否删除")
    @JsonProperty("deleted")
    private Boolean isDeleted;
}

