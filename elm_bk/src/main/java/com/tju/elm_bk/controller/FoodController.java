package com.tju.elm_bk.controller;

import com.tju.elm_bk.service.FoodService;
import com.tju.elm_bk.entity.Food;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "商品接口")
@RestController
@RequestMapping("/api/foods")
public class FoodController {

    @Autowired
    private FoodService foodService;

    @Operation(summary = "获取指定商家商品列表")
    @PostMapping("/listFoodByBusinessId")
    public List<Food> listFoodByBusinessId(@RequestBody Food food) {
        return foodService.listFoodByBusinessId(food);
    }

    @Operation(summary = "添加商品")
    @PostMapping("/addFood")
    public int addFood(@RequestBody Food food) {
        return foodService.addFood(food);
    }
}