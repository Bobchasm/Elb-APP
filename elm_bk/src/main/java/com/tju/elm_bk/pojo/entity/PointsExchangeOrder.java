package com.tju.elm_bk.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "积分兑换订单实体")
public class PointsExchangeOrder {
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用户ID（关联users表）")
    private Long userId;

    @Schema(description = "关联订单ID（关联orders表）")
    private Long orderId;

    @Schema(description = "兑换商品ID（关联food表）")
    private Long foodId;

    @Schema(description = "使用积分数量")
    private Long pointsUsed;

    @Schema(description = "现金金额（积分+现金消费时使用）")
    private BigDecimal cashAmount;

    @Schema(description = "状态 0-待处理 1-已完成 2-已取消")
    private Integer status;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "是否删除")
    @JsonProperty("deleted")
    private Boolean isDeleted;
}

