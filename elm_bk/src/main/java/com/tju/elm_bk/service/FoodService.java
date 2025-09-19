package com.tju.elm_bk.service;

import com.tju.elm_bk.dto.FoodCreateDTO;
import com.tju.elm_bk.vo.FoodVO;

import java.util.List;

public interface FoodService {

    List<FoodVO> getFoodList(Integer business,Integer order);

    FoodVO getFoodById(Long id);

    FoodVO addFood(FoodCreateDTO food);
}
