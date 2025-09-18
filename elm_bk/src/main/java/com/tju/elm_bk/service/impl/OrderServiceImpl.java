package com.tju.elm_bk.service.impl;

import com.tju.elm_bk.dto.OrderDTO;
import com.tju.elm_bk.entity.Order;
import com.tju.elm_bk.entity.User;
import com.tju.elm_bk.exception.APIException;
import com.tju.elm_bk.mapper.OrdersMapper;
import com.tju.elm_bk.mapper.UserMapper;
import com.tju.elm_bk.result.ResultCodeEnum;
import com.tju.elm_bk.service.OrderService;
import com.tju.elm_bk.utils.SecurityUtils;
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
    @Autowired
    private UserMapper userMapper;

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
        if (!orderDTO.verify()) {
            throw new APIException(ResultCodeEnum.PARAM_NOT_MATCHED);
        }
        User user = userMapper.findByUsername(SecurityUtils.getCurrentUsername().orElseThrow(() -> new APIException(ResultCodeEnum.VALUE_MISSED)));

        Order order = new Order();
        BeanUtils.copyProperties(orderDTO, order);
        order.setCreator(user.getId());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdater(user.getId());
        order.setUpdateTime(LocalDateTime.now());
        order.setIsDeleted(false);
        order.setBusinessId(orderDTO.getBusiness().getId());
        order.setCustomerId(user.getId());
        order.setAddressId(orderDTO.getDeliveryAddress().getId());
        order.setOrderState(0);
        ordersMapper.insertOrder(order);
        return ordersMapper.selectOrderById(order.getId());
    }


}
