package com.tju.elm_bk.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantApplicationsVO {
    @Schema(description = "申请ID")
    private Integer id;
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "用户名")
    private String username;

}
