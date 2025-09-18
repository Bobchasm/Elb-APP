package com.tju.elm_bk.service.impl;

import com.tju.elm_bk.dto.CartItemCreateDTO;
import com.tju.elm_bk.entity.Cart;
import com.tju.elm_bk.service.CartService;
import com.tju.elm_bk.vo.CartVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CartServiceImpl implements CartService {

    @Override
    public CartVO addCart(CartItemCreateDTO cartItemCreateDTO) {
        Cart cart = new Cart();
        BeanUtils.copyProperties(cartItemCreateDTO, cart);
        cart.setBusinessId(cartItemCreateDTO.getBusiness().getId());
        cart.setFoodId(cartItemCreateDTO.getFood().getId());
        cart.setCustomerId(cartItemCreateDTO.getCustomer().getId());
        cart.setCreateTime(LocalDateTime.now());
        cart.setIsDeleted(false);
        return null;
    }
}
