package com.tju.elm.point.service;

import com.tju.elm.point.zoo.pojo.vo.PointsLotteryInfoVO;
import com.tju.elm.point.zoo.pojo.vo.PointsLotteryRecordVO;
import com.tju.elm.point.zoo.pojo.vo.PointsLotteryResultVO;

import java.util.List;

/**
 * 积分抽奖服务接口
 */
public interface PointsLotteryService {
    
    /**
     * 获取用户抽奖信息（包括剩余次数等）
     * @param userId 用户ID
     * @return 抽奖信息
     */
    PointsLotteryInfoVO getLotteryInfo(Long userId);
    
    /**
     * 执行抽奖
     * @param userId 用户ID
     * @return 抽奖结果
     */
    PointsLotteryResultVO doLottery(Long userId);
    
    /**
     * 查询用户抽奖记录
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 抽奖记录列表
     */
    List<PointsLotteryRecordVO> getLotteryRecords(Long userId, Integer limit);
}

