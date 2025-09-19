package com.tju.elm_bk.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Schema(description = "用户id")
    private String userId;
    @Schema(description = "用户密码")
    private String password;
    @Schema(description = "用户名")
    private String userName;
    @Schema(description = "用户性别")
    private Integer userSex;
    @Schema(description = "用户头像")
    private String userImg;
    @Schema(description = "逻辑删除")
    private Integer delTag;
}
