package com.tju.elm_bk.mapper;
import java.util.List;

import com.tju.elm_bk.untity.Food;
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

}