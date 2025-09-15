package com.tju.elm_bk.controller;

import com.tju.elm_bk.entity.LoginRequest;
import com.tju.elm_bk.result.Result;
import com.tju.elm_bk.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "登录认证接口")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Result login(@RequestBody LoginRequest loginRequest,
                        @RequestHeader("userType") String userType) {
        return authService.login(loginRequest, userType);
    }
}
