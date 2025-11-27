package com.tju.elm_bk.mapper;

import com.tju.elm_bk.pojo.entity.MarketingPointsRule;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface MarketingPointsRuleMapper {
    
    /**
     * 插入积分规则
     */
    @Insert("INSERT INTO marketing_points_rule (rule_name, rule_type, rule_status, points_ratio, " +
            "points_multiplier, member_level, min_order_amount, max_order_amount, food_id, " +
            "holiday_start, holiday_end, behavior_type, points_amount, " +
            "expire_days, start_time, end_time, priority, create_time, creator, updater, is_deleted) " +
            "VALUES (#{ruleName}, #{ruleType}, #{ruleStatus}, #{pointsRatio}, #{pointsMultiplier}, " +
            "#{memberLevel}, #{minOrderAmount}, #{maxOrderAmount}, #{foodId}, " +
            "#{holidayStart}, #{holidayEnd}, #{behaviorType}, #{pointsAmount}, #{expireDays}, " +
            "#{startTime}, #{endTime}, #{priority}, #{createTime}, #{creator}, #{updater}, #{isDeleted})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(MarketingPointsRule rule);
    
    /**
     * 根据ID查询积分规则
     */
    @Select("SELECT * FROM marketing_points_rule WHERE id = #{id} AND is_deleted = 0")
    MarketingPointsRule selectById(Long id);
    
    /**
     * 更新积分规则
     */
    void updateById(MarketingPointsRule rule);
    
    /**
     * 逻辑删除积分规则
     */
    @Update("UPDATE marketing_points_rule SET is_deleted = 1, update_time = #{updateTime}, " +
            "updater = #{updater} WHERE id = #{id}")
    void deleteById(@Param("id") Long id, @Param("updateTime") LocalDateTime updateTime, 
                    @Param("updater") Long updater);
    
    /**
     * 查询积分规则列表
     */
    List<MarketingPointsRule> selectRules(@Param("ruleType") Integer ruleType,
                                          @Param("ruleStatus") Integer ruleStatus,
                                          @Param("offset") Integer offset,
                                          @Param("limit") Integer limit);
    
    /**
     * 统计积分规则总数
     */
    @Select("SELECT COUNT(*) FROM marketing_points_rule WHERE is_deleted = 0 " +
            "AND (#{ruleType} IS NULL OR rule_type = #{ruleType}) " +
            "AND (#{ruleStatus} IS NULL OR rule_status = #{ruleStatus})")
    Long countRules(@Param("ruleType") Integer ruleType, @Param("ruleStatus") Integer ruleStatus);
    
    /**
     * 查询适用的消费积分规则
     */
    @Select("SELECT * FROM marketing_points_rule WHERE rule_type = 0 AND rule_status = 1 " +
            "AND is_deleted = 0 AND (member_level IS NULL OR member_level = #{memberLevel}) " +
            "AND (start_time IS NULL OR start_time <= NOW()) " +
            "AND (end_time IS NULL OR end_time >= NOW()) " +
            "ORDER BY priority DESC LIMIT 1")
    MarketingPointsRule selectBaseConsumptionRule(Integer memberLevel);
    
    /**
     * 查询适用的促销积分规则
     */
    List<MarketingPointsRule> selectPromotionRules(@Param("orderDate") LocalDate orderDate,
                                                   @Param("orderAmount") java.math.BigDecimal orderAmount,
                                                   @Param("foodIds") List<Long> foodIds,
                                                   @Param("memberLevel") Integer memberLevel);
    
    /**
     * 查询适用的等级积分规则
     */
    @Select("SELECT * FROM marketing_points_rule WHERE rule_type = 2 AND rule_status = 1 " +
            "AND is_deleted = 0 AND member_level = #{memberLevel} " +
            "AND (start_time IS NULL OR start_time <= NOW()) " +
            "AND (end_time IS NULL OR end_time >= NOW()) " +
            "ORDER BY priority DESC LIMIT 1")
    MarketingPointsRule selectLevelRule(Integer memberLevel);
    
    /**
     * 查询适用的行为积分规则（根据会员等级）
     * 优先匹配精确的会员等级，如果没有则匹配通用规则（member_level IS NULL）
     */
    @Select("SELECT * FROM marketing_points_rule WHERE rule_type = 3 AND rule_status = 1 " +
            "AND is_deleted = 0 AND behavior_type = #{behaviorType} " +
            "AND (member_level = #{memberLevel} OR member_level IS NULL) " +
            "AND (start_time IS NULL OR start_time <= NOW()) " +
            "AND (end_time IS NULL OR end_time >= NOW()) " +
            "ORDER BY CASE WHEN member_level = #{memberLevel} THEN 0 ELSE 1 END, priority DESC LIMIT 1")
    MarketingPointsRule selectBehaviorRule(@Param("behaviorType") String behaviorType, 
                                          @Param("memberLevel") Integer memberLevel);
}

