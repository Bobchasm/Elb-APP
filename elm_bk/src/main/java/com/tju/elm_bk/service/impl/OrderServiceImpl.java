package com.tju.elm_bk.service.impl;

import com.tju.elm_bk.dto.OrderCreateDTO;
import com.tju.elm_bk.dto.OrderDTO;
import com.tju.elm_bk.entity.*;
import com.tju.elm_bk.exception.APIException;
import com.tju.elm_bk.mapper.*;
import com.tju.elm_bk.result.ResultCodeEnum;
import com.tju.elm_bk.service.OrderService;
import com.tju.elm_bk.utils.SecurityUtils;
import com.tju.elm_bk.vo.OrderFoodVO;
import com.tju.elm_bk.vo.OrderItemDetailVO;
import com.tju.elm_bk.vo.OrderItemVO;
import com.tju.elm_bk.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BusinessMapper businessMapper;
    @Autowired
    private FoodMapper foodMapper;
    @Autowired
    private OrderDetailetMapper orderDetailetMapper;

    // 订单状态(0-待支付,1-待接单,2-已结单,3-已完成,4-已取消
    public static final List<Integer> orderStatusList;

    static {
        orderStatusList = List.of(0,1,2,3,4);
    }

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





    @Override
    public List<OrderItemVO> getOrderItemListByBusiness(Long businessId, Integer orderState) {
        return ordersMapper.selectOrderItemsList(businessId, orderState, null);
    }

    @Override
    public List<OrderItemVO> getOrderItemListByUser(Integer orderState) {
        Long userId = userMapper.getUserIdByUsername(SecurityUtils.getCurrentUsername().orElseThrow(() -> new APIException(ResultCodeEnum.VALUE_MISSED)));
        return ordersMapper.selectOrderItemsList(null, orderState, userId);
    }

    @Override
    public OrderItemDetailVO getOrderItemDetail(Long orderItemId) {
        return ordersMapper.selectOrderItemById(orderItemId);
    }

    @Override
    public Long setOrderState(Long orderId, Integer orderState) {
        if (!orderStatusList.contains(orderState)) {
            throw new APIException(ResultCodeEnum.ORDER_STATUS_UNMATCHED);
        }
        Order order = ordersMapper.getOrderById(orderId);
        Long userId = userMapper.getUserIdByUsername(SecurityUtils.getCurrentUsername().orElseThrow(() -> new APIException(ResultCodeEnum.VALUE_MISSED)));
        if (null == order) {
            throw new APIException(ResultCodeEnum.ORDER_MISSED);
        }

        Business business = businessMapper.selectBusinessById(order.getBusinessId());

        // 订单状态(0-待支付,1-待接单,2-已接单,3-已完成,4-已取消)
        if (orderState == 1) {
            if (order.getOrderState() != 0 || !Objects.equals(userId, order.getCustomerId())) {
                throw new APIException(ResultCodeEnum.ORDER_PAY_FAILED);
            }
        }
        if (orderState == 2) {
            if (order.getOrderState() != 1 || !Objects.equals(business.getUserId(),userId)) {
                throw new APIException(ResultCodeEnum.ORDER_ACCEPT_FAILED);
            }
        }
        if (orderState == 3) {
            if (order.getOrderState() != 2 || (!Objects.equals(business.getUserId(),userId) && !Objects.equals(order.getCustomerId(),userId))) {
                throw new APIException(ResultCodeEnum.ORDER_ACCEPT_FAILED);
            }
        }
        if (orderState == 4) {
            if (order.getOrderState() != 0) {
                throw new APIException(ResultCodeEnum.ORDER_CANCEL_DENY);
            }
            if ((!Objects.equals(business.getUserId(),userId) && !Objects.equals(order.getCustomerId(),userId))) {
                throw new APIException(ResultCodeEnum.ORDER_CANCEL_FAILED);
            }
        }
        return order.getId();
    }

    @Override
    @Transactional
    public Long orderSubmit(OrderCreateDTO orderCreateDTO) {
        if (!orderCreateDTO.verify()) {
            throw new APIException(ResultCodeEnum.PARAM_NOT_MATCHED);
        }
        Long userId = userMapper.findByUsername(SecurityUtils.getCurrentUsername().orElseThrow(() -> new APIException(ResultCodeEnum.VALUE_MISSED))).getId();

        Order order = new Order();
        order.setCreator(userId);
        order.setCreateTime(LocalDateTime.now());
        order.setUpdater(userId);
        order.setUpdateTime(LocalDateTime.now());
        order.setIsDeleted(false);
        order.setBusinessId(orderCreateDTO.getBusinessId());
        order.setCustomerId(userId);
        order.setAddressId(orderCreateDTO.getAddressId());
        order.setOrderState(0);

        Double orderTotal = 0.0;
        List<OrderFoodVO> foodList = orderCreateDTO.getFoodList();
        for (OrderFoodVO food : foodList) {
            Food f = foodMapper.selectFoodById(food.getFoodId());
            if (null == f) {
                throw new APIException(ResultCodeEnum.FOOD_MISSED);
            }
            orderTotal += (f.getFoodPrice().doubleValue() * food.getQuantity());
            OrderDetailet orderDetailet = new OrderDetailet();
            orderDetailet.setQuantity(food.getQuantity());
            orderDetailet.setOrderId(order.getId());
            orderDetailet.setFoodId(f.getId());
            orderDetailet.setCreator(userId);
            orderDetailet.setCreateTime(LocalDateTime.now());
            orderDetailet.setUpdater(userId);
            orderDetailet.setUpdateTime(LocalDateTime.now());
            orderDetailet.setIsDeleted(false);

            orderDetailetMapper.saveOrderDetail(orderDetailet);
        }

        order.setOrderTotal(new BigDecimal(orderTotal));
        ordersMapper.insertOrder(order);
        return order.getId();
    }
}
