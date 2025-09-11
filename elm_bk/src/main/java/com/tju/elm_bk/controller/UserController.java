package com.tju.elm_bk.controller;

import com.tju.elm_bk.result.Result;
import com.tju.elm_bk.service.UserService;
import com.tju.elm_bk.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户接口")
@RestController
@RequestMapping("/UserController")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * UserController/getUserByIdByPass
     * 参数：userId、password
     * 返回值：User
     * 功能：根据用户编号和密码查询用户表返回的行数
     */
    @Operation(summary = "根据用户编号和密码查询用户")
    @PostMapping("/getUserByIdByPass")
    public User getUserByIdByPass(
            @RequestBody User user) {
        return userService.getUserByIdByPass(user);
    }

    /**
     * UserController/getUserById
     * 参数：userId
     * 返回值：int（返回行数）
     * 功能：根据用户编号查询用户表返回的行数
     */
    @Operation(summary = "根据用户id获取用户")
    @PostMapping("/getUserById")
    public Integer getUserById(@RequestBody User user) {
        return userService.getUserById(user);
    }

    /**
     * UserController/saveUser
     * 参数：userId、password、userName、userSex
     * 返回值：int（影响的行数）
     * 功能：向用户表中添加一条记录
     */
    @Operation(summary = "创建用户")
    @PostMapping("/saveUser")
    public Result saveUser(@RequestBody User user) {
        return userService.saveUser(user);
    }


    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result logout() {
        return userService.logout();
    }
}
