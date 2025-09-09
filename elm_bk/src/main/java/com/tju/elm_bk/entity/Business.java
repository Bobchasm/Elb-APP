package com.tju.elm_bk.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Business {
    @Schema(description = "商家id")
    private Integer businessId;
    @Schema(description = "商家名称")
    private String businessName;
    @Schema(description = "商家地址")
    private String businessAddress;
    @Schema(description = "商家描述")
    private String businessExplain;
    @Schema(description = "商家图片")
    private String businessImg;
    @Schema(description = "商家的商品类型")
    private Integer orderTypeId;
    @Schema(description = "起送费")
    private double starPrice;
    @Schema(description = "配送费")
    private double deliveryPrice;
    @Schema(description = "备注")
    private String remarks;
    @Schema(description = "月销量")
    private int monthSales;
    @Schema(description = "联系电话")
    private String phoneNumber;
    @Schema(description = "密码")
    private String password;

}