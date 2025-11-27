package com.tju.elm_bk.service;

import com.tju.elm_bk.pojo.entity.PointsExpiration;
import com.tju.elm_bk.pojo.vo.PointsExpirationVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 积分过期记录服务接口
 * 职责：积分过期记录的查询、统计和管理
 * 设计原则：单一职责原则 - 只负责过期记录管理
 */
public interface PointsExpirationService {
    
    /**
     * 查询用户即将过期的积分记录（按过期时间升序）
     * @param userId 用户ID
     * @return 即将过期的积分记录列表
     */
    List<PointsExpirationVO> getExpiringPoints(Long userId);
    
    /**
     * 查询用户已过期的积分记录
     * @param userId 用户ID
     * @return 已过期的积分记录列表
     */
    List<PointsExpirationVO> getExpiredPoints(Long userId);
    
    /**
     * 根据过期日期查询即将过期的积分记录
     * @param expireDate 过期日期
     * @return 即将过期的积分记录列表
     */
    List<PointsExpirationVO> getPointsByExpireDate(LocalDate expireDate);
    
    /**
     * 统计用户即将过期的积分总数
     * @param userId 用户ID
     * @return 即将过期的积分总数
     */
    Long countExpiringPoints(Long userId);
    
    /**
     * 处理积分过期（定时任务调用）
     * 将已过期的积分记录标记为已过期，并更新积分账户
     */
    void processExpiredPoints();
}

