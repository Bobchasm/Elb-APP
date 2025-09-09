package com.tju.elm_bk.service;

import com.tju.elm_bk.entity.Cart;

import java.util.List;

public interface CartService {

    List<Cart> listCart(Cart ca);

    int saveCart(Cart cart);

    int updateCart(Cart cart);

    int removeCart(Cart cart);
}
