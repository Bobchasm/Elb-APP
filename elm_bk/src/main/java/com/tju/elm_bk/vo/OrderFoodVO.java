package com.tju.elm_bk.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderFoodVO {
    @Schema(description = "商品id")
    private Long foodId;

    @Schema(description = "商品数量")
    private Integer quantity;
}
