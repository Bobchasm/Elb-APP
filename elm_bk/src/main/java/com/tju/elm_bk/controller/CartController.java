package com.tju.elm_bk.controller;

import com.tju.elm_bk.service.CartService;
import com.tju.elm_bk.untity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/CartController")
public class CartController {
    @Autowired
    CartService cartService;

    @PostMapping("/listCart")
    public List<Cart> listCart(@RequestBody Cart cart)
    {
        return cartService.listCart(cart);
    }

    @PostMapping ("/saveCart")
    public int saveCart(@RequestBody Cart cart)
    {
        if (null==cart) {
            return -1; // Return error code for invalid parameters
        }
        return cartService.saveCart(cart);
    }

    @PostMapping("/updateCart")
    public int updateCart(@RequestBody Cart cart)
    {
        if (null==cart) {
            return -1; // Return error code for invalid parameters
        }
        return cartService.updateCart(cart);
    }

    @PostMapping("/removeCart")
    public int removeCart(@RequestBody Cart cart)
    {
        if (null==cart) {
            return -1; // Return error code for invalid parameters
        }
        return cartService.removeCart(cart);
    }
}
