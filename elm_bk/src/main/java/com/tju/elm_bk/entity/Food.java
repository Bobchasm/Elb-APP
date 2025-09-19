package com.tju.elm_bk.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.swing.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Food {
    @Schema(description = "商品id")
    private Integer foodId;
    @Schema(description = "商品名")
    private String foodName;
    @Schema(description = "商品描述")
    private String foodExplain;
    @Schema(description = "商品图片")
    private String foodImg;
    @Schema(description = "商品单价")
    private Double foodPrice;
    @Schema(description = "商家id")
    private Integer businessId;
    @Schema(description = "备注")
    private String remarks;

}
