package com.tju.elm_bk.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAddress {
    @Schema(description = "地址id")
    private Integer daId;
    @Schema(description = "联系人姓名")
    private String contactName;
    @Schema(description = "联系人性别")
    private Integer contactSex;
    @Schema(description = "联系电话")
    private String contactTel;
    @Schema(description = "地址")
    private String address;
    @Schema(description = "用户id")
    private String userId;

}
