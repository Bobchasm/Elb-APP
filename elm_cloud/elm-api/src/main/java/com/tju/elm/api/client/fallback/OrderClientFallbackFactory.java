package com.tju.elm.api.client.fallback;


import com.tju.elm.api.dto.CreateOrderDTO;
import com.tju.elm.api.po.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import result.HttpResult;

import java.util.ArrayList;
import java.util.List;

import com.tju.elm.api.client.OrderClient;
import com.tju.elm.api.client.fallback.OrderClientFallbackFactory;


@Slf4j
@Component
public class OrderClientFallbackFactory implements FallbackFactory<OrderClient> {

    @Override
    public OrderClient create(Throwable throwable) {
        log.error("OrderClient 调用失败，触发降级", throwable);
        
        return new OrderClient() {
            @Override
            public HttpResult<Order> gainOrder(Long orderId) {
                log.warn("gainOrder 降级处理，orderId: {}", orderId);
                // 返回友好的降级响应
                return HttpResult.failure("500", "订单服务暂时不可用，请稍后重试");
            }

            @Override
            public HttpResult<Boolean> orderCreate(CreateOrderDTO orderCreateDTO) {
                log.warn("orderCreate 降级处理");
                return HttpResult.failure("500","订单创建服务暂时不可用，请稍后重试");
            }

            @Override
            public HttpResult<Long> setOrderStatus(Integer orderState, Long orderId, Boolean usePoints) {
                log.warn("setOrderStatus 降级处理");
                return HttpResult.failure("500","更新订单状态服务暂时不可用，请稍后重试");
            }

            @Override
            public HttpResult<Integer> setPaymentStatus(Long orderId, Integer orderState) {
                log.warn("setPaymentStatus 降级处理");
                return HttpResult.failure("500","更新支付状态服务暂时不可用，请稍后重试");
            }

            @Override
            public HttpResult<Boolean> sendOrderPaidMessage(Long orderId) {
                log.warn("sendOrderPaidMessage 降级处理");
                return HttpResult.failure("500","发送支付消息服务暂时不可用，请稍后重试");
            }

            @Override
            public HttpResult<Integer> orderCount(Long businessId) {
                log.warn("orderCount 降级处理");
                return HttpResult.success(0);  // 返回默认值 0
            }

            @Override
            public HttpResult<List<Order>> selectRecentOrdersByUserId(Long userId, Integer limit) {
                log.warn("selectRecentOrdersByUserId 降级处理");
                return HttpResult.success(new ArrayList<>());  // 返回空列表
            }

            @Override
            public Integer updateOrderPointsAmount(Long orderId, Long pointsAmount) {
                log.warn("updateOrderPointsAmount 降级处理");
                return 0;  // 返回默认值
            }
        };
    }
}