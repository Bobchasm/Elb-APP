package com.tju.elm.order.zoo.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderVO {
    @Schema(description = "订单ID")
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "创建人ID")
    private Long creator;

    @Schema(description = "更新人ID")
    private Long updater;

    @JsonProperty("deleted")
    @Schema(description = "是否删除")
    private Boolean isDeleted;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "下单时间")
    private LocalDateTime orderDate;

    @Schema(description = "订单总金额")
    private BigDecimal orderTotal;

    @Schema(description = "订单状态（0-待支付，1-已支付，2-已取消，3-已完成）")
    private Integer orderState;


    @Schema(description = "使用积分数量")
    private Long pointsUsed;

    @Schema(description = "获得积分数量")
    private Long pointsAmount;

    @Schema(description = "积分抵扣的现金金额（元）")
    private BigDecimal pointsDiscountAmount;
}
