package com.tju.elm_bk.mapper;
import java.util.List;

import com.tju.elm_bk.dto.FoodCreateDTO;
import com.tju.elm_bk.entity.Food;
import com.tju.elm_bk.vo.FoodVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FoodMapper {


    List<FoodVO> selectFoodVOList(Integer businessId,Integer orderId);

    FoodVO selectFoodVOById(Long id);


    void insertFoodVO(Food food);

    @Select("""
        SELECT * FROM food WHERE is_deleted = 0 AND id = #{id}
    """)
    Food selectFoodById(Long id);
}