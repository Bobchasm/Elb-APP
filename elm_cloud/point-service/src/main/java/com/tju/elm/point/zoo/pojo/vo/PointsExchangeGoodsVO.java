package com.tju.elm.point.zoo.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 积分兑换商品VO
 * 包含商品信息和所需积分信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "积分兑换商品VO")
public class PointsExchangeGoodsVO {
    @Schema(description = "商品ID")
    private Long foodId;

    @Schema(description = "商品名称")
    private String foodName;

    @Schema(description = "商品价格")
    private BigDecimal foodPrice;

    @Schema(description = "商品说明")
    private String foodExplain;

    @Schema(description = "商品图片")
    private String foodImg;

    @Schema(description = "备注")
    private String remarks;

    @Schema(description = "所需积分")
    private Long requiredPoints;

    @Schema(description = "库存数量")
    private Integer stockQuantity;

    @Schema(description = "兑换规则ID")
    private Long ruleId;

    @Schema(description = "所属商家ID")
    private Long businessId;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}

