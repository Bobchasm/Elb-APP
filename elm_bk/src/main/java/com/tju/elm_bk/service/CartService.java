package com.tju.elm_bk.service;

import com.tju.elm_bk.dto.CartItemCreateDTO;
import com.tju.elm_bk.vo.CartItemVO;
import com.tju.elm_bk.vo.CartVO;
import io.swagger.v3.oas.models.links.Link;

import java.util.List;

public interface CartService {


    CartVO addCart(CartItemCreateDTO cartItemCreateDTO);



    List<CartItemVO> getCartItemList(Long businessId);

    Integer addItem(Long foodId,Integer quantity);

    Integer updateItem(Long cartId,Integer quantity);

    Integer clearCart(Long businessId);

    Integer removeItem(Long cartId);

}
