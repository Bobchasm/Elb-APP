package com.tju.elm_bk.wallet.domain.infrastructure.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletVipQuery {
    private Integer id;
    @Schema(description = "vip名称")
    private String name;
    @Schema(description = "描述")
    private String description;
}
