package com.tju.elm_bk.service.impl;

import com.tju.elm_bk.mapper.NotificationMapper;
import com.tju.elm_bk.mapper.PersonMapper;
import com.tju.elm_bk.mapper.PointsExpirationAlertConfigMapper;
import com.tju.elm_bk.mapper.PointsExpirationAlertLogMapper;
import com.tju.elm_bk.mapper.PointsExpirationMapper;
import com.tju.elm_bk.pojo.entity.Notification;
import com.tju.elm_bk.pojo.entity.Person;
import com.tju.elm_bk.pojo.entity.PointsExpiration;
import com.tju.elm_bk.pojo.entity.PointsExpirationAlertConfig;
import com.tju.elm_bk.pojo.entity.PointsExpirationAlertLog;
import com.tju.elm_bk.service.PointsExpirationAlertService;
import com.tju.elm_bk.websocket.WebSocketServer;
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

    @Autowired
    private PersonMapper personMapper;
    
    @Autowired
    private NotificationMapper notificationMapper;
    
    @Autowired
    private WebSocketServer webSocketServer;

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
        // 1. 查询预警配置
        PointsExpirationAlertConfig config = alertConfigMapper.selectConfig();
        if (config == null || !config.getIsEnabled()) {
            log.info("预警配置不存在或已禁用，跳过预警任务");
            return;
        }

        // 2. 计算预警日期
        LocalDate alertDate = LocalDate.now().plusDays(config.getAlertDays());

        // 3. 查询即将过期的积分
        List<PointsExpiration> expiringList = pointsExpirationMapper.selectByExpireDate(alertDate);

        // 4. 检查并发送预警
        for (PointsExpiration exp : expiringList) {
            try {
                // 检查是否需要发送预警
                PointsExpirationAlertLog existingLog = alertLogMapper.selectByUserIdAndExpireDate(
                    exp.getUserId(), alertDate);
                
                boolean shouldSend = false;
                if (existingLog == null) {
                    // 第一次预警
                    shouldSend = true;
                } else if (config.getAlertCycle() == null) {
                    // alert_cycle 为 null 表示只预警一次，已有记录则不发送
                    shouldSend = false;
                } else if (existingLog.getNextAlertTime() != null && 
                          existingLog.getNextAlertTime().isBefore(LocalDateTime.now())) {
                    // 到了下次预警时间
                    shouldSend = true;
                }

                if (shouldSend) {
                    sendAlert(exp.getUserId(), exp.getPointsAmount(), alertDate, config, existingLog);
                }
            } catch (Exception e) {
                log.error("发送积分到期预警失败，用户ID: {}, 错误: {}", exp.getUserId(), e.getMessage(), e);
            }
        }
    }

    /**
     * 发送预警通知（创建 notification 记录并通过 WebSocket 推送）
     * 设计原则：封装与抽象 - 封装发送逻辑
     */
    private void sendAlert(Long userId, Long pointsAmount, LocalDate expireDate, 
                          PointsExpirationAlertConfig config, PointsExpirationAlertLog existingLog) {
        // 1. 获取用户信息（用于模板变量替换）
        Person person = personMapper.getPersonByUserId(userId);
        String username = "用户";
        String phone = null;
        if (person != null) {
            username = person.getFirstName() != null ? person.getFirstName() : "用户";
            phone = person.getPhone();
        }

        // 2. 替换模板变量生成通知内容
        String notificationContent = config.getSmsTemplate()
            .replace("{username}", username)
            .replace("{points}", String.valueOf(pointsAmount))
            .replace("{expireDate}", expireDate.toString());

        // 3. 创建站内通知（存储在 notification 表中）
        try {
            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setNotificationType(2); // 2=积分过期预警
            notification.setNotificationContent(notificationContent);
            notification.setAuditResult(null); // 积分预警不需要审核结果
            notification.setIsRead(0);
            notification.setIsDeleted(0);
            notification.setCreateTime(LocalDateTime.now());
            notificationMapper.insert(notification);
            
            log.info("积分到期预警通知已创建，用户: {}, 内容: {}", userId, notificationContent);
            
            // 4. 通过WebSocket推送通知
            try {
                String wsMessage = String.format("{\"type\": \"points_expiration_alert\", " +
                    "\"notificationId\": %d, \"points\": %d, \"expireDate\": \"%s\"}", 
                    notification.getId(), pointsAmount, expireDate.toString());
                webSocketServer.sendToClient(userId.toString(), wsMessage);
                log.info("积分到期预警WebSocket通知已推送，用户: {}", userId);
            } catch (Exception e) {
                log.warn("积分到期预警WebSocket通知推送失败，用户: {}, 错误: {}", userId, e.getMessage());
                // WebSocket推送失败不影响整体流程
            }
        } catch (Exception e) {
            log.error("创建积分到期预警通知失败，用户: {}, 错误: {}", userId, e.getMessage(), e);
            // 通知创建失败不影响预警日志记录
        }

        // 5. 记录预警日志
        LocalDateTime now = LocalDateTime.now();
        // 如果 alert_cycle 为 null，表示只预警一次，next_alert_time 设置为 null
        LocalDateTime nextAlertTime = null;
        if (config.getAlertCycle() != null) {
            nextAlertTime = now.plusDays(config.getAlertCycle());
        }
        
        if (existingLog == null) {
            // 创建新记录
            PointsExpirationAlertLog log = new PointsExpirationAlertLog();
            log.setUserId(userId);
            log.setPointsAmount(pointsAmount);
            log.setExpireDate(expireDate);
            log.setAlertTime(now);
            log.setNextAlertTime(nextAlertTime); // 可能为 null（只预警一次）
            log.setIsSent(true);
            log.setPhone(phone); // 可能为null
            log.setCreateTime(now);
            alertLogMapper.insert(log);
        } else {
            // 更新现有记录
            existingLog.setAlertTime(now);
            existingLog.setNextAlertTime(nextAlertTime); // 可能为 null（只预警一次）
            existingLog.setIsSent(true);
            alertLogMapper.updateById(existingLog);
        }
    }
}

