package com.tju.elm.point.service.impl;

import com.tju.elm.api.client.NotificationClient;
import com.tju.elm.api.client.UserClient;
import com.tju.elm.api.dto.NotificationSendDTO;
import com.tju.elm.api.po.Person;
import com.tju.elm.point.mapper.PointsExpirationAlertConfigMapper;
import com.tju.elm.point.mapper.PointsExpirationAlertLogMapper;
import com.tju.elm.point.mapper.PointsExpirationMapper;
import com.tju.elm.point.service.PointsExpirationAlertService;
import com.tju.elm.point.zoo.pojo.entity.PointsExpiration;
import com.tju.elm.point.zoo.pojo.entity.PointsExpirationAlertConfig;
import com.tju.elm.point.zoo.pojo.entity.PointsExpirationAlertLog;
import com.tju.elm.point.zoo.websocket.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 积分到期预警服务实现类
 * 职责：积分到期预警配置管理和预警任务执行
 * 设计原则：单一职责原则 - 只负责预警相关功能
 */
@Service
public class PointsExpirationAlertServiceImpl implements PointsExpirationAlertService {

    private static final Logger log = LoggerFactory.getLogger(PointsExpirationAlertServiceImpl.class);

    @Autowired
    private PointsExpirationAlertConfigMapper alertConfigMapper;

    @Autowired
    private PointsExpirationAlertLogMapper alertLogMapper;

    @Autowired
    private PointsExpirationMapper pointsExpirationMapper;

//    @Autowired
//    private PersonMapper personMapper;
//
//    @Autowired
//    private NotificationMapper notificationMapper;
//
    @Autowired
    private WebSocketServer webSocketServer;

    @Autowired
    private NotificationClient notificationClient;
    @Autowired
    private UserClient userClient;


    @Override
    public PointsExpirationAlertConfig getAlertConfig() {
        return alertConfigMapper.selectConfig();
    }

    @Override
    @Transactional
    public Boolean updateAlertConfig(PointsExpirationAlertConfig config) {
        PointsExpirationAlertConfig existing = alertConfigMapper.selectConfig();
        if (existing != null) {
            config.setId(existing.getId());
            config.setUpdateTime(LocalDateTime.now());
            alertConfigMapper.updateById(config);
        } else {
            config.setCreateTime(LocalDateTime.now());
            config.setUpdateTime(LocalDateTime.now());
            alertConfigMapper.insert(config);
        }
        return true;
    }

    /**
     * 检查并发送即将过期积分预警（定时任务调用）
     * 设计原则：封装与抽象 - 封装预警逻辑
     */
    @Override
    @Transactional
    public void checkAndSendExpirationAlerts() {
        log.info("========== 开始检查并发送积分过期预警 ==========");
        LocalDateTime startTime = LocalDateTime.now();

        // 1. 查询预警配置
        PointsExpirationAlertConfig config = alertConfigMapper.selectConfig();
        if (config == null) {
            log.warn("【预警配置】预警配置不存在，跳过预警任务");
            return;
        }

        log.info("【预警配置】配置ID: {}, 是否启用: {}, 预警天数: {}, 预警周期: {}天, 模板: {}",
                config.getId(), config.getIsEnabled(), config.getAlertDays(),
                config.getAlertCycle(), config.getSmsTemplate());

        if (!config.getIsEnabled()) {
            log.warn("【预警配置】预警功能已禁用，跳过预警任务");
            return;
        }

        // 2. 计算预警日期
        LocalDate alertDate = LocalDate.now().plusDays(config.getAlertDays());
        log.info("【预警日期】当前日期: {}, 预警天数: {}, 计算的预警日期: {}",
                LocalDate.now(), config.getAlertDays(), alertDate);

        // 3. 查询即将过期的积分
        List<PointsExpiration> expiringList = pointsExpirationMapper.selectByExpireDate(alertDate);
        log.info("【查询结果】查询到 {} 条即将在 {} 过期的积分记录", expiringList.size(), alertDate);

        if (expiringList.isEmpty()) {
            log.info("【预警结果】没有需要预警的积分，任务结束");
            return;
        }

        // 4. 统计信息
        int totalUsers = 0;
        int sentCount = 0;
        int skippedCount = 0;
        int errorCount = 0;

        // 5. 检查并发送预警
        for (PointsExpiration exp : expiringList) {
            totalUsers++;
            try {
                log.info("【处理用户】用户ID: {}, 积分数量: {}, 过期日期: {}",
                        exp.getUserId(), exp.getPointsAmount(), exp.getExpireDate());

                // 检查是否需要发送预警
                PointsExpirationAlertLog existingLog = alertLogMapper.selectByUserIdAndExpireDate(
                    exp.getUserId(), alertDate);

                boolean shouldSend = false;
                String reason = "";

                if (existingLog == null) {
                    // 第一次预警
                    shouldSend = true;
                    reason = "首次预警（无历史记录）";
                    log.info("【预警判断】用户ID: {} - {} - 需要发送预警", exp.getUserId(), reason);
                } else {
                    log.info("【预警判断】用户ID: {} - 已存在预警记录，上次预警时间: {}, 下次预警时间: {}",
                            exp.getUserId(), existingLog.getAlertTime(), existingLog.getNextAlertTime());

                    if (config.getAlertCycle() == null) {
                        // alert_cycle 为 null 表示只预警一次，已有记录则不发送
                        shouldSend = false;
                        reason = "已预警过（alert_cycle为null，只预警一次）";
                        log.info("【预警判断】用户ID: {} - {} - 跳过发送", exp.getUserId(), reason);
                    } else if (existingLog.getNextAlertTime() != null &&
                              existingLog.getNextAlertTime().isBefore(LocalDateTime.now())) {
                        // 到了下次预警时间
                        shouldSend = true;
                        reason = String.format("到达下次预警时间（当前时间: %s, 下次预警时间: %s）",
                                LocalDateTime.now(), existingLog.getNextAlertTime());
                        log.info("【预警判断】用户ID: {} - {} - 需要发送预警", exp.getUserId(), reason);
                    } else {
                        shouldSend = false;
                        reason = String.format("未到达下次预警时间（当前时间: %s, 下次预警时间: %s）",
                                LocalDateTime.now(), existingLog.getNextAlertTime());
                        log.info("【预警判断】用户ID: {} - {} - 跳过发送", exp.getUserId(), reason);
                    }
                }

                if (shouldSend) {
                    log.info("【发送预警】用户ID: {}, 积分数量: {}, 过期日期: {}, 原因: {}",
                            exp.getUserId(), exp.getPointsAmount(), alertDate, reason);
                    sendAlert(exp.getUserId(), exp.getPointsAmount(), alertDate, config, existingLog);
                    sentCount++;
                    log.info("【发送成功】用户ID: {} 的预警已成功发送", exp.getUserId());
                } else {
                    skippedCount++;
                    log.info("【跳过发送】用户ID: {} 的预警被跳过，原因: {}", exp.getUserId(), reason);
                }
            } catch (Exception e) {
                errorCount++;
                log.error("【发送失败】用户ID: {} 的预警发送失败，错误: {}",
                        exp.getUserId(), e.getMessage(), e);
            }
        }

        // 6. 输出统计信息
        LocalDateTime endTime = LocalDateTime.now();
        long duration = java.time.Duration.between(startTime, endTime).toMillis();
        log.info("========== 积分过期预警检查完成 ==========");
        log.info("【统计信息】总用户数: {}, 成功发送: {}, 跳过: {}, 失败: {}, 耗时: {}ms",
                totalUsers, sentCount, skippedCount, errorCount, duration);
    }

    /**
     * 发送预警通知（创建 notification 记录并通过 WebSocket 推送）
     * 设计原则：封装与抽象 - 封装发送逻辑
     */
    private void sendAlert(Long userId, Long pointsAmount, LocalDate expireDate,
                          PointsExpirationAlertConfig config, PointsExpirationAlertLog existingLog) {
        log.info("【发送预警详情】开始为用户ID: {} 发送预警，积分数量: {}, 过期日期: {}",
                userId, pointsAmount, expireDate);

        // 1. 获取用户信息（用于模板变量替换）
        Person person = userClient.gainActualPerson(userId).getBody();
        String username = "用户";
        String phone = null;
        if (person != null) {
            username = person.getFirstName() != null ? person.getFirstName() : "用户";
            phone = person.getPhone();
            log.info("【用户信息】用户ID: {}, 姓名: {}, 电话: {}", userId, username, phone);
        } else {
            log.warn("【用户信息】用户ID: {} 的用户信息不存在，使用默认值", userId);
        }

        // 2. 替换模板变量生成通知内容
        String notificationContent = config.getSmsTemplate()
            .replace("{username}", username)
            .replace("{points}", String.valueOf(pointsAmount))
            .replace("{expireDate}", expireDate.toString());
        log.info("【通知内容】用户ID: {}, 原始模板: {}, 生成内容: {}",
                userId, config.getSmsTemplate(), notificationContent);

        // 3. 创建站内通知（存储在 notification 表中）
        Long notificationId = null;
        try {
            NotificationSendDTO notification = new NotificationSendDTO();
            notification.setReceiverId(userId);
            notification.setType(2); // 2=积分过期预警
            notification.setText(notificationContent);
            notification.setAuditResult(null); // 积分预警不需要审核结果

            notificationId = notificationClient.sendNotification(notification).getData();

            log.info("【通知创建】用户ID: {}, 通知ID: {}, 通知内容: {}",
                    userId, notificationId, notificationContent);

            // 4. 通过WebSocket推送通知
            try {
                String wsMessage = String.format("{\"type\": \"points_expiration_alert\", " +
                    "\"notificationId\": %d, \"points\": %d, \"expireDate\": \"%s\"}",
                        notificationId, pointsAmount, expireDate);
                webSocketServer.sendToClient(userId.toString(), wsMessage);
                log.info("【WebSocket推送】用户ID: {}, 通知ID: {}, 推送成功", userId, notificationId);
            } catch (Exception e) {
                log.warn("【WebSocket推送】用户ID: {}, 通知ID: {}, 推送失败，错误: {}",
                        userId, notificationId, e.getMessage(), e);
                // WebSocket推送失败不影响整体流程
            }
        } catch (Exception e) {
            log.error("【通知创建失败】用户ID: {}, 创建通知失败，错误: {}", userId, e.getMessage(), e);
            // 通知创建失败不影响预警日志记录
        }

        // 5. 记录预警日志
        LocalDateTime now = LocalDateTime.now();
        // 如果 alert_cycle 为 null，表示只预警一次，next_alert_time 设置为 null
        LocalDateTime nextAlertTime = null;
        if (config.getAlertCycle() != null) {
            nextAlertTime = now.plusDays(config.getAlertCycle());
            log.info("【预警周期】用户ID: {}, alert_cycle: {}天, 下次预警时间: {}",
                    userId, config.getAlertCycle(), nextAlertTime);
        } else {
            log.info("【预警周期】用户ID: {}, alert_cycle为null，只预警一次，下次预警时间为null", userId);
        }

        if (existingLog == null) {
            // 创建新记录
            PointsExpirationAlertLog alertLog = new PointsExpirationAlertLog();
            alertLog.setUserId(userId);
            alertLog.setPointsAmount(pointsAmount);
            alertLog.setExpireDate(expireDate);
            alertLog.setAlertTime(now);
            alertLog.setNextAlertTime(nextAlertTime); // 可能为 null（只预警一次）
            alertLog.setIsSent(true);
            alertLog.setPhone(phone); // 可能为null
            alertLog.setCreateTime(now);
            alertLogMapper.insert(alertLog);
            log.info("【预警日志】用户ID: {}, 创建新的预警日志记录，下次预警时间: {}",
                    userId, nextAlertTime);
        } else {
            // 更新现有记录
            existingLog.setAlertTime(now);
            existingLog.setNextAlertTime(nextAlertTime); // 可能为 null（只预警一次）
            existingLog.setIsSent(true);
            alertLogMapper.updateById(existingLog);
            log.info("【预警日志】用户ID: {}, 更新现有预警日志记录，上次预警时间: {}, 下次预警时间: {}",
                    userId, existingLog.getAlertTime(), nextAlertTime);
        }

        log.info("【发送预警详情】用户ID: {} 的预警发送流程完成", userId);
    }
}

