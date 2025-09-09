package com.tju.elm_bk.mapper;
import java.util.List;

import com.tju.elm_bk.entity.Food;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FoodMapper {
    @Select("""
    select 
    foodId,foodName,foodExplain,foodImg,foodPrice,businessId,remarks 
    from elm.food where businessId=#{businessId}
    """)
    List<Food> listFoodByBusinessId(Integer businessId);

    @Select("""
    select
    foodId,foodName,foodExplain,foodImg,foodPrice,businessId,remarks
    from elm.food where foodId=#{foodId}
            """)
    Food getFoodById(Integer foodId);

    @Insert("INSERT INTO food (foodName,foodExplain,foodImg,foodPrice,businessId) VALUES (#{foodName},#{foodExplain},#{foodImg},#{foodPrice},#{businessId})")
    public int insertFood(Food food);
}