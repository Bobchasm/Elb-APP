package com.tju.elm.user.controller;

import com.tju.elm.user.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import result.HttpResult;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "管理端数据看板", description = "管理端数据看板")
public class AdminController {
    @Autowired
    private UserMapper userMapper;
//    @Autowired
//    private BusinessMapper businessMapper;
//    @Autowired
//    private OrdersMapper ordersMapper;

    @GetMapping("/countUser")
    @Operation(summary = "获取总用户数", description = "获取总用户数")
    public HttpResult<Integer> countUser(){
        return HttpResult.success(userMapper.count());
    }

//    @GetMapping("/countBusiness")
//    @Operation(summary = "获取总店铺数", description = "获取总店铺数")
//    public HttpResult<Integer> countBusiness(){
//        return HttpResult.success(businessMapper.count());
//    }
//
//    @GetMapping("/countPrice")
//    @Operation(summary = "获取总营业额", description = "获取总营业额")
//    public HttpResult<Double> countPrice(){
//        return HttpResult.success(ordersMapper.countPrice());
//    }
}
