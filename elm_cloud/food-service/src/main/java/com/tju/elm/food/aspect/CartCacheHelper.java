package com.tju.elm.food.aspect;

import com.tju.elm.food.mapper.FoodMapper;
import com.tju.elm.food.pojo.entity.Cart;
import com.tju.elm.food.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CartCacheHelper {

    private final FoodMapper foodMapper;
    private final CartService cartService;

    /**
     * 通过foodId获取businessId
     */
    public Long getBusinessIdByFoodId(Long foodId) {
        try {
            return foodMapper.selectFoodBusinessId(foodId);
        } catch (Exception e) {
            log.warn("通过foodId获取businessId失败: {}", foodId, e);
            return null;
        }
    }

    /**
     * 通过cartId获取businessId
     */
    public Long getBusinessIdByCartId(Long cartId) {
        try {
            Cart cart = cartService.getCartById(cartId);
            return cart != null ? cart.getBusinessId() : null;
        } catch (Exception e) {
            log.warn("通过cartId获取businessId失败: {}", cartId, e);
            return null;
        }
    }
}