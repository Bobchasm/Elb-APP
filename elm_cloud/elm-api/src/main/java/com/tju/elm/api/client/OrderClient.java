package com.tju.elm.api.client;

import com.tju.elm.api.dto.CreateOrderDTO;
import com.tju.elm.api.po.Order;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import result.HttpResult;

import java.util.List;

@FeignClient("order-service")
public interface OrderClient {
    @PostMapping("/api/orders/create")
    HttpResult<Long> orderCreate(@RequestBody CreateOrderDTO orderCreateDTO);

    @GetMapping("/api/orders")
    HttpResult<Order> gainOrder(@RequestParam Long orderId);

    @PutMapping("/api/orders/status")
    HttpResult<Long> setOrderStatus(
            @RequestParam Integer orderState,
            @RequestParam Long orderId,
            @RequestParam(required = false) Boolean usePoints);

    @GetMapping("/api/orders/payment/status")
    HttpResult<Integer> setPaymentStatus(@RequestParam Long orderId,@RequestParam Integer orderState);


    @GetMapping("/api/orders/payment/message")
    HttpResult<Boolean> sendOrderPaidMessage(@RequestParam Long orderId);

    @GetMapping("/api/orders/count")
    HttpResult<Integer> orderCount(@RequestParam Long businessId);

    @GetMapping("/api/orders/ai/recent_by_user")
    HttpResult<List<Order>> selectRecentOrdersByUserId(@RequestParam Long userId, @RequestParam Integer limit);

    @GetMapping("/api/orders/point/recent_by_user")
    public Integer updateOrderPointsAmount(@RequestParam Long orderId, @RequestParam Long pointsAmount);
}
