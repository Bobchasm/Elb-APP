package com.tju.elm.food.service;

import com.tju.elm.food.pojo.dto.FoodCreateDTO;
import com.tju.elm.food.pojo.dto.FoodUpdateDTO;
import com.tju.elm.food.pojo.entity.Food;
import com.tju.elm.food.pojo.vo.FoodDetailVO;
import com.tju.elm.food.pojo.vo.FoodItemVO;
import com.tju.elm.food.pojo.vo.FoodVO;

import java.util.List;
import java.util.Set;

public interface FoodService {


    List<FoodItemVO> getFoodItemList(Long businessId, Integer shelveStatus);

    Long addFoodItem(FoodCreateDTO foodCreateDTO);

    Long setFoodStatus(Long foodId,Integer shelveStatus);

    Long modifyFoodMessage(FoodUpdateDTO foodUpdateDTO);

    Long deleteFood(Long foodId);

    /**
     * 根据foodId查询已上架且未删除的food详细信息及所属商铺名字
     * @param foodId 食品ID
     * @return FoodDetailVO
     */
    FoodDetailVO getFoodDetailByFoodId(Long foodId);

    List<Food> getFoodsByIds(Set<Long> foodIds);

    Food getFoodByFoodId(Long foodId);
}
