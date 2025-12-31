package com.tju.elm.point.service.impl;

import com.tju.elm.point.mapper.MarketingPointsExchangeRuleMapper;
import com.tju.elm.point.mapper.PointsExchangeOrderMapper;
import com.tju.elm.point.service.MarketingPointsExchangeRuleService;
import com.tju.elm.point.service.PointsCacheService;
import com.tju.elm.point.service.PointsService;
import com.tju.elm.point.zoo.pojo.dto.PointsDeductDTO;
import com.tju.elm.point.zoo.pojo.dto.PointsExchangeDTO;
import com.tju.elm.point.zoo.pojo.dto.PointsExchangeRuleCreateDTO;
import com.tju.elm.point.zoo.pojo.entity.MarketingPointsExchangeRule;
import com.tju.elm.point.zoo.pojo.entity.PointsExchangeOrder;
import com.tju.elm.point.zoo.pojo.vo.PointsExchangeGoodsVO;
import com.tju.elm.point.zoo.pojo.vo.PointsExchangeRuleVO;
import exception.APIException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import result.ResultCodeEnum;

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

//    @Autowired
//    private FoodMapper foodMapper;

    @Autowired
    private PointsService pointsService; // 依赖注入积分服务接口

    @Autowired
    private PointsCacheService pointsCacheService;

//    @Autowired
//    private UserMapper userMapper;
//
//    @Autowired
//    private OrdersMapper ordersMapper;
//
//    @Autowired
//    private DeliveryAddressMapper deliveryAddressMapper;
//
//    @Autowired
//    private OrderDetailetMapper orderDetailetMapper;


//    @Override
//    @Transactional
//    public Long createRule(PointsExchangeRuleCreateDTO dto) {
//        // 参数验证：积分+现金规则（rule_type=0）必须提供 exchange_ratio
//        if (dto.getRuleType() != null && dto.getRuleType() == 0) {
//            if (dto.getExchangeRatio() == null) {
//                throw new APIException(ResultCodeEnum.PARAM_NOT_MATCHED);
//            }
//        }
//        // 兑换商品规则（rule_type=1）不需要 exchange_ratio，可以为空
//
//        MarketingPointsExchangeRule rule = new MarketingPointsExchangeRule();
//        BeanUtils.copyProperties(dto, rule);
//
//        if (rule.getRuleStatus() == null) {
//            rule.setRuleStatus(1); // 默认启用
//        }
//
//        rule.setCreateTime(LocalDateTime.now());
//        rule.setUpdateTime(LocalDateTime.now());
//        rule.setIsDeleted(false);
//        Long currentUserId = getCurrentUserId();
//        rule.setCreator(currentUserId);
//        rule.setUpdater(currentUserId);
//
//        exchangeRuleMapper.insert(rule);
//        return rule.getId();
//    }
//
//    @Override
//    @Transactional
//    public Boolean updateRule(Long ruleId, PointsExchangeRuleCreateDTO dto) {
//        MarketingPointsExchangeRule rule = exchangeRuleMapper.selectById(ruleId);
//        if (rule == null) {
//            throw new APIException(ResultCodeEnum.NOT_FOUND);
//        }
//
//        // 参数验证：如果更新为积分+现金规则（rule_type=0），必须提供 exchange_ratio
//        Integer newRuleType = dto.getRuleType() != null ? dto.getRuleType() : rule.getRuleType();
//        if (newRuleType == 0) {
//            BigDecimal newExchangeRatio = dto.getExchangeRatio() != null ? dto.getExchangeRatio() : rule.getExchangeRatio();
//            if (newExchangeRatio == null) {
//                throw new APIException(ResultCodeEnum.PARAM_NOT_MATCHED);
//            }
//        }
//
//        BeanUtils.copyProperties(dto, rule);
//        rule.setUpdateTime(LocalDateTime.now());
//        Long currentUserId = getCurrentUserId();
//        rule.setUpdater(currentUserId);
//
//        exchangeRuleMapper.updateById(rule);
//        return true;
//    }

//    @Override
//    @Transactional
//    public Boolean deleteRule(Long ruleId) {
//        MarketingPointsExchangeRule rule = exchangeRuleMapper.selectById(ruleId);
//        if (rule == null) {
//            throw new APIException(ResultCodeEnum.NOT_FOUND);
//        }
//        Long currentUserId = getCurrentUserId();
//        exchangeRuleMapper.deleteById(ruleId, currentUserId);
//        return true;
//    }

    @Override
    public List<PointsExchangeRuleVO> getRules(Integer ruleType, Integer ruleStatus, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        Integer offset = (pageNum - 1) * pageSize;
        List<MarketingPointsExchangeRule> rules = exchangeRuleMapper.selectRules(
            ruleType, ruleStatus, offset, pageSize);

        List<PointsExchangeRuleVO> voList = new ArrayList<>();
        for (MarketingPointsExchangeRule rule : rules) {
            PointsExchangeRuleVO vo = new PointsExchangeRuleVO();
            BeanUtils.copyProperties(rule, vo);
            vo.setRuleTypeName(getRuleTypeName(rule.getRuleType()));
            voList.add(vo);
        }

        return voList;
    }

//    @Override
//    public List<PointsExchangeGoodsVO> getExchangeGoodsList() {
//        // 1. 查询所有可兑换商品规则
//        List<MarketingPointsExchangeRule> rules = exchangeRuleMapper.selectExchangeGoodsRules();
//
//        // 2. 根据规则中的foodId查询商品信息，组装成VO
//        List<PointsExchangeGoodsVO> voList = new ArrayList<>();
//        for (MarketingPointsExchangeRule rule : rules) {
//            if (rule.getFoodId() == null) {
//                continue; // 跳过没有商品ID的规则
//            }
//
//            // 查询商品信息
//            Food food = foodMapper.selectFoodById(rule.getFoodId());
//            if (food == null || food.getIsDeleted() ||
//                (food.getShelveStatus() != null && food.getShelveStatus() == 0)) {
//                continue; // 跳过已删除或已下架的商品
//            }
//
//            // 组装VO
//            PointsExchangeGoodsVO vo = new PointsExchangeGoodsVO();
//            vo.setFoodId(food.getId());
//            vo.setFoodName(food.getFoodName());
//            vo.setFoodPrice(food.getFoodPrice());
//            vo.setFoodExplain(food.getFoodExplain());
//            vo.setFoodImg(food.getFoodImg());
//            vo.setRemarks(food.getRemarks());
//            vo.setBusinessId(food.getBusinessId());
//            vo.setRequiredPoints(rule.getRequiredPoints());
//            vo.setStockQuantity(rule.getStockQuantity());
//            vo.setRuleId(rule.getId());
//            vo.setCreateTime(rule.getCreateTime());
//
//            voList.add(vo);
//        }
//
//        return voList;
//    }
//
//    @Override
//    @Transactional
//    public Long exchangeGoods(Long userId, PointsExchangeDTO dto) {
//        // 1. 查询兑换规则
//        MarketingPointsExchangeRule rule = exchangeRuleMapper.selectByFoodId(dto.getFoodId());
//        if (rule == null) {
//            throw new APIException("EXCHANGE_RULE_NOT_FOUND", "兑换规则不存在");
//        }
//
//        // 2. 检查库存
//        if (rule.getStockQuantity() == null || rule.getStockQuantity() < dto.getQuantity()) {
//            throw new APIException("STOCK_INSUFFICIENT", "库存不足");
//        }
//
//        // 3. 查询商品信息，获取商家ID和商品价格
//        Food food = foodMapper.selectFoodById(dto.getFoodId());
//        if (food == null) {
//            throw new APIException("FOOD_NOT_FOUND", "商品不存在");
//        }
//
//        // 4. 计算所需积分
//        Long requiredPoints = rule.getRequiredPoints() * dto.getQuantity();
//
//        // 5. 先减少库存（减少兑换数量），避免积分扣除后库存不足
//        // 注意：使用事务保证原子性，如果后续步骤失败，库存会自动回滚
//        int updateCount = exchangeRuleMapper.decreaseStock(rule.getId(), dto.getQuantity());
//        if (updateCount == 0) {
//            throw new APIException("STOCK_INSUFFICIENT", "库存不足，无法完成兑换");
//        }
//
//        // 6. 验证配送地址（必须提供，与普通订单创建逻辑保持一致）
//        Long addressId = dto.getAddressId();
//        if (addressId == null) {
//            throw new APIException("ADDRESS_MISSED", "请选择配送地址");
//        }
//
//        // 验证地址是否属于当前用户
//        DeliveryAddress address = deliveryAddressMapper.getDeliveryAddressById(addressId);
//        if (address == null || !address.getUserId().equals(userId)) {
//            throw new APIException("ADDRESS_MISSED", "配送地址不存在或不属于当前用户");
//        }
//
//        // 7. 创建普通订单（供商家查看和处理）
//        Order order = new Order();
//        order.setBusinessId(food.getBusinessId());
//        order.setCustomerId(userId);
//        order.setAddressId(addressId);
//        order.setOrderDate(LocalDateTime.now());
//        order.setOrderState(1); // 1-已支付（积分已扣除，相当于已支付）
//        order.setOrderTotal(BigDecimal.ZERO); // 积分兑换，订单金额为0
//        order.setDeliveryPrice(BigDecimal.ZERO); // 积分兑换商品免配送费
//        order.setPaymentMethod(3); // 3-积分兑换（新增支付方式）
//        order.setPointsUsed(requiredPoints); // 使用的积分数量
//        order.setPointsAmount(0L); // 积分兑换不获得积分
//        order.setPointsDiscountAmount(BigDecimal.ZERO);
//        order.setCreator(userId);
//        order.setUpdater(userId);
//        order.setCreateTime(LocalDateTime.now());
//        order.setUpdateTime(LocalDateTime.now());
//        order.setIsDeleted(false);
//        ordersMapper.insertOrderPlus(order);
//        ordersMapper.setOrderPaymentMethod(order.getId(),3);
//
//        // 8. 创建订单详情
//        OrderDetailet orderDetailet = new OrderDetailet();
//        orderDetailet.setOrderId(order.getId());
//        orderDetailet.setFoodId(dto.getFoodId());
//        orderDetailet.setQuantity(dto.getQuantity());
//        orderDetailet.setFoodPrice(food.getFoodPrice()); // 保存商品原价
//        orderDetailet.setCreator(userId);
//        orderDetailet.setUpdater(userId);
//        orderDetailet.setCreateTime(LocalDateTime.now());
//        orderDetailet.setUpdateTime(LocalDateTime.now());
//        orderDetailet.setIsDeleted(false);
//        orderDetailetMapper.saveOrderDetailPlus(orderDetailet);
//
//        // 9. 扣减积分（库存已减少，如果积分不足，库存会自动回滚）
//        PointsDeductDTO deductDTO = new PointsDeductDTO();
//        deductDTO.setRelatedOrderId(order.getId());
//        deductDTO.setUserId(userId);
//        deductDTO.setPoints(requiredPoints);
//        deductDTO.setPointsSource(4); // 4-兑换商品
//        deductDTO.setRelatedFoodId(dto.getFoodId());
//        deductDTO.setDescription("兑换商品：" + food.getFoodName() + " x" + dto.getQuantity());
//        pointsService.deductPoints(deductDTO);
//
//        // 10. 创建积分兑换订单记录
//        PointsExchangeOrder exchangeOrder = new PointsExchangeOrder();
//        exchangeOrder.setUserId(userId);
//        exchangeOrder.setOrderId(order.getId()); // 关联普通订单
//        exchangeOrder.setFoodId(dto.getFoodId());
//        exchangeOrder.setPointsUsed(requiredPoints);
//        exchangeOrder.setCashAmount(BigDecimal.ZERO); // 纯积分兑换，现金为0
//        exchangeOrder.setStatus(0); // 0-待处理（对应订单状态1-已支付）
//        exchangeOrder.setCreateTime(LocalDateTime.now());
//        exchangeOrder.setUpdateTime(LocalDateTime.now());
//        exchangeOrder.setIsDeleted(false);
//        exchangeOrderMapper.insert(exchangeOrder);
//
//        return order.getId(); // 返回普通订单ID，这样商家可以在订单列表中看到
//    }

    @Override
    public BigDecimal getCashExchangeRatio() {
        // 1. 先查缓存
        BigDecimal cached = pointsCacheService.getExchangeRatioCache();
        if (cached != null) {
            return cached;
        }
        
        // 2. 缓存未命中，查数据库
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

//    /**
//     * 获取当前用户ID
//     */
//    private Long getCurrentUserId() {
//        Long currentUserId = userMapper.getUserIdByUsername(
//                SecurityUtils.getCurrentUsername().orElse(null));
//        return currentUserId;
//    }
}

