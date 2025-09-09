package com.tju.elm_bk.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Orders {
    @Schema(description = "订单id")
    private Integer orderId;
    @Schema(description = "用户id")
    private String userId;
    @Schema(description = "商家id")
    private Integer businessId;
    @Schema(description = "下单时间")
    private String orderDate;
    @Schema(description = "订单总价")
    private Double orderTotal;
    @Schema(description = "配送地址id")
    private Integer daId;
    @Schema(description = "订单状态（0：未支付； 1：已支付）")
    private Integer orderState;
    @Schema(description = "配送费")
    private double deliveryPrice;
    @Schema(description = "下单商家")
    private Business business;
    @Schema(description = "订单商品详情")
    private List<OrderDetailet> list;
}
