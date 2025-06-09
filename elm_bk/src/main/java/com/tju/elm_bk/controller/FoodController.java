package com.tju.elm_bk.controller;

import com.tju.elm_bk.service.FoodService;
import com.tju.elm_bk.untity.Food;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/FoodController")
public class FoodController {

    @Autowired
    private FoodService foodService;

    @GetMapping("/listFoodByBusinessId")
    public List<Food> listFoodByBusinessId(@RequestParam Integer businessId) {
        return foodService.listFoodByBusinessId(businessId);
    }
}