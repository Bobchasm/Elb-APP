package com.tju.elm_bk.mapper;

import com.tju.elm_bk.entity.Cart;
import com.tju.elm_bk.entity.Food;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CartMapper {
    @Select("SELECT * FROM elm.food WHERE foodId=#{foodId}")
    public Food selectByFoodId(Integer foodId);

    @Select("SELECT * FROM elm.cart WHERE userId=#{userId}")
    public List<Cart> listCartByUserId(String userId);

    @Select("SELECT * FROM elm.cart WHERE userId=#{userId} AND businessId=#{businessId}")
    public List<Cart> listCartByUserIdAndBusinessId(String userId, int businessId);

    @Insert("INSERT INTO elm.cart (userId,businessId,foodId,quantity) VALUES (#{userId},#{businessId},#{foodId},1)")
    public int insertCart(String userId,int businessId,int foodId);

    @Update("UPDATE elm.cart set quantity=#{quantity} WHERE userId=#{userId} AND businessId=#{businessId} AND foodId=#{foodId}")
    public int updateCart(String userId,int businessId,int foodId,int quantity);

    @Delete("DELETE FROM elm.cart WHERE userId=#{userId} AND businessId=#{businessId} AND foodId=#{foodId}")
    public int deleteCartByFood(String userId,Integer businessId,Integer foodId);

    @Delete("DELETE FROM cart WHERE userId=#{userId} AND businessId=#{businessId}")
    public int deleteCartByBusinessId(String userId,Integer businessId);

    //下面两个供Orders接口使用
    @Select("SELECT * FROM elm.cart WHERE userId = #{userId} AND businessId = #{businessId}")
    List<Cart> listCart(@Param("userId") String userId,
                        @Param("businessId") Integer businessId);

    @Delete("DELETE FROM elm.cart WHERE userId = #{userId} AND businessId = #{businessId}")
    int removeCart(@Param("userId") String userId,
                   @Param("businessId") Integer businessId);
}
