package com.tju.elm_bk.controller;

import com.tju.elm_bk.service.FoodService;
import com.tju.elm_bk.untity.Food;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/FoodController")
public class FoodController {

    @Autowired
    private FoodService foodService;

    @PostMapping("/listFoodByBusinessId")
    public List<Food> listFoodByBusinessId(@RequestBody Food food) {
        return foodService.listFoodByBusinessId(food);
    }

    @PostMapping("/addFood")
    public int addFood(@RequestBody Food food) {
        return foodService.addFood(food);
    }
}