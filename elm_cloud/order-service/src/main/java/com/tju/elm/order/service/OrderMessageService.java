package com.tju.elm.order.service;

import com.tju.elm.order.zoo.pojo.dto.OrderPaidMessage;

/**
 * 订单消息服务接口
 * 职责：发送订单相关消息到消息队列
 * 设计原则：单一职责原则 - 只负责消息发送
 */
public interface OrderMessageService {
    
    /**
     * 发送订单支付完成消息
     * @param message 订单支付完成消息
     */
    void sendOrderPaidMessage(OrderPaidMessage message);
}

