package com.tju.elm_bk.controller;

import com.tju.elm_bk.pojo.dto.PointsExchangeRuleCreateDTO;
import com.tju.elm_bk.pojo.dto.PointsRuleCreateDTO;
import com.tju.elm_bk.pojo.dto.PointsRuleUpdateDTO;
import com.tju.elm_bk.pojo.entity.PointsExpirationAlertConfig;
import com.tju.elm_bk.pojo.vo.PointsExchangeRuleVO;
import com.tju.elm_bk.pojo.vo.PointsRuleVO;
import com.tju.elm_bk.result.HttpResult;
import com.tju.elm_bk.service.MarketingPointsExchangeRuleService;
import com.tju.elm_bk.service.MarketingPointsRuleService;
import com.tju.elm_bk.service.PointsExpirationAlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 营销系统Controller
 * 职责：处理管理员端营销系统相关HTTP请求
 * 设计原则：
 * 1. 单一职责原则 - 只负责HTTP请求处理
 * 2. 依赖注入 - 注入Service接口
 * 3. 权限控制 - 使用@PreAuthorize控制访问权限
 */
@RestController
@RequestMapping("/api/marketing/points")
@Tag(name = "营销系统-积分管理", description = "管理员端积分规则管理接口")
@PreAuthorize("hasAuthority('ADMIN')")
public class MarketingPointsController {

    @Autowired
    private MarketingPointsRuleService marketingPointsRuleService;

    @Autowired
    private MarketingPointsExchangeRuleService exchangeRuleService;

    @Autowired
    private PointsExpirationAlertService alertService;

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
    public HttpResult<Boolean> deleteRule(@PathVariable Long id) {
        Boolean result = marketingPointsRuleService.deleteRule(id);
        return HttpResult.success(result);
    }

    /**
     * 查询积分规则列表
     */
    @GetMapping("/rules")
    @Operation(summary = "查询积分规则列表", description = "分页查询积分规则列表")
    public HttpResult<List<PointsRuleVO>> getRules(
            @RequestParam(required = false) Integer ruleType,
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        List<PointsRuleVO> rules = marketingPointsRuleService.getRules(ruleType, pageNum, pageSize);
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
    public HttpResult<Boolean> deleteExchangeRule(@PathVariable Long id) {
        Boolean result = exchangeRuleService.deleteRule(id);
        return HttpResult.success(result);
    }

    /**
     * 查询积分兑换规则列表
     */
    @GetMapping("/exchange-rules")
    @Operation(summary = "查询积分兑换规则列表", description = "分页查询积分兑换规则列表")
    public HttpResult<List<PointsExchangeRuleVO>> getExchangeRules(
            @RequestParam(required = false) Integer ruleType,
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        List<PointsExchangeRuleVO> rules = exchangeRuleService.getRules(ruleType, pageNum, pageSize);
        return HttpResult.success(rules);
    }

    // ========== 预警配置管理 ==========

    /**
     * 查询预警配置
     */
    @GetMapping("/alert-config")
    @Operation(summary = "查询预警配置", description = "查询积分到期预警配置")
    public HttpResult<PointsExpirationAlertConfig> getAlertConfig() {
        PointsExpirationAlertConfig config = alertService.getAlertConfig();
        return HttpResult.success(config);
    }

    /**
     * 更新预警配置
     */
    @PutMapping("/alert-config")
    @Operation(summary = "更新预警配置", description = "更新积分到期预警配置")
    public HttpResult<Boolean> updateAlertConfig(@RequestBody PointsExpirationAlertConfig config) {
        Boolean result = alertService.updateAlertConfig(config);
        return HttpResult.success(result);
    }
}

