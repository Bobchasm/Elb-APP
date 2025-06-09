package com.tju.elm_bk.controller;

import com.tju.elm_bk.service.OrdersService;
import com.tju.elm_bk.untity.CreateOrderRequest;
import com.tju.elm_bk.untity.OrderDetailet;
import com.tju.elm_bk.untity.Orders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/OrdersController")
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
    @GetMapping("/getOrdersById")
    public Orders getOrdersById(@RequestParam Integer orderId) {
        return ordersService.getOrdersById(orderId);
    }

    /**
     * OrdersController/listOrdersByUserId
     * 参数：userId
     * 返回值：orders数组（包括多对一：商家信息； 一对多：订单明细信息）
     * 功能：根据用户编号查询此用户的所有订单信息
     */

    @GetMapping("/listOrdersByUserId")
    public List<Orders> listOrdersByUserId(@RequestParam String userId) {
        return ordersService.listOrdersByUserId(userId);
    }

    // 改变订单状态为已完成
    @GetMapping("/payOk")
    public int payOk(@RequestParam Integer orderId) {
        return ordersService.completeOrder(orderId);
    }

    @PostMapping("/listOrderDetailetByOrderId")
        public List<OrderDetailet> listOrderDetailetByOrderId(@RequestBody Orders orders){
            return ordersService.listOrderDetailetByOrderId(orders);
    }
    
    @RequestMapping("/listOdIdByOrderId")
        public List<Integer> listOdIdByOrderId(@RequestBody Orders orders){
            return ordersService.listOdIdByOrderId(orders);
    }
}