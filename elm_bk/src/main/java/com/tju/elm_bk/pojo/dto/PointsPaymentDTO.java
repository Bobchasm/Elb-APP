package com.tju.elm_bk.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "积分支付DTO")
public class PointsPaymentDTO {
    @Schema(description = "订单ID", required = true)
    private Long orderId;

    @Schema(description = "使用积分数量", required = true)
    private Long pointsUsed;

    @Schema(description = "现金金额（订单总金额减去积分抵扣后的金额）")
    private BigDecimal cashAmount;
}

