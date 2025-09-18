package com.tju.elm_bk.service.impl;

import com.tju.elm_bk.dto.OrderDTO;
import com.tju.elm_bk.entity.Order;
import com.tju.elm_bk.mapper.OrdersMapper;
import com.tju.elm_bk.service.OrderService;
import com.tju.elm_bk.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Override
    public List<OrderVO> getCustomerOrderList(Long customerId) {
        return ordersMapper.selectOrders(customerId);
    }

    @Override
    public OrderVO getOrderById(Long orderId) {
        return ordersMapper.selectOrderById(orderId);
    }

    @Override
    public OrderVO addOrder(OrderDTO orderDTO) {
        Order order = new Order();
        BeanUtils.copyProperties(orderDTO, order);
        order.setCreateTime(LocalDateTime.now());
        order.setIsDeleted(false);
        order.setBusinessId(orderDTO.getBusiness().getId());
        order.setCustomerId(orderDTO.getCustomer().getId());
        order.setAddressId(orderDTO.getDeliveryAddress().getId());
        ordersMapper.insertOrder(order);
        return ordersMapper.selectOrderById(order.getId());
    }


}
