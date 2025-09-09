package com.tju.elm_bk.service.impl;

import com.tju.elm_bk.mapper.CartMapper;
import com.tju.elm_bk.service.CartService;
import com.tju.elm_bk.entity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    CartMapper cartMapper;

    @Override
    public List<Cart> listCart(Cart ca)
    {
        List<Cart> carts=new ArrayList<>();
        if(ca.getBusinessId()!=null)
            carts=cartMapper.listCartByUserIdAndBusinessId(ca.getUserId(),ca.getBusinessId());
        else
            carts=cartMapper.listCartByUserId(ca.getUserId());
        for(Cart cart:carts)
        {
            cart.setFood(cartMapper.selectByFoodId(cart.getFoodId()));
        }
        return carts;
    }

    @Override
    public int saveCart(Cart cart)
    {
        return cartMapper.insertCart(cart.getUserId(),cart.getBusinessId(),cart.getFoodId());
    }

    @Override
    public int updateCart(Cart cart)
    {
        return cartMapper.updateCart(cart.getUserId(),cart.getBusinessId(),cart.getFoodId(),cart.getQuantity());
    }

    @Override
    public int removeCart(Cart cart)
    {
        if(cart.getFoodId()!=null)
            return cartMapper.deleteCartByFood(cart.getUserId(),cart.getBusinessId(),cart.getFoodId());
        else
            return cartMapper.deleteCartByBusinessId(cart.getUserId(),cart.getBusinessId());
    }
}
