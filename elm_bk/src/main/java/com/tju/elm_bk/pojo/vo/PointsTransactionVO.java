package com.tju.elm_bk.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "积分明细VO")
public class PointsTransactionVO {
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "交易类型 0-获得 1-消费 2-过期 3-冻结 4-解冻")
    private Integer transactionType;

    @Schema(description = "交易类型名称")
    private String transactionTypeName;

    @Schema(description = "积分来源 0-消费积分 1-促销积分 2-等级积分 3-行为积分 4-兑换商品 5-积分+现金消费")
    private Integer pointsSource;

    @Schema(description = "积分来源名称")
    private String pointsSourceName;

    @Schema(description = "积分变动数量（正数表示增加，负数表示减少）")
    private Long pointsChange;

    @Schema(description = "变动后积分余额")
    private Long pointsBalance;

    @Schema(description = "积分过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

    @Schema(description = "关联订单ID")
    private Long relatedOrderId;

    @Schema(description = "关联商品ID")
    private Long relatedFoodId;

    @Schema(description = "交易描述")
    private String description;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}

