package com.tju.elm.point.service;


import com.tju.elm.point.zoo.pojo.vo.PointsAccountVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 积分缓存服务接口
 * 职责：管理积分相关的 Redis 缓存
 * 设计原则：单一职责原则 - 只负责缓存操作
 */
public interface PointsCacheService {
    
    /**
     * 获取积分账户缓存
     * @param userId 用户ID
     * @return 积分账户信息，如果缓存不存在返回 null
     */
    PointsAccountVO getAccountCache(Long userId);
    
    /**
     * 设置积分账户缓存
     * @param userId 用户ID
     * @param account 积分账户信息
     */
    void setAccountCache(Long userId, PointsAccountVO account);
    
    /**
     * 删除积分账户缓存
     * @param userId 用户ID
     */
    void deleteAccountCache(Long userId);
    
    /**
     * 获取积分兑换比例缓存
     * @return 积分兑换比例，如果缓存不存在返回 null
     */
    BigDecimal getExchangeRatioCache();
    
    /**
     * 设置积分兑换比例缓存
     * @param ratio 积分兑换比例
     */
    void setExchangeRatioCache(BigDecimal ratio);
    
    /**
     * 删除积分兑换比例缓存
     */
    void deleteExchangeRatioCache();
    
    /**
     * 获取即将过期积分列表缓存
     * @param userId 用户ID
     * @return 即将过期积分列表，如果缓存不存在返回 null
     */
    List<?> getExpiringPointsCache(Long userId);
    
    /**
     * 设置即将过期积分列表缓存
     * @param userId 用户ID
     * @param expiringPoints 即将过期积分列表
     */
    void setExpiringPointsCache(Long userId, List<?> expiringPoints);
    
    /**
     * 删除即将过期积分列表缓存
     * @param userId 用户ID
     */
    void deleteExpiringPointsCache(Long userId);
    
    /**
     * 删除用户相关的所有缓存
     * @param userId 用户ID
     */
    void deleteUserAllCache(Long userId);
}

