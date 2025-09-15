package com.tju.elm_bk.service;
import java.util.List;

import com.tju.elm_bk.entity.CreateOrderRequest;
import com.tju.elm_bk.entity.OrderDetailet;
import com.tju.elm_bk.entity.Order;
import com.tju.elm_bk.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface OrdersService {
    public int createOrders(CreateOrderRequest request);
    public Order getOrdersById(Order order);
    public List<Order> listOrdersByUserId(User user);
    public int completeOrder(Order order);
    public List<OrderDetailet> listOrderDetailetByOrderId(Order orders);
 
    public List<Integer> listOdIdByOrderId(Order order);
}