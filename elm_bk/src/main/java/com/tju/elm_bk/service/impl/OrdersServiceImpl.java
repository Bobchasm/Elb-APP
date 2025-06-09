// OrdersServiceImpl.java
package com.tju.elm_bk.service.impl;

import com.tju.elm_bk.mapper.*;
import com.tju.elm_bk.service.OrdersService;
import com.tju.elm_bk.untity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrdersServiceImpl implements OrdersService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderDetailetMapper orderDetailetMapper;

    @Autowired
    private BusinessMapper businessMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private FoodMapper foodMapper;

    @Override
    @Transactional
    public int createOrders(CreateOrderRequest request) {
        int orderId = 0;
        try {
            // 1. 查询购物车
            List<Cart> cartList = cartMapper.listCart(request.getUserId(), request.getBusinessId());

            // 2. 创建订单
            Orders orders = new Orders();

            //数据库非空
            orders.setOrderDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())); // 设置当前时间


            orders.setUserId(request.getUserId());
            orders.setBusinessId(request.getBusinessId());
            orders.setDaId(request.getDaId());
            orders.setOrderTotal(request.getOrderTotal());
            orderId = ordersMapper.saveOrders(orders);
            orderId = orders.getOrderId(); // 获取自动生成的订单编号

            // 3. 批量插入订单明细
            List<OrderDetailet> orderDetails = new ArrayList<>();
            for (Cart cart : cartList) {
                OrderDetailet od = new OrderDetailet();
                od.setOrderId(orderId);
                od.setFoodId(cart.getFoodId());
                od.setQuantity(cart.getQuantity());
                orderDetails.add(od);
            }
            orderDetailetMapper.saveOrderDetailetBatch(orderDetails);

            // 4. 清空购物车
            cartMapper.removeCart(request.getUserId(), request.getBusinessId());

        } catch (Exception e) {
            throw new RuntimeException("创建订单失败", e);
        }
        return orderId;
    }

    @Override
    public Orders getOrdersById(Integer orderId) {
        Orders orders = ordersMapper.getOrdersById(orderId);
        if (orders != null) {
            // 关联商家信息
            orders.setBusiness(businessMapper.getBusinessById(orders.getBusinessId()));

            // 关联订单明细
            List<OrderDetailet> details = orderDetailetMapper.listOrderDetailetByOrderId(orderId);
            orders.setList(details);
        }
        return orders;
    }

    @Override
    public List<Orders> listOrdersByUserId(String userId) {
        List<Orders> ordersList = ordersMapper.listOrdersByUserId(userId);
        for (Orders orders : ordersList) {
            // 关联商家信息
            orders.setBusiness(businessMapper.getBusinessById(orders.getBusinessId()));

            // 关联订单明细
            List<OrderDetailet> details = orderDetailetMapper.listOrderDetailetByOrderId(orders.getOrderId());
            orders.setList(details);
        }
        return ordersList;
    }

    @Override
    public int completeOrder(Integer orderId) {
        try {
            // 更新订单状态为已完成
            return ordersMapper.completeOrder(orderId);
        } catch (Exception e) {
            throw new RuntimeException("更新订单状态失败", e);
        }
    }

    public List<OrderDetailet> listOrderDetailetByOrderId(Orders orders){
        List<OrderDetailet> orderDetails = orderDetailetMapper.listOrderDetailetByOrderId(orders.getOrderId());
        
        // 为每个订单明细填充食品信息
        for (OrderDetailet detail : orderDetails) {
            Food food = foodMapper.getFoodById(detail.getFoodId());
            detail.setFood(food);
        }
        
        return orderDetails;
     }
	
	
	
	public List<Integer> listOdIdByOrderId(Orders orders){
		 return ordersMapper.listOdIdByOrderId(orders);
	 }

}
