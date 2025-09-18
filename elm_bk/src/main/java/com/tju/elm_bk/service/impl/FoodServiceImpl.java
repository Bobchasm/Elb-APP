package com.tju.elm_bk.service.impl;

import com.tju.elm_bk.dto.FoodCreateDTO;
import com.tju.elm_bk.entity.Food;
import com.tju.elm_bk.mapper.FoodMapper;
import com.tju.elm_bk.service.FoodService;
import com.tju.elm_bk.vo.FoodVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FoodServiceImpl implements FoodService {
    @Autowired
    private FoodMapper foodMapper;

    @Override
    public List<FoodVO> getFoodList(Integer business, Integer order) {
        return foodMapper.selectFoodVOList(business, order);
    }

    @Override
    public FoodVO getFoodById(Long id) {
        return foodMapper.selectFoodVOById(id);
    }

    @Override
    public FoodVO addFood(FoodCreateDTO foodCreateDTO) {
        Food food = new Food();
        BeanUtils.copyProperties(foodCreateDTO, food);
        food.setBusinessId(foodCreateDTO.getBusiness().getId());
        foodMapper.insertFoodVO(food);
        return foodMapper.selectFoodVOById(food.getId());
    }
}
