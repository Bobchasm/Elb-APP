package com.tju.elm_bk.service;

import com.tju.elm_bk.dto.CartItemCreateDTO;
import com.tju.elm_bk.vo.CartVO;

public interface CartService {


    CartVO addCart(CartItemCreateDTO cartItemCreateDTO);

}
