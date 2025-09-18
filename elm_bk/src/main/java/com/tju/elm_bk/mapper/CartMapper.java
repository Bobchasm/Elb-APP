package com.tju.elm_bk.mapper;

import com.tju.elm_bk.entity.Cart;
import com.tju.elm_bk.entity.Food;
import com.tju.elm_bk.vo.CartVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CartMapper {

    void insertCart(Cart cart);

    @Select("""
        SELECT * FROM cart C WHERE C.id = #{cartId} AND C.is_deleted = 0
    """)
    CartVO selectCart(Long cartId);


}
