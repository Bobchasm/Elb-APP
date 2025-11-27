package com.tju.elm_bk.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "减少积分DTO")
public class PointsDeductDTO {
    @Schema(description = "用户ID", required = true)
    private Long userId;

    @Schema(description = "积分数量", required = true)
    private Long points;

    @Schema(description = "积分来源 4-兑换商品 5-积分+现金消费", required = true)
    private Integer pointsSource;

    @Schema(description = "关联订单ID")
    private Long relatedOrderId;

    @Schema(description = "关联商品ID")
    private Long relatedFoodId;

    @Schema(description = "交易描述")
    private String description;
}

