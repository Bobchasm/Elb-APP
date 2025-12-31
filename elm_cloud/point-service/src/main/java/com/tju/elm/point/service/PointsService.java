package com.tju.elm.point.service;

import com.tju.elm.point.zoo.pojo.dto.PointsAddDTO;
import com.tju.elm.point.zoo.pojo.dto.PointsDeductDTO;
import com.tju.elm.point.zoo.pojo.vo.PointsAccountVO;
import com.tju.elm.point.zoo.pojo.vo.PointsTransactionVO;

import java.util.List;

/**
 * 积分系统核心服务接口
 * 职责：积分账户操作（增加、减少、查询、查询明细）
 * 设计原则：单一职责原则 - 只负责积分账户操作，不涉及规则计算
 */
public interface PointsService {
    
//    /**
//     * 增加积分
//     * @param pointsAddDTO 积分增加DTO
//     * @return 积分明细ID
//     */
//    Long addPoints(PointsAddDTO pointsAddDTO);
//
//    /**
//     * 减少积分（优先扣减即将过期的积分）
//     * @param pointsDeductDTO 积分扣减DTO
//     * @return 是否成功
//     */
//    Boolean deductPoints(PointsDeductDTO pointsDeductDTO);
    
    /**
     * 查询用户积分账户
     * @param userId 用户ID
     * @return 积分账户信息
     */
    PointsAccountVO getPointsAccount(Long userId);
    
    /**
     * 查询积分明细
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param transactionType 交易类型（可选）
     * @param pointsSource 积分来源（可选）
     * @return 积分明细列表
     */
    List<PointsTransactionVO> getPointsTransactions(Long userId, Integer pageNum, Integer pageSize, Integer transactionType, Integer pointsSource);
    
//    /**
//     * 冻结积分（用于订单处理）
//     * @param userId 用户ID
//     * @param points 积分数量
//     * @param orderId 订单ID
//     * @return 是否成功
//     */
//    Boolean freezePoints(Long userId, Long points, Long orderId);
//
//    /**
//     * 解冻积分（订单取消时）
//     * @param userId 用户ID
//     * @param orderId 订单ID
//     * @return 是否成功
//     */
//    Boolean unfreezePoints(Long userId, Long orderId);
//
//    /**
//     * 解冻奖励积分（订单完成时）
//     * @param userId 用户ID
//     * @param orderId 订单ID
//     * @return 是否成功
//     */
//    Boolean unfreezeRewardPoints(Long userId, Long orderId);
//
//    /**
//     * 取消奖励积分（订单取消时）
//     * @param userId 用户ID
//     * @param orderId 订单ID
//     * @return 是否成功
//     */
//    Boolean cancelRewardPoints(Long userId, Long orderId);
//
//    /**
//     * 更新会员等级并增加等级积分
//     * @param userId 用户ID
//     * @param newMemberLevel 新会员等级（1-白银，2-黄金，3-钻石）
//     * @return 是否成功
//     */
//    Boolean upgradeMemberLevel(Long userId, Integer newMemberLevel);
    
    /**
     * 计算可用积分可以抵扣的现金金额
     * @param userId 用户ID
     * @param orderAmount 订单金额（用于计算最大可抵扣金额）
     * @return 可抵扣金额（元）
     */
    java.math.BigDecimal calculateDeductibleAmount(Long userId, java.math.BigDecimal orderAmount);
    
//    /**
//     * 真正扣除冻结的积分（订单完成时）
//     * @param userId 用户ID
//     * @param orderId 订单ID
//     * @return 是否成功
//     */
//    Boolean deductFrozenPoints(Long userId, Long orderId);
}

