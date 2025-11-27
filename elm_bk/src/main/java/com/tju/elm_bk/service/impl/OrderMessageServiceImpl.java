package com.tju.elm_bk.service.impl;

import com.tju.elm_bk.config.RabbitMQConfig;
import com.tju.elm_bk.pojo.dto.OrderPaidMessage;
import com.tju.elm_bk.service.OrderMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 订单消息服务实现类
 * 职责：发送订单相关消息到RabbitMQ
 * 设计原则：
 * 1. 单一职责原则 - 只负责消息发送
 * 2. 依赖注入 - 通过Spring注入RabbitTemplate
 * 3. 封装与抽象 - 封装消息发送逻辑
 */
@Slf4j
@Service
public class OrderMessageServiceImpl implements OrderMessageService {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Override
    public void sendOrderPaidMessage(OrderPaidMessage message) {
        try {
            log.info("发送订单支付完成消息: orderId={}, userId={}, orderAmount={}", 
                    message.getOrderId(), message.getUserId(), message.getOrderAmount());
            
            // 发送消息到队列
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ORDER_PAID_QUEUE, 
                    message
            );
            
            log.info("订单支付完成消息发送成功: orderId={}", message.getOrderId());
        } catch (Exception e) {
            log.error("发送订单支付完成消息失败: orderId={}", message.getOrderId(), e);
            // 这里不抛出异常，避免影响订单状态更新
            // 可以考虑将失败的消息保存到数据库，后续重试
        }
    }
}

