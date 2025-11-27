package com.tju.elm_bk.service;

import com.tju.elm_bk.pojo.entity.PointsExpirationAlertConfig;

/**
 * 积分到期预警服务接口
 * 职责：积分到期预警配置管理和预警任务执行
 * 设计原则：单一职责原则 - 只负责预警相关功能
 */
public interface PointsExpirationAlertService {
    
    /**
     * 查询预警配置
     * @return 预警配置
     */
    PointsExpirationAlertConfig getAlertConfig();
    
    /**
     * 更新预警配置
     * @param config 预警配置
     * @return 是否成功
     */
    Boolean updateAlertConfig(PointsExpirationAlertConfig config);
    
    /**
     * 检查并发送即将过期积分预警（定时任务调用）
     */
    void checkAndSendExpirationAlerts();
}

