package com.tju.elm_bk.service;

import com.tju.elm_bk.mapper.CartMapper;
import com.tju.elm_bk.untity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {
    @Autowired
    CartMapper cartMapper;

    public List<Cart> listCart(String userId,Integer businessId)
    {
        List<Cart> carts=new ArrayList<>();
        if(businessId!=null)
            carts=cartMapper.listCartByUserIdAndBusinessId(userId,businessId);
        else
            carts=cartMapper.listCartByUserId(userId);
        for(Cart cart:carts)
        {
            cart.setFood(cartMapper.selectByFoodId(cart.getFoodId()));
        }
        return carts;
    }

    public int saveCart(String userId,int businessId,int foodId)
    {
        return cartMapper.insertCart(userId,businessId,foodId);
    }

    public int updateCart(String userId,int businessId,int foodId,int quantity)
    {
        return cartMapper.updateCart(userId,businessId,foodId,quantity);
    }

    public int removeCart(String userId,Integer businessId,Integer foodId)
    {
        if(foodId!=null)
            return cartMapper.deleteCartByFood(userId,businessId,foodId);
        else
            return cartMapper.deleteCartByBusinessId(userId,businessId);
    }
}
