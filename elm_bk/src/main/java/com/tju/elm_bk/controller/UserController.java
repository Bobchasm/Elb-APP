// UserController.java
package com.tju.elm_bk.controller;

import com.tju.elm_bk.service.UserService;
import com.tju.elm_bk.untity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/getUserByIdByPass")
    public User getUserByIdByPass(
            @RequestParam String userId,
            @RequestParam String password) {
        return userService.getUserByIdByPass(userId, password);
    }

    /**
     * UserController/getUserById
     * 参数：userId
     * 返回值：int（返回行数）
     * 功能：根据用户编号查询用户表返回的行数
     */
    @GetMapping("/getUserById")
    public Integer getUserById(@RequestParam String userId) {
        return userService.getUserById(userId);
    }

    /**
     * UserController/saveUser
     * 参数：userId、password、userName、userSex
     * 返回值：int（影响的行数）
     * 功能：向用户表中添加一条记录
     */
    @PostMapping("/saveUser")
    public Integer saveUser(@RequestBody User user) {
        return userService.saveUser(user);
    }
}
