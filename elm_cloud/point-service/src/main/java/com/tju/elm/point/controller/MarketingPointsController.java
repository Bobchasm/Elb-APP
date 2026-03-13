package com.tju.elm.point.controller;

import com.tju.elm.point.service.MarketingPointsExchangeRuleService;
import com.tju.elm.point.service.MarketingPointsRuleService;
import com.tju.elm.point.service.PointsExpirationAlertService;
import com.tju.elm.point.service.PointsLotteryRuleService;
import com.tju.elm.point.zoo.pojo.dto.*;
import com.tju.elm.point.zoo.pojo.entity.PointsExpirationAlertConfig;
import com.tju.elm.point.zoo.pojo.vo.PointsExchangeRuleVO;
import com.tju.elm.point.zoo.pojo.vo.PointsLotteryRuleVO;
import com.tju.elm.point.zoo.pojo.vo.PointsRuleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import result.HttpResult;

import java.util.List;

/**
 * 营销系统Controller
 * 职责：处理营销系统相关HTTP请求
 * 设计原则：
 * 1. 单一职责原则 - 只负责HTTP请求处理
 * 2. 依赖注入 - 注入Service接口
 * 3. 权限控制 - 使用@PreAuthorize控制访问权限（查询接口开放，增删改需要管理员权限）
 */
@RestController
@RequestMapping("/api/marketing/points")
@Tag(name = "营销系统-积分管理", description = "积分规则管理接口")
public class MarketingPointsController {

    @Autowired
    private MarketingPointsRuleService marketingPointsRuleService;

    @Autowired
    private MarketingPointsExchangeRuleService exchangeRuleService;

    @Autowired
    private PointsExpirationAlertService alertService;

    @Autowired
    private PointsLotteryRuleService lotteryRuleService;

    // ========== 积分规则管理 ==========

    /**
     * 创建积分规则
     */
    @PostMapping("/rules")
    @Operation(summary = "创建积分规则", description = "创建新的积分获取规则")
    public HttpResult<Long> createRule(@RequestBody PointsRuleCreateDTO dto) {
        Long ruleId = marketingPointsRuleService.createRule(dto);
        return HttpResult.success(ruleId);
    }

    /**
     * 更新积分规则
     */
    @PutMapping("/rules/{id}")
    @Operation(summary = "更新积分规则", description = "更新指定的积分规则")
    @CacheEvict(value = "point_rule_list", allEntries = true)
    public HttpResult<Boolean> updateRule(@PathVariable Long id, 
                                          @RequestBody PointsRuleUpdateDTO dto) {
        Boolean result = marketingPointsRuleService.updateRule(id, dto);
        return HttpResult.success(result);
    }

    /**
     * 删除积分规则
     */
    @DeleteMapping("/rules/{id}")
    @Operation(summary = "删除积分规则", description = "删除指定的积分规则")
    @CacheEvict(value = "point_rule_list", allEntries = true)
    public HttpResult<Boolean> deleteRule(@PathVariable Long id) {
        Boolean result = marketingPointsRuleService.deleteRule(id);
        return HttpResult.success(result);
    }

    /**
     * 查询积分规则列表
     */
    @GetMapping("/rules")
    @Operation(summary = "查询积分规则列表", description = "分页查询积分规则列表，支持按规则类型和状态筛选")
    @Cacheable(value = "point_rule_list", key = "'rt' + (#ruleType != null ? #ruleType : '') + 'rs' + (#ruleStatus != null ? #ruleStatus : '') + 'pn' + (#pageNum != null ? #pageNum : 1) + 'ps' + (#pageSize != null ? #pageSize : 10)")
    public HttpResult<List<PointsRuleVO>> getRules(
            @RequestParam(required = false) Integer ruleType,
            @RequestParam(required = false) Integer ruleStatus,
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        List<PointsRuleVO> rules = marketingPointsRuleService.getRules(ruleType, ruleStatus, pageNum, pageSize);
        return HttpResult.success(rules);
    }

    // ========== 积分兑换规则管理 ==========

    /**
     * 创建积分兑换规则
     */
    @PostMapping("/exchange-rules")
    @Operation(summary = "创建积分兑换规则", description = "创建新的积分兑换规则")
    public HttpResult<Long> createExchangeRule(@RequestBody PointsExchangeRuleCreateDTO dto) {
        Long ruleId = exchangeRuleService.createRule(dto);
        return HttpResult.success(ruleId);
    }

    /**
     * 更新积分兑换规则
     */
    @PutMapping("/exchange-rules/{id}")
    @Operation(summary = "更新积分兑换规则", description = "更新指定的积分兑换规则")
    @CacheEvict(value = "point_exchange_rule_list", allEntries = true)
    public HttpResult<Boolean> updateExchangeRule(@PathVariable Long id, 
                                                   @RequestBody PointsExchangeRuleCreateDTO dto) {
        Boolean result = exchangeRuleService.updateRule(id, dto);
        return HttpResult.success(result);
    }

    /**
     * 删除积分兑换规则
     */
    @DeleteMapping("/exchange-rules/{id}")
    @Operation(summary = "删除积分兑换规则", description = "删除指定的积分兑换规则")
    @CacheEvict(value = "point_exchange_rule_list", allEntries = true)
    public HttpResult<Boolean> deleteExchangeRule(@PathVariable Long id) {
        Boolean result = exchangeRuleService.deleteRule(id);
        return HttpResult.success(result);
    }

    /**
     * 查询积分兑换规则列表
     */
    @GetMapping("/exchange-rules")
    @Operation(summary = "查询积分兑换规则列表", description = "分页查询积分兑换规则列表，支持按规则类型和状态筛选")
    @Cacheable(value = "point_exchange_rule_list", key = "'rt' + (#ruleType != null ? #ruleType : '') + 'rs' + (#ruleStatus != null ? #ruleStatus : '') + (#pageNum != null ? #pageNum : 1) + 'ps' + (#pageSize != null ? #pageSize : 10)")
    public HttpResult<List<PointsExchangeRuleVO>> getExchangeRules(
            @RequestParam(required = false) Integer ruleType,
            @RequestParam(required = false) Integer ruleStatus,
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        List<PointsExchangeRuleVO> rules = exchangeRuleService.getRules(ruleType, ruleStatus, pageNum, pageSize);
        return HttpResult.success(rules);
    }

    // ========== 积分抽奖规则管理 ==========

    /**
     * 创建积分抽奖规则
     */
    @PostMapping("/lottery-rules")
    @Operation(summary = "创建积分抽奖规则", description = "创建新的积分抽奖规则")
    public HttpResult<Long> createLotteryRule(@RequestBody PointsLotteryRuleCreateDTO dto) {
        Long ruleId = lotteryRuleService.createRule(dto);
        return HttpResult.success(ruleId);
    }

    /**
     * 更新积分抽奖规则
     */
    @PutMapping("/lottery-rules/{id}")
    @Operation(summary = "更新积分抽奖规则", description = "更新指定的积分抽奖规则")
    @CacheEvict(value = "point_lottery_rule_list", allEntries = true)
    public HttpResult<Boolean> updateLotteryRule(@PathVariable Long id, 
                                                 @RequestBody PointsLotteryRuleUpdateDTO dto) {
        Boolean result = lotteryRuleService.updateRule(id, dto);
        return HttpResult.success(result);
    }

    /**
     * 删除积分抽奖规则
     */
    @DeleteMapping("/lottery-rules/{id}")
    @Operation(summary = "删除积分抽奖规则", description = "删除指定的积分抽奖规则")
    @CacheEvict(value = "point_lottery_rule_list", allEntries = true)
    public HttpResult<Boolean> deleteLotteryRule(@PathVariable Long id) {
        Boolean result = lotteryRuleService.deleteRule(id);
        return HttpResult.success(result);
    }

    /**
     * 查询积分抽奖规则列表
     */
    @GetMapping("/lottery-rules")
    @Operation(summary = "查询积分抽奖规则列表", description = "分页查询积分抽奖规则列表，支持按会员等级和状态筛选")
    @Cacheable(value = "point_lottery_rule_list", key = "'ml' + (#memberLevel != null ? #memberLevel : '') + 'rs' + (#ruleStatus != null ? #ruleStatus : '') + 'pn' + (#pageNum != null ? #pageNum : 1) + 'ps' + (#pageSize != null ? #pageSize : 10)")
    public HttpResult<List<PointsLotteryRuleVO>> getLotteryRules(
            @RequestParam(required = false) Integer memberLevel,
            @RequestParam(required = false) Integer ruleStatus,
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        List<PointsLotteryRuleVO> rules = lotteryRuleService.getRules(memberLevel, ruleStatus, pageNum, pageSize);
        return HttpResult.success(rules);
    }

    // ========== 预警配置管理 ==========

    /**
     * 查询预警配置
     */
    @GetMapping("/alert-config")
    @Operation(summary = "查询预警配置", description = "查询积分到期预警配置")
    @Cacheable(value = "point", key = "'alert'")
    public HttpResult<PointsExpirationAlertConfig> getAlertConfig() {
        PointsExpirationAlertConfig config = alertService.getAlertConfig();
        return HttpResult.success(config);
    }

    /**
     * 更新预警配置
     */
    @PutMapping("/alert-config")
    @Operation(summary = "更新预警配置", description = "更新积分到期预警配置")
    @CacheEvict(value = "point", key = "'alert'")
    public HttpResult<Boolean> updateAlertConfig(@RequestBody PointsExpirationAlertConfig config) {
        Boolean result = alertService.updateAlertConfig(config);
        return HttpResult.success(result);
    }
}

