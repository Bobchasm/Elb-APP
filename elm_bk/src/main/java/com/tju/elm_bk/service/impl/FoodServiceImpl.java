package com.tju.elm_bk.service.impl;

import com.tju.elm_bk.dto.FoodCreateDTO;
import com.tju.elm_bk.entity.Food;
import com.tju.elm_bk.entity.User;
import com.tju.elm_bk.exception.APIException;
import com.tju.elm_bk.mapper.FoodMapper;
import com.tju.elm_bk.mapper.UserMapper;
import com.tju.elm_bk.result.ResultCodeEnum;
import com.tju.elm_bk.service.FoodService;
import com.tju.elm_bk.utils.SecurityUtils;
import com.tju.elm_bk.vo.FoodVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FoodServiceImpl implements FoodService {
    @Autowired
    private FoodMapper foodMapper;
    @Autowired
    private UserMapper userMapper;

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
        if(!foodCreateDTO.verify()) {
            throw new APIException(ResultCodeEnum.PARAM_NOT_MATCHED);
        }
        Food food = new Food();
        BeanUtils.copyProperties(foodCreateDTO, food);
        food.setBusinessId(foodCreateDTO.getBusiness().getId());
        User user = userMapper.findByUsername(SecurityUtils.getCurrentUsername().orElseThrow(() -> new APIException(ResultCodeEnum.VALUE_MISSED)));
        food.setCreator(user.getId());
        food.setCreateTime(LocalDateTime.now());
        food.setCreator(user.getId());
        food.setUpdateTime(LocalDateTime.now());
        food.setIsDeleted(false);
        foodMapper.insertFoodVO(food);
        return foodMapper.selectFoodVOById(food.getId());
    }
}
