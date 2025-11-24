package com.tju.elm_bk.schedule;

import com.tju.elm_bk.service.PointsExpirationAlertService;
import com.tju.elm_bk.service.PointsExpirationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 积分过期定时任务
 * 职责：定时处理积分过期和预警
 * 设计原则：单一职责原则 - 只负责定时任务调度
 */
@Slf4j
@Component
public class PointsExpirationSchedule {

    @Autowired
    private PointsExpirationService pointsExpirationService;
    
    @Autowired
    private PointsExpirationAlertService pointsExpirationAlertService;

    /**
     * 处理积分过期
     * 每天凌晨1点执行
     * cron表达式：秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void processExpiredPoints() {
        log.info("定时任务：开始处理积分过期");
        try {
            pointsExpirationService.processExpiredPoints();
            log.info("定时任务：积分过期处理完成");
        } catch (Exception e) {
            log.error("定时任务：积分过期处理失败", e);
        }
    }
    
    /**
     * 检查并发送积分过期预警
     * 每天上午9点执行
     * cron表达式：秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void checkAndSendExpirationAlerts() {
        log.info("定时任务：开始检查并发送积分过期预警");
        try {
            pointsExpirationAlertService.checkAndSendExpirationAlerts();
            log.info("定时任务：积分过期预警检查完成");
        } catch (Exception e) {
            log.error("定时任务：积分过期预警检查失败", e);
        }
    }
}

