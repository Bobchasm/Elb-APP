package com.tju.elm_bk.controller;

import com.tju.elm_bk.dto.FoodCreateDTO;
import com.tju.elm_bk.result.HttpResult;
import com.tju.elm_bk.service.FoodService;
import com.tju.elm_bk.vo.FoodVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/foods")
@Tag(name="管理商品")
public class FoodController {

    @Autowired
    private FoodService foodService;

    @GetMapping
    @Operation(summary = "根据商家或订单获取商品列表")
    public HttpResult<List<FoodVO>> getAllFoods(@RequestParam(required = false) Integer business, @RequestParam(required = false) Integer order) {
        return HttpResult.success(foodService.getFoodList(business,order));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据商家或订单获取商品列表")
    public HttpResult<FoodVO> getAllFoods(@PathVariable Long id) {
        return HttpResult.success(foodService.getFoodById(id));
    }

    @PostMapping
    @Operation(summary = "新增商品")
    public HttpResult<FoodVO> addFood(@RequestBody FoodCreateDTO foodCreateDTO) {
        return HttpResult.success(foodService.addFood(foodCreateDTO));
    }
}
