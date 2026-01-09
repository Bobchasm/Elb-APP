package com.tju.elm.order.zoo.schedule;

import com.tju.elm.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 订单自动完成定时任务
 * 职责：定时处理订单自动完成（支付7天后）
 * 设计原则：单一职责原则 - 只负责定时任务调度
 */
@Slf4j
@Component
public class OrderAutoCompleteSchedule {

    @Autowired
    private OrderService orderService;

    /**
     * 自动完成订单（支付7天后）
     * 每天凌晨2点执行
     * cron表达式：秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void autoCompleteOrders() {
        log.info("========== 定时任务：开始自动完成订单（支付7天后） ==========");
        log.info("【定时任务】任务执行时间: {}", LocalDateTime.now());
        try {
            orderService.autoCompleteOrders();
            log.info("【定时任务】订单自动完成任务执行成功");
        } catch (Exception e) {
            log.error("【定时任务】订单自动完成任务执行失败", e);
        }
        log.info("========== 定时任务：订单自动完成任务结束 ==========");
    }
}
