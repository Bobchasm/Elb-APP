package com.tju.elm_bk.mapper;

import com.tju.elm_bk.pojo.entity.PointsLotteryRule;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PointsLotteryRuleMapper {
    
    /**
     * 根据会员等级查询启用的抽奖规则（按排序和概率排序）
     */
    @Select("SELECT * FROM points_lottery_rule " +
            "WHERE member_level = #{memberLevel} AND rule_status = 1 AND is_deleted = 0 " +
            "AND (start_time IS NULL OR start_time <= NOW()) " +
            "AND (end_time IS NULL OR end_time >= NOW()) " +
            "ORDER BY prize_order ASC, probability DESC")
    List<PointsLotteryRule> selectByMemberLevel(Integer memberLevel);
    
    /**
     * 根据ID查询规则
     */
    @Select("SELECT * FROM points_lottery_rule WHERE id = #{id} AND is_deleted = 0")
    PointsLotteryRule selectById(Long id);
}

