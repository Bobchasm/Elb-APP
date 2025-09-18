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

    @Insert("""
        INSERT INTO food (id, create_time, creator, is_deleted, update_time, updater, food_explain, food_img, food_name, food_price, remarks, business_id) 
        VALUES (#{id},#{createTime}, #{creator}, #{isDeleted}, #{updateTime}, #{updater}, #{foodExplain}, #{foodImg}, #{foodName}, #{foodPrice}, #{remarks}, #{businessId})
    """)
    void insertFoodVO(Food food);
}