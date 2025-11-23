package com.tju.elm_bk.service.impl;

import com.tju.elm_bk.mapper.PersonMapper;
import com.tju.elm_bk.mapper.PointsExpirationAlertConfigMapper;
import com.tju.elm_bk.mapper.PointsExpirationAlertLogMapper;
import com.tju.elm_bk.mapper.PointsExpirationMapper;
import com.tju.elm_bk.pojo.entity.Person;
import com.tju.elm_bk.pojo.entity.PointsExpiration;
import com.tju.elm_bk.pojo.entity.PointsExpirationAlertConfig;
import com.tju.elm_bk.pojo.entity.PointsExpirationAlertLog;
import com.tju.elm_bk.service.PointsExpirationAlertService;
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
     * 发送预警短信
     * 设计原则：封装与抽象 - 封装发送逻辑
     */
    private void sendAlert(Long userId, Long pointsAmount, LocalDate expireDate, 
                          PointsExpirationAlertConfig config, PointsExpirationAlertLog existingLog) {
        // 1. 获取用户信息
        Person person = personMapper.getPersonByUserId(userId);
        if (person == null || person.getPhone() == null) {
            log.warn("用户不存在或未设置手机号，用户ID: {}", userId);
            return;
        }

        // 2. 替换模板变量
        String smsContent = config.getSmsTemplate()
            .replace("{username}", person.getFirstName() != null ? person.getFirstName() : "用户")
            .replace("{points}", String.valueOf(pointsAmount))
            .replace("{expireDate}", expireDate.toString());

        // 3. 发送短信（这里简化处理，实际应该调用短信服务）
        log.info("发送积分到期预警短信，用户: {}, 手机号: {}, 内容: {}", 
            userId, person.getPhone(), smsContent);
        // TODO: 调用短信服务发送短信

        // 4. 记录预警日志
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextAlertTime = now.plusDays(config.getAlertCycle());
        
        if (existingLog == null) {
            // 创建新记录
            PointsExpirationAlertLog log = new PointsExpirationAlertLog();
            log.setUserId(userId);
            log.setPointsAmount(pointsAmount);
            log.setExpireDate(expireDate);
            log.setAlertTime(now);
            log.setNextAlertTime(nextAlertTime);
            log.setIsSent(true);
            log.setPhone(person.getPhone());
            log.setCreateTime(now);
            alertLogMapper.insert(log);
        } else {
            // 更新现有记录
            existingLog.setAlertTime(now);
            existingLog.setNextAlertTime(nextAlertTime);
            existingLog.setIsSent(true);
            alertLogMapper.updateById(existingLog);
        }
    }
}

