package com.tju.elm_bk.service;
import com.tju.elm_bk.untity.Food;
import java.util.List;

public interface FoodService {
    public List<Food> listFoodByBusinessId(Integer businessId);
    public int addFood(Food food);
}