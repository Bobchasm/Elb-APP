package com.tju.elm_bk.service.impl;

import com.tju.elm_bk.mapper.PointsAccountMapper;
import com.tju.elm_bk.mapper.PointsExpirationMapper;
import com.tju.elm_bk.pojo.entity.PointsAccount;
import com.tju.elm_bk.pojo.entity.PointsExpiration;
import com.tju.elm_bk.pojo.vo.PointsExpirationVO;
import com.tju.elm_bk.service.PointsCacheService;
import com.tju.elm_bk.service.PointsExpirationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * 积分过期记录服务实现类
 * 职责：积分过期记录的查询、统计和管理
 * 设计原则：单一职责原则 - 只负责过期记录管理
 */
@Slf4j
@Service
public class PointsExpirationServiceImpl implements PointsExpirationService {

    @Autowired
    private PointsExpirationMapper pointsExpirationMapper;
    
    @Autowired
    private PointsAccountMapper pointsAccountMapper;
    
    @Autowired
    private PointsCacheService pointsCacheService;

    @Override
    public List<PointsExpirationVO> getExpiringPoints(Long userId) {
        // 1. 先查缓存
        List<?> cached = pointsCacheService.getExpiringPointsCache(userId);
        if (cached != null) {
            @SuppressWarnings("unchecked")
            List<PointsExpirationVO> result = (List<PointsExpirationVO>) cached;
            return result;
        }
        
        // 2. 缓存未命中，查数据库
        List<PointsExpiration> expiringList = pointsExpirationMapper.selectExpiringPoints(userId);
        List<PointsExpirationVO> voList = convertToVOList(expiringList);
        
        // 3. 写入缓存
        pointsCacheService.setExpiringPointsCache(userId, voList);
        
        return voList;
    }

    @Override
    public List<PointsExpirationVO> getExpiredPoints(Long userId) {
        // 查询指定用户已过期的积分记录
        List<PointsExpiration> expiredList = pointsExpirationMapper.selectExpiredPointsByUserId(userId);
        return convertToVOList(expiredList);
    }

    @Override
    public List<PointsExpirationVO> getPointsByExpireDate(LocalDate expireDate) {
        List<PointsExpiration> expirationList = pointsExpirationMapper.selectByExpireDate(expireDate);
        return convertToVOList(expirationList);
    }

    @Override
    public Long countExpiringPoints(Long userId) {
        List<PointsExpiration> expiringList = pointsExpirationMapper.selectExpiringPoints(userId);
        return expiringList.stream()
                .mapToLong(PointsExpiration::getPointsAmount)
                .sum();
    }

    @Override
    @Transactional
    public void processExpiredPoints() {
        log.info("开始处理积分过期...");
        
        // 1. 查询所有已过期但未标记的积分记录
        List<PointsExpiration> expiredList = pointsExpirationMapper.selectExpiredPoints();
        
        if (expiredList.isEmpty()) {
            log.info("没有需要处理的过期积分");
            return;
        }
        
        log.info("找到{}条过期积分记录需要处理", expiredList.size());
        
        // 2. 按用户分组处理
        for (PointsExpiration expiration : expiredList) {
            try {
                // 2.1 标记为已过期
                pointsExpirationMapper.markAsExpired(expiration.getId());
                
                // 2.2 更新积分账户（减少可用积分和总积分）
                PointsAccount account = pointsAccountMapper.selectForUpdate(expiration.getUserId());
                if (account != null) {
                    Long expiredAmount = expiration.getPointsAmount();
                    account.setAvailablePoints(account.getAvailablePoints() - expiredAmount);
                    account.setTotalPoints(account.getTotalPoints() - expiredAmount);
                    account.setUpdateTime(LocalDateTime.now());
                    pointsAccountMapper.updateById(account);
                    
                    log.info("用户{}的{}积分已过期，已从账户中扣除", 
                            expiration.getUserId(), expiredAmount);
                }
            } catch (Exception e) {
                log.error("处理过期积分失败: expirationId={}, error={}", 
                        expiration.getId(), e.getMessage(), e);
                // 继续处理下一条，不中断整个流程
            }
        }
        
        log.info("积分过期处理完成");
    }

    /**
     * 将实体列表转换为VO列表
     */
    private List<PointsExpirationVO> convertToVOList(List<PointsExpiration> expirationList) {
        List<PointsExpirationVO> voList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (PointsExpiration expiration : expirationList) {
            PointsExpirationVO vo = new PointsExpirationVO();
            BeanUtils.copyProperties(expiration, vo);
            
            // 计算距离过期的天数
            if (expiration.getExpireTime() != null && !expiration.getIsExpired()) {
                long days = ChronoUnit.DAYS.between(now.toLocalDate(), expiration.getExpireTime().toLocalDate());
                vo.setDaysUntilExpiration(days);
            } else {
                vo.setDaysUntilExpiration(null);
            }
            
            voList.add(vo);
        }
        
        return voList;
    }
}

