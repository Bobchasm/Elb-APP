package com.tju.elm_bk.service;

import com.tju.elm_bk.pojo.dto.FoodCreateDTO;
import com.tju.elm_bk.pojo.dto.FoodDTO;
import com.tju.elm_bk.pojo.dto.FoodUpdateDTO;
import com.tju.elm_bk.pojo.vo.FoodDetailVO;
import com.tju.elm_bk.pojo.vo.FoodItemVO;
import com.tju.elm_bk.pojo.vo.FoodVO;

import java.util.List;

public interface FoodService {

    List<FoodVO> getFoodList(Integer business,Integer order);

    FoodVO getFoodById(Long id);

    FoodVO addFood(FoodDTO food);

    FoodVO updateFood(FoodDTO foodDTO,Long id);


    List<FoodItemVO> getFoodItemList(Long businessId,Integer shelveStatus);

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


}
