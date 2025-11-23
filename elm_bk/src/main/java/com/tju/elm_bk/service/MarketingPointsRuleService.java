package com.tju.elm_bk.service;

import com.tju.elm_bk.pojo.dto.OrderPaidMessage;
import com.tju.elm_bk.pojo.dto.PointsRuleCreateDTO;
import com.tju.elm_bk.pojo.dto.PointsRuleUpdateDTO;
import com.tju.elm_bk.pojo.vo.PointsRuleVO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 营销系统积分规则服务接口
 * 职责：积分规则管理（增删改查）和积分计算
 * 设计原则：单一职责原则 - 只负责规则管理和计算，不直接操作积分账户
 */
public interface MarketingPointsRuleService {
    
    /**
     * 创建积分规则
     * @param dto 创建规则DTO
     * @return 规则ID
     */
    Long createRule(PointsRuleCreateDTO dto);
    
    /**
     * 更新积分规则
     * @param ruleId 规则ID
     * @param dto 更新规则DTO
     * @return 是否成功
     */
    Boolean updateRule(Long ruleId, PointsRuleUpdateDTO dto);
    
    /**
     * 删除积分规则
     * @param ruleId 规则ID
     * @return 是否成功
     */
    Boolean deleteRule(Long ruleId);
    
    /**
     * 查询积分规则列表
     * @param ruleType 规则类型（可选）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 积分规则列表
     */
    List<PointsRuleVO> getRules(Integer ruleType, Integer pageNum, Integer pageSize);
    
    /**
     * 根据订单信息计算应获得积分（同步调用积分系统）
     * @param userId 用户ID
     * @param orderId 订单ID
     * @param orderAmount 订单金额
     * @param orderDate 订单日期
     * @param foodIds 商品ID列表
     * @return 应获得积分数量
     */
    Long calculatePoints(Long userId, Long orderId, BigDecimal orderAmount, 
                        LocalDateTime orderDate, List<Long> foodIds);
    
    /**
     * 根据行为类型计算应获得积分
     * @param userId 用户ID
     * @param behaviorType 行为类型（like-点赞 collect-收藏 repay_loan-还贷款）
     * @return 应获得积分数量
     */
    Long calculateBehaviorPoints(Long userId, String behaviorType);
    
    /**
     * 根据订单支付完成消息计算应获得积分（用于消息队列监听器）
     * @param userId 用户ID
     * @param orderId 订单ID
     * @param orderAmount 订单金额
     * @param orderDate 订单日期
     * @param foodDetails 商品详情列表
     * @return 应获得积分数量
     */
    Long calculateOrderPoints(Long userId, Long orderId, BigDecimal orderAmount, 
                              LocalDateTime orderDate, List<OrderPaidMessage.OrderFoodDetail> foodDetails);
}

