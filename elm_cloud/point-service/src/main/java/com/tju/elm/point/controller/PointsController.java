package com.tju.elm.point.controller;

import com.tju.elm.point.service.MarketingPointsExchangeRuleService;
import com.tju.elm.point.service.PointsExpirationService;
import com.tju.elm.point.service.PointsService;
import com.tju.elm.point.zoo.pojo.dto.PointsExchangeDTO;
import com.tju.elm.point.zoo.pojo.vo.PointsAccountVO;
import com.tju.elm.point.zoo.pojo.vo.PointsExchangeGoodsVO;
import com.tju.elm.point.zoo.pojo.vo.PointsExpirationVO;
import com.tju.elm.point.zoo.pojo.vo.PointsTransactionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import result.HttpResult;

import java.util.List;

/**
 * 积分系统Controller
 * 职责：处理用户端积分相关HTTP请求
 * 设计原则：
 * 1. 单一职责原则 - 只负责HTTP请求处理
 * 2. 依赖注入 - 注入Service接口
 * 3. 基于接口编程 - 依赖Service接口而非实现
 */
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

//    @Autowired
//    private UserMapper userMapper;

//    /**
//     * 查询积分账户
//     */
//    @GetMapping("/account")
//    @Operation(summary = "查询积分账户", description = "查询当前用户的积分账户信息")
//    public HttpResult<PointsAccountVO> getAccount() {
//        Long userId = getCurrentUserId();
//        PointsAccountVO account = pointsService.getPointsAccount(userId);
//        return HttpResult.success(account);
//    }
//
//    /**
//     * 查询积分明细
//     */
//    @GetMapping("/transactions")
//    @Operation(summary = "查询积分明细", description = "分页查询当前用户的积分明细，支持按交易类型和积分来源筛选")
//    public HttpResult<List<PointsTransactionVO>> getTransactions(
//            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
//            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
//            @RequestParam(required = false) Integer transactionType,
//            @RequestParam(required = false) Integer pointsSource) {
//        Long userId = getCurrentUserId();
//        List<PointsTransactionVO> transactions = pointsService.getPointsTransactions(
//            userId, pageNum, pageSize, transactionType, pointsSource);
//        return HttpResult.success(transactions);
//    }

//    /**
//     * 积分兑换商品
//     */
//    @PostMapping("/exchange")
//    @Operation(summary = "积分兑换商品", description = "使用积分兑换商品")
//    public HttpResult<Long> exchangeGoods(@RequestBody PointsExchangeDTO dto) {
//        Long userId = getCurrentUserId();
//        Long orderId = exchangeRuleService.exchangeGoods(userId, dto);
//        return HttpResult.success(orderId);
//    }
//
//    /**
//     * 获取可兑换商品列表
//     */
//    @GetMapping("/exchange-goods")
//    @Operation(summary = "获取可兑换商品列表", description = "查询所有可兑换的商品（包含商品信息和所需积分）")
//    public HttpResult<List<PointsExchangeGoodsVO>> getExchangeGoodsList() {
//        List<PointsExchangeGoodsVO> goodsList =
//            exchangeRuleService.getExchangeGoodsList();
//        return HttpResult.success(goodsList);
//    }

    /**
     * 获取积分+现金兑换比例
     */
    @GetMapping("/exchange-ratio")
    @Operation(summary = "获取积分+现金兑换比例", description = "查询积分兑换现金的比例")
    public HttpResult<java.math.BigDecimal> getExchangeRatio() {
        java.math.BigDecimal ratio = exchangeRuleService.getCashExchangeRatio();
        return HttpResult.success(ratio);
    }

//    /**
//     * 计算可抵扣金额（用于前端显示"已优惠xx元"）
//     */
//    @GetMapping("/deductible-amount")
//    @Operation(summary = "计算可抵扣金额", description = "根据订单金额和用户可用积分，计算可抵扣的现金金额")
//    public HttpResult<java.math.BigDecimal> calculateDeductibleAmount(
//            @RequestParam java.math.BigDecimal orderAmount) {
//        Long userId = getCurrentUserId();
//        java.math.BigDecimal deductibleAmount = pointsService.calculateDeductibleAmount(userId, orderAmount);
//        return HttpResult.success(deductibleAmount);
//    }
//
//    /**
//     * 查询即将过期的积分记录
//     */
//    @GetMapping("/expiring")
//    @Operation(summary = "查询即将过期的积分记录", description = "查询当前用户即将过期的积分记录（按过期时间升序）")
//    public HttpResult<List<PointsExpirationVO>> getExpiringPoints() {
//        Long userId = getCurrentUserId();
//        List<PointsExpirationVO> expiringList = pointsExpirationService.getExpiringPoints(userId);
//        return HttpResult.success(expiringList);
//    }
//
//    /**
//     * 查询已过期的积分记录
//     */
//    @GetMapping("/expired")
//    @Operation(summary = "查询已过期的积分记录", description = "查询当前用户已过期的积分记录")
//    public HttpResult<List<PointsExpirationVO>> getExpiredPoints() {
//        Long userId = getCurrentUserId();
//        List<PointsExpirationVO> expiredList = pointsExpirationService.getExpiredPoints(userId);
//        return HttpResult.success(expiredList);
//    }
//
//    /**
//     * 统计即将过期的积分总数
//     */
//    @GetMapping("/expiring/count")
//    @Operation(summary = "统计即将过期的积分总数", description = "统计当前用户即将过期的积分总数")
//    public HttpResult<Long> countExpiringPoints() {
//        Long userId = getCurrentUserId();
//        Long count = pointsExpirationService.countExpiringPoints(userId);
//        return HttpResult.success(count);
//    }

//    /**
//     * 获取当前用户ID
//     */
//    private Long getCurrentUserId() {
//        String username = SecurityUtils.getCurrentUsername()
//            .orElseThrow(() -> new com.tju.elm_bk.exception.APIException(
//                com.tju.elm_bk.result.ResultCodeEnum.VALUE_MISSED));
//        return userMapper.getUserIdByUsername(username);
//    }
}

