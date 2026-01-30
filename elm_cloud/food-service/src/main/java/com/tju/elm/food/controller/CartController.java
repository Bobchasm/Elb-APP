package com.tju.elm.food.controller;

import cn.hutool.core.thread.ThreadUtil;
import com.tju.elm.food.annotation.CartCacheEvict;
import com.tju.elm.food.annotation.CartCacheable;
import com.tju.elm.food.aspect.CartCacheHelper;
import com.tju.elm.food.mapper.FoodMapper;
import com.tju.elm.food.pojo.vo.CartItemVO;
import com.tju.elm.food.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import result.HttpResult;
import utils.UserContext;

import java.util.List;

@RestController
@RequestMapping("/api/carts")
@Tag(name="管理购物车")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final CartCacheHelper cartCacheHelper;

    @GetMapping("/list")
    @Operation(summary = "获取用户在指定商家的购物车商品列表")
    @CartCacheable(businessIdSpEL = "#businessId")
    public HttpResult<List<CartItemVO>> listCartItem(@RequestParam Long businessId) {
        return HttpResult.success(cartService.getCartItemList(businessId));
    }

    @GetMapping("/add")
    @Operation(summary = "向购物车添加商品")
    @CartCacheEvict(businessIdSpEL = "@cartCacheHelper.getBusinessIdByFoodId(#foodId)")
    public HttpResult<Long> addCartItem(@RequestParam Long foodId, @RequestParam Integer quantity) {
//        ThreadUtil.sleep(1000);
        return HttpResult.success(cartService.addItem(foodId, quantity));
    }

    @GetMapping("/quantity")
    @Operation(summary = "修改购物车指定商品数量")
    @CartCacheEvict(businessIdSpEL = "@cartCacheHelper.getBusinessIdByCartId(#cartId)")
    public HttpResult<Long> updateItemQuantity(@RequestParam Long cartId, @RequestParam Integer quantity) {
        return HttpResult.success(cartService.updateItem(cartId, quantity));
    }

    @GetMapping("/clear")
    @Operation(summary = "清空用户在指定商家的购物车")
    @CartCacheEvict(businessIdSpEL = "#businessId")
    public HttpResult<Long> clearCart(@RequestParam Long businessId) {
        return HttpResult.success(cartService.clearCart(businessId));
    }

    @GetMapping("/remove")
    @Operation(summary = "移除指定购物车商品")
    @CartCacheEvict(businessIdSpEL = "@cartCacheHelper.getBusinessIdByCartId(#cartId)")
    public HttpResult<Long> removeItem(@RequestParam Long cartId) {
        return HttpResult.success(cartService.removeItem(cartId));
    }
}