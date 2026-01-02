package com.tju.elm.order.controller;

import com.tju.elm.order.mapper.OrdersMapper;
import com.tju.elm.order.zoo.pojo.dto.CreateOrderDTO;
import com.tju.elm.order.zoo.pojo.vo.Order;
import com.tju.elm.order.zoo.pojo.vo.OrderItemDetailVO;
import com.tju.elm.order.zoo.pojo.vo.OrderItemVO;
import com.tju.elm.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import result.HttpResult;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name="管理订单")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrdersMapper ordersMapper;


    @GetMapping("/list/business")
    @Operation(summary = "根据商家和状态获取订单列表")
    public HttpResult<List<OrderItemDetailVO>> listOrdersByBusiness(@RequestParam(required = false) Long businessId, @RequestParam(required = false) Integer orderState) {
        return HttpResult.success(orderService.getOrderItemListByBusiness(businessId,orderState));
    }

    @GetMapping("/list/user")
    @Operation(summary = "获取用户自己的相应状态的订单列表")
    public HttpResult<List<OrderItemVO>> listOrdersByUser(@RequestParam(required = false) Integer orderState) {
        return HttpResult.success(orderService.getOrderItemListByUser(orderState));
    }

    @GetMapping("/detail")
    @Operation(summary = "获取订单详情")
    public HttpResult<OrderItemDetailVO> listOrdersByUser(@RequestParam Long orderId) {
        return HttpResult.success(orderService.getOrderItemDetail(orderId));
    }

    @PutMapping("/status")
    @Operation(summary = "设置订单状态",description = "订单状态(0-待支付,1-待接单,2-已接单,3-已完成,4-已取消)。当orderState=1时，usePoints参数控制是否使用积分抵扣（true-使用积分，false-不使用积分，不传-默认使用积分）")
    public HttpResult<Long> setOrderStatus(
            @RequestParam Integer orderState,
            @RequestParam Long orderId,
            @RequestParam(required = false) Boolean usePoints) {
        return HttpResult.success(orderService.setOrderState(orderId, orderState, usePoints));
    }

    @GetMapping("/submit")
    @Operation(summary = "下单")
    public HttpResult<Long> orderSubmit(@RequestParam Long businessId,@RequestParam Long addressId) {
        return HttpResult.success(orderService.orderSubmit(businessId,addressId));
    }

    @PostMapping("/create")
    @Operation(summary = "创建订单")
    public HttpResult<Boolean> orderCreate(@RequestBody CreateOrderDTO orderCreateDTO) {
        return HttpResult.success(orderService.create(orderCreateDTO));
    }

    @GetMapping("/countPrice")
    @Operation(summary = "获取总营业额", description = "获取总营业额")
    public HttpResult<Double> countPrice(){
        return HttpResult.success(ordersMapper.countPrice());
    }

    @GetMapping
    @Operation(summary = "获取订单详情")
    public HttpResult<Order> gainOrder(@RequestParam Long orderId) {
        return HttpResult.success(ordersMapper.getOrderById(orderId));
    }

    @GetMapping("/payment/status")
    @Operation(summary = "设置订单支付状态")
    public HttpResult<Integer> setPaymentStatus(@RequestParam Long orderId,@RequestParam Integer orderState) {
        return HttpResult.success(ordersMapper.setOrderPaymentMethod(orderId,orderState));
    }

    @GetMapping("/payment/message")
    @Operation(summary = "发送订单支付完成消息")
    public HttpResult<Boolean> sendOrderPaidMessage(@RequestParam Long orderId) {
        return HttpResult.success(orderService.sendPaidMessage(orderId));
    }

    @GetMapping("/count")
    @Operation(summary = "统计已完成订单数量")
        public HttpResult<Integer> orderCount(@RequestParam Long businessId) {
        return HttpResult.success(ordersMapper.getSalesCount(businessId));
    }

    @GetMapping("/ai/recent_by_user")
    @Operation(summary = "ai服务查询最近用户订单")
    public HttpResult<List<Order>> selectRecentOrdersByUserId(@RequestParam Long userId, @RequestParam Integer limit) {
        return HttpResult.success(ordersMapper.selectRecentOrdersByUserId(userId,limit));
    }
}
