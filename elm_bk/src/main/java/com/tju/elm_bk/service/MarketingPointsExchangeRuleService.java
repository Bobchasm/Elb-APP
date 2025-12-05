package com.tju.elm_bk.service;

import com.tju.elm_bk.pojo.dto.PointsExchangeDTO;
import com.tju.elm_bk.pojo.dto.PointsExchangeRuleCreateDTO;
import com.tju.elm_bk.pojo.vo.PointsExchangeGoodsVO;
import com.tju.elm_bk.pojo.vo.PointsExchangeRuleVO;

import java.util.List;

/**
 * 营销系统积分兑换规则服务接口
 * 职责：积分兑换规则管理（增删改查）和兑换商品管理
 * 设计原则：单一职责原则 - 只负责兑换规则管理
 */
public interface MarketingPointsExchangeRuleService {
    
    /**
     * 创建积分兑换规则
     * @param dto 创建规则DTO
     * @return 规则ID
     */
    Long createRule(PointsExchangeRuleCreateDTO dto);
    
    /**
     * 更新积分兑换规则
     * @param ruleId 规则ID
     * @param dto 更新规则DTO
     * @return 是否成功
     */
    Boolean updateRule(Long ruleId, PointsExchangeRuleCreateDTO dto);
    
    /**
     * 删除积分兑换规则
     * @param ruleId 规则ID
     * @return 是否成功
     */
    Boolean deleteRule(Long ruleId);
    
    /**
     * 查询积分兑换规则列表
     * @param ruleType 规则类型（可选）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 积分兑换规则列表
     */
    List<PointsExchangeRuleVO> getRules(Integer ruleType, Integer pageNum, Integer pageSize);
    
    /**
     * 查询可兑换商品列表
     * @return 可兑换商品列表（包含商品信息和所需积分）
     */
    List<PointsExchangeGoodsVO> getExchangeGoodsList();
    
    /**
     * 兑换商品
     * @param userId 用户ID
     * @param dto 兑换DTO
     * @return 兑换订单ID
     */
    Long exchangeGoods(Long userId, PointsExchangeDTO dto);
    
    /**
     * 获取积分+现金兑换比例
     * @return 兑换比例（如10表示10积分=1元）
     */
    java.math.BigDecimal getCashExchangeRatio();
    
    /**
     * 启用积分兑换规则
     * @param ruleId 规则ID
     * @return 是否成功
     */
    Boolean enableRule(Long ruleId);
    
    /**
     * 禁用积分兑换规则
     * @param ruleId 规则ID
     * @return 是否成功
     */
    Boolean disableRule(Long ruleId);
}

