package com.tju.elm_bk.controller;

import com.tju.elm_bk.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "通用接口")
@RestController
@RequestMapping("/api")
public class CommonController {
    @Operation(summary = "修改用户密码",description = "目前仅占位，让apifox上有这个文件夹，还没实现")
    @PostMapping("/password")
    public User getUserByIdByPass() {
        return null;
    }

}
