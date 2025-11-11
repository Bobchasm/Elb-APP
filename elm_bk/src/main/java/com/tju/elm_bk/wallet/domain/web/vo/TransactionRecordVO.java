package com.tju.elm_bk.wallet.domain.web.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRecordVO {
    @Schema(description = "交易id")
    private Long id;
    @Schema(description = "交易类型 0-支付 1-收款 2-提现 3-充值")
    private Integer type;
    @Schema(description = "操作金额")
    private BigDecimal amount;
    @Schema(description = "手续费或奖励")
    private BigDecimal fee;
    @Schema(description = "交易时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private LocalDateTime create_time;
}
