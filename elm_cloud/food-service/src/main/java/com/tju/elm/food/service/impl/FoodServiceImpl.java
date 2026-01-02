package com.tju.elm.food.service.impl;

import com.tju.elm.api.client.BusinessClient;
import com.tju.elm.api.client.UserClient;
import com.tju.elm.api.po.Authority;
import com.tju.elm.api.po.Business;
import com.tju.elm.api.po.User;
import com.tju.elm.food.mapper.FoodMapper;
import com.tju.elm.food.pojo.dto.FoodCreateDTO;
import com.tju.elm.food.pojo.dto.FoodUpdateDTO;
import com.tju.elm.food.pojo.entity.Food;
import com.tju.elm.food.pojo.vo.FoodDetailVO;
import com.tju.elm.food.pojo.vo.FoodItemVO;
import com.tju.elm.food.pojo.vo.FoodVO;
import com.tju.elm.food.service.FoodService;
import exception.APIException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import result.ResultCodeEnum;
import utils.ObjectCopyUtil;
import utils.UserContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class FoodServiceImpl implements FoodService {
    @Autowired
    private FoodMapper foodMapper;

    @Autowired
    private UserClient userClient;
    @Autowired
    private BusinessClient businessClient;



    @Override
    public List<FoodItemVO> getFoodItemList(Long businessId, Integer shelveStatus) {
        User user = getCurrentUser();
        List<Authority> authorities = user.getAuthorities();

        // 普通用户只能看到已上架的商品
        if (authorities.stream()
                .noneMatch(authority -> (Objects.equals(authority.getName(), "BUSINESS") || Objects.equals(authority.getName(), "ADMIN")))) {
            shelveStatus = 1;
        }

        return foodMapper.selectFoodItemVOList(businessId, shelveStatus);
    }

    @Override
    public Long addFoodItem(FoodCreateDTO foodCreateDTO) {
        if (!foodCreateDTO.verify()) {
            throw new APIException(ResultCodeEnum.PARAM_NOT_MATCHED);
        }
        Business business = businessClient.gainBusinessById(foodCreateDTO.getBusinessId()).getData();
        if (business == null) {
            throw new APIException(ResultCodeEnum.BUSINESS_MISSED);
        }

        User user = getCurrentUser();
        List<Authority> authorities = user.getAuthorities();

        // 非管理员只能添加自己商铺的商品
        if (authorities.stream()
                .noneMatch(authority -> Objects.equals(authority.getName(), "ADMIN"))
                && !Objects.equals(user.getId(), business.getUserId())) {
            throw new APIException(ResultCodeEnum.NOT_ENOUGH_PERMISSION);
        }

        Food food = new Food();
        BeanUtils.copyProperties(foodCreateDTO, food);
        food.setBusinessId(food.getBusinessId());
        food.setCreator(user.getId());
        food.setCreateTime(LocalDateTime.now());
        food.setUpdater(user.getId());
        food.setUpdateTime(LocalDateTime.now());
        food.setIsDeleted(false);
        foodMapper.insertFood(food);

        return food.getId();
    }

    @Override
    public Long setFoodStatus(Long foodId, Integer shelveStatus) {
        Food food = foodMapper.selectFoodById(foodId);
        if (food == null) {
            throw new APIException(ResultCodeEnum.FOOD_MISSED);
        }
        Business business = businessClient.gainBusinessById(food.getBusinessId()).getData();

        User user = getCurrentUser();
        List<Authority> authorities = user.getAuthorities();
        if (authorities.stream()
                .noneMatch(authority -> Objects.equals(authority.getName(), "ADMIN"))
                && !Objects.equals(user.getId(), business.getUserId())) {
            throw new APIException(ResultCodeEnum.NOT_ENOUGH_PERMISSION);
        }

        if (shelveStatus != 0 && shelveStatus != 1) {
            throw new APIException(ResultCodeEnum.FOOD_STATUS_SET_FAILED);
        }

        foodMapper.updateFoodStatus(foodId, shelveStatus);

        return foodId;
    }

    @Override
    @Transactional
    public Long modifyFoodMessage(FoodUpdateDTO foodUpdateDTO) {
        if (foodUpdateDTO.getFoodId() == null) {
            throw new APIException(ResultCodeEnum.PARAM_NOT_MATCHED);
        }
        Food food = foodMapper.selectFoodById(foodUpdateDTO.getFoodId());
        if (food == null) {
            throw new APIException(ResultCodeEnum.FOOD_MISSED);
        }

        Business business = businessClient.gainBusinessById(food.getBusinessId()).getData();
        User user = getCurrentUser();
        List<Authority> authorities = user.getAuthorities();
        if (authorities.stream()
                .noneMatch(authority -> Objects.equals(authority.getName(), "ADMIN"))
                && !Objects.equals(user.getId(), business.getUserId())) {
            throw new APIException(ResultCodeEnum.NOT_ENOUGH_PERMISSION);
        }

        ObjectCopyUtil.copyPropertiesIgnoreNull(foodUpdateDTO,food);
        food.setUpdater(user.getId());
        food.setUpdateTime(LocalDateTime.now());
        foodMapper.updateFoodMessage(food);
        return food.getId();
    }

    @Override
    public Long deleteFood(Long foodId) {
        Food food = foodMapper.selectFoodById(foodId);
        if (food == null) {
            throw new APIException(ResultCodeEnum.FOOD_MISSED);
        }
        Business business = businessClient.gainBusinessById(food.getBusinessId()).getData();
        User user = getCurrentUser();
        List<Authority> authorities = user.getAuthorities();
        if (authorities.stream()
                .noneMatch(authority -> Objects.equals(authority.getName(), "ADMIN"))
                && !Objects.equals(user.getId(), business.getUserId())) {
            throw new APIException(ResultCodeEnum.NOT_ENOUGH_PERMISSION);
        }

        foodMapper.deleteFood(foodId);
        return foodId;
    }

    @Override
    public FoodDetailVO getFoodDetailByFoodId(Long foodId) {
        return foodMapper.selectFoodDetailVOByFoodId(foodId);
    }

    /**
     * 获取当前用户ID
     */
    private User getCurrentUser() {
        return userClient.getUserByName(UserContext.getUsername()).getData();
    }
}
