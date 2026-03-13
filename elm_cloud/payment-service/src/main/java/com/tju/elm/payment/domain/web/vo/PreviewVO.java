package com.tju.elm.payment.domain.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PreviewVO {
    @Schema(description = "操作金额")
    private BigDecimal amount;
    @Schema(description = "手续费或奖励")
    private BigDecimal fee;
    @Schema(description = "总金额")
    private BigDecimal total;
    @Schema(description = "手续费率或奖励率")
    private Float feeRate;
    @Schema(description = "对于提现，是否透支，无法提现超过余额的金额")
    private Boolean isOver;
}
