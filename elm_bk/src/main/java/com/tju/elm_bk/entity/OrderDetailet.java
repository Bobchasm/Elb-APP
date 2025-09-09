package com.tju.elm_bk.entity;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailet {
    @Schema(description = "订单详细信息id")
    private Integer odId;
    @Schema(description = "订单id")
    private Integer orderId;
    @Schema(description = "商品id")
    private Integer foodId;
    @Schema(description = "数量")
    private Integer quantity;
    @Schema(description = "商品价格")
    private Double foodPrice;
    @Schema(description = "商品名称")
    private String foodName;
    @Schema(description = "商品详细信息")
    private Food food;
}
