package com.tju.elm.food.service;

import com.tju.elm.food.pojo.entity.Cart;
import com.tju.elm.food.pojo.vo.CartItemVO;
import com.tju.elm.food.pojo.vo.CartVO;

import java.util.List;

public interface CartService {

    List<CartItemVO> getCartItemList(Long businessId);

    Long addItem(Long foodId,Integer quantity);

    Long updateItem(Long cartId,Integer quantity);

    Long clearCart(Long businessId);

    Long removeItem(Long cartId);

    Cart getCartById(Long cartId);
}
