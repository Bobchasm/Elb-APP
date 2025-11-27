package com.tju.elm_bk.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "积分兑换DTO")
public class PointsExchangeDTO {
    @Schema(description = "商品ID", required = true)
    private Long foodId;

    @Schema(description = "兑换数量", required = true)
    private Integer quantity;
    
    @Schema(description = "配送地址ID", required = true)
    private Long addressId;
}

