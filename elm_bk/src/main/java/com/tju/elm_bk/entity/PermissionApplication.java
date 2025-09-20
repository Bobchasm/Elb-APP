package com.tju.elm_bk.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionApplication {
    @Schema(description = "申请ID")
    private Integer id;

    @Schema(description = "申请人用户ID（关联users表）")
    private Long userId;

    @Schema(description = "申请状态（0-未审核，1-同意，2-拒绝）")
    private Integer status = 0; // 默认未审核

    @Schema(description = "逻辑删除")
    private Boolean isDeleted = false;
}
