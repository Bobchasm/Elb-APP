package com.tju.elm.point.mapper;

import com.tju.elm.point.zoo.pojo.entity.MarketingPointsExchangeRule;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MarketingPointsExchangeRuleMapper {
    
    /**
     * 插入积分兑换规则
     * 注意：exchange_ratio 在 rule_type=1（兑换商品）时可以为 NULL
     */
    void insert(MarketingPointsExchangeRule rule);
    
    /**
     * 根据ID查询积分兑换规则
     */
    @Select("SELECT * FROM marketing_points_exchange_rule WHERE id = #{id} AND is_deleted = 0")
    MarketingPointsExchangeRule selectById(Long id);
    
    /**
     * 更新积分兑换规则
     */
    void updateById(MarketingPointsExchangeRule rule);
    
    /**
     * 逻辑删除积分兑换规则
     */
    @Update("UPDATE marketing_points_exchange_rule SET is_deleted = 1, update_time = NOW(), " +
            "updater = #{updater} WHERE id = #{id}")
    void deleteById(@Param("id") Long id, @Param("updater") Long updater);
    
    /**
     * 查询积分兑换规则列表
     */
    List<MarketingPointsExchangeRule> selectRules(@Param("ruleType") Integer ruleType,
                                                   @Param("ruleStatus") Integer ruleStatus,
                                                   @Param("offset") Integer offset,
                                                   @Param("limit") Integer limit);
    
    /**
     * 统计积分兑换规则总数
     */
    @Select("SELECT COUNT(*) FROM marketing_points_exchange_rule WHERE is_deleted = 0 " +
            "AND (#{ruleType} IS NULL OR rule_type = #{ruleType}) " +
            "AND (#{ruleStatus} IS NULL OR rule_status = #{ruleStatus})")
    Long countRules(@Param("ruleType") Integer ruleType, @Param("ruleStatus") Integer ruleStatus);
    
    /**
     * 查询积分+现金兑换规则
     */
    @Select("SELECT * FROM marketing_points_exchange_rule WHERE rule_type = 0 AND rule_status = 1 " +
            "AND is_deleted = 0 AND (start_time IS NULL OR start_time <= NOW()) " +
            "AND (end_time IS NULL OR end_time >= NOW()) ORDER BY id DESC LIMIT 1")
    MarketingPointsExchangeRule selectCashExchangeRule();
    
    /**
     * 查询兑换商品规则列表
     */
    @Select("SELECT * FROM marketing_points_exchange_rule WHERE rule_type = 1 AND rule_status = 1 " +
            "AND is_deleted = 0 AND stock_quantity > 0 " +
            "AND (start_time IS NULL OR start_time <= NOW()) " +
            "AND (end_time IS NULL OR end_time >= NOW()) " +
            "ORDER BY id DESC")
    List<MarketingPointsExchangeRule> selectExchangeGoodsRules();
    
    /**
     * 根据商品ID查询兑换规则
     */
    @Select("SELECT * FROM marketing_points_exchange_rule WHERE food_id = #{foodId} " +
            "AND rule_type = 1 AND rule_status = 1 AND is_deleted = 0 " +
            "AND (start_time IS NULL OR start_time <= NOW()) " +
            "AND (end_time IS NULL OR end_time >= NOW()) LIMIT 1")
    MarketingPointsExchangeRule selectByFoodId(Long foodId);
    
    /**
     * 更新商品库存（减少指定数量）
     */
    @Update("UPDATE marketing_points_exchange_rule SET stock_quantity = stock_quantity - #{quantity} " +
            "WHERE id = #{id} AND stock_quantity >= #{quantity}")
    int decreaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);



    /**
     * 使用乐观锁减少库存
     */
    @Update("UPDATE marketing_points_exchange_rule " +
            "SET stock_quantity = stock_quantity - #{quantity}, " +
            "    update_time = NOW() " +
            "WHERE id = #{id} " +
            "  AND stock_quantity >= #{quantity} ")
    int decreaseStockWithVersion(@Param("id") Long id,
                                 @Param("quantity") Integer quantityn);
}

