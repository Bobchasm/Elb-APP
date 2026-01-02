package com.tju.elm.api.client;

import com.tju.elm.api.po.Food;
import com.tju.elm.api.vo.CartItemVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import result.HttpResult;

import java.util.List;
import java.util.Set;

@FeignClient("food-service")
public interface FoodClient {

    @PostMapping("/api/foods/ids")
    HttpResult<List<Food>> gainFoodsByIds(@RequestBody Set<Long> foodIds);

    @GetMapping("/api/foods/id")
    HttpResult<Food> gainFoodId(@RequestParam Long foodId);

    @GetMapping("/api/carts/list")
    HttpResult<List<CartItemVO>> listCartItem(@RequestParam Long businessId);


    @GetMapping("/api/carts/clear")
    HttpResult<Long> clearCart(@RequestParam Long businessId);

    @GetMapping("/api/foods/ai/keyword")
    HttpResult<List<Food>> searchByKeyword(@RequestParam String keyword, @RequestParam Integer limit);
}
