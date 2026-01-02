package com.tju.elm.food.controller;

import com.tju.elm.food.mapper.FoodMapper;
import com.tju.elm.food.pojo.dto.FoodCreateDTO;
import com.tju.elm.food.pojo.dto.FoodUpdateDTO;
import com.tju.elm.food.pojo.entity.Food;
import com.tju.elm.food.pojo.vo.FoodDetailVO;
import com.tju.elm.food.pojo.vo.FoodItemVO;
import com.tju.elm.food.service.FoodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import result.HttpResult;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/foods")
@Tag(name="管理商品")
public class FoodController {

    @Autowired
    private FoodService foodService;
    @Autowired
    private FoodMapper foodMapper;



    @GetMapping("/list")
    @Operation(summary = "根据商家获取商品列表",description = "普通用户只能看到已上架的")
    public HttpResult<List<FoodItemVO>> getAllFoods(@RequestParam Long businessId, @RequestParam(required = false) Integer shelveStatus) {
        return HttpResult.success(foodService.getFoodItemList(businessId, shelveStatus));
    }

    @PostMapping("/addItem")
    @Operation(summary = "(前端用这个)商铺新增商品",description = "管理员可以随便添，商家只能为自己的商铺添")
    //@PreAuthorize("hasAuthority('BUSINESS')")
    public HttpResult<Long> addFoodItem(@RequestBody FoodCreateDTO foodCreateDTO) {
        return HttpResult.success(foodService.addFoodItem(foodCreateDTO));
    }

    @GetMapping("/status")
    @Operation(summary = "上架/下架商品",description = "shelveStatus 0-下架 1-上架")
    //@PreAuthorize("hasAuthority('BUSINESS')")
    public HttpResult<Long> setFoodShelveStatus(@RequestParam Long foodId,@RequestParam Integer shelveStatus) {
        return HttpResult.success(foodService.setFoodStatus(foodId,shelveStatus));
    }

    @PostMapping("/modifyItem")
    @Operation(summary = "(前端用这个)商铺修改商品",description = "管理员可以随便改，商家只能为自己的商铺改")
    //@PreAuthorize("hasAuthority('BUSINESS')")
    public HttpResult<Long> modifyFoodItem(@RequestBody FoodUpdateDTO foodUpdateDTO) {
        return HttpResult.success(foodService.modifyFoodMessage(foodUpdateDTO));
    }

    @GetMapping("/delete")
    @Operation(summary = "商家删除商品")
    //@PreAuthorize("hasAuthority('BUSINESS')")
    public HttpResult<Long> setFoodShelveStatus(@RequestParam Long foodId) {
        return HttpResult.success(foodService.deleteFood(foodId));
    }

    @GetMapping("/detail/{foodId}")
    @Operation(summary = "根据foodId查询已上架且未删除的food详细信息及所属商铺名字")
    public HttpResult<FoodDetailVO> getFoodDetailByFoodId(@PathVariable Long foodId) {
        return HttpResult.success(foodService.getFoodDetailByFoodId(foodId));
    }

    @PostMapping("/ids")
    @Operation(summary = "根据id列表商品列表")
    public HttpResult<List<Food>> gainFoodsByIds(@RequestBody Set<Long> foodIds) {
        return HttpResult.success(foodService.getFoodsByIds(foodIds));
    }

    @GetMapping("/id")
    @Operation(summary = "根据foodId查询已上架且未删除的food详细信息及所属商铺名字")
    public HttpResult<Food> gainFoodId(@RequestParam Long foodId) {
        return HttpResult.success(foodService.getFoodByFoodId(foodId));
    }

    @GetMapping("/ai/keyword")
    @Operation(summary = "ai服务关键词查询商品")
    public HttpResult<List<Food>> searchByKeyword(String keyword, Integer limit) {
        return HttpResult.success(foodMapper.searchByKeyword(keyword, limit));
    }
}
