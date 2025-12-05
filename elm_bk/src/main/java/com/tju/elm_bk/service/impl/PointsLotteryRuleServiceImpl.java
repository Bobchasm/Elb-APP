package com.tju.elm_bk.service.impl;

import com.tju.elm_bk.exception.APIException;
import com.tju.elm_bk.mapper.PointsLotteryRuleMapper;
import com.tju.elm_bk.mapper.UserMapper;
import com.tju.elm_bk.pojo.dto.PointsLotteryRuleCreateDTO;
import com.tju.elm_bk.pojo.dto.PointsLotteryRuleUpdateDTO;
import com.tju.elm_bk.pojo.entity.PointsLotteryRule;
import com.tju.elm_bk.pojo.vo.PointsLotteryRuleVO;
import com.tju.elm_bk.result.ResultCodeEnum;
import com.tju.elm_bk.service.PointsLotteryRuleService;
import com.tju.elm_bk.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 积分抽奖规则服务实现类
 * 职责：积分抽奖规则管理（增删改查、启用/禁用）
 */
@Slf4j
@Service
public class PointsLotteryRuleServiceImpl implements PointsLotteryRuleService {

    @Autowired
    private PointsLotteryRuleMapper lotteryRuleMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 创建积分抽奖规则
     */
    @Override
    @Transactional
    public Long createRule(PointsLotteryRuleCreateDTO dto) {
        PointsLotteryRule rule = new PointsLotteryRule();
        BeanUtils.copyProperties(dto, rule);
        
        // 设置默认值
        if (rule.getRuleStatus() == null) {
            rule.setRuleStatus(1); // 默认启用
        }
        if (rule.getPrizeOrder() == null) {
            rule.setPrizeOrder(0);
        }
        
        rule.setCreateTime(LocalDateTime.now());
        rule.setUpdateTime(LocalDateTime.now());
        rule.setIsDeleted(false);
        
        // 获取当前用户ID
        Long currentUserId = getCurrentUserId();
        rule.setCreator(currentUserId);
        rule.setUpdater(currentUserId);
        
        lotteryRuleMapper.insert(rule);
        return rule.getId();
    }

    /**
     * 更新积分抽奖规则
     */
    @Override
    @Transactional
    public Boolean updateRule(Long ruleId, PointsLotteryRuleUpdateDTO dto) {
        PointsLotteryRule rule = lotteryRuleMapper.selectById(ruleId);
        if (rule == null) {
            throw new APIException(ResultCodeEnum.NOT_FOUND);
        }

        // 更新字段
        if (dto.getRuleName() != null) {
            rule.setRuleName(dto.getRuleName());
        }
        if (dto.getMemberLevel() != null) {
            rule.setMemberLevel(dto.getMemberLevel());
        }
        if (dto.getPrizeType() != null) {
            rule.setPrizeType(dto.getPrizeType());
        }
        if (dto.getPrizePoints() != null) {
            rule.setPrizePoints(dto.getPrizePoints());
        }
        if (dto.getPrizeMultiplier() != null) {
            rule.setPrizeMultiplier(dto.getPrizeMultiplier());
        }
        if (dto.getProbability() != null) {
            rule.setProbability(dto.getProbability());
        }
        if (dto.getPrizeOrder() != null) {
            rule.setPrizeOrder(dto.getPrizeOrder());
        }
        if (dto.getPrizeDescription() != null) {
            rule.setPrizeDescription(dto.getPrizeDescription());
        }
        if (dto.getRuleStatus() != null) {
            rule.setRuleStatus(dto.getRuleStatus());
        }
        if (dto.getStartTime() != null) {
            rule.setStartTime(dto.getStartTime());
        }
        if (dto.getEndTime() != null) {
            rule.setEndTime(dto.getEndTime());
        }

        rule.setUpdateTime(LocalDateTime.now());
        rule.setUpdater(getCurrentUserId());
        
        lotteryRuleMapper.updateById(rule);
        return true;
    }

    /**
     * 删除积分抽奖规则
     */
    @Override
    @Transactional
    public Boolean deleteRule(Long ruleId) {
        PointsLotteryRule rule = lotteryRuleMapper.selectById(ruleId);
        if (rule == null) {
            throw new APIException(ResultCodeEnum.NOT_FOUND);
        }
        
        lotteryRuleMapper.deleteById(ruleId, LocalDateTime.now(), getCurrentUserId());
        return true;
    }

    /**
     * 查询积分抽奖规则列表
     */
    @Override
    public List<PointsLotteryRuleVO> getRules(Integer memberLevel, Integer ruleStatus, 
                                            Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        Integer offset = (pageNum - 1) * pageSize;
        List<PointsLotteryRule> rules = lotteryRuleMapper.selectRules(
            memberLevel, ruleStatus, offset, pageSize);

        List<PointsLotteryRuleVO> voList = new ArrayList<>();
        for (PointsLotteryRule rule : rules) {
            PointsLotteryRuleVO vo = new PointsLotteryRuleVO();
            BeanUtils.copyProperties(rule, vo);
            vo.setMemberLevelName(getMemberLevelName(rule.getMemberLevel()));
            vo.setPrizeTypeName(getPrizeTypeName(rule.getPrizeType()));
            voList.add(vo);
        }

        return voList;
    }

    /**
     * 启用积分抽奖规则
     */
    @Override
    @Transactional
    public Boolean enableRule(Long ruleId) {
        PointsLotteryRule rule = lotteryRuleMapper.selectById(ruleId);
        if (rule == null) {
            throw new APIException(ResultCodeEnum.NOT_FOUND);
        }
        
        rule.setRuleStatus(1); // 1-启用
        rule.setUpdateTime(LocalDateTime.now());
        rule.setUpdater(getCurrentUserId());
        
        lotteryRuleMapper.updateById(rule);
        return true;
    }

    /**
     * 禁用积分抽奖规则
     */
    @Override
    @Transactional
    public Boolean disableRule(Long ruleId) {
        PointsLotteryRule rule = lotteryRuleMapper.selectById(ruleId);
        if (rule == null) {
            throw new APIException(ResultCodeEnum.NOT_FOUND);
        }
        
        rule.setRuleStatus(0); // 0-禁用
        rule.setUpdateTime(LocalDateTime.now());
        rule.setUpdater(getCurrentUserId());
        
        lotteryRuleMapper.updateById(rule);
        return true;
    }

    /**
     * 获取会员等级名称
     */
    private String getMemberLevelName(Integer memberLevel) {
        if (memberLevel == null) {
            return "未知";
        }
        switch (memberLevel) {
            case 0: return "普通用户";
            case 1: return "白银会员";
            case 2: return "黄金会员";
            case 3: return "钻石会员";
            default: return "未知";
        }
    }

    /**
     * 获取奖品类型名称
     */
    private String getPrizeTypeName(Integer prizeType) {
        if (prizeType == null) {
            return "未知";
        }
        switch (prizeType) {
            case 0: return "没中奖";
            case 1: return "固定积分";
            case 2: return "积分翻倍";
            default: return "未知";
        }
    }

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        Long currentUserId = userMapper.getUserIdByUsername(
                SecurityUtils.getCurrentUsername().orElse(null));
        return currentUserId;
    }
}

