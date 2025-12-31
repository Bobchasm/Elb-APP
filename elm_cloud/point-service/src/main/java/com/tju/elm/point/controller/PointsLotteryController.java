package com.tju.elm.point.controller;

import com.tju.elm.point.service.PointsLotteryService;
import com.tju.elm.point.zoo.pojo.vo.PointsLotteryInfoVO;
import com.tju.elm.point.zoo.pojo.vo.PointsLotteryRecordVO;
import com.tju.elm.point.zoo.pojo.vo.PointsLotteryResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import result.HttpResult;

import java.util.List;

/**
 * 积分抽奖Controller
 */
@RestController
@RequestMapping("/api/points/lottery")
@Tag(name = "积分抽奖", description = "积分抽奖相关接口")
public class PointsLotteryController {

    @Autowired
    private PointsLotteryService pointsLotteryService;

//    @Autowired
//    private UserMapper userMapper;

//    /**
//     * 获取用户抽奖信息
//     */
//    @GetMapping("/info")
//    @Operation(summary = "获取抽奖信息", description = "获取当前用户的抽奖信息，包括会员等级、剩余次数等")
//    public HttpResult<PointsLotteryInfoVO> getLotteryInfo() {
//        Long userId = getCurrentUserId();
//        PointsLotteryInfoVO info = pointsLotteryService.getLotteryInfo(userId);
//        return HttpResult.success(info);
//    }
//
//    /**
//     * 执行抽奖
//     */
//    @PostMapping("/draw")
//    @Operation(summary = "执行抽奖", description = "执行一次抽奖，返回抽奖结果")
//    public HttpResult<PointsLotteryResultVO> doLottery() {
//        Long userId = getCurrentUserId();
//        PointsLotteryResultVO result = pointsLotteryService.doLottery(userId);
//        return HttpResult.success(result);
//    }
//
//    /**
//     * 查询抽奖记录
//     */
//    @GetMapping("/records")
//    @Operation(summary = "查询抽奖记录", description = "查询当前用户的抽奖记录")
//    public HttpResult<List<PointsLotteryRecordVO>> getLotteryRecords(
//            @RequestParam(required = false, defaultValue = "10") Integer limit) {
//        Long userId = getCurrentUserId();
//        List<PointsLotteryRecordVO> records = pointsLotteryService.getLotteryRecords(userId, limit);
//        return HttpResult.success(records);
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

