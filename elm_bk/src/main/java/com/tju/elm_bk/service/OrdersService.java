package com.tju.elm_bk.service;
import java.util.List;

import com.tju.elm_bk.untity.CreateOrderRequest;
import com.tju.elm_bk.untity.OrderDetailet;
import com.tju.elm_bk.untity.Orders;
import com.tju.elm_bk.untity.User;
import org.springframework.stereotype.Service;

@Service
public interface OrdersService {
    public int createOrders(CreateOrderRequest request);
    public Orders getOrdersById(Orders order);
    public List<Orders> listOrdersByUserId(User user);
    public int completeOrder(Orders order);
    public List<OrderDetailet> listOrderDetailetByOrderId(Orders orders);
 
    public List<Integer> listOdIdByOrderId(Orders order);
}