package com.tju.elm_bk.controller;

import com.tju.elm_bk.dto.CartItemCreateDTO;
import com.tju.elm_bk.dto.FoodCreateDTO;
import com.tju.elm_bk.result.HttpResult;
import com.tju.elm_bk.service.CartService;
import com.tju.elm_bk.vo.CartVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/carts")
@Tag(name="管理购物车")
public class CartController {
    @Autowired
    private CartService cartService;

    @PostMapping
    @Operation(summary = "向添加商品")
    public HttpResult<CartVO> addCartItem(@RequestBody CartItemCreateDTO cartItemCreateDTO) {
        return HttpResult.success(cartService.addCart(cartItemCreateDTO));
    }
}
