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

    @GetMapping("/listCart")
    public List<Cart> listCart(@RequestParam String userId, @RequestParam Integer businessId)
    {
        return cartService.listCart(userId,businessId);
    }

    @GetMapping("/saveCart")
    public int saveCart(@RequestParam String userId, @RequestParam Integer businessId, @RequestParam Integer foodId)
    {
        if (userId == null || businessId == null || foodId == null) {
            return -1; // Return error code for invalid parameters
        }
        return cartService.saveCart(userId,businessId,foodId);
    }

    @GetMapping("/updateCart")
    public int updateCart(@RequestParam String userId, @RequestParam Integer businessId, @RequestParam Integer foodId, @RequestParam Integer quantity)
    {
        if (userId == null || businessId == null || foodId == null || quantity == null) {
            return -1; // Return error code for invalid parameters
        }
        return cartService.updateCart(userId,businessId,foodId,quantity);
    }

    @GetMapping("/removeCart")
    public int removeCart(@RequestParam String userId, @RequestParam Integer businessId, @RequestParam Integer foodId)
    {
        if (userId == null || businessId == null || foodId == null) {
            return -1; // Return error code for invalid parameters
        }
        return cartService.removeCart(userId,businessId,foodId);
    }
}
