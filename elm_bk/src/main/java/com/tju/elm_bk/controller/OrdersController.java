package com.tju.elm_bk.controller;

import com.tju.elm_bk.service.OrdersService;
import com.tju.elm_bk.entity.CreateOrderRequest;
import com.tju.elm_bk.entity.OrderDetailet;
import com.tju.elm_bk.entity.Orders;
import com.tju.elm_bk.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "订单接口")
@RestController
@RequestMapping("/api/orders")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    /**
     * OrdersController/createOrders
     * 参数：userId、businessId、daId、orderTotal
     * 返回值：int（订单编号）
     * 功能：根据用户编号、商家编号、订单总金额、送货地址编号向订单表中添加一条记录，
     *  并获取自动生成的订单编号，
     *  然后根据用户编号、商家编号从购物车表中查询所有数据，批量添加到订单明细表中，
     *  然后根据用户编号、商家编号删除购物车表中的数据。
     */
    @Operation(summary = "创建订单")
    @PostMapping("/createOrders")
    public Integer createOrders(@RequestBody CreateOrderRequest request) {
        return ordersService.createOrders(request);
    }

    /**
     * POST http://localhost:8080/OrdersController/createOrders
     * {
     *   "userId": "1",
     *   "businessId": 2,
     *   "daId": 5,
     *   "orderTotal": 3.00
     * }
     * OrdersController/getOrdersById
     * 参数：orderId
     * 返回值：orders对象（包括多对一：商家信息； 一对多：订单明细信息）
     * 功能：根据订单编号查询订单信息，包括所属商家信息，和此订单的所有订单明细信息
     */
    @Operation(summary = "根据订单id获取订单信息")
    @PostMapping("/getOrdersById")
    public Orders getOrdersById(@RequestBody Orders order) {
        return ordersService.getOrdersById(order);
    }

    /**
     * OrdersController/listOrdersByUserId
     * 参数：userId
     * 返回值：orders数组（包括多对一：商家信息； 一对多：订单明细信息）
     * 功能：根据用户编号查询此用户的所有订单信息
     */
    @Operation(summary = "获取用户订单列表")
    @PostMapping("/listOrdersByUserId")
    public List<Orders> listOrdersByUserId(@RequestBody User user) {
        return ordersService.listOrdersByUserId(user);
    }

    @Operation(summary = "修改订单状态为已完成")
    @PostMapping("/payOk")
    public int payOk(@RequestBody Orders order) {
        return ordersService.completeOrder(order);
    }

    @Operation(summary = "获取订单详细信息")
    @PostMapping("/listOrderDetailetByOrderId")
        public List<OrderDetailet> listOrderDetailByOrderId(@RequestBody Orders orders) {
            return ordersService.listOrderDetailetByOrderId(orders);
    }

    @Operation(summary = "获取订单中的商品id列表")
    @PostMapping("/listOdIdByOrderId")
        public List<Integer> listOdIdByOrderId(@RequestBody Orders order) {
            return ordersService.listOdIdByOrderId(order);
    }
}