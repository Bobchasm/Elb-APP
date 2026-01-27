package com.tju.elm.point.controller;

import com.tju.elm.api.client.PointClient;
import com.tju.elm.api.client.UserClient;
import com.tju.elm.point.mapper.PointsAccountMapper;
import com.tju.elm.point.mapper.PointsExchangeOrderMapper;
import com.tju.elm.point.service.MarketingPointsExchangeRuleService;
import com.tju.elm.point.service.MarketingPointsRuleService;
import com.tju.elm.point.service.PointsExpirationService;
import com.tju.elm.point.service.PointsService;
import com.tju.elm.point.zoo.pojo.dto.PointsExchangeDTO;
import com.tju.elm.point.zoo.pojo.entity.PointsAccount;
import com.tju.elm.point.zoo.pojo.entity.PointsExchangeOrder;
import com.tju.elm.point.zoo.pojo.vo.PointsAccountVO;
import com.tju.elm.point.zoo.pojo.vo.PointsExchangeGoodsVO;
import com.tju.elm.point.zoo.pojo.vo.PointsExpirationVO;
import com.tju.elm.point.zoo.pojo.vo.PointsTransactionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import result.HttpResult;
import utils.UserContext;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 积分系统Controller
 * 职责：处理用户端积分相关HTTP请求
 * 设计原则：
 * 1. 单一职责原则 - 只负责HTTP请求处理
 * 2. 依赖注入 - 注入Service接口
 * 3. 基于接口编程 - 依赖Service接口而非实现
 */
@Slf4j
@RestController
@RequestMapping("/api/points")
@Tag(name = "积分系统", description = "用户端积分相关接口")
public class PointsController {

    @Autowired
    private PointsService pointsService;

    @Autowired
    private MarketingPointsExchangeRuleService exchangeRuleService;

    @Autowired
    private PointsExpirationService pointsExpirationService;

    @Autowired
    private PointsAccountMapper pointsAccountMapper;

    @Autowired
    private PointsExchangeOrderMapper pointsExchangeOrderMapper;

    @Autowired
    private UserClient userClient;

    @Autowired
    private MarketingPointsRuleService marketingPointsRuleService;

    /**
     * 查询积分账户
     */
    @GetMapping("/account")
    @Operation(summary = "查询积分账户", description = "查询当前用户的积分账户信息")
    public HttpResult<PointsAccountVO> getAccount() {
        Long userId = getCurrentUserId();
        PointsAccountVO account = pointsService.getPointsAccount(userId);
        return HttpResult.success(account);
    }

    @GetMapping("/account/create")
    @Operation(summary = "创建积分账户")
    public HttpResult<Long> createAccount(@RequestParam Long userId) {
        LocalDateTime now = LocalDateTime.now();
        PointsAccount pointsAccount = new PointsAccount();
        pointsAccount.setUserId(userId);
        pointsAccount.setTotalPoints(0L);
        pointsAccount.setAvailablePoints(0L);
        pointsAccount.setFrozenPoints(0L);
        pointsAccount.setMemberLevel(0); // 0-普通会员
        pointsAccount.setCreateTime(now);
        pointsAccount.setUpdateTime(now);
        pointsAccount.setCreator(userId);
        pointsAccount.setUpdater(userId);
        pointsAccount.setIsDeleted(false);
        pointsAccountMapper.insert(pointsAccount);
        log.info("为新用户 {} 创建积分账户成功，账户ID: {}", userId, pointsAccount.getId());

        return HttpResult.success(pointsAccount.getId());
    }


    /**
     * 查询积分明细
     */
    @GetMapping("/transactions")
    @Operation(summary = "查询积分明细", description = "分页查询当前用户的积分明细，支持按交易类型和积分来源筛选")
    public HttpResult<List<PointsTransactionVO>> getTransactions(
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer transactionType,
            @RequestParam(required = false) Integer pointsSource) {
        Long userId = getCurrentUserId();
        List<PointsTransactionVO> transactions = pointsService.getPointsTransactions(
            userId, pageNum, pageSize, transactionType, pointsSource);
        return HttpResult.success(transactions);
    }


    /**
     * 积分兑换商品
     */
    @PostMapping("/exchange")
    @Operation(summary = "积分兑换商品", description = "使用积分兑换商品")
    public HttpResult<Long> exchangeGoods(@RequestBody PointsExchangeDTO dto,
                                          @RequestHeader(value = "username") String username) {
        Long userId = userClient.getUserByName(username).getData().getId();
        Long orderId = exchangeRuleService.exchangeGoods(userId, dto);
        return HttpResult.success(orderId);
    }

    /**
     * 获取可兑换商品列表
     */
    @GetMapping("/exchange-goods")
    @Operation(summary = "获取可兑换商品列表", description = "查询所有可兑换的商品（包含商品信息和所需积分）")
    @Cacheable(value = "exchange", key = "'good_list'")
    public HttpResult<List<PointsExchangeGoodsVO>> getExchangeGoodsList() {
        List<PointsExchangeGoodsVO> goodsList =
            exchangeRuleService.getExchangeGoodsList();
        return HttpResult.success(goodsList);
    }

    /**
     * 获取积分+现金兑换比例
     */
    @GetMapping("/exchange-ratio")
    @Operation(summary = "获取积分+现金兑换比例", description = "查询积分兑换现金的比例")
    @Cacheable(value = "exchange", key = "'rate'")
    public HttpResult<java.math.BigDecimal> getExchangeRatio() {
        java.math.BigDecimal ratio = exchangeRuleService.getCashExchangeRatio();
        return HttpResult.success(ratio);
    }

    /**
     * 冻结积分
     */
    @GetMapping("/freeze")
    @Operation(summary = "冻结积分")
    public HttpResult<Boolean> freezePoints(@RequestParam Long userId,
                                            @RequestParam Long points,
                                            @RequestParam Long orderId) {
        return HttpResult.success(pointsService.freezePoints(userId, points, orderId));
    }

    /**
     * 计算可抵扣金额（用于前端显示"已优惠xx元"）
     */
    @GetMapping("/deductible-amount")
    @Operation(summary = "计算可抵扣金额", description = "根据订单金额和用户可用积分，计算可抵扣的现金金额")
    public HttpResult<java.math.BigDecimal> calculateDeductibleAmount(
            @RequestParam java.math.BigDecimal orderAmount) {
        Long userId = getCurrentUserId();
        java.math.BigDecimal deductibleAmount = pointsService.calculateDeductibleAmount(userId, orderAmount);
        return HttpResult.success(deductibleAmount);
    }

    /**
     * 查询即将过期的积分记录
     */
    @GetMapping("/expiring")
    @Operation(summary = "查询即将过期的积分记录", description = "查询当前用户即将过期的积分记录（按过期时间升序）")
    public HttpResult<List<PointsExpirationVO>> getExpiringPoints() {
        Long userId = getCurrentUserId();
        List<PointsExpirationVO> expiringList = pointsExpirationService.getExpiringPoints(userId);
        return HttpResult.success(expiringList);
    }

    /**
     * 查询已过期的积分记录
     */
    @GetMapping("/expired")
    @Operation(summary = "查询已过期的积分记录", description = "查询当前用户已过期的积分记录")
    public HttpResult<List<PointsExpirationVO>> getExpiredPoints() {
        Long userId = getCurrentUserId();
        List<PointsExpirationVO> expiredList = pointsExpirationService.getExpiredPoints(userId);
        return HttpResult.success(expiredList);
    }

    /**
     * 统计即将过期的积分总数
     */
    @GetMapping("/expiring/count")
    @Operation(summary = "统计即将过期的积分总数", description = "统计当前用户即将过期的积分总数")
    public HttpResult<Long> countExpiringPoints() {
        Long userId = getCurrentUserId();
        Long count = pointsExpirationService.countExpiringPoints(userId);
        return HttpResult.success(count);
    }

    /**
     * 统计即将过期的积分总数
     */
    @GetMapping("/type/count")
    @Operation(summary = "按类型统计积分")
    public HttpResult<Long> countPointsByType(@RequestParam String behaviorType) {
        Long userId = getCurrentUserId();
        Long count = marketingPointsRuleService.calculateBehaviorPoints(userId,behaviorType);
        return HttpResult.success(count);
    }

    @GetMapping("/deduct_rozen")
    @Operation(summary = "扣除冻结的积分")
    public HttpResult<Boolean> deductFrozenPoints(@RequestParam Long userId, @RequestParam Long orderId) {
        return HttpResult.success(pointsService.deductFrozenPoints(userId, orderId));
    }

    @GetMapping("/unfreeze")
    @Operation(summary = "解冻奖励积分")
    public HttpResult<Boolean> unfreezeRewardPoints(@RequestParam Long userId, @RequestParam Long orderId) {
        return HttpResult.success(pointsService.unfreezeRewardPoints(userId, orderId));
    }

    @GetMapping("/exchange_order")
    @Operation(summary = "获取积分兑换订单")
    public HttpResult<PointsExchangeOrder> gainByOrderId(@RequestParam Long orderId) {
        return HttpResult.success(pointsExchangeOrderMapper.selectByOrderId(orderId));
    }

    @GetMapping("/update_status")
    @Operation(summary = "更新积分兑换订单状态")
    public HttpResult<Boolean> updateOrderStatusFromPoint(@RequestParam Long orderId,@RequestParam Integer status) {
        return HttpResult.success(pointsService.updateStatus(orderId,status));
    }

    @GetMapping("/unfreeze/cancel")
    @Operation(summary = "解冻奖励积分（订单取消时）")
    public HttpResult<Boolean> unfreezePointsCanceled(@RequestParam Long userId, @RequestParam Long orderId) {
        return HttpResult.success(pointsService.unfreezePoints(userId, orderId));
    }

    @GetMapping("/cancel/cancel")
    @Operation(summary = "取消奖励积分（订单取消时）")
    public HttpResult<Boolean> cancelRewardPoints(@RequestParam Long userId, @RequestParam Long orderId) {
        return HttpResult.success(pointsService.cancelRewardPoints(userId, orderId));
    }

    @GetMapping("/vip/update")
    @Operation(summary = "更新会员等级并增加等级积分")
    public HttpResult<Boolean> updateVip(@RequestParam Long userId, @RequestParam Integer newMemberLevel) {
        return HttpResult.success(pointsService.upgradeMemberLevel(userId, newMemberLevel));
    }

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        return userClient.getUserByName(UserContext.getUsername()).getData().getId();
    }
}

