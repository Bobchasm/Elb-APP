package com.tju.elm_bk.controller;

import com.tju.elm_bk.service.impl.CartServiceImpl;
import com.tju.elm_bk.entity.Cart;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "购物车接口")
@RestController
@RequestMapping("/CartController")
public class CartController {
    @Autowired
    CartServiceImpl cartService;

    @Operation(summary="获取用户购物车")
    @PostMapping("/listCart")
    public List<Cart> listCart(@RequestBody Cart cart)
    {
        return cartService.listCart(cart);
    }

    @Operation(summary = "将指定商品加入购物车")
    @PostMapping ("/saveCart")
    public int saveCart(@RequestBody Cart cart)
    {
        if (null==cart) {
            return -1; // Return error code for invalid parameters
        }
        return cartService.saveCart(cart);
    }

    @Operation(summary = "更新购物车")
    @PostMapping("/updateCart")
    public int updateCart(@RequestBody Cart cart)
    {
        if (null==cart) {
            return -1; // Return error code for invalid parameters
        }
        return cartService.updateCart(cart);
    }

    @Operation(summary = "移除购物车中指定商品")
    @PostMapping("/removeCart")
    public int removeCart(@RequestBody Cart cart)
    {
        if (null==cart) {
            return -1;
        }
        return cartService.removeCart(cart);
    }
}
