package com.tju.elm_bk.service;
import java.util.List;

import com.tju.elm_bk.untity.CreateOrderRequest;
import com.tju.elm_bk.untity.OrderDetailet;
import com.tju.elm_bk.untity.Orders;
import org.springframework.stereotype.Service;

@Service
public interface OrdersService {
    public int createOrders(CreateOrderRequest request);
    public Orders getOrdersById(Integer orderId);
    public List<Orders> listOrdersByUserId(String userId);
    public int completeOrder(Integer orderId);
    public List<OrderDetailet> listOrderDetailetByOrderId(Orders orders);
 
    public List<Integer> listOdIdByOrderId(Orders orders);
}