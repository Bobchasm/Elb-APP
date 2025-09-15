package com.tju.elm_bk.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "权限实体类")
public class Authority {
    @Schema(description = "权限名称")
    private String name;

    @Schema(description = "拥有该权限的用户列表")
    private List<User> users;
}
