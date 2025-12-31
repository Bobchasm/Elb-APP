package com.tju.elm.point.service;

import com.tju.elm.point.zoo.pojo.dto.PointsLotteryRuleCreateDTO;
import com.tju.elm.point.zoo.pojo.dto.PointsLotteryRuleUpdateDTO;
import com.tju.elm.point.zoo.pojo.vo.PointsLotteryRuleVO;

import java.util.List;

/**
 * 积分抽奖规则服务接口
 * 职责：积分抽奖规则管理（增删改查、启用/禁用）
 */
public interface PointsLotteryRuleService {
    
//    /**
//     * 创建积分抽奖规则
//     * @param dto 创建规则DTO
//     * @return 规则ID
//     */
//    Long createRule(PointsLotteryRuleCreateDTO dto);
//
//    /**
//     * 更新积分抽奖规则
//     * @param ruleId 规则ID
//     * @param dto 更新规则DTO
//     * @return 是否成功
//     */
//    Boolean updateRule(Long ruleId, PointsLotteryRuleUpdateDTO dto);
//
//    /**
//     * 删除积分抽奖规则
//     * @param ruleId 规则ID
//     * @return 是否成功
//     */
//    Boolean deleteRule(Long ruleId);

    /**
     * 查询积分抽奖规则列表
     * @param memberLevel 会员等级（可选）
     * @param ruleStatus 规则状态（可选）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 积分抽奖规则列表
     */
    List<PointsLotteryRuleVO> getRules(Integer memberLevel, Integer ruleStatus,
                                       Integer pageNum, Integer pageSize);

}

