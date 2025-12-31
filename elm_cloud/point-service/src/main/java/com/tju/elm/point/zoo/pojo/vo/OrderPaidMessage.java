package com.tju.elm.point.zoo.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "订单支付完成消息DTO")
public class OrderPaidMessage {
    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "订单金额")
    private BigDecimal orderAmount;

    @Schema(description = "订单日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderDate;

    @Schema(description = "商品ID列表")
    private List<Long> foodIds;

    @Schema(description = "商品详情列表")
    private List<OrderFoodDetail> foodDetails;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "订单商品详情")
    public static class OrderFoodDetail {
        @Schema(description = "商品ID")
        private Long foodId;

        @Schema(description = "商品价格")
        private BigDecimal foodPrice;

        @Schema(description = "商品数量")
        private Integer quantity;
    }
}

