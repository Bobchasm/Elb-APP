package com.tju.elm_bk.controller;

import com.tju.elm_bk.dto.OrderDTO;
import com.tju.elm_bk.result.HttpResult;
import com.tju.elm_bk.service.OrderService;
import com.tju.elm_bk.vo.OrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name="管理订单")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping
    @Operation(summary = "获取用户订单列表")
    public HttpResult<List<OrderVO>> listOrdersByUserId (Long userId) {
        return HttpResult.success(orderService.getCustomerOrderList(userId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取用户订单列表")
    public HttpResult<OrderVO> getOrderById(@PathVariable Long id) {
        return HttpResult.success(orderService.getOrderById(id));
    }

    @PostMapping
    public HttpResult<OrderVO> addOrders(@RequestBody OrderDTO orderDTO) {
        return HttpResult.success(orderService.addOrder(orderDTO));
    }

}
