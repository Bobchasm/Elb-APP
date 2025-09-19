package com.tju.elm_bk.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    @Schema(description = "购物车id")
    private Integer cartId;
    @Schema(description = "商品id")
    private Integer foodId;
    @Schema(description = "商家id")
    private Integer businessId;
    @Schema(description = "用户id")
    private String userId;
    @Schema(description = "商品数量")
    private Integer quantity;

    @Schema(description = "商品信息")
    private Food food;
    @Schema(description = "商家信息")
    private Business business;

}
