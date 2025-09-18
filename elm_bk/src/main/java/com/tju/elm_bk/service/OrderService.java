package com.tju.elm_bk.service;

import com.tju.elm_bk.dto.OrderDTO;
import com.tju.elm_bk.service.impl.OrderServiceImpl;
import com.tju.elm_bk.vo.OrderVO;

import java.util.List;

public interface OrderService {

    List<OrderVO> getCustomerOrderList(Long customerId);

    OrderVO getOrderById(Long orderId);

    OrderVO addOrder(OrderDTO orderDTO);
}
