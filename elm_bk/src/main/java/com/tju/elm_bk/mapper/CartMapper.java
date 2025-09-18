package com.tju.elm_bk.mapper;

import com.tju.elm_bk.entity.Cart;
import com.tju.elm_bk.entity.Food;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CartMapper {

    void insertCart(Cart cart);

}
