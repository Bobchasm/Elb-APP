package com.tju.elm_bk.rich.domain.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletVipVO {
    private Integer id;
    @Schema(description = "vip名称")
    private String name;
    @Schema(description = "描述")
    private String description;
    @Schema(description = "申请费用/月")
    private BigDecimal cost;
}
