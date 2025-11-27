package com.tju.elm_bk.service;

import com.tju.elm_bk.pojo.dto.OrderDTO;
import com.tju.elm_bk.pojo.vo.OrderItemDetailVO;
import com.tju.elm_bk.pojo.vo.OrderItemVO;
import com.tju.elm_bk.pojo.vo.OrderVO;

import java.util.List;

public interface OrderService {

    List<OrderVO> getCustomerOrderList(Long customerId);

    OrderVO getOrderById(Long orderId);

    OrderVO addOrder(OrderDTO orderDTO);




    List<OrderItemDetailVO> getOrderItemListByBusiness(Long businessId, Integer orderState);

    List<OrderItemVO> getOrderItemListByUser(Integer orderState);

    OrderItemDetailVO getOrderItemDetail(Long orderItemId);

    /**
     * 设置订单状态
     * @param orderId 订单ID
     * @param orderState 订单状态(0-待支付,1-待接单,2-已接单,3-已完成,4-已取消)
     * @param usePoints 是否使用积分抵扣（仅当orderState=1时有效，true-使用积分，false-不使用积分，null-默认使用积分）
     * @return 订单ID
     */
    Long setOrderState(Long orderId, Integer orderState, Boolean usePoints);

    Long orderSubmit(Long businessId,Long addressId);
    
    /**
     * 自动完成订单（系统调用，用于定时任务）
     * 订单支付七天后自动完成
     */
    void autoCompleteOrders();
}
