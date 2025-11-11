package com.tju.elm_bk.rich.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirtualWalletVipRule {
    private Integer id;
    @Schema(description = "vip名称")
    private String name;
    @Schema(description = "描述")
    private String description;
    @Schema(description = "逻辑删除")
    private Integer isDeleted;
}
