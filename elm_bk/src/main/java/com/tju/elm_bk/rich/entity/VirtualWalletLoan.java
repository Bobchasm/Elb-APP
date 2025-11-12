package com.tju.elm_bk.rich.entity;

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
public class VirtualWalletLoan {
    private Long id;
    @Schema(description = "虚拟钱包id")
    private Long walletId;
    @Schema(description = "贷款时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private LocalDateTime createTime;
    @Schema(description = "还款时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private LocalDateTime repayTime;
    @Schema(description = "金额")
    private BigDecimal loanAmount;

}
