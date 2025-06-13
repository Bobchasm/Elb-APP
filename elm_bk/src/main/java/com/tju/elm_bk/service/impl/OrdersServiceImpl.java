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
            orders.setOrderDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            orders.setUserId(request.getUserId());
            orders.setBusinessId(request.getBusinessId());
            orders.setDaId(request.getDaId());
            orders.setOrderTotal(request.getOrderTotal());

            // 添加配送费设置
            orders.setDeliveryPrice(businessMapper.getBusinessById(request.getBusinessId()).getDeliveryPrice());
            orderId = ordersMapper.saveOrders(orders);
            orderId = orders.getOrderId();

            // 3. 批量插入订单明细
            List<OrderDetailet> orderDetails = new ArrayList<>();
            for (Cart cart : cartList) {

                OrderDetailet od = new OrderDetailet();
                cart.setFood(foodMapper.getFoodById(cart.getFoodId()));
                Food currentFood = cart.getFood(); // 直接从购物车获取食品信息

                od.setOrderId(orderId);
                od.setFoodId(cart.getFoodId());
                od.setQuantity(cart.getQuantity());
                od.setFoodName(currentFood.getFoodName()); // 使用创建时的食品名称
                od.setFoodPrice(currentFood.getFoodPrice()); // 使用创建时的食品价格
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
    public Orders getOrdersById(Orders order) {
        Orders orders = ordersMapper.getOrdersById(order.getOrderId());
        if (orders != null) {
            // 关联商家信息??????
            orders.setBusiness(businessMapper.getBusinessById(orders.getBusinessId()));

            // 关联订单明细
            List<OrderDetailet> details = orderDetailetMapper.listOrderDetailetByOrderId(order.getOrderId());
            orders.setList(details);
        }
        return orders;
    }

    @Override
    public List<Orders> listOrdersByUserId(User user) {
        List<Orders> ordersList = ordersMapper.listOrdersByUserId(user.getUserId());
        for (Orders orders : ordersList) {
            // 关联商家信息
            orders.setBusiness(businessMapper.getBusinessById(orders.getBusinessId()));

            // 关联订单明细,为了插入当前的食品信息
            List<OrderDetailet> details = orderDetailetMapper.listOrderDetailetByOrderId(orders.getOrderId());
            orders.setList(details);
        }
        return ordersList;
    }

    @Override
    public int completeOrder(Orders order) {
        try {
            // 更新订单状态为已完成
            return ordersMapper.completeOrder(order.getOrderId());
        } catch (Exception e) {
            throw new RuntimeException("更新订单状态失败", e);
        }
    }

    public List<OrderDetailet> listOrderDetailetByOrderId(Orders orders){
        if(null==orders) return null;
        List<OrderDetailet> orderDetails = orderDetailetMapper.listOrderDetailetByOrderId(orders.getOrderId());
        
        // 为每个订单明细填充食品信息
        for (OrderDetailet detail : orderDetails) {
            Food food = foodMapper.getFoodById(detail.getFoodId());
            detail.setFood(food);
        }
        
        return orderDetails;
     }
	
	
	
	public List<Integer> listOdIdByOrderId(Orders order){
		 return ordersMapper.listOdIdByOrderId(order.getOrderId());
	 }

}
