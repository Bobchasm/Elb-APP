package com.tju.elm_bk.service.impl;
import com.tju.elm_bk.service.FoodService;
import com.tju.elm_bk.untity.Food;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tju.elm_bk.mapper.FoodMapper;
import java.util.List;

@Service
public class FoodServiceImpl implements FoodService {
    @Autowired
    private FoodMapper foodMapper;
    @Override
    public List<Food> listFoodByBusinessId(Integer businessId) {

        return foodMapper.listFoodByBusinessId(businessId);
    }
}