package com.tju.elm_bk.entity;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailet {
    @Schema(description = "订单详情ID")
    private Long id;

    @Schema(description = "商品数量")
    private Integer quantity;

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

    @Schema(description = "所属订单ID")
    private Long orderId;

    @Schema(description = "商品ID")
    private Long foodId;

    // 关联字段
    @Schema(description = "所属订单")
    private Order order;

    @Schema(description = "商品信息")
    private Food food;
}
