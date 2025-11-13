package com.tju.elm_bk.rich.domain.web.vo;


import com.tju.elm_bk.rich.entity.VirtualWalletLoan;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanVO extends VirtualWalletLoan {
    @Schema(description = "当前利息")
    private BigDecimal interestAmount;
}
