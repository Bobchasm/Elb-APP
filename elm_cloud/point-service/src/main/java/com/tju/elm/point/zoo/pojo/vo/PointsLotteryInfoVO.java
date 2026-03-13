package com.tju.elm.point.zoo.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "积分抽奖信息VO")
public class PointsLotteryInfoVO {
    @Schema(description = "当前会员等级 0-普通 1-白银 2-黄金 3-钻石")
    private Integer memberLevel;

    @Schema(description = "会员等级名称")
    private String memberLevelName;

    @Schema(description = "每月抽奖次数限制")
    private Integer monthlyLimit;

    @Schema(description = "本月已抽奖次数")
    private Integer usedChances;

    @Schema(description = "剩余抽奖次数")
    private Integer remainingChances;

    @Schema(description = "是否可以抽奖")
    private Boolean canLottery;
    
    @Schema(description = "当前会员等级对应的奖池配置")
    private List<LotteryPrizeVO> prizes;
}

