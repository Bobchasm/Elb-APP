package com.tju.elm_bk.service.impl;

import com.tju.elm_bk.exception.APIException;
import com.tju.elm_bk.mapper.PointsAccountMapper;
import com.tju.elm_bk.mapper.PointsExpirationMapper;
import com.tju.elm_bk.mapper.PointsTransactionMapper;
import com.tju.elm_bk.pojo.entity.PointsAccount;
import com.tju.elm_bk.pojo.entity.PointsExpiration;
import com.tju.elm_bk.pojo.entity.PointsTransaction;
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
    
    @Autowired
    private PointsTransactionMapper pointsTransactionMapper;

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
        LocalDateTime currentTime = LocalDateTime.now();
        List<PointsExpiration> expiringList = pointsExpirationMapper.selectExpiringPoints(userId, currentTime);
        List<PointsExpirationVO> voList = convertToVOList(expiringList);
        
        // 3. 写入缓存
        pointsCacheService.setExpiringPointsCache(userId, voList);
        
        return voList;
    }

    @Override
    public List<PointsExpirationVO> getExpiredPoints(Long userId) {
        // 查询指定用户已过期的积分记录
        LocalDateTime currentTime = LocalDateTime.now();
        List<PointsExpiration> expiredList = pointsExpirationMapper.selectExpiredPointsByUserId(userId, currentTime);
        return convertToVOList(expiredList);
    }

    @Override
    public List<PointsExpirationVO> getPointsByExpireDate(LocalDate expireDate) {
        List<PointsExpiration> expirationList = pointsExpirationMapper.selectByExpireDate(expireDate);
        return convertToVOList(expirationList);
    }

    @Override
    public Long countExpiringPoints(Long userId) {
        LocalDateTime currentTime = LocalDateTime.now();
        List<PointsExpiration> expiringList = pointsExpirationMapper.selectExpiringPoints(userId, currentTime);
        return expiringList.stream()
                .mapToLong(PointsExpiration::getPointsAmount)
                .sum();
    }

    @Override
    @Transactional
    public void processExpiredPoints() {
        log.info("开始处理积分过期...");
        
        // 1. 查询所有已过期但未标记的积分记录（使用Java时区，确保是东八区）
        LocalDateTime currentTime = LocalDateTime.now();
        List<PointsExpiration> expiredList = pointsExpirationMapper.selectExpiredPoints(currentTime);
        
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
                
                // 2.2 查询原始交易记录（用于获取积分来源等信息）
                PointsTransaction originalTransaction = null;
                if (expiration.getTransactionId() != null) {
                    originalTransaction = pointsTransactionMapper.selectById(expiration.getTransactionId());
                }
                
                // 2.3 更新积分账户（减少可用积分和总积分）
                PointsAccount account = pointsAccountMapper.selectForUpdate(expiration.getUserId());
                if (account != null) {
                    Long expiredAmount = expiration.getPointsAmount();
                    Long newAvailablePoints = account.getAvailablePoints() - expiredAmount;
                    Long newTotalPoints = account.getTotalPoints() - expiredAmount;
                    
                    // 验证数据完整性：抛出异常而不是自动修正
                    if (newAvailablePoints < 0) {
                        log.error("【数据异常】用户{}的可用积分计算结果为负数: {}, 账户ID: {}, 过期积分: {}", 
                                expiration.getUserId(), newAvailablePoints, account.getId(), expiredAmount);
                        throw new APIException("POINTS_DATA_INCONSISTENT", 
                                String.format("积分账户数据异常：可用积分为负数 %d（过期扣减 %d），请联系管理员", 
                                        newAvailablePoints, expiredAmount));
                    }
                    if (newTotalPoints < 0) {
                        log.error("【数据异常】用户{}的总积分计算结果为负数: {}, 账户ID: {}, 过期积分: {}", 
                                expiration.getUserId(), newTotalPoints, account.getId(), expiredAmount);
                        throw new APIException("POINTS_DATA_INCONSISTENT", 
                                String.format("积分账户数据异常：总积分为负数 %d（过期扣减 %d），请联系管理员", 
                                        newTotalPoints, expiredAmount));
                    }
                    
                    // 验证业务逻辑：总积分 = 可用积分 + 冻结积分
                    if (!newTotalPoints.equals(newAvailablePoints + account.getFrozenPoints())) {
                        log.error("【数据异常】用户{}的积分账户数据不一致: 总积分={}, 可用积分={}, 冻结积分={}, 账户ID: {}", 
                                expiration.getUserId(), newTotalPoints, newAvailablePoints, 
                                account.getFrozenPoints(), account.getId());
                        throw new APIException("POINTS_DATA_INCONSISTENT",
                                String.format("积分账户数据不一致：总积分(%d)不等于可用积分(%d)+冻结积分(%d)，请联系管理员", 
                                        newTotalPoints, newAvailablePoints, account.getFrozenPoints()));
                    }
                    
                    account.setAvailablePoints(newAvailablePoints);
                    account.setTotalPoints(newTotalPoints);
                    account.setUpdateTime(LocalDateTime.now());
                    pointsAccountMapper.updateById(account);
                    
                    // 2.4 插入过期交易记录（transaction_type = 2）
                    PointsTransaction expireTransaction = new PointsTransaction();
                    expireTransaction.setUserId(expiration.getUserId());
                    expireTransaction.setAccountId(account.getId());
                    expireTransaction.setTransactionType(2); // 2-过期
                    // 从原始交易记录中获取积分来源等信息
                    if (originalTransaction != null) {
                        expireTransaction.setPointsSource(originalTransaction.getPointsSource());
                        expireTransaction.setRelatedOrderId(originalTransaction.getRelatedOrderId());
                        expireTransaction.setRelatedFoodId(originalTransaction.getRelatedFoodId());
                        expireTransaction.setRelatedRuleId(originalTransaction.getRelatedRuleId());
                    }
                    expireTransaction.setPointsChange(-expiredAmount); // 负数表示扣减
                    expireTransaction.setPointsBalance(newAvailablePoints); // 扣减后的可用积分余额
                    expireTransaction.setExpireTime(null); // 已过期，不再有过期时间
                    expireTransaction.setDescription("积分过期扣减");
                    LocalDateTime now = LocalDateTime.now();
                    expireTransaction.setCreateTime(now);
                    expireTransaction.setUpdateTime(now);
                    expireTransaction.setCreator(1L); // 系统用户ID（定时任务）
                    expireTransaction.setUpdater(1L); // 系统用户ID（定时任务）
                    expireTransaction.setIsDeleted(false);
                    pointsTransactionMapper.insert(expireTransaction);
                    
                    // 2.5 删除相关缓存
                    pointsCacheService.deleteUserAllCache(expiration.getUserId());
                    
                    log.info("用户{}的{}积分已过期，已从账户中扣除，交易记录ID: {}", 
                            expiration.getUserId(), expiredAmount, expireTransaction.getId());
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

