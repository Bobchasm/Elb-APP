package com.tju.elm_bk.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "抽奖奖品配置VO")
public class LotteryPrizeVO {
    @Schema(description = "奖品类型 0-没中奖 1-固定积分 2-积分翻倍")
    private Integer type;
    
    @Schema(description = "奖品类型名称")
    private String typeName;
    
    @Schema(description = "固定积分数量（仅type=1时有效）")
    private Long points;
    
    @Schema(description = "积分翻倍倍数（仅type=2时有效）")
    private java.math.BigDecimal multiplier;
    
    @Schema(description = "中奖概率（百分比，0-100）")
    private Integer probability;
    
    @Schema(description = "奖品描述")
    private String description;
}

