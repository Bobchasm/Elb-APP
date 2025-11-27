package com.tju.elm_bk.listener;

import com.tju.elm_bk.pojo.dto.OrderPaidMessage;
import com.tju.elm_bk.service.MarketingPointsRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 订单支付完成消息监听器
 * 职责：监听订单支付完成消息，触发营销系统计算积分
 * 设计原则：
 * 1. 单一职责原则 - 只负责消息监听和触发积分计算
 * 2. 依赖注入 - 通过Spring注入MarketingPointsRuleService
 * 3. 高内聚、松耦合 - 订单系统和营销系统通过消息队列解耦
 */
@Slf4j
@Component
public class OrderPaidListener {
    
    @Autowired
    private MarketingPointsRuleService marketingPointsRuleService;
    
    /**
     * 监听订单支付完成消息
     * 当订单状态从0（待支付）变为1（待接单/已支付）时，订单系统会发送此消息
     * 营销系统接收到消息后，根据积分规则计算并发放积分
     * 
     * @param message 订单支付完成消息
     */
    @RabbitListener(queues = "order.paid.queue")
    public void handleOrderPaid(OrderPaidMessage message) {
        try {
            log.info("收到订单支付完成消息: orderId={}, userId={}, orderAmount={}", 
                    message.getOrderId(), message.getUserId(), message.getOrderAmount());
            
            // 调用营销系统计算积分
            // 营销系统会根据订单金额、商品信息、促销规则等计算积分
            marketingPointsRuleService.calculateOrderPoints(
                    message.getUserId(),
                    message.getOrderId(),
                    message.getOrderAmount(),
                    message.getOrderDate(),
                    message.getFoodDetails()
            );
            
            log.info("订单积分计算完成: orderId={}, userId={}", 
                    message.getOrderId(), message.getUserId());
        } catch (Exception e) {
            log.error("处理订单支付完成消息失败: orderId={}", message.getOrderId(), e);
            // 消息处理失败时，RabbitMQ会自动重试（如果配置了重试机制）
            // 也可以手动将消息发送到死信队列
            throw e; // 抛出异常，触发重试机制
        }
    }
}

