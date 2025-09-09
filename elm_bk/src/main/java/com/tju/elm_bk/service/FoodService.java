package com.tju.elm_bk.service;
import com.tju.elm_bk.entity.Food;
import java.util.List;

public interface FoodService {
    public List<Food> listFoodByBusinessId(Food food);
    public int addFood(Food food);
}