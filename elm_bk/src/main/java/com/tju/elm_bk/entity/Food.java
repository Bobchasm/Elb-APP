package com.tju.elm_bk.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.swing.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Food {
    @Schema(description = "食品ID")
    private Long id;

    @Schema(description = "食品名称")
    private String foodName;

    @Schema(description = "食品价格")
    private BigDecimal foodPrice;

    @Schema(description = "食品说明")
    private String foodExplain;

    @Schema(description = "食品图片")
    private String foodImg;

    @Schema(description = "备注")
    private String remarks;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "创建人ID")
    private Long creator;

    @Schema(description = "是否删除")
    private Boolean isDeleted;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "更新人ID")
    private Long updater;

    @Schema(description = "所属商家ID")
    private Long businessId;

//    // 关联字段
//    @Schema(description = "所属商家")
//    private Business business;
//
//    @Schema(description = "关联的订单详情列表")
//    private List<OrderDetailet> orderDetailets;
//
//    @Schema(description = "关联的购物车列表")
//    private List<Cart> carts;
}
