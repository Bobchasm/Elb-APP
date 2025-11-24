package com.tju.elm_bk.service.impl;

import com.tju.elm_bk.exception.APIException;
import com.tju.elm_bk.mapper.*;
import com.tju.elm_bk.pojo.dto.PointsAddDTO;
import com.tju.elm_bk.pojo.dto.PointsDeductDTO;
import com.tju.elm_bk.pojo.entity.*;
import com.tju.elm_bk.pojo.vo.PointsAccountVO;
import com.tju.elm_bk.pojo.vo.PointsTransactionVO;
import com.tju.elm_bk.result.ResultCodeEnum;
import com.tju.elm_bk.service.PointsCacheService;
import com.tju.elm_bk.service.PointsService;
import com.tju.elm_bk.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 积分系统核心服务实现类
 * 职责：积分账户操作（增加、减少、查询、查询明细）
 * 设计原则：
 * 1. 单一职责原则 - 只负责积分账户操作
 * 2. 依赖注入 - 通过@Autowired注入Mapper
 * 3. 事务管理 - 使用@Transactional保证数据一致性
 */
@Slf4j
@Service
public class PointsServiceImpl implements PointsService {

    @Autowired
    private PointsAccountMapper pointsAccountMapper;

    @Autowired
    private PointsTransactionMapper pointsTransactionMapper;

    @Autowired
    private PointsExpirationMapper pointsExpirationMapper;

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private MarketingPointsRuleMapper marketingPointsRuleMapper;
    
    @Autowired
    private MarketingPointsExchangeRuleMapper exchangeRuleMapper;
    
    @Autowired
    private PointsCacheService pointsCacheService;

    /**
     * 增加积分
     * 设计原则：事务管理 - 保证积分账户和明细的一致性
     */
    @Override
    @Transactional
    public Long addPoints(PointsAddDTO pointsAddDTO) {
        // 1. 参数校验
        if (pointsAddDTO.getUserId() == null || pointsAddDTO.getPoints() == null || 
            pointsAddDTO.getPoints() <= 0) {
            throw new APIException(ResultCodeEnum.PARAM_NOT_MATCHED);
        }

        if (pointsAddDTO.getExpireTime() == null) {
            pointsAddDTO.setExpireTime(LocalDateTime.now().plusMonths(1));
        }

        // 2. 查询或创建积分账户（带行锁，保证并发安全）
        PointsAccount account = pointsAccountMapper.selectForUpdate(pointsAddDTO.getUserId());
        if (account == null) {
            // 如果账户不存在，创建新账户
            account = new PointsAccount();
            account.setUserId(pointsAddDTO.getUserId());
            account.setTotalPoints(0L);
            account.setAvailablePoints(0L);
            account.setFrozenPoints(0L);
            account.setMemberLevel(0);
            account.setCreateTime(LocalDateTime.now());
            account.setUpdateTime(LocalDateTime.now());
            account.setIsDeleted(false);
            // 获取当前用户ID作为创建人
            Long currentUserId = getCurrentUserId();
            account.setCreator(currentUserId);
            account.setUpdater(currentUserId);
            pointsAccountMapper.insert(account);
        }

        // 3. 更新积分余额
        // 判断是否是订单奖励积分（需要冻结）
        boolean isOrderReward = (pointsAddDTO.getPointsSource() == 0 && 
                                pointsAddDTO.getRelatedOrderId() != null);
        
        Long newTotalPoints = account.getTotalPoints() + pointsAddDTO.getPoints();
        Long newAvailablePoints = account.getAvailablePoints();
        Long newFrozenPoints = account.getFrozenPoints();
        
        if (isOrderReward) {
            // 订单奖励积分：增加总积分和冻结积分，不增加可用积分
            newFrozenPoints = account.getFrozenPoints() + pointsAddDTO.getPoints();
        } else {
            // 其他积分（行为积分等）：直接增加可用积分
            newAvailablePoints = account.getAvailablePoints() + pointsAddDTO.getPoints();
        }
        
        account.setTotalPoints(newTotalPoints);
        account.setAvailablePoints(newAvailablePoints);
        account.setFrozenPoints(newFrozenPoints);
        account.setUpdateTime(LocalDateTime.now());
        Long currentUserId = getCurrentUserId();
        account.setUpdater(currentUserId);
        pointsAccountMapper.updateById(account);

        // 4. 记录积分明细
        PointsTransaction transaction = new PointsTransaction();
        transaction.setUserId(pointsAddDTO.getUserId());
        transaction.setAccountId(account.getId());
        transaction.setTransactionType(0); // 0-获得
        if (isOrderReward) {
            // 订单奖励积分：记录为获得类型，但积分是冻结的
            transaction.setDescription(pointsAddDTO.getDescription() + "（冻结中，订单完成后解冻）");
        } else {
            // 其他积分：正常获得
            transaction.setDescription(pointsAddDTO.getDescription());
        }
        transaction.setPointsSource(pointsAddDTO.getPointsSource());
        transaction.setPointsChange(pointsAddDTO.getPoints());
        transaction.setPointsBalance(newAvailablePoints);
        transaction.setExpireTime(pointsAddDTO.getExpireTime());
        transaction.setRelatedOrderId(pointsAddDTO.getRelatedOrderId());
        transaction.setRelatedFoodId(pointsAddDTO.getRelatedFoodId());
        transaction.setRelatedRuleId(pointsAddDTO.getRelatedRuleId());
        transaction.setCreateTime(LocalDateTime.now());
        transaction.setCreator(currentUserId);
        transaction.setIsDeleted(false);
        pointsTransactionMapper.insert(transaction);

        // 5. 创建过期记录（所有积分都必须有有效期）
        PointsExpiration expiration = new PointsExpiration();
        expiration.setUserId(pointsAddDTO.getUserId());
        expiration.setTransactionId(transaction.getId());
        expiration.setPointsAmount(pointsAddDTO.getPoints());
        expiration.setExpireTime(pointsAddDTO.getExpireTime());
        expiration.setExpireDate(pointsAddDTO.getExpireTime().toLocalDate());
        expiration.setIsExpired(false);
        expiration.setCreateTime(LocalDateTime.now());
        pointsExpirationMapper.insert(expiration);

        // 6. 删除相关缓存（写操作后清除缓存，保证数据一致性）
        pointsCacheService.deleteUserAllCache(pointsAddDTO.getUserId());

        return transaction.getId();
    }

    /**
     * 减少积分（优先扣减即将过期的积分）
     * 设计原则：
     * 1. 优先扣减即将过期的积分算法
     * 2. 事务管理 - 保证数据一致性
     */
    @Override
    @Transactional
    public Boolean deductPoints(PointsDeductDTO pointsDeductDTO) {
        // 1. 参数校验
        if (pointsDeductDTO.getUserId() == null || pointsDeductDTO.getPoints() == null || 
            pointsDeductDTO.getPoints() <= 0) {
            throw new APIException(ResultCodeEnum.PARAM_NOT_MATCHED);
        }

        // 2. 查询积分账户（带行锁）
        PointsAccount account = pointsAccountMapper.selectForUpdate(pointsDeductDTO.getUserId());
        if (account == null || account.getAvailablePoints() < pointsDeductDTO.getPoints()) {
            throw new APIException("POINTS_INSUFFICIENT", "积分不足");
        }

        // 3. 优先扣减即将过期的积分
        Long remainingPoints = pointsDeductDTO.getPoints();
        List<PointsExpiration> expiringPoints = pointsExpirationMapper.selectExpiringPoints(
            pointsDeductDTO.getUserId());

        // 按过期时间升序扣减
        for (PointsExpiration exp : expiringPoints) {
            if (remainingPoints <= 0) {
                break;
            }

            Long deductAmount = Math.min(remainingPoints, exp.getPointsAmount());
            exp.setPointsAmount(exp.getPointsAmount() - deductAmount);
            
            if (exp.getPointsAmount() <= 0) {
                exp.setIsExpired(true);
            }
            pointsExpirationMapper.updateById(exp);
            
            remainingPoints -= deductAmount;
        }

        // 4. 如果还有剩余积分需要扣减，说明即将过期的积分不足
        // 由于所有积分都有有效期，如果即将过期的积分不足，说明积分不足
        if (remainingPoints > 0) {
            throw new APIException("POINTS_INSUFFICIENT", "可用积分不足，请检查积分有效期");
        }

        // 5. 更新积分账户余额
        Long newAvailablePoints = account.getAvailablePoints() - pointsDeductDTO.getPoints();
        Long newTotalPoints = account.getTotalPoints() - pointsDeductDTO.getPoints();
        account.setAvailablePoints(newAvailablePoints);
        account.setTotalPoints(newTotalPoints);
        account.setUpdateTime(LocalDateTime.now());
        Long currentUserId = getCurrentUserId();
        account.setUpdater(currentUserId);
        pointsAccountMapper.updateBalance(account);

        // 6. 记录积分明细
        PointsTransaction transaction = new PointsTransaction();
        transaction.setUserId(pointsDeductDTO.getUserId());
        transaction.setAccountId(account.getId());
        transaction.setTransactionType(1); // 1-消费
        transaction.setPointsSource(pointsDeductDTO.getPointsSource());
        transaction.setPointsChange(-pointsDeductDTO.getPoints()); // 负数表示减少
        transaction.setPointsBalance(newAvailablePoints);
        transaction.setRelatedOrderId(pointsDeductDTO.getRelatedOrderId());
        transaction.setRelatedFoodId(pointsDeductDTO.getRelatedFoodId());
        transaction.setDescription(pointsDeductDTO.getDescription());
        transaction.setCreateTime(LocalDateTime.now());
        transaction.setCreator(currentUserId);
        transaction.setIsDeleted(false);
        pointsTransactionMapper.insert(transaction);

        // 7. 删除相关缓存（写操作后清除缓存，保证数据一致性）
        pointsCacheService.deleteUserAllCache(pointsDeductDTO.getUserId());

        return true;
    }

    /**
     * 查询用户积分账户（带缓存）
     * 设计原则：Cache-Aside 模式 - 先查缓存，未命中查数据库
     */
    @Override
    public PointsAccountVO getPointsAccount(Long userId) {
        // 1. 先查缓存
        PointsAccountVO cached = pointsCacheService.getAccountCache(userId);
        if (cached != null) {
            log.debug("从缓存获取积分账户，userId: {}", userId);
            return cached;
        }
        
        // 2. 缓存未命中，查数据库
        PointsAccount account = pointsAccountMapper.selectByUserId(userId);
        PointsAccountVO vo;
        if (account == null) {
            // 如果账户不存在，返回默认值
            vo = new PointsAccountVO();
            vo.setUserId(userId);
            vo.setTotalPoints(0L);
            vo.setAvailablePoints(0L);
            vo.setFrozenPoints(0L);
            vo.setMemberLevel(0);
            vo.setMemberLevelName(getMemberLevelName(0));
        } else {
            vo = new PointsAccountVO();
            BeanUtils.copyProperties(account, vo);
            vo.setMemberLevelName(getMemberLevelName(account.getMemberLevel()));
        }
        
        // 3. 写入缓存
        pointsCacheService.setAccountCache(userId, vo);
        return vo;
    }

    /**
     * 查询积分明细
     */
    @Override
    public List<PointsTransactionVO> getPointsTransactions(Long userId, Integer pageNum, 
                                                           Integer pageSize, Integer transactionType) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        Integer offset = (pageNum - 1) * pageSize;
        List<PointsTransaction> transactions = pointsTransactionMapper.selectByUserId(
            userId, transactionType, offset, pageSize);

        List<PointsTransactionVO> voList = new ArrayList<>();
        for (PointsTransaction trans : transactions) {
            PointsTransactionVO vo = new PointsTransactionVO();
            BeanUtils.copyProperties(trans, vo);
            vo.setTransactionTypeName(getTransactionTypeName(trans.getTransactionType()));
            vo.setPointsSourceName(getPointsSourceName(trans.getPointsSource()));
            voList.add(vo);
        }

        return voList;
    }

    /**
     * 冻结积分（用于订单处理）
     */
    @Override
    @Transactional
    public Boolean freezePoints(Long userId, Long points, Long orderId) {
        PointsAccount account = pointsAccountMapper.selectForUpdate(userId);
        if (account == null || account.getAvailablePoints() < points) {
            throw new APIException("POINTS_INSUFFICIENT", "积分不足");
        }

        account.setAvailablePoints(account.getAvailablePoints() - points);
        account.setFrozenPoints(account.getFrozenPoints() + points);
        account.setUpdateTime(LocalDateTime.now());
        Long currentUserId = getCurrentUserId();
        account.setUpdater(currentUserId);
        pointsAccountMapper.updateById(account);

        // 记录冻结明细
        PointsTransaction transaction = new PointsTransaction();
        transaction.setUserId(userId);
        transaction.setAccountId(account.getId());
        transaction.setTransactionType(3); // 3-冻结
        transaction.setPointsSource(5); // 5-积分+现金消费
        transaction.setPointsChange(-points);
        transaction.setPointsBalance(account.getAvailablePoints());
        transaction.setRelatedOrderId(orderId);
        transaction.setDescription("订单冻结积分");
        transaction.setCreateTime(LocalDateTime.now());
        transaction.setCreator(currentUserId);
        transaction.setIsDeleted(false);
        pointsTransactionMapper.insert(transaction);

        // 删除相关缓存（写操作后清除缓存，保证数据一致性）
        pointsCacheService.deleteAccountCache(userId);

        return true;
    }

    /**
     * 解冻积分（订单取消时）
     * 设计原则：直接查询指定订单的冻结记录，提高查询效率
     */
    @Override
    @Transactional
    public Boolean unfreezePoints(Long userId, Long orderId) {
        // 1. 查询积分账户（带行锁）
        PointsAccount account = pointsAccountMapper.selectForUpdate(userId);
        if (account == null) {
            return false;
        }

        // 2. 直接查询该订单的冻结交易记录（transaction_type = 3）
        List<PointsTransaction> freezeTransactions = pointsTransactionMapper.selectByOrderIdAndType(
            orderId, 3); // 3-冻结
        
        // 3. 计算需要解冻的积分总数
        Long totalFrozen = 0L;
        for (PointsTransaction trans : freezeTransactions) {
            // 确保是当前用户的冻结记录
            if (Objects.equals(trans.getUserId(), userId)) {
                totalFrozen += Math.abs(trans.getPointsChange());
            }
        }

        // 4. 如果没有冻结记录，直接返回
        if (totalFrozen == 0) {
            return true;
        }

        // 5. 更新积分账户（解冻积分）
        account.setAvailablePoints(account.getAvailablePoints() + totalFrozen);
        account.setFrozenPoints(account.getFrozenPoints() - totalFrozen);
        account.setUpdateTime(LocalDateTime.now());
        Long currentUserId = getCurrentUserId();
        account.setUpdater(currentUserId);
        pointsAccountMapper.updateById(account);

        // 6. 记录解冻明细
        PointsTransaction transaction = new PointsTransaction();
        transaction.setUserId(userId);
        transaction.setAccountId(account.getId());
        transaction.setTransactionType(4); // 4-解冻
        transaction.setPointsSource(5); // 5-积分+现金消费
        transaction.setPointsChange(totalFrozen);
        transaction.setPointsBalance(account.getAvailablePoints());
        transaction.setRelatedOrderId(orderId);
        transaction.setDescription("订单取消解冻积分");
        transaction.setCreateTime(LocalDateTime.now());
        transaction.setCreator(currentUserId);
        transaction.setIsDeleted(false);
        pointsTransactionMapper.insert(transaction);

        // 删除相关缓存（写操作后清除缓存，保证数据一致性）
        pointsCacheService.deleteAccountCache(userId);

        return true;
    }

    /**
     * 解冻奖励积分（订单完成时）
     * 设计原则：将订单奖励积分从冻结状态转为可用状态
     */
    @Override
    @Transactional
    public Boolean unfreezeRewardPoints(Long userId, Long orderId) {
        // 1. 查询积分账户（带行锁）
        PointsAccount account = pointsAccountMapper.selectForUpdate(userId);
        if (account == null) {
            return false;
        }
        
        // 2. 查询该订单的奖励积分记录（points_source = 0, related_order_id = orderId）
        List<PointsTransaction> rewardTransactions = pointsTransactionMapper.selectByOrderIdAndSourceList(
            orderId, 0); // 0-消费积分
        
        // 3. 计算需要解冻的积分总数
        Long totalRewardPoints = 0L;
        for (PointsTransaction trans : rewardTransactions) {
            // 确保是当前用户的奖励积分记录
            if (Objects.equals(trans.getUserId(), userId) && 
                Objects.equals(trans.getRelatedOrderId(), orderId)) {
                totalRewardPoints += trans.getPointsChange();
            }
        }
        
        // 4. 如果没有奖励积分记录，直接返回
        if (totalRewardPoints == 0) {
            return true;
        }
        
        // 5. 更新积分账户（解冻积分：从冻结积分转为可用积分）
        account.setAvailablePoints(account.getAvailablePoints() + totalRewardPoints);
        account.setFrozenPoints(account.getFrozenPoints() - totalRewardPoints);
        account.setUpdateTime(LocalDateTime.now());
        Long currentUserId = getCurrentUserId();
        account.setUpdater(currentUserId);
        pointsAccountMapper.updateById(account);
        
        // 6. 记录解冻明细
        PointsTransaction transaction = new PointsTransaction();
        transaction.setUserId(userId);
        transaction.setAccountId(account.getId());
        transaction.setTransactionType(4); // 4-解冻
        transaction.setPointsSource(0); // 0-消费积分
        transaction.setPointsChange(totalRewardPoints);
        transaction.setPointsBalance(account.getAvailablePoints());
        transaction.setRelatedOrderId(orderId);
        transaction.setDescription("订单完成解冻奖励积分");
        transaction.setCreateTime(LocalDateTime.now());
        transaction.setCreator(currentUserId);
        transaction.setIsDeleted(false);
        pointsTransactionMapper.insert(transaction);
        
        // 删除相关缓存（写操作后清除缓存，保证数据一致性）
        pointsCacheService.deleteAccountCache(userId);
        
        return true;
    }
    
    /**
     * 取消奖励积分（订单取消时）
     * 设计原则：删除订单奖励积分，减少总积分和冻结积分
     */
    @Override
    @Transactional
    public Boolean cancelRewardPoints(Long userId, Long orderId) {
        // 1. 查询积分账户（带行锁）
        PointsAccount account = pointsAccountMapper.selectForUpdate(userId);
        if (account == null) {
            return false;
        }
        
        // 2. 查询该订单的奖励积分记录
        List<PointsTransaction> rewardTransactions = pointsTransactionMapper.selectByOrderIdAndSourceList(
            orderId, 0); // 0-消费积分
        
        // 3. 计算需要取消的积分总数
        Long totalRewardPoints = 0L;
        for (PointsTransaction trans : rewardTransactions) {
            // 确保是当前用户的奖励积分记录
            if (Objects.equals(trans.getUserId(), userId) && 
                Objects.equals(trans.getRelatedOrderId(), orderId)) {
                totalRewardPoints += trans.getPointsChange();
            }
        }
        
        // 4. 如果没有奖励积分记录，直接返回
        if (totalRewardPoints == 0) {
            return true;
        }
        
        // 5. 更新积分账户（减少总积分和冻结积分）
        account.setTotalPoints(account.getTotalPoints() - totalRewardPoints);
        account.setFrozenPoints(account.getFrozenPoints() - totalRewardPoints);
        account.setUpdateTime(LocalDateTime.now());
        Long currentUserId = getCurrentUserId();
        account.setUpdater(currentUserId);
        pointsAccountMapper.updateById(account);
        
        // 6. 标记相关积分明细和过期记录为已删除
        for (PointsTransaction trans : rewardTransactions) {
            if (Objects.equals(trans.getUserId(), userId) && 
                Objects.equals(trans.getRelatedOrderId(), orderId)) {
                // 标记积分明细为已删除
                pointsTransactionMapper.deleteById(trans.getId(), LocalDateTime.now(), currentUserId);
                
                // 标记过期记录为已删除
                pointsExpirationMapper.deleteByTransactionId(trans.getId());
            }
        }

        // 删除相关缓存（写操作后清除缓存，保证数据一致性）
        pointsCacheService.deleteUserAllCache(userId);

        return true;
    }

    /**
     * 获取会员等级名称
     */
    private String getMemberLevelName(Integer memberLevel) {
        if (memberLevel == null) {
            return "普通用户";
        }
        switch (memberLevel) {
            case 0: return "普通用户";
            case 1: return "白银会员";
            case 2: return "黄金会员";
            case 3: return "钻石会员";
            default: return "普通用户";
        }
    }

    /**
     * 获取交易类型名称
     */
    private String getTransactionTypeName(Integer transactionType) {
        if (transactionType == null) {
            return "未知";
        }
        switch (transactionType) {
            case 0: return "获得";
            case 1: return "消费";
            case 2: return "过期";
            case 3: return "冻结";
            case 4: return "解冻";
            default: return "未知";
        }
    }

    /**
     * 获取积分来源名称
     */
    private String getPointsSourceName(Integer pointsSource) {
        if (pointsSource == null) {
            return "未知";
        }
        switch (pointsSource) {
            case 0: return "消费积分";
            case 1: return "促销积分";
            case 2: return "等级积分";
            case 3: return "行为积分";
            case 4: return "兑换商品";
            case 5: return "积分+现金消费";
            default: return "未知";
        }
    }

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        Long currentUserId = userMapper.getUserIdByUsername(
                SecurityUtils.getCurrentUsername().orElse(null));
        return currentUserId;
    }
    
    /**
     * 更新会员等级并增加等级积分
     * 设计原则：
     * 1. 单一职责原则 - 只负责积分账户的会员等级更新和积分增加
     * 2. 事务管理 - 保证会员等级更新和积分增加的一致性
     * 3. 依赖注入 - 从数据库查询等级积分规则，而不是硬编码
     * 
     * 积分奖励规则从 marketing_points_rule 表中查询（rule_type = 2，member_level = 目标等级）
     */
    @Override
    @Transactional
    public Boolean upgradeMemberLevel(Long userId, Integer newMemberLevel) {
        // 1. 参数校验
        if (userId == null || newMemberLevel == null) {
            throw new APIException(ResultCodeEnum.PARAM_NOT_MATCHED);
        }
        
        if (newMemberLevel < 1 || newMemberLevel > 3) {
            throw new APIException("PARAM_NOT_MATCHED", "会员等级必须在1-3之间");
        }
        
        // 2. 查询积分账户（带行锁）
        PointsAccount account = pointsAccountMapper.selectForUpdate(userId);
        if (account == null) {
            // 如果账户不存在，创建新账户
            account = new PointsAccount();
            account.setUserId(userId);
            account.setTotalPoints(0L);
            account.setAvailablePoints(0L);
            account.setFrozenPoints(0L);
            account.setMemberLevel(0); // 默认普通用户
            account.setCreateTime(LocalDateTime.now());
            account.setUpdateTime(LocalDateTime.now());
            account.setIsDeleted(false);
            Long currentUserId = getCurrentUserId();
            account.setCreator(currentUserId);
            account.setUpdater(currentUserId);
            pointsAccountMapper.insert(account);
        }
        
        // 3. 获取当前会员等级
        Integer currentLevel = account.getMemberLevel() != null ? account.getMemberLevel() : 0;
        
        // 4. 检查是否可以升级
        if (newMemberLevel <= currentLevel) {
            throw new APIException("PARAM_NOT_MATCHED", "新会员等级必须大于当前等级");
        }
        
        // 5. 从数据库查询等级积分规则（rule_type = 2，member_level = 目标等级）
        MarketingPointsRule levelRule = marketingPointsRuleMapper.selectLevelRule(newMemberLevel);
        
        if (levelRule == null) {
            log.warn("未找到会员等级 {} 的积分规则，跳过积分奖励", newMemberLevel);
            // 如果没有配置规则，只更新会员等级，不增加积分
        } else {
            // 6. 获取积分数量和有效期
            Long pointsToAdd = levelRule.getPointsAmount() != null ? levelRule.getPointsAmount() : 0L;
            String levelName = getMemberLevelName(newMemberLevel);
            
            // 7. 增加等级积分（如果积分大于0）
            if (pointsToAdd > 0) {
                PointsAddDTO addDTO = new PointsAddDTO();
                addDTO.setUserId(userId);
                addDTO.setPoints(pointsToAdd);
                addDTO.setPointsSource(2); // 2-等级积分
                addDTO.setRelatedRuleId(levelRule.getId());
                addDTO.setDescription("升级为" + levelName + "获得积分");
                
                // 计算过期时间
                // 如果规则设置了积分有效期，使用规则设置的值；否则使用默认值（30天）
                Integer expireDays = (levelRule.getExpireDays() != null) 
                    ? levelRule.getExpireDays() : 30; // 默认30天有效期
                addDTO.setExpireTime(LocalDateTime.now().plusDays(expireDays));
                
                addPoints(addDTO);
                
                log.info("用户 {} 升级到会员等级 {}，获得 {} 积分", userId, newMemberLevel, pointsToAdd);
            } else {
                log.warn("会员等级 {} 的积分规则中 points_amount 为 0 或 NULL，不增加积分", newMemberLevel);
            }
        }
        
        // 8. 更新会员等级
        account.setMemberLevel(newMemberLevel);
        account.setUpdateTime(LocalDateTime.now());
        Long currentUserId = getCurrentUserId();
        account.setUpdater(currentUserId);
        pointsAccountMapper.updateById(account);
        
        // 9. 删除相关缓存（写操作后清除缓存，保证数据一致性）
        pointsCacheService.deleteUserAllCache(userId);
        
        return true;
    }
    
    /**
     * 计算可用积分可以抵扣的现金金额
     * 设计原则：优先使用即将过期的积分，计算最大可抵扣金额
     */
    @Override
    public BigDecimal calculateDeductibleAmount(Long userId, BigDecimal orderAmount) {
        // 1. 查询用户积分账户（使用缓存）
        PointsAccountVO accountVO = getPointsAccount(userId);
        if (accountVO == null || accountVO.getAvailablePoints() == null || accountVO.getAvailablePoints() <= 0) {
            return BigDecimal.ZERO;
        }
        
        // 2. 获取积分兑换比例（使用缓存）
        BigDecimal exchangeRatio = pointsCacheService.getExchangeRatioCache();
        if (exchangeRatio == null) {
            // 缓存未命中，查数据库
            MarketingPointsExchangeRule rule = exchangeRuleMapper.selectCashExchangeRule();
            exchangeRatio = (rule != null && rule.getExchangeRatio() != null) 
                ? rule.getExchangeRatio() 
                : BigDecimal.valueOf(100); // 默认100积分=1元
            // 写入缓存
            pointsCacheService.setExchangeRatioCache(exchangeRatio);
        }
        
        // 3. 计算可用积分可以抵扣的最大金额
        // 可用积分 / 兑换比例 = 可抵扣金额（元）
        BigDecimal maxDeductibleAmount = BigDecimal.valueOf(accountVO.getAvailablePoints())
            .divide(exchangeRatio, 2, RoundingMode.DOWN);
        
        // 4. 可抵扣金额不能超过订单金额
        BigDecimal deductibleAmount = maxDeductibleAmount.min(orderAmount);
        
        return deductibleAmount;
    }
    
    /**
     * 真正扣除冻结的积分（订单完成时）
     * 设计原则：将冻结的积分从冻结状态转为已扣除状态
     */
    @Override
    @Transactional
    public Boolean deductFrozenPoints(Long userId, Long orderId) {
        // 1. 查询积分账户（带行锁）
        PointsAccount account = pointsAccountMapper.selectForUpdate(userId);
        if (account == null) {
            return false;
        }
        
        // 2. 查询该订单的冻结积分记录（transaction_type = 3, points_source = 5）
        List<PointsTransaction> freezeTransactions = pointsTransactionMapper.selectByOrderIdAndType(
            orderId, 3); // 3-冻结
        
        // 3. 过滤出积分+现金消费的冻结记录（points_source = 5）
        Long totalFrozen = 0L;
        for (PointsTransaction trans : freezeTransactions) {
            // 确保是积分+现金消费的冻结记录（points_source = 5）
            if (Objects.equals(trans.getPointsSource(), 5) && 
                Objects.equals(trans.getUserId(), userId) && 
                Objects.equals(trans.getRelatedOrderId(), orderId)) {
                totalFrozen += Math.abs(trans.getPointsChange()); // 冻结时是负数，取绝对值
            }
        }
        
        // 4. 如果没有冻结积分记录，直接返回
        if (totalFrozen == 0) {
            return true;
        }
        
        // 5. 真正扣除积分：从冻结积分转为已扣除（减少总积分和冻结积分）
        account.setFrozenPoints(account.getFrozenPoints() - totalFrozen);
        account.setTotalPoints(account.getTotalPoints() - totalFrozen);
        account.setUpdateTime(LocalDateTime.now());
        Long currentUserId = getCurrentUserId();
        account.setUpdater(currentUserId);
        pointsAccountMapper.updateById(account);
        
        // 6. 优先扣减即将过期的积分（更新过期记录）
        Long remainingPoints = totalFrozen;
        List<PointsExpiration> expiringPoints = pointsExpirationMapper.selectExpiringPoints(userId);
        
        // 按过期时间升序扣减
        for (PointsExpiration exp : expiringPoints) {
            if (remainingPoints <= 0) {
                break;
            }
            
            Long deductAmount = Math.min(remainingPoints, exp.getPointsAmount());
            exp.setPointsAmount(exp.getPointsAmount() - deductAmount);
            
            if (exp.getPointsAmount() <= 0) {
                exp.setIsExpired(true);
            }
            pointsExpirationMapper.updateById(exp);
            
            remainingPoints -= deductAmount;
        }
        
        // 7. 记录消费明细
        PointsTransaction transaction = new PointsTransaction();
        transaction.setUserId(userId);
        transaction.setAccountId(account.getId());
        transaction.setTransactionType(1); // 1-消费
        transaction.setPointsSource(5); // 5-积分+现金消费
        transaction.setPointsChange(-totalFrozen); // 负数表示减少
        transaction.setPointsBalance(account.getAvailablePoints()); // 可用积分不变（因为已经从可用转为冻结了）
        transaction.setRelatedOrderId(orderId);
        transaction.setDescription("订单完成扣除积分");
        transaction.setCreateTime(LocalDateTime.now());
        transaction.setCreator(currentUserId);
        transaction.setIsDeleted(false);
        pointsTransactionMapper.insert(transaction);
        
        // 8. 删除相关缓存（写操作后清除缓存，保证数据一致性）
        pointsCacheService.deleteUserAllCache(userId);
        
        return true;
    }
}

