package com.tju.elm_bk.mapper;

import com.tju.elm_bk.pojo.entity.MarketingPointsRuleFood;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 营销积分规则与商品关联 Mapper
 */
@Mapper
public interface MarketingPointsRuleFoodMapper {
    
    /**
     * 插入关联记录
     */
    @Insert("INSERT INTO marketing_points_rule_food (rule_id, food_id, create_time, update_time, is_deleted) " +
            "VALUES (#{ruleId}, #{foodId}, #{createTime}, #{updateTime}, #{isDeleted})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(MarketingPointsRuleFood ruleFood);
    
    /**
     * 批量插入关联记录
     */
    @Insert("<script>" +
            "INSERT INTO marketing_points_rule_food (rule_id, food_id, create_time, update_time, is_deleted) VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.ruleId}, #{item.foodId}, #{item.createTime}, #{item.updateTime}, #{item.isDeleted})" +
            "</foreach>" +
            "</script>")
    void batchInsert(@Param("list") List<MarketingPointsRuleFood> ruleFoods);
    
    /**
     * 根据规则ID查询关联的商品ID列表
     */
    @Select("SELECT food_id FROM marketing_points_rule_food " +
            "WHERE rule_id = #{ruleId} AND is_deleted = 0")
    List<Long> selectFoodIdsByRuleId(Long ruleId);
    
    /**
     * 根据规则ID查询所有关联记录
     */
    @Select("SELECT * FROM marketing_points_rule_food " +
            "WHERE rule_id = #{ruleId} AND is_deleted = 0")
    List<MarketingPointsRuleFood> selectByRuleId(Long ruleId);
    
    /**
     * 根据商品ID查询关联的规则ID列表
     */
    @Select("SELECT rule_id FROM marketing_points_rule_food " +
            "WHERE food_id = #{foodId} AND is_deleted = 0")
    List<Long> selectRuleIdsByFoodId(Long foodId);
    
    /**
     * 删除规则的所有关联记录（逻辑删除）
     */
    @Update("UPDATE marketing_points_rule_food SET is_deleted = 1, " +
            "update_time = #{updateTime} WHERE rule_id = #{ruleId}")
    void deleteByRuleId(@Param("ruleId") Long ruleId, @Param("updateTime") LocalDateTime updateTime);
    
    /**
     * 删除指定的关联记录（逻辑删除）
     */
    @Update("UPDATE marketing_points_rule_food SET is_deleted = 1, " +
            "update_time = #{updateTime} WHERE rule_id = #{ruleId} AND food_id = #{foodId}")
    void deleteByRuleIdAndFoodId(@Param("ruleId") Long ruleId, 
                                  @Param("foodId") Long foodId, 
                                  @Param("updateTime") LocalDateTime updateTime);
    
    /**
     * 检查规则是否关联了指定商品
     */
    @Select("SELECT COUNT(*) > 0 FROM marketing_points_rule_food " +
            "WHERE rule_id = #{ruleId} AND food_id = #{foodId} AND is_deleted = 0")
    Boolean existsByRuleIdAndFoodId(@Param("ruleId") Long ruleId, @Param("foodId") Long foodId);
}

