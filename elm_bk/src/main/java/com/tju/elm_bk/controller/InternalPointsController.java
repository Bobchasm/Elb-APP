package com.tju.elm_bk.controller;

import com.tju.elm_bk.pojo.dto.PointsAddDTO;
import com.tju.elm_bk.pojo.dto.PointsDeductDTO;
import com.tju.elm_bk.pojo.vo.PointsAccountVO;
import com.tju.elm_bk.result.HttpResult;
import com.tju.elm_bk.service.PointsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 内部积分接口Controller
 * 职责：提供内部系统调用的积分接口（如营销系统、订单系统）
 * 设计原则：
 * 1. 单一职责原则 - 只负责内部接口
 * 2. 依赖注入 - 注入Service接口
 * 3. 封装与抽象 - 封装内部调用逻辑
 * 
 * 注意：这些接口应该通过内部网络调用，不应该暴露给外部
 */
@RestController
@RequestMapping("/api/internal/points")
@Tag(name = "内部积分接口", description = "供其他系统调用的内部积分接口")
public class InternalPointsController {

    @Autowired
    private PointsService pointsService;

    /**
     * 增加积分（内部接口）
     */
    @PostMapping("/add")
    @Operation(summary = "增加积分", description = "内部接口：增加用户积分")
    public HttpResult<Long> addPoints(@RequestBody PointsAddDTO dto) {
        Long transactionId = pointsService.addPoints(dto);
        return HttpResult.success(transactionId);
    }

    /**
     * 减少积分（内部接口）
     */
    @PostMapping("/deduct")
    @Operation(summary = "减少积分", description = "内部接口：减少用户积分")
    public HttpResult<Boolean> deductPoints(@RequestBody PointsDeductDTO dto) {
        Boolean result = pointsService.deductPoints(dto);
        return HttpResult.success(result);
    }

    /**
     * 查询积分账户（内部接口）
     */
    @GetMapping("/account/{userId}")
    @Operation(summary = "查询积分账户", description = "内部接口：查询指定用户的积分账户")
    public HttpResult<PointsAccountVO> getAccount(@PathVariable Long userId) {
        PointsAccountVO account = pointsService.getPointsAccount(userId);
        return HttpResult.success(account);
    }

    /**
     * 冻结积分（内部接口）
     */
    @PostMapping("/freeze")
    @Operation(summary = "冻结积分", description = "内部接口：冻结用户积分（用于订单处理）")
    public HttpResult<Boolean> freezePoints(@RequestParam Long userId, 
                                           @RequestParam Long points,
                                           @RequestParam Long orderId) {
        Boolean result = pointsService.freezePoints(userId, points, orderId);
        return HttpResult.success(result);
    }

    /**
     * 解冻积分（内部接口）
     */
    @PostMapping("/unfreeze")
    @Operation(summary = "解冻积分", description = "内部接口：解冻用户积分（订单取消时）")
    public HttpResult<Boolean> unfreezePoints(@RequestParam Long userId, 
                                             @RequestParam Long orderId) {
        Boolean result = pointsService.unfreezePoints(userId, orderId);
        return HttpResult.success(result);
    }
}

