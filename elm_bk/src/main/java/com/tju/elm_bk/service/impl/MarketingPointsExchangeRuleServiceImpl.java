package com.tju.elm_bk.service.impl;

import com.tju.elm_bk.exception.APIException;
import com.tju.elm_bk.mapper.FoodMapper;
import com.tju.elm_bk.mapper.MarketingPointsExchangeRuleMapper;
import com.tju.elm_bk.mapper.PointsExchangeOrderMapper;
import com.tju.elm_bk.mapper.UserMapper;
import com.tju.elm_bk.pojo.dto.PointsDeductDTO;
import com.tju.elm_bk.pojo.dto.PointsExchangeDTO;
import com.tju.elm_bk.pojo.dto.PointsExchangeRuleCreateDTO;
import com.tju.elm_bk.pojo.entity.Food;
import com.tju.elm_bk.pojo.entity.MarketingPointsExchangeRule;
import com.tju.elm_bk.pojo.entity.PointsExchangeOrder;
import com.tju.elm_bk.pojo.vo.PointsExchangeGoodsVO;
import com.tju.elm_bk.pojo.vo.PointsExchangeRuleVO;
import com.tju.elm_bk.result.ResultCodeEnum;
import com.tju.elm_bk.service.MarketingPointsExchangeRuleService;
import com.tju.elm_bk.service.PointsService;
import com.tju.elm_bk.utils.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 营销系统积分兑换规则服务实现类
 * 职责：积分兑换规则管理（增删改查）和兑换商品管理
 * 设计原则：单一职责原则 - 只负责兑换规则管理
 */
@Service
public class MarketingPointsExchangeRuleServiceImpl implements MarketingPointsExchangeRuleService {

    @Autowired
    private MarketingPointsExchangeRuleMapper exchangeRuleMapper;

    @Autowired
    private PointsExchangeOrderMapper exchangeOrderMapper;

    @Autowired
    private FoodMapper foodMapper;

    @Autowired
    private PointsService pointsService; // 依赖注入积分服务接口

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public Long createRule(PointsExchangeRuleCreateDTO dto) {
        MarketingPointsExchangeRule rule = new MarketingPointsExchangeRule();
        BeanUtils.copyProperties(dto, rule);
        
        if (rule.getRuleStatus() == null) {
            rule.setRuleStatus(1); // 默认启用
        }
        
        rule.setCreateTime(LocalDateTime.now());
        rule.setUpdateTime(LocalDateTime.now());
        rule.setIsDeleted(false);
        Long currentUserId = getCurrentUserId();
        rule.setCreator(currentUserId);
        rule.setUpdater(currentUserId);
        
        exchangeRuleMapper.insert(rule);
        return rule.getId();
    }

    @Override
    @Transactional
    public Boolean updateRule(Long ruleId, PointsExchangeRuleCreateDTO dto) {
        MarketingPointsExchangeRule rule = exchangeRuleMapper.selectById(ruleId);
        if (rule == null) {
            throw new APIException(ResultCodeEnum.NOT_FOUND);
        }

        BeanUtils.copyProperties(dto, rule);
        rule.setUpdateTime(LocalDateTime.now());
        Long currentUserId = getCurrentUserId();
        rule.setUpdater(currentUserId);
        
        exchangeRuleMapper.updateById(rule);
        return true;
    }

    @Override
    @Transactional
    public Boolean deleteRule(Long ruleId) {
        MarketingPointsExchangeRule rule = exchangeRuleMapper.selectById(ruleId);
        if (rule == null) {
            throw new APIException(ResultCodeEnum.NOT_FOUND);
        }
        Long currentUserId = getCurrentUserId();
        exchangeRuleMapper.deleteById(ruleId, currentUserId);
        return true;
    }

    @Override
    public List<PointsExchangeRuleVO> getRules(Integer ruleType, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        Integer offset = (pageNum - 1) * pageSize;
        List<MarketingPointsExchangeRule> rules = exchangeRuleMapper.selectRules(
            ruleType, 1, offset, pageSize);

        List<PointsExchangeRuleVO> voList = new ArrayList<>();
        for (MarketingPointsExchangeRule rule : rules) {
            PointsExchangeRuleVO vo = new PointsExchangeRuleVO();
            BeanUtils.copyProperties(rule, vo);
            vo.setRuleTypeName(getRuleTypeName(rule.getRuleType()));
            voList.add(vo);
        }

        return voList;
    }

    @Override
    public List<PointsExchangeGoodsVO> getExchangeGoodsList() {
        // 1. 查询所有可兑换商品规则
        List<MarketingPointsExchangeRule> rules = exchangeRuleMapper.selectExchangeGoodsRules();
        
        // 2. 根据规则中的foodId查询商品信息，组装成VO
        List<PointsExchangeGoodsVO> voList = new ArrayList<>();
        for (MarketingPointsExchangeRule rule : rules) {
            if (rule.getFoodId() == null) {
                continue; // 跳过没有商品ID的规则
            }

            // 查询商品信息
            Food food = foodMapper.selectFoodById(rule.getFoodId());
            if (food == null || food.getIsDeleted() ||
                (food.getShelveStatus() != null && food.getShelveStatus() == 0)) {
                continue; // 跳过已删除或已下架的商品
            }

            // 组装VO
            PointsExchangeGoodsVO vo = new PointsExchangeGoodsVO();
            vo.setFoodId(food.getId());
            vo.setFoodName(food.getFoodName());
            vo.setFoodPrice(food.getFoodPrice());
            vo.setFoodExplain(food.getFoodExplain());
            vo.setFoodImg(food.getFoodImg());
            vo.setRemarks(food.getRemarks());
            vo.setBusinessId(food.getBusinessId());
            vo.setRequiredPoints(rule.getRequiredPoints());
            vo.setStockQuantity(rule.getStockQuantity());
            vo.setRuleId(rule.getId());
            vo.setCreateTime(rule.getCreateTime());

            voList.add(vo);
        }

        return voList;
    }

    @Override
    @Transactional
    public Long exchangeGoods(Long userId, PointsExchangeDTO dto) {
        // 1. 查询兑换规则
        MarketingPointsExchangeRule rule = exchangeRuleMapper.selectByFoodId(dto.getFoodId());
        if (rule == null) {
            throw new APIException("EXCHANGE_RULE_NOT_FOUND", "兑换规则不存在");
        }

        // 2. 检查库存
        if (rule.getStockQuantity() == null || rule.getStockQuantity() < dto.getQuantity()) {
            throw new APIException("STOCK_INSUFFICIENT", "库存不足");
        }

        // 3. 计算所需积分
        Long requiredPoints = rule.getRequiredPoints() * dto.getQuantity();

        // 4. 扣减积分
        PointsDeductDTO deductDTO = new PointsDeductDTO();
        deductDTO.setUserId(userId);
        deductDTO.setPoints(requiredPoints);
        deductDTO.setPointsSource(4); // 4-兑换商品
        deductDTO.setRelatedFoodId(dto.getFoodId());
        deductDTO.setDescription("兑换商品：" + dto.getFoodId());
        pointsService.deductPoints(deductDTO);

        // 5. 减少库存
        int updateCount = exchangeRuleMapper.decreaseStock(rule.getId());
        if (updateCount == 0) {
            throw new APIException("STOCK_UPDATE_FAILED", "库存更新失败");
        }

        // 6. 创建兑换订单
        PointsExchangeOrder order = new PointsExchangeOrder();
        order.setUserId(userId);
        order.setExchangeType(1); // 1-纯积分兑换商品
        order.setFoodId(dto.getFoodId());
        order.setPointsUsed(requiredPoints);
        order.setExchangeRatio(rule.getExchangeRatio());
        order.setStatus(0); // 0-待处理
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        order.setIsDeleted(false);
        exchangeOrderMapper.insert(order);

        return order.getId();
    }

    @Override
    public BigDecimal getCashExchangeRatio() {
        MarketingPointsExchangeRule rule = exchangeRuleMapper.selectCashExchangeRule();
        if (rule == null) {
            return BigDecimal.valueOf(100); // 默认100积分=1元
        }
        return rule.getExchangeRatio();
    }

    private String getRuleTypeName(Integer ruleType) {
        if (ruleType == null) {
            return "未知";
        }
        switch (ruleType) {
            case 0: return "积分+现金";
            case 1: return "兑换商品";
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
}

