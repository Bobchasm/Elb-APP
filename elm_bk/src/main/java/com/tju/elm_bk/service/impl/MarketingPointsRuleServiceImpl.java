package com.tju.elm_bk.service.impl;

import com.tju.elm_bk.exception.APIException;
import com.tju.elm_bk.mapper.MarketingPointsRuleMapper;
import com.tju.elm_bk.mapper.OrdersMapper;
import com.tju.elm_bk.mapper.PointsAccountMapper;
import com.tju.elm_bk.mapper.UserMapper;
import com.tju.elm_bk.pojo.dto.OrderPaidMessage;
import com.tju.elm_bk.pojo.dto.PointsAddDTO;
import com.tju.elm_bk.pojo.dto.PointsRuleCreateDTO;
import com.tju.elm_bk.pojo.dto.PointsRuleUpdateDTO;
import com.tju.elm_bk.pojo.entity.MarketingPointsRule;
import com.tju.elm_bk.pojo.entity.PointsAccount;
import com.tju.elm_bk.pojo.vo.PointsRuleVO;
import com.tju.elm_bk.result.ResultCodeEnum;
import com.tju.elm_bk.service.MarketingPointsRuleService;
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
import java.util.stream.Collectors;

/**
 * 营销系统积分规则服务实现类
 * 职责：积分规则管理（增删改查）和积分计算
 * 设计原则：
 * 1. 单一职责原则 - 只负责规则管理和计算
 * 2. 依赖注入 - 注入PointsService（依赖反转原则）
 * 3. 基于接口编程 - 依赖PointsService接口而非实现
 */
@Slf4j
@Service
public class MarketingPointsRuleServiceImpl implements MarketingPointsRuleService {

    @Autowired
    private MarketingPointsRuleMapper marketingPointsRuleMapper;

    @Autowired
    private PointsAccountMapper pointsAccountMapper;

    @Autowired
    private PointsService pointsService; // 依赖注入积分服务接口
    
    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 创建积分规则
     */
    @Override
    @Transactional
    public Long createRule(PointsRuleCreateDTO dto) {
        MarketingPointsRule rule = new MarketingPointsRule();
        BeanUtils.copyProperties(dto, rule);
        
        // 设置默认值
        if (rule.getRuleStatus() == null) {
            rule.setRuleStatus(1); // 默认启用
        }
        if (rule.getPriority() == null) {
            rule.setPriority(0);
        }
        
        rule.setCreateTime(LocalDateTime.now());
        rule.setUpdateTime(LocalDateTime.now());
        rule.setIsDeleted(false);
        
        // 获取当前用户ID
        Long currentUserId = getCurrentUserId();
        rule.setCreator(currentUserId);
        rule.setUpdater(currentUserId);
        
        marketingPointsRuleMapper.insert(rule);
        return rule.getId();
    }

    /**
     * 更新积分规则
     */
    @Override
    @Transactional
    public Boolean updateRule(Long ruleId, PointsRuleUpdateDTO dto) {
        MarketingPointsRule rule = marketingPointsRuleMapper.selectById(ruleId);
        if (rule == null) {
            throw new APIException(ResultCodeEnum.NOT_FOUND);
        }

        // 更新字段
        if (dto.getRuleName() != null) {
            rule.setRuleName(dto.getRuleName());
        }
        if (dto.getRuleStatus() != null) {
            rule.setRuleStatus(dto.getRuleStatus());
        }
        if (dto.getPointsRatio() != null) {
            rule.setPointsRatio(dto.getPointsRatio());
        }
        if (dto.getPointsMultiplier() != null) {
            rule.setPointsMultiplier(dto.getPointsMultiplier());
        }
        if (dto.getMemberLevel() != null) {
            rule.setMemberLevel(dto.getMemberLevel());
        }
        if (dto.getMinOrderAmount() != null) {
            rule.setMinOrderAmount(dto.getMinOrderAmount());
        }
        if (dto.getMaxOrderAmount() != null) {
            rule.setMaxOrderAmount(dto.getMaxOrderAmount());
        }
        if (dto.getFoodId() != null) {
            rule.setFoodId(dto.getFoodId());
        }
        if (dto.getHolidayStart() != null) {
            rule.setHolidayStart(dto.getHolidayStart());
        }
        if (dto.getHolidayEnd() != null) {
            rule.setHolidayEnd(dto.getHolidayEnd());
        }
        if (dto.getBehaviorType() != null) {
            rule.setBehaviorType(dto.getBehaviorType());
        }
        if (dto.getPointsAmount() != null) {
            rule.setPointsAmount(dto.getPointsAmount());
        }
        if (dto.getExpireDays() != null) {
            rule.setExpireDays(dto.getExpireDays());
        }
        if (dto.getStartTime() != null) {
            rule.setStartTime(dto.getStartTime());
        }
        if (dto.getEndTime() != null) {
            rule.setEndTime(dto.getEndTime());
        }
        if (dto.getPriority() != null) {
            rule.setPriority(dto.getPriority());
        }

        rule.setUpdateTime(LocalDateTime.now());
        rule.setUpdater(getCurrentUserId());
        
        marketingPointsRuleMapper.updateById(rule);
        return true;
    }

    /**
     * 删除积分规则
     */
    @Override
    @Transactional
    public Boolean deleteRule(Long ruleId) {
        MarketingPointsRule rule = marketingPointsRuleMapper.selectById(ruleId);
        if (rule == null) {
            throw new APIException(ResultCodeEnum.NOT_FOUND);
        }
        
        marketingPointsRuleMapper.deleteById(ruleId, LocalDateTime.now(), getCurrentUserId());
        return true;
    }

    /**
     * 查询积分规则列表
     */
    @Override
    public List<PointsRuleVO> getRules(Integer ruleType, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        Integer offset = (pageNum - 1) * pageSize;
        List<MarketingPointsRule> rules = marketingPointsRuleMapper.selectRules(
            ruleType, 1, offset, pageSize); // 只查询启用的规则

        List<PointsRuleVO> voList = new ArrayList<>();
        for (MarketingPointsRule rule : rules) {
            PointsRuleVO vo = new PointsRuleVO();
            BeanUtils.copyProperties(rule, vo);
            vo.setRuleTypeName(getRuleTypeName(rule.getRuleType()));
            voList.add(vo);
        }

        return voList;
    }

    /**
     * 根据订单信息计算应获得积分
     * 设计原则：依赖注入 - 调用PointsService接口增加积分
     */
    @Override
    @Transactional
    public Long calculatePoints(Long userId, Long orderId, BigDecimal orderAmount, 
                               LocalDateTime orderDate, List<Long> foodIds) {
        log.info("========== 开始计算订单积分 ==========");
        log.info("订单ID: {}, 用户ID: {}, 订单金额: {}元, 订单日期: {}, 商品ID列表: {}", 
            orderId, userId, orderAmount, orderDate, foodIds);
        
        // 1. 查询用户会员等级
        PointsAccount account = pointsAccountMapper.selectByUserId(userId);
        Integer memberLevel = (account != null) ? account.getMemberLevel() : 0;
        log.info("用户会员等级: {} ({})", memberLevel, getMemberLevelName(memberLevel));

        // 2. 查询基础消费积分规则
        MarketingPointsRule baseRule = marketingPointsRuleMapper.selectBaseConsumptionRule(memberLevel);
        Long basePoints = 0L;
        if (baseRule != null && baseRule.getPointsRatio() != null) {
            basePoints = orderAmount.multiply(baseRule.getPointsRatio())
                .setScale(0, RoundingMode.DOWN).longValue();
            log.info("【基础消费积分规则】规则ID: {}, 规则名称: {}, 积分比例: {}, 会员等级: {}, 计算出的基础积分: {}",
                baseRule.getId(), baseRule.getRuleName(), baseRule.getPointsRatio(), 
                baseRule.getMemberLevel(), basePoints);
        } else {
            log.warn("【基础消费积分规则】未找到匹配的规则（会员等级: {}）", memberLevel);
        }

        // 3. 查询促销积分规则
        LocalDate orderDateLocal = orderDate.toLocalDate();
        List<MarketingPointsRule> promotionRules = marketingPointsRuleMapper.selectPromotionRules(
            orderDateLocal, orderAmount, foodIds, memberLevel);
        
        Long promotionPoints = 0L;
        if (!promotionRules.isEmpty()) {
            // 取最高优先级的促销规则
            MarketingPointsRule topRule = promotionRules.get(0);
            log.info("【促销积分规则】使用最高优先级规则: ID={}, 名称={}, 优先级={}, 倍数={}",
                topRule.getId(), topRule.getRuleName(), topRule.getPriority(), topRule.getPointsMultiplier());
            
            if (topRule.getPointsMultiplier() != null && basePoints > 0) {
                BigDecimal basePointsDecimal = BigDecimal.valueOf(basePoints);
                promotionPoints = basePointsDecimal.multiply(
                    topRule.getPointsMultiplier().subtract(BigDecimal.ONE))
                    .setScale(0, RoundingMode.DOWN).longValue();
                log.info("【促销积分计算】基础积分: {}, 倍数: {}, 额外积分: {}, 计算出的促销积分: {}",
                    basePoints, topRule.getPointsMultiplier(), 
                    topRule.getPointsMultiplier().subtract(BigDecimal.ONE), promotionPoints);
            } else {
                log.warn("【促销积分计算】跳过计算，原因: 倍数={}, 基础积分={}", 
                    topRule.getPointsMultiplier(), basePoints);
            }
        } else {
            log.info("【促销积分规则】未找到匹配的促销规则");
        }

        // 4. 查询等级积分规则
        MarketingPointsRule levelRule = marketingPointsRuleMapper.selectLevelRule(memberLevel);
        Long levelPoints = 0L;
        if (levelRule != null && levelRule.getPointsRatio() != null) {
            levelPoints = orderAmount.multiply(levelRule.getPointsRatio())
                .setScale(0, RoundingMode.DOWN).longValue();
            log.info("【等级积分规则】规则ID: {}, 规则名称: {}, 积分比例: {}, 会员等级: {}, 计算出的等级积分: {}",
                levelRule.getId(), levelRule.getRuleName(), levelRule.getPointsRatio(),
                levelRule.getMemberLevel(), levelPoints);
        } else {
            log.warn("【等级积分规则】未找到匹配的规则（会员等级: {}）", memberLevel);
        }

        // 5. 计算总积分（基础积分 + 促销积分 + 等级积分）
        Long totalPoints = basePoints + promotionPoints + levelPoints;
        log.info("【积分汇总】基础积分: {}, 促销积分: {}, 等级积分: {}, 总积分: {}",
            basePoints, promotionPoints, levelPoints, totalPoints);

        // 6. 如果积分大于0，调用积分系统增加积分
        if (totalPoints > 0) {
            PointsAddDTO addDTO = new PointsAddDTO();
            addDTO.setUserId(userId);
            addDTO.setPoints(totalPoints);
            addDTO.setPointsSource(0); // 0-消费积分
            addDTO.setRelatedOrderId(orderId);
            addDTO.setDescription("订单消费获得积分");

            // 计算过期时间（所有积分都必须有有效期）
            // 如果规则设置了积分有效期，使用规则设置的值；否则使用默认值（30天）
            Integer expireDays = (baseRule != null && baseRule.getExpireDays() != null) 
                ? baseRule.getExpireDays() : 30; // 默认30天有效期
            addDTO.setExpireTime(orderDate.plusDays(expireDays));
            
            log.info("【积分添加】准备添加积分: 用户ID={}, 积分数量={}, 过期天数={}, 过期时间={}",
                userId, totalPoints, expireDays, addDTO.getExpireTime());

            pointsService.addPoints(addDTO);
            
            // 更新订单的获得积分数量
            ordersMapper.updateOrderPointsAmount(orderId, totalPoints);
            log.info("【订单更新】已更新订单 {} 的获得积分数量为 {}", orderId, totalPoints);
        } else {
            // 如果没有获得积分，也要更新订单的points_amount为0
            ordersMapper.updateOrderPointsAmount(orderId, 0L);
            log.warn("【积分添加】订单 {} 未获得积分，总积分为0", orderId);
        }

        log.info("========== 订单积分计算完成，总积分: {} ==========", totalPoints);
        return totalPoints;
    }
    
    /**
     * 获取会员等级名称
     */
    private String getMemberLevelName(Integer memberLevel) {
        if (memberLevel == null) {
            return "未知";
        }
        switch (memberLevel) {
            case 0: return "普通用户";
            case 1: return "白银会员";
            case 2: return "黄金会员";
            case 3: return "钻石会员";
            default: return "未知";
        }
    }

    /**
     * 根据行为类型计算应获得积分
     */
    @Override
    @Transactional
    public Long calculateBehaviorPoints(Long userId, String behaviorType) {
        log.info("========== 开始计算行为积分 ==========");
        log.info("用户ID: {}, 行为类型: {}", userId, behaviorType);
        
        // 查询用户会员等级
        PointsAccount pointsAccount = pointsAccountMapper.selectByUserId(userId);
        Integer memberLevel = (pointsAccount != null && pointsAccount.getMemberLevel() != null) 
            ? pointsAccount.getMemberLevel() : 0; // 默认为普通会员（0）
        log.info("用户会员等级: {} ({})", memberLevel, getMemberLevelName(memberLevel));
        
        // 根据会员等级查询行为积分规则
        MarketingPointsRule behaviorRule = marketingPointsRuleMapper.selectBehaviorRule(behaviorType, memberLevel);
        if (behaviorRule == null) {
            log.warn("【行为积分规则】未找到匹配的规则: behaviorType={}, memberLevel={}", behaviorType, memberLevel);
            return 0L;
        }
        
        if (behaviorRule.getPointsAmount() == null || behaviorRule.getPointsAmount() <= 0) {
            log.warn("【行为积分规则】规则ID={} 的积分数量为0或NULL: pointsAmount={}", 
                behaviorRule.getId(), behaviorRule.getPointsAmount());
            return 0L;
        }

        Long points = behaviorRule.getPointsAmount();
        log.info("【行为积分规则】规则ID: {}, 规则名称: {}, 积分数量: {}, 会员等级: {}, 有效期: {}天",
            behaviorRule.getId(), behaviorRule.getRuleName(), points, 
            behaviorRule.getMemberLevel(), behaviorRule.getExpireDays());

        // 调用积分系统增加积分
        PointsAddDTO addDTO = new PointsAddDTO();
        addDTO.setUserId(userId);
        addDTO.setPoints(points);
        addDTO.setPointsSource(3); // 3-行为积分
        addDTO.setRelatedRuleId(behaviorRule.getId());
        addDTO.setDescription(getBehaviorDescription(behaviorType));

        // 计算过期时间（所有积分都必须有有效期）
        // 如果规则设置了积分有效期，使用规则设置的值；否则使用默认值（30天）
        Integer expireDays = (behaviorRule.getExpireDays() != null) 
            ? behaviorRule.getExpireDays() : 30; // 默认30天有效期
        addDTO.setExpireTime(LocalDateTime.now().plusDays(expireDays));
        
        log.info("【积分添加】准备添加行为积分: 用户ID={}, 积分数量={}, 过期天数={}, 过期时间={}",
            userId, points, expireDays, addDTO.getExpireTime());

        try {
            pointsService.addPoints(addDTO);
            log.info("========== 行为积分计算完成，获得积分: {} ==========", points);
            return points;
        } catch (Exception e) {
            log.error("【积分添加失败】用户ID={}, 行为类型={}, 积分数量={}, 错误: {}", 
                userId, behaviorType, points, e.getMessage(), e);
            throw e; // 重新抛出异常，让调用方知道积分添加失败
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
     * 获取规则类型名称
     */
    private String getRuleTypeName(Integer ruleType) {
        if (ruleType == null) {
            return "未知";
        }
        switch (ruleType) {
            case 0: return "消费积分";
            case 1: return "促销积分";
            case 2: return "等级积分";
            case 3: return "行为积分";
            default: return "未知";
        }
    }

    /**
     * 获取行为描述
     */
    private String getBehaviorDescription(String behaviorType) {
        if (behaviorType == null) {
            return "行为积分";
        }
        switch (behaviorType) {
            case "like": return "点赞商家获得积分";
            case "collect": return "收藏商家获得积分";
            case "repay_loan": return "还贷款获得积分";
            default: return "行为积分";
        }
    }
    
    /**
     * 根据订单支付完成消息计算应获得积分（用于消息队列监听器）
     * 设计原则：依赖注入 - 调用PointsService接口增加积分
     */
    @Override
    @Transactional
    public Long calculateOrderPoints(Long userId, Long orderId, BigDecimal orderAmount, 
                                    LocalDateTime orderDate, List<OrderPaidMessage.OrderFoodDetail> foodDetails) {
        // 将foodDetails转换为foodIds列表
        List<Long> foodIds = foodDetails != null 
            ? foodDetails.stream()
                .map(OrderPaidMessage.OrderFoodDetail::getFoodId)
                .collect(Collectors.toList())
            : new ArrayList<>();
        
        // 调用原有的calculatePoints方法
        return calculatePoints(userId, orderId, orderAmount, orderDate, foodIds);
    }

    /**
     * 启用积分规则
     */
    @Override
    @Transactional
    public Boolean enableRule(Long ruleId) {
        MarketingPointsRule rule = marketingPointsRuleMapper.selectById(ruleId);
        if (rule == null) {
            throw new APIException(ResultCodeEnum.NOT_FOUND);
        }
        
        rule.setRuleStatus(1); // 1-启用
        rule.setUpdateTime(LocalDateTime.now());
        rule.setUpdater(getCurrentUserId());
        
        marketingPointsRuleMapper.updateById(rule);
        return true;
    }

    /**
     * 禁用积分规则
     */
    @Override
    @Transactional
    public Boolean disableRule(Long ruleId) {
        MarketingPointsRule rule = marketingPointsRuleMapper.selectById(ruleId);
        if (rule == null) {
            throw new APIException(ResultCodeEnum.NOT_FOUND);
        }
        
        rule.setRuleStatus(0); // 0-禁用
        rule.setUpdateTime(LocalDateTime.now());
        rule.setUpdater(getCurrentUserId());
        
        marketingPointsRuleMapper.updateById(rule);
        return true;
    }
}

